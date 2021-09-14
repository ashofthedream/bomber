package ashes.of.bomber.example.users;

import ashes.of.bomber.example.clients.AccountClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ExampleUsersApp {

    @Bean
    public AccountClient accountClient(@Value("${example.accounts.url}") String url) {
        return new AccountClient(url);
    }

    public static void main(String... args) {
        SpringApplication.run(ExampleUsersApp.class, args);
    }
}
