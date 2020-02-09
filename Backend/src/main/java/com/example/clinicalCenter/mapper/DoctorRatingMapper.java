package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.DoctorRatingDTO;
import com.example.clinicalCenter.dto.DoctorRatingOperationDTO;
import com.example.clinicalCenter.model.DoctorRating;

public class DoctorRatingMapper implements MapperInterface<DoctorRating, DoctorRatingDTO> {

    @Override
    public DoctorRating toEntity(DoctorRatingDTO dto) {
        return new DoctorRating();
    }

    public DoctorRating toEntityForOperation(DoctorRatingOperationDTO dto) {
        return new DoctorRating();
    }

    @Override
    public DoctorRatingDTO toDto(DoctorRating entity) {
        return new DoctorRatingDTO(entity.getId(), entity.getPatient().getId(), entity.getDoctor().getId(), entity.getRating());
    }

}
