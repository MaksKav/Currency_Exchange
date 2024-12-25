package com.maxkavun.factory;

import com.maxkavun.dao.CurrencyDao;
import com.maxkavun.dao.ExchangeRateDao;
import com.maxkavun.mapper.ExchangeRateMapper;
import com.maxkavun.service.ExchangeRateService;

public class ExchangeRateServiceFactory {
    public static ExchangeRateService createExchangeRateService() {
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
        CurrencyDao currencyDao = new CurrencyDao();
        ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper();
        return new ExchangeRateService(exchangeRateDao , exchangeRateMapper, currencyDao);
    }
}
