package com.freemanan.microservicebase.grpc.client;

import com.freemanan.microservicebase.core.thread.ThreadContextHolder;
import com.freemanan.microservicebase.core.tracing.Header;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import java.util.List;

/**
 * @author Freeman
 * @since 1.0.0
 */
public class TracingClientInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata metadata) {
                List<Header> headers = ThreadContextHolder.get().getHeaders();
                for (Header header : headers) {
                    Metadata.Key<String> key = Metadata.Key.of(header.getKey(), Metadata.ASCII_STRING_MARSHALLER);
                    if (!metadata.containsKey(key)) {
                        metadata.put(key, header.getValue());
                    }
                }
                super.start(responseListener, metadata);
            }
        };
    }
}
