package ashes.of.bomber.squadron.zookeeper;

import ashes.of.bomber.squadron.Barrier;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class ZookeeperBarrier implements Barrier {
    private static final Logger log = LogManager.getLogger();

    private final Map<String, DistributedDoubleBarrier> barriers = new ConcurrentHashMap<>();

    private final CuratorFramework cf;
    private final Duration awaitTime;
    private final int members;

    private volatile PersistentNode node;

    public ZookeeperBarrier(CuratorFramework cf, int members, Duration awaitTime) {
        this.cf = cf;
        this.members = members;
        this.awaitTime = awaitTime;
    }

    @Override
    public void enterCase(String testApp, String testSuite, String testCase) {
        try {
            log.trace("enterCase testSuite: {}, testCase: {}", testSuite, testCase);
            getOrCreateBarrier(testApp, testCase, testCase)
                    .enter(awaitTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("enterCase failed. testSuite: {}, testCase: {}", testSuite, testCase, e);
        }
    }

    @Override
    public void leaveCase(String testApp, String testSuite, String testCase) {
        try {
            log.trace("leaveCase testSuite: {}, testCase: {}", testSuite, testCase);
            getOrCreateBarrier(testApp, testCase, testCase)
                    .leave(awaitTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("leaveCase failed. testSuite: {}, testCase: {}", testSuite, testCase, e);
        }
    }

    private DistributedDoubleBarrier getOrCreateBarrier(String testApp, String testSuite, String testCase) {
        return barriers.computeIfAbsent("%s.%s.%s".formatted(testApp, testSuite, testCase), name -> new DistributedDoubleBarrier(cf, "/bomber/barriers/" + name, members));
    }
}
