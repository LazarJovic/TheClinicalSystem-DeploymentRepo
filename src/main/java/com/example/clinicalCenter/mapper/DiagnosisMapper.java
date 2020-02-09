package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.DiagnosisDTO;
import com.example.clinicalCenter.model.Diagnosis;

public class DiagnosisMapper implements MapperInterface<Diagnosis, DiagnosisDTO> {
    @Override
    public Diagnosis toEntity(DiagnosisDTO dto) {
        return new Diagnosis(dto.id, dto.name, dto.code);
    }

    @Override
    public DiagnosisDTO toDto(Diagnosis entity) {
        return new DiagnosisDTO(entity.getId(), entity.getName(), entity.getCode());
    }
}
