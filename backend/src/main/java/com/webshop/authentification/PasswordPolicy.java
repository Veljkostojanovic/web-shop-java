package com.webshop.authentification;

import com.webshop.common.exceptions.DomainValidationException;

import java.nio.charset.StandardCharsets;

public final class PasswordPolicy {

    public static final int MIN_LENGTH = 10;
    public static final int MAX_LENGTH = 64;
    private static final int BCRYPT_MAX_BYTES = 72;

    private PasswordPolicy() {
    }

    public static void requireValid(String password) {
        if (password == null) {
            throw new DomainValidationException("Password is required");
        }
        int length = password.codePointCount(0, password.length());
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new DomainValidationException(
                    "Password must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
        }
        if (exceedsBcryptLimit(password)) {
            throw new DomainValidationException(
                    "Password is too long: non-English characters and emoji take more space");
        }
    }

    public static boolean exceedsBcryptLimit(String password) {
        return password != null && password.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_BYTES;
    }
}