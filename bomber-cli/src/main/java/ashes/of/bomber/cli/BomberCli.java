package ashes.of.bomber.cli;

import ashes.of.bomber.Bomber;
import ashes.of.bomber.builder.BomberBuilder;
import ashes.of.bomber.builder.ConfigurationBuilder;
import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.builder.TestCaseBuilder;
import ashes.of.bomber.cli.config.BomberConfig;
import ashes.of.bomber.cli.config.ConfigurationConfig;
import ashes.of.bomber.cli.config.HttpRequestConfig;
import ashes.of.bomber.cli.config.SettingsConfig;
import ashes.of.bomber.cli.config.TestCaseConfig;
import ashes.of.bomber.builder.SettingsBuilder;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.utils.Sleep;
import ashes.of.bomber.watcher.Watcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;

public class BomberCli implements Runnable {
    private static final Logger log = LogManager.getLogger();

    @Option(names = { "-f", "--file" }, description = "Configuration file (yaml, json)")
    String file = "";


    @Option(names = { "-t", "--threads" }, description = "Threads count")
    Integer threadsCount;

    @Option(names = { "-i", "--iterations" }, description = "Iteration count")
    Long iterationsCount;

    @Option(names = { "-s", "--seconds" }, description = "Duration in seconds")
    Long seconds;

    @Parameters(index = "0..*", arity = "0..2")
    List<String> params;

    @Override
    public void run() {
        var config = createBomberConfig();
        var bomber = createBomber(config);
        var report = bomber.start();
        report.testApps().forEach(ta ->
                ta.testSuites().forEach(ts -> {
                    ts.testCases().forEach(tc -> {
                        log.info("Report for {}.{}.{} is ready: total iterations {} over {}ms with {} errors",
                                ta.plan().name(), ts.name(), tc.name(),
                                tc.iterationsCount(),
                                tc.totalTimeElapsed(),
                                tc.errorsCount());

                    });
                })
        );

        Sleep.sleepQuietlyExact(1000);
    }

    private BomberConfig createBomberConfig() {
        if (file != null) {
            return createBomberConfigFromFile();
        }
        else {
            return createBomberConfigFromOptions();
        }
    }

    private BomberConfig createBomberConfigFromFile() {
        log.info("Config file provided");
        try {
            var f = new File(file);
            var mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(f, BomberConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Can't read config from file", e);
        }
    }

    private BomberConfig createBomberConfigFromOptions() {
        var sinks = List.of(
                "ashes.of.bomber.sink.histogram.HistogramTimelineSink"
        );

        var watchers = List.of();
        var settings = new SettingsConfig(threadsCount, seconds, iterationsCount);
        var configuration = new ConfigurationConfig(settings);

        if (params.size() < 2)
            throw new RuntimeException("please profile method, like GET or POST and URL");

        var http = new HttpRequestConfig(params.get(0), params.get(1), null);
        var testCases = List.of(
                new TestCaseConfig("params", false, null, http)
        );

        return new BomberConfig(sinks, List.of(), configuration, testCases);
    }

    private Bomber createBomber(BomberConfig config) {
        var app = new TestAppBuilder()
                .name("cli")
                .createSuite(suite -> {
                    suite.name("cli");

                    if (config.configuration() != null) {
                        suite.config(buildConfiguration(config.configuration()));
                    }

                    config.testCases().forEach(testCaseCfg -> {
                            suite.testCase(builder -> buildTestCase(builder, testCaseCfg));
                    });
                });

        var builder = new BomberBuilder()
                .add(app);

        if (config.sinks() != null) {
            config.sinks().forEach(name -> {
                log.debug("Found sink: {}", name);
                try {
                    Class<?> cls = Class.forName(name);
                    Constructor<?> constructor = cls.getConstructor();
                    builder.sink((Sink) constructor.newInstance());
                } catch (Throwable e) {
                    log.warn("No class found for sink: {} or some other bad thing happened, skip it", name, e);
                }
            });
        }

        if (config.watchers() != null) {
            config.watchers().forEach(name -> {
                log.debug("Found watcher: {}", name);
                try {
                    Class<?> cls = Class.forName(name);
                    Constructor<?> constructor = cls.getConstructor();
                    builder.watcher((Watcher) constructor.newInstance());
                } catch (Throwable e) {
                    log.warn("No class found for watcher: {} or some other bad thing happened, skip it", name, e);
                }
            });
        }

        return builder
                .build();
    }

    private ConfigurationBuilder buildConfiguration(ConfigurationConfig cfg) {
        var b = new ConfigurationBuilder();

        if (cfg.settings() != null)
            b.settings(buildSettings(cfg.settings()));

        return b;
    }

    private SettingsBuilder buildSettings(SettingsConfig cfg) {
        var b = new SettingsBuilder();
        if (cfg.threads() != null)
            b.setThreads(cfg.threads());

        if (cfg.seconds() != null)
            b.setSeconds(cfg.seconds());

        if (cfg.iterations() != null)
            b.setIterations(cfg.iterations());

        return b;
    }

    private void buildTestCase(TestCaseBuilder<?> testCase, TestCaseConfig config) {
        testCase.name(config.name())
                .async(config.async());

        if (config.configuration() != null) {
            testCase.config(buildConfiguration(config.configuration()));
        }

        var http = config.http();
        if (http != null) {
            WebClient webClient = WebClient.builder()
                    .build();

            var method = HttpMethod.resolve(http.method());
            Objects.requireNonNull(method, "invalid http method: " + http.method());

            testCase.test((context, tools) -> {
                var request = webClient.method(method)
                        .uri(http.url());
//                        .body(BodyInserters.fromValue(http.payload()));

                if (config.async()) {
                    var stopwatch = tools.stopwatch("async");
                    request.retrieve()
                            .toBodilessEntity()
                            .subscribe(entity -> stopwatch.success(), stopwatch::fail);
                }
                else {
                    request.retrieve()
                            .toBodilessEntity()
                            .block();
                }
            });
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new BomberCli()).execute(args);
        System.exit(exitCode);
    }
}
