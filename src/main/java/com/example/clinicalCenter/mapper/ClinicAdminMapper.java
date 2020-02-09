package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.ClinicAdminDTO;
import com.example.clinicalCenter.dto.ClinicAdminEditDTO;
import com.example.clinicalCenter.model.ClinicAdmin;
import org.springframework.stereotype.Service;

@Service
public class ClinicAdminMapper implements MapperInterface<ClinicAdmin, ClinicAdminDTO> {

    @Override
    public ClinicAdmin toEntity(ClinicAdminDTO dto) {
        return new ClinicAdmin(dto.email, dto.password, dto.name, dto.surname, dto.phone, null);
    }

    @Override
    public ClinicAdminDTO toDto(ClinicAdmin entity) {
        return new ClinicAdminDTO(entity.getId(), entity.getEmail(), entity.getPassword(), entity.getPassword(), entity.getName(),
                entity.getSurname(), entity.getPhone(), entity.getClinic().getId());
    }

    public ClinicAdminEditDTO toClinicAdminEdit(ClinicAdmin entity) {
        return new ClinicAdminEditDTO(entity.getId(), entity.getEmail(), entity.getName(), entity.getSurname(), entity.getPhone());
    }
}
