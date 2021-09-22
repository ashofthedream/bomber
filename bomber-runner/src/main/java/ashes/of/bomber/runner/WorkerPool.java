package ashes.of.bomber.runner;

import ashes.of.bomber.threads.BomberThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.IntStream;


public class WorkerPool {
    private static final Logger log = LogManager.getLogger();

    private final List<Worker> workers = new CopyOnWriteArrayList<>();
    private final BlockingQueue<Worker> available = new LinkedBlockingQueue<>();
    private final BlockingQueue<Worker> acquired = new LinkedBlockingQueue<>();

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
        Thread thread = BomberThreadFactory.worker().newThread(() -> {
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
        thread.start();

        Worker worker = new Worker(queue, thread);

        workers.add(worker);
        log.debug("Created new worker: {}", worker.getName());
        return worker;
    }

    public Worker acquire() {
        Worker available = this.available.poll();
        Worker worker = available != null ? available : createWorker();

        log.debug("Acquire worker: {}", worker.getName());
        acquired.add(worker);

        return worker;
    }

    public List<Worker> acquire(int count) {
        log.debug("Acquire {} workers", count);
        List<Worker> workers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            workers.add(acquire());
        }

        return workers;
    }

    public void release(Worker worker) {
        log.debug("Release worker: {}", worker.getName());
        if (!acquired.remove(worker)) {
            log.warn("woker: {} not in acquired workers, looks like bug", worker.getName());
            return;
        }

        available.add(worker);
    }

    public void releaseAll() {
        log.debug("Release all {} workers", acquired.size());
        acquired.drainTo(available);
    }

    public void shutdown() {
        log.info("Shutdown WorkerPool, {} workers ({} available) will receive a stop signal", workers.size(), available.size());
        available.clear();
        if (acquired.isEmpty()) {
            log.warn("Some threads still acquired: {}. Doesn't matter", acquired);
        }

        workers.forEach(Worker::stop);
        workers.clear();
    }

    public Queue<Worker> getAcquired() {
        return acquired;
    }
}
