package ashes.of.bomber.methods;

@FunctionalInterface
public interface LifeCycleMethod<T> {

    /**
     * One of lifecycle (BeforeAll, BeforeEach, AfterEach, AfterAll) method
     */
    void call(T object) throws Throwable;
}
