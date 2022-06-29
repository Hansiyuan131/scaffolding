package com.yuanstack.sca.service.system.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: JsonStringUtils
 * @author: hansiyuan
 * @date: 2022/6/29 11:47 AM
 */
public class JsonStringUtils {
    /**
     * 判断是否是json
     */
    public static boolean isJson(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        Object parse = null;
        try {
            parse = JSON.parse(str);
        } catch (Exception e) {
            // ignore exception and return false
        }
        if (parse instanceof JSONObject || parse instanceof JSONArray) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是json
     */
    public static boolean isJsonString(String str) {
        try {
            JSONValidator validator = JSONValidator.from(str);
            return validator.validate() && validator.getType() == JSONValidator.Type.Object;
        } catch (Exception e) {
            // ignore exception and return false
        }
        return false;
    }

    /**
     * 判断是否是jsonArray
     */
    public static boolean isJsonArray(String rawConfig) {
        try {
            JSONValidator validator = JSONValidator.from(rawConfig);
            return validator.validate() && validator.getType() == JSONValidator.Type.Array;
        } catch (Exception e) {
            // ignore exception and return false
        }
        return false;
    }
}
