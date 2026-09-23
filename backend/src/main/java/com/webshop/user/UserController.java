package com.webshop.user;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findUserByUsername(username);
    }
}
