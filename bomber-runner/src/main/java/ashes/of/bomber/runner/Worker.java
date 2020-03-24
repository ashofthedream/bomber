package ashes.of.bomber.runner;

import java.util.concurrent.BlockingQueue;

public class Worker {
    private final BlockingQueue<Runnable> queue;
    private final Thread thread;

    public Worker(BlockingQueue<Runnable> queue, Thread thread) {
        this.queue = queue;
        this.thread = thread;
    }

    public void run(Runnable task) {
        boolean success = queue.offer(task);
        if (!success)
            throw new RuntimeException("Hey, you can't run task on this worker. It's terrible situation and should be fixed");
    }

    public String getName() {
        return thread.getName();
    }

    public boolean isActive() {
        return thread.isAlive();
    }

    public void stop() {
        thread.stop();
    }
}
