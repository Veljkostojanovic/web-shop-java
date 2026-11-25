package com.webshop.category;

import java.time.Instant;

public record CategoryAdminResponse(
        Long id,
        String name,
        CategoryStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
