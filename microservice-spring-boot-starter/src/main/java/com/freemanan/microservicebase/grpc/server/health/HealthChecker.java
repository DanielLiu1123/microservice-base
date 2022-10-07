package com.freemanan.microservicebase.grpc.server.health;

import static io.grpc.health.v1.HealthCheckResponse.ServingStatus.SERVING;

import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;

/**
 * Grpc health check basic implementation.
 *
 * @author Freeman
 * @since 1.0.0
 */
@Controller
public class HealthChecker extends HealthGrpc.HealthImplBase {

    private final List<HealthDetector> healthDetectors;

    public HealthChecker(List<HealthDetector> healthDetectors) {
        this.healthDetectors = Optional.ofNullable(healthDetectors).orElse(new ArrayList<>());
    }

    @Override
    public void check(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
        HealthCheckResponse.ServingStatus result = SERVING;
        for (HealthDetector detector : healthDetectors) {
            HealthCheckResponse.ServingStatus status = detector.check(request);
            if (status != SERVING) {
                // oops, we have a problem
                result = status;
                break;
            }
        }
        responseObserver.onNext(
                HealthCheckResponse.newBuilder().setStatus(result).build());
        responseObserver.onCompleted();
    }

    @Override
    public void watch(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
        // should not be called
        super.watch(request, responseObserver);
    }
}
