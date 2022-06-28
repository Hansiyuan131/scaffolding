package com.yuanstack.sca.service.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @description: 健康检查Check
 * @author: hansiyuan
 * @date: 2022/6/28 2:37 PM
 */
@RestController
public class HealthCheckController {
    /**
     * 健康检查
     */
    @GetMapping("/check")
    public String checkPreload(HttpServletRequest request) {
        return "success";
    }
}
