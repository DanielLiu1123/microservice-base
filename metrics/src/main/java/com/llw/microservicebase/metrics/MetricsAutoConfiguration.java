package com.llw.microservicebase.metrics;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * @author Freeman
 * @since 1.0.0
 */
@AutoConfiguration
public class MetricsAutoConfiguration implements SmartInitializingSingleton {

    @Override
    public void afterSingletonsInstantiated() {

    }
}
