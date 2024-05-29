package com.edu.dto;

import java.math.BigDecimal;

public record ExchangeRatesDTO  (Long id,
                                 CurrencyDTO baseCurrency,
                                 CurrencyDTO targetCurrency,
                                 BigDecimal rate) {}

