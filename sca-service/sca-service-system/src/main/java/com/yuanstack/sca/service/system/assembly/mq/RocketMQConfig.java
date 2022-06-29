package com.yuanstack.sca.service.system.assembly.mq;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @description: RocketMQConfig
 * @author: hansiyuan
 * @date: 2022/6/29 2:35 PM
 */
@Data
@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.accessKey}")
    private String accessKey;

    @Value("${rocketmq.secretKey}")
    private String secretKey;

    @Value("${rocketmq.nameSrvAddr}")
    private String nameSrvAddr;

    /**
     * 基础配置
     */
    public Properties getBaseProperties() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.AccessKey, accessKey);
        properties.setProperty(PropertyKeyConst.SecretKey, secretKey);
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, nameSrvAddr);

        return properties;
    }

}

