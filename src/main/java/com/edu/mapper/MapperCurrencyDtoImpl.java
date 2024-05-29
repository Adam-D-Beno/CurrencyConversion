package com.edu.mapper;

import com.edu.dto.CurrencyDTO;
import com.edu.mapper.MapperDto;
import com.edu.model.Currency;

public class MapperCurrencyDtoImpl implements MapperDto<Currency, CurrencyDTO> {
    @Override
    public CurrencyDTO toDto(Currency currency) {
        return new CurrencyDTO(currency.getId(), currency.getCode(), currency.getFullName(), currency.getSign());
    }
}
