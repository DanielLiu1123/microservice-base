package com.llw.microservicebase.it.rpc.feign.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Freeman
 * @since 1.0.0
 */
@FeignClient(name = "rpc-feign", url = "http://localhost:8080", contextId = "FooApi")
public interface FooApi {
    @GetMapping("/v1/rpcfeign/foo")
    String foo();
}
