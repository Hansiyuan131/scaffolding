package com.yuanstack.sca.service.system.common.exception;

import com.yuanstack.sca.service.system.common.constants.CustomExceptionType;

/**
 * @description: 自定义异常类型
 * @author: hansiyuan
 * @date: 2022/6/28 3:28 PM
 */

public class CustomException extends RuntimeException {
    /**
     * 异常错误编码
     */
    private String code;

    /**
     * 异常信息
     */
    private String message;

    private CustomException() {
    }

    public CustomException(CustomExceptionType exceptionTypeEnum) {
        this.code = exceptionTypeEnum.getCode();
        this.message = exceptionTypeEnum.getDesc();
    }

    public CustomException(CustomExceptionType exceptionTypeEnum, String message) {
        this.code = exceptionTypeEnum.getCode();
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
