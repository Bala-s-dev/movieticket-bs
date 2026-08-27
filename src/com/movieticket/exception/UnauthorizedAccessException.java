package com.movieticket.exception;

public class UnauthorizedAccessException extends ApplicationException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
