package com.yuanstack.sca.service.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Api(tags = "用户相关接口")
public class UserController {

    @GetMapping("/user/{id}")
    @ApiOperation("根据用户Id获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", dataTypeClass = Long.class, dataType = "query"),
    })
    public String getUserInfo(@PathVariable Long id) {
        return "Hello " + id;
    }
}
