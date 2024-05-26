package com.vulkantechnologies.pnet.threading;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class ErrataThreadFactory implements ThreadFactory {

    private static final String PREFIX = "errata-";
    private static final AtomicInteger FACTORY_NUMBER = new AtomicInteger(1);
    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);

    private final String prefix;
    private final Thread.UncaughtExceptionHandler handler;

    public ErrataThreadFactory() {
        this.prefix = PREFIX + FACTORY_NUMBER.getAndIncrement() + "-";

        this.handler = (th, ex) -> {
            System.out.println("Uncaught exception: " + ex);
            ex.printStackTrace();
        };
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, this.prefix + THREAD_NUMBER.getAndIncrement());
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(this.handler);
        return thread;
    }
}
