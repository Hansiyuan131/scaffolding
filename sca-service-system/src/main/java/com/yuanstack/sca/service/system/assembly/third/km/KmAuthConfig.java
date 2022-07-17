package com.yuanstack.sca.service.system.assembly.third.km;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @description: KmAuthConfig
 * @author: hansiyuan
 * @date: 2022/6/29 3:31 PM
 */
@Getter
@Setter
@Configuration
public class KmAuthConfig {


    @Value("${km.client.accesskey}")
    private String apiAppKey;


    @Value("${km.client.apiAppSecret}")
    private String apiAppSecret;


    @Value("${km.client.host}")
    private String host;


}
