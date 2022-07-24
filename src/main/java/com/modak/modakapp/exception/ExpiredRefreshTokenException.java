package com.modak.modakapp.exception;

public class ExpiredRefreshTokenException extends RuntimeException{
    public ExpiredRefreshTokenException(String message){
        super(message);
    }
}
