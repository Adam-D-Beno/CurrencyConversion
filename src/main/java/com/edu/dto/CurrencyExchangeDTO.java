package com.edu.dto;

import java.math.BigDecimal;

public record CurrencyExchangeDTO(
                                  CurrencyDTO baseCurrency,
                                  CurrencyDTO targetCurrency,
                                  BigDecimal rate,
                                  BigDecimal amount,
                                  BigDecimal ConvertedAmount) {}

