package com.yuanstack.sca.service.system.assembly.third.km;

import com.yuanstack.sca.service.system.assembly.httpclient.HttpClientUtils;
import com.yuanstack.sca.service.system.assembly.httpclient.HttpResponseDTO;

/**
 * @description:  
 * @author: hansiyuan
 * @date: 2022/6/29 5:08 PM
 */
public class HttpSendMethod {

    public static HttpResponseDTO doHttp(ThirdRequestDTO thirdRequestDTO) {
        boolean timeOutPost = false;
        if (thirdRequestDTO.getConnectTimeout() != null
                || thirdRequestDTO.getSocketTimeout() != null || thirdRequestDTO.getRequestTimeout() != null) {
            //判断是否需要超时机制
            timeOutPost = true;
        }
        HttpResponseDTO httpResponseDTO = new HttpResponseDTO();
        switch (thirdRequestDTO.getRequestTypeConstant()) {
            case GET:
                if (timeOutPost) {
                    httpResponseDTO = HttpClientUtils.doGet(thirdRequestDTO.getUrl(), thirdRequestDTO.getHeaderMap(),
                            thirdRequestDTO.getConnectTimeout(), thirdRequestDTO.getRequestTimeout(), thirdRequestDTO.getSocketTimeout());
                } else {
                    httpResponseDTO = HttpClientUtils.doGet(thirdRequestDTO.getUrl(), thirdRequestDTO.getHeaderMap());
                }
                break;
            case POST:
                if (timeOutPost) {
                    httpResponseDTO = HttpClientUtils.doPost(thirdRequestDTO.getUrl(), thirdRequestDTO.getParamMap(),
                            thirdRequestDTO.getHeaderMap(), thirdRequestDTO.getJsonBody(),
                            thirdRequestDTO.getConnectTimeout(), thirdRequestDTO.getRequestTimeout(), thirdRequestDTO.getSocketTimeout());
                } else {
                    httpResponseDTO = HttpClientUtils.doPost(thirdRequestDTO.getUrl(),
                            thirdRequestDTO.getParamMap(), thirdRequestDTO.getHeaderMap(), thirdRequestDTO.getJsonBody());
                }
                break;
            case DELETE:
                httpResponseDTO = HttpClientUtils.doDelete(thirdRequestDTO.getUrl(), thirdRequestDTO.getHeaderMap());
                break;
            case PATCH:
                if (timeOutPost) {
                    httpResponseDTO = HttpClientUtils.doPatch(thirdRequestDTO.getUrl(), thirdRequestDTO.getParamMap(),
                            thirdRequestDTO.getHeaderMap(), thirdRequestDTO.getJsonBody(),
                            thirdRequestDTO.getConnectTimeout(), thirdRequestDTO.getRequestTimeout(), thirdRequestDTO.getSocketTimeout());
                } else {
                    httpResponseDTO = HttpClientUtils.doPatch(thirdRequestDTO.getUrl(),
                            thirdRequestDTO.getParamMap(), thirdRequestDTO.getHeaderMap(), thirdRequestDTO.getJsonBody());
                }
                break;
            default:
                break;
        }
        return httpResponseDTO;
    }
}

