package com.freemanan.microservicebase.grpc.server;

import com.freemanan.microservicebase.grpc.GrpcProperties;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptor;
import io.grpc.internal.GrpcUtil;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.SmartLifecycle;

/**
 * Embedded gRPC server.
 *
 * @author Freeman
 * @since 1.0.0
 */
public class GrpcServer implements SmartLifecycle, ApplicationEventPublisherAware {

    private static final Logger log = LoggerFactory.getLogger(GrpcServer.class);

    private final Server server;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final CountDownLatch latch = new CountDownLatch(1);
    private final GrpcServerStarter serverStarter;

    private ApplicationEventPublisher publisher;

    public GrpcServer(
            List<ServerInterceptor> interceptors, GrpcProperties properties, GrpcServerStarter serverStarter) {
        this.serverStarter = serverStarter;
        int port = properties.getServer().getPort();
        ServerBuilder<?> builder = ServerBuilder.forPort(Math.max(port, 0));
        serverStarter.getServices().forEach(builder::addService);
        // grpc invoke interceptor in reverse order
        Collections.reverse(interceptors);
        interceptors.forEach(builder::intercept);
        // TODO(Freeman): support more configuration
        server = builder.build();
    }

    @Override
    public void start() {
        if (!serverStarter.isNeedStartup()) {
            log.info("no available gRPC service found, will not start a gRPC server!");
            return;
        }
        if (isRunning()) {
            return;
        }
        try {
            server.start();
            isRunning.set(true);
            log.info("gRPC server started on port: {} ({})", server.getPort(), GrpcUtil.getGrpcBuildVersion());

            publisher.publishEvent(new GrpcServerStartedEvent(server));
            waitUntilShutdown();
        } catch (IOException e) {
            isRunning.set(false);
            server.shutdownNow();
            try {
                server.awaitTermination();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException("Start gRPC server failed!", e);
        }
    }

    private void waitUntilShutdown() {
        new Thread(
                        () -> {
                            try {
                                // wait here until terminate
                                latch.await();
                                log.info("Grpc server stopped");
                                // TODO(Freeman): graceful shutdown?
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        "grpc-termination-awaiter")
                .start();
    }

    @Override
    public void stop() {
        if (isRunning.get()) {
            server.shutdown();
            // FIXME(Freeman): wait for server to stop
            isRunning.set(false);
            latch.countDown();
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
