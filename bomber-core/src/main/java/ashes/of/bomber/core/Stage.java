package ashes.of.bomber.core;


public enum Stage {

    /**
     * This stage doesn't run any tests
     */
    Rest,

    /**
     * Like a test, but used for warm-up
     */
    WarmUp,

    /**
     * Main test stage
     */
    Test
}
