package com.yuanstack.sca.restful.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuan
 */
@RestController
@RequestMapping("/check")
public class CheckController {

    @RequestMapping("/health")
    public String health() {
        return "ok";
    }
}
