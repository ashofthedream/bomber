package ashes.of.bomber.squadron;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;


public class LocalCascadeBarrier implements Barrier {
    private static final Logger log = LogManager.getLogger();


    private static class NamedCascadeBarrier {
        private final String testApp;
        private final String testSuite;
        private final String testCase;

        private final CyclicBarrier enter;
        private final CyclicBarrier leave;

        public NamedCascadeBarrier(String testApp, String testSuite, String testCase, int members, Barrier next) {
            this.testApp = testApp;
            this.testSuite = testSuite;
            this.testCase = testCase;

            this.enter = new CyclicBarrier(members, () -> next.enterCase(testApp, testSuite, testCase));
            this.leave = new CyclicBarrier(members, () -> next.leaveCase(testApp, testSuite, testCase));
        }

        public void enter() {
            try {
                enter.await();
            } catch (Exception e) {
                log.error("Can't enter testApp: {}, testSuite: {}, testCase: {}", testApp, testSuite, testCase, e);
            }
        }

        public void leave() {
            try {
                leave.await();
            } catch (Exception e) {
                log.error("Can't leave stage: {}, testSuite: {}, testCase: {}", testApp, testSuite, testCase, e);
            }
        }
    }


    private final Map<String, NamedCascadeBarrier> barriers = new ConcurrentHashMap<>();
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
    public void enterCase(String testApp, String testSuite, String testCase) {
        NamedCascadeBarrier barrier = getOrCreateBarrier(testApp, testSuite, testCase);
        String thread = Thread.currentThread().getName();
        log.trace("enterCase testCase: {}, thread: {} try to start test barrier", testCase, thread);
        barrier.enter();
    }

    @Override
    public void leaveCase(String testApp, String testSuite, String testCase) {
        NamedCascadeBarrier barrier = getOrCreateBarrier(testApp, testSuite, testCase);
        String thread = Thread.currentThread().getName();
        log.trace("enterCase testCase: {}, thread: {} try to finish test barrier", testCase, thread);
        barrier.leave();
    }


    private NamedCascadeBarrier getOrCreateBarrier(String testApp, String testSuite, String testCase) {
        return barriers.computeIfAbsent(testSuite + "." + testCase,
                k -> new NamedCascadeBarrier(testApp, testSuite, testCase, members, next));
    }
}
