package com.edu.service;

import com.edu.config.ConnectionDao;
import com.edu.config.ConnectionDaoSqlLiteImpl;
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

public class ServiceExchangeRates {
    private final ConnectionDao connectionDao;
    private final SpecificExchangeRatesDao<ExchangeRates> exchangeRatesDao;
    private final SpecificCurrencyDao<Currency> currencyDao;
    private final MapperDto<ExchangeRates, ExchangeRatesDTO> exchangeRatesMapperDto;

    public ServiceExchangeRates() {
         connectionDao = new ConnectionDaoSqlLiteImpl();
         exchangeRatesDao = new ExchangeRatesDaoImpl(connectionDao);
         currencyDao = new CurrencyDaoImpl(connectionDao);
         exchangeRatesMapperDto = new MapperExchangeRatesDtoImpl();
    }

    public List<ExchangeRatesDTO> getListOfExchangeRates() throws SQLException {
        return  exchangeRatesDao.getAll().map(exchangeRatesMapperDto::toDto).toList();
    }

    public ExchangeRatesDTO getSpecificExchangeRate( List<String> BaseAndTargetCodes) throws SQLException {
        String baseCurrency = BaseAndTargetCodes.get(0);
        String targetCurrency = BaseAndTargetCodes.get(1);

        Optional<ExchangeRates> exchangeRates = exchangeRatesDao.getBySpecificExchangeRate(baseCurrency, targetCurrency);
        return exchangeRates.map(exchangeRatesMapperDto::toDto)
                .orElseThrow(() -> new CurrencyNotExistInDataBase("Обменный курс для пары не найден - " + BaseAndTargetCodes));
    }

    public ExchangeRatesDTO addNewExchangeRates(List<String> BaseAndTargetCodes, BigDecimal rate) throws SQLException {
        var baseCurrencyCode = BaseAndTargetCodes.get(0);
        var targetCurrencyCode = BaseAndTargetCodes.get(1);

        Long baseCurrencyId = currencyDao.getByCode(baseCurrencyCode).map(Currency::getId)
                .orElseThrow(() -> new CurrencyNotExistInDataBase("Одна (или обе) валюта из валютной пары не существует в БД: " + baseCurrencyCode));
        Long targetCurrencyId = currencyDao.getByCode(targetCurrencyCode).map(Currency::getId)
                .orElseThrow(() -> new CurrencyNotExistInDataBase("Одна (или обе) валюта из валютной пары не существует в БД: " + targetCurrencyCode));
        //todo нужно обработать
        ExchangeRates exchangeRates = exchangeRatesDao.save(new ExchangeRates(baseCurrencyId, targetCurrencyId, rate)).get();
        return exchangeRatesMapperDto.toDto(exchangeRates);
    }

    public ExchangeRatesDTO updateExchangeRate(List<String> BaseAndTargetCodes, BigDecimal rate) throws SQLException {
       Long baseCurrencyId = currencyDao.getByCode(BaseAndTargetCodes.get(0)).map(Currency::getId)
               .orElseThrow(() -> new CurrencyNotExistInDataBase("Одна (или обе) валюта из валютной пары не существует в БД: " + BaseAndTargetCodes.get(0)));
       Long targetCurrencyId = currencyDao.getByCode(BaseAndTargetCodes.get(1)).map(Currency::getId)
               .orElseThrow(() -> new CurrencyNotExistInDataBase("Одна (или обе) валюта из валютной пары не существует в БД: " + BaseAndTargetCodes.get(1)));
        //todo нужно обработать
        ExchangeRates exchangeRates = exchangeRatesDao.update(new ExchangeRates(baseCurrencyId, targetCurrencyId, rate)).get();

        return exchangeRatesMapperDto.toDto(exchangeRates);
    }
}
