package com.edu.service;

import com.edu.dto.CurrencyDTO;
import com.edu.model.Currency;

import java.sql.SQLException;
import java.util.List;

public interface ServiceCurrencies {
    List<CurrencyDTO> getListOfCurrencies() throws SQLException;
    CurrencyDTO getSpecificCurrency(String code) throws SQLException;
    CurrencyDTO addNewCurrencies(Currency currency) throws SQLException;
}
