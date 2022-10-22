package com.freemanan.microservicebase.autoconfigure.condition;

import com.freemanan.microservicebase.http.HttpProperties;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ServletApplicationAndHttpEnabledCondition.class)
public @interface ConditionalOnHttpEnabled {
}

class ServletApplicationAndHttpEnabledCondition extends AllNestedConditions {
    public ServletApplicationAndHttpEnabledCondition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    static class ServletWebApplication {
    }

    @ConditionalOnProperty(prefix = HttpProperties.PREFIX, name = "enabled", matchIfMissing = true)
    static class HttpEnabled {
    }
}
