package ashes.of.bomber.squadron;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;


public interface Barrier {
    default void init(String name, Settings settings) {}
    default void stageStart(Stage stage) {}
    default void testStart(String test) {}
    default void testFinish(String test) {}
    default void stageLeave(Stage stage) {}
}
