package com.edu.exception;

public class WrongRateInExchangeRate extends RuntimeException{
    public WrongRateInExchangeRate(String message) {
        super(message);
    }
}
