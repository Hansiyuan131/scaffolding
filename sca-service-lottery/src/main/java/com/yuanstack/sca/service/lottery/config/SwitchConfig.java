package com.yuanstack.sca.service.lottery.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author hansiyuan
 * @date 2022年06月29日 21:52
 */
@Data
@Component
@RefreshScope
public class SwitchConfig {

    @Value("${disableAwardRequest:false}")
    private Boolean disableCoupon;
}
