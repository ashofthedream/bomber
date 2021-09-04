package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AtcController {

    @GetMapping("/atc/users/current")
    public ResponseEntity<User> getCurrentUser(Authentication auth) {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        User user = new User(principal.getUsername());
        return ResponseEntity.ok(user);
    }
}
