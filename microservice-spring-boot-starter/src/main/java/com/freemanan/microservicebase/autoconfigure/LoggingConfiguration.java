package com.freemanan.microservicebase.autoconfigure;

import com.freemanan.microservicebase.autoconfigure.condition.ConditionalOnGrpcEnabled;
import com.freemanan.microservicebase.autoconfigure.condition.ConditionalOnGrpcServerEnabled;
import com.freemanan.microservicebase.autoconfigure.condition.ConditionalOnHttpEnabled;
import com.freemanan.microservicebase.autoconfigure.condition.ConditionalOnHttpServerEnabled;
import com.freemanan.microservicebase.grpc.GrpcProperties;
import com.freemanan.microservicebase.grpc.observability.logging.LoggingServerInterceptor;
import com.freemanan.microservicebase.http.HttpProperties;
import com.freemanan.microservicebase.http.server.mvc.LoggingHandlerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
class LoggingConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnGrpcEnabled
    static class Grpc {

        @Bean
        @ConditionalOnGrpcServerEnabled
        @ConditionalOnProperty(
                prefix = GrpcProperties.PREFIX + ".observability.logging",
                name = "enabled",
                matchIfMissing = true)
        public LoggingServerInterceptor loggingServerInterceptor() {
            return new LoggingServerInterceptor();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnHttpEnabled
    static class Http {

        @Bean
        @ConditionalOnHttpServerEnabled
        @ConditionalOnProperty(
                prefix = HttpProperties.PREFIX + ".observability.logging",
                name = "enabled",
                matchIfMissing = true)
        public WebMvcConfigurer loggingWebMvcConfigurer(HttpProperties properties) {
            return new WebMvcConfigurer() {
                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    registry.addInterceptor(new LoggingHandlerInterceptor())
                            .addPathPatterns(
                                    properties.getObservability().getLogging().getIncludePatterns())
                            .excludePathPatterns(
                                    properties.getObservability().getLogging().getExcludePatterns());
                }
            };
        }
    }
}
