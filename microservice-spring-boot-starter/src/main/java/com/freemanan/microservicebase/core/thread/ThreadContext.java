package com.freemanan.microservicebase.core.thread;

import com.freemanan.microservicebase.core.tracing.Header;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Getter
@Builder
public class ThreadContext {
    private final List<Header> headers;
}
