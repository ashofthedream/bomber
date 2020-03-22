package ashes.of.bomber.dispatcher.app.config;

import ashes.of.bomber.core.Application;
import ashes.of.bomber.dispatcher.Dispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DispatcherConfig {

    @Bean
    public Dispatcher dispatcher(Application application) {
        return new Dispatcher(application);
    }
}
