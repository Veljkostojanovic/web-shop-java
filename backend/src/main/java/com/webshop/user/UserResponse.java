package com.webshop.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    public Long id;
    public String username;
    public String email;
    public String role;
}
