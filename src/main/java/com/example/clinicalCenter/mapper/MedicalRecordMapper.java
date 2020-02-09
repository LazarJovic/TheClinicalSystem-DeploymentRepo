package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.MedicalRecordDTO;
import com.example.clinicalCenter.model.MedicalRecord;

import java.time.LocalDate;

public class MedicalRecordMapper implements MapperInterface<MedicalRecord, MedicalRecordDTO> {

    @Override
    public MedicalRecord toEntity(MedicalRecordDTO dto) {
        return new MedicalRecord(dto.height, dto.weight, dto.bloodType, LocalDate.parse(dto.birthDate), null);
    }

    @Override
    public MedicalRecordDTO toDto(MedicalRecord entity) {
        String date = null;
        if (entity.getBirthDate() != null) {
            date = entity.getBirthDate().toString();
        }
        return new MedicalRecordDTO(entity.getId(), entity.getHeight(), entity.getWeight(), entity.getBloodType(),
                date, entity.getPatient().getId());
    }
}
