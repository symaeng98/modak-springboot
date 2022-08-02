package com.modak.modakapp.exception.token;

public class ExpiredAccessTokenException extends RuntimeException{
    public ExpiredAccessTokenException(String message){
        super(message);
    }
}
