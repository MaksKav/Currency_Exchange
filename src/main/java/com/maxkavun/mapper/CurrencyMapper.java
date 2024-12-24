package com.maxkavun.mapper;

import com.maxkavun.dto.CurrencyDto;
import com.maxkavun.exception.DtoToModelConversionException;
import com.maxkavun.exception.ModelToDtoConversionException;
import com.maxkavun.model.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrencyMapper {
    private static final Logger log = LoggerFactory.getLogger(CurrencyMapper.class);


    public  CurrencyDto toDto(Currency model ) throws ModelToDtoConversionException {
        if (model == null){
            throw new ModelToDtoConversionException("Cannot convert model to DTO , Currency model is null");
        }
        return new CurrencyDto(model.getId(), model.getCode(), model.getFullName(), model.getSign());
    }


    public List<CurrencyDto> toDtoList(List<Currency> models) throws ModelToDtoConversionException {
        if (models == null || models.isEmpty()){
            log.warn("Cannot convert models list to DTO in mapper,  models is null or empty");
            return Collections.emptyList();
        }
        List<CurrencyDto> currencyDtoList = new ArrayList<>();
        for (Currency currency : models){
            currencyDtoList.add(toDto(currency));
        }
        return currencyDtoList;
    }


    public Currency toModel(CurrencyDto dto) throws DtoToModelConversionException {
        if (dto == null){
            throw new DtoToModelConversionException("Cannot convert DTO to model , dto is null");
        }
        return new Currency(dto.getId(), dto.getCode(), dto.getFullName(), dto.getSign());
    }
}
