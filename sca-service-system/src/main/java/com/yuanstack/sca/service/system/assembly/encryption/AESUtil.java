package com.yuanstack.sca.service.system.assembly.encryption;

import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Objects;

/**
 * @description: AESUtil
 * @author: hansiyuan
 * @date: 2022/6/29 12:01 PM
 */
@Slf4j
public class AESUtil {

    private static final int KEYSIZE = 128;

    /**
     * AES加密
     */
    public static byte[] encrypt(String data, String key, String iv) {
        if (StringUtils.isNotBlank(data)) {
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                //选择一种固定算法，为了避免不同java实现的不同算法，生成不同的密钥，而导致解密失败
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                random.setSeed(key.getBytes(StandardCharsets.UTF_8));
                keyGenerator.init(KEYSIZE, random);
                SecretKey secretKey = keyGenerator.generateKey();
                byte[] enCodeFormat = secretKey.getEncoded();
                SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
                //Cipher cipher = Cipher.getInstance("AES");// 创建密码器

                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                byte[] byteContent = data.getBytes(StandardCharsets.UTF_8);

                GCMParameterSpec params = new GCMParameterSpec(KEYSIZE, iv.getBytes(StandardCharsets.UTF_8), 0, 16);

                // 初始化
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, params);
                // 加密
                return cipher.doFinal(byteContent);
            } catch (Exception e) {
                LogUtils.error(log, ModelEnum.COMMON_UTILS, "aes加密异常", e);
            }
        }
        return null;
    }

    /**
     * AES加密，返回String
     */
    public static String encryptToStr(String data, String key, String iv) {

        return StringUtils.isNotBlank(data) ? parseByte2HexStr(Objects.requireNonNull(encrypt(data, key, iv))) : null;
    }

    /**
     * AES解密
     */
    public static byte[] decrypt(byte[] data, String key, String iv) {
        if (ArrayUtils.isNotEmpty(data)) {
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                //选择一种固定算法，为了避免不同java实现的不同算法，生成不同的密钥，而导致解密失败
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                random.setSeed(key.getBytes(StandardCharsets.UTF_8));
                keyGenerator.init(KEYSIZE, random);
                SecretKey secretKey = keyGenerator.generateKey();
                byte[] enCodeFormat = secretKey.getEncoded();
                SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
                //Cipher cipher = Cipher.getInstance("AES");// 创建密码器

                GCMParameterSpec params = new GCMParameterSpec(KEYSIZE, iv.getBytes(StandardCharsets.UTF_8), 0, 16);
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                // 初始化
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, params);
                return cipher.doFinal(data);
            } catch (Exception e) {
                LogUtils.error(log, ModelEnum.COMMON_UTILS, "aes解密异常", e);
            }
        }
        return null;
    }

    /**
     * AES解密，返回String
     */
    public static String decryptToStr(String enCryptdata, String key, String iv) {
        return StringUtils.isNotBlank(enCryptdata) ? new String(Objects.requireNonNull(decrypt(parseHexStr2Byte(enCryptdata), key, iv))) : null;
    }

    /**
     * 将二进制转换成16进制
     */
    public static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = String.format("%02X", b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }

        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) {
        String encryptToStr1 = encryptToStr("8047487258010275993", "6y2fw7zeqgde3796rtbuk8ag9iyxmam6", "vgj5kz13hasie8c8irezz7u5fok3mzb6");
        System.out.println(encryptToStr1);
        String str = decryptToStr(encryptToStr1, "6y2fw7zeqgde3796rtbuk8ag9iyxmam6", "vgj5kz13hasie8c8irezz7u5fok3mzb6");
        System.out.println(str);
    }
}

