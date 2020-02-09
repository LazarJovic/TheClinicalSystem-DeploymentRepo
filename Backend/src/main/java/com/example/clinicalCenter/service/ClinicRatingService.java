package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.ClinicRatingDTO;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.ClinicRatingMapper;
import com.example.clinicalCenter.model.Clinic;
import com.example.clinicalCenter.model.ClinicRating;
import com.example.clinicalCenter.model.Patient;
import com.example.clinicalCenter.repository.ClinicRatingRepository;
import com.example.clinicalCenter.repository.ClinicRepository;
import com.example.clinicalCenter.repository.UserRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClinicRatingService implements ServiceInterface<ClinicRatingDTO> {

    @Autowired
    private ClinicRatingRepository clinicRatingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClinicService clinicService;

    @Autowired
    private ClinicRepository clinicRepository;

    private ClinicRatingMapper clinicRatingMapper;

    public ClinicRatingService() {
        this.clinicRatingMapper = new ClinicRatingMapper();
    }

    @Override
    public List<ClinicRatingDTO> findAll() {
        return null;
    }

    @Override
    public ClinicRatingDTO findOne(Long id) {
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    @Override
    public ClinicRatingDTO create(ClinicRatingDTO dto) throws Exception {


        if (dto == null)
            return null;
        ClinicRating clinicRating = this.clinicRatingMapper.toEntity(dto);
        Patient patient = this.userRepository.findPatient(dto.patientId);
        clinicRating.setPatient(patient);

        clinicRating.setRating(dto.clinicRating);

        Clinic clinic = this.clinicService.findOneEntity(dto.clinicId);
        clinicRating.setClinic(clinic);

        ClinicRating cr = this.clinicRatingRepository.save(clinicRating);
        double avg = this.clinicRatingRepository.clinicRatingAverage(clinic.getId());
        clinic.setRatingAvg(avg);
        this.clinicRepository.save(clinic);

        return this.clinicRatingMapper.toDto(cr);
    }

    @Transactional(readOnly =  false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    @Override
    public ClinicRatingDTO update(ClinicRatingDTO dto) throws Exception {

        if (dto == null || dto.clinicRating < 0 || dto.clinicRating > 5) {
            String validation = "Fields are not filled correctly!";
            throw new ValidationException(validation);
        }
        ClinicRating cr = this.clinicRatingRepository.findByPatientAndClinic(dto.clinicId, dto.patientId);
        cr.setRating(dto.clinicRating);

        ClinicRating cr1 = this.clinicRatingRepository.save(cr);
        double avg = this.clinicRatingRepository.clinicRatingAverage(dto.clinicId);
        Clinic clinic = this.clinicService.findOneEntity(dto.clinicId);
        clinic.setRatingAvg(avg);
        Clinic newClinic = this.clinicRepository.save(clinic);

        return this.clinicRatingMapper.toDto(cr1);
    }

    public boolean alreadyRated(ClinicRatingDTO dto) {
        ClinicRating cr = this.clinicRatingRepository.findByPatientAndClinic(dto.clinicId, dto.patientId);
        return cr != null;
    }

    @Override
    public ClinicRatingDTO delete(Long id) throws Exception {
        return null;
    }
}
