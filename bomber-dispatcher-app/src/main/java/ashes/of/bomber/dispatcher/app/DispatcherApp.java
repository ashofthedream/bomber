package ashes.of.bomber.dispatcher.app;

import ashes.of.bomber.core.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DispatcherApp {
    private static final Logger log = LogManager.getLogger();

    public static void main(String... args) {
        ConfigurableApplicationContext app = new SpringApplicationBuilder()
                .sources(DispatcherApp.class)
                .initializers(context -> context.getBeanFactory().registerResolvableDependency(Application.class, new EmptyApp()))
                .run(args);
    }
}
