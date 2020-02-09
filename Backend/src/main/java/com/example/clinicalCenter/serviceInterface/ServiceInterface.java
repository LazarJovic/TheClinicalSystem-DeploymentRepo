package com.example.clinicalCenter.serviceInterface;

import java.util.List;

public interface ServiceInterface<T> {

    List<T> findAll();

    T findOne(Long id);

    T create(T dto) throws Exception;

    T update(T dto) throws Exception;

    T delete(Long id) throws Exception;

}
