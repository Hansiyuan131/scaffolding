package com.yuanstack.sca.service.system.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @description: Configuration注解声明配置类
 * @author: hansiyuan
 * @date: 2022/6/27 6:15 PM
 */
@Configuration
public class SystemConfiguration {

    /**
     * 注册Bean并添加负载均衡功能
     *
     * @return WebClient
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder register() {
        return WebClient.builder();
    }
}
