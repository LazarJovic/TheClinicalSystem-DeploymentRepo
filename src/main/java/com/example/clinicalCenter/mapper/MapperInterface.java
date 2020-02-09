package com.example.clinicalCenter.mapper;

public interface MapperInterface<T, U> {

    T toEntity(U dto);

    U toDto(T entity);
}
