package ashes.of.bomber.sink;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.stopwatch.Record;
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
        log.log(level, "{} | timeRecorded: {}ms  | {}",
                ctx.toLogString(), record.getElapsed() / 1_000_000.0, ctx.getInv(), record.getError());
    }

    @Override
    public void afterEach(Context ctx, long elapsed, @Nullable Throwable throwable) {
//        if (throwable != null)
//            log.error("{} | afterEach error(inv: {})", ctx.toLogString(), ctx.getInv(), throwable);
    }
}
