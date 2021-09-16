package ashes.of.bomber.squadron;

public interface Barrier {
    default void enterCase(String testApp, String testSuite, String testCase) {}
    default void leaveCase(String testApp, String testSuite, String testCase) {}
}
