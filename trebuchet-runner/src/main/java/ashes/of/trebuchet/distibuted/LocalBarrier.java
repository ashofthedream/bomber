package ashes.of.trebuchet.distibuted;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocalBarrier implements Barrier {
    private static final Logger log = LogManager.getLogger();

    @Override
    public void enter(String test) {
        log.info("enter barrier test: {}", test);
    }

    @Override
    public void leave(String test) {
        log.info("leave barrier test: {}", test);
    }
}
