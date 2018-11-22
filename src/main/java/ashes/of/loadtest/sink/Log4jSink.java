package ashes.of.loadtest.sink;

import ashes.of.loadtest.runner.TestCaseContext;
import ashes.of.loadtest.runner.TestContext;
import ashes.of.loadtest.stopwatch.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;


public class Log4jSink implements Sink {
    private static final Logger log = LogManager.getLogger(Log4jSink.class);

    @Override
    public void afterTest(TestContext context, long elapsed, Stopwatch stopwatch, @Nullable Throwable throwable) {
        if (throwable != null) {
            log.warn("onTestError stage: {}, testCase: {}, test: {}, thread: {}, it: {}, ts: {}, time: {}",
                    context.getTestCase().getStage(), context.getTestCase().getName(), context.getName(),
                    context.getTestCase().getThreadName(), context.getTestCase().getInvocationNumber(), context.getStartTime(), elapsed, throwable);

            return;
        }

        log.info("afterTest stage: {}, testCase: {}, test: {}, thread: {}, it: {}, ts: {}, time: {}",
                context.getTestCase().getStage(), context.getTestCase().getName(), context.getName(),
                context.getTestCase().getThreadName(), context.getTestCase().getInvocationNumber(), context.getStartTime(), elapsed);
    }

    @Override
    public void afterAllTests(TestCaseContext context, long elapsed) {
        log.info("afterAllTests stage: {}, testCase: {}, thread: {}, it: {}, ts: {}, time: {}",
                context.getStage(), context.getName(), context.getThreadName(), context.getInvocationNumber(), context.getStartTime(), elapsed);
    }
}
