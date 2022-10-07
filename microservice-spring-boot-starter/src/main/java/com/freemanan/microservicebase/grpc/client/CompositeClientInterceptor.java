package com.freemanan.microservicebase.grpc.client;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.MethodDescriptor;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;

/**
 * @author Freeman
 * @since 1.0.0
 */
public class CompositeClientInterceptor implements ClientInterceptor, BeanFactoryAware, SmartInitializingSingleton {

    private List<ClientInterceptor> interceptors;
    private BeanFactory beanFactory;

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return ClientInterceptors.intercept(next, interceptors).newCall(method, callOptions);
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.interceptors = beanFactory
                .getBeanProvider(ClientInterceptor.class)
                .orderedStream()
                .filter(interceptor -> interceptor != this)
                .collect(Collectors.toList());
        Collections.reverse(this.interceptors);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
