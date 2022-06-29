package com.yuanstack.sca.service.system.assembly.third.km;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.yuanstack.sca.service.system.assembly.redis.RedisUtil;
import com.yuanstack.sca.service.system.common.constants.CustomExceptionType;
import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import com.yuanstack.sca.service.system.common.response.AjaxResponse;
import com.yuanstack.sca.service.system.common.utils.JsonStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: KmService
 * @author: hansiyuan
 * @date: 2022/6/29 3:19 PM
 */
@Component
@Slf4j
public class KmService {
    @Resource
    private KmAuthConfig kmAuthConfig;

    @Resource
    private KmInvokeService thirdInvokeService;

    @Resource
    private KmUrlHelper kmUrlHelper;

    @Resource
    private RedisUtil redisUtil;

    private static final int CONNECT_TIMEOUT = 2000;

    private static final int REQUEST_TIMEOUT = 2000;

    private static final int SOCKET_TIMEOUT = 2000;

    /**
     * 获取音乐列表
     */
    public AjaxResponse<MusicResultDTO> callKmMusicList(MusicRequestDTO requestDTO) {

        // 获取腾讯我的优惠券列表接口url
        String url = kmUrlHelper.buildKmMusicListUrl();
        Map<String, Object> paramMap = new HashMap<>();
        ThirdRequestDTO thirdRequestDTO = new ThirdRequestDTO();
        thirdRequestDTO.setUrl(url);
        thirdRequestDTO.setRequestTypeConstant(RequestTypeConstant.GET);
        thirdRequestDTO.setParamMap(paramMap);
        KmResultDTO<MusicResultDTO> kmResultDTO = callKm(requestDTO, thirdRequestDTO, MusicResultDTO.class);
        LogUtils.info(log, ModelEnum.COMMON_UTILS, "获取我的优惠券", url,
                "callKmMyCouponsRequest " + JSON.toJSONString(requestDTO),
                "callKmMyCouponsResult " + JSON.toJSONString(kmResultDTO.getData()));
        return processResultDTO(kmResultDTO);
    }


    private <T> AjaxResponse processResultDTO(KmResultDTO<T> kmResultDTO) {
        AjaxResponse<T> resultDTO = new AjaxResponse<>();
        if (!kmResultDTO.isSuccess() && CustomExceptionType.E_SYS_HTTP_429.getCode().equals(kmResultDTO.getStatusCode())) {
            // 第三方服务限流
            return AjaxResponse.error(resultDTO, CustomExceptionType.E_SYS_HTTP_429);
        } else if (kmResultDTO.isSuccess()) {
            // 请求成功
            return AjaxResponse.success(kmResultDTO.getData());
        } else {
            // 透出腾讯错误码
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "KmService.callKm()失败 {}", JSON.toJSONString(kmResultDTO));
            KmErrorEnum kmErrorEnum = KmErrorEnum.OTHER_ERROR;
            if (kmResultDTO.getErrorCode() != null) {
                kmErrorEnum = KmErrorEnum.getKmErrorEnumByCode(kmResultDTO.getErrorCode().toString());
            }
            return AjaxResponse.error(kmErrorEnum.getCode(), kmErrorEnum.getDesc());
        }
    }

    public <T> KmResultDTO<T> callKm(RequestDTO requestDTO, ThirdRequestDTO thirdRequestDTO, Class<T> clazz) {
        KmResultDTO kmResultDTO = new KmResultDTO<>();

        if (ObjectUtils.isNotEmpty(thirdRequestDTO)) {
            Integer connectTimeout = thirdRequestDTO.getConnectTimeout() == null ? CONNECT_TIMEOUT : thirdRequestDTO.getConnectTimeout();
            Integer requestTimeout = thirdRequestDTO.getRequestTimeout() == null ? REQUEST_TIMEOUT : thirdRequestDTO.getRequestTimeout();
            Integer socketTimeout = thirdRequestDTO.getSocketTimeout() == null ? SOCKET_TIMEOUT : thirdRequestDTO.getSocketTimeout();
            thirdRequestDTO.setConnectTimeout(connectTimeout);
            thirdRequestDTO.setRequestTimeout(requestTimeout);
            thirdRequestDTO.setSocketTimeout(socketTimeout);
            if (thirdRequestDTO.getParamMap() != null && !thirdRequestDTO.getParamMap().isEmpty()) {
                thirdRequestDTO.setJsonBody(JSON.toJSONString(thirdRequestDTO.getParamMap()));
                thirdRequestDTO.setParamMap(null);
            }
            AjaxResponse<String> stringResultDTO = null;
            try {
                // redisKey = null ,腾讯接口鉴权每次重新获取，无需缓存
                stringResultDTO = thirdInvokeService.invokeThirdService(String.class, thirdRequestDTO, null);
                if (StringUtils.isNotEmpty(stringResultDTO.getData()) && JsonStringUtils.isJson(stringResultDTO.getData())) {
                    kmResultDTO = JSON.parseObject(stringResultDTO.getData(), KmResultDTO.class);
                    kmResultDTO.setData(JSON.parseObject(JSON.toJSONString(kmResultDTO.getData()), clazz));
                }
                LogUtils.info(log, ModelEnum.COMMON_UTILS, "invokeKm接口结束", thirdRequestDTO.getUrl(), "invokeKmRequest " + JSON.toJSONString(thirdRequestDTO),
                        "invokeKmResult " + JSON.toJSONString(stringResultDTO.getData()));
            } catch (Exception e) {
                LogUtils.error(log, ModelEnum.COMMON_UTILS, "invokeKm接口失败", thirdRequestDTO.getUrl(),
                        JSON.toJSONString(thirdRequestDTO), JSON.toJSONString(stringResultDTO.getData()), e.getMessage(), e);
                return kmResultDTO;
            }
        }
        return kmResultDTO;
    }

}