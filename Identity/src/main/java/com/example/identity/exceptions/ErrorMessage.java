package com.example.identity.exceptions;

import java.time.LocalDateTime;

public class ErrorMessage {

    public String error;
    public String message;
    public LocalDateTime timeStamp;

    public ErrorMessage(String error, String message, LocalDateTime timeStamp) {
        this.error = error;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}