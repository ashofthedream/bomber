package ashes.of.bomber.example.carrier.configuration;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.descriptions.TestAppDescription;
import ashes.of.bomber.example.app.ExampleAnnotatedTestApp;
import ashes.of.bomber.sink.histogram.HistogramSink;
import ashes.of.bomber.sink.histogram.HistogramTimelineSink;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.squadron.NoBarrier;
import ashes.of.bomber.squadron.zookeeper.ZookeeperBarrierBuilder;
import ashes.of.bomber.watcher.Log4jWatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.temporal.ChronoUnit;

@Configuration
public class BobmerAppConfiguration {

    @Bean
    public TestAppDescription bomberApp(@Value("${bomber.target.url}") String url,
                                        @Value("${bomber.squadron.members}") int members) {

        WebClient testClient = WebClient.builder()
                .baseUrl(url)
                .build();

        BarrierBuilder barrier = members > 1 ? new ZookeeperBarrierBuilder()
                .members(members) : new NoBarrier.Builder();

        return TestAppBuilder.create(ExampleAnnotatedTestApp.class)
                // log all times to console via log4j and HdrHistogram
//                .sink(new Log4jSink())
                .sink(new HistogramTimelineSink(ChronoUnit.SECONDS, System.out))
                .sink(new HistogramSink())
                .barrier(barrier)
                .watcher(1000, new Log4jWatcher())
                .build();
    }
}
