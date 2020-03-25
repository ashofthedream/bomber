package ashes.of.bomber.sink;

import ashes.of.bomber.core.Iteration;
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
    public void timeRecorded(Iteration it, Record record) {
        Level level = record.isSuccess() ? this.level : Level.WARN;
        log.log(level, "time {}: {}ms  #{}",
                record.getLabel(), record.getElapsed() / 1_000_000.0, it.getNumber(), record.getError());
    }

    @Override
    public void afterEach(Iteration it, long elapsed, @Nullable Throwable throwable) {
        if (throwable != null)
            log.error("{}.{} #{}", it.getTestSuite(), it.getTestCase(), it.getNumber(), throwable);
    }
}
