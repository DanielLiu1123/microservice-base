package com.freemanan.microservicebase.http.client.resttemplate;

import com.freemanan.microservicebase.core.thread.ThreadContext;
import com.freemanan.microservicebase.core.thread.ThreadContextHolder;
import com.freemanan.microservicebase.core.tracing.Header;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * @author Freeman
 * @since 2022/10/7
 */
public class RestTracingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        ThreadContext ctx = ThreadContextHolder.get();
        HttpHeaders headers = request.getHeaders();
        for (Header header : ctx.getHeaders()) {
            if (!headers.containsKey(header.getKey())) {
                headers.add(header.getKey(), header.getValue());
            }
        }
        return execution.execute(request, body);
    }
}
