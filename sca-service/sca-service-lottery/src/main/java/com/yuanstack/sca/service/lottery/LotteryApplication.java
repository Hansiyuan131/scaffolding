package com.yuanstack.sca.service.lottery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @description: 抽奖应用
 * @author: hansiyuan
 * @date: 2022/6/27 5:34 PM
 */
@SpringBootApplication
@EnableDiscoveryClient
public class LotteryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LotteryApplication.class, args);
    }
}
