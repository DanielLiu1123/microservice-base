package com.freemanan.microservicebase.autoconfigure;

import com.freemanan.microservicebase.autoconfigure.condition.ConditionalOnGrpcEnabled;
import com.freemanan.microservicebase.autoconfigure.condition.ConditionalOnHttpEnabled;
import com.freemanan.microservicebase.core.tracing.HeaderTransferor;
import com.freemanan.microservicebase.grpc.GrpcProperties;
import com.freemanan.microservicebase.grpc.client.TracingClientInterceptor;
import com.freemanan.microservicebase.grpc.server.TracingServerInterceptor;
import com.freemanan.microservicebase.http.HttpProperties;
import com.freemanan.microservicebase.http.client.feign.TracingRequestInterceptor;
import com.freemanan.microservicebase.http.server.mvc.TracingOncePerRequestFilter;
import feign.Feign;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
class TracingConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnGrpcEnabled
    @ConditionalOnProperty(prefix = GrpcProperties.PREFIX, name = "tracing-enabled", matchIfMissing = true)
    static class Grpc {
        @Bean
        public TracingServerInterceptor tracingServerInterceptor(List<HeaderTransferor> headerTransferors) {
            return new TracingServerInterceptor(headerTransferors);
        }

        @Bean
        public TracingClientInterceptor tracingClientInterceptor() {
            return new TracingClientInterceptor();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnHttpEnabled
    @ConditionalOnProperty(prefix = HttpProperties.PREFIX, name = "tracing-enabled", matchIfMissing = true)
    static class Http {

        @Bean
        public FilterRegistrationBean<TracingOncePerRequestFilter> tracingOncePerRequestFilter(
                List<HeaderTransferor> headerTransferors) {
            FilterRegistrationBean<TracingOncePerRequestFilter> bean = new FilterRegistrationBean<>();
            bean.setFilter(new TracingOncePerRequestFilter(headerTransferors));
            bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
            return bean;
        }

        @Configuration(proxyBeanMethods = false)
        @ConditionalOnClass({Feign.class, FeignClientFactoryBean.class})
        static class OpenFeign {
            @Bean
            public TracingRequestInterceptor tracingRequestInterceptor() {
                return new TracingRequestInterceptor();
            }
        }
    }
}
