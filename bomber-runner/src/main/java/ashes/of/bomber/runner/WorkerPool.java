package ashes.of.bomber.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerPool {
    private static final Logger log = LogManager.getLogger();

    private final AtomicInteger idSeq = new AtomicInteger();

    private final List<Worker> workers = new CopyOnWriteArrayList<>();
    private final BlockingQueue<Worker> available = new LinkedBlockingQueue<>();

    public WorkerPool(int count) {
        for (int i = 0; i < count; i++)
            available.offer(createWorker());
    }

    public WorkerPool() {
        this(1);
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
        Worker worker = available.poll();
        Worker accuired = worker != null ? worker : createWorker();

        log.debug("acquire {}", accuired.getName());
        return accuired;
    }

    public void release(Worker worker) {
        log.debug("release {}", worker.getName());
        available.add(worker);
    }

    public void release(List<Worker> workers) {
        workers.forEach(this::release);
    }

    public void shutdown() {
        available.clear();
        workers.forEach(Worker::stop);
        workers.clear();
    }
}
