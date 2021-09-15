package ashes.of.bomber.configuration;


public enum Stage {

    /**
     * This stage doesn't run any tests
     */
    IDLE,

    /**
     * Like a test, but used for warm-up
     */
    WARM_UP,

    /**
     * Main test stage
     */
    TEST
}
