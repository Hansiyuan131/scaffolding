package com.yuanstack.sca.service.system.assembly.desensitize;

/**
 * @description: 默认脱敏策略
 * @author: hansiyuan
 * @date: 2022/6/29 10:54 AM
 */
public class DefaultDesensitizeStrategy extends DesensitizeStrategy {
    @Override
    public String encryptToStr(String parameter) {
        return parameter;
    }
}
