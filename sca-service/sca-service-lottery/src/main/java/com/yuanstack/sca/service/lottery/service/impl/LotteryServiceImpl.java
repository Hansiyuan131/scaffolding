package com.yuanstack.sca.service.lottery.service.impl;

import com.yuanstack.sca.service.lottery.feign.UserService;
import com.yuanstack.sca.service.lottery.service.LotteryService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;

/**
 * @description: 抽奖业务逻辑实现类
 * @author: hansiyuan
 * @date: 2022/6/27 6:39 PM
 */
@Service
public class LotteryServiceImpl implements LotteryService {

    @Resource
    private WebClient.Builder webClientBuilder;

    @Resource
    private UserService userService;

    @Override
    public String extractPrize(Long userId) {
        String userInfo = userService.getUserInfo(userId);
        return userInfo + " 参与了抽奖";
    }


    private String getUserInfo(Long userId) {
        return webClientBuilder.build()
                .get()
                .uri("http://sca-service-system/api/user/" + userId)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
