package ashes.of.bomber.atc.config;

import ashes.of.bomber.atc.config.properties.ZookeeperProperties;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ZookeeperConfiguration {

    @Bean
    public CuratorFramework curatorFramework(ZookeeperProperties properties) {
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(properties.getURL())
                .retryPolicy(new RetryOneTime(30_000))
                .build();

        cf.start();

        return cf;
    }
}
