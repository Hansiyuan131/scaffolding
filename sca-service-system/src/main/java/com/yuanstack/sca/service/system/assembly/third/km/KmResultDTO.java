package com.yuanstack.sca.service.system.assembly.third.km;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description: KmResultDTO
 * @author: hansiyuan
 * @date: 2022/6/29 4:59 PM
 */
@Getter
@Setter
@ToString
public class KmResultDTO<T> implements Serializable {


    private static final long serialVersionUID = 4867823514960206179L;

    public static final Integer SUCCESS = 0;
    public static final Integer FAIL = 1;
    /**
     * 返操作结果。成功：0，失败：1
     */
    private Integer ret;
    /**
     * 错误码，当操作失败时（ret=1）返回。
     */
    private Integer errorCode;
    /**
     * 请求返回码
     */
    private Integer code;
    /**
     * 请求唯一标识
     */
    private String requestId;
    /**
     * 返回数据，当操作成功时（ret=0）返回。
     */
    private T data;
    /**
     * 错误信息，当操作失败时（ret=1）返回。
     */
    private String msg;
    /**
     * 错误信息，当操作失败时（ret=1）返回。
     */
    private T errInfos;
    /**
     * http请求返回码
     */
    private String statusCode;

    public boolean isSuccess() {
        return this.ret != null && this.ret.equals(SUCCESS);
    }
}
