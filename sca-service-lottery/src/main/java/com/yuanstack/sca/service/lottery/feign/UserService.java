package com.yuanstack.sca.service.lottery.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @description: UserService OpenFeign
 * @author: hansiyuan
 * @date: 2022/6/28 10:46 AM
 */
@FeignClient(value = "sca-service-system", path = "/api")
public interface UserService {

    @GetMapping("/user/{id}")
    String getUserInfo(@PathVariable("id") Long id);

}
