package ashes.of.bomber.sink;

import ashes.of.bomber.events.TestCaseAfterEachEvent;
import ashes.of.bomber.tools.Record;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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
    public void timeRecorded(Record record) {
        Level level = record.isSuccess() ? this.level : Level.WARN;
        log.log(level, "time {}: {}ms  #{}",
                record.getLabel(), record.getElapsed() / 1_000_000.0, record.getIteration().getNumber(), record.getError());
    }

    @Override
    public void afterEach(TestCaseAfterEachEvent event) {
        if (event.getThrowable() != null)
            log.error("{} #{}", event.getTest().getName(), event.getNumber(), event.getThrowable());
    }
}
