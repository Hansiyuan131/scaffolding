package com.yuanstack.sca.service.lottery.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.google.common.collect.Maps;
import com.yuanstack.sca.service.lottery.config.SwitchConfig;
import com.yuanstack.sca.service.lottery.service.LotteryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/getAward")
    @SentinelResource(value = "getAward")
    public String getAward(@RequestParam("id") Long id) {
        return "获取奖品";
    }

    @GetMapping("/getBatch")
    @SentinelResource(value = "getTemplateInBatch", blockHandler = "getTemplateInBatchBlock")
    public Map<Long, String> getTemplateInBatch(@RequestParam("ids") Collection<Long> ids) {
        return new HashMap<>();
    }

    public Map<Long, String> getTemplateInBatchBlock(Collection<Long> ids, BlockException exception) {
        log.info("接口被限流");
        return Maps.newHashMap();
    }
}
