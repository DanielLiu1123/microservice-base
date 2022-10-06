package com.llw.microservicebase.rpc.feign.interceptor;

import com.llw.microservicebase.common.App;
import com.llw.microservicebase.common.Const;
import com.llw.microservicebase.common.tracing.TracingContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Optional;

/**
 * @author Freeman
 * @since 1.0.0
 */
public class TracingInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        TracingContext ctx = TracingContext.get();
        template.header(Const.HEADER_REQUEST_ID, ctx.getRequestId());
        String internalOrigin = Optional.ofNullable(ctx.getInternalOrigin()).orElse(Const.INTERNAL_ORIGIN_UNKNOWN);
        internalOrigin = internalOrigin + "->" + App.getAppName();
        template.header(Const.HEADER_INTERNAL_ORIGIN, internalOrigin);
    }
}
