package com.yuanstack.sca.service.system.assembly.dingtalk;

import com.alibaba.fastjson.JSONObject;
import com.yuanstack.sca.service.system.assembly.httpclient.HttpClientUtils;
import com.yuanstack.sca.service.system.assembly.httpclient.HttpResponseDTO;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: DingTalkUtil
 * @author: hansiyuan
 * @date: 2022/6/29 2:06 PM
 */
public class DingTalkUtil {

    /** 加密算法:HmacSHA256 **/
    private static final String ENCRYPTION_ALGORITHM = "HmacSHA256";

    /**
     * 通过关键字发送钉钉消息
     */
    public static SendResult sendByKeyWord(String webHookUrl, Map<String, Object> paramMap, Message message) throws Exception{
        return send(webHookUrl, paramMap, SecurityTypeEnum.KEYWORD, message);
    }

    /**
     * 通过加签方式发送钉钉消息
     */
    public static SendResult sendBySign(String webHookUrl, Map<String, Object> paramMap, Message message) throws Exception{
        return send(webHookUrl, paramMap, SecurityTypeEnum.SIGN, message);
    }

    /**
     * 发送钉钉消息
     * 返回值样例:
     * {"errcode":0,"errmsg":"ok"}
     * {"errcode":310000,"errmsg":"keywords not in content, more: [https://ding-doc.dingtalk.com/doc#/serverapi2/qf2nxq]"}
     *
     * @param webHookUrl 钉钉机器人url
     * @param paramMap 请求参数
     * @param securityTypeEnum 机器人安全设置类型
     * @param message 消息内容
     * @return
     * @throws Exception
     */
    private static SendResult send(String webHookUrl, Map<String, Object> paramMap, SecurityTypeEnum securityTypeEnum, Message message) throws Exception{
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json;charset=UTF-8");

        String accessToken = (String) paramMap.get("access_token");
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(webHookUrl).append("?access_token=").append(accessToken);
        if (SecurityTypeEnum.SIGN == securityTypeEnum) {
            String signKey = (String) paramMap.get("sign");
            urlBuilder.append(sign(signKey));
        }

        HttpResponseDTO httpResponseDTO = HttpClientUtils.doPost(urlBuilder.toString(), null, headerMap, message.toJsonString());
        if (httpResponseDTO == null || StringUtils.isBlank(httpResponseDTO.getResponseStr())) {
            return SendResult.builder().errorCode(-1).errorMsg("返回值为空").build();
        }

        JSONObject jsonObject = JSONObject.parseObject(httpResponseDTO.getResponseStr());
        if (jsonObject == null) {
            return SendResult.builder().errorCode(-1).errorMsg("返回值解析为空").build();
        }

        return SendResult.builder()
                .errorCode(jsonObject.getInteger("errcode"))
                .errorMsg(jsonObject.getString("errmsg"))
                .build();
    }

    /**
     * 选择加签方式下的加签方法
     * @param secret 密钥，机器人安全设置页面，加签一栏下面显示的SEC开头的字符串
     * @return
     */
    private static String sign(String secret) throws Exception{
        long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance(ENCRYPTION_ALGORITHM);
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8.toString()), ENCRYPTION_ALGORITHM));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8.toString()));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8.toString());
        return "&timestamp=" + timestamp + "&sign=" + sign;
    }
}
