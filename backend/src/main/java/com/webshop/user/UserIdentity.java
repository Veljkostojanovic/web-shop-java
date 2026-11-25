package com.webshop.user;

import com.webshop.common.exceptions.DomainValidationException;

import java.util.Locale;
import java.util.regex.Pattern;

public final class UserIdentity {
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 30;
    public static final int MAX_EMAIL_LENGTH = 254;
    public static final String USERNAME_PATTERN = "^[a-z0-9](?:[a-z0-9._-]*[a-z0-9])?$";
    private static final Pattern USERNAME_REGEX = Pattern.compile(USERNAME_PATTERN);

    private UserIdentity() {
    }

    public static String normalizeUsername(String username) {
        return username == null ? null : username.strip().toLowerCase(Locale.ROOT);
    }

    public static String normalizeEmail(String email) {
        return email == null ? null : email.strip().toLowerCase(Locale.ROOT);
    }

    public static String requireValidUsername(String raw) {
        String username = normalizeUsername(raw);
        if (username == null
                || username.length() < MIN_USERNAME_LENGTH
                || username.length() > MAX_USERNAME_LENGTH
                || !USERNAME_REGEX.matcher(username).matches()) {
            throw new DomainValidationException(
                    "Username must be " + MIN_USERNAME_LENGTH + "-" + MAX_USERNAME_LENGTH
                            + " characters: lowercase letters, digits, '.', '_' or '-'");
        }
        return username;
    }

    public static String requireValidEmail(String raw) {
        String email = normalizeEmail(raw);
        if (email == null || email.length() > MAX_EMAIL_LENGTH || email.indexOf('@') < 1) {
            throw new DomainValidationException("A valid email is required");
        }
        return email;
    }
}
