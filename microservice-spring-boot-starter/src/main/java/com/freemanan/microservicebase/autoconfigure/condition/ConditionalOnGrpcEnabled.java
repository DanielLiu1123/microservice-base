package com.freemanan.microservicebase.autoconfigure.condition;

import com.freemanan.microservicebase.grpc.GrpcProperties;
import io.grpc.Grpc;
import io.grpc.stub.AbstractStub;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@Conditional(HasGrpcAndGrpcEnabledCondition.class)
public @interface ConditionalOnGrpcEnabled {
}

class HasGrpcAndGrpcEnabledCondition extends AllNestedConditions {
    public HasGrpcAndGrpcEnabledCondition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnClass({Grpc.class, AbstractStub.class})
    static class HasGrpc {
    }

    @ConditionalOnProperty(prefix = GrpcProperties.PREFIX, name = "enabled", matchIfMissing = true)
    static class GrpcEnabled {
    }
}
