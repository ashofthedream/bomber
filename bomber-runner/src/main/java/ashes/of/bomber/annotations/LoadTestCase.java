package ashes.of.bomber.annotations;

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
     * @return test case name
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
     * @return should this test run in concurrent mode (one instance shared around all test threads)
     */
    boolean concurrent() default false;
}
