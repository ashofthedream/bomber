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
        Level level = record.success() ? this.level : Level.WARN;
        log.log(level, "time {}: {}ms  #{}",
                record.label(), record.elapsed() / 1_000_000.0, record.iteration().number(), record.error());
    }

    @Override
    public void afterEach(TestCaseAfterEachEvent event) {
        if (event.throwable() != null)
            log.error("{} #{}", event.test().name(), event.number(), event.throwable());
    }
}
