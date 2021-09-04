package ashes.of.bomber.atc.config;

import ashes.of.bomber.atc.config.properties.SecurityProperties;
import ashes.of.bomber.atc.dto.ResponseEntities;
import ashes.of.bomber.atc.dto.UserDto;
import ashes.of.bomber.atc.dto.requests.LoginRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.UncheckedIOException;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@EnableWebFluxSecurity
public class SecurityConfiguration {
    private static final Logger log = LogManager.getLogger();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public AuthenticationWebFilter authenticationWebFilter(ReactiveUserDetailsService userDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager manager
                = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);

        AuthenticationWebFilter filter = new AuthenticationWebFilter(manager);
        filter.setServerAuthenticationConverter(this::authConverter);
        filter.setAuthenticationFailureHandler(this::onAuthenticationFailure);
        filter.setAuthenticationSuccessHandler(this::onAuthenticationSuccess);
        filter.setSecurityContextRepository(new WebSessionServerSecurityContextRepository());
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/atc/login")
        );

        return filter;
    }


    private Mono<Authentication> authConverter(ServerWebExchange exchange) {
        return exchange.getRequest().getBody()
                .next()
                .map(DataBuffer::asInputStream)
                .map(is -> {
                    try {
                        LoginRequest req = objectMapper.readValue(is, LoginRequest.class);

                        return new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }


    private Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        UserDto dto = new UserDto(principal.getUsername());
        return sendResponse(exchange, ResponseEntities.ok(dto));
    }

    private Mono<Void> onAuthenticationFailure(WebFilterExchange exchange, AuthenticationException e) {
        return sendResponse(exchange, ResponseEntities.failed(e));
    }


    private Mono<Void> sendResponse(WebFilterExchange exchange, ResponseEntity<?> message) {
        ServerHttpResponse response = exchange.getExchange().getResponse();

        try {
            byte[] json = objectMapper.writeValueAsBytes(message.getBody());
            DataBuffer wrap = response.bufferFactory()
                    .wrap(json);

            Mono<DataBuffer> body = Mono.just(wrap);

            response.getHeaders().add("Content-Type", "application/json");
            return response.writeWith(body);
        } catch (JsonProcessingException e) {
            log.error("Can't send response", e);
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
    public MapReactiveUserDetailsService userDetailsService(PasswordEncoder passwordEncoder, SecurityProperties properties) {
        UserDetails user = User
                .withUsername(properties.getUsername())
                .password(passwordEncoder.encode(properties.getPassword()))
                .roles("ADMIN")
                .build();

        return new MapReactiveUserDetailsService(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationWebFilter authFilter) {
        return http
                .csrf().disable()

                .authorizeExchange()
                .pathMatchers("/atc/login").permitAll()
                .anyExchange().authenticated()

                .and()

                .exceptionHandling().authenticationEntryPoint(this::authenticationEntryPoint)
                .and()
                .httpBasic().disable()
                .formLogin().disable()

                .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .logout()
                .logoutUrl("/atc/logout")
                .logoutSuccessHandler(this::onLogoutSuccess)

                .and()
                .build();
    }
}
