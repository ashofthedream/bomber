package ashes.of.bomber.watcher;

import ashes.of.bomber.core.BomberApp;
import ashes.of.bomber.core.StateModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import java.lang.management.ManagementFactory;

public class GarbageCollectorWatcher implements Watcher {
    private static final Logger log = LogManager.getLogger(new StringFormatterMessageFactory());

    @Override
    public void watch(BomberApp app) {
        StateModel state = app.getState();
        ThreadContext.put("stage", state.getStage().name());
        ThreadContext.put("testSuite", state.getTestSuite());
        ThreadContext.put("testCase", state.getTestCase());

        ManagementFactory.getGarbageCollectorMXBeans()
                .forEach(gc -> log.info("%-24s %,16d count, %,16dms", gc.getName(), gc.getCollectionCount(), gc.getCollectionTime()));

        ThreadContext.clearAll();
    }
}
