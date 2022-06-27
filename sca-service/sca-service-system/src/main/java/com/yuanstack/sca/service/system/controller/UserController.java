package com.yuanstack.sca.service.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 用户控制器类
 * @author: hansiyuan
 * @date: 2022/6/27 6:27 PM
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/user/{id}")
    public String getUserInfo(@PathVariable Long id) {
        return "Hello " + id;
    }
}
