package ashes.of.bomber.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;

public class WorkerThread implements Executor, Runnable {
    private static final Logger log = LogManager.getLogger();

    private final SynchronousQueue<Runnable> queue = new SynchronousQueue<>() ;

    @Override
    public void run() {
        while (true) {
            try {
                var task = queue.take();

                task.run();
            } catch (Exception e) {
                log.warn("task failed", e);
            }
        }
    }

    @Override
    public void execute(Runnable command) {
        // a bit of busy-wait here, todo investigate
        for (int i = 0; i < 10000; i++) {
            boolean success = queue.offer(command);
            if (success)
                return;
        }

        throw new RuntimeException("Hey, you can't run task on %s. It's terrible situation and should be fixed".formatted(Thread.currentThread().getName()));
    }

    public void stop() {
    }
}
