package com.freemanan.microservicebase.autoconfigure.condition;

/**
 * @author Freeman
 * @since 1.0.0
 */
import com.freemanan.microservicebase.http.HttpProperties;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author Freeman
 * @since 2022/10/22
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(prefix = HttpProperties.PREFIX + ".client", name = "enabled", matchIfMissing = true)
public @interface ConditionalOnHttpClientEnabled {}