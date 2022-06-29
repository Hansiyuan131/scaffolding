package com.yuanstack.sca.service.system.assembly.dingtalk;

import lombok.Getter;

/**
 * @description: 安全设置目前有3种方式：
 * （1）方式一，自定义关键词
 * （2）方式二，加签
 * （3）方式三，IP地址（段）
 * 安全设置的上述三种方式，需要至少设置其中一种，以进行安全保护。
 * @author: hansiyuan
 * @date: 2022/6/29 2:07 PM
 */
@Getter
public enum SecurityTypeEnum {
    KEYWORD(1, "自定义关键词"),
    SIGN(2, "加签"),
    IP(2, "IP地址（段）");


    private final int code;
    private final String message;

    SecurityTypeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
