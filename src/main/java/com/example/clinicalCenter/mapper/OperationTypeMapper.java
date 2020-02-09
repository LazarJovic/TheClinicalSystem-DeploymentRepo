package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.OperationTypeDTO;
import com.example.clinicalCenter.model.OperationType;

public class OperationTypeMapper implements MapperInterface<OperationType, OperationTypeDTO> {

    @Override
    public OperationType toEntity(OperationTypeDTO dto) {
        return new OperationType(dto.name, Double.parseDouble(dto.price));
    }

    @Override
    public OperationTypeDTO toDto(OperationType entity) {
        return new OperationTypeDTO(entity.getId(), entity.getName(), Double.toString(entity.getPrice()));
    }
}
