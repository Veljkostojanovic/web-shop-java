package com.webshop.authentification;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
    @NotBlank(message = "Username cannot be empty")
    String username,

    @NotBlank(message = "Passowrd cannot be empty")
    String password
){}
