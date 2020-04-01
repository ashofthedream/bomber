package ashes.of.bomber.runner;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.core.State;
import ashes.of.bomber.sink.AsyncSink;
import ashes.of.bomber.sink.MultiSink;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.Barrier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;


public class Runner<T> {
    private static final Logger log = LogManager.getLogger();

    private final WorkerPool pool;
    private final Environment env;
    private final LifeCycle<T> lifeCycle;
    private final Sink sink;

    private final Map<String, Worker> workers = new ConcurrentHashMap<>();

    public Runner(WorkerPool pool, Environment env, LifeCycle<T> lifeCycle) {
        this.pool = pool;
        this.env = env;
        this.lifeCycle = lifeCycle;
        this.sink = new AsyncSink(new MultiSink(env.getSinks()));
    }

    public void run(State state) {
        run(state, state.getStage(), state.getSettings());
    }

    /**
     * Runs the test case
     */
    public void run(State state, Stage stage, Settings settings) {
        ThreadContext.put("stage", state.getStage().name());
        ThreadContext.put("testSuite", state.getTestSuite());

        log.info("Start with settings: {}", settings);

        Barrier barrier = env.getBarrier()
                .workers(settings.getThreadsCount())
                .build();

        barrier.enterSuite(stage, state.getTestSuite(), settings);
        state.startSuiteIfNotStarted();
        sink.beforeTestSuite(stage, state.getTestSuite(), state.getTestSuiteStartTime(), settings);

        CountDownLatch begin = new CountDownLatch(settings.getThreadsCount());
        CountDownLatch end = new CountDownLatch(settings.getThreadsCount());

        for (int i = 0; i < settings.getThreadsCount(); i++) {
            Worker worker = pool.acquire();
            workers.put(worker.getName(), worker);
            worker.run(state, settings, begin, end, barrier, env, sink, lifeCycle);
        }

        try {
            log.debug("Await end of stage");
            end.await();

            log.debug("all workers done, 1s cooldown");
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            log.error("We've been interrupted", e);
        }

        log.debug("release all workers");
        pool.release(workers.values());

        log.info("Ended, elapsed {}ms", state.getCaseElapsedTime());
        barrier.leaveSuite(stage, state.getTestSuite(), settings);
        sink.afterTestSuite(stage, state.getTestSuite());

        ThreadContext.clearAll();
    }
}
