package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.MedicalRecordDTO;
import com.example.clinicalCenter.dto.PatientMedicalRecordDTO;
import com.example.clinicalCenter.exception.NotFoundException;
import com.example.clinicalCenter.mapper.MedicalRecordMapper;
import com.example.clinicalCenter.model.MedicalRecord;
import com.example.clinicalCenter.model.Patient;
import com.example.clinicalCenter.repository.MedicalRecordRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MedicalRecordService implements ServiceInterface<MedicalRecordDTO> {

    @Autowired
    private MedicalRecordRepository repository;

    private MedicalRecordMapper mapper = new MedicalRecordMapper();

    @Autowired
    private UserService userService;

    @Override
    public MedicalRecordDTO findOne(Long id) {
        return mapper.toDto(repository.findById(id).get());
    }

    public PatientMedicalRecordDTO findByPatient(Long id) throws Exception {
        Patient patient = (Patient) userService.findById(id);
        if (patient == null)
            throw new NotFoundException();
        if (patient.getRecord() == null) {
            MedicalRecord medicalRecord = new MedicalRecord();
            patient.setRecord(medicalRecord);
            medicalRecord.setPatient(patient);
            this.repository.save(medicalRecord);
        }
        MedicalRecordDTO medicalRecordDTO = mapper.toDto(patient.getRecord());
        return new PatientMedicalRecordDTO(patient.getId(), patient.getEmail(), patient.getName(), patient.getSurname(),
                medicalRecordDTO.id);
    }

    @Override
    public List<MedicalRecordDTO> findAll() {
        return null;
    }

    @Override
    public MedicalRecordDTO create(MedicalRecordDTO dto) {
        return null;
    }

    public MedicalRecord saveEntity(MedicalRecord medicalRecord) {
        return this.repository.save(medicalRecord);
    }

    @Override
    public MedicalRecordDTO update(MedicalRecordDTO dto) throws Exception {
        if (dto == null || dto.patient_id == null || !(userService.findById(dto.patient_id) instanceof Patient)) {
            throw new NotFoundException();
        }
        MedicalRecord medicalRecord = repository.findById(dto.id).get();
        medicalRecord.setHeight(dto.height);
        medicalRecord.setWeight(dto.weight);
        if (dto.birthDate != null)
            try {
                medicalRecord.setBirthDate(LocalDate.parse(dto.birthDate));
            } catch (Exception ignored) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'")
                        .withZone(ZoneId.of("UTC"));
                medicalRecord.setBirthDate(LocalDateTime.parse(dto.birthDate, formatter).toLocalDate().plusDays(1));
            }
        medicalRecord.setBloodType(dto.bloodType);
        this.repository.save(medicalRecord);
        return mapper.toDto(medicalRecord);
    }

    @Override
    public MedicalRecordDTO delete(Long id) {
        return null;
    }
}
