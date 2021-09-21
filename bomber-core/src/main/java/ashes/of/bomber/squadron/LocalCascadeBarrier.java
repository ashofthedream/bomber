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
    private final int members;
    private final Barrier next;


    public LocalCascadeBarrier(int members, Barrier next) {
        this.members = members;
        this.next = next;
    }

    public LocalCascadeBarrier(int members) {
        this(members, new NoBarrier());
    }


    @Override
    public void enterCase(Test test) {
        NamedCascadeBarrier barrier = getOrCreateBarrier(test);
        String thread = Thread.currentThread().getName();
        log.trace("enterCase test: {}, thread: {} try to start test barrier", test.getName(), thread);
        barrier.enter();
    }

    @Override
    public void leaveCase(Test test) {
        NamedCascadeBarrier barrier = getOrCreateBarrier(test);
        String thread = Thread.currentThread().getName();
        log.trace("enterCase test: {}, thread: {} try to finish test barrier", test.getName(), thread);
        barrier.leave();
    }


    private NamedCascadeBarrier getOrCreateBarrier(Test test) {
        return barriers.computeIfAbsent(test,
                k -> new NamedCascadeBarrier(test, members, next));
    }
}
