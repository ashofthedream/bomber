package ashes.of.bomber.carrier.config;

import ashes.of.bomber.Bomber;
import ashes.of.bomber.builder.BomberBuilder;
import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.carrier.config.properties.BomberProperties;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.function.Supplier;

@Configuration
//@ConditionalOnProperty("bomber")
public class BomberConfiguration {
    private static final Logger log = LogManager.getLogger();

    @Bean
    public Bomber bomberApp(BomberProperties properties) {
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

        var exampleA = new TestAppBuilder()
                .name("exampleA")
                .config(config -> config
                        .settings(settings -> settings
                                .setThreadsCount(8)
                                .setSeconds(30)
                                .setThreadIterationsCount(1_000_000)
                                .setTotalIterationsCount(1_000_000)
                        ))
                .createSuite(a -> a
                                .name("TestSuiteA")
                                .testCase("A_Sync", (suite, tools) -> {
                                    sleepQuietlyAround(10);
                                })
                                .asyncTestCase("A_Async", (suite, tools) -> {
                                    var stopwatch = tools.stopwatch("");
                                    sleepQuietlyAround(10);
                                    stopwatch.success();
                                })
                )
                .createSuite(b -> b
                                .name("TestSuiteB")
//                        .config(config -> config
//                                .settings(settings -> settings
//                                        .setThreadsCount(2)
//                                        .setSeconds(5)
//                                        .setThreadIterationsCount(20)
//                                        .setTotalIterationsCount(40)))
                                .testCase(tc -> tc
                                                .name("B_Sync")
//                                .config(config -> config
//                                        .settings(settings -> settings
//                                                .setThreadsCount(1)
//                                                .setSeconds(1)
//                                                .setThreadIterationsCount(50)
//                                                .setTotalIterationsCount(100)))
                                                .test((suite, tools) -> {
                                                    sleepQuietlyAround(10);
                                                })
                                )
                                .asyncTestCase("B_Async", (suite, tools) -> {
                                    var stopwatch = tools.stopwatch();
//                            var labeled = tools.stopwatch("Some Label");
//                            sleepQuietlyAround(70);
//                            labeled.success();
                                    sleepQuietlyAround(10);
                                    stopwatch.success();
                                })
                );

        builder.add(exampleA);

        return builder.build();
    }

    private static final Random random = new Random();

    public static void sleepQuietlyAround(long ms) {
        sleepQuietly(ms, 0.2);
    }

    public static void sleepQuietly(long ms, double sp) {
        try {
            double spread = ms * Math.max(0.0, Math.min(sp, 1.0));
            double timeout = ms + random.nextDouble() * spread * 2 - spread;

            Thread.sleep(Math.round(timeout));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
