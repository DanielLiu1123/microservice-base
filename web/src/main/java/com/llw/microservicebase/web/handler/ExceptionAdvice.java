package com.llw.microservicebase.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * @author Freeman
 * @since 1.0.0
 */
@RestControllerAdvice
public class ExceptionAdvice {
    private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

    /**
     * 异常统一处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> freemanWebExceptionHandler(Exception e) {
        // e.getCause() == null 说明 e 就是 cause
        String cause = e.getCause() == null ? e.toString() : e.getCause().toString();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(cause);
    }

    /**
     * 最大只抓到 RuntimeException
     *
     * @param e e
     * @return {@link ResponseEntity}
     */
//    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> runtimeException(RuntimeException e) {
        String cause = e.getCause() == null ? e.toString() : e.getCause().toString();
        log.warn("{}", e.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cause);
    }

}
