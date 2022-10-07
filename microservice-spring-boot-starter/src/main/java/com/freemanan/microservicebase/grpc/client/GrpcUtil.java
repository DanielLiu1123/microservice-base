package com.freemanan.microservicebase.grpc.client;

import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractFutureStub;
import io.grpc.stub.AbstractStub;

/**
 * @author Freeman
 * @since 1.0.0
 */
public final class GrpcUtil {

    public static String getNewStubMethodName(Class<? extends AbstractStub> clz) {
        if (AbstractBlockingStub.class.isAssignableFrom(clz)) {
            return "newBlockingStub";
        }
        if (AbstractFutureStub.class.isAssignableFrom(clz)) {
            return "newFutureStub";
        }
        if (AbstractAsyncStub.class.isAssignableFrom(clz)) {
            return "newStub";
        }
        throw new IllegalArgumentException("Unsupported stub class: " + clz);
    }

    public static String determineRealClientName(String name, Class<? extends AbstractStub> stub) {
        if (AbstractBlockingStub.class.isAssignableFrom(stub)) {
            return name + "BlockingStub";
        }
        if (AbstractFutureStub.class.isAssignableFrom(stub)) {
            return name + "FutureStub";
        }
        if (AbstractAsyncStub.class.isAssignableFrom(stub)) {
            return name + "AsyncStub";
        }
        throw new IllegalArgumentException("Unsupported stub type: " + stub);
    }
}
