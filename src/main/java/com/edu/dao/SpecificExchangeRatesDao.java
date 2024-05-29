package com.edu.dao;

import com.edu.model.ExchangeRates;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SpecificExchangeRatesDao<T> extends CrudDao<T>{
    public Optional<T> getBySpecificExchangeRate(String baseCurrency, String targetCurrency) throws SQLException;
    public List<Optional<T>> getBySpecificExchangeRateForCrossUSD(String baseCurrency, String targetCurrency) throws SQLException;
}
