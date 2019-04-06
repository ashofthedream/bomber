package ashes.of.trebuchet.manager.configuration;

import ashes.of.trebuchet.manager.auth.InMemoryAuthenticationManager;
import ashes.of.trebuchet.manager.dto.LoginRequest;
import ashes.of.trebuchet.manager.dto.ResponseEntities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@EnableWebFluxSecurity
public class SecurityConfiguration {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public AuthenticationWebFilter authenticationWebFilter(InMemoryAuthenticationManager manager) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(manager);
        filter.setServerAuthenticationConverter(this::authConverter);
        filter.setAuthenticationFailureHandler(this::onAuthenticationFailure);
        filter.setAuthenticationSuccessHandler(this::onAuthenticationSuccess);
        filter.setSecurityContextRepository(new WebSessionServerSecurityContextRepository());
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/manager/login")
        );

        return filter;
    }


    private Mono<Authentication> authConverter(ServerWebExchange exchange) {
        return exchange.getRequest().getBody()
                .next()
                .map(DataBuffer::asInputStream)
                .flatMap(is -> {
                    try {
                        LoginRequest req = objectMapper.readValue(is, LoginRequest.class);

                        return Mono.just(new UsernamePasswordAuthenticationToken(req.username, req.password));
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                });
    }


    private Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {
        return sendResponse(exchange, ResponseEntities.ok());
    }

    private Mono<Void> onAuthenticationFailure(WebFilterExchange exchange, AuthenticationException e) {
        return sendResponse(exchange, ResponseEntities.failed(e));
    }


    private Mono<Void> sendResponse(WebFilterExchange exchange, ResponseEntity<?> message) {
        ServerHttpResponse response = exchange.getExchange().getResponse();

        try {
            byte[] json = objectMapper.writeValueAsBytes(message);
            DataBuffer wrap = response.bufferFactory()
                    .wrap(json);

            Mono<DataBuffer> body = Mono.just(wrap);

            response.getHeaders().add("Content-Type", "application/json");
            return response.writeWith(body);
        } catch (JsonProcessingException e) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return response.setComplete();
        }
    }


    private Mono<Void> authenticationEntryPoint(ServerWebExchange exchange, AuthenticationException e) {
        return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(UNAUTHORIZED));
    }


    private Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        return Mono.fromRunnable(() -> exchange.getExchange().getResponse().setStatusCode(OK));
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationWebFilter authFilter) {
        return http
                .csrf().disable()

                .authorizeExchange()
                .pathMatchers("/manager/login").permitAll()
                .anyExchange().permitAll()

                .and()

                .exceptionHandling().authenticationEntryPoint(this::authenticationEntryPoint)
                .and()

                .httpBasic().disable()
                .formLogin().disable()

                .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(this::onLogoutSuccess)

                .and()
                .build();
    }
}
