package ashes.of.bomber.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class BomberThreadFactory implements ThreadFactory {
    private static final Logger log = LogManager.getLogger();

    public static final ThreadFactory WATCHER_FACTORY = new BomberThreadFactory("watcher");
    public static final ThreadFactory WORKER_FACTORY = new BomberThreadFactory("worker");
    public static final ThreadFactory SINK_FACTORY = new BomberThreadFactory("sink");

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
}
