package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.AppointmentForListDTO;
import com.example.clinicalCenter.dto.ExaminationRequestDTO;
import com.example.clinicalCenter.model.ExaminationRequest;

public class ExaminationRequestMapper implements MapperInterface<ExaminationRequest, ExaminationRequestDTO> {
    @Override
    public ExaminationRequest toEntity(ExaminationRequestDTO dto) {
        return null;
    }

    @Override
    public ExaminationRequestDTO toDto(ExaminationRequest entity) {

        Long roomId = (long) 0;
        if (entity.getRoom() != null) {
            roomId = entity.getRoom().getId();
        }

        return new ExaminationRequestDTO(entity.getId(),
                entity.getExaminationDate().toString(),
                entity.getStartTime().toString(),
                entity.getEndTime().toString(),
                entity.getType().getId(),
                entity.getDoctor().getId(),
                entity.getClinic().getId(),
                roomId,
                entity.getPatient().getId());
    }

    public AppointmentForListDTO toForListDTO(ExaminationRequest entity) {

        Long roomId = (long) 0;
        if (entity.getRoom() != null) {
            roomId = entity.getRoom().getId();
        }

        return new AppointmentForListDTO(entity.getId(), entity.getExaminationDate().toString(), entity.getStartTime().toString(),
                entity.getEndTime().toString(), entity.getDoctor().getId(), entity.getDoctor().getName(), entity.getDoctor().getSurname(), (long) 0, roomId,
                entity.getPatient().getId(), entity.getPatient().getName(), entity.getPatient().getSurname());
    }

}
