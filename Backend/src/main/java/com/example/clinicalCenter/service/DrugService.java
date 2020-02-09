package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.DrugDTO;
import com.example.clinicalCenter.exception.GenericConflictException;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.DrugMapper;
import com.example.clinicalCenter.model.Drug;
import com.example.clinicalCenter.repository.DrugRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DrugService implements ServiceInterface<DrugDTO> {

    @Autowired
    private DrugRepository repository;

    private DrugMapper drugMapper = new DrugMapper();

    @Override
    public DrugDTO findOne(Long id) {
        return null;
    }

    public Drug findOneEntity(Long id) {
        return repository.findById(id).get();
    }

    @Override
    public List<DrugDTO> findAll() {
        List<Drug> list = findAllEntity();
        List<DrugDTO> retVal = new ArrayList<>();
        for (Drug d : list) {
            retVal.add(drugMapper.toDto(d));
        }
        return retVal;
    }

    public List<Drug> findAllEntity() {
        return repository.findAll();
    }

    @Override
    public DrugDTO create(DrugDTO dto) throws Exception {
        String validation = dtoValid(dto);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        for (Drug d : repository.findAll()) {
            if (d.getCode().equals(dto.code))
                throw new GenericConflictException("Code " + dto.code + "already exists");
        }
        Drug entity = drugMapper.toEntity(dto);
        Drug createdEntity = this.repository.save(entity);
        return drugMapper.toDto(createdEntity);
    }

    @Override
    public DrugDTO update(DrugDTO dto) {
        return null;
    }

    @Override
    public DrugDTO delete(Long id) {
        return null;
    }

    private String dtoValid(DrugDTO dto) {
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
