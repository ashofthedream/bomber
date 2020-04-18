package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/atc")
public class AtcController {

    @GetMapping("/user")
    public ResponseEntity<UserDto> getCurrentUser(Authentication auth) {
        User principal = (User) auth.getPrincipal();
        UserDto dto = new UserDto();
        dto.setUsername(principal.getUsername());

        return ResponseEntity.ok(dto);
    }
}
