package com.webshop.authentification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank(message = "Username cannot be empty")
    String username,

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email is invalid")
    String email,

    @NotBlank(message = "Password cannot be empty")
    String password
){}

