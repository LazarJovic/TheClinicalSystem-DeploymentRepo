package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.DrugDTO;
import com.example.clinicalCenter.model.Drug;

public class DrugMapper implements MapperInterface<Drug, DrugDTO> {
    @Override
    public Drug toEntity(DrugDTO dto) {
        return new Drug(dto.id, dto.name, dto.code);

    }

    @Override
    public DrugDTO toDto(Drug entity) {
        return new DrugDTO(entity.getId(), entity.getName(), entity.getCode());
    }
}
