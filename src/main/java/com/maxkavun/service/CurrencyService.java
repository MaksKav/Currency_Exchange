package com.maxkavun.service;

import com.maxkavun.dao.CurrencyDao;
import com.maxkavun.dto.CurrencyDto;
import com.maxkavun.exception.BusinessException;
import com.maxkavun.exception.DtoToModelConversionException;
import com.maxkavun.exception.ModelToDtoConversionException;
import com.maxkavun.exception.ValidationException;
import com.maxkavun.mapper.CurrencyMapper;
import com.maxkavun.model.Currency;

import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private final CurrencyDao currencyDao = new CurrencyDao();
    private final CurrencyMapper mapper = new CurrencyMapper();


    public CurrencyService() {
    }

    public List<CurrencyDto> getAllCurrencies()  {
        List<Currency> currencies = currencyDao.findAll();
        return mapper.toDtoList(currencies);
    }


    public Optional<CurrencyDto> getOptionalCurrencyByCode(String code) {
        Optional<Currency> currencyOptional = currencyDao.findByCode(code);
        if (currencyOptional.isPresent()) {
            Currency currency = currencyOptional.get();
            return Optional.of(mapper.toDto(currency));
        } else {
            return Optional.empty();
        }
    }


    public void addCurrency(CurrencyDto currencyDto) {
        checkCurrencyUniqueness(currencyDto.getCode());
        Currency currency = new Currency(currencyDto.getId(), currencyDto.getName(), currencyDto.getCode(), currencyDto.getSign());
        currencyDao.save(currency);
    }


    public void updateCurrency(CurrencyDto currencyDto) {
        Optional<Currency> optionalCurrency = currencyDao.findByCode(currencyDto.getCode());
        if (!optionalCurrency.isPresent()) {
            throw new BusinessException("Failed update Currency , Currency not found in DB " + currencyDto);
        }
        Currency currency = mapper.toModel(currencyDto);
        currencyDao.update(currency);
    }


    private void checkCurrencyUniqueness(String code) {
        if (currencyDao.findByCode(code).isPresent()) {
            throw new ValidationException("Currency with this code already exists " + code);
        }
    }

}
