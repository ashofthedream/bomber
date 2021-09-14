package ashes.of.bomber.example.carrier.configuration;

import ashes.of.bomber.Bomber;
import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.example.accounts.ExampleAccountsTestApp;
import ashes.of.bomber.example.users.ExampleUsersTestApp;
import ashes.of.bomber.sink.histogram.HistogramTimelinePrintStreamPrinter;
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
public class BomberAppConfiguration {

    @Bean
    public Bomber bomberApp(@Value("${bomber.target.url}") String url,
                            @Value("${bomber.squadron.members}") int members) {

        WebClient testClient = WebClient.builder()
                .baseUrl(url)
                .build();

        BarrierBuilder barrier = members > 1 ? new ZookeeperBarrierBuilder()
                .members(members) : new NoBarrier.Builder();

        return new Bomber()
                .add(TestAppBuilder.create(ExampleUsersTestApp.class)
                        .provide(WebClient.class, () -> testClient)
                        .config(config -> config.barrier(barrier))
                )
                .add(TestAppBuilder.create(ExampleAccountsTestApp.class)
                        .provide(WebClient.class, () -> testClient)
                        .config(config -> config.barrier(barrier))
                )
                .addSink(new HistogramTimelineSink(ChronoUnit.SECONDS, new HistogramTimelinePrintStreamPrinter()))
                .addWatcher(new Log4jWatcher());
    }
}
