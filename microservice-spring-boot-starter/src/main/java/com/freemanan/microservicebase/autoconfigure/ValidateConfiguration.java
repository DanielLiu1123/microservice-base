package com.freemanan.microservicebase.autoconfigure;

import com.freemanan.microservicebase.autoconfigure.condition.ConditionalOnGrpcClientEnabled;
import com.freemanan.microservicebase.autoconfigure.condition.ConditionalOnGrpcEnabled;
import com.freemanan.microservicebase.autoconfigure.condition.ConditionalOnGrpcServerEnabled;
import com.freemanan.microservicebase.grpc.GrpcProperties;
import io.envoyproxy.pgv.ReflectiveValidatorIndex;
import io.envoyproxy.pgv.grpc.ValidatingClientInterceptor;
import io.envoyproxy.pgv.grpc.ValidatingServerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class ValidateConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ValidatingClientInterceptor.class})
    @ConditionalOnGrpcEnabled
    static class Grpc {

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE + 100)
        @ConditionalOnGrpcClientEnabled
        @ConditionalOnProperty(
                prefix = GrpcProperties.PREFIX + ".client",
                name = "validation-enabled",
                matchIfMissing = true)
        public ValidatingClientInterceptor validatingClientInterceptor() {
            return new ValidatingClientInterceptor(new ReflectiveValidatorIndex());
        }

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE + 100)
        @ConditionalOnGrpcServerEnabled
        @ConditionalOnProperty(
                prefix = GrpcProperties.PREFIX + ".server",
                name = "validation-enabled",
                matchIfMissing = true)
        public ValidatingServerInterceptor validatingServerInterceptor() {
            return new ValidatingServerInterceptor(new ReflectiveValidatorIndex());
        }
    }
}
