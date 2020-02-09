package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.AppointmentDTO;
import com.example.clinicalCenter.dto.OperationCalendarDTO;
import com.example.clinicalCenter.dto.OperationCalendarDetailedDTO;
import com.example.clinicalCenter.dto.OperationDTO;
import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.model.Operation;
import com.example.clinicalCenter.model.Patient;
import com.example.clinicalCenter.model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OperationMapper implements MapperInterface<Operation, OperationDTO> {
    @Override
    public Operation toEntity(OperationDTO dto) {
        return null;
    }

    @Override
    public OperationDTO toDto(Operation entity) {
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
        ArrayList<Long> doctors = new ArrayList<>();
        for (Doctor d : entity.getDoctors()) {
            doctors.add(d.getId());
        }
        return new OperationDTO(entity.getId(), entity.getType().getId(), examDate, startTime, endTime, doctors,
                entity.getRoom().getId(), patient_id);
    }

    public OperationCalendarDTO toCalendarDto(Operation entity) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return new OperationCalendarDTO(entity.getId(), entity.getStartDateTime().format(dateTimeFormatter),
                entity.getEndDateTime().format(dateTimeFormatter), entity.getRoom().getName());
    }

    public OperationCalendarDetailedDTO toCalendarDetailedDto(Operation entity) {
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
        List<String> doctorNames = new ArrayList<>();
        List<String> doctorSurnames = new ArrayList<>();
        List<String> doctorEmails = new ArrayList<>();
        for (Doctor d : entity.getDoctors()) {
            doctorNames.add(d.getName());
            doctorSurnames.add(d.getSurname());
            doctorEmails.add(d.getUsername());
        }
        return new OperationCalendarDetailedDTO(entity.getId(), entity.getStartDateTime().format(dateTimeFormatter),
                entity.getEndDateTime().format(dateTimeFormatter), entity.getRoom().getName(),
                patientName, patientSurname, patientEmail,
                doctorNames, doctorSurnames, doctorEmails, entity.getStatus());
    }

    public AppointmentDTO toAppointmentDTO(Operation entity, User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return new AppointmentDTO(entity.getId(), entity.getType().getName(), entity.getStartDateTime().toLocalDate().format(dateFormatter),
                entity.getStartDateTime().toLocalTime().format(formatter), entity.getEndDateTime().toLocalTime().format(formatter),
                user.getName(), user.getSurname(), (long) 0, entity.getRoom().getName(),
                entity.getPatient().getId(), entity.getPatient().getName(), entity.getPatient().getSurname());
    }

    public AppointmentDTO toAppointmentCancelDTO(Operation entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return new AppointmentDTO(entity.getId(), entity.getType().getName(), entity.getStartDateTime().toLocalDate().format(dateFormatter),
                entity.getStartDateTime().toLocalTime().format(formatter), entity.getEndDateTime().toLocalTime().format(formatter),
                entity.getDoctors().get(0).getName(), entity.getDoctors().get(0).getSurname(), null, entity.getRoom().getName(),
                (long) 0, "", "");
    }
}
