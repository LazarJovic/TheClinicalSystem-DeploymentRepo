package com.example.clinicalCenter.exception;

public class WrongStatusException extends Exception {

    public WrongStatusException() {
    }

    public WrongStatusException(String message) {
        super(message);
    }

}
