package ashes.of.bomber.carrier.starter.config;

import ashes.of.bomber.Bomber;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "ashes.of.bomber.carrier.starter")
public class CarrierConfiguration {
    private static final Logger log = LogManager.getLogger();

    @Autowired(required = false)
    public void configureSinks(Bomber bomber, List<Sink> sinks) {
        log.warn("CONFIGURE SINKS: {}", sinks);
        sinks.forEach(bomber::addSink);
    }

    @Autowired(required = false)
    public void configureWatchers(Bomber bomber, List<Watcher> watchers) {
        log.warn("CONFIGURE WATCHERS: {}", watchers);
        watchers.forEach(bomber::addWatcher);
    }
}
