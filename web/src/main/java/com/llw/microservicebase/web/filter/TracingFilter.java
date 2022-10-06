package com.llw.microservicebase.web.filter;

import com.llw.microservicebase.common.Const;
import com.llw.microservicebase.common.thread.ThreadContext;
import com.llw.microservicebase.common.thread.ThreadContextHolder;
import com.llw.microservicebase.common.tracing.TracingContext;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Freeman
 * @since 1.0.0
 */
public class TracingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        TracingContext ctx = TracingContext.builder()
                .requestId(request.getHeader(Const.HEADER_REQUEST_ID))
                .internalOrigin(request.getHeader(Const.HEADER_INTERNAL_ORIGIN))
                .build();
        ThreadContext threadContext = ThreadContext.builder()
                .tracingContext(ctx)
                .build();
        ThreadContextHolder.set(threadContext);
        try {
            filterChain.doFilter(request, response);
        } finally {
            ThreadContextHolder.remove();
        }
    }
}
