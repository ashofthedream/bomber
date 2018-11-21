package ashes.of.loadtest.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * Marks class as load test case and defines settings fot load test stage
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoadTestCase {

    /**
     * @return test case name, if empty class name will be used
     */
    String value() default "";

    /**
     * @return test case name, if empty - {@link this#value() will be used}
     */
    String name() default "";

    /**
     * @return number of threads
     */
    int threads() default 1;

    /**
     * @return number of total iterations for all threads
     */
    long totalIterations() default Long.MAX_VALUE;

    /**
     * @return number of iterations per each thread.
     */
    long threadIterations() default Long.MAX_VALUE;

    /**
     * @return test time
     */
    long time() default 60;

    /**
     * @return time unit for time
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;


    /**
     * @return should this test run in concurrent mode (one instance shared around all test threads)
     */
    boolean concurrent() default false;
}
