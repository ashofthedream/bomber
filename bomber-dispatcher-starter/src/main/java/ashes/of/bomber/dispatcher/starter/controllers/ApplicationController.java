package ashes.of.bomber.dispatcher.starter.controllers;

import ashes.of.bomber.core.BomberApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/application")
public class ApplicationController {
    private static final Logger log = LogManager.getLogger();

    private final BomberApp app;

    public ApplicationController(BomberApp app) {
        this.app = app;
    }

    @PostMapping("/run")
    public ResponseEntity<?> run() {
        log.info("run");
        this.app.runAsync();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/shutdown")
    public ResponseEntity<?> shutdown() {
        log.info("shutdown");
        this.app.shutdown();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/state")
    public ResponseEntity<?> getState() {
        log.info("getState");

        return ResponseEntity.ok().build();
    }
}
