package com.yuanstack.sca.service.system.assembly.dingtalk;

/**
 * @description: 消息
 * @author: hansiyuan
 * @date: 2022/6/29 2:08 PM
 */
public interface Message {

    /**
     * 返回消息的Json格式字符串
     *
     * @return 消息的Json格式字符串
     */
    String toJsonString();
}

