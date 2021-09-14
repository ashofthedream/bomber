package ashes.of.bomber.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleApp {

//    @Bean
//    public AccountClient accountClient(@Value("${example.accounts.url}") String url) {
//        return new AccountClient(url);
//    }
//
//    @Bean
//    public UsersClient usersClient(@Value("${example.users.url}") String url) {
//        return new UsersClient(url);
//    }

    public static void main(String... args) {
        SpringApplication.run(ExampleApp.class, args);
    }
}
