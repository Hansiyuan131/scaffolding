package com.yuanstack.sca.service.system.common.constants;

import lombok.Getter;

/**
 * @description: 自定义异常类型
 * @author: hansiyuan
 * @date: 2022/6/28 3:28 PM
 */
@Getter
public enum CustomExceptionType {
    USER_INPUT_ERROR("400", "您输入的数据错误或您没有权限访问资源！"),
    SYSTEM_ERROR("500", "系统出现异常，请您稍后再试或联系管理员！"),
    OTHER_ERROR("999", "系统出现未知异常，请联系管理员！");

    /**
     * 异常类型中文描述
     */
    private final String desc;

    /**
     * code
     */
    private final String code;

    CustomExceptionType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
