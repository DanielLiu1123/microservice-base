package com.llw.microservicebase.it.rpc.feign.controller;

import com.llw.microservicebase.it.rpc.feign.api.EchoApi;
import com.llw.microservicebase.it.rpc.feign.api.FooApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Freeman
 * @since 2022/9/12
 */
@RestController
public class EchoController {

    @Autowired
    private FooApi fooApi;

    @GetMapping("/v1/rpcfeign/echo")
    public String echo() {
        return fooApi.foo();
    }

}
