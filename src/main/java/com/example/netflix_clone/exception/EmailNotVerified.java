package com.example.netflix_clone.exception;

public class EmailNotVerified extends RuntimeException {
    public EmailNotVerified(String message) {
        super(message);
    }
}
