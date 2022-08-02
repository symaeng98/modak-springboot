package com.modak.modakapp.exception.token;

public class ExpiredRefreshTokenException extends RuntimeException{
    public ExpiredRefreshTokenException(String message){
        super(message);
    }
}
