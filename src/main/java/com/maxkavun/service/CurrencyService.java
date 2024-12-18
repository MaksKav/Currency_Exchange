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
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final CurrencyMapper mapper = new CurrencyMapper();

    private static final CurrencyService INSTANCE = new CurrencyService();

    private CurrencyService (){
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }


    public Optional<CurrencyDto> getOptionalCurrencyByCode(String code) throws BusinessException, ModelToDtoConversionException {
        Optional<Currency> currencyOptional = currencyDao.findByCode(code);
        if (currencyOptional.isPresent()) {
            Currency currency = currencyOptional.get();
            return Optional.of(mapper.toDto(currency));
        }else {
            return Optional.empty();
        }
    }


    public List<CurrencyDto> getAllCurrencies() throws ModelToDtoConversionException {
        List<Currency> currencies = currencyDao.findAll();
        return mapper.toDtoList(currencies);
    }


    public void addCurrency(CurrencyDto currencyDto) throws ValidationException {
        checkCurrencyUniqueness(currencyDto.getCode());
        Currency currency = new Currency(currencyDto.getId(), currencyDto.getCode(), currencyDto.getFullName() ,currencyDto.getSign());
        currencyDao.save(currency);
    }


    public void updateCurrency(CurrencyDto currencyDto) throws ValidationException, BusinessException, DtoToModelConversionException {
        Optional<Currency> optionalCurrency = currencyDao.findById(currencyDto.getId());
        if (!optionalCurrency.isPresent()) {
            throw new BusinessException("Failed update Currency , Currency not found in DB " + currencyDto);
        }
        Currency currency = mapper.toModel(currencyDto);
        currencyDao.update(currency);
    }


    private void checkCurrencyUniqueness(String code) throws ValidationException {
        if (currencyDao.findByCode(code).isPresent()) {
            throw new ValidationException("Currency with this code already exists " + code);
        }
    }

}
