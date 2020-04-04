package ashes.of.bomber.methods;

@FunctionalInterface
public interface LifeCycleMethod<T> {

    /**
     * One of lifecycle method
     */
    void call(T instance) throws Throwable;
}
