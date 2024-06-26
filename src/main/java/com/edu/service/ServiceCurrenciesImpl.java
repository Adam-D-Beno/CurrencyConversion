package com.edu.service;

import com.edu.dao.CurrencyDaoImpl;
import com.edu.dao.SpecificCurrencyDao;
import com.edu.dto.CurrencyDTO;
import com.edu.exception.CurrencyNotExistInDataBase;
import com.edu.mapper.MapperCurrencyDtoImpl;
import com.edu.mapper.MapperDto;
import com.edu.model.Currency;
import java.sql.SQLException;
import java.util.List;

public class ServiceCurrenciesImpl implements ServiceCurrencies{
    private final SpecificCurrencyDao<Currency> currencyDao;
    private final MapperDto<Currency, CurrencyDTO> mapperCurrencyDto;

    public ServiceCurrenciesImpl() {
        this.currencyDao =  CurrencyDaoImpl.getInstance();
        this.mapperCurrencyDto = new MapperCurrencyDtoImpl();
    }

    @Override
    public List<CurrencyDTO> getListOfCurrencies() throws SQLException {
            return currencyDao.getAll().map(mapperCurrencyDto::toDto).toList();
    }

    @Override
    public CurrencyDTO getSpecificCurrency(String code) throws SQLException {
        return currencyDao.getByCode(code)
            .map(mapperCurrencyDto::toDto).orElseThrow(() -> new CurrencyNotExistInDataBase("Валюта не найдена " + code));
    }

    @Override
    public CurrencyDTO addNewCurrencies(Currency currency) throws SQLException {
        return currencyDao.save(currency).map(mapperCurrencyDto::toDto).get();
    }
}
