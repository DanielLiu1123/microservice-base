package com.llw.microservicebase.it.rpc.feign.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Freeman
 * @since 2022/9/12
 */
@RestController
public class FooController {

    @GetMapping("/v1/rpcfeign/foo")
    public String foo() {
        return "foo v1";
    }
}
