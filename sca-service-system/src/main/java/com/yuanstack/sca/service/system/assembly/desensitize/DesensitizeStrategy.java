package com.yuanstack.sca.service.system.assembly.desensitize;

/**
 * @description: 脱敏策略
 * @author: hansiyuan
 * @date: 2022/6/29 10:40 AM
 */
public abstract class DesensitizeStrategy {
    /**
     * 脱敏
     *
     * @param parameter 待脱敏字符串
     * @return 结果
     */
    public abstract String encryptToStr(String parameter);
}
