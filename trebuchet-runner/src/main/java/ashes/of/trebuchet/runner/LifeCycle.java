package ashes.of.trebuchet.runner;

@FunctionalInterface
public interface LifeCycle<T> {

    /**
     * One of lifecycle (BeforeAll, BeforeEach, AfterEach, AfterAll) method
     */
    void call(T testCase) throws Throwable;
}
