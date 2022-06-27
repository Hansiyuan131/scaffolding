package com.yuanstack.sca.service.lottery.service;

/**
 * @description: 抽奖业务逻辑类
 * @author: hansiyuan
 * @date: 2022/6/27 6:38 PM
 */
public interface LotteryService {

    /**
     * 抽取奖品
     *
     * @param userId 用户
     * @return 抽取结果
     */
    String extractPrize(Long userId);
}
