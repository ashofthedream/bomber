package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AtcController {

    @GetMapping("/atc/users/current")
    public ResponseEntity<UserDto> getCurrentUser(Authentication auth) {
        var principal = (User) auth.getPrincipal();
        UserDto userDto = new UserDto(principal.getUsername());
        return ResponseEntity.ok(userDto);
    }
}
