package ashes.of.bomber.example.carrier.configuration;

import ashes.of.bomber.Bomber;
import ashes.of.bomber.example.tests.ExampleTestApp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BomberAppConfiguration {

    @Bean
    public Bomber bomberApp(@Value("${bomber.target.url}") String url,
                            @Value("${bomber.squadron.members}") int members) {

        return ExampleTestApp.create(url, members);
    }
}
