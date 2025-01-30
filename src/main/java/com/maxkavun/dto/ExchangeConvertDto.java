package com.maxkavun.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeConvertDto {

    private ExchangeRateDto exchangeRateDto;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

    public ExchangeConvertDto(ExchangeRateDto exchangeRateDto, BigDecimal amount, BigDecimal convertedAmount) {
        this.exchangeRateDto = exchangeRateDto;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public ExchangeConvertDto() {
    }

    @Override
    public String toString() {
        return "ExchangeDto{" + "exchangeRateDto=" + exchangeRateDto + ", amount=" + amount + ", convertedAmount=" + convertedAmount + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeConvertDto that = (ExchangeConvertDto) o;
        return Objects.equals(exchangeRateDto, that.exchangeRateDto) && Objects.equals(amount, that.amount) && Objects.equals(convertedAmount, that.convertedAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exchangeRateDto, amount, convertedAmount);
    }

    public ExchangeRateDto getExchangeRateDto() {
        return exchangeRateDto;
    }

    public void setExchangeRateDto(ExchangeRateDto exchangeRateDto) {
        this.exchangeRateDto = exchangeRateDto;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }
}
