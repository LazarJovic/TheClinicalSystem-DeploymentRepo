package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.ExaminationReportForListDTO;
import com.example.clinicalCenter.dto.ReportDTO;
import com.example.clinicalCenter.exception.NotFoundException;
import com.example.clinicalCenter.mapper.ReportMapper;
import com.example.clinicalCenter.model.Report;
import com.example.clinicalCenter.model.Status;
import com.example.clinicalCenter.repository.ReportRepository;
import com.example.clinicalCenter.repository.UserRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = false)
public class ReportService implements ServiceInterface<ReportDTO> {

    @Autowired
    private ReportRepository repository;

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private DiagnosisService diagnosisService;

    @Autowired
    private UserRepository userRepository;

    private ReportMapper reportMapper = new ReportMapper();

    @Override
    public ReportDTO findOne(Long id) {
        return reportMapper.toDto(repository.findById(id).orElseGet(null));
    }

    @Override
    public List<ReportDTO> findAll() {
        return null;
    }

    @Override
    public ReportDTO create(ReportDTO dto) {
        Report report = reportMapper.toEntity(dto);
        report.setExamination(examinationService.findOneEntity(dto.examination));
        report.setDiagnosis(diagnosisService.findOneEntity(dto.diagnosis));
        for (Long id : dto.prescription) {
            report.getPrescription().add(drugService.findOneEntity(id));
        }
        report.setRecord(report.getExamination().getPatient().getRecord());
        report.getExamination().setStatus(Status.FINISHED);
        examinationService.updateEntity(report.getExamination());
        return reportMapper.toDto(repository.save(report));
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public ReportDTO update(ReportDTO dto) {
        Report report = this.repository.findOneLocked(dto.id);

        report.setReviewed(dto.reviewed);
        if (!dto.notes.isEmpty()) {
            report.setNotes(dto.notes);
        }
        if (!(dto.prescription.size() == 1 && dto.prescription.get(0) == -1)) {
            report.setPrescription(new ArrayList<>());
            for (Long id : dto.prescription) {
                report.getPrescription().add(drugService.findOneEntity(id));
            }
        }
        if (dto.diagnosis != -1) {
            report.setDiagnosis(diagnosisService.findOneEntity(dto.diagnosis));
        }
        return reportMapper.toDto(this.repository.save(report));
    }

    @Override
    public ReportDTO delete(Long id) {
        return null;
    }

    public ArrayList<ExaminationReportForListDTO> findAllExaminationReportOfMedicalRecord(Long recordId) {

        ArrayList<ExaminationReportForListDTO> examinationReports = new ArrayList<ExaminationReportForListDTO>();
        List<Report> listOfReports = this.repository.findAllExaminationReportsOfMedicalRecord(recordId);
        for (Report r : listOfReports) {
            ExaminationReportForListDTO dto = this.reportMapper.toDtoForList(r);
            examinationReports.add(dto);
        }

        return examinationReports;
    }

    public boolean canLoggedInEditReport(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //User user = this.userRepository.findByEmail(username);
        Report report = repository.findById(id).orElseGet(null);
        if (report == null)
            throw new NotFoundException();
        return report.getExamination().getDoctor().getUsername().equals(username);
    }
}
