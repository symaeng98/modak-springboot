package com.modak.modakapp.exception;

public class NotAuthorizedMemberException extends RuntimeException{
    public NotAuthorizedMemberException(String message) {
        super(message);
    }
}
