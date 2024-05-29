package com.edu.exception;

public class WrongCurrencyCode extends RuntimeException{
    public WrongCurrencyCode(String message) {
        super(message);
    }
}
