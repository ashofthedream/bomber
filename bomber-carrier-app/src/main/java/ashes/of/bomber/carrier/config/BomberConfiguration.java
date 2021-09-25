package ashes.of.bomber.carrier.config;

import ashes.of.bomber.Bomber;
import ashes.of.bomber.builder.BomberBuilder;
import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.carrier.config.properties.BomberProperties;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

@Configuration
//@ConditionalOnProperty("bomber")
public class BomberConfiguration {
    private static final Logger log = LogManager.getLogger();

    @Bean
    public Bomber bomberApp(BomberProperties properties,
                            @Value("${env.target.url}") String url,
                            @Value("${env.squadron.members}") int members) {

        log.debug("Create bomber with properties: {}", properties);

        var builder = new BomberBuilder();


        properties.getSinks().forEach(name -> {
            log.debug("Found sink: {}", name);
            try {
                Class<?> cls = Class.forName(name);
                Constructor<?> constructor = cls.getConstructor();
                builder.sink((Sink) constructor.newInstance());
            } catch (Throwable e) {
                log.warn("No class found for sink: {} or some other bad thing happened, skip it", name, e);
            }
        });

        properties.getWatchers().forEach(name -> {
            log.debug("Found watcher: {}", name);
            try {
                Class<?> cls = Class.forName(name);
                Constructor<?> constructor = cls.getConstructor();
                builder.watcher((Watcher) constructor.newInstance());
            } catch (Throwable e) {
                log.warn("No class found for watcher: {} or some other bad thing happened, skip it", name, e);
            }
        });

        properties.getApps().getInclude().forEach(name -> {
            log.debug("Found app to include: {}", name);
            try {
                Class<?> cls = Class.forName(name);
                builder.add(cls);
            } catch (Throwable e) {
                log.warn("No class found for app: {}, skip it", name, e);
            }
        });

        properties.getApps().getBuild().forEach(name -> {
            log.debug("Found app to build: {}", name);

            try {
                Class<?> cls = Class.forName(name);
                Constructor<?> constructor = cls.getConstructor();
                var supplier = (Supplier<TestAppBuilder>) constructor.newInstance();
                builder.add(supplier.get());
            } catch (Throwable e) {
                log.warn("No class found for app: {} or some other bad thing happened, skip it", name, e);
            }
        });

        return builder.build();
    }
}
