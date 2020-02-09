package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.ExaminationTypeDTO;
import com.example.clinicalCenter.dto.SearchTypeDTO;
import com.example.clinicalCenter.exception.GenericConflictException;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.ExaminationTypeMapper;
import com.example.clinicalCenter.model.ClinicAdmin;
import com.example.clinicalCenter.model.ExaminationType;
import com.example.clinicalCenter.model.User;
import com.example.clinicalCenter.repository.ExaminationTypeRepository;
import com.example.clinicalCenter.repository.UserRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ComponentScan
@Service
@Transactional(readOnly = true)
public class ExaminationTypeService implements ServiceInterface<ExaminationTypeDTO> {

    @Autowired
    private ExaminationTypeRepository examinationTypeRepository;

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserRepository userRepository;

    private ExaminationTypeMapper examinationTypeMapper;

    public ExaminationTypeService() {
        examinationTypeMapper = new ExaminationTypeMapper();
    }

    public List<ExaminationType> findAllEntity() {
        return this.examinationTypeRepository.findAllActive();
    }

    @Override
    public List<ExaminationTypeDTO> findAll() {

        return this.toExaminationTypeDTOList(this.examinationTypeRepository.findAllActiveOfClinic(this.getClinicIdFromClinicAdmin()));
    }

    public List<ExaminationTypeDTO> getAllExaminationTypes() {
        return this.toExaminationTypeDTOList(this.examinationTypeRepository.findAllActive());
    }

    public ExaminationType findOneEntity(Long id) {
        return examinationTypeRepository.findById(id).get();
    }

    @Override
    public ExaminationTypeDTO findOne(Long id) {

        ExaminationType exType = this.examinationTypeRepository.findById(id).orElseGet(null);

        if (exType != null) {
            return this.examinationTypeMapper.toDto(exType);
        }

        return null;
    }

    @Override
    @Transactional(readOnly = false)
    public ExaminationTypeDTO create(ExaminationTypeDTO dto) throws Exception {
        String validation = dtoValid(dto);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        ExaminationType entity = examinationTypeMapper.toEntity(dto);
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
        ExaminationType createdEntity = this.examinationTypeRepository.save(entity);
        return examinationTypeMapper.toDto(createdEntity);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public ExaminationTypeDTO update(ExaminationTypeDTO dto) throws Exception {
        String validation = dtoValid(dto);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        if (!this.examinationService.getAllScheduledAndInProgressExaminationsOfExaminationType(dto.id).isEmpty()) {
            throw new GenericConflictException("Selected type has some scheduled or in progress examinations.");
        }
        ExaminationType updatedType = this.examinationTypeRepository.findById(dto.id).orElseGet(null);
        updatedType.setName(dto.name);
        updatedType.setPrice(Double.parseDouble(dto.price));

        ExaminationType examinationType = this.examinationTypeRepository.save(updatedType);
        return this.examinationTypeMapper.toDto(examinationType);
    }


    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public ExaminationTypeDTO delete(Long id) throws Exception {
        ExaminationType type = this.examinationTypeRepository.findById(id).orElseGet(null);
        if (!this.examinationService.getAllScheduledAndInProgressExaminationsOfExaminationType(id).isEmpty()) {
            throw new GenericConflictException("Selected type has some scheduled or in progress examinations.");
        } else if (!this.doctorService.getDoctorsOfExaminationType(id).isEmpty()) {
            throw new GenericConflictException("There are registered doctors of selected type.");
        }
        if (!type.isDeleted()) {
            type.delete();
        } else {
            throw new GenericConflictException("Examination type already deleted!");
        }

        return this.examinationTypeMapper.toDto(this.examinationTypeRepository.save(type));
    }

    public List<ExaminationTypeDTO> searchExaminationTypes(SearchTypeDTO searchType) throws Exception {
        String validation = dtoSearchValid(searchType);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);

        List<ExaminationType> examinationTypeList = this.findAllEntity();

        Iterator i = examinationTypeList.iterator();
        while (i.hasNext()) {
            ExaminationType type = (ExaminationType) i.next();
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

        return this.toExaminationTypeDTOList(examinationTypeList);

    }

    private List<ExaminationTypeDTO> toExaminationTypeDTOList(List<ExaminationType> entityList) {
        List<ExaminationTypeDTO> retVal = new ArrayList<>();
        for (ExaminationType type : entityList) {
            retVal.add(this.examinationTypeMapper.toDto(type));
        }

        return retVal;
    }

    private Long getClinicIdFromClinicAdmin() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }

        return clinicId;
    }

    private boolean containsName(ExaminationType type, String searchName) {
        return type.getName().toLowerCase().contains(searchName.toLowerCase());
    }

    private boolean isPriceHigher(ExaminationType type, Double minPrice) {
        return type.getPrice() > minPrice;
    }

    private boolean isPriceLesser(ExaminationType type, Double maxPrice) {
        return type.getPrice() < maxPrice;
    }

    private boolean isPriceBetween(ExaminationType type, Double minPrice, Double maxPrice) {
        if (minPrice.compareTo(maxPrice) == 0) {
            return type.getPrice() == maxPrice;
        } else {
            return type.getPrice() > minPrice && type.getPrice() < maxPrice;
        }


    }


    private String dtoValid(ExaminationTypeDTO dto) {

        if (dto.name.isEmpty()) {
            return "Examination type name cannot be empty.";
        }

        if (dto.price.isEmpty()) {
            return "Examination type price cannot be empty.";
        }

        if (dto.name.length() > 30) {
            return "Examination type name cannot be longer than 30 characters.";
        }
        Double price;
        try {
            price = Double.parseDouble(dto.price);
        } catch (Exception e) {
            return "Price must be positive numerical value.";
        }

        if (price < 0) {
            return "Price must be positive numerical value.";
        }

        return "OK";
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

    private boolean nameTaken(String typeName, Long clinicId) {
        List<ExaminationType> clinicExaminationTypes = this.examinationTypeRepository.findAllActiveOfClinic(clinicId);
        for (ExaminationType type : clinicExaminationTypes) {
            if (type.getName().equals(typeName))
                return true;
        }

        return false;
    }

}
