package ashes.of.bomber.example.gateway;

import ashes.of.bomber.example.accounts.client.AccountClient;
import ashes.of.bomber.example.users.client.UsersClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ExampleApp {

    @Bean
    public AccountClient accountClient(@Value("${example.accounts.url}") String url) {
        return new AccountClient(url);
    }

    @Bean
    public UsersClient usersClient(@Value("${example.users.url}") String url) {
        return new UsersClient(url);
    }

    public static void main(String... args) {
        SpringApplication.run(ExampleApp.class, args);
    }
}
