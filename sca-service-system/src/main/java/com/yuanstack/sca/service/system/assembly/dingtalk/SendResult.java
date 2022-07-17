package com.yuanstack.sca.service.system.assembly.dingtalk;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 钉钉机器人接口返回值
 * @author: hansiyuan
 * @date: 2022/6/29 2:07 PM
 */
@Data
@Builder
@Accessors(chain = true)
public class SendResult {
    /** 成功状态码 **/
    public static final Integer SUCCESS_CODE = 0;
    public static final String SUCCESS_MSG = "发送成功";
    /** 失败状态码(非0都是失败状态) **/
    public static final Integer ERROR_CODE = -1;
    public static final String ERROR_MSG = "发送失败";

    /** 状态码 **/
    private Integer errorCode;

    /** 错误信息 **/
    private String errorMsg;

    public static SendResult success() {
        return SendResult.builder().errorCode(SUCCESS_CODE).errorMsg(SUCCESS_MSG).build();
    }

    public static SendResult error() {
        return SendResult.builder().errorCode(ERROR_CODE).errorMsg(ERROR_MSG).build();
    }

    public static SendResult error(String extraMsg) {
        return SendResult.builder().errorCode(ERROR_CODE).errorMsg(ERROR_MSG + "\n" + extraMsg).build();
    }
}

