package ashes.of.loadtest.sink;

import ashes.of.loadtest.runner.TestCaseContext;
import ashes.of.loadtest.runner.TestContext;
import ashes.of.loadtest.stopwatch.Lap;
import ashes.of.loadtest.stopwatch.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;


public class Log4jSink implements Sink {
    private static final Logger log = LogManager.getLogger(Log4jSink.class);

    @Override
    public void afterTest(TestContext context, long elapsed, Stopwatch stopwatch, @Nullable Throwable throwable) {
        Map<String, List<Lap>> laps = stopwatch.lapsByLabel();

        TestCaseContext tc = context.getTestCase();
        if (throwable != null) {
            log.warn("onTestError stage: {}, testCase: {}, test: {}, thread: {}, it: {}, ts: {}, time: {}ms, laps: {}",
                    tc.getStage(), tc.getName(), context.getName(),
                    tc.getThreadName(), tc.getInvocationNumber(), context.getStartTime(), elapsed / 1_000_000.0, laps, throwable);

            return;
        }

        log.info("afterTest stage: {}, testCase: {}, test: {}, thread: {}, it: {}, ts: {}, time: {}ms, laps: {}",
                tc.getStage(), tc.getName(), context.getName(),
                tc.getThreadName(), tc.getInvocationNumber(), context.getStartTime(), elapsed/ 1_000_000.0, laps);
    }

    @Override
    public void afterAllTests(TestCaseContext context, long elapsed) {
        log.info("afterAllTests stage: {}, testCase: {}, thread: {}, it: {}, ts: {}, time: {}",
                context.getStage(), context.getName(), context.getThreadName(), context.getInvocationNumber(), context.getStartTime(), elapsed);
    }
}
