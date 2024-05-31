package com.edu.mapper;

import com.edu.dao.CurrencyDaoImpl;
import com.edu.dao.SpecificCurrencyDao;
import com.edu.dto.CurrencyDTO;
import com.edu.dto.ExchangeRatesDTO;
import com.edu.model.Currency;
import com.edu.model.ExchangeRates;
import java.sql.SQLException;
import java.util.Optional;

public class MapperExchangeRatesDtoImpl implements MapperDto<ExchangeRates, ExchangeRatesDTO> {
    private final MapperDto<Currency, CurrencyDTO> mapperCurrencyDto;

    public MapperExchangeRatesDtoImpl() {
        this.mapperCurrencyDto = new MapperCurrencyDtoImpl();
    }

    @Override
    public ExchangeRatesDTO toDto(ExchangeRates exchangeRates) {
        CurrencyDTO baseCurrency = getEntityById(exchangeRates.getBaseCurrencyId()).
                map(mapperCurrencyDto::toDto).get();

        CurrencyDTO targetCurrency = getEntityById(exchangeRates.getTargetCurrencyId()).
                map(mapperCurrencyDto::toDto).get();

       return new ExchangeRatesDTO(
               exchangeRates.getId(),
               baseCurrency,
               targetCurrency,
               exchangeRates.getRate()
       );
    }

    private Optional<Currency> getEntityById(Long id)  {

        try {
            return   CurrencyDaoImpl.getInstance().getById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
