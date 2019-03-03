package ashes.of.trebuchet.distibuted;

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
    public void enter(String test) {
        log.trace("test: {} no barrier enter", test);
    }

    @Override
    public void leave(String test) {
        log.trace("test: {} no barrier leave", test);
    }
}
