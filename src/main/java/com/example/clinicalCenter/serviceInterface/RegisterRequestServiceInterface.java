package com.example.clinicalCenter.serviceInterface;

import com.example.clinicalCenter.dto.RegisterRequestDTO;

import java.util.List;

public interface RegisterRequestServiceInterface {

    List<RegisterRequestDTO> findAll();

    RegisterRequestDTO findOne(Long id);

    RegisterRequestDTO create(RegisterRequestDTO dto);

    RegisterRequestDTO update(RegisterRequestDTO dto);

    RegisterRequestDTO remove(Long id);
}
