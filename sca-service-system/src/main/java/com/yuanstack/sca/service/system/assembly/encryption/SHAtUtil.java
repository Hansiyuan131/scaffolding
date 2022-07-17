package com.yuanstack.sca.service.system.assembly.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @description: 加密工具类
 * @author: hansiyuan
 * @date: 2022/6/29 11:54 AM
 */
public class SHAtUtil {
    /**
     * 传入文本内容，返回 SHA-256 串
     */
    public static String sha56(final String strText) {
        return sha(strText, "SHA-256");
    }

    /**
     * 传入文本内容，返回 SHA-512 串
     */
    public static String sha512(final String strText) {
        return sha(strText, "SHA-512");
    }

    /**
     * 字符串 SHA 加密
     */
    private static String sha(final String strText, final String strType) {
        // 返回值
        String strResult = null;
        // 是否是有效字符串
        if (strText != null && strText.length() > 0) {
            try {
                // SHA 加密开始
                // 创建加密对象 并傳入加密類型
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                // 传入要加密的字符串
                messageDigest.update(strText.getBytes());
                // 得到 byte 類型结果
                byte[] byteBuffer = messageDigest.digest();

                // 將 byte 轉換爲 string
                StringBuilder strHexString = new StringBuilder();
                // 遍歷 byte buffer
                for (byte b : byteBuffer) {
                    // String hex = Integer.toHexString(0xff & byteBuffer[i]);
                    String hex = String.format("%02x", b);
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                // 得到返回結果
                strResult = strHexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }

}
