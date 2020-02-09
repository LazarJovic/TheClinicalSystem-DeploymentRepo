package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.DoctorDTO;
import com.example.clinicalCenter.dto.DoctorEditDTO;
import com.example.clinicalCenter.dto.DoctorForListDTO;
import com.example.clinicalCenter.dto.DoctorSearchListDTO;
import com.example.clinicalCenter.model.Doctor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DoctorMapper implements MapperInterface<Doctor, DoctorDTO> {

    @Override
    public Doctor toEntity(DoctorDTO dto) {
        return new Doctor(dto.email, dto.password, dto.name, dto.surname, dto.phone, (long) 0, 0,
                LocalTime.parse(dto.shiftStart), LocalTime.parse(dto.shiftEnd));
    }

    @Override
    public DoctorDTO toDto(Doctor entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String shiftStart = entity.getShiftStart().format(formatter);
        String shiftEnd = entity.getShiftEnd().format(formatter);
        return new DoctorDTO(entity.getId(), entity.getEmail(), entity.getPassword(), entity.getPassword(), entity.getName(), entity.getSurname(),
                entity.getPhone(), shiftStart, shiftEnd, entity.getSpecialty().getId());
    }

    public DoctorEditDTO toDoctorEdit(Doctor entity) {
        return new DoctorEditDTO(entity.getId(), entity.getEmail(), entity.getName(), entity.getSurname(), entity.getPhone());
    }

    public DoctorForListDTO toDoctorForList(Doctor entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String shiftStart = entity.getShiftStart().format(formatter);
        String shiftEnd = entity.getShiftEnd().format(formatter);
        return new DoctorForListDTO(entity.getId(), entity.getEmail(), entity.getName(), entity.getSurname(), entity.getPhone(),
                shiftStart, shiftEnd);
    }

    public BusinessDoctorDTO toBusinessDoctorDTO(Doctor entity) {
        return new BusinessDoctorDTO(entity.getId(), entity.getEmail(), entity.getName(), entity.getSurname(), entity.getRatingAvg());
    }

    public DoctorSearchListDTO toDoctorSearchListDto(Doctor entity, String examinationDate, String startTime, String endTime) {
        return new DoctorSearchListDTO(entity.getId(), entity.getName(), entity.getSurname(),
                Double.toString(entity.getRatingAvg()), examinationDate, startTime, endTime);
    }

}
