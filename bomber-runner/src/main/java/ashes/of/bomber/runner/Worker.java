package ashes.of.bomber.runner;

class Worker {
    private final Thread thread;

    Worker(Thread thread) {
        this.thread = thread;
    }

    public String getName() {
        return thread.getName();
    }

    public boolean isActive() {
        return thread.isAlive();
    }
}
