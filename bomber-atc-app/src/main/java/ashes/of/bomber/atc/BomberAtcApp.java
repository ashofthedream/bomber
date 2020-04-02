package ashes.of.bomber.atc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class BomberAtcApp {

    public static void main(String... args) {
        SpringApplication.run(BomberAtcApp.class, args);
    }
}
