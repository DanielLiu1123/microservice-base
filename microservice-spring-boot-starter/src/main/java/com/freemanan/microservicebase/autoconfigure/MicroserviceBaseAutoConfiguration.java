package com.freemanan.microservicebase.autoconfigure;

import com.freemanan.microservicebase.core.App;
import com.freemanan.microservicebase.core.Const;
import com.freemanan.microservicebase.core.tracing.HeaderTransferor;
import com.freemanan.microservicebase.grpc.GrpcProperties;
import com.freemanan.microservicebase.grpc.client.GrpcClientBeanPostProcessor;
import com.freemanan.microservicebase.grpc.client.GrpcTracingClientInterceptor;
import com.freemanan.microservicebase.grpc.server.GrpcServer;
import com.freemanan.microservicebase.grpc.server.GrpcServerStarter;
import com.freemanan.microservicebase.grpc.server.GrpcTracingServerInterceptor;
import com.freemanan.microservicebase.grpc.server.exception.advice.GrpcAdviceDiscoverer;
import com.freemanan.microservicebase.grpc.server.exception.advice.GrpcAdviceExceptionHandler;
import com.freemanan.microservicebase.grpc.server.exception.advice.GrpcExceptionHandlerMethodResolver;
import com.freemanan.microservicebase.grpc.server.exception.error.GrpcExceptionResponseHandler;
import com.freemanan.microservicebase.grpc.server.exception.error.GrpcExceptionServerInterceptor;
import com.freemanan.microservicebase.http.HttpProperties;
import com.freemanan.microservicebase.http.client.feign.TracingInterceptor;
import com.freemanan.microservicebase.http.client.resttemplate.RestTracingInterceptor;
import com.freemanan.microservicebase.http.server.mvc.HttpMvcTracingFilter;
import com.freemanan.microservicebase.http.server.mvc.LoggingInterceptor;
import feign.RequestInterceptor;
import io.envoyproxy.pgv.ReflectiveValidatorIndex;
import io.envoyproxy.pgv.grpc.ValidatingClientInterceptor;
import io.envoyproxy.pgv.grpc.ValidatingServerInterceptor;
import io.grpc.BindableService;
import io.grpc.ClientInterceptor;
import io.grpc.Grpc;
import io.grpc.ServerInterceptor;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class MicroserviceBaseAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(MicroserviceBaseAutoConfiguration.class);

    @Bean
    public App app() {
        return new App();
    }

    @Bean
    public HeaderTransferor microserviceBaseDefaultHeaderTransferor() {
        return () -> Arrays.asList(Const.HEADER_REQUEST_ID, Const.HEADER_FROM_APP);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = HttpProperties.PREFIX, name = "enabled", matchIfMissing = true)
    @EnableConfigurationProperties(HttpProperties.class)
    public static class HttpConfiguration {

        @Configuration(proxyBeanMethods = false)
        @ConditionalOnClass(HandlerInterceptor.class)
        static class ServerMvcConfiguration {
            @Bean
            public HttpMvcTracingFilter httpMvcTracingFilter(List<HeaderTransferor> headerTransferors) {
                return new HttpMvcTracingFilter(headerTransferors);
            }

            @Configuration(proxyBeanMethods = false)
            @ConditionalOnProperty(prefix = HttpProperties.PREFIX + ".logging", name = "enabled", matchIfMissing = true)
            static class LoggingConfiguration implements WebMvcConfigurer {
                private final HttpProperties httpProperties;

                public LoggingConfiguration(HttpProperties httpProperties) {
                    this.httpProperties = httpProperties;
                }

                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    registry.addInterceptor(new LoggingInterceptor())
                            .addPathPatterns(httpProperties.getLogging().getIncludePatterns())
                            .excludePathPatterns(httpProperties.getLogging().getExcludePatterns());
                }
            }
        }

        @Configuration(proxyBeanMethods = false)
        @ConditionalOnClass({FeignClientFactoryBean.class})
        static class ClientFeignConfiguration {
            @Bean
            public RequestInterceptor feignTracingInterceptor() {
                return new TracingInterceptor();
            }
        }

        @Configuration(proxyBeanMethods = false)
        @ConditionalOnClass({RestTemplate.class})
        static class ClientRestTemplateConfiguration {
            @Bean
            public ClientHttpRequestInterceptor restTemplateTracingInterceptor() {
                return new RestTracingInterceptor();
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(Grpc.class)
    @ConditionalOnProperty(prefix = GrpcProperties.PREFIX, name = "enabled", matchIfMissing = true)
    @EnableConfigurationProperties(GrpcProperties.class)
    public static class GrpcConfiguration {

        @Configuration(proxyBeanMethods = false)
        @ConditionalOnProperty(prefix = GrpcProperties.PREFIX + ".client", name = "enabled", matchIfMissing = true)
        static class GrpcClientConfiguration {
            @Bean
            static GrpcClientBeanPostProcessor grpcClientBeanPostProcessor() {
                return new GrpcClientBeanPostProcessor();
            }

            @Bean
            @Order(Ordered.HIGHEST_PRECEDENCE + 100)
            @ConditionalOnClass(ValidatingClientInterceptor.class)
            @ConditionalOnProperty(
                    prefix = GrpcProperties.PREFIX + ".client",
                    name = "validation-enabled",
                    matchIfMissing = true)
            public ClientInterceptor validatingClientInterceptor() {
                return new ValidatingClientInterceptor(new ReflectiveValidatorIndex());
            }

            @Bean
            public ClientInterceptor grpcTracingClientInterceptor() {
                return new GrpcTracingClientInterceptor();
            }
        }

        @Configuration(proxyBeanMethods = false)
        @ConditionalOnProperty(prefix = GrpcProperties.PREFIX + ".server", name = "enabled", matchIfMissing = true)
        static class GrpcServerConfiguration {

            @Configuration(proxyBeanMethods = false)
            @ConditionalOnProperty(
                    prefix = GrpcProperties.PREFIX + ".server",
                    name = "exception-handler-enabled",
                    matchIfMissing = true)
            static class ExceptionHandlerConfiguration {
                @Bean
                public GrpcAdviceDiscoverer grpcAdviceDiscoverer() {
                    return new GrpcAdviceDiscoverer();
                }

                @Bean
                public GrpcExceptionHandlerMethodResolver grpcExceptionHandlerMethodResolver(
                        GrpcAdviceDiscoverer grpcAdviceDiscoverer) {
                    return new GrpcExceptionHandlerMethodResolver(grpcAdviceDiscoverer);
                }

                @Bean
                public GrpcAdviceExceptionHandler grpcAdviceExceptionHandler(
                        GrpcExceptionHandlerMethodResolver grpcExceptionHandlerMethodResolver) {
                    return new GrpcAdviceExceptionHandler(grpcExceptionHandlerMethodResolver);
                }

                @Bean
                @Order(Ordered.HIGHEST_PRECEDENCE + 200)
                public ServerInterceptor grpcExceptionServerInterceptor(
                        GrpcExceptionResponseHandler grpcExceptionResponseHandler) {
                    return new GrpcExceptionServerInterceptor(grpcExceptionResponseHandler);
                }
            }

            @Bean
            @ConditionalOnProperty(value = GrpcProperties.PREFIX + ".server.debug-enabled")
            public BindableService reflectionBindableService() {
                log.info("gRPC server is in debug mode");
                return ProtoReflectionService.newInstance();
            }

            @Bean
            public GrpcServerStarter grpcServerStarter(List<BindableService> bindableServices) {
                return new GrpcServerStarter(bindableServices);
            }

            @Bean
            @Order(Ordered.HIGHEST_PRECEDENCE + 100)
            @ConditionalOnClass(ValidatingServerInterceptor.class)
            @ConditionalOnProperty(
                    prefix = GrpcProperties.PREFIX + ".server",
                    name = "validation-enabled",
                    matchIfMissing = true)
            public ServerInterceptor validatingServerInterceptor() {
                return new ValidatingServerInterceptor(new ReflectiveValidatorIndex());
            }

            @Bean
            public ServerInterceptor grpcTracingServerInterceptor(List<HeaderTransferor> headerTransferors) {
                return new GrpcTracingServerInterceptor(headerTransferors);
            }

            @Bean
            public GrpcServer grpcServer(
                    List<ServerInterceptor> serverInterceptors,
                    GrpcProperties grpcProperties,
                    GrpcServerStarter grpcServerStarter) {
                return new GrpcServer(serverInterceptors, grpcProperties, grpcServerStarter);
            }
        }
    }
}
