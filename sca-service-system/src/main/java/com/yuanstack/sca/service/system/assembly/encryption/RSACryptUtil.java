package com.yuanstack.sca.service.system.assembly.encryption;

import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import com.yuanstack.sca.service.system.common.response.AjaxResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @description: RSACryptUtil
 * @author: hansiyuan
 * @date: 2022/6/29 3:08 PM
 */
@Component
@Slf4j
public class RSACryptUtil {

    /**
     * 签名算法
     */
    private static final String KEY_ALGORITHM = "RSA";
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;
    /**
     * 数据编码
     */
    private static final String ENCODING = "UTF-8";
    /**
     * 来源
     */
    private static final String RSA_SOURCE = "ali";
    /**
     * 重试次数
     */
    private static final int RETRY_TIME = 3;

    /**
     * @param cipherData
     * @return String
     * @Description :RSA加密
     */
    public String rsaEncrypt(String cipherData) {
        RsaKeyRequestDTO requestDTO = new RsaKeyRequestDTO();
        requestDTO.setSource(RSA_SOURCE);
        requestDTO.setCache(true);
        AjaxResponse<RsaKeyResultDTO> rsaKeyResultDTO = getRsaKey(requestDTO);
        if (ObjectUtils.isNotEmpty(rsaKeyResultDTO) && ObjectUtils.isNotEmpty(rsaKeyResultDTO.getData())
                && StringUtils.isNotEmpty(rsaKeyResultDTO.getData().getPublicKey()) && StringUtils.isNotEmpty(cipherData)) {
            String encryptData = encryptByPublicKey(cipherData, rsaKeyResultDTO.getData().getPublicKey());
            if (StringUtils.isNotEmpty(encryptData)) {
                return encryptData;
            } else {
                requestDTO.setSource(RSA_SOURCE);
                requestDTO.setCache(false);
                rsaKeyResultDTO = getRsaKey(requestDTO);
                return encryptByPublicKey(cipherData, rsaKeyResultDTO.getData().getPublicKey());
            }
        }
        return null;
    }

    /**
     * @param encryptData
     * @return String
     * @Description :RSA解密
     */
    public String rsaDecrypt(String encryptData) {
        RsaKeyRequestDTO requestDTO = new RsaKeyRequestDTO();
        requestDTO.setSource(RSA_SOURCE);
        requestDTO.setCache(true);
        AjaxResponse<RsaKeyResultDTO> rsaKeyResultDTO = getRsaKey(requestDTO);
        if (ObjectUtils.isNotEmpty(rsaKeyResultDTO) && ObjectUtils.isNotEmpty(rsaKeyResultDTO.getData())
                && StringUtils.isNotEmpty(rsaKeyResultDTO.getData().getPrivateKey()) && StringUtils.isNotEmpty(encryptData)) {
            String decryptData = decryptByPrivateKey(encryptData, rsaKeyResultDTO.getData().getPrivateKey());
            if (StringUtils.isNotEmpty(decryptData)) {
                return decryptData;
            } else {
                requestDTO.setSource(RSA_SOURCE);
                requestDTO.setCache(false);
                rsaKeyResultDTO = getRsaKey(requestDTO);
                return decryptByPrivateKey(encryptData, rsaKeyResultDTO.getData().getPrivateKey());
            }
        }
        return null;
    }

    /**
     * @param requestDTO
     * @return String
     * @Description :RSA加密
     */
    public AjaxResponse<RsaKeyResultDTO> getRsaKey(RsaKeyRequestDTO requestDTO) {

        AjaxResponse<RsaKeyResultDTO> rsaKeyResultDTO = getRsa(requestDTO);
        if (ObjectUtils.isEmpty(rsaKeyResultDTO) || ObjectUtils.isEmpty(rsaKeyResultDTO.getData())
                || StringUtils.isEmpty(rsaKeyResultDTO.getData().getPublicKey())) {
            // 重试机制
            int retry = 0;
            while (ObjectUtils.isNotEmpty(rsaKeyResultDTO) && ObjectUtils.isNotEmpty(rsaKeyResultDTO.getData())
                    && StringUtils.isNotEmpty(rsaKeyResultDTO.getData().getPublicKey())
                    && retry++ < RETRY_TIME) {
                rsaKeyResultDTO = getRsa(requestDTO);
            }
        }
        return rsaKeyResultDTO;
    }

    private AjaxResponse<RsaKeyResultDTO> getRsa(RsaKeyRequestDTO requestDTO) {
        return null;
    }

