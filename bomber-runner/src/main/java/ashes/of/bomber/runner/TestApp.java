package ashes.of.bomber.runner;

import ashes.of.bomber.sink.Sink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class TestApp {
    private static final Logger log = LogManager.getLogger();

    private final Environment env;
    private final List<TestSuite<?>> suites;

    public TestApp(Environment env, List<TestSuite<?>> suites) {
        this.env = env;
        this.suites = suites;
    }

    public Report run() {
        Instant startTime = Instant.now();
        env.getSinks().forEach(Sink::afterStartUp);
        List<String> testSuiteNames = suites.stream()
                .map(testSuite -> {
                    return String.format("%s %s", testSuite.getName(), testSuite.getTestCases());
                })
                .collect(Collectors.toList());

        log.info("run test app with {} suites: {}", suites.size(), testSuiteNames);
        LongAdder errorsCount = new LongAdder();
        try {
            suites.stream()
                    .map(suite -> suite.run(this))
                    .forEach(states -> {
                        states.forEach(state -> {
                            errorsCount.add(state.getErrorCount());
                        });
                    });
        } catch (Throwable th) {
            log.error("unexpected throwable", th);
        }

        env.getSinks().forEach(Sink::afterShutdown);
        Instant finishTime = Instant.now();
        return new Report(startTime, finishTime, errorsCount.sum());
    }
}
