package com.gestor_empresarial.api_gateway.exceptions;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
