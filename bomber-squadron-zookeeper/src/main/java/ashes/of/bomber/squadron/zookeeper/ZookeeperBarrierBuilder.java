package ashes.of.bomber.squadron.zookeeper;

import ashes.of.bomber.configuration.Builder;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.squadron.LocalCascadeBarrier;
import com.google.common.base.Preconditions;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;

import java.time.Duration;

public class ZookeeperBarrierBuilder implements Builder<Barrier> {

    private String url = "localhost:2181";
    private Duration wait = Duration.ofMinutes(1);
    private RetryPolicy retry = new RetryOneTime(1000);


    public ZookeeperBarrierBuilder url(String url) {
        this.url = url;
        return this;
    }

    public ZookeeperBarrierBuilder retry(RetryPolicy retry) {
        this.retry = retry;
        return this;
    }

    public ZookeeperBarrierBuilder wait(Duration wait) {
        this.wait = wait;
        return this;
    }

    @Override
    public Barrier build() {
        Preconditions.checkNotNull(retry, "retry is null");
        Preconditions.checkNotNull(wait, "wait is null");

        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(url)
                .retryPolicy(new RetryOneTime(1000))
                .build();

        cf.start();

        return new LocalCascadeBarrier(new ZookeeperBarrier(cf, wait));
    }
}
