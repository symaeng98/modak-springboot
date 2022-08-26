package com.modak.modakapp.exception.todo;

public class NoSuchTodoException extends RuntimeException {
    public NoSuchTodoException(String message) {
        super(message);
    }
}
