package com.webshop.common.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ErrorResponse{
    int status;
    String message;
    long timestamp;
    Map<String, String> errors;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        timestamp = System.currentTimeMillis();
        errors = null;
    }
}
