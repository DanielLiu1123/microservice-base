package com.freemanan.microservicebase.grpc.client;

import com.freemanan.microservicebase.grpc.GrpcProperties;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author Freeman
 * @since 1.0.0
 */
public class GrpcClientBeanPostProcessor
        implements BeanPostProcessor,
                EnvironmentAware,
                BeanFactoryAware,
                BeanDefinitionRegistryPostProcessor,
                PriorityOrdered {

    private BeanFactory beanFactory;
    private final Map<String, String> channels = new HashMap<>();

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            GrpcClient anno = field.getAnnotation(GrpcClient.class);
            if (anno == null) {
                return;
            }
            if (!AbstractStub.class.isAssignableFrom(field.getType())) {
                throw new BeanInitializationException("GrpcClient field must be a subclass of AbstractStub!");
            }
            Class<? extends AbstractStub> clz = (Class<? extends AbstractStub>) field.getType();
            String name = anno.value();
            AbstractStub<?> stub = GrpcClientBeanHolder.get(name, clz);
            if (stub != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, bean, stub);
                return;
            }
            createStubAndPut2BeanFactory(name, clz, field, bean);
        });
        return bean;
    }

    @SuppressWarnings("rawtypes")
    private void createStubAndPut2BeanFactory(
            String clientName, Class<? extends AbstractStub> clz, Field field, Object bean) {
        if (!channels.containsKey(clientName)) {
            throw new BeanInitializationException("No channel found for GrpcClient: " + clientName);
        }

        ManagedChannel channel = ManagedChannelBuilder.forTarget(channels.get(clientName))
                .intercept(beanFactory.getBean(CompositeClientInterceptor.class))
                .usePlaintext()
                .build();

        Method newStubMethod =
                ReflectionUtils.findMethod(clz.getEnclosingClass(), GrpcUtil.getNewStubMethodName(clz), Channel.class);
        if (newStubMethod == null) {
            throw new BeanInitializationException("No new stub method found for GrpcClient: " + clientName);
        }
        AbstractStub<?> grpcStub = (AbstractStub<?>) ReflectionUtils.invokeMethod(newStubMethod, null, channel);
        if (grpcStub == null) {
            throw new BeanInitializationException("Failed to create stub for GrpcClient: " + clientName);
        }
        field.setAccessible(true);
        ReflectionUtils.setField(field, bean, grpcStub);
        GrpcClientBeanHolder.put(clientName, grpcStub);
        ((ConfigurableBeanFactory) beanFactory)
                .registerSingleton(GrpcUtil.determineRealClientName(clientName, grpcStub.getClass()), grpcStub);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setEnvironment(Environment environment) {
        Binder binder = Binder.get(environment);
        Boolean grpcClientEnabled = binder.bind(GrpcProperties.PREFIX + ".client.enabled", Boolean.class)
                .orElseGet(() -> true);
        if (!grpcClientEnabled) {
            return;
        }
        HashMap<String, String> properties =
                binder.bindOrCreate(GrpcProperties.PREFIX + ".client.channels", HashMap.class);
        properties.forEach((clientName, address) -> {
            if (!StringUtils.hasText(address)) {
                throw new IllegalStateException("GrpcClient address must not be empty!");
            }
            channels.put(clientName, address);
        });
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {}

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
                CompositeClientInterceptor.class, CompositeClientInterceptor::new);
        registry.registerBeanDefinition(
                StringUtils.uncapitalize(CompositeClientInterceptor.class.getSimpleName()),
                builder.getBeanDefinition());
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
