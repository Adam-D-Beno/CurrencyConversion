package com.edu.validation;

import com.edu.exception.WrongFormFields;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

public class ValidateExchange {
    public void validationExchange(HttpServletRequest req) {
        String fromCurrencyCode = req.getParameter("from");
        String toCurrencyCode = req.getParameter("to");
        String amount = req.getParameter("amount");

        validationIsNull(fromCurrencyCode, toCurrencyCode, amount);
        validationCurrencyCodes(fromCurrencyCode, toCurrencyCode);
        validationEmpty(amount);
        validationAlphabet(fromCurrencyCode, toCurrencyCode);
        validationNumber(amount);
        validationZeroOrNegative(amount);
    }

    private void validationCurrencyCodes(String fromCurrencyCode, String toCurrencyCode) {
        if (fromCurrencyCode.trim().length() != 3 || toCurrencyCode.trim().length() != 3) {
            throw new WrongFormFields("Поля формы в теле запроса не корректны ");
        }
    }

    private void validationAlphabet(String fromCurrencyCode, String toCurrencyCode) {
        String concatFromCodeAndToCodes = fromCurrencyCode.trim().concat(toCurrencyCode.trim());
        char[] chars = concatFromCodeAndToCodes.toCharArray();
        for (char ch : chars) {
            if (Character.isDigit(ch)) {
                throw new WrongFormFields("Поля формы в теле запроса не корректны ");
            }
        }
    }

    private void validationIsNull(String fromCurrencyCode, String toCurrencyCode, String amount) {
        if (Objects.isNull(fromCurrencyCode) || Objects.isNull(toCurrencyCode) || Objects.isNull(amount)) {
            throw new WrongFormFields("Поля формы в теле запроса не корректны ");
        }
    }

    private void validationNumber(String amount) {
        for (char ch : amount.trim().toCharArray()) {
            if (Character.isAlphabetic(ch)) {
                throw new WrongFormFields("Поля формы в теле запроса не корректны ");
            }
        }
    }

    private void validationZeroOrNegative(String amount){
        if (Double.parseDouble(amount.trim()) <= 0) {
            throw new WrongFormFields("Поля формы в теле запроса не корректны ");
        }
    }

    private void validationEmpty(String amount) {
        if (amount.isBlank()) {
            throw new WrongFormFields("Поля формы в теле запроса не корректны ");
        }
    }
}
