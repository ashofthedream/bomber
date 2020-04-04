package ashes.of.bomber.squadron;

import ashes.of.bomber.core.Stage;

public interface Barrier {
    default void enterCase(Stage stage, String testSuite, String testCase) {}
    default void leaveCase(Stage stage, String testSuite, String testCase) {}
}
