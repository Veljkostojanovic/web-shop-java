package com.webshop.authentification;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresIn
) {
}