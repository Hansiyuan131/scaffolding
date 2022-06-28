package com.yuanstack.sca.service.system.config;

import org.springframework.context.annotation.Configuration;

/**
 * @description: 开关配置
 * @author: hansiyuan
 * @date: 2022/6/28 3:56 PM
 */
@Configuration
public class SwitchConfig {
    /**
     * 日志打印级别 0: Debug 1: Info 2:Warn 3: Error 4: Exception
     */
    public static int LogPrintLevel = 1;
}
