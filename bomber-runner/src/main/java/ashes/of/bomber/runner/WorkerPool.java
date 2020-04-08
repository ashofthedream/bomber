package ashes.of.bomber.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerPool {
    private static final Logger log = LogManager.getLogger();

    private final AtomicInteger idSeq = new AtomicInteger();

    private final List<Worker> workers = new CopyOnWriteArrayList<>();
    private final BlockingQueue<Worker> available = new LinkedBlockingQueue<>();
    private final Set<Worker> acquired = new CopyOnWriteArraySet<>();

    public WorkerPool(int count) {
        for (int i = 0; i < count; i++)
            available.offer(createWorker());
    }

    public WorkerPool() {
        this(0);
    }

    private static void uncaughtExceptionHandler(Thread t, Throwable e) {
        if (e instanceof ThreadDeath)
            return;
        log.error("Uncaught exception in thread: {}", t.getName(), e);
    }


    private Worker createWorker() {
        BlockingQueue<Runnable> queue = new SynchronousQueue<>();
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Runnable task = queue.take();

                    task.run();
                } catch (Exception e) {
                    log.warn("task failed", e);
                }
            }
        });

        thread.setUncaughtExceptionHandler(WorkerPool::uncaughtExceptionHandler);
        thread.setName(String.format("bomber-worker-%04d", idSeq.getAndIncrement()));
        thread.start();

        Worker worker = new Worker(queue, thread);

        workers.add(worker);
        log.debug("created new worker: {}", worker.getName());
        return worker;
    }

    public Worker acquire() {
        Worker available = this.available.poll();
        Worker worker = available != null ? available : createWorker();

        log.debug("acquire {}", worker.getName());
        acquired.add(worker);

        return worker;
    }

    public void release(Worker worker) {
        log.debug("release {}", worker.getName());
        available.add(worker);
        acquired.remove(worker);
    }

    public void release(Collection<Worker> workers) {
        workers.forEach(this::release);
    }

    public void shutdown() {
        log.info("Shutdown WorkerPool, {} workers ({} available) will receive a stop signal", workers.size(), available.size());
        available.clear();
        workers.forEach(Worker::stop);
        workers.clear();
    }

    public Set<Worker> getAcquired() {
        return acquired;
    }
}
