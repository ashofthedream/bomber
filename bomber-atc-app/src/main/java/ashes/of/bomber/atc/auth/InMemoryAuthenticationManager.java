package ashes.of.bomber.atc.auth;

import ashes.of.bomber.atc.config.properties.SecurityProperties;
import ashes.of.bomber.atc.model.User;
import com.google.common.collect.ImmutableList;
import org.apache.curator.shaded.com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class InMemoryAuthenticationManager implements ReactiveAuthenticationManager {
    private static final Logger log = LogManager.getLogger();

    private final SecurityProperties properties;
    private final Map<String, User> users;

    public InMemoryAuthenticationManager(SecurityProperties properties) {
        this.properties = properties;
        this.users = ImmutableMap.<String, User>builder()
                .put(properties.getUser(), new User(properties.getUser(), properties.getPassword()))
                .build();
    }

    @Override
    public Mono<Authentication> authenticate(Authentication auth) {
        User user = users.get(auth.getPrincipal().toString());

        if (!Objects.equals(user.getPassword(), auth.getCredentials().toString()))
            throw new BadCredentialsException("Invalid user");

        log.debug("return user: {}", user.getUsername());
        List<GrantedAuthority> authorities = ImmutableList.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return Mono.just(new UsernamePasswordAuthenticationToken(user, null, authorities));
    }
}
