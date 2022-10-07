package com.freemanan.microservicebase.grpc.server;

import io.grpc.Server;
import org.springframework.context.ApplicationEvent;

/**
 * Grpc server started event.
 * <p> Can use to get the random server port.
 *
 * @author Freeman
 * @since 1.0.0
 */
public class GrpcServerStartedEvent extends ApplicationEvent {

    public GrpcServerStartedEvent(Server source) {
        super(source);
    }

    @Override
    public Server getSource() {
        return ((Server) super.getSource());
    }
}
