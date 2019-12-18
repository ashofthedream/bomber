package ashes.of.bomber.squadron;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
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
            this.enter = new CyclicBarrier(members, () -> next.testStart(name));
            this.leave = new CyclicBarrier(members, () -> next.testFinish(name));
        }
        
        public void enter() {
            try {
                enter.await();
            } catch (Exception e) {
                log.error("Can't start test: {}", name, e);
            }
        }

        public void leave() {
            try {
                leave.await();
            } catch (Exception e) {
                log.error("Can't finish test: {}", name, e);
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
    public void stageStart(Stage stage) {

    }

    @Override
    public void testStart(String test) {
        NamedCascadeBarrier barrier = getOrCreateBarrier(test);
        String thread = Thread.currentThread().getName();
        log.trace("test: {}, thread: {} try to start test barrier", test, thread);
        barrier.enter();
    }

    @Override
    public void testFinish(String test) {
        NamedCascadeBarrier barrier = getOrCreateBarrier(test);
        String thread = Thread.currentThread().getName();
        log.trace("test: {}, thread: {} try to finish test barrier", test, thread);
        barrier.leave();
    }

    @Override
    public void stageLeave(Stage stage) {

    }


    private NamedCascadeBarrier getOrCreateBarrier(String test) {
        return barriers.computeIfAbsent(test, k -> new NamedCascadeBarrier(test, members, next));
    }
}
