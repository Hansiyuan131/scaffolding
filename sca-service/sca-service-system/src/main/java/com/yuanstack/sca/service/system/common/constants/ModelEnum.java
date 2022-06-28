package com.yuanstack.sca.service.system.common.constants;

import lombok.Getter;

/**
 * @description: 系统模块枚举
 * @author: hansiyuan
 * @date: 2022/6/28 4:05 PM
 */
@Getter
public enum ModelEnum {

    // 业务模块
    BIZ_USER("user", "用户模块"),

    // 组件
    ASSEMBLY_ROCKETMQ("rocketMQ", "rocketMQ模块");

    /**
     * 编码
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

    ModelEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
