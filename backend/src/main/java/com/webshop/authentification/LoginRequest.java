package com.webshop.authentification;

import com.webshop.user.UserIdentity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Username is required")
        @Size(max = UserIdentity.MAX_USERNAME_LENGTH)
        String username,

        @NotBlank(message = "Password is required")
        @Size(max = PasswordPolicy.MAX_LENGTH)
        String password
) {
    public LoginRequest {
        username = UserIdentity.normalizeUsername(username);
    }

    @Override
    public String toString() {
        return "LoginRequest[username=" + username + ", password=****]";
    }
}