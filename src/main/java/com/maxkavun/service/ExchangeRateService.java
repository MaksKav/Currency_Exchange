package com.maxkavun.service;

import com.maxkavun.dao.CurrencyDao;
import com.maxkavun.dao.ExchangeRateDao;
import com.maxkavun.dto.CurrencyDto;
import com.maxkavun.dto.ExchangeConvertDto;
import com.maxkavun.dto.ExchangeRateDto;
import com.maxkavun.exception.BusinessException;
import com.maxkavun.exception.CurrencyNotFoundException;
import com.maxkavun.exception.ExchangeRateAlreadyExistsException;
import com.maxkavun.mapper.ExchangeRateMapper;
import com.maxkavun.model.Currency;
import com.maxkavun.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
            mapToDtoWithoutCurrencies(exchangeRate).ifPresent(exchangeRateDtoList::add);
        }
        return exchangeRateDtoList;
    }


    public Optional<ExchangeRateDto> getExchangeRateByCode(String code) {
        return exchangeRateDao.findByCode(code)
                .flatMap(this::mapToDtoWithoutCurrencies);
    }

    public ExchangeRateDto addNewExchangeRate(String baseCurrencyCode , String targetCurrencyCode , BigDecimal rate) {
        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new CurrencyNotFoundException("Base currency not found in database " + baseCurrencyCode));
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(() -> new CurrencyNotFoundException("Target currency not found in database " + targetCurrencyCode));

        String baseAndTargetCode = baseCurrencyCode + targetCurrencyCode;
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
               Optional <ExchangeRateDto> exchangeRateDtoOptional = mapToDtoWithoutCurrencies(updatedExchangeRate);
               if (exchangeRateDtoOptional.isPresent()){
                   return exchangeRateDtoOptional.get();
               }
           }
       } else {
           throw new CurrencyNotFoundException("Currency not found in database " + baseAndTargetCurrencyCode);
       }
        throw new BusinessException("This exchange rate is not exist " );
    }


    public ExchangeConvertDto calculateExchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        String baseTargetCode = baseCurrencyCode + targetCurrencyCode;
        Optional<ExchangeRate> exchangeRateOptional = exchangeRateDao.findByCode(baseTargetCode);

        if (exchangeRateOptional.isPresent()) {
            return convertAmount(exchangeRateOptional.get(), amount);
        } else {
            return handleReverseConversion(targetCurrencyCode, baseCurrencyCode, amount);
        }
    }

    private ExchangeConvertDto convertAmount(ExchangeRate exchangeRate, BigDecimal amount) {
        Optional<ExchangeRateDto> exchangeRateDto = mapToDtoWithoutCurrencies(exchangeRate);

        if (!exchangeRateDto.isPresent()) {
            throw new BusinessException("Unable to map exchange rate to DTO");
        }

        BigDecimal convertedAmount = exchangeRate.getRate().multiply(amount);
        return new ExchangeConvertDto(exchangeRateDto.get(), amount, convertedAmount);
    }

    private ExchangeConvertDto handleReverseConversion(String targetCurrencyCode, String baseCurrencyCode, BigDecimal amount) {
        String targetBaseCode = targetCurrencyCode + baseCurrencyCode;
        Optional<ExchangeRate> exchangeRateOpt = exchangeRateDao.findByCode(targetBaseCode);

        if (exchangeRateOpt.isPresent()) {
            return convertReverseAmount(exchangeRateOpt.get(), amount);
        } else {
            throw new BusinessException("Exchange rate not found in database ");
        }
    }

    private ExchangeConvertDto convertReverseAmount(ExchangeRate exchangeRate, BigDecimal amount) {
        Optional<ExchangeRateDto> exchangeRateDto = mapToDtoWithoutCurrencies(exchangeRate);

        if (!exchangeRateDto.isPresent()) {
            throw new BusinessException("Unable to map exchange rate to DTO");
        }

        CurrencyDto temp = exchangeRateDto.get().getBaseCurrency();
        BigDecimal reverseRate = BigDecimal.ONE.divide(exchangeRateDto.get().getRate(), 3, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = reverseRate.multiply(amount);

        exchangeRateDto.get().setBaseCurrency(exchangeRateDto.get().getTargetCurrency());
        exchangeRateDto.get().setTargetCurrency(temp);
        exchangeRateDto.get().setRate(reverseRate);

        return new ExchangeConvertDto(exchangeRateDto.get(), amount, convertedAmount);
    }


    public Optional<ExchangeRateDto> mapToDtoWithoutCurrencies(ExchangeRate exchangeRate) {
        Optional<Currency> baseCurrency = currencyDao.findById(exchangeRate.getBaseCurrencyId());
        Optional<Currency> targetCurrency = currencyDao.findById(exchangeRate.getTargetCurrencyId());
        if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
            return Optional.of(exchangeRateMapper.toDto(exchangeRate, baseCurrency.get(), targetCurrency.get()));
        }
        return Optional.empty();
    }

}
