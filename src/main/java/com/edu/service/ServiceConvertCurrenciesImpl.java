package com.edu.service;

import com.edu.dao.CurrencyDaoImpl;
import com.edu.dao.ExchangeRatesDaoImpl;
import com.edu.dao.SpecificCurrencyDao;
import com.edu.dao.SpecificExchangeRatesDao;
import com.edu.dto.CurrencyExchangeDTO;
import com.edu.exception.CurrencyNotExistInDataBase;
import com.edu.exception.WrongRateInExchangeRate;
import com.edu.mapper.*;
import com.edu.model.Currency;
import com.edu.model.ExchangeRates;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ServiceConvertCurrenciesImpl implements ServiceConvertCurrencies{
    private final SpecificExchangeRatesDao<ExchangeRates> exchangeRatesDao;
    private final  SpecificCurrencyDao<Currency> currencyDao;
    private final MapperCurrencyExchangeDto<ExchangeRates, CurrencyExchangeDTO> mapperDto;

    public ServiceConvertCurrenciesImpl() {
        this.exchangeRatesDao = new ExchangeRatesDaoImpl();
        this.currencyDao = new CurrencyDaoImpl();
        this.mapperDto = new MapperCurrencyExchangeDTOImpl();
    }

    @Override
    public CurrencyExchangeDTO convert(List<String> currencyCodes, BigDecimal amount) throws SQLException {
        var fromCode = currencyCodes.get(0);
        var toCode =  currencyCodes.get(1);

       currencyDao.getByCode(fromCode).orElseThrow(() -> new CurrencyNotExistInDataBase("Валюта не найдена " + fromCode));
       currencyDao.getByCode(toCode).orElseThrow(() -> new CurrencyNotExistInDataBase("Валюта не найдена " + toCode));

       Optional<ExchangeRates> directExchange = exchangeRatesDao.getBySpecificExchangeRate(fromCode, toCode);

       if (directExchange.isPresent()) {
           directExchange.map(ExchangeRates::getRate).filter(rate -> rate.compareTo(BigDecimal.ZERO) > 0)
                   .orElseThrow(() -> new WrongRateInExchangeRate("Не корректный обменный курс = 0"));

           BigDecimal convertedAmount = directExchangeRate(directExchange.get(), amount);
           return mapperDto.toDTO(directExchange.get(), amount, convertedAmount);
       }

        Optional<ExchangeRates> reverseExchange= exchangeRatesDao.getBySpecificExchangeRate(toCode, fromCode);
        if (reverseExchange.isPresent()) {
            reverseExchange.map(ExchangeRates::getRate).filter(rate -> rate.compareTo(BigDecimal.ZERO) > 0)
                    .orElseThrow(() -> new WrongRateInExchangeRate("Не корректный обменный курс = 0"));

            BigDecimal convertedAmount = reverseExchangeRate(reverseExchange.get(), amount);
            return mapperDto.toDTO(fromCode, toCode, reverseExchange.get().getRate(), amount, convertedAmount);
        }

        List<ExchangeRates> crossExchange =
                exchangeRatesDao.getBySpecificExchangeRateForCrossUSD(fromCode, toCode).stream()
                        .peek(exchangeRates -> exchangeRates
                                .orElseThrow(() -> new CurrencyNotExistInDataBase("Кросс курс обмена через USD не найден ")))
                        .map(Optional::get).toList();

        List<BigDecimal> convertedAmountWithCrossRate = crossExchangeRate(crossExchange, amount);

        var crossExchangeRate = convertedAmountWithCrossRate.get(0);
        var convertedAmount = convertedAmountWithCrossRate.get(1);

        return mapperDto.toDTO(fromCode, toCode, crossExchangeRate, amount, convertedAmount);
    }

    private BigDecimal directExchangeRate(ExchangeRates exchangeRates, BigDecimal amount) {
        BigDecimal rate = exchangeRates.getRate();
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal reverseExchangeRate(ExchangeRates exchangeRates, BigDecimal amount) {
        BigDecimal rate = exchangeRates.getRate();
        BigDecimal reverseExchangeRate = BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_UP);

        return amount.multiply(reverseExchangeRate).setScale(2, RoundingMode.HALF_UP);
    }

    private List<BigDecimal> crossExchangeRate(List<ExchangeRates> exchangeRates, BigDecimal amount) {
        ExchangeRates from = exchangeRates.get(0);
        ExchangeRates to = exchangeRates.get(1);
        BigDecimal reverseExchangeRate = BigDecimal.ONE.divide(from.getRate(), 6, RoundingMode.HALF_UP);
        BigDecimal crossExchangeRate  = reverseExchangeRate.multiply(to.getRate()).setScale(6, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = amount.multiply(crossExchangeRate).setScale(2, RoundingMode.HALF_UP);

        return List.of(crossExchangeRate, convertedAmount);
    }
}