    /**
     * 公钥加密
     *
     * @param dataStr   原始数据
     * @param publicKey 公钥
     * @return 加密数据
     * @throws Exception
     */
    public static String encryptByPublicKey(String dataStr, String publicKey) {
        LogUtils.info(log, ModelEnum.COMMON_UTILS, "明文数据和公钥分别是", dataStr, publicKey);
        String cipherData;
        try {
            byte[] data = dataStr.getBytes(ENCODING);
            byte[] keyBytes = Base64.decodeBase64(publicKey);

            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicK = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

            cipher.init(Cipher.ENCRYPT_MODE, publicK);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int offSet = 0;
            byte[] cache;
            int i = 0;
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            cipherData = Base64.encodeBase64String(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "公钥加密失败异常", dataStr, publicKey, e.getMessage());
            return null;
        }
        LogUtils.info(log, ModelEnum.COMMON_UTILS, "加密后的数据为", cipherData);
        return cipherData;
    }

    /**
     * 私钥解密
     *
     * @param encryptedDataStr 加密数据
     * @param privateKey       私钥
     * @return 解密后数据
     * @throws Exception
     */
    public static String decryptByPrivateKey(String encryptedDataStr, String privateKey) {
        LogUtils.info(log, ModelEnum.COMMON_UTILS, "密文数据和私钥是", encryptedDataStr, privateKey);
        byte[] encryptedData = Base64.decodeBase64(encryptedDataStr);
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        String plainText;
        try {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

            cipher.init(Cipher.DECRYPT_MODE, privateK);
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int offSet = 0;
            byte[] cache;
            int i = 0;
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            out.close();
            plainText = out.toString(ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "私钥解密数据异常", encryptedDataStr, privateKey, e.getMessage());
            return null;
        }
        LogUtils.info(log, ModelEnum.COMMON_UTILS, "解密后的数据为", plainText);
        return plainText;
    }


    public static void main(String[] args) throws Exception {

        String enData = encryptByPublicKey("TravelPermit001", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdxMrshBvvwT89hmXrJsNA6PY9KXS+ysODxFx9W6rH2bEQ5JPxiP6qFS+ksq8zDZDomVVTziXjGeSXBIkJ5r4GTFu0egpmIxuJVCowe7aIcCR+MXtIDMr+cr80fdaYwwEdxGCd/A1z6uU151zlGb4GyKD8eI7b2Pf2rR7KBUtL3wIDAQAB");
        System.out.println("加密之后数据：{" + enData + "}");
        String deData = decryptByPrivateKey("L7fXX1tcko5wBBOqj0OUPXGBEUWB1nOIQXIaLTerozAU/VNchhK5zr1Izky6n1uFir7QTET0NNKLIqTNlse7olKZzPkIQZpq/Ld9IY7iqcOqmMZsCc5AQi0kTjCkB6whANBPAqg2DC/cCpW45fBiNUtzGmXyR0xJgQGrt78VVAs="
                , "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMCwPBw6HiX/Wo6mDMftkyQlE1TP+iyeDr2c4XNMSG/zXRtPPivMqXbDBylKTgKYCWO+dlSohGYjdB8+IsAnY5/eCFdWlhbd0qmz1PGvXQd+Qi+Q6mCbKlozQIQFLqOPnxf72Y3dpo+/jvqa1FT3GVesBIOGAiSDf1WsdT7/0uUxAgMBAAECgYBSlrcM+jQV4T81SSLNRpz8rw98GLCIEy8KQoW3X4jkwxCzNeHV8CDqkGkvX3PUVMohgG9Dlf85sBSDl6RZbAicrJZkicswcBFHMOrxOaM5XLd/rj4UyCzsncZVAJ5Ylop/0rutVSOj/A1/AxVrDTG3FoheqZ9O1BAtlhXthsJsAQJBAOVhL+QRCDKgDJEnDkYxJ1OizGGefiAalMRkQqK1c9cBHPuVBHEBhTyBJCq6RzsB+66RjgVNuA9s+kRCcP39FmECQQDXDPXngQI+nw3CVFZPMZJWUsseQZhildMUK/lJp2rqL1tR91zQrzzuU09L9BXXGMv0/yIIHUe7Qfgxwiojl6DRAkEApvQfHHv9IIP3warQq+xY+HoVErR+jrVqNKvZCH7T6tlHlCI5klsN9CT3AxaEhAGEg+Izoc6YurmSSx6lCWkt4QJBAKNhB1lwW8IzmIJxDHlyEdlfYU2FTD5p0Ulb9zKL7OAqRljJc0xgNgxiYrYzpHwcLKffRX2gsSSFBAjZmKYdxmECQAzyZ8FKnD39CJjdzd8GnJ8VO+/OHNHMDGyQJCY7p83Oy2xHbNIGMxtvpcrN9K7RO+xV3pbITmXAfAXOCNirZeg=\",\"publicKey\":\"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXDYaLyVE1RPlw+OJOJ+8WNGMk3ix6jz+aDN52h6CFGkOzazbtQwVaBcLEzuwwYCVsea0suNXpXsJnsA6q1zJicT7QctV0mAfEzJ6Wg0mR/W9RR1WrIgmOUZHhoMwhdJhPAyPJAkf/tlMs4Mv5fEMyqsU3e8mg9R95qQ+mWR+plQIDAQAB");
        System.out.println("解密之后数据：{" + deData + "}");
    }


}


