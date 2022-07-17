package com.yuanstack.sca.service.system.assembly.third.km;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: ThirdRequestDTO
 * @author: hansiyuan
 * @date: 2022/6/29 3:25 PM
 */
@Getter
@Setter
public class ThirdRequestDTO implements Serializable {
    private static final long serialVersionUID = -8928258181324833423L;

    private String url;

    private Map<String, Object> paramMap = new HashMap<>();

    private Map<String, String> headerMap = new HashMap<>();

    private boolean needToken = true;

    private String jsonBody;

    private RequestTypeConstant requestTypeConstant = RequestTypeConstant.POST;

    /**
     * 是否手动传入token
     */
    private String token;

    /**
     * auth2信息
     */
    AuthIdentityDTO authIdentityDTO;

    private Integer connectTimeout;

    private Integer requestTimeout;

    private Integer socketTimeout;
}
