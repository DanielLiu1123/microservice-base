package com.llw.microservicebase.common.thread;

/**
 * @author Freeman
 * @since 1.0.0
 */
public class ThreadContextHolder {
    private static final ThreadLocal<ThreadContext> ctx = new ThreadLocal<>();

    public static ThreadContext get() {
        return ctx.get();
    }

    public static void set(ThreadContext threadContext) {
        ctx.set(threadContext);
    }

    public static void remove() {
        ctx.remove();
    }
}
