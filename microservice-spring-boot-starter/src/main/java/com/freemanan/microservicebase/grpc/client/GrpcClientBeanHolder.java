package com.freemanan.microservicebase.grpc.client;

import io.grpc.stub.AbstractStub;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Freeman
 * @since 1.0.0
 */
public class GrpcClientBeanHolder {
    private static final Map<String, AbstractStub<?>> stubs = new HashMap<>();

    public static void put(String name, AbstractStub stub) {
        stubs.put(GrpcUtil.determineRealClientName(name, stub.getClass()), stub);
    }

    public static AbstractStub<?> get(String name, Class<? extends AbstractStub> stubClass) {
        return stubs.get(GrpcUtil.determineRealClientName(name, stubClass));
    }
}
