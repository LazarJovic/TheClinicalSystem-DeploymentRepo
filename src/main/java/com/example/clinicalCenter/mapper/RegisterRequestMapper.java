package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.RegisterRequestDTO;
import com.example.clinicalCenter.model.RegisterRequest;

public class RegisterRequestMapper implements MapperInterface<RegisterRequest, RegisterRequestDTO> {

    @Override
    public RegisterRequest toEntity(RegisterRequestDTO dto) {
        return new RegisterRequest(dto.email, dto.password, dto.name, dto.surname, dto.address, dto.city, dto.country, dto.phone,
                dto.socialSecurityNumber, dto.status, dto.reason);
    }

    @Override
    public RegisterRequestDTO toDto(RegisterRequest entity) {
        RegisterRequestDTO dto = new RegisterRequestDTO(entity.getEmail(), entity.getPassword(), entity.getPassword(), entity.getName(), entity.getSurname(), entity.getAddress(),
                entity.getCity(), entity.getCountry(), entity.getPhone(), entity.getSocialSecurityNumber(), entity.getStatus(), entity.getReason());
        dto.id = entity.getId();
        return dto;
    }
}
