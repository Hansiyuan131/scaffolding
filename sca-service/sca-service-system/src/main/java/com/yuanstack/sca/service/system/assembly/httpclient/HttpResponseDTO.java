package com.yuanstack.sca.service.system.assembly.httpclient;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * @description: HttpResponseDTO
 * @author: hansiyuan
 * @date: 2022/6/29 12:07 PM
 */
@Getter
@Setter
public class HttpResponseDTO implements Serializable {
    private static final long serialVersionUID = -3229023289617160023L;

    private String responseStr;

    private Map<String, String> headerMap;

    private Map<String, String> cookieMap;

    private Integer statusCode;
}
