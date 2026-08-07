package com.webshop.authorization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Username cannot be empty")
    public String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email is invalid")
    public String email;

    @NotBlank(message = "Password cannot be empty")
    public String password;
}

