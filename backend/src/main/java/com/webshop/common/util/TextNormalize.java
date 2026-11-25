package com.webshop.common.util;

public final class TextNormalize {
    public static String normalize(String name) {
        return (name == null ? null : name.strip().replaceAll("\\s+", " "));
    }
}
