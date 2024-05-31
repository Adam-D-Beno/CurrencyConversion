package com.edu.mapper;

import com.edu.dao.CurrencyDaoImpl;
import com.edu.dao.SpecificCurrencyDao;
import com.edu.dto.CurrencyDTO;
import com.edu.dto.CurrencyExchangeDTO;
import com.edu.model.Currency;
import com.edu.model.ExchangeRates;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

public class MapperCurrencyExchangeDTOImpl implements MapperCurrencyExchangeDto<ExchangeRates, CurrencyExchangeDTO>{
    private final SpecificCurrencyDao<Currency> currencyDao;
    private final MapperDto<Currency, CurrencyDTO> mapperCurrencyDto;

    public MapperCurrencyExchangeDTOImpl() {
        this.currencyDao = CurrencyDaoImpl.getInstance();
        this.mapperCurrencyDto = new MapperCurrencyDtoImpl();

    }

    @Override
    public CurrencyExchangeDTO toDTO(ExchangeRates exchangeRates, BigDecimal amount, BigDecimal convertedAmount) {
        CurrencyDTO base = getEntityById(exchangeRates.getBaseCurrencyId()).map(mapperCurrencyDto::toDto).get();
        CurrencyDTO target = getEntityById(exchangeRates.getTargetCurrencyId()).map(mapperCurrencyDto::toDto).get();

        return new CurrencyExchangeDTO(
                base,
                target,
                exchangeRates.getRate(),
                amount,
                convertedAmount
        );
    }

    @Override
    public CurrencyExchangeDTO toDTO(String fromCode, String toCode, BigDecimal rate,
                                     BigDecimal amount, BigDecimal convertedAmount) {

        CurrencyDTO base = getByCode(fromCode).map(mapperCurrencyDto::toDto).get();
        CurrencyDTO target = getByCode(toCode).map(mapperCurrencyDto::toDto).get();

        return new CurrencyExchangeDTO(base, target, rate, amount, convertedAmount);
    }

    private Optional<Currency> getEntityById(Long id)  {

        try {
            return   currencyDao.getById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Currency> getByCode(String code)  {

        try {
            return currencyDao.getByCode(code);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
