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
        // a bit of busy-wait here, todo investigate
        for (int i = 0; i < 10000; i++) {
            boolean success = queue.offer(task);
            if (success)
                return;
        }

        throw new RuntimeException("Hey, you can't run task on " + thread.getName() + ". It's terrible situation and should be fixed");
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
