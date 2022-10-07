package com.freemanan.microservicebase.grpc.server.health;

import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse.ServingStatus;

/**
 * Health check detector.
 *
 * @author Freeman
 * @see HealthChecker
 * @since 2022/8/17
 */
public interface HealthDetector {
    /**
     * Check whether the service is healthy.
     *
     * @param request request
     * @return {@link ServingStatus#SERVING} if healthy, otherwise {@link ServingStatus#NOT_SERVING}
     */
    ServingStatus check(HealthCheckRequest request);
}
