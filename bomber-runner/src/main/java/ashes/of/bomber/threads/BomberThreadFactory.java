package ashes.of.bomber.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class BomberThreadFactory implements ThreadFactory {
    private static final Logger log = LogManager.getLogger();

    private static final ThreadFactory watcherFactory = new BomberThreadFactory("watcher");
    private static final ThreadFactory workerFactory = new BomberThreadFactory("worker");
    private static final ThreadFactory asyncSinkFactory = new BomberThreadFactory("async-sink");

    private final AtomicInteger threadNumberSeq = new AtomicInteger();
    private final String threadName;

    public BomberThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(String.format("bomber-%s-%04d", threadName, threadNumberSeq.incrementAndGet()));
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> log.warn("Uncaught exception in thread: {}", t.getName(), e));
        return thread;
    }

    public static ThreadFactory worker() {
        return workerFactory;
    }

    public static ThreadFactory watcher() {
        return watcherFactory;
    }

    public static ThreadFactory asyncSink() {
        return asyncSinkFactory;
    }
}
