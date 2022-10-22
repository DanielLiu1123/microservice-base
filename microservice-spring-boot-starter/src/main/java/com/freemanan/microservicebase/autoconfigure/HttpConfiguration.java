package com.freemanan.microservicebase.autoconfigure;

import com.freemanan.microservicebase.autoconfigure.condition.ConditionalOnHttpEnabled;
import com.freemanan.microservicebase.http.HttpProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnHttpEnabled
@EnableConfigurationProperties(HttpProperties.class)
class HttpConfiguration {}
