package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.PatientDTO;
import com.example.clinicalCenter.dto.PatientEditDTO;
import com.example.clinicalCenter.model.MedicalRecord;
import com.example.clinicalCenter.model.Patient;
import com.example.clinicalCenter.model.RegisterRequest;

public class PatientMapper implements MapperInterface<Patient, PatientDTO> {

    @Override
    public Patient toEntity(PatientDTO dto) {
        return new Patient(dto.id, dto.email, dto.password, dto.name, dto.surname, dto.address, dto.city, dto.country, dto.phone, dto.socialSecurityNumber, new MedicalRecord());
    }

    @Override
    public PatientDTO toDto(Patient entity) {
        return new PatientDTO(entity.getId(), entity.getEmail(), entity.getPassword(), entity.getPassword(),
                entity.getName(), entity.getSurname(), entity.getAddress(), entity.getCity(), entity.getCountry(),
                entity.getPhone(), entity.getSocialSecurityNumber());
    }

    public PatientDTO toDto(RegisterRequest entity) {
        return new PatientDTO(entity.getId(), entity.getEmail(), entity.getPassword(), entity.getPassword(),
                entity.getName(), entity.getSurname(), entity.getAddress(), entity.getCity(), entity.getCountry(),
                entity.getPhone(), entity.getSocialSecurityNumber());
    }

    public PatientEditDTO toDtoForEdit(Patient entity) {
        return new PatientEditDTO(entity.getId(), entity.getEmail(), entity.getName(), entity.getSurname(), entity.getAddress(),
                entity.getCity(), entity.getCountry(), entity.getPhone(), entity.getSocialSecurityNumber());
    }
}
