package com.yuanstack.sca.service.lottery.controller;

import com.yuanstack.sca.service.lottery.config.SwitchConfig;
import com.yuanstack.sca.service.lottery.service.LotteryService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class LotteryController {

    @Resource
    private LotteryService lotteryService;

    @Resource
    private SwitchConfig switchConfig;

    @GetMapping("/lottery/extract/{userId}")
    public String extractPrize(@PathVariable Long userId) {
        return lotteryService.extractPrize(userId);
    }

    @GetMapping("/switch")
    public String switchDy() {
        if (switchConfig.getDisableCoupon()) {
            log.info("动态开关 打开");
            return "开";
        } else {
            log.info("动态开关 关闭");
            return "关";
        }
    }
}
