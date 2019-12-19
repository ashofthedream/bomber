//package ashes.of.bomber.example.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
//
//@Configuration
//public class SecurityConfiguration {
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationWebFilter authFilter) {
//        return http
//                .authorizeExchange()
//                .anyExchange().permitAll()
//                .and()
//                .build();
//    }
//}
