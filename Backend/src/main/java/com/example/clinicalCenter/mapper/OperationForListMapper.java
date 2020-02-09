package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.OperationForListDTO;
import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.model.Operation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class OperationForListMapper implements MapperInterface<Operation, OperationForListDTO> {
    @Override
    public Operation toEntity(OperationForListDTO dto) {
        return new Operation();
    }

    @Override
    public OperationForListDTO toDto(Operation entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate date = entity.getStartDateTime().toLocalDate();
        String opDate = date.format(dateFormatter);
        LocalTime startTimeExam = entity.getStartDateTime().toLocalTime();
        String startTime = startTimeExam.format(formatter);
        LocalTime endTimeExam = entity.getEndDateTime().toLocalTime();
        String endTime = endTimeExam.format(formatter);
        String doctorsNames = "";
        for (Doctor d : entity.getDoctors()) {
            doctorsNames = doctorsNames + d.getName() + " ";
        }
        return new OperationForListDTO(entity.getId(), entity.getType().getName(), opDate, startTime, endTime,
                Double.toString(entity.getType().getPrice()), doctorsNames, entity.getRoom().getName());
    }
}
