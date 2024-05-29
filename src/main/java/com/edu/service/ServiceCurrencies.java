package com.edu.service;

import com.edu.config.ConnectionDao;
import com.edu.config.ConnectionDaoSqlLiteImpl;
import com.edu.dao.CurrencyDaoImpl;
import com.edu.dao.SpecificCurrencyDao;
import com.edu.dto.CurrencyDTO;
import com.edu.exception.CurrencyNotExistInDataBase;
import com.edu.mapper.MapperCurrencyDtoImpl;
import com.edu.mapper.MapperDto;
import com.edu.model.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class ServiceCurrencies {
    private final ConnectionDao connectionDao;
    private final SpecificCurrencyDao<Currency> currencyDao;
    private final MapperDto<Currency, CurrencyDTO> mapperCurrencyDto;

    public ServiceCurrencies() {
        connectionDao = new ConnectionDaoSqlLiteImpl();
        currencyDao = new CurrencyDaoImpl(connectionDao);
        mapperCurrencyDto = new MapperCurrencyDtoImpl();
    }

    public List<CurrencyDTO> getListOfCurrencies() throws SQLException {
            return currencyDao.getAll().map(mapperCurrencyDto::toDto).toList();
    }

    //todo use validation
    public CurrencyDTO getSpecificCurrency(String code) throws SQLException {
        return currencyDao.getByCode(code)
            .map(mapperCurrencyDto::toDto).orElseThrow(() -> new CurrencyNotExistInDataBase("Валюта не найдена " + code));
    }
    //todo use validation
    public CurrencyDTO addNewCurrencies(Currency currency) throws SQLException {
        return currencyDao.save(currency).map(mapperCurrencyDto::toDto).get();
    }
}
