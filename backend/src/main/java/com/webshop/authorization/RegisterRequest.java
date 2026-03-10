package com.webshop.authorization;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username cannot be empty")
    public String username;

    @NotBlank(message = "Email cannot be empty")
    public String email;

    @NotBlank(message = "Password cannot be empty")
    public String password;
}

