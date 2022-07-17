package com.yuanstack.sca.service.system.assembly.desensitize;

import com.yuanstack.sca.service.system.common.utils.CommonUtils;

/**
 * @description: 邮箱脱敏
 * @author: hansiyuan
 * @date: 2022/6/29 10:39 AM
 */
public class EmailDesensitizeStrategy extends DesensitizeStrategy {
    @Override
    public String encryptToStr(String parameter) {
        return CommonUtils.hideMailBox(parameter);
    }
}
