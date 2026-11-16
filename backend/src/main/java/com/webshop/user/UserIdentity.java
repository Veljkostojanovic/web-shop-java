package com.webshop.user;

import java.util.Locale;

public final class UserIdentity {

    public static String normalizeUsername(String username){

        return username == null ? null : username.strip().toLowerCase(Locale.ROOT);
    }

    public static String normalizeEmail(String email){
        return email == null ? null : email.strip().toLowerCase(Locale.ROOT);
    }
}
