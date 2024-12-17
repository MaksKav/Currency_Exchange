package com.maxkavun.mapper;

import com.maxkavun.exception.DtoToModelConversionException;
import com.maxkavun.exception.ModelToDtoConversionException;

import java.util.List;

public interface ModelDtoMapper<M , D> {
    D toDto(M model) throws ModelToDtoConversionException;
    M toModel(D dto) throws DtoToModelConversionException;
    List<D> toDtoList(List<M> models) throws ModelToDtoConversionException;
}
