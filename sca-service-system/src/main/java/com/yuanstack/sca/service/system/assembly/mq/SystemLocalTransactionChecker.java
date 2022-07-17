package com.yuanstack.sca.service.system.assembly.mq;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: 校验本地事务
 * @author: hansiyuan
 * @date: 2022/6/29 2:56 PM
 */
@Slf4j
@Component
public class SystemLocalTransactionChecker implements LocalTransactionChecker {

    @Override
    public TransactionStatus check(Message message) {

        if (message.getTopic().equals(RocketMQConstant.TOPIC.TOPIC_MESSAGE.getTopic())) {
            log.info("校验本地事务");
        }

        return TransactionStatus.CommitTransaction;
    }
}
