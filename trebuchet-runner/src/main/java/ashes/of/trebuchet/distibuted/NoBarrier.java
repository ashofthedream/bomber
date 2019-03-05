package ashes.of.trebuchet.distibuted;

import ashes.of.trebuchet.builder.Settings;
import ashes.of.trebuchet.runner.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NoBarrier implements Barrier {
    private static final Logger log = LogManager.getLogger();

    public static class Builder extends BarrierBuilder {

        @Override
        public Barrier build() {
            return new NoBarrier();
        }
    }

    @Override
    public void init(String name, Settings settings) {

    }

    @Override
    public void stageStart(Stage stage) {
        log.trace("start stage: {}", stage);
    }

    @Override
    public void testStart(String test) {
        log.trace("start test: {}", test);
    }

    @Override
    public void testFinish(String test) {
        log.trace("finish test: {}", test);
    }

    @Override
    public void stageLeave(Stage stage) {
        log.trace("leave stage: {}", stage);
    }

}
