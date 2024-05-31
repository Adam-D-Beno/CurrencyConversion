package com.edu.mapper;

import com.edu.dao.CurrencyDaoImpl;
import com.edu.dao.SpecificCurrencyDao;
import com.edu.dto.CurrencyDTO;
import com.edu.dto.CurrencyExchangeDTO;
import com.edu.exception.CurrencyNotExistInDataBase;
import com.edu.model.Currency;
import com.edu.model.ExchangeRates;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

public class MapperCurrencyExchangeDTOImpl implements MapperCurrencyExchangeDto<ExchangeRates, CurrencyExchangeDTO>{
    private final MapperDto<Currency, CurrencyDTO> mapperCurrencyDto;

    public MapperCurrencyExchangeDTOImpl() {
        this.mapperCurrencyDto = new MapperCurrencyDtoImpl();
    }

    @Override
    public CurrencyExchangeDTO toDTO(ExchangeRates exchangeRates, BigDecimal amount, BigDecimal convertedAmount) {
        CurrencyDTO base = getEntityById(exchangeRates.getBaseCurrencyId()).map(mapperCurrencyDto::toDto)
               .orElseThrow(() -> new CurrencyNotExistInDataBase("Код валюты не найден " + exchangeRates.getBaseCurrencyId()));;
        CurrencyDTO target = getEntityById(exchangeRates.getTargetCurrencyId()).map(mapperCurrencyDto::toDto)
                .orElseThrow(() -> new CurrencyNotExistInDataBase("Валюта не найдена " + exchangeRates.getTargetCurrencyId()));;

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

        CurrencyDTO base = getByCode(fromCode).map(mapperCurrencyDto::toDto)
                .orElseThrow(() -> new CurrencyNotExistInDataBase("Валюта не найдена " + fromCode));
        CurrencyDTO target = getByCode(toCode).map(mapperCurrencyDto::toDto)
                .orElseThrow(() -> new CurrencyNotExistInDataBase("Валюта не найдена " + toCode));

        return new CurrencyExchangeDTO(base, target, rate, amount, convertedAmount);
    }

    private Optional<Currency> getEntityById(Long id)  {

        try {
            return   CurrencyDaoImpl.getInstance().getById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Currency> getByCode(String code)  {

        try {
            return CurrencyDaoImpl.getInstance().getByCode(code);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
