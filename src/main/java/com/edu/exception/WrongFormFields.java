package com.edu.exception;

public class WrongFormFields extends RuntimeException{
    public WrongFormFields(String message) {
        super(message);
    }
}
