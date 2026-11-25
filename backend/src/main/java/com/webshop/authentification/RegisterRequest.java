package com.webshop.authentification;

import com.webshop.user.UserIdentity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = UserIdentity.MIN_USERNAME_LENGTH, max = UserIdentity.MAX_USERNAME_LENGTH,
                message = "Username must be between {min} and {max} characters")
        @Pattern(regexp = UserIdentity.USERNAME_PATTERN,
                message = "Username may contain only lowercase letters, digits, '.', '_' and '-'")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        @Size(max = UserIdentity.MAX_EMAIL_LENGTH)
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = PasswordPolicy.MIN_LENGTH, max = PasswordPolicy.MAX_LENGTH,
                message = "Password must be between {min} and {max} characters")
        String password
) {
    public RegisterRequest {
        username = UserIdentity.normalizeUsername(username);
        email = UserIdentity.normalizeEmail(email);
    }

    @Override
    public String toString() {
        return "RegisterRequest[username=" + username + ", email=" + email + ", password=****]";
    }
}