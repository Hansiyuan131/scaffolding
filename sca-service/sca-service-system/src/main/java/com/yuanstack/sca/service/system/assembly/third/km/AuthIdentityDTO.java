package com.yuanstack.sca.service.system.assembly.third.km;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @description: AuthIdentityDTO
 * @author: hansiyuan
 * @date: 2022/6/29 3:27 PM
 */
@Setter
@Getter
@ToString
public class AuthIdentityDTO {
    private static final long serialVersionUID = -8928258181324833423L;

    private String clientId;

    private String clientSecret;

    private String grantType;

    private String granTokenUrl;
    /**
     * 调用那个auth方法，目前支持Gems
     */
    private Integer authType;
}
