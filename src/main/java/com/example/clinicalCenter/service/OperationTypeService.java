package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.ExaminationTypeDTO;
import com.example.clinicalCenter.dto.OperationTypeDTO;
import com.example.clinicalCenter.dto.SearchTypeDTO;
import com.example.clinicalCenter.exception.GenericConflictException;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.OperationTypeMapper;
import com.example.clinicalCenter.model.ClinicAdmin;
import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.model.OperationType;
import com.example.clinicalCenter.model.User;
import com.example.clinicalCenter.repository.OperationTypeRepository;
import com.example.clinicalCenter.repository.UserRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OperationTypeService implements ServiceInterface<OperationTypeDTO> {

    @Autowired
    private OperationTypeRepository operationTypeRepository;

    @Autowired
    private OperationService operationService;

    @Autowired
    private UserRepository userRepository;

    private OperationTypeMapper operationTypeMapper;

    public OperationTypeService() {
        operationTypeMapper = new OperationTypeMapper();
    }

    public List<OperationType> findAllEntity() {
        return this.operationTypeRepository.findAllActive();
    }

    @Override
    public List<OperationTypeDTO> findAll() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }
        else {
            Doctor doctor = (Doctor)user;
            clinicId = doctor.getClinic().getId();
        }


        return this.toOperationTypeDTOList(this.operationTypeRepository.findAllActiveOfClinic((clinicId)));
    }

    @Override
    public OperationTypeDTO findOne(Long id) {
        return null;
    }

    @Override
    @Transactional(readOnly = false)
    public OperationTypeDTO create(OperationTypeDTO dto) throws Exception {
        String validation = dtoValid(dto);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        OperationType entity = operationTypeMapper.toEntity(dto);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            entity.setClinic(clinicAdmin.getClinic());
            clinicId = clinicAdmin.getClinic().getId();
        }

        if (this.nameTaken(entity.getName(), clinicId)) {
            throw new GenericConflictException("Type name is already taken.");
        }

        OperationType createdEntity = this.operationTypeRepository.save(entity);
        return operationTypeMapper.toDto(createdEntity);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public OperationTypeDTO update(OperationTypeDTO dto) throws Exception {
        String validation = dtoValid(dto);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        if (!this.operationService.getAllScheduledAndInProgressOperationsOfOperationType(dto.id).isEmpty()) {
            throw new GenericConflictException("Selected type has some scheduled or in progress examinations.");
        }
        OperationType updatedType = this.operationTypeRepository.findById(dto.id).orElseGet(null);
        updatedType.setName(dto.name);
        updatedType.setPrice(Double.parseDouble(dto.price));

        OperationType operationType = this.operationTypeRepository.save(updatedType);
        return this.operationTypeMapper.toDto(operationType);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public OperationTypeDTO delete(Long id) throws Exception {
        OperationType type = this.operationTypeRepository.findById(id).orElseGet(null);
        if (!this.operationService.getAllScheduledAndInProgressOperationsOfOperationType(id).isEmpty()) {
            throw new GenericConflictException("Selected type has some scheduled or in progress operations.");
        }
        if (!type.isDeleted()) {
            type.delete();
        } else {
            throw new GenericConflictException("Operation type already deleted!");
        }

        return this.operationTypeMapper.toDto(this.operationTypeRepository.save(type));
    }


    public List<OperationTypeDTO> searchOperationTypes(SearchTypeDTO searchType) throws Exception {
        String validation = dtoSearchValid(searchType);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);

        List<OperationType> operationTypeList = this.findAllEntity();

        Iterator i = operationTypeList.iterator();
        while (i.hasNext()) {
            OperationType type = (OperationType) i.next();
            if (isThereName(searchType.searchName)) {
                if (!this.containsName(type, searchType.searchName)) {
                    i.remove();
                    continue;
                }

            }
            Double minPrice;
            Double maxPrice;
            if (this.isThereMinPrice(searchType.minPrice) && isThereMaxPrice(searchType.maxPrice)) {
                minPrice = Double.parseDouble(searchType.minPrice);
                maxPrice = Double.parseDouble(searchType.maxPrice);
                if (!this.isPriceBetween(type, minPrice, maxPrice))
                    i.remove();
            } else if (this.isThereMinPrice(searchType.minPrice) && !isThereMaxPrice(searchType.maxPrice)) {
                minPrice = Double.parseDouble(searchType.minPrice);
                if (!this.isPriceHigher(type, minPrice))
                    i.remove();
            } else if (!this.isThereMinPrice(searchType.minPrice) && isThereMaxPrice(searchType.maxPrice)) {
                maxPrice = Double.parseDouble(searchType.maxPrice);
                if (!this.isPriceLesser(type, maxPrice))
                    i.remove();
            }
        }

        return this.toOperationTypeDTOList(operationTypeList);
    }

    private List<OperationTypeDTO> toOperationTypeDTOList(List<OperationType> entityList) {
        List<OperationTypeDTO> retVal = new ArrayList<>();
        for (OperationType type : entityList) {
            retVal.add(this.operationTypeMapper.toDto(type));
        }

        return retVal;
    }

    private boolean containsName(OperationType type, String searchName) {
        return type.getName().toLowerCase().contains(searchName.toLowerCase());
    }

    private boolean isPriceHigher(OperationType type, Double minPrice) {
        return type.getPrice() > minPrice;
    }

    private boolean isPriceLesser(OperationType type, Double maxPrice) {
        return type.getPrice() < maxPrice;
    }

    private boolean isPriceBetween(OperationType type, Double minPrice, Double maxPrice) {
        if (minPrice.compareTo(maxPrice) == 0) {
            return type.getPrice() == maxPrice;
        } else {
            return type.getPrice() > minPrice && type.getPrice() < maxPrice;
        }


    }

    private String dtoSearchValid(SearchTypeDTO dto) {

        if (isThereName(dto.searchName) && dto.searchName.length() > 30) {
            return "Examination type name cannot be longer than 30 characters.";
        }
        Double minPrice;
        Double maxPrice;
        if (this.isThereMinPrice(dto.minPrice)) {
            try {
                minPrice = Double.parseDouble(dto.minPrice);
            } catch (Exception e) {
                return "Price must be numerical value.";
            }

            if (minPrice < 0) {
                return "Price must be positive numerical value.";
            }
        }

        if (this.isThereMaxPrice(dto.maxPrice)) {
            try {
                maxPrice = Double.parseDouble(dto.maxPrice);
            } catch (Exception e) {
                return "Price must be numerical value.";
            }

            if (maxPrice < 0) {
                return "Price must be positive numerical value.";
            }
        }

        if (isThereMaxPrice(dto.maxPrice) && isThereMinPrice(dto.minPrice)) {
            minPrice = Double.parseDouble(dto.minPrice);
            maxPrice = Double.parseDouble(dto.maxPrice);
            if (minPrice > maxPrice) {
                return "Max price must be greater than min price.";
            }
        }

        return "OK";
    }

    private boolean isThereName(String searchName) {
        return searchName != null && !searchName.equals("");
    }

    private boolean isThereMinPrice(String minPrice) {
        return minPrice != null && !minPrice.equals("");
    }

    private boolean isThereMaxPrice(String maxPrice) {
        return maxPrice != null && !maxPrice.equals("");
    }

    private String dtoValid(OperationTypeDTO dto) {

        if (dto.name.isEmpty()) {
            return "Examination type name cannot be empty.";
        }

        if (dto.price.isEmpty()) {
            return "Examination type price cannot be empty.";
        }

        if (dto.name.length() > 30) {
            return "Examination type name cannot be longer than30 characters.";
        }
        Double price;
        try {
            price = Double.parseDouble(dto.price);
        } catch (Exception e) {
            return "Price must be numerical value.";
        }

        if (price < 0) {
            return "Price must be positive numerical value.";
        }

        return "OK";
    }

    private boolean nameTaken(String typeName, Long clinicId) {
        List<OperationType> clinicOperationTypes = this.operationTypeRepository.findAllActiveOfClinic(clinicId);
        for (OperationType type : clinicOperationTypes) {
            if (type.getName().equals(typeName))
                return true;
        }

        return false;
    }
}
