package org.phial.baas.manager.config.app;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An standard thread factory
 *
 * @author mayanjun
 * @since 25/10/2017
 */
public class StandardThreadFactory implements ThreadFactory {

    private static final String DEFAULT_PREFIX = "WORK";
    private static final AtomicInteger poolNumber = new AtomicInteger(0);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(0);
    private final String namePrefix;
    private boolean daemon = false;

    public StandardThreadFactory() {
        this(DEFAULT_PREFIX);
    }

    public StandardThreadFactory(boolean daemon) {
        this(DEFAULT_PREFIX, daemon);
    }

    public StandardThreadFactory(String prefix) {
        this(prefix, false);
    }

    public StandardThreadFactory(String prefix, boolean daemon) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = prefix + poolNumber.getAndIncrement();
        this.daemon = daemon;
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        t.setDaemon(daemon);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}