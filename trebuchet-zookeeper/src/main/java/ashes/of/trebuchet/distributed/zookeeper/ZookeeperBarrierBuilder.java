package ashes.of.trebuchet.distributed.zookeeper;

import com.google.common.base.Preconditions;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;

import java.time.Duration;

public class ZookeeperBarrierBuilder {

    private String url = "localhost:2181";
    private int members = 1;
    private Duration awaitTime = Duration.ofMinutes(1);


    public ZookeeperBarrierBuilder url(String url) {
        this.url = url;
        return this;
    }

    public ZookeeperBarrierBuilder members(int members) {
        this.members = members;
        return this;
    }

    public ZookeeperBarrierBuilder awaitTime(Duration awaitTime) {
        this.awaitTime = awaitTime;
        return this;
    }


    public ZookeeperBarrier build() {
        Preconditions.checkArgument(members > 0, "Members should be greater than 0");
        Preconditions.checkNotNull(awaitTime, "awaitTime should not be null");

        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(url)
                .retryPolicy(new RetryOneTime(1000))
                .build();

        cf.start();

        return new ZookeeperBarrier(cf, members, awaitTime);
    }
}
