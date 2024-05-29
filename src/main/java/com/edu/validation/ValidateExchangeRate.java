package com.edu.validation;

import com.edu.exception.WrongCurrencyCode;
import com.edu.exception.WrongFormFields;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

public class ValidateExchangeRate {

    public void validateExchangeRate(HttpServletRequest req) {
        var httpMethod = req.getMethod();
        if (httpMethod.equals("POS")) {
            var baseCurrencyCode =req.getParameter("baseCurrencyCode");
            var targetCurrencyCode = req.getParameter("targetCurrencyCode");
            var rate = req.getParameter("rate");

            validationNull(baseCurrencyCode, targetCurrencyCode, rate);
            var currencyCodes = baseCurrencyCode.concat(targetCurrencyCode);
            validationCurrencyCodes(currencyCodes, httpMethod);
            validationAlphabet(currencyCodes);

            validationEmptyRate(rate);
            validationNumber(rate);
            validationZeroOrNegative(rate);
        } else {
            var currencyCodes = req.getHttpServletMapping().getMatchValue();

            validationNull(currencyCodes);
            validationCurrencyCodes(currencyCodes, httpMethod);
            validationAlphabet(currencyCodes);
        }
    }

    public void validateRateFiled(String requestParameter) throws IOException {
        validationNull(requestParameter);

        String[] parameterRate = requestParameter.split("=");

        validationExistRate(parameterRate);

        var rate = replaceSpace(parameterRate[1]);

        validationEmptyRate(rate);
        validationNumber(rate);
        validationZeroOrNegative(rate);
    }


    private void validationCurrencyCodes(String CurrencyCodes, String httpMethod) {
        if (CurrencyCodes.trim().length() != 6) {
             if (httpMethod.equals("POST")) {
                    throw new WrongFormFields("Поля формы в теле запроса не корректны ");
             } else {
                    throw new WrongCurrencyCode("Не корректный коды валюты " + CurrencyCodes);
             }
        }
    }

    private void validationNumber(String rate) {
        for (char ch : rate.trim().toCharArray()) {
            if (Character.isAlphabetic(ch)) {
                throw new WrongFormFields("Не корректное значение: Rate ");
            }
        }
    }
    private void validationAlphabet(String CurrencyCodes) {
        char[] chars = CurrencyCodes.toCharArray();
        for (char ch : chars) {
            if (Character.isDigit(ch)) {
                throw new WrongFormFields("Поля формы в теле запроса не корректны ");
            }
        }
    }

    private void validationZeroOrNegative(String rate){
        if (Double.parseDouble(rate.trim()) <= 0)
            throw new WrongFormFields("Не корректное значение: Rate ");
    }

    private void validationNull(String requestParameter) {
        if (Objects.isNull(requestParameter) ) {
            throw new WrongFormFields("Не корректное значение rate ");
        }
    }
    private void validationNull(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        if (Objects.isNull(baseCurrencyCode) || Objects.isNull(targetCurrencyCode) || Objects.isNull(rate)) {
            throw new WrongFormFields("Поля формы в теле запроса не корректны ");
        }
    }

    private void validationExistRate(String[] parameterRate) {
        if (parameterRate.length != 2) {
            throw new WrongFormFields("Не корректное значение rate ");
        }
    }

    private void validationEmptyRate(String rate) {
        if (rate.isBlank()) {
            throw new WrongFormFields("Не корректное значение rate ");
        }
    }

    private String replaceSpace(String rate) {
        return rate.replaceAll("%20"," ");
    }
}
