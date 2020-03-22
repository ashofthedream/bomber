package ashes.of.bomber.dispatcher.starter.controllers;

import ashes.of.bomber.dispatcher.Dispatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DispatcherController {
    private static final Logger log = LogManager.getLogger();

    private final Dispatcher dispatcher;

    public DispatcherController(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @PostMapping("/dispatcher/run")
    public ResponseEntity<?> run() {
        log.info("run");
        this.dispatcher.getApplication()
                .runAsync();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/dispatcher/shutdown")
    public ResponseEntity<?> shutdown() {
        log.info("shutdown");
        this.dispatcher.getApplication().shutdown();

        return ResponseEntity.ok().build();
    }
}
