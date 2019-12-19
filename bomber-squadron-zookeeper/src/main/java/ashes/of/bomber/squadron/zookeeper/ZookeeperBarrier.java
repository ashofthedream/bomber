package ashes.of.bomber.squadron.zookeeper;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.core.Stage;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;

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
    public void init(String name, Settings settings) {
        byte[] data = String.format("{\"name\": \"%s\"}", name).getBytes();
        try {
            PersistentNode node = new PersistentNode(cf, CreateMode.EPHEMERAL, false, "/bomber/instances/" + name, data);
            node.start();
            this.node = node;
        } catch (Exception e) {
            log.error("Can't init", e);
        }
    }

    @Override
    public void stageStart(Stage stage) {

    }

    @Override
    public void testStart(String test) {
        try {
            log.trace("test: {} testStart", test);
            getOrCreateBarrier(test).enter(awaitTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Can't start test", e);
        }
    }

    @Override
    public void testFinish(String test) {
        try {
            log.trace("test: {} testFinish", test);
            getOrCreateBarrier(test).leave(awaitTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Can't finish test", e);
        }
    }

    @Override
    public void stageLeave(Stage stage) {

    }

    private DistributedDoubleBarrier getOrCreateBarrier(String test) {
        return barriers.computeIfAbsent(test, k -> new DistributedDoubleBarrier(cf, "/bomber/barriers/" + test, members));
    }
}
