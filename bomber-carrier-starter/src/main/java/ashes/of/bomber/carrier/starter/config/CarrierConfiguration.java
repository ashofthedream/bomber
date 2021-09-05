package ashes.of.bomber.carrier.starter.config;

import ashes.of.bomber.runner.TestApp;
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

    @Autowired
    public void configureSinks(TestApp app, List<Sink> sinks) {
        log.warn("CONFIGURE SINKS: {}", sinks);
        sinks.forEach(app::add);
    }

    @Autowired
    public void configureWatchers(TestApp app, List<Watcher> watchers) {
        log.warn("CONFIGURE WATCHERS: {}", watchers);
        watchers.forEach(watcher -> app.add(1000, watcher));
    }
}
