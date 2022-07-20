package ashes.of.bomber.squadron.zookeeper;

import ashes.of.bomber.core.Test;
import ashes.of.bomber.squadron.Barrier;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class ZookeeperBarrier implements Barrier {
    private static final Logger log = LogManager.getLogger();

    private final Map<Test, DistributedDoubleBarrier> barriers = new ConcurrentHashMap<>();

    private final CuratorFramework cf;
    private final Duration awaitTime;
    private final int members;

    public ZookeeperBarrier(CuratorFramework cf, int members, Duration awaitTime) {
        this.cf = cf;
        this.members = members;
        this.awaitTime = awaitTime;
    }

    @Override
    public void enterCase(Test test) {
        try {
            log.trace("enterCase test: {}", test);
            getOrCreateBarrier(test)
                    .enter(awaitTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("enterCase failed. test: {}", test, e);
        }
    }

    @Override
    public void leaveCase(Test test) {
        try {
            log.trace("leaveCase test: {}", test);
            getOrCreateBarrier(test)
                    .leave(awaitTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("leaveCase failed. test: {}", test, e);
        }
    }

    private DistributedDoubleBarrier getOrCreateBarrier(Test test) {
        return barriers.computeIfAbsent(test, t -> new DistributedDoubleBarrier(cf, "/bomber/barriers/" + t.name(), members));
    }
}
