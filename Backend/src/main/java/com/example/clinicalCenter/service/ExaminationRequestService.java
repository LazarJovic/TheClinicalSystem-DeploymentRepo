package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.AppointmentForListDTO;
import com.example.clinicalCenter.dto.CreateAppointmentDTO;
import com.example.clinicalCenter.dto.ExaminationRequestDTO;
import com.example.clinicalCenter.exception.DateConflictException;
import com.example.clinicalCenter.exception.GenericConflictException;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.ExaminationRequestMapper;
import com.example.clinicalCenter.model.ClinicAdmin;
import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.model.ExaminationRequest;
import com.example.clinicalCenter.model.Patient;
import com.example.clinicalCenter.model.enums.ExaminationRequestStatus;
import com.example.clinicalCenter.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@ComponentScan
@Service
public class ExaminationRequestService {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExaminationRequestRepository examinationRequestRepository;

    @Autowired
    private ExaminationTypeRepository examinationTypeRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ExaminationService examinationService;

    private ExaminationRequestMapper examinationRequestMapper;

    public ExaminationRequestService() {
        this.examinationRequestMapper = new ExaminationRequestMapper();
    }

    public ExaminationRequestDTO createExaminationRequest(CreateAppointmentDTO dto) throws Exception {

        LocalDate date = LocalDate.parse(dto.date);
        LocalTime startTime = LocalTime.parse(dto.startTime);
        LocalTime endTime = LocalTime.parse(dto.endTime);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Doctor doctor = (Doctor) this.userRepository.findByEmail(username);

        String isOk = doctorService.isDoctorFreeAtGivenTime(date, startTime, endTime, doctor);

        if (!isOk.equals("OK")) {
            throw new ValidationException(isOk);
        }

        if (date.isBefore((LocalDate.now())) || date.isEqual(LocalDate.now())) {
            throw new DateConflictException("Can only send requests for tomorrow and onwards.");
        }

        Patient patient = (Patient) userRepository.findById(dto.patientId).get();

        ExaminationRequest examinationRequest = new ExaminationRequest(date, startTime, endTime, doctor.getSpecialty(), null,
                doctor, patient, ExaminationRequestStatus.WAITING_FOR_ADMIN);

        examinationRequest.setClinic(doctor.getClinic());

        ExaminationRequest newRequest = this.examinationRequestRepository.save(examinationRequest);

        List<ClinicAdmin> clinicAdmins = this.userRepository.findClinicAdmins(doctor.getClinic().getId());

        for (ClinicAdmin admin : clinicAdmins) {
            this.emailService.sendMailForExaminationRequest(admin.getEmail());
        }

        return this.examinationRequestMapper.toDto(newRequest);

    }

    public List<AppointmentForListDTO> getRequestsOfClinic() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ClinicAdmin clinicAdmin = (ClinicAdmin) this.userRepository.findByEmail(username);

        List<AppointmentForListDTO> list = new ArrayList<>();

        for (ExaminationRequest request : this.examinationRequestRepository.findAllRequestsForAdmin(clinicAdmin.getClinic().getId())) {
            list.add(this.examinationRequestMapper.toForListDTO(request));
        }

        return list;

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public ExaminationRequestDTO createExaminationRequestPatient(ExaminationRequestDTO dto) throws Exception {

        //System.out.println("Transaction open: " + TransactionSynchronizationManager.isActualTransactionActive());

        ExaminationRequest er = new ExaminationRequest();

        LocalDate date = null;
        LocalTime startTime = null;
        LocalTime endTime = null;
        try {
            date = LocalDate.parse(dto.examDate);
            startTime = LocalTime.parse(dto.startTime);
            endTime = LocalTime.parse(dto.endTime);
        } catch (Exception e) {
            return null;
        }
        er.setExaminationDate(date);
        er.setStartTime(startTime);
        er.setEndTime(endTime);
        er.setType(this.examinationTypeRepository.findById(dto.type).orElseGet(null));
        er.setClinic(this.clinicRepository.findById(dto.clinic).orElseGet(null));

        try {
            Patient p = (Patient) this.userRepository.findById(dto.patient).orElseGet(null);
            er.setPatient(p);
        } catch (Exception e) {
            e.getStackTrace();
        }

        try {
            Doctor d = this.userRepository.findDoctorExaminationDetails(dto.doctor);
            er.setDoctor(d);
            if(this.examinationService.isDoctorFreeAtGivenTime(date, startTime, endTime, d).equals("Not ok")) {
                throw new GenericConflictException("Chosen doctor is not available at given time.");
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

        er.setStatus(ExaminationRequestStatus.WAITING_FOR_ADMIN);

        try {
            List<ClinicAdmin> clinicAdmins = this.userRepository.findClinicAdmins(dto.clinic);
            for(ClinicAdmin clinicAdmin: clinicAdmins) {
                this.emailService.sendMailForExaminationRequest(clinicAdmin.getEmail());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ExaminationRequest newRequest = this.examinationRequestRepository.save(er);

        //System.out.println("Transaction open: " + TransactionSynchronizationManager.isActualTransactionActive());

        return this.examinationRequestMapper.toDto(newRequest);

    }

}
