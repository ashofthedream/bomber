package ashes.of.bomber.dispatcher.starter.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "ashes.of.bomber.dispatcher.starter")
public class DispatcherConfig {

}
