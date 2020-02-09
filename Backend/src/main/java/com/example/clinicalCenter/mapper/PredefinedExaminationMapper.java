package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.PredefinedExaminationDTO;
import com.example.clinicalCenter.model.Examination;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PredefinedExaminationMapper implements MapperInterface<Examination, PredefinedExaminationDTO> {
    @Override
    public Examination toEntity(PredefinedExaminationDTO dto) {
        return new Examination();
    }

    @Override
    public PredefinedExaminationDTO toDto(Examination entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate date = entity.getStartDateTime().toLocalDate();
        String examDate = date.format(dateFormatter);
        LocalTime startTimeExam = entity.getStartDateTime().toLocalTime();
        String startTime = startTimeExam.format(formatter);
        LocalTime endTimeExam = entity.getEndDateTime().toLocalTime();
        String endTime = endTimeExam.format(formatter);
        double price = entity.getType().getPrice();
        double discount = entity.getDiscount() / 100;
        double priceWithDiscount = price - (price * discount);
        String doctor = entity.getDoctor().getName() + " " + entity.getDoctor().getSurname();
        String nurse = entity.getNurse().getName() + " " + entity.getNurse().getSurname();
        return new PredefinedExaminationDTO(entity.getId(), entity.getType().getName(), examDate, startTime, endTime,
                Double.toString(priceWithDiscount), doctor, nurse, entity.getRoom().getName());
    }
}
