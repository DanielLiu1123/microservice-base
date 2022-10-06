package com.llw.microservicebase.web;

import com.llw.microservicebase.web.filter.TracingFilter;
import com.llw.microservicebase.web.handler.ExceptionAdvice;
import com.llw.microservicebase.web.interceptor.LoggingInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Freeman
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = WebProperties.PREFIX, name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(WebProperties.class)
public class WebAutoConfiguration {

    @Bean
    public TracingFilter tracingFilter() {
        return new TracingFilter();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = WebProperties.PREFIX + ".exception-handler", name = "enabled", matchIfMissing = true)
    static class ExceptionHandlerConfiguration {
        @Bean
        public ExceptionAdvice exceptionAdvice() {
            return new ExceptionAdvice();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = WebProperties.PREFIX + ".logging", name = "enabled", matchIfMissing = true)
    static class LoggingConfiguration implements WebMvcConfigurer {
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new LoggingInterceptor());
        }
    }

}
