package com.freemanan.microservicebase.autoconfigure;

import com.freemanan.microservicebase.core.Const;
import com.freemanan.microservicebase.core.tracing.HeaderTransferor;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Import({GrpcConfiguration.class, HttpConfiguration.class})
public class MicroserviceBaseAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @Import({LoggingConfiguration.class, TracingConfiguration.class, ValidateConfiguration.class})
    static class FunctionConfiguration {}

    @Bean
    public HeaderTransferor microserviceBaseDefaultHeaderTransferor() {
        return () -> Arrays.asList(Const.HEADER_REQUEST_ID, Const.HEADER_FROM_APP);
    }
}
