package com.sinosoft.siava.encrypt;

import java.security.MessageDigest;

/**
 * 签名方法通用工具类
 * @author ZhangXin
 *
 */
public class  SignUtil {

    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * 根据传入的加密方法做相应的加密
     *
     * @param algorithm 加密方法
     * @param str 要加密的内容
     * @return String 加密之后的结果
     */
    public static String encode(String algorithm, String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(str.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 加密结果做16进制转换工具类
     * @param bytes	要转换的原文
     * @return 转换结果
     */
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }
}
