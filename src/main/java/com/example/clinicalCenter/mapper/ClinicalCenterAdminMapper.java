package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.ClinicalCenterAdminDTO;
import com.example.clinicalCenter.dto.ClinicalCenterAdminEditDTO;
import com.example.clinicalCenter.model.ClinicalCenterAdmin;

public class ClinicalCenterAdminMapper implements MapperInterface<ClinicalCenterAdmin, ClinicalCenterAdminDTO> {

    @Override
    public ClinicalCenterAdmin toEntity(ClinicalCenterAdminDTO dto) {
        return new ClinicalCenterAdmin(dto.email, dto.password, dto.name, dto.surname, dto.phone);
    }

    @Override
    public ClinicalCenterAdminDTO toDto(ClinicalCenterAdmin entity) {
        return new ClinicalCenterAdminDTO(entity.getId(), entity.getEmail(), entity.getPassword(), entity.getPassword(), entity.getName(), entity.getSurname(), entity.getPhone());
    }

    public ClinicalCenterAdminEditDTO toClinicAdminEdit(ClinicalCenterAdmin entity) {
        return new ClinicalCenterAdminEditDTO(entity.getId(), entity.getEmail(), entity.getName(), entity.getSurname(), entity.getPhone());
    }
}
