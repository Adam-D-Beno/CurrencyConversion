package com.edu.service;

import com.edu.dto.ExchangeRatesDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface ServiceExchangeRates {
    List<ExchangeRatesDTO> getListOfExchangeRates() throws SQLException;
    ExchangeRatesDTO getSpecificExchangeRate( List<String> BaseAndTargetCodes) throws SQLException;
    ExchangeRatesDTO addNewExchangeRates(List<String> BaseAndTargetCodes, BigDecimal rate) throws SQLException;
    ExchangeRatesDTO updateExchangeRate(List<String> BaseAndTargetCodes, BigDecimal rate) throws SQLException;

}
