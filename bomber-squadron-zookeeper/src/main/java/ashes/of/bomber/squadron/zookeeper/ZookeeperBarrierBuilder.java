package ashes.of.bomber.squadron.zookeeper;

import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.squadron.BarrierBuilder;
import com.google.common.base.Preconditions;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;

import java.time.Duration;


public class ZookeeperBarrierBuilder extends BarrierBuilder {

    private String url = "localhost:2181";
    private int nodes = 1;
    private Duration awaitTime = Duration.ofMinutes(1);


    public ZookeeperBarrierBuilder url(String url) {
        this.url = url;
        return this;
    }

    public ZookeeperBarrierBuilder members(int nodes) {
        this.nodes = nodes;
        return this;
    }

    public ZookeeperBarrierBuilder awaitTime(Duration awaitTime) {
        this.awaitTime = awaitTime;
        return this;
    }


    public Barrier build() {
        Preconditions.checkArgument(nodes > 0, "Nodes should be greater than 0");
        Preconditions.checkNotNull(awaitTime, "awaitTime should not be null");

        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(url)
                .retryPolicy(new RetryOneTime(1000))
                .build();

        cf.start();

        return new LocalDelegateBarrier(workers, new ZookeeperBarrier(cf, nodes, awaitTime));
    }
}
