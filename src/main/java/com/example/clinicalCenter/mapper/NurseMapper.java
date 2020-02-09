package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.NurseDTO;
import com.example.clinicalCenter.dto.NurseEditDTO;
import com.example.clinicalCenter.model.Nurse;

public class NurseMapper implements MapperInterface<Nurse, NurseDTO> {

    @Override
    public Nurse toEntity(NurseDTO dto) {
        return new Nurse(dto.email, dto.password, dto.name, dto.surname, dto.phone);
    }

    @Override
    public NurseDTO toDto(Nurse entity) {
        return new NurseDTO(entity.getId(), entity.getEmail(), entity.getPassword(), entity.getPassword(), entity.getName(),
                entity.getSurname(), entity.getPhone());
    }

    public NurseEditDTO toNurseEdit(Nurse entity) {
        return new NurseEditDTO(entity.getId(), entity.getEmail(), entity.getName(), entity.getSurname(), entity.getPhone());
    }
}
