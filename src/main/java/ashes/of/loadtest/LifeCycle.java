package ashes.of.loadtest;

@FunctionalInterface
public interface LifeCycle<T extends TestCase> {

    /**
     * One of lifecycle (BeforeAll, BeforeEach, AfterEach, AfterAll) method
     */
    void call(T testCase) throws Throwable;
}
