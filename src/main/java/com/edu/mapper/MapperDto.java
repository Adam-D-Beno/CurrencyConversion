package com.edu.mapper;

import com.edu.model.Currency;

public interface MapperDto<T, S> {

     S toDto(T t);
}
