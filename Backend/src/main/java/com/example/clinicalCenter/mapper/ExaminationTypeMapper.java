package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.ExaminationTypeDTO;
import com.example.clinicalCenter.model.ExaminationType;

public class ExaminationTypeMapper implements MapperInterface<ExaminationType, ExaminationTypeDTO> {

    @Override
    public ExaminationType toEntity(ExaminationTypeDTO dto) {
        return new ExaminationType(dto.name, Double.parseDouble(dto.price));
    }

    @Override
    public ExaminationTypeDTO toDto(ExaminationType entity) {
        return new ExaminationTypeDTO(entity.getId(), entity.getName(), Double.toString(entity.getPrice()));
    }
}
