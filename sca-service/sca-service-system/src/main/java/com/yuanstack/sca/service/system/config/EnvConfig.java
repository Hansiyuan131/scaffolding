package com.yuanstack.sca.service.system.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @description: 环境配置
 * @author: hansiyuan
 * @date: 2022/6/28 2:46 PM
 */
@Configuration
@PropertySource("classpath:config.properties")
@Getter
public class EnvConfig {
    @Value("${env}")
    private String env;

    /**
     * 是否日常
     *
     * @return True: 是 False 否
     */
    public boolean isDaily() {
        return "daily".equals(this.env);
    }

    /**
     * 是否开发
     *
     * @return True: 是 False 否
     */
    public boolean isDev() {
        return "dev".equals(this.env);
    }

    /**
     * 预发布/仿真环境
     *
     * @return True: 是 False 否
     */
    public boolean isPrepub() {
        return "prepub".equals(this.env);
    }

    /**
     * 是否生产
     *
     * @return True: 是 False 否
     */
    public boolean isProduct() {
        return "product".equals(this.env);
    }
}
