package com.yuanstack.sca.service.system.common.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @description: 全局响应结果处理
 * @author: hansiyuan
 * @date: 2022/6/28 3:46 PM
 */
@Component
@ControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice {

    public static final String STATUS_CODE_999 = "999";

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType mediaType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        //如果响应结果是JSON数据类型
        if (mediaType.equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
            if (body instanceof AjaxResponse) {
                AjaxResponse ajaxResponse = (AjaxResponse) body;
                //999 不是标准的HTTP状态码，特殊处理
                if (!STATUS_CODE_999.equals(ajaxResponse.getCode())) {
                    response.setStatusCode(HttpStatus.valueOf(ajaxResponse.getCode()));
                }

                return body;
            }

        }
        return body;
    }
}
