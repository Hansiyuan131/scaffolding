package com.yuanstack.sca.service.system.assembly.third.km;

import com.yuanstack.sca.service.system.common.response.AjaxResponse;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Map;

/**
 * @description: HttpAuthenticationMethod
 * @author: hansiyuan
 * @date: 2022/6/29 3:29 PM
 */
@Component
public class HttpAuthenticationMethod {

    @Resource
    private KmAuthManager kmAuthManager;

    public AjaxResponse getAuthentication(ThirdRequestDTO thirdRequestDTO, Serializable serializable, String redisKey) {
        AjaxResponse<T> ajaxResponse = new AjaxResponse<>();

        AjaxResponse<KmAuthDTO> kmAuthDTO;
        kmAuthDTO = kmAuthManager.paramAuth(thirdRequestDTO.getUrl(),
                thirdRequestDTO.getRequestTypeConstant().getString(),
                thirdRequestDTO.getJsonBody());
        if (ObjectUtils.isNotEmpty(kmAuthDTO) && ObjectUtils.isNotEmpty(kmAuthDTO.getData())
                && MapUtils.isNotEmpty(kmAuthDTO.getData().getHeardMap())) {
            Map<String, String> map = kmAuthDTO.getData().getHeardMap();
            map.keySet().forEach(key -> thirdRequestDTO.getHeaderMap().put(key, map.get(key)));
        }
        return AjaxResponse.success(ajaxResponse);
    }
}
