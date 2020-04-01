package ashes.of.bomber.runner;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.core.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public class TestSuite<T> {
    private static final Logger log = LogManager.getLogger();

    private final WorkerPool pool;
    private final String name;
    private final Environment env;
    private final LifeCycle<T> lifeCycle;
    private final Settings settings;
    private final Settings warmUp;


    public TestSuite(WorkerPool pool, String name, Environment env, LifeCycle<T> lifeCycle, Settings settings, Settings warmUp) {
        this.pool = pool;
        this.name = name;
        this.env = env;
        this.lifeCycle = lifeCycle;
        this.settings = new Settings(settings);
        this.warmUp = new Settings(warmUp);
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

    public Settings getSettings() {
        return settings;
    }

    public Set<String> getTestCases() {
        return lifeCycle.testCases().keySet();
    }


    public void run(TestApp app) {
        Runner<T> runner = new Runner<>(pool, env, lifeCycle);
        State warmUp = new State(Stage.WarmUp, this.warmUp, name, app::isStop);
        State test = new State(Stage.Test, this.settings, name, app::isStop);

        try {
            log.info("Start testSuite: {}", name);
            if (!warmUp.getSettings().isDisabled()) {
                app.setState(warmUp);
                runner.run(warmUp);
            }

            app.setState(test);
            runner.run(test);

            app.setState(null);
        } catch (Exception e) {
            log.warn("Some shit happened testSuite: {}", name, e);
        }

        log.info("Finish testSuite: {}", name);
    }
}
