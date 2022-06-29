package com.yuanstack.sca.service.system.assembly.encryption;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: TRsaKeyResultDTO
 * @author: hansiyuan
 * @date: 2022/6/29 3:12 PM
 */
@Data
public class RsaKeyResultDTO implements Serializable {

    private static final long serialVersionUID = -5016186712450556747L;

    /**
     * 上传证件号到中台加密数据公钥
     */
    private String publicKey;
    /**
     * 中台返回证件号解密私钥
     */
    private String privateKey;
    /**
     * 过期时间
     */
    private Long expireIn;
}
