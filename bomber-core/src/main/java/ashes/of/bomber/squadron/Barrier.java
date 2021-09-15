package ashes.of.bomber.squadron;

import ashes.of.bomber.configuration.Stage;
import ashes.of.bomber.events.EventHandler;

public interface Barrier extends EventHandler {
    default void enterCase(Stage stage, String testSuite, String testCase) {}
    default void leaveCase(Stage stage, String testSuite, String testCase) {}
}
