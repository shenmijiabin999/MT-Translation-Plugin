package bin.mt.plugin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Bin
 */
public class SignUtil {

    /**
     * 谷歌翻译和百度翻译的网页版用同样的签名算法
     */
    public static String signWeb(String text, long key1, long key2) {
        List<Integer> c = new ArrayList<>();
        for (int F = 0; F < text.length(); F++) {
            int p = text.charAt(F);
            if (128 > p) {
                c.add(p);
            } else {
                if (2048 > p) {
                    c.add(p >> 6 | 192);
                } else {
                    if ((55296 == (64512 & p) && F + 1 < text.length() && 56320 == (64512 & text.charAt(F + 1)))) {
                        p = 65536 + ((1023 & p) << 10) + (1023 & text.charAt(++F));
                        c.add(p >> 18 | 240);
                        c.add(p >> 12 & 63 | 128);
                    } else {
                        c.add(p >> 12 | 224);
                    }
                    c.add(p >> 6 & 63 | 128);
                }
                c.add(63 & p | 128);
            }
        }
        String formula1 = "+-a^+6";
        String formula2 = "+-3^+b+-f";
        long v = key1;
        for (Integer i : c) {
            v += i;
            v = n(v, formula1);
        }
        v = n(v, formula2);
        v ^= key2;
        if (0 > v)
            v = (0x7fffffff & v) + 0x80000000L;
        v %= 1e6;
        return v + "." + (v ^ key1);
    }

    private static long n(long r, String o) {
        for (int t = 0; t < o.length() - 2; t += 3) {
            long e = o.charAt(t + 2);
            e = e >= 'a' ? e - 87 : e - '0';
            e = '+' == o.charAt(t + 1) ? r >>> e : r << e;
            r = '+' == o.charAt(t) ? r + e & 0xffffffffL : r ^ e;
        }
        return r;
    }

    public static String getRandomText(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String toHex(byte[] digest) {
        char[] str = new char[digest.length * 2];
        int k = 0;
        for (byte b : digest) {
            str[k++] = hexDigits[b >>> 4 & 0xf];
            str[k++] = hexDigits[b & 0xf];
        }
        return new String(str);
    }

    public static String md5(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return toHex(digest.digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
