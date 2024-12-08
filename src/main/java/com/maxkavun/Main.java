package com.maxkavun;

import com.maxkavun.dao.CurrencyDao;
import com.maxkavun.dao.ExchangeRateDao;
import com.maxkavun.model.Currency;
import com.maxkavun.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        BigDecimal decimal = new BigDecimal("42.78");
        ExchangeRate ex = new ExchangeRate(1, 6, decimal);
        ExchangeRateDao.getInstance().save(ex);
    }
}
