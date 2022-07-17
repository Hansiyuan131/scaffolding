package com.yuanstack.sca.service.system.assembly.mq;

import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * @description: SystemProducer
 * @author: hansiyuan
 * @date: 2022/6/29 2:53 PM
 */
@Slf4j
@Component
public class SystemProducer extends ProducerBean {

    @Resource
    private RocketMQConfig rocketMQConfig;

    @PostConstruct
    private void initProducerInstance() {
        LogUtils.info(log, ModelEnum.COMMON_UTILS, "启动RocketMQ生产者");

        super.setProperties(rocketMQConfig.getBaseProperties());

        super.start();
        LogUtils.info(log, ModelEnum.COMMON_UTILS, "启动RocketMQ生产者成功");
    }

    @PreDestroy
    private void destroy() {
        super.shutdown();
        LogUtils.info(log, ModelEnum.COMMON_UTILS, "关闭RocketMQ生产者成功");
    }
}
