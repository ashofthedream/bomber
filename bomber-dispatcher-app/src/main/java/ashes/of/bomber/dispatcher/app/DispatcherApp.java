package ashes.of.bomber.dispatcher.app;

import ashes.of.bomber.core.Application;
import ashes.of.bomber.dispatcher.starter.config.DispatcherConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(DispatcherConfig.class)
public class DispatcherApp {
    private static final Logger log = LogManager.getLogger();

    @Bean
    public Application application() {
        return new EmptyApp();
    }

    public static void main(String... args) {
        SpringApplication.run(DispatcherApp.class, args);
    }
}
