package com.yuanstack.sca.service.system.assembly.third.km;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @description: KmConfig
 * @author: hansiyuan
 * @date: 2022/6/29 4:52 PM
 */
@Configuration
@PropertySource("classpath:config.properties")
@Getter
public class KmConfig {
    @Value("${km.music.list.get}")
    private String musicListUrl;
}
