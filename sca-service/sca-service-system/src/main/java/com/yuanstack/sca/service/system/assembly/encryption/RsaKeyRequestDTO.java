package com.yuanstack.sca.service.system.assembly.encryption;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: RsaKeyRequestDTO
 * @author: hansiyuan
 * @date: 2022/6/29 3:10 PM
 */
@Data
public class RsaKeyRequestDTO implements Serializable {

    private static final long serialVersionUID = -7777148337963860431L;

    /**
     * 渠道来源
     */
    private String source;
    /**
     * 是否取缓存数据
     */
    private boolean cache;

}
