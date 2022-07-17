package com.yuanstack.sca.service.system.assembly.mq;

import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * @description: 事务消息
 * @author: hansiyuan
 * @date: 2022/6/29 2:57 PM
 */
@Slf4j
@Component
@Configuration
public class TransactionProducer extends TransactionProducerBean {

    @Resource
    private RocketMQConfig rocketMQConfig;

    @Resource
    private SystemLocalTransactionChecker checker;

    @PostConstruct
    private void initProducerInstance() {
        log.info("=== 启动RocketMQ事务生产者 ===");

        super.setProperties(rocketMQConfig.getBaseProperties());
        super.setLocalTransactionChecker(checker);
        super.start();
        log.info("=== 启动RocketMQ事务生产者成功 ===");
    }

    @PreDestroy
    private void destroy() {
        super.shutdown();
        log.info("=== 关闭RocketMQ事务生产者成功 ===");
    }


}

