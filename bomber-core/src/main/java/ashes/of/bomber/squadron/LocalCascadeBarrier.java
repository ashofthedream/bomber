package ashes.of.bomber.squadron;

import ashes.of.bomber.core.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;


public class LocalCascadeBarrier implements Barrier {
    private static final Logger log = LogManager.getLogger();


    private static class NamedCascadeBarrier {
        private final Test test;

        private final CyclicBarrier enter;
        private final CyclicBarrier leave;

        public NamedCascadeBarrier(Test test, int members, Barrier next) {
            this.test = test;

            this.enter = new CyclicBarrier(members, () -> next.enterCase(test));
            this.leave = new CyclicBarrier(members, () -> next.leaveCase(test));
        }

        public void enter() {
            try {
                enter.await();
            } catch (Exception e) {
                log.error("Can't enter test: {}", test, e);
            }
        }

        public void leave() {
            try {
                leave.await();
            } catch (Exception e) {
                log.error("Can't leave test: {}", test, e);
            }
        }
    }


    private final Map<Test, NamedCascadeBarrier> barriers = new ConcurrentHashMap<>();
    private final Barrier next;

    private volatile int members;

    public LocalCascadeBarrier(Barrier next) {
        this.next = next;
    }

    public LocalCascadeBarrier() {
        this(new NoBarrier());
    }

    private boolean isNotInitialized() {
        return this.members == 0;
    }

    @Override
    public void init(int members) {
        if (isNotInitialized()) {
            this.members = members;
            this.next.init(members);
        }
    }

    @Override
    public void enterCase(Test test) {
        if (isNotInitialized())
            throw new RuntimeException("Barrier is not initialized");

        NamedCascadeBarrier barrier = getOrCreateBarrier(test);
        String thread = Thread.currentThread().getName();
        log.trace("enterCase test: {}, thread: {} try to start test barrier", test.name(), thread);
        barrier.enter();
    }

    @Override
    public void leaveCase(Test test) {
        if (isNotInitialized())
            throw new RuntimeException("Barrier is not initialized");

        NamedCascadeBarrier barrier = getOrCreateBarrier(test);
        String thread = Thread.currentThread().getName();
        log.trace("enterCase test: {}, thread: {} try to finish test barrier", test.name(), thread);
        barrier.leave();
    }


    private NamedCascadeBarrier getOrCreateBarrier(Test test) {
        return barriers.computeIfAbsent(test,
                k -> new NamedCascadeBarrier(test, members, next));
    }
}
