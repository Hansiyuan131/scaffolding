package com.yuanstack.sca.service.system.assembly.mq;

import com.google.common.base.Strings;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @description: 测试
 * @author: hansiyuan
 * @date: 2022/6/29 2:59 PM
 */
public class DemoMain {

    @Resource
    private MsgProducer msgProducer;

    public String sendDelayMessage(Long id) {
        //发送延时任务
        String delayMessageResult = msgProducer.sendDelayMessage(String.valueOf(id), new Date());
        if (Strings.isNullOrEmpty(delayMessageResult)) {
            return "FAIL";
        }
        return "SUCCESSFUL";
    }
}
