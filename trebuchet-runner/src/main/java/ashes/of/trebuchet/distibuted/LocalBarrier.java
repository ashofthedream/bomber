package ashes.of.trebuchet.distibuted;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;


public class LocalBarrier implements Barrier {
    private static final Logger log = LogManager.getLogger();

    
    private static class NamedCascadeBarrier {
        private final String name;
        private final CyclicBarrier enter;
        private final CyclicBarrier leave;

        public NamedCascadeBarrier(String name, int members, Barrier next) {
            this.name = name;
            this.enter = new CyclicBarrier(members, () -> next.enter(name));
            this.leave = new CyclicBarrier(members, () -> next.leave(name));
        }
        
        public void enter() {
            try {
                enter.await();
            } catch (Exception e) {
                log.error("Can't enter barrier: {}", name, e);
            }
        }

        public void leave() {
            try {
                leave.await();
            } catch (Exception e) {
                log.error("Can't leave barrier: {}", name, e);
            }
        }
    }


    private final Map<String, NamedCascadeBarrier> barriers = new ConcurrentHashMap<>();
    private final int members;
    private final Barrier next;


    public LocalBarrier(int members, Barrier next) {
        this.members = members;
        this.next = next;
    }

    public LocalBarrier(int members) {
        this(members, new NoBarrier());
    }


    @Override
    public void enter(String test) {
        NamedCascadeBarrier barrier = getOrCreateBarrier(test);
        String thread = Thread.currentThread().getName();
        log.trace("test: {}, thread: {} try to enter barrier", test, thread);
        barrier.enter();
    }

    @Override
    public void leave(String test) {
        NamedCascadeBarrier barrier = getOrCreateBarrier(test);
        String thread = Thread.currentThread().getName();
        log.trace("test: {}, thread: {} try to leave barrier", test, thread);
        barrier.leave();
    }


    private NamedCascadeBarrier getOrCreateBarrier(String test) {
        return barriers.computeIfAbsent(test, k -> new NamedCascadeBarrier(test, members, next));
    }
}
