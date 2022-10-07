package com.freemanan.example.advice;

import com.freemanan.microservicebase.grpc.server.exception.advice.GrpcAdvice;
import com.freemanan.microservicebase.grpc.server.exception.advice.GrpcExceptionHandler;
import io.grpc.Status;

/**
 * @author Freeman
 * @since 2022/10/7
 */
@GrpcAdvice
public class ExceptionHandler {

    @GrpcExceptionHandler(Exception.class)
    public Status handleException(Exception e) {
        return Status.fromThrowable(e).withDescription(e.getMessage());
    }
}
