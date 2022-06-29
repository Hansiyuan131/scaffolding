package com.yuanstack.sca.service.system.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yuanstack.sca.service.system.common.constants.ModelEnum.COMMON_UTILS;

/**
 * @description: 通用工具类
 * @author: hansiyuan
 * @date: 2022/6/29 10:44 AM
 */
@Slf4j
public class CommonUtils {
    private static final Map<String, String> CHINA_MOBILE_REGION = new HashMap<String, String>() {{
        put("+86", "+86");
        put("86", "86");
        put("+852", "+852");
        put("852", "852");
        put("+853", "+853");
        put("853", "853");
        put("+886", "+886");
        put("886", "886");
    }};

    public static String desensitizedName(String fullName) {
        if (StringUtils.isEmpty(fullName)) {
            return fullName;
        }
        String name = StringUtils.left(fullName, 1);
        return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
    }

    public static String desensitizedAddress(String address) {
        if (StringUtils.isEmpty(address)) {
            return address;
        }
        return StringUtils.left(address, 3).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(address, address.length() - 11), StringUtils.length(address), "*"), "***"));
    }

    /**
     * 手机号隐藏加* 中国手机号隐藏中间4位，其他隐藏位数不大于二分之一
     *
     * @param mobileRegion
     * @param mobileNo
     * @return
     */
    public static String hideMobileNo(String mobileRegion, String mobileNo) {
        if (StringUtils.isBlank(mobileNo) || mobileNo.length() <= 2) {
            return mobileNo;
        } else if (CHINA_MOBILE_REGION.containsKey(mobileRegion)) {
            return mobileNo.substring(0, 3) + "****" + mobileNo.substring(7);
        } else {
            StringBuilder hideMobileNo = new StringBuilder();

            int length = mobileNo.length();
            int hideCount = length / 2;
            int prefixCount = hideCount / 2 + hideCount % 2;

            hideMobileNo.append(mobileNo, 0, prefixCount);

            for (int i = 0; i < hideCount; i++) {
                hideMobileNo.append("*");
            }

            hideMobileNo.append(mobileNo.substring(prefixCount + hideCount));

            return hideMobileNo.toString();
        }
    }

    public static String hideMailBox(String mailBox) {
        if (StringUtils.isBlank(mailBox)) {
            return mailBox;
        }
        StringBuilder hideMail = new StringBuilder();
        int mailLength = mailBox.length();
        int prefix = mailBox.indexOf("@");
        String mailPrefix = mailBox.substring(0, prefix);
        String mailSuffix = mailBox.substring(prefix, mailLength);
        if (mailPrefix.length() > 3) {
            hideMail.append(mailPrefix.substring(0, 3));
        } else {
            hideMail.append(mailPrefix);
        }
        hideMail.append("***");
        hideMail.append(mailSuffix);
        return hideMail.toString();
    }

    /**
     * 获取map中第一个非空数据值
     *
     * @param <K> Key的类型
     * @param <V> Value的类型
     * @param map 数据源
     * @return 返回的值
     */
    public static <K, V> V getFirstNotNull(Map<K, V> map) {
        V obj = null;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            obj = entry.getValue();
            if (obj != null) {
                break;
            }
        }

        return obj;
    }

    /**
     * 获取map中第一个key值
     *
     * @param map 数据源
     * @return
     */
    public static <K, V> K getKeyOrNull(Map<K, V> map) {
        K obj = null;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            obj = entry.getKey();
            if (obj != null) {
                break;
            }
        }
        return obj;
    }

    /**
     * jsonArray sublist
     *
     * @param start
     * @param length
     * @param jsonArray
     * @return
     */
    public static JSONArray subJSONArray(int start, int length, JSONArray jsonArray) {
        if (CollectionUtils.isEmpty(jsonArray) || start > jsonArray.size()) {
            return new JSONArray();
        }

        int size = jsonArray.size();
        int toIndex = start + length > size ? size : start + length;

        List<Object> subList = jsonArray.subList(start, toIndex);

        return JSON.parseArray(JSON.toJSONString(subList));
    }

    public static void main(String[] args) {
        LogUtils.info(log, COMMON_UTILS, hideMobileNo("+123", "140000000330403403043"));
    }
}
