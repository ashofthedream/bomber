package ashes.of.bomber.watcher;

import ashes.of.bomber.snapshots.TestFlightSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

public class MemoryUsageWatcher implements Watcher {
    private static final Logger log = LogManager.getLogger(new StringFormatterMessageFactory());

    @Override
    public void watch(TestFlightSnapshot flight) {
        var testApp = flight.current();
        if (testApp != null) {
            ThreadContext.put("testApp", testApp.name());
            var testSuite = testApp.current();
            if (testSuite != null) {
                ThreadContext.put("testSuite", testSuite.name());
                var testCase = testSuite.current();
                if (testCase != null) {
                    ThreadContext.put("testCase", testCase.name());
                }
            }
        }

        ManagementFactory.getMemoryPoolMXBeans()
                .stream()
                .filter(pool -> !pool.getName().equals("Code Cache"))
                .forEach(pool -> {
                    MemoryUsage usage = pool.getUsage();
                    log.info("%-24s %,20d used, %,20d committed,  %,20d max", pool.getName(), usage.getUsed(), usage.getCommitted(), usage.getMax());
                });

        ThreadContext.clearAll();
    }
}

