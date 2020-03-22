package ashes.of.bomber.runner;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.core.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TestSuite<T> {
    private static final Logger log = LogManager.getLogger();

    private final String name;
    private final Environment env;
    private final LifeCycle<T> lifeCycle;
    private final Settings warmUp;
    private final Settings test;


    public TestSuite(String name, Environment env, LifeCycle<T> lifeCycle, Settings warmUp, Settings test) {
        this.name = name;
        this.env = env;
        this.lifeCycle = lifeCycle;
        this.warmUp = warmUp;
        this.test = test;
    }


    public String getName() {
        return name;
    }

    public Environment getEnv() {
        return env;
    }

    public LifeCycle<T> getLifeCycle() {
        return lifeCycle;
    }

    public Settings getWarmUp() {
        return warmUp;
    }

    public Settings getTest() {
        return test;
    }

    public Set<String> getTestCases() {
        return lifeCycle.testCases().keySet();
    }


    public List<State> run(TestApp app) {
        State warmUp = new State(Stage.WarmUp, this.warmUp, name, () -> app.isShutdown());
        State test = new State(Stage.Test, this.test, name, () -> app.isShutdown());

        try {
            log.info("Start testSuite: {}", name);

            app.setState(warmUp);
            new Runner<>(warmUp, env, lifeCycle).run();

            app.setState(test);
            new Runner<>(test, env, lifeCycle).run();

            app.setState(null);
        } catch (Exception e) {
            log.warn("Some shit happened testSuite: {}", name, e);
        }

        log.info("Finish testSuite: {}", name);
        return Arrays.asList(warmUp, test);
    }
}
