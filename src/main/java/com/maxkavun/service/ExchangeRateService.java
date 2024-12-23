package com.maxkavun.service;

import com.maxkavun.dao.ExchangeRateDao;
import com.maxkavun.dto.ExchangeRateDto;
import com.maxkavun.mapper.ExchangeRateMapper;
import com.maxkavun.model.ExchangeRate;

import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private final ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper();

    public List<ExchangeRateDto> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates  = exchangeRateDao.findAll();
        return exchangeRateMapper.toDtoList(exchangeRates);
    }

    public Optional<ExchangeRateDto> getExchangeRateByCode(String code) {
        Optional<ExchangeRate> exchangeRate = exchangeRateDao.findByCode(code);
        return exchangeRate.map(exchangeRateMapper::toDto);
    }


}
