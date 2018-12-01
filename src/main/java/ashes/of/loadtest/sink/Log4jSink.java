package ashes.of.loadtest.sink;

import ashes.of.loadtest.runner.Context;
import ashes.of.loadtest.stopwatch.Lap;
import ashes.of.loadtest.stopwatch.Stopwatch;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;


public class Log4jSink implements Sink {
    private static final Logger log = LogManager.getLogger(Log4jSink.class);

    @Override
    public void afterEach(Context ctx, long elapsed, Stopwatch stopwatch, @Nullable Throwable throwable) {
        Map<String, List<Lap>> laps = stopwatch.lapsByLabel();

        Level level = throwable != null ? Level.ERROR : Level.INFO;
        log.log(level, "afterEach stage: {}, testCase: {}, test: {}, inv: {}, ts: {}, elapsed: {}ms, laps: {}",
                ctx.getStage(), ctx.getTestCase(), ctx.getTest(), ctx.getInv(), ctx.getTimestamp(), elapsed / 1_000_000.0, laps);
    }
}
