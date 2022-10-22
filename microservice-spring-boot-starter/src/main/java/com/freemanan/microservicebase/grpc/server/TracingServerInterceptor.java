package com.freemanan.microservicebase.grpc.server;

import com.freemanan.microservicebase.core.thread.ThreadContext;
import com.freemanan.microservicebase.core.thread.ThreadContextHolder;
import com.freemanan.microservicebase.core.tracing.Header;
import com.freemanan.microservicebase.core.tracing.HeaderTransferor;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Freeman
 * @since 1.0.0
 */
public class TracingServerInterceptor implements ServerInterceptor {

    private final List<HeaderTransferor> headerTransferors;

    public TracingServerInterceptor(List<HeaderTransferor> headerTransferors) {
        this.headerTransferors = headerTransferors;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata metadata, ServerCallHandler<ReqT, RespT> next) {
        List<Header> headers = new ArrayList<>();
        for (HeaderTransferor provider : headerTransferors) {
            for (String key : provider.keys()) {
                String value = metadata.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
                if (value != null) {
                    headers.add(Header.of(key, value));
                }
            }
        }
        ThreadContext threadContext = ThreadContext.builder().headers(headers).build();
        ThreadContextHolder.set(threadContext);
        try {
            return new MetadataServerCallListener<>(next.startCall(call, metadata), threadContext);
        } finally {
            ThreadContextHolder.remove();
        }
    }

    static class MetadataServerCallListener<Req>
            extends ForwardingServerCallListener.SimpleForwardingServerCallListener<Req> {

        private final ThreadContext threadContext;

        public MetadataServerCallListener(ServerCall.Listener<Req> delegate, ThreadContext threadContext) {
            super(delegate);
            this.threadContext = threadContext;
        }

        @Override
        public void onMessage(Req message) {
            doInContext(() -> super.onMessage(message));
        }

        @Override
        public void onHalfClose() {
            doInContext(super::onHalfClose);
        }

        private void doInContext(Runnable runnable) {
            ThreadContextHolder.set(threadContext);
            try {
                runnable.run();
            } finally {
                ThreadContextHolder.remove();
            }
        }
    }
}
