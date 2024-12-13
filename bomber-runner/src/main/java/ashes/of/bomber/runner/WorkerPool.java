package ashes.of.bomber.runner;

import ashes.of.bomber.events.EventMachine;
import ashes.of.bomber.threads.BomberThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;


public class WorkerPool implements Executor {
    private static final Logger log = LogManager.getLogger();

    private final EventMachine em;
    private final List<Worker> workers = new CopyOnWriteArrayList<>();

    public WorkerPool(EventMachine em, int count) {
        this.em = em;
        for (int i = 0; i < count; i++)
            createWorker();
    }

    private static void uncaughtExceptionHandler(Thread t, Throwable e) {
        if (e instanceof ThreadDeath)
            return;
        log.error("Uncaught exception in thread: {}", t.getName(), e);
    }


    private Worker createWorker() {
        var queue = new SynchronousQueue<Runnable>();
        var thread = BomberThreadFactory.WORKER_FACTORY.newThread(() -> {
            while (true) {
                try {
                    var task = queue.take();

                    task.run();
                } catch (Exception e) {
                    log.warn("task failed", e);
                }
            }
        });

        thread.setUncaughtExceptionHandler(WorkerPool::uncaughtExceptionHandler);
        thread.start();

        var worker = new Worker(em, queue, thread);

        workers.add(worker);
        log.debug("Created new worker: {}", worker.getName());
        return worker;
    }

    public void shutdown() {
        log.info("Shutdown WorkerPool, {} workers will receive a stop signal", workers.size());

        workers.forEach(Worker::stop);
        workers.clear();
    }


    public List<Worker> getWorkers() {
        return workers;
    }

    @Override
    public void execute(Runnable command) {

    }
}
