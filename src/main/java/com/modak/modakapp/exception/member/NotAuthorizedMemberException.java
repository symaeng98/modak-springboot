package com.modak.modakapp.exception.member;

public class NotAuthorizedMemberException extends RuntimeException{
    public NotAuthorizedMemberException(String message) {
        super(message);
    }
}
