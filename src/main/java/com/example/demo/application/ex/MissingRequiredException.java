package com.example.demo.application.ex;

public class MissingRequiredException extends RuntimeException {
    public MissingRequiredException(String message) {
        super(message);
    }
}
