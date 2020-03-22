package ashes.of.bomber.dispatcher.app.config;

import ashes.of.bomber.core.Application;
import ashes.of.bomber.dispatcher.app.EmptyApp;
import ashes.of.bomber.dispatcher.starter.config.DispatcherConfig;
import ashes.of.bomber.dispatcher.starter.controllers.DispatcherController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
public class ApplicationConfig {

    @Bean
    public Application application() {
        return new EmptyApp();
    }
}
