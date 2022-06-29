package com.yuanstack.sca.service.system.assembly.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * @description: HttpGetWithEntity
 * @author: hansiyuan
 * @date: 2022/6/29 12:15 PM
 */
@Slf4j
public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {

    public final static String METHOD_NAME = "GET";

    public HttpGetWithEntity(URI url) {
        super();
        this.setURI(url);
    }

    public HttpGetWithEntity(String url) {
        super();
        setURI(URI.create(url));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
