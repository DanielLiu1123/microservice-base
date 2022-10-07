package com.freemanan.microservicebase.http.server.mvc;

import com.freemanan.microservicebase.core.Const;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author Freeman
 * @since 1.0.0
 */
public class LoggingInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final String START_TIME = "START_TIME";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        String fromApp =
                Optional.ofNullable(request.getHeader(Const.HEADER_FROM_APP)).orElse(Const.FROM_APP_UNKNOWN);
        String uri = request.getRequestURI();
        int status = response.getStatus();
        long consuming = System.currentTimeMillis() - (long) request.getAttribute(START_TIME);
        // TODO: method, params, request body ...
        if (ex == null) {
            log.info("from: {}, uri: {}, consuming: {}, status: {}", fromApp, uri, consuming, status);
        } else {
            log.warn(
                    "from: {}, uri: {}, consuming: {}, status: {}, cause: {}",
                    fromApp,
                    uri,
                    consuming,
                    status,
                    ex.getMessage());
        }
    }
}
