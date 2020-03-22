package ashes.of.bomber.dispatcher.starter.config;

import ashes.of.bomber.core.Application;
import ashes.of.bomber.dispatcher.Dispatcher;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "ashes.of.bomber.dispatcher.starter")
public class DispatcherConfig {

    @Bean
    public Dispatcher dispatcher(Application application) {
        return new Dispatcher(application);
    }
}
