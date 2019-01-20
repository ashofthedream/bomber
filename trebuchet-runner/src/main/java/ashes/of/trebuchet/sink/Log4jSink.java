package ashes.of.trebuchet.sink;

import ashes.of.trebuchet.runner.Context;
import ashes.of.trebuchet.stopwatch.Stopwatch;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;


public class Log4jSink implements Sink {
    private static final Logger log = LogManager.getLogger(Log4jSink.class);

    @Override
    public void afterEach(Context ctx, long elapsed, Stopwatch stopwatch, @Nullable Throwable throwable) {

        Level level = throwable != null ? Level.ERROR : Level.INFO;
        log.log(level, "afterEach stage: {}, testCase: {}, test: {}, inv: {}, ts: {}, elapsed: {}ms, laps: {}",
                ctx.getStage(), ctx.getTestCase(), ctx.getTest(), ctx.getInv(), ctx.getTimestamp(), elapsed / 1_000_000.0, stopwatch.laps());
    }
}
