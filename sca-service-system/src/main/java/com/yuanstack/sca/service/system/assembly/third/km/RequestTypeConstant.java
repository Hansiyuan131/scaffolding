package com.yuanstack.sca.service.system.assembly.third.km;

import lombok.Getter;

/**
 * @description: RequestTypeConstant
 * @author: hansiyuan
 * @date: 2022/6/29 3:42 PM
 */
@Getter
public enum RequestTypeConstant {
    POST("POST"),
    GET("GET"),
    HEAD("HEAD"),
    PUT("PUT"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    CONNECT("CONNECT"),
    PATCH("PATCH"),
    TRACE("TRACE");

    private final String string;

    RequestTypeConstant(String string) {
        this.string = string;
    }

}
