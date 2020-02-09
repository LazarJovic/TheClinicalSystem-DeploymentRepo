package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.BusinessClinicDTO;
import com.example.clinicalCenter.dto.ClinicDTO;
import com.example.clinicalCenter.dto.ClinicSearchListDTO;
import com.example.clinicalCenter.model.Clinic;
import com.example.clinicalCenter.model.ExaminationType;

public class ClinicMapper implements MapperInterface<Clinic, ClinicDTO> {
    @Override
    public Clinic toEntity(ClinicDTO dto) {
        return new Clinic(dto.name, dto.address, dto.city, dto.description, (long) 0, 0);
    }

    @Override
    public ClinicDTO toDto(Clinic entity) {
        return new ClinicDTO(entity.getId(), entity.getName(), entity.getAddress(), entity.getCity(), entity.getDescription());
    }


    public BusinessClinicDTO toBusinessClinicDTO(Clinic entity) {
        return new BusinessClinicDTO(entity.getId(), entity.getName(), entity.getRatingAvg());
    }

    public ClinicSearchListDTO toDtoSearchList(Clinic entity, ExaminationType et) {
        String price = "-";
        if (et != null) {
            price = Double.toString(et.getPrice());
        }
        return new ClinicSearchListDTO(entity.getId(), entity.getName(), entity.getAddress(), entity.getCity(),
                Double.toString(entity.getRatingAvg()), price);

    }
}
