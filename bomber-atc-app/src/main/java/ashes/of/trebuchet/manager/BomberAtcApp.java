package ashes.of.trebuchet.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BomberAtcApp {
    private static final Logger log = LogManager.getLogger();

    public static void main(String... args) {
        SpringApplication.run(BomberAtcApp.class, args);
    }
}
