package com.freemanan.microservicebase.http.server.mvc;

import com.freemanan.microservicebase.core.thread.ThreadContext;
import com.freemanan.microservicebase.core.thread.ThreadContextHolder;
import com.freemanan.microservicebase.core.tracing.Header;
import com.freemanan.microservicebase.core.tracing.HeaderTransferor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author Freeman
 * @since 1.0.0
 */
public class HttpMvcTracingFilter extends OncePerRequestFilter {

    private final List<HeaderTransferor> headerTransferors;

    public HttpMvcTracingFilter(List<HeaderTransferor> headerTransferors) {
        this.headerTransferors = headerTransferors;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        List<Header> headers = new ArrayList<>();
        for (HeaderTransferor provider : headerTransferors) {
            for (String key : provider.keys()) {
                String value = request.getHeader(key);
                if (value != null) {
                    headers.add(Header.of(key, value));
                }
            }
        }

        ThreadContext threadContext = ThreadContext.builder().headers(headers).build();
        ThreadContextHolder.set(threadContext);
        try {
            filterChain.doFilter(request, response);
        } finally {
            ThreadContextHolder.remove();
        }
    }
}
