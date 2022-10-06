package com.llw.microservicebase.common.tracing;

import com.llw.microservicebase.common.thread.ThreadContext;
import com.llw.microservicebase.common.thread.ThreadContextHolder;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Getter
@Builder
public class TracingContext {
    private String requestId;
    private String internalOrigin;

    public static TracingContext get() {
        ThreadContext threadContext = ThreadContextHolder.get();
        if (threadContext == null) {
            throw new IllegalStateException("Current thread '"+Thread.currentThread().getName()+"' not bind a ThreadContext!");
        }
        TracingContext tracingContext = threadContext.getTracingContext();
        if (tracingContext == null) {
            throw new IllegalStateException("Current thread '"+Thread.currentThread().getName()+"' bind a null TracingContext!");
        }
        return tracingContext;
    }
}
