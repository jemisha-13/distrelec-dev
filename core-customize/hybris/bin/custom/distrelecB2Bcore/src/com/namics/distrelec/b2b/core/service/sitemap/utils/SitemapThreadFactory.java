/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code DefaultThreadFactory}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.6
 */
public class SitemapThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final int threadPriority;

    /**
     * Create a new instance of {@code DefaultThreadFactory}
     *
     * @param namePrefix
     * @param threadPriority
     */
    public SitemapThreadFactory(final String namePrefix, final int threadPriority) {
        final SecurityManager secManager = System.getSecurityManager();
        group = (secManager == null) ? Thread.currentThread().getThreadGroup() : secManager.getThreadGroup(); // NOPMD
        this.namePrefix = namePrefix;
        this.threadPriority = threadPriority;
    }

    /**
     * Create a new instance of {@code DefaultThreadFactory}
     *
     * @param threadPriority
     */
    public SitemapThreadFactory(final int threadPriority) {
        this("pool-" + POOL_NUMBER.getAndIncrement() + "-thread-", threadPriority);
    }

    /** {@inheritDoc} */
    @Override
    public Thread newThread(final Runnable target) {
        final Thread thread = new Thread(group, target, namePrefix + threadNumber.getAndIncrement(), 0);
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }

        if (thread.getPriority() != this.threadPriority) {
            thread.setPriority(this.threadPriority);
        }
        return thread;
    }

    /**
     * Constructs a new {@code Thread}. Implementations may also initialise priority, name, daemon status, {@code ThreadGroup}, etc.
     *
     * @param target
     *            a runnable to be executed by new thread instance
     * @param threadName
     *            the thread name
     * @return constructed thread, or {@code null} if the request to create a thread is rejected
     * @see #newThread(Runnable)
     */
    public Thread newThread(final Runnable target, final String threadName) {
        final Thread thread = newThread(target);
        if (threadName != null && !threadName.trim().isEmpty()) {
            thread.setName(namePrefix + threadName.trim());
        }
        return thread;
    }
}
