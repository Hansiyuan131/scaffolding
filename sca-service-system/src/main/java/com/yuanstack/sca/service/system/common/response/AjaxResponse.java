package com.yuanstack.sca.service.system.common.response;

import com.yuanstack.sca.service.system.common.exception.CustomException;
import com.yuanstack.sca.service.system.common.constants.CustomExceptionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @description: 统一响应结果
 * @author: hansiyuan
 * @date: 2022/6/28 3:12 PM
 */
@ApiModel(value = "统一响应结果")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AjaxResponse<T> implements Serializable {

    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";
    public static final String SUCCESS_CODE = "200";
    public static final String SUCCESS_MESSAGE = "请求响应成功!";

    @ApiModelProperty(value = "状态 SUCCESS:成功 FAIL:失败", example = "SUCCESS")
    private String result;

    @ApiModelProperty(value = "状态码", example = "200")
    private String code;

    @ApiModelProperty(value = "状态描述", example = "请求响应成功!")
    private String message;

    @ApiModelProperty(value = "业务数据")
    private T data;

    @ApiModelProperty(value = "链路id")
    private String traceId;

    public boolean isSuccess() {
        return this.result != null && this.result.equals(SUCCESS);
    }

    /**
     * 请求出现异常时的响应数据封装
     *
     * @param e 异常类型
     * @return AjaxResponse<Object>
     */
    public static AjaxResponse<Object> error(CustomException e) {
        return AjaxResponse.builder()
                .result(FAIL)
                .code(e.getCode())
                .message(e.getMessage())
                .data(null)
                .build();
    }

    /**
     * 请求出现异常时的响应数据封装
     *
     * @param customExceptionType customExceptionType
     * @param errorMessage        errorMessage
     * @return AjaxResponse<Object>
     */
    public static AjaxResponse<Object> error(CustomExceptionType customExceptionType,
                                             String errorMessage) {
        return AjaxResponse.builder()
                .result(FAIL)
                .code(customExceptionType.getCode())
                .message(errorMessage)
                .data(null)
                .build();
    }

    public static <T> AjaxResponse<T> error(T data, CustomExceptionType customExceptionType, String errorMessage) {
        AjaxResponse<T> resultBean = new AjaxResponse<>();
        resultBean.setResult(FAIL);
        resultBean.setCode(customExceptionType.getCode());
        resultBean.setMessage(errorMessage);
        resultBean.setData(data);
        return resultBean;
    }

    public static <T> AjaxResponse<T> error(T data, CustomExceptionType customExceptionType) {
        AjaxResponse<T> resultBean = new AjaxResponse<>();
        resultBean.setResult(FAIL);
        resultBean.setCode(customExceptionType.getCode());
        resultBean.setMessage(customExceptionType.getDesc());
        resultBean.setData(data);
        return resultBean;
    }

    public static <T> AjaxResponse<T> error(T data, String errorMessage) {
        AjaxResponse<T> resultBean = new AjaxResponse<>();
        resultBean.setResult(FAIL);
        resultBean.setCode(String.valueOf(999));
        resultBean.setMessage(errorMessage);
        resultBean.setData(data);
        return resultBean;
    }

    /**
     * 请求成功地响应，不带查询数据（用于删除、修改、新增接口）
     *
     * @return AjaxResponse<Object>
     */
    public static AjaxResponse<Object> success() {
        return AjaxResponse.builder()
                .result(SUCCESS)
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .data(null)
                .build();
    }

    /**
     * 请求成功的响应，带有查询数据（用于数据查询接口）
     *
     * @param data 数据
     * @param <T>  范型
     * @return AjaxResponse<T>
     */
    public static <T> AjaxResponse<T> success(T data) {
        AjaxResponse<T> resultBean = new AjaxResponse<>();
        resultBean.setResult(SUCCESS);
        resultBean.setCode(SUCCESS_CODE);
        resultBean.setMessage(SUCCESS_MESSAGE);
        resultBean.setData(data);
        return resultBean;
    }

    /**
     * 请求成功的响应，带有查询数据（用于数据查询接口）
     *
     * @param data    数据
     * @param message message
     * @return AjaxResponse<T>
     */
    public static <T> AjaxResponse<T> success(T data, String message) {
        AjaxResponse<T> resultBean = new AjaxResponse<>();
        resultBean.setResult(SUCCESS);
        resultBean.setCode(SUCCESS_CODE);
        resultBean.setMessage(message);
        resultBean.setData(data);
        return resultBean;
    }
}
