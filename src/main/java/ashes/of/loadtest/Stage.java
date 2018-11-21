package ashes.of.loadtest;


public enum Stage {

    /**
     * Baseline stage doesn't run any tests, no-op method are invoked instead.
     *
     * This stage is used for framework warm-up
     */
    Baseline,

    /**
     * Like a test, but used for warm-up
     */
    WarmUp,

    /**
     * Main test stage
     */
    Test
}
