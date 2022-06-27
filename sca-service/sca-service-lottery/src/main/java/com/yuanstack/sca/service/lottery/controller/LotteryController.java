package com.yuanstack.sca.service.lottery.controller;

import com.yuanstack.sca.service.lottery.service.LotteryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description: 抽奖控制器
 * @author: hansiyuan
 * @date: 2022/6/27 6:31 PM
 */
@RestController
@RequestMapping("/api")
public class LotteryController {

    @Resource
    private LotteryService lotteryService;

    @GetMapping("/lottery/extract/{userId}")
    public String extractPrize(@PathVariable Long userId) {
        return lotteryService.extractPrize(userId);
    }
}
