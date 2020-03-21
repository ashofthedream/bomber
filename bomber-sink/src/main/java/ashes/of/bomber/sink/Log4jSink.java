package ashes.of.bomber.sink;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.stopwatch.Record;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;


public class Log4jSink implements Sink {
    private static final Logger log = LogManager.getLogger();
    private final Level level;

    public Log4jSink(Level level) {
        this.level = level;
    }

    public Log4jSink() {
        this(Level.INFO);
    }

    @Override
    public void timeRecorded(Context ctx, Record record) {
        Level level = record.isSuccess() ? this.level : Level.WARN;
        log.log(level, "onTimeRecorded. stage: {}, testCase: {}, test: {}, inv: {}, ts: {}, elapsed: {}ms, error: {}",
                ctx.getStage(), ctx.getTestSuite(), ctx.getTestCase(), ctx.getInv(), ctx.getTimestamp(), record.getElapsed() / 1_000_000.0, record.getError());
    }

    @Override
    public void afterTestCase(Context ctx, long elapsed, @Nullable Throwable throwable) {
        Level level = throwable == null ? this.level : Level.ERROR;
        log.log(level, "afterEachTest. stage: {}, testCase: {}, test: {}, inv: {}, ts: {}, elapsed: {}ms",
                ctx.getStage(), ctx.getTestSuite(), ctx.getTestCase(), ctx.getInv(), ctx.getTimestamp(), elapsed / 1_000_000.0, throwable);
    }
}
