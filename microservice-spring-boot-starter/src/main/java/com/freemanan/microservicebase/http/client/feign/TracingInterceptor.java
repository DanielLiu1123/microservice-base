package com.freemanan.microservicebase.http.client.feign;

import com.freemanan.microservicebase.core.thread.ThreadContext;
import com.freemanan.microservicebase.core.thread.ThreadContextHolder;
import com.freemanan.microservicebase.core.tracing.Header;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author Freeman
 * @since 1.0.0
 */
public class TracingInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ThreadContext ctx = ThreadContextHolder.get();
        for (Header header : ctx.getHeaders()) {
            if (!template.headers().containsKey(header.getKey())) {
                template.header(header.getKey(), header.getValue());
            }
        }
    }
}
