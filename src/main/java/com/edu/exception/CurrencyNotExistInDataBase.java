package com.edu.exception;

public class CurrencyNotExistInDataBase extends RuntimeException{
    public CurrencyNotExistInDataBase(String message) {
        super(message);
    }
}
