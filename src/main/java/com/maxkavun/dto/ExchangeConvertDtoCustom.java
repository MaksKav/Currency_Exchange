package com.maxkavun.dto;

import java.math.BigDecimal;

public class ExchangeConvertDtoCustom {
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

    public ExchangeConvertDtoCustom(ExchangeConvertDto dto) {
        this.baseCurrency = dto.getExchangeRateDto().getBaseCurrency();
        this.targetCurrency = dto.getExchangeRateDto().getTargetCurrency();
        this.rate = dto.getExchangeRateDto().getRate();
        this.amount = dto.getAmount();
        this.convertedAmount = dto.getConvertedAmount();
    }

    public CurrencyDto getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyDto getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }
}