package com.yuanstack.sca.service.system.assembly.httpclient;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.io.Serializable;

/**
 * @description: HttpFileDTO
 * @author: hansiyuan
 * @date: 2022/6/29 12:10 PM
 */
@Getter
@Setter
public class HttpFileDTO implements Serializable {
    private static final long serialVersionUID = -3229023289617160023L;

    /**
     * 入参名称
     */
    private String paramName;

    /**
     * 文件流
     */
    private InputStream inputStream;

    /**
     * 文件名，带后缀
     */
    private String fileName;
}
