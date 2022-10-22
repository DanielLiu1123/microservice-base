package com.freemanan.microservicebase.autoconfigure;

import com.freemanan.microservicebase.autoconfigure.condition.ConditionalOnGrpcClientEnabled;
import com.freemanan.microservicebase.autoconfigure.condition.ConditionalOnGrpcEnabled;
import com.freemanan.microservicebase.autoconfigure.condition.ConditionalOnGrpcServerEnabled;
import com.freemanan.microservicebase.grpc.GrpcProperties;
import com.freemanan.microservicebase.grpc.client.GrpcClientBeanPostProcessor;
import com.freemanan.microservicebase.grpc.server.GrpcServer;
import com.freemanan.microservicebase.grpc.server.GrpcServerStarter;
import com.freemanan.microservicebase.grpc.server.exception.advice.GrpcAdviceDiscoverer;
import com.freemanan.microservicebase.grpc.server.exception.advice.GrpcAdviceExceptionHandler;
import com.freemanan.microservicebase.grpc.server.exception.advice.GrpcExceptionHandlerMethodResolver;
import com.freemanan.microservicebase.grpc.server.exception.error.GrpcExceptionResponseHandler;
import com.freemanan.microservicebase.grpc.server.exception.error.GrpcExceptionServerInterceptor;
import io.grpc.BindableService;
import io.grpc.ServerInterceptor;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnGrpcEnabled
@EnableConfigurationProperties(GrpcProperties.class)
class GrpcConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnGrpcClientEnabled
    static class GrpcClientConfiguration {
        @Bean
        static GrpcClientBeanPostProcessor grpcClientBeanPostProcessor() {
            return new GrpcClientBeanPostProcessor();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnGrpcServerEnabled
    static class GrpcServerConfiguration {
        private static final Logger log = LoggerFactory.getLogger(GrpcServerConfiguration.class);

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
            public GrpcExceptionServerInterceptor grpcExceptionServerInterceptor(
                    GrpcExceptionResponseHandler grpcExceptionResponseHandler) {
                return new GrpcExceptionServerInterceptor(grpcExceptionResponseHandler);
            }
        }

        @Bean
        @ConditionalOnProperty(value = GrpcProperties.PREFIX + ".server.debug-enabled")
        public BindableService reflectionBindableService() {
            log.info("Started gRPC server in debug mode");
            return ProtoReflectionService.newInstance();
        }

        @Bean
        public GrpcServerStarter grpcServerStarter(List<BindableService> bindableServices) {
            return new GrpcServerStarter(bindableServices);
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
