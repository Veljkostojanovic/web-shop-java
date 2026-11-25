package com.webshop.common.persistence;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.Locale;

public final class ConstraintViolations {

    private ConstraintViolations() {
    }

    public static boolean isViolationOf(DataIntegrityViolationException e, String constraintPrefix) {
        String prefix = constraintPrefix.toLowerCase(Locale.ROOT);
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof org.hibernate.exception.ConstraintViolationException cve) {
                String name = cve.getConstraintName();
                return name != null && name.toLowerCase(Locale.ROOT).startsWith(prefix);
            }
        }
        return false;
    }
}