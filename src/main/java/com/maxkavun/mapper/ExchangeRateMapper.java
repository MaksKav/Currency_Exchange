package com.maxkavun.mapper;

import com.maxkavun.dto.ExchangeRateDto;
import com.maxkavun.exception.ModelToDtoConversionException;
import com.maxkavun.model.Currency;
import com.maxkavun.model.ExchangeRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExchangeRateMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateMapper.class);
    private final CurrencyMapper currencyMapper= new CurrencyMapper();


    public ExchangeRateDto toDto(ExchangeRate model , Currency baseCurrency , Currency targetCurrency) throws ModelToDtoConversionException {
        if (model == null) {
            throw new ModelToDtoConversionException("Can't converse model to dto , ExchangeRate model is null");
        }
        if (baseCurrency == null || targetCurrency == null) {
            throw new ModelToDtoConversionException("Base or target currency is null");
        }
        LOGGER.info("Successfully converted ExchangeRate to DTO");
        return new ExchangeRateDto(
                model.getId(),
                currencyMapper.toDto(baseCurrency),
                currencyMapper.toDto(targetCurrency),
                model.getRate()
        );
    }
}
