package ashes.of.trebuchet.distributed.zookeeper;

import ashes.of.trebuchet.distibuted.Barrier;
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

    private final Map<String, DistributedDoubleBarrier> barriers = new ConcurrentHashMap<>();

    private final CuratorFramework cf;
    private final Duration awaitTime;
    private final int members;


    public ZookeeperBarrier(CuratorFramework cf, int members, Duration awaitTime) {
        this.cf = cf;
        this.members = members;
        this.awaitTime = awaitTime;
    }

    @Override
    public void enter(String test) {
        try {
            getOrCreateBarrier(test).enter(awaitTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Can't enter", e);
        }
    }

    @Override
    public void leave(String test) {
        try {
            getOrCreateBarrier(test).leave(awaitTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Can't leave", e);
        }
    }


    private DistributedDoubleBarrier getOrCreateBarrier(String test) {
        return barriers.computeIfAbsent(test, k -> new DistributedDoubleBarrier(cf, "/trebuchet/barriers/" + test, members));
    }
}
