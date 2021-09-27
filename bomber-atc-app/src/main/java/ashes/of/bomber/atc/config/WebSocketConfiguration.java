package ashes.of.bomber.atc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.Map;

@Configuration
public class WebSocketConfiguration {

    @Bean
    public HandlerMapping handlerMapping(WebSocketHandler handler) {
        var handlers = Map.of("/atc/socket", handler);
        return new SimpleUrlHandlerMapping(handlers, 0);
    }

}
