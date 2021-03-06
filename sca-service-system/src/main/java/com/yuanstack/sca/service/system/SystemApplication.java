package com.yuanstack.sca.service.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @description: RBAC 系统应用
 * @author: hansiyuan
 * @date: 2022/6/27 5:32 PM
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableSwagger2
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
