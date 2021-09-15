package ashes.of.bomber.squadron;

import ashes.of.bomber.configuration.Stage;

public interface Barrier {
    default void enterSuite(Stage stage, String testSuite) {}
    default void enterCase(Stage stage, String testSuite, String testCase) {}
    default void leaveCase(Stage stage, String testSuite, String testCase) {}
    default void leaveSuite(Stage stage, String testSuite) {}
}
