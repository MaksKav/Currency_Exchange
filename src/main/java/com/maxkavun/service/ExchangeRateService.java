package com.maxkavun.service;

import com.maxkavun.dao.CurrencyDao;
import com.maxkavun.dao.ExchangeRateDao;
import com.maxkavun.dto.ExchangeRateDto;
import com.maxkavun.exception.BusinessException;
import com.maxkavun.exception.CurrencyNotFoundException;
import com.maxkavun.exception.ExchangeRateAlreadyExistsException;
import com.maxkavun.mapper.ExchangeRateMapper;
import com.maxkavun.model.Currency;
import com.maxkavun.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao ;
    private final ExchangeRateMapper exchangeRateMapper ;
    private final CurrencyDao currencyDao ;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao, ExchangeRateMapper exchangeRateMapper, CurrencyDao currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.exchangeRateMapper = exchangeRateMapper;
        this.currencyDao = currencyDao;
    }

    public List<ExchangeRateDto> getAllExchangeRates() {
        List<ExchangeRateDto> exchangeRateDtoList = new ArrayList<>();
        for (ExchangeRate exchangeRate : exchangeRateDao.findAll()) {
            mapToDtoWithCurrencies(exchangeRate).ifPresent(exchangeRateDtoList::add);
        }
        return exchangeRateDtoList;
    }


    public Optional<ExchangeRateDto> getExchangeRateByCode(String code) {
        return exchangeRateDao.findByCode(code)
                .flatMap(this::mapToDtoWithCurrencies);
    }

    public ExchangeRateDto addNewExchangeRate(String baseCurrencyCode , String targetCurrencyCode , BigDecimal rate) {
        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new CurrencyNotFoundException("Base currency not found in database " + baseCurrencyCode));
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(() -> new CurrencyNotFoundException("Target currency not found in database " + targetCurrencyCode));

        String baseAndTargetCode = baseCurrencyCode + targetCurrencyCode; //TODO если пытаюсь добавить уже существующую валюту - дает неправильную ошибку
        Optional<ExchangeRate> exchangeRateDto = exchangeRateDao.findByCode(baseAndTargetCode);
        if (exchangeRateDto.isPresent()){
            throw new ExchangeRateAlreadyExistsException("This exchange rate is exist " + baseAndTargetCode);
        }

        ExchangeRate exchangeRate = exchangeRateDao.save(new ExchangeRate(baseCurrency.getId() , targetCurrency.getId(), rate));
        return exchangeRateMapper.toDto(exchangeRate , baseCurrency , targetCurrency);
    }


    public ExchangeRateDto updateExchangeRate(String baseAndTargetCurrencyCode, BigDecimal rate){
       Optional<ExchangeRate> exchangeRateOptional = exchangeRateDao.findByCode(baseAndTargetCurrencyCode);
       if (exchangeRateOptional.isPresent()){
           ExchangeRate exchangeRate = exchangeRateOptional.get();
           exchangeRate.setRate(rate);
           exchangeRateDao.update(exchangeRate);


        Optional<ExchangeRate> updatedExchangeRateOptional = exchangeRateDao.findByCode(baseAndTargetCurrencyCode);
           if (updatedExchangeRateOptional.isPresent()){
               ExchangeRate updatedExchangeRate = updatedExchangeRateOptional.get();
               Optional <ExchangeRateDto> exchangeRateDtoOptional = mapToDtoWithCurrencies(updatedExchangeRate);
               if (exchangeRateDtoOptional.isPresent()){
                   return exchangeRateDtoOptional.get();
               }
           }
       } else {
           throw new CurrencyNotFoundException("Currency not found in database " + baseAndTargetCurrencyCode);
       }
        throw new BusinessException("This exchange rate is not exist " );
    }


    public Optional<ExchangeRateDto> mapToDtoWithCurrencies(ExchangeRate exchangeRate) {
        Optional<Currency> baseCurrency = currencyDao.findById(exchangeRate.getBaseCurrencyId());
        Optional<Currency> targetCurrency = currencyDao.findById(exchangeRate.getTargetCurrencyId());
        if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
            return Optional.of(exchangeRateMapper.toDto(exchangeRate, baseCurrency.get(), targetCurrency.get()));
        }
        return Optional.empty();
    }

}
