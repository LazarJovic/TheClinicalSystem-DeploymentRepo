package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.ExaminationReportForListDTO;
import com.example.clinicalCenter.dto.OperationReportForListDTO;
import com.example.clinicalCenter.dto.ReportDTO;
import com.example.clinicalCenter.dto.ReportReviewDTO;
import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.model.Drug;
import com.example.clinicalCenter.model.Report;
import com.example.clinicalCenter.model.ReportOperation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class ReportMapper implements MapperInterface<Report, ReportDTO> {
    @Override
    public Report toEntity(ReportDTO dto) {
        return new Report(dto.id, dto.notes, null, new ArrayList<Drug>(), dto.reviewed);
    }

    @Override
    public ReportDTO toDto(Report entity) {
        return new ReportDTO(entity);
    }

    public ReportReviewDTO toReviewDTO(Report entity) {
        String patient = entity.getExamination().getPatient().getName() + " " +
                entity.getExamination().getPatient().getSurname();
        ArrayList<String> prescription = new ArrayList<>();
        for (Drug d : entity.getPrescription()) {
            prescription.add(d.getCode() + " - " + d.getName());
        }
        String diagnosis = entity.getDiagnosis().getCode() + " - " + entity.getDiagnosis().getName();
        return new ReportReviewDTO(entity.getId(), entity.getNotes(), diagnosis, prescription,
                entity.isReviewed(), entity.getExamination().getId(), patient);
    }

    public ReportDTO toDtoOperation(ReportOperation entity) {
        ArrayList<Long> prescription = new ArrayList<>();
        for (Drug d : entity.getPrescription())
            prescription.add(d.getId());
        return new ReportDTO(entity.getId(), entity.getNotes(), entity.getDiagnosis().getId(), prescription,
                entity.isReviewed(), entity.getOperation().getId());
    }

    public ReportOperation toEntityOperation(ReportDTO dto) {
        return new ReportOperation(dto.id, dto.notes, null, new ArrayList<Drug>(), dto.reviewed);
    }

    public ExaminationReportForListDTO toDtoForList(Report entity) {

        LocalDate date = entity.getExamination().getStartDateTime().toLocalDate();
        LocalTime startTime = entity.getExamination().getStartDateTime().toLocalTime();
        LocalTime endTime = entity.getExamination().getEndDateTime().toLocalTime();
        String startEndTime = startTime.toString() + " - " + endTime.toString();
        String doctorNameSurname = entity.getExamination().getDoctor().getName() + " " + entity.getExamination().getDoctor().getSurname();

        return new ExaminationReportForListDTO(entity.getId(), date.toString(), startEndTime, entity.getDiagnosis().getName(), doctorNameSurname);
    }

    public OperationReportForListDTO toDtoForListOperation(ReportOperation entity) {

        LocalDate date = entity.getOperation().getStartDateTime().toLocalDate();
        LocalTime startTime = entity.getOperation().getStartDateTime().toLocalTime();
        LocalTime endTime = entity.getOperation().getEndDateTime().toLocalTime();
        String startEndTime = startTime.toString() + " - " + endTime.toString();
        String doctorsNames = "";
        for (Doctor d : entity.getOperation().getDoctors()) {
            doctorsNames = doctorsNames + d.getName() + " " + d.getSurname();
        }
        return new OperationReportForListDTO(entity.getId(), date.toString(), startEndTime, entity.getDiagnosis().getName(), doctorsNames);
    }
}
