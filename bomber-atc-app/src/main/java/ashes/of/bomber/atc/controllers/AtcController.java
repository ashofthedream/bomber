package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.records.UserRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/atc")
public class AtcController {

    @GetMapping("/users/current")
    public ResponseEntity<UserRecord> getCurrentUser(Authentication auth) {
        User principal = (User) auth.getPrincipal();
        UserRecord user = new UserRecord(principal.getUsername());
        return ResponseEntity.ok(user);
    }
}
