package com.edu.validation;

import com.edu.exception.WrongCurrencyCode;
import com.edu.exception.WrongFormFields;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

public class ValidationCurrencies {
    public void validationCurrency(HttpServletRequest req) {
        var httpMethod = req.getMethod();

        if (httpMethod.equals("POST")) {

            String name  = req.getParameter("name");
            String code  = req.getParameter("code");
            String sign  = req.getParameter("sign");

            validationNull(name, code, sign);
            validationBlank(name, code, sign);
            validationAlphabet(name, code);
            validationCurrencyCode(code, httpMethod);
        } else {
            String currencyCode = req.getHttpServletMapping().getMatchValue();

            validationNull(currencyCode);
            validationCurrencyCode(currencyCode, httpMethod);
            validationAlphabet(currencyCode);
        }
    }

    private void validationCurrencyCode(String currencyCode, String httpMethod) {
            if (currencyCode.trim().length() != 3) {
                if (httpMethod.equals("POST")) {
                    throw new WrongFormFields("Поля формы в теле запроса не корректны ");
                } else {
                    throw new WrongCurrencyCode("Не корректный код валюты " + currencyCode);
                }
            }
    }

    private void validationAlphabet(String name, String code) {
        String concatNameAndCode = name.trim().concat(code.trim());
        char[] chars = concatNameAndCode.toCharArray();
        for (char ch : chars) {
            if (Character.isDigit(ch)) {
                throw new WrongFormFields("Поля формы в теле запроса не корректны ");
            }
        }
    }

    private void validationAlphabet(String currencyCode) {
        for (char ch : currencyCode.trim().toCharArray()) {
            if (Character.isDigit(ch)) {
                throw new WrongCurrencyCode("Не корректный код валюты " + currencyCode);
            }
        }
    }

    private void validationBlank(String name, String code, String sign) {
        if (name.isBlank() || code.isBlank() || sign.isBlank()) {
            throw new WrongFormFields("Поля формы в теле запроса не корректны ");
        }
    }

    private void validationNull(String name, String code, String sign) {
        if (Objects.isNull(name) || Objects.isNull(code) || Objects.isNull(sign)) {
            throw new WrongFormFields("Поля формы в теле запроса не корректны ");
        }
    }

    private void validationNull(String currencyCode) {
        if (Objects.isNull(currencyCode) ) {
            throw new WrongCurrencyCode("Не корректный код валюты ");
        }
    }
}
