package com.maxkavun.model;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeRate {

    private Integer id;
    private Integer baseCurrencyId;
    private Integer targetCurrencyId;
    private BigDecimal rate;

    public ExchangeRate(Integer id, Integer baseCurrencyId, Integer targetCurrencyId, BigDecimal rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public ExchangeRate(Integer baseCurrencyId, Integer targetCurrencyId, BigDecimal rate) {
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public ExchangeRate() {
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
               "id=" + id +
               ", baseCurrencyId=" + baseCurrencyId +
               ", targetCurrencyId=" + targetCurrencyId +
               ", rate=" + rate +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return Objects.equals(id, that.id) && Objects.equals(baseCurrencyId, that.baseCurrencyId) && Objects.equals(targetCurrencyId, that.targetCurrencyId) && Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCurrencyId, targetCurrencyId, rate);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public void setBaseCurrencyId(Integer baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public Integer getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public void setTargetCurrencyId(Integer targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
