package ashes.of.trebuchet.distibuted;


import ashes.of.trebuchet.builder.Settings;
import ashes.of.trebuchet.runner.Stage;

public interface Barrier {

    void init(String name, Settings settings);

    void stageStart(Stage stage);
    void testStart(String test);
    void testFinish(String test);
    void stageLeave(Stage stage);
}
