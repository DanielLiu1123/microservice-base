package com.llw.microservicebase.rpc.feign;

import com.llw.microservicebase.rpc.feign.interceptor.TracingInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author Freeman
 * @since 1.0.0
 */
@AutoConfiguration
public class RpcFeignAutoConfiguration {

    @Bean
    public TracingInterceptor feignTracingInterceptor() {
        return new TracingInterceptor();
    }

}
