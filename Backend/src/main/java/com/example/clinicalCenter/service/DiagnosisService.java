package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.DiagnosisDTO;
import com.example.clinicalCenter.exception.GenericConflictException;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.DiagnosisMapper;
import com.example.clinicalCenter.model.Diagnosis;
import com.example.clinicalCenter.repository.DiagnosisRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiagnosisService implements ServiceInterface<DiagnosisDTO> {

    @Autowired
    private DiagnosisRepository repository;

    private DiagnosisMapper diagnosisMapper = new DiagnosisMapper();

    @Override
    public DiagnosisDTO findOne(Long id) {
        return null;
    }

    public Diagnosis findOneEntity(Long id) {
        return repository.findById(id).get();
    }

    @Override
    public List<DiagnosisDTO> findAll() {
        List<Diagnosis> list = findAllEntity();
        List<DiagnosisDTO> retVal = new ArrayList<>();
        for (Diagnosis d : list) {
            retVal.add(diagnosisMapper.toDto(d));
        }
        return retVal;
    }

    public List<Diagnosis> findAllEntity() {
        return repository.findAll();
    }

    @Override
    public DiagnosisDTO create(DiagnosisDTO dto) throws Exception {
        String validation = dtoValid(dto);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        for (Diagnosis d : repository.findAll()) {
            if (d.getCode().equals(dto.code))
                throw new GenericConflictException("Code " + dto.code + "already exists");
        }
        Diagnosis entity = diagnosisMapper.toEntity(dto);
        Diagnosis createdEntity = this.repository.save(entity);
        return diagnosisMapper.toDto(createdEntity);
    }

    @Override
    public DiagnosisDTO update(DiagnosisDTO dto) {
        return null;
    }

    @Override
    public DiagnosisDTO delete(Long id) {
        return null;
    }

    private String dtoValid(DiagnosisDTO dto) {
        if (dto.name.isEmpty()) {
            return "Name cannot be empty.";
        }

        if (dto.name.length() > 30) {
            return "Name cannot be longer than 30 characters.";
        }

        if (dto.code.isEmpty()) {
            return "Code cannot be empty.";
        }

        if (dto.code.length() > 10) {
            return "Code cannot be longer than 10 characters.";
        }

        return "OK";
    }

}
