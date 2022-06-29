package com.yuanstack.sca.service.system.assembly.httpclient;

import org.apache.http.ssl.TrustStrategy;

import java.security.cert.X509Certificate;

/**
 * @description: SSLTrustStrategy
 * @author: hansiyuan
 * @date: 2022/6/29 12:16 PM
 */
public class SSLTrustStrategy implements TrustStrategy {
    @Override
    public boolean isTrusted(X509Certificate[] chain, String authType) {
        return true;
    }
}
