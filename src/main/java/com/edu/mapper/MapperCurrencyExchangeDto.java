package com.edu.mapper;

import com.edu.dto.CurrencyExchangeDTO;
import com.edu.model.ExchangeRates;

import java.math.BigDecimal;

public interface MapperCurrencyExchangeDto<T, S>{

    CurrencyExchangeDTO toDTO (ExchangeRates exchangeRates, BigDecimal amount, BigDecimal convertedAmount);
    public CurrencyExchangeDTO toDTO(String fromCode, String toCode, BigDecimal rate,
                                     BigDecimal amount, BigDecimal convertedAmount);


}
