package com.modak.modakapp.exception;

public class ExpiredAccessTokenException extends RuntimeException{
    public ExpiredAccessTokenException(String message){
        super(message);
    }
}
