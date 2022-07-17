package com.yuanstack.sca.service.system.assembly.third.km;

import lombok.Getter;

/**
 * @description: KmErrorEnum
 * @author: hansiyuan
 * @date: 2022/6/29 5:04 PM
 */
@Getter
public enum KmErrorEnum {
    USER_INPUT_ERROR("400", "您输入的数据错误或您没有权限访问资源！"),
    SYSTEM_ERROR("500", "系统出现异常，请您稍后再试或联系管理员！"),
    OTHER_ERROR("999", "系统出现未知异常，请联系管理员！"),
    E_SYS_HTTP_429("429", "超时");

    /**
     * 异常类型中文描述
     */
    private final String desc;

    /**
     * code
     */
    private final String code;

    KmErrorEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static KmErrorEnum getKmErrorEnumByCode(String toString) {
        return E_SYS_HTTP_429;
    }
}
