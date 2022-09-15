package com.modak.modakapp.exception.todaytalk;

public class AlreadyExistsTodayTalkException extends RuntimeException {
    public AlreadyExistsTodayTalkException(String message) {
        super(message);
    }
}