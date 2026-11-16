package com.webshop.authentification;

import com.webshop.user.UserIdentity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Username is required")
    @Size(max = 255)
    String username,

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    @Size(max = 255)
    String email,

    @NotBlank(message = "Password cannot be empty")
    @Size(max = 255)
    String password
){
    public RegisterRequest{
        username = UserIdentity.normalizeUsername(username);
        email = UserIdentity.normalizeEmail(email);
    }
}

