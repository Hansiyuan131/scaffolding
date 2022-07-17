package com.yuanstack.sca.service.system.assembly.encryption;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @description: MD5加密
 * @author: hansiyuan
 * @date: 2022/6/29 11:45 AM
 */
public class MD5Utils {
    /**
     * MD5方法
     *
     * @param text 明文
     * @return 密文
     */
    private static String md5Impl(String text) {
        //加密后的字符串
        return DigestUtils.md5Hex(text);
    }

    /**
     * @param salt 盐值
     * @param args 要加密的业务参数
     */
    public static String md5(String salt, String... args) {
        StringBuilder buf = new StringBuilder(salt);
        for (String arg : args) {
            buf.append(arg);
        }

        return md5Impl(buf.toString());
    }

    /**
     * encode
     */
    public static String encode(String str) {
        if (str != null && str.length() > 0) {
            try {
                return URLEncoder.encode(str, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String md5NoSalt(String str) {
        if (str != null && str.length() > 0) {
            byte[] digest = null;
            try {
                MessageDigest md5 = MessageDigest.getInstance("md5");
                digest = md5.digest(str.getBytes(StandardCharsets.UTF_8));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            if (digest != null) {
                return new BigInteger(1, digest).toString(16);
            }
            //16是表示转换为16进制数
        }
        return null;
    }
}
