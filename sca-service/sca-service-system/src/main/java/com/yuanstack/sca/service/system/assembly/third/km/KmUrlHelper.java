package com.yuanstack.sca.service.system.assembly.third.km;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @description:
 * @author: hansiyuan
 * @date: 2022/6/29 3:23 PM
 */
@Configuration
@Getter
public class KmUrlHelper {

    @Resource
    private KmConfig kmConfig;

    public String buildKmMusicListUrl() {
        return kmConfig.getMusicListUrl();
    }
}
