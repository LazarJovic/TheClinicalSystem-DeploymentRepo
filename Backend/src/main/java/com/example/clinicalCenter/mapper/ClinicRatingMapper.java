package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.ClinicRatingDTO;
import com.example.clinicalCenter.model.ClinicRating;

public class ClinicRatingMapper implements MapperInterface<ClinicRating, ClinicRatingDTO> {

    @Override
    public ClinicRating toEntity(ClinicRatingDTO dto) {
        return new ClinicRating();
    }

    @Override
    public ClinicRatingDTO toDto(ClinicRating entity) {
        return new ClinicRatingDTO(entity.getId(), entity.getPatient().getId(), entity.getClinic().getId(), entity.getRating());
    }
}
