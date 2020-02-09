package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.DoctorRatingDTO;
import com.example.clinicalCenter.dto.DoctorRatingOperationDTO;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.DoctorRatingMapper;
import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.model.DoctorRating;
import com.example.clinicalCenter.model.Operation;
import com.example.clinicalCenter.model.Patient;
import com.example.clinicalCenter.repository.DoctorRatingRepository;
import com.example.clinicalCenter.repository.DoctorRepository;
import com.example.clinicalCenter.repository.OperationRepository;
import com.example.clinicalCenter.repository.UserRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DoctorRatingService implements ServiceInterface<DoctorRatingDTO> {

    @Autowired
    private DoctorRatingRepository doctorRatingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private OperationService operationService;

    @Autowired
    private OperationRepository operationRepository;

    private DoctorRatingMapper doctorRatingMapper;

    public DoctorRatingService() {
        this.doctorRatingMapper = new DoctorRatingMapper();
    }

    @Override
    public List<DoctorRatingDTO> findAll() {
        return null;
    }

    @Override
    public DoctorRatingDTO findOne(Long id) {
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    @Override
    public DoctorRatingDTO create(DoctorRatingDTO dto) throws Exception {

        if (dto == null)
            return null;

        DoctorRating doctorRating = this.doctorRatingMapper.toEntity(dto);
        Patient patient = this.userRepository.findPatient(dto.patientId);
        doctorRating.setPatient(patient);

        doctorRating.setRating(dto.doctorRating);

        Doctor doctor = this.userRepository.findDoctor(dto.doctorId);
        doctorRating.setDoctor(doctor);

        DoctorRating dr = this.doctorRatingRepository.save(doctorRating);
        double avg = this.doctorRatingRepository.doctorRatingAverage(doctor.getId());
        doctor.setRatingAvg(avg);
        Doctor newDoctor = this.doctorRepository.save(doctor);

        return this.doctorRatingMapper.toDto(dr);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public DoctorRatingOperationDTO createForOperation(DoctorRatingOperationDTO dto, Long id) {

        if (dto == null)
            return null;

        DoctorRating doctorRating = this.doctorRatingMapper.toEntityForOperation(dto);
        Patient patient = this.userRepository.findPatient(dto.patientId);
        doctorRating.setPatient(patient);

        doctorRating.setRating(dto.doctorRating);

        Operation operation = this.operationService.findOneEntity(dto.operationId);
        operation.setAvgRating(dto.doctorRating);
        this.operationRepository.save(operation);

        Doctor doctor = this.userRepository.findDoctor(id);
        doctorRating.setDoctor(doctor);
        this.doctorRatingRepository.save(doctorRating);
        double avg = this.doctorRatingRepository.doctorRatingAverage(doctor.getId());
        doctor.setRatingAvg(avg);
        this.doctorRepository.save(doctor);

        return dto;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    @Override
    public DoctorRatingDTO update(DoctorRatingDTO dto) throws Exception {

        if (dto == null || dto.doctorRating < 0 || dto.doctorRating > 5) {
            String validation = "Fields are not filled correctly!";
            throw new ValidationException(validation);
        }

        DoctorRating dr = this.doctorRatingRepository.findByPatientAndDoctor(dto.doctorId, dto.patientId);
        dr.setRating(dto.doctorRating);

        DoctorRating dr1 = this.doctorRatingRepository.save(dr);
        double avg = this.doctorRatingRepository.doctorRatingAverage(dto.doctorId);
        Doctor doctor = this.doctorService.findOneEntity(dto.doctorId);
        doctor.setRatingAvg(avg);
        Doctor newDoctor = this.doctorRepository.save(doctor);

        return this.doctorRatingMapper.toDto(dr1);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public DoctorRatingOperationDTO updateForOperation(DoctorRatingOperationDTO dto, Long id) throws Exception {

        if (dto == null || dto.doctorRating < 0 || dto.doctorRating > 5) {
            String validation = "Fields are not filled correctly!";
            throw new ValidationException(validation);
        }

        Operation operation = this.operationService.findOneEntity(dto.operationId);
        operation.setAvgRating(dto.doctorRating);
        this.operationRepository.save(operation);

        DoctorRating dr = this.doctorRatingRepository.findByPatientAndDoctor(id, dto.patientId);
        dr.setRating(dto.doctorRating);
        this.doctorRatingRepository.save(dr);
        double avg = this.doctorRatingRepository.doctorRatingAverage(id);
        Doctor doctor = this.doctorService.findOneEntity(id);
        doctor.setRatingAvg(avg);
        this.doctorRepository.save(doctor);

        return dto;
    }

    @Override
    public DoctorRatingDTO delete(Long id) throws Exception {
        return null;
    }

    public boolean alreadyRated(DoctorRatingDTO dto) {
        DoctorRating dr = this.doctorRatingRepository.findByPatientAndDoctor(dto.doctorId, dto.patientId);
        return dr != null;
    }

    public boolean alreadyRatedForOperation(Long doctorId, Long patientId) {
        DoctorRating dr = this.doctorRatingRepository.findByPatientAndDoctor(doctorId, patientId);
        return dr != null;
    }
}
