package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.OperationReportForListDTO;
import com.example.clinicalCenter.dto.ReportDTO;
import com.example.clinicalCenter.exception.NotFoundException;
import com.example.clinicalCenter.mapper.ReportMapper;
import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.model.ReportOperation;
import com.example.clinicalCenter.model.Status;
import com.example.clinicalCenter.repository.ReportOperationRepository;
import com.example.clinicalCenter.repository.UserRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportOperationService implements ServiceInterface<ReportDTO> {

    @Autowired
    private OperationService operationService;

    @Autowired
    private DiagnosisService diagnosisService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private ReportOperationRepository repository;

    @Autowired
    private UserRepository userRepository;

    private ReportMapper reportMapper = new ReportMapper();

    @Override
    public List<ReportDTO> findAll() {
        return null;
    }

    @Override
    public ReportDTO findOne(Long id) {
        return reportMapper.toDtoOperation(repository.findById(id).get());
    }

    @Override
    public ReportDTO create(ReportDTO dto) throws Exception {
        ReportOperation report = reportMapper.toEntityOperation(dto);
        report.setOperation(operationService.findOneEntity(dto.examination));
        report.setDiagnosis(diagnosisService.findOneEntity(dto.diagnosis));
        for (Long id : dto.prescription) {
            report.getPrescription().add(drugService.findOneEntity(id));
        }
        report.setRecord(report.getOperation().getPatient().getRecord());
        report.getOperation().setStatus(Status.FINISHED);
        operationService.updateEntity(report.getOperation());
        return reportMapper.toDtoOperation(repository.save(report));
    }

    @Override
    public ReportDTO update(ReportDTO dto) throws Exception {
        ReportOperation report = this.repository.findById(dto.id).get();

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
        return reportMapper.toDtoOperation(this.repository.save(report));
    }

    @Override
    public ReportDTO delete(Long id) throws Exception {
        return null;
    }

    public ArrayList<OperationReportForListDTO> getAllOperationReportsOfMedicalRecord(Long recordId) {

        ArrayList<OperationReportForListDTO> operationReports = new ArrayList<OperationReportForListDTO>();
        List<ReportOperation> listOfReports = this.repository.findAllOperationReportsOfMedicalRecord(recordId);
        for (ReportOperation r : listOfReports) {
            OperationReportForListDTO dto = this.reportMapper.toDtoForListOperation(r);
            operationReports.add(dto);
        }

        return operationReports;
    }

    public boolean canLoggedInEditReport(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        /*User user = this.userRepository.findByEmail(username);*/
        ReportOperation report = repository.findById(id).orElseGet(null);
        if (report == null)
            throw new NotFoundException();
        for (Doctor d : report.getOperation().getDoctors()) {
            if (d.getUsername().equals(username))
                return true;
        }
        return false;
    }
}
