package com.edu.service;

import com.edu.dto.CurrencyExchangeDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface ServiceConvertCurrencies {
     CurrencyExchangeDTO convert(List<String> currencyCodes, BigDecimal amount) throws SQLException;

}
