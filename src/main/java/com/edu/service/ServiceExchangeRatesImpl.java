package com.edu.service;

import com.edu.dao.*;
import com.edu.dto.ExchangeRatesDTO;
import com.edu.exception.CurrencyNotExistInDataBase;
import com.edu.mapper.MapperDto;
import com.edu.mapper.MapperExchangeRatesDtoImpl;
import com.edu.model.Currency;
import com.edu.model.ExchangeRates;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ServiceExchangeRatesImpl implements ServiceExchangeRates{
    private final SpecificExchangeRatesDao<ExchangeRates> exchangeRatesDao;
    private final SpecificCurrencyDao<Currency> currencyDao;
    private final MapperDto<ExchangeRates, ExchangeRatesDTO> exchangeRatesMapperDto;

    public ServiceExchangeRatesImpl() {
        this. exchangeRatesDao = ExchangeRatesDaoImpl.getInstance();
        this. currencyDao = CurrencyDaoImpl.getInstance();
        this. exchangeRatesMapperDto = new MapperExchangeRatesDtoImpl();
    }

    @Override
    public List<ExchangeRatesDTO> getListOfExchangeRates() throws SQLException {
        return  exchangeRatesDao.getAll().map(exchangeRatesMapperDto::toDto).toList();
    }

    @Override
    public ExchangeRatesDTO getSpecificExchangeRate( List<String> BaseAndTargetCodes) throws SQLException {
        String baseCurrency = BaseAndTargetCodes.get(0);
        String targetCurrency = BaseAndTargetCodes.get(1);

        Optional<ExchangeRates> exchangeRates = exchangeRatesDao.getBySpecificExchangeRate(baseCurrency, targetCurrency);
        return exchangeRates.map(exchangeRatesMapperDto::toDto)
                .orElseThrow(() -> new CurrencyNotExistInDataBase("Обменный курс для пары не найден - " + BaseAndTargetCodes));
    }

    @Override
    public ExchangeRatesDTO addNewExchangeRates(List<String> BaseAndTargetCodes, BigDecimal rate) throws SQLException {
        var baseCurrencyCode = BaseAndTargetCodes.get(0);
        var targetCurrencyCode = BaseAndTargetCodes.get(1);

        Long baseCurrencyId = currencyDao.getByCode(baseCurrencyCode).map(Currency::getId)
                .orElseThrow(() -> new CurrencyNotExistInDataBase("Одна (или обе) валюта из валютной пары не существует в БД: " + baseCurrencyCode));
        Long targetCurrencyId = currencyDao.getByCode(targetCurrencyCode).map(Currency::getId)
                .orElseThrow(() -> new CurrencyNotExistInDataBase("Одна (или обе) валюта из валютной пары не существует в БД: " + targetCurrencyCode));

        ExchangeRates exchangeRates = exchangeRatesDao.save(new ExchangeRates(baseCurrencyId, targetCurrencyId, rate)).get();
        return exchangeRatesMapperDto.toDto(exchangeRates);
    }

    @Override
    public ExchangeRatesDTO updateExchangeRate(List<String> BaseAndTargetCodes, BigDecimal rate) throws SQLException {
       Long baseCurrencyId = currencyDao.getByCode(BaseAndTargetCodes.get(0)).map(Currency::getId)
               .orElseThrow(() -> new CurrencyNotExistInDataBase("Одна (или обе) валюта из валютной пары не существует в БД: " + BaseAndTargetCodes.get(0)));
       Long targetCurrencyId = currencyDao.getByCode(BaseAndTargetCodes.get(1)).map(Currency::getId)
               .orElseThrow(() -> new CurrencyNotExistInDataBase("Одна (или обе) валюта из валютной пары не существует в БД: " + BaseAndTargetCodes.get(1)));

        ExchangeRates exchangeRates = exchangeRatesDao.update(new ExchangeRates(baseCurrencyId, targetCurrencyId, rate)).get();

        return exchangeRatesMapperDto.toDto(exchangeRates);
    }
}
