package com.llw.microservicebase.common.thread;

import com.llw.microservicebase.common.tracing.TracingContext;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Getter
@Builder
public class ThreadContext {
    private final TracingContext tracingContext;
}
