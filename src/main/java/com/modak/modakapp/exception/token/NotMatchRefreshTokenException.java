package com.modak.modakapp.exception.token;

public class NotMatchRefreshTokenException extends RuntimeException {
    public NotMatchRefreshTokenException(String message) {
        super(message);
    }
}