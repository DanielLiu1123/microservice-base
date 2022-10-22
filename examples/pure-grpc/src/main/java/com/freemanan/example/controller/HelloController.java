package com.freemanan.example.controller;

import com.freemanan.example.hello.api.HelloGrpc;
import com.freemanan.example.hello.api.HelloRequest;
import com.freemanan.example.hello.api.HelloResponse;
import com.freemanan.microservicebase.grpc.client.GrpcClient;
import com.freemanan.microservicebase.grpc.server.GrpcService;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

/**
 * @author Freeman
 * @since 2022/10/7
 */
@GrpcService
public class HelloController extends HelloGrpc.HelloImplBase {

    @GrpcClient("hello")
    private HelloGrpc.HelloBlockingStub helloBlockingStub;

    @GrpcClient("hello")
    private HelloGrpc.HelloFutureStub helloFutureStub;

    @GrpcClient("hello")
    private HelloGrpc.HelloStub helloStub;

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        System.out.println("request: " + request.getName());
        HelloResponse response = HelloResponse.newBuilder()
                .setMessage("Hello " + request.getName())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void greet(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        HelloResponse response = helloBlockingStub.sayHello(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void exception(Empty request, StreamObserver<Empty> responseObserver) {
        throw new RuntimeException("Oops!");
    }
}
