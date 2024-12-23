package com.maxkavun.mapper;

import com.maxkavun.dto.ExchangeRateDto;
import com.maxkavun.exception.DtoToModelConversionException;
import com.maxkavun.exception.ModelToDtoConversionException;
import com.maxkavun.model.ExchangeRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExchangeRateMapper implements ModelDtoMapper <ExchangeRate , ExchangeRateDto>{
    private static final Logger log = LoggerFactory.getLogger(ExchangeRateMapper.class);

    @Override
    public ExchangeRateDto toDto(ExchangeRate model) throws ModelToDtoConversionException {
        if (model == null) {
            throw new ModelToDtoConversionException("Can't converse model to dto , ExchangeRate model is null");
        }
        return new ExchangeRateDto(model.getId(), model.getBaseCurrencyId(), model.getTargetCurrencyId() , model.getRate());
    }

    @Override
    public ExchangeRate toModel(ExchangeRateDto dto) throws DtoToModelConversionException {
        if (dto == null) {
            throw new DtoToModelConversionException("Can't convert dto to model, ExchangeRate dto is null");
        }
        return new ExchangeRate(dto.getId(), dto.getBaseCurrencyId(), dto.getTargetCurrencyId(), dto.getRate());
    }

    @Override
    public List<ExchangeRateDto> toDtoList(List<ExchangeRate> models) throws ModelToDtoConversionException {
        if (models == null || models.isEmpty()){
            log.warn("Cannot convert models list to DTO in mapper,  models is null or empty");
            return Collections.emptyList();
        }
        List<ExchangeRateDto> dtoList = new ArrayList<ExchangeRateDto>();
        for (ExchangeRate model : models){
            dtoList.add(toDto(model));
        }
        return dtoList;
    }
}
