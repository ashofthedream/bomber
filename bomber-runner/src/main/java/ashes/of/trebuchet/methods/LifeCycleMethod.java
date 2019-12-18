package ashes.of.trebuchet.methods;

@FunctionalInterface
public interface LifeCycleMethod<T> {

    /**
     * One of lifecycle (BeforeAll, BeforeEach, AfterEach, AfterAll) method
     */
    void call(T testCase) throws Throwable;
}
