package ashes.of.bomber.runner;

import ashes.of.bomber.core.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
}
