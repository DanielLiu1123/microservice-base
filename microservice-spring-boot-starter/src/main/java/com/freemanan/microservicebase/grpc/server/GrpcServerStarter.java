package com.freemanan.microservicebase.grpc.server;

import com.freemanan.microservicebase.grpc.server.health.HealthChecker;
import io.grpc.BindableService;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.util.Arrays;
import java.util.List;

/**
 * Determine if we need to start the servers (gRPC, Metrics).
 *
 * @author Freeman
 * @since 1.0.0
 */
public class GrpcServerStarter {

    private static final List<Class<? extends BindableService>> omitServices =
            Arrays.asList(ProtoReflectionService.class, HealthChecker.class);

    private final boolean needStartup;
    private final List<BindableService> services;

    public GrpcServerStarter(List<BindableService> services) {
        this.services = services;
        this.needStartup = services.stream()
                .anyMatch(service -> omitServices.stream().noneMatch(svc -> svc.isAssignableFrom(service.getClass())));
    }

    public boolean isNeedStartup() {
        return needStartup;
    }

    public List<BindableService> getServices() {
        return services;
    }
}
