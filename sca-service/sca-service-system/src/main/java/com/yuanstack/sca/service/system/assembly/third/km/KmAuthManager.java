package com.yuanstack.sca.service.system.assembly.third.km;

import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.exception.CustomException;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import com.yuanstack.sca.service.system.common.response.AjaxResponse;
import com.yuanstack.sca.service.system.config.EnvConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yuanstack.sca.service.system.common.constants.CustomExceptionType.USER_INPUT_ERROR;

/**
 * @description: KmAuthManager
 * @author: hansiyuan
 * @date: 2022/6/29 3:30 PM
 */
@Slf4j
@Component
public class KmAuthManager {

    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";

    @Resource
    KmAuthConfig kmAuthConfig;

    @Resource
    private EnvConfig envConfig;

    public AjaxResponse<KmAuthDTO> paramAuth(String url, String httpMethod) {
        return this.paramAuth(url, httpMethod, null);
    }

    public AjaxResponse<KmAuthDTO> paramAuth(String url, String httpMethod, String reqBody) {

        AjaxResponse<KmAuthDTO> ajaxResponse = new AjaxResponse<>();
        String host;
        String apiAppKey = kmAuthConfig.getApiAppKey();
        String apiAppSecret = kmAuthConfig.getApiAppSecret();
        String acceptHeader = "application/json";
        Map<String, String> heardMap = new HashMap<>();
        String contentMD5 = "";


        String contentType = "application/json";
        if (reqBody != null) {
            contentMD5 = base64Encode(getMD5(reqBody).getBytes());
        }

        URL parsedUrl = null;
        try {
            if (envConfig.isDaily()) {
                url = url.replace("/test/", "/");
            }
            parsedUrl = new URL(url);
            host = parsedUrl.getHost();
        } catch (MalformedURLException e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "url new url() 异常", e.getMessage(), url);
            return AjaxResponse.success(null);
        }

        String pathAndParams = parsedUrl.getPath();
        if (parsedUrl.getQuery() != null) {
            pathAndParams = pathAndParams + "?" + sortQueryParams(parsedUrl.getQuery());
        }

        String xDate = getGMTTime();
        String stringToSign = String.format("x-date: %s\n%s\n%s\n%s\n%s\n%s", xDate, httpMethod, acceptHeader, contentType, contentMD5, pathAndParams);

        byte[] hmacStr;
        try {
            hmacStr = hmacSHA1Encrypt(stringToSign, apiAppSecret);
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "url new url() 异常", e.getMessage(), url);
            return AjaxResponse.success(null);
        }

        String signature = base64Encode(hmacStr);
        String authHeader = String.format("hmac id=\"%s\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"%s\""
                , apiAppKey, signature);

        KmAuthDTO kmAuthDTO = new KmAuthDTO();
        heardMap.put("Accept", acceptHeader);
        heardMap.put("Host", host);
        heardMap.put("x-date", xDate);
        heardMap.put("Content-Type", contentType);
        heardMap.put("Content-MD5", contentMD5);
        heardMap.put("Authorization", authHeader);
        kmAuthDTO.setHttpMethod(httpMethod);
        kmAuthDTO.setHeardMap(heardMap);
        kmAuthDTO.setReqBody(reqBody);
        kmAuthDTO.setUrl(url);
        return AjaxResponse.success(kmAuthDTO);
    }


    private static String getGMTTime() {
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(cd.getTime());
    }

    private static String sortQueryParams(String queryParam) {
        if (StringUtils.isEmpty(queryParam)) {
            return "";
        }


        String[] queryParams = queryParam.split("&");
        Map<String, String> queryPairs = new TreeMap<>();
        for (String query : queryParams) {
            String[] kv = query.split("=");
            queryPairs.put(kv[0], kv[1]);
        }

        StringBuilder sortedParamsBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : queryPairs.entrySet()) {
            sortedParamsBuilder.append(entry.getKey());
            sortedParamsBuilder.append("=");
            sortedParamsBuilder.append(entry.getValue());
            sortedParamsBuilder.append("&");
        }
        String sortedParams = sortedParamsBuilder.toString();
        sortedParams = sortedParams.substring(0, sortedParams.length() - 1);

        return sortedParams;
    }

    private static byte[] hmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        Mac mac = Mac.getInstance(MAC_NAME);
        mac.init(secretKey);

        byte[] text = encryptText.getBytes(ENCODING);
        return mac.doFinal(text);
    }

    private static String base64Encode(byte[] key) {
        final Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(key);
    }

    private static String getMD5(String str) {
        return DigestUtils.md5Hex(str);
    }
}
