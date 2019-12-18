package ashes.of.bomber.sink;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.stopwatch.Stopwatch;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;


public class Log4jSink implements Sink {
    private static final Logger log = LogManager.getLogger();

    @Override
    public void afterEach(Context ctx, long elapsed, Stopwatch stopwatch, @Nullable Throwable throwable) {
        Level level = throwable != null ? Level.ERROR : Level.INFO;
        log.log(level, "afterEach stage: {}, testCase: {}, test: {}, inv: {}, ts: {}, elapsed: {}ms, laps: {}",
                ctx.getStage(), ctx.getTestCase(), ctx.getTest(), ctx.getInv(), ctx.getTimestamp(), elapsed / 1_000_000.0, stopwatch.laps(), throwable);
    }
}
