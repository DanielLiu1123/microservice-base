package com.llw.microservicebase.common.autoconfigure;

import com.llw.microservicebase.common.App;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author Freeman
 * @since 1.0.0
 */
@AutoConfiguration
public class CommonAutoConfiguration {

    @Bean
    public App app() {
        return new App();
    }

}
