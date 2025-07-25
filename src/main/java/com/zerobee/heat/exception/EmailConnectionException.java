package com.zerobee.heat.exception;

public class EmailConnectionException extends RuntimeException {
    public EmailConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}