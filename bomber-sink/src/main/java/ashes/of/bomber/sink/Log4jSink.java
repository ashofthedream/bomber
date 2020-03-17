package ashes.of.bomber.sink;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.stopwatch.Lap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;


public class Log4jSink implements Sink {
    private static final Logger log = LogManager.getLogger();

    @Override
    public void afterEachLap(Context ctx, Lap.Record record) {
        Level level = record.isSuccess() ? Level.INFO : Level.WARN;
        log.log(level, "afterEachLap. stage: {}, testCase: {}, test: {}, inv: {}, ts: {}, elapsed: {}ms, error: {}",
                ctx.getStage(), ctx.getTestCase(), ctx.getTest(), ctx.getInv(), ctx.getTimestamp(), record.getElapsed() / 1_000_000.0, record.getError());
    }

    @Override
    public void afterEachTest(Context ctx, long elapsed, @Nullable Throwable throwable) {
        Level level = throwable != null ? Level.ERROR : Level.INFO;
        log.log(level, "afterEachTest. stage: {}, testCase: {}, test: {}, inv: {}, ts: {}, elapsed: {}ms",
                ctx.getStage(), ctx.getTestCase(), ctx.getTest(), ctx.getInv(), ctx.getTimestamp(), elapsed / 1_000_000.0, throwable);
    }
}
