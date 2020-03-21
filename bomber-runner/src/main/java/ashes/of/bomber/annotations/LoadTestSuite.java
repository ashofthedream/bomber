package ashes.of.bomber.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * Marks class as load test suite and defines settings for load test stage
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoadTestSuite {

    /**
     * @return test suite name
     */
    String name() default "";

    /**
     * @return test time
     */
    long time() default 60;

    /**
     * @return time unit for time
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * @return number of threads
     */
    int threads() default 1;

    /**
     * @return number of total invocations for all threads
     */
    long totalInvocations() default Long.MAX_VALUE;

    /**
     * @return number of invocations per each thread.
     */
    long threadInvocations() default Long.MAX_VALUE;

    /**
     * @return indicates that test suite will be shared around all test threads
     */
    boolean shared() default false;
}
