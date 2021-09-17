package ashes.of.bomber.watcher;

import ashes.of.bomber.snapshots.TestFlightSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import java.lang.management.ManagementFactory;

public class GarbageCollectorWatcher implements Watcher {
    private static final Logger log = LogManager.getLogger(new StringFormatterMessageFactory());

    @Override
    public void watch(TestFlightSnapshot flight) {
        var testApp = flight.getCurrent();
        if (testApp != null) {
            ThreadContext.put("testApp", testApp.getName());
            var testSuite = testApp.getCurrent();
            if (testSuite != null) {
                ThreadContext.put("testSuite", testSuite.getName());
                var testCase = testSuite.getCurrent();
                if (testCase != null) {
                    ThreadContext.put("testCase", testCase.getName());
                }
            }
        }

        ManagementFactory.getGarbageCollectorMXBeans()
                .forEach(gc -> log.info("%-24s %,16d count, %,16dms", gc.getName(), gc.getCollectionCount(), gc.getCollectionTime()));

        ThreadContext.clearAll();
    }
}
