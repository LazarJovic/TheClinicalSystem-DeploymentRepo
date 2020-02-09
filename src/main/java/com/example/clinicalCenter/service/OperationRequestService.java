package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.AppointmentForListDTO;
import com.example.clinicalCenter.dto.CreateAppointmentDTO;
import com.example.clinicalCenter.dto.OperationRequestDTO;
import com.example.clinicalCenter.exception.DateConflictException;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.OperationRequestMapper;
import com.example.clinicalCenter.model.ClinicAdmin;
import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.model.OperationRequest;
import com.example.clinicalCenter.model.Patient;
import com.example.clinicalCenter.model.enums.ExaminationRequestStatus;
import com.example.clinicalCenter.repository.OperationRequestRepository;
import com.example.clinicalCenter.repository.OperationTypeRepository;
import com.example.clinicalCenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@ComponentScan
@Service
public class OperationRequestService {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OperationRequestRepository operationRequestRepository;

    @Autowired
    private OperationTypeRepository operationTypeRepository;

    @Autowired
    private EmailService emailService;

    private OperationRequestMapper operationRequestMapper;

    public OperationRequestService() {
        this.operationRequestMapper = new OperationRequestMapper();
    }

    public OperationRequestDTO createOperationRequest(CreateAppointmentDTO dto) throws Exception {

        LocalDate date = LocalDate.parse(dto.date);
        LocalTime startTime = LocalTime.parse(dto.startTime);
        LocalTime endTime = LocalTime.parse(dto.endTime);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Doctor doctor = (Doctor) this.userRepository.findByEmail(username);

        String isOk = doctorService.isDoctorFreeAtGivenTime(date, startTime, endTime, doctor);

        if (!isOk.equals("OK")) {
            throw new ValidationException(isOk);
        }

        Patient patient = (Patient) userRepository.findById(dto.patientId).get();

        if (date.isBefore((LocalDate.now())) || date.isEqual(LocalDate.now())) {
            throw new DateConflictException("Can only send requests for tomorrow and onwards.");
        }

        OperationRequest operationRequest = new OperationRequest(date, startTime, endTime,
                this.operationTypeRepository.findById(dto.operationType).get(), null,
                doctor, patient, ExaminationRequestStatus.WAITING_FOR_ADMIN);

        operationRequest.setClinic(doctor.getClinic());

        OperationRequest newRequest = this.operationRequestRepository.save(operationRequest);

        List<ClinicAdmin> clinicAdmins = this.userRepository.findClinicAdmins(doctor.getClinic().getId());

        for (ClinicAdmin admin : clinicAdmins) {
            this.emailService.sendMailForOperationRequest(admin.getEmail());
        }

        return this.operationRequestMapper.toDto(newRequest);

    }

    public List<AppointmentForListDTO> getRequestsOfClinic() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ClinicAdmin clinicAdmin = (ClinicAdmin) this.userRepository.findByEmail(username);

        List<AppointmentForListDTO> list = new ArrayList<>();

        for (OperationRequest request : this.operationRequestRepository.findAllRequestsForAdmin(clinicAdmin.getClinic().getId())) {
            list.add(this.operationRequestMapper.toForListDTO(request));
        }

        return list;

    }

    public OperationRequestDTO findOne(Long id) {
        return operationRequestMapper.toDto(operationRequestRepository.findById(id).get());
    }
}
