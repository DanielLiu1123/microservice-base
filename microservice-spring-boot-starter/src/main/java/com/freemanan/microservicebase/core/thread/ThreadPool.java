package com.freemanan.microservicebase.core.thread;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author Freeman
 * @since 1.0.0
 */
public class ThreadPool extends ThreadPoolExecutor {
    public ThreadPool(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public ThreadPool(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public ThreadPool(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public ThreadPool(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory,
            RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void execute(Runnable command) {
        super.execute(new TracingRunnable(command));
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return super.newTaskFor(new TracingRunnable(runnable), value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return super.newTaskFor(new TracingCallable<>(callable));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(new TracingRunnable(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(new TracingRunnable(task), result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(new TracingCallable<>(task));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return super.invokeAny(tasks.stream().map(TracingCallable::new).collect(Collectors.toList()));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return super.invokeAny(tasks.stream().map(TracingCallable::new).collect(Collectors.toList()), timeout, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return super.invokeAll(tasks.stream().map(TracingCallable::new).collect(Collectors.toList()));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        return super.invokeAll(tasks.stream().map(TracingCallable::new).collect(Collectors.toList()), timeout, unit);
    }

    static class TracingRunnable implements Runnable {
        private final Runnable delegate;
        private final ThreadContext threadContext;

        public TracingRunnable(Runnable delegate) {
            this.delegate = delegate;
            this.threadContext = ThreadContextHolder.get();
        }

        @Override
        public void run() {
            ThreadContextHolder.set(threadContext);
            try {
                delegate.run();
            } finally {
                ThreadContextHolder.remove();
            }
        }
    }

    static class TracingCallable<V> implements Callable<V> {
        private final Callable<V> delegate;
        private final ThreadContext threadContext;

        public TracingCallable(Callable<V> delegate) {
            this.delegate = delegate;
            this.threadContext = ThreadContextHolder.get();
        }

        @Override
        public V call() throws Exception {
            ThreadContextHolder.set(threadContext);
            try {
                return delegate.call();
            } finally {
                ThreadContextHolder.remove();
            }
        }
    }
}
