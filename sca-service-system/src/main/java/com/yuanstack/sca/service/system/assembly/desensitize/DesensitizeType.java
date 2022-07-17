package com.yuanstack.sca.service.system.assembly.desensitize;

import lombok.Getter;

/**
 * @description: 脱敏类型枚举
 * @author: hansiyuan
 * @date: 2022/6/29 10:38 AM
 */
@Getter
public enum DesensitizeType {
    /**
     * 手机号脱敏
     */
    PHONE("mobile", "手机号处理策略", new PhoneDesensitizeStrategy()),
    EMAIL("email", "邮箱处理策略", new EmailDesensitizeStrategy()),
    DEFAULT("default", "默认处理策略", new DefaultDesensitizeStrategy());
    private final String type;
    private final String desc;
    private final DesensitizeStrategy desensitizeStrategy;

    DesensitizeType(String type, String desc, DesensitizeStrategy desensitizeStrategy) {
        this.type = type;
        this.desc = desc;
        this.desensitizeStrategy = desensitizeStrategy;
    }

    public static DesensitizeStrategy getDescByType(String type) {
        for (DesensitizeType desc : DesensitizeType.values()) {
            if (desc.getType().equals(type)) {
                return desc.getDesensitizeStrategy();
            }
        }
        return null;
    }
}
