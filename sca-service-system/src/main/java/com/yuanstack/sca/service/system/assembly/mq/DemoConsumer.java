package com.yuanstack.sca.service.system.assembly.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Message;
import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @description: demo
 * @author: hansiyuan
 * @date: 2022/6/29 2:41 PM
 */
@Slf4j
@Component
@MQConsumer(value = RocketMQConstant.TOPIC.TOPIC_MESSAGE, tags = {RocketMQConstant.TAG.TOPIC_PHONE_MSG_TAG, RocketMQConstant.TAG.TOPIC_EMAIL_MSG_TAG, RocketMQConstant.TAG.TOPIC_ALIPAY_MSG_TAG})
public class DemoConsumer extends BaseConsumer {

    @Override
    protected void consumerMsg(Message message) {
        LogUtils.info(log, ModelEnum.COMMON_UTILS, "公告 消息中心 支付宝消息", JSON.toJSONString(message));
        RocketMQConstant.TAG tag = RocketMQConstant.TAG.getTag(message.getTag());
        Long id = Long.valueOf(new String(message.getBody()));
        switch (Objects.requireNonNull(tag)) {
            case TOPIC_PHONE_MSG_TAG:
                try {
                    handlerMsg1();
                } catch (Exception ex) {
                    LogUtils.error(log, ModelEnum.COMMON_UTILS, " msg task execute fail", ex);
                }

                break;
            case TOPIC_EMAIL_MSG_TAG:
                try {
                    handlerMsg2();
                } catch (Exception ex) {
                    LogUtils.error(log, ModelEnum.COMMON_UTILS, " msg task execute fail", ex);
                }
                break;
            case TOPIC_ALIPAY_MSG_TAG:
                try {
                    handlerMsg();
                } catch (Exception ex) {
                    LogUtils.error(log, ModelEnum.COMMON_UTILS, " msg task execute fail", ex);
                }
                break;
            default:
                throw new IllegalArgumentException("未知tag");
        }
    }

    private void handlerMsg1() {

    }

    private void handlerMsg2() {
    }

    private void handlerMsg() {
    }
}
