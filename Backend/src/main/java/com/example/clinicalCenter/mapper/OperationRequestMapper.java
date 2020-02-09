package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.AppointmentForListDTO;
import com.example.clinicalCenter.dto.OperationRequestDTO;
import com.example.clinicalCenter.model.OperationRequest;

public class OperationRequestMapper implements MapperInterface<OperationRequest, OperationRequestDTO> {
    @Override
    public OperationRequest toEntity(OperationRequestDTO dto) {
        return null;
    }

    @Override
    public OperationRequestDTO toDto(OperationRequest entity) {
        Long roomId = (long) 0;
        if (entity.getRoom() != null) {
            roomId = entity.getRoom().getId();
        }

        return new OperationRequestDTO(entity.getId(),
                entity.getOperationDate().toString(),
                entity.getStartTime().toString(),
                entity.getEndTime().toString(),
                entity.getType().getId(),
                entity.getDoctor().getId(),
                entity.getClinic().getId(),
                roomId,
                entity.getPatient().getId());
    }

    public AppointmentForListDTO toForListDTO(OperationRequest entity) {

        Long roomId = (long) 0;
        if (entity.getRoom() != null) {
            roomId = entity.getRoom().getId();
        }

        return new AppointmentForListDTO(entity.getId(), entity.getOperationDate().toString(), entity.getStartTime().toString(),
                entity.getEndTime().toString(), entity.getDoctor().getId(), entity.getDoctor().getName(), entity.getDoctor().getSurname(), (long) 0, roomId,
                entity.getPatient().getId(), entity.getPatient().getName(), entity.getPatient().getSurname());
    }
}
