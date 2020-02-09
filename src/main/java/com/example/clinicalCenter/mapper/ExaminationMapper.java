package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.AppointmentDTO;
import com.example.clinicalCenter.dto.ExaminationCalendarDTO;
import com.example.clinicalCenter.dto.ExaminationCalendarDetailedDTO;
import com.example.clinicalCenter.dto.ExaminationDTO;
import com.example.clinicalCenter.model.Examination;
import com.example.clinicalCenter.model.Patient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ExaminationMapper implements MapperInterface<Examination, ExaminationDTO> {
    @Override
    public Examination toEntity(ExaminationDTO dto) {
        return new Examination();
    }

    @Override
    public ExaminationDTO toDto(Examination entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate date = entity.getStartDateTime().toLocalDate();
        String examDate = date.format(dateFormatter);
        LocalTime startTimeExam = entity.getStartDateTime().toLocalTime();
        String startTime = startTimeExam.format(formatter);
        LocalTime endTimeExam = entity.getEndDateTime().toLocalTime();
        String endTime = endTimeExam.format(formatter);
        Long patient_id = null;
        try {
            patient_id = entity.getPatient().getId();
        } catch (Exception ignored) {

        }

        return new ExaminationDTO(entity.getId(), entity.getType().getId(), examDate, startTime, endTime, Double.toString(entity.getDiscount()), entity.getDoctor().getId(),
                entity.getNurse().getId(), entity.getRoom().getId(), patient_id);
    }

    public ExaminationCalendarDTO toCalendarDTO(Examination entity) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return new ExaminationCalendarDTO(entity.getId(), entity.getStartDateTime().format(dateTimeFormatter),
                entity.getEndDateTime().format(dateTimeFormatter), entity.getRoom().getName(), entity.getStatus());
    }

    public ExaminationCalendarDetailedDTO toCalendarDetailedDTO(Examination entity) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        Patient patient = entity.getPatient();
        String patientName = null;
        String patientSurname = null;
        String patientEmail = null;
        if (patient != null) {
            patientName = patient.getName();
            patientSurname = patient.getSurname();
            patientEmail = patient.getEmail();
        }
        return new ExaminationCalendarDetailedDTO(entity.getId(), entity.getStartDateTime().format(dateTimeFormatter),
                entity.getEndDateTime().format(dateTimeFormatter), entity.getRoom().getName(),
                patientName, patientSurname, patientEmail, entity.getNurse().getName(), entity.getNurse().getSurname(),
                entity.getNurse().getEmail(), entity.getDoctor().getName(), entity.getDoctor().getSurname(),
                entity.getDoctor().getEmail(), entity.getStatus());
    }

    public AppointmentDTO toAppointmentDTO(Examination entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return new AppointmentDTO(entity.getId(), entity.getType().getName(), entity.getStartDateTime().toLocalDate().format(dateFormatter),
                entity.getStartDateTime().toLocalTime().format(formatter), entity.getEndDateTime().toLocalTime().format(formatter),
                entity.getDoctor().getName(), entity.getDoctor().getSurname(), entity.getNurse().getId(), entity.getRoom().getName(),
                entity.getPatient().getId(), entity.getPatient().getName(), entity.getPatient().getSurname());
    }

    public AppointmentDTO toAppointmentCancelDTO(Examination entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return new AppointmentDTO(entity.getId(), entity.getType().getName(), entity.getStartDateTime().toLocalDate().format(dateFormatter),
                entity.getStartDateTime().toLocalTime().format(formatter), entity.getEndDateTime().toLocalTime().format(formatter),
                entity.getDoctor().getName(), entity.getDoctor().getSurname(), entity.getNurse().getId(), entity.getRoom().getName(),
                (long) 0, "", "");
    }
}
