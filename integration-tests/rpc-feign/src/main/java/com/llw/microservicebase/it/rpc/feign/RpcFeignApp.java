package com.llw.microservicebase.it.rpc.feign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Freeman
 * @since 1.0.0
 */
@SpringBootApplication
@EnableFeignClients
public class RpcFeignApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(RpcFeignApp.class, args);
        System.out.println(ctx.getEnvironment().getProperty("username"));
    }
}
