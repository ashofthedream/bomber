package ashes.of.loadtest;

@FunctionalInterface
public interface LifeCycle<T> {

    /**
     * One of lifecycle (BeforeAll, BeforeEach, AfterEach, AfterAll) method
     */
    void call(T testCase) throws Throwable;
}
