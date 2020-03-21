package ashes.of.bomber.runner;

import ashes.of.bomber.core.Stage;
import ashes.of.bomber.core.State;
import ashes.of.bomber.sink.Sink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TestApp {
    private static final Logger log = LogManager.getLogger();

    private final Environment env;
    private final List<TestSuite<?>> suites;

    public TestApp(Environment env, List<TestSuite<?>> suites) {
        this.env = env;
        this.suites = suites;
    }


    public void run() {
        env.getSinks().forEach(Sink::afterStartUp);
        log.info("run test app with {} suites", suites.size());
        try {
            suites.forEach(this::run);
        } catch (Throwable th) {
            log.error("unexpected throwable", th);
        }

        env.getSinks().forEach(Sink::afterShutdown);
    }

    private void run(TestSuite<?> testSuite) {
        try {
            log.info("Start testSuite: {}", testSuite.getName());

            State warmUp = new State(Stage.WarmUp, testSuite.getTest(), testSuite.getName());
            State test = new State(Stage.Test, testSuite.getWarmUp(), testSuite.getName());

            new Runner<>(warmUp,   env, testSuite.getLifeCycle()).run();
            new Runner<>(test,     env, testSuite.getLifeCycle()).run();

        } catch (Exception e) {
            log.warn("Some shit happened testSuite: {}", testSuite.getName(), e);
        }

        log.info("End testSuite: {}", testSuite.getName());
    }
}
