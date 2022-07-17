package com.yuanstack.sca.service.system.assembly.third.km;

import com.alibaba.fastjson.JSON;
import com.yuanstack.sca.service.system.assembly.httpclient.HttpResponseDTO;
import com.yuanstack.sca.service.system.assembly.monitor.KvMonitor;
import com.yuanstack.sca.service.system.assembly.redis.RedisUtil;
import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.exception.ExceptionUtil;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import com.yuanstack.sca.service.system.common.response.AjaxResponse;
import com.yuanstack.sca.service.system.common.utils.JsonStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * @description: KmInvokeService
 * @author: hansiyuan
 * @date: 2022/6/29 3:23 PM
 */
@Component
@Slf4j
public class KmInvokeService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private HttpAuthenticationMethod httpAuthenticationMethod;

    public <T> AjaxResponse<T> invokeThirdService(Class<T> clazz, ThirdRequestDTO thirdRequestDTO, String key) {
        long startTime = System.currentTimeMillis();
        AjaxResponse<T> resultDTO = invokeThirdServiceInner(clazz, thirdRequestDTO, key);
        //KV监控
        KvMonitor.rt(thirdRequestDTO.getUrl().split("\\?")[0], resultDTO.getCode(), resultDTO.getMessage()).record(startTime);
        return resultDTO;
    }

    public <T> AjaxResponse<T> invokeThirdServiceInner(Class<T> clazz, ThirdRequestDTO thirdRequestDTO, String key) {
        AjaxResponse<T> resultDTO = new AjaxResponse<>();
        try {
            Serializable serializable = null;
            if (StringUtils.isNotEmpty(key)) {
                serializable = redisUtil.get(key);
            }
            AjaxResponse authentication = httpAuthenticationMethod.getAuthentication(thirdRequestDTO, serializable, key);
            if (ObjectUtils.isNotEmpty(authentication) && !authentication.isSuccess()) {
                // 鉴权不为空且鉴权失败判断
                return authentication;
            }
            LogUtils.info(log, ModelEnum.COMMON_UTILS, "invoker数据", thirdRequestDTO.getUrl(),
                    thirdRequestDTO.getParamMap() + "" + thirdRequestDTO.getHeaderMap() + thirdRequestDTO.getJsonBody());
            // ---http请求发送开始----入参thirdRequestDTO
            HttpResponseDTO httpResponseDTO = HttpSendMethod.doHttp(thirdRequestDTO);
            // ---http请求发送结束----返回httpResponseDTO
            LogUtils.info(log, ModelEnum.COMMON_UTILS, thirdRequestDTO.getUrl() + "invokeThirdService结果", JSON.toJSONString(httpResponseDTO));
            return getResultDTO(clazz, thirdRequestDTO, httpResponseDTO);
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "ThirdInvokeService.invokeThirdService异常", thirdRequestDTO.getUrl(), JSON.toJSONString(thirdRequestDTO), e);
            resultDTO.setData((T) ExceptionUtil.printTop3StackTrace(e));
            return resultDTO;
        }
    }

    private <T> AjaxResponse getResultDTO(Class<T> clazz, ThirdRequestDTO thirdRequestDTO, HttpResponseDTO httpResponseDTO) {
        AjaxResponse<T> resultDTO = new AjaxResponse<>();
        if (ObjectUtils.isEmpty(httpResponseDTO)) {
            return AjaxResponse.error(resultDTO, "AjaxResponse.error");
        }
        if (httpResponseDTO.getStatusCode() != null && HttpStatusConstants.SC_TOO_MANY_REQUESTS == httpResponseDTO.getStatusCode()) {
            return AjaxResponse.error(resultDTO, "AjaxResponse.error");
        }
        if (StringUtils.isNotBlank(httpResponseDTO.getResponseStr())) {
            if (JsonStringUtils.isJson(httpResponseDTO.getResponseStr())) {
                T success = JSON.parseObject(httpResponseDTO.getResponseStr(), clazz);
                if (success != null) {
                    LogUtils.info(log, ModelEnum.COMMON_UTILS, "invokeThirdService结果success", thirdRequestDTO.getUrl(), JSON.toJSONString(thirdRequestDTO), JSON.toJSONString(httpResponseDTO));
                    resultDTO.setData(success);
                    return AjaxResponse.success(resultDTO);
                } else {
                    LogUtils.error(log, ModelEnum.COMMON_UTILS, "ThirdInvokeService.getResultDTO获取请求结果失败", thirdRequestDTO.getUrl(), JSON.toJSONString(thirdRequestDTO), JSON.toJSONString(httpResponseDTO));
                    resultDTO.setData((T) httpResponseDTO.getResponseStr());
                    return AjaxResponse.error(resultDTO, "AjaxResponse.error");
                }
            } else {
                LogUtils.error(log, ModelEnum.COMMON_UTILS, "ThirdInvokeService.getResultDTO获取请求结果失败", thirdRequestDTO.getUrl(), JSON.toJSONString(thirdRequestDTO), JSON.toJSONString(httpResponseDTO));
                resultDTO.setData((T) httpResponseDTO.getResponseStr());
                return AjaxResponse.error(resultDTO, "AjaxResponse.error");
            }
        } else {
            if (thirdRequestDTO.getRequestTypeConstant().equals(RequestTypeConstant.DELETE)
                    && HttpStatusConstants.SC_INTERNAL_SERVER_ERROR != httpResponseDTO.getStatusCode()) {
                LogUtils.info(log, ModelEnum.COMMON_UTILS, "invokeThirdService结果success", thirdRequestDTO.getUrl(), JSON.toJSONString(thirdRequestDTO), JSON.toJSONString(httpResponseDTO));
                return AjaxResponse.success(resultDTO);
            }
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "ThirdInvokeService.invokeThirdService失败", thirdRequestDTO.getUrl(), JSON.toJSONString(thirdRequestDTO), JSON.toJSONString(httpResponseDTO));
            resultDTO.setData((T) "httpResponseDTO返回空");
            return AjaxResponse.error(resultDTO, "AjaxResponse.error");
        }
    }
}


