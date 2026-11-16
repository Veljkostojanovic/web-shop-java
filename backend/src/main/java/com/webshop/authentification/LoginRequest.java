package com.webshop.authentification;

import com.webshop.user.UserIdentity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest (
    @NotBlank(message = "Username is required")
    @Size(max = 255)
    String username,

    @NotBlank(message = "Passowrd is required")
    @Size(max = 255)
    String password
){
    public LoginRequest{
        username = UserIdentity.normalizeUsername(username);
    }
}
