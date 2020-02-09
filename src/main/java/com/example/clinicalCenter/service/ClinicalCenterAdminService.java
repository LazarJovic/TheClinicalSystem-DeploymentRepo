package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.ClinicalCenterAdminDTO;
import com.example.clinicalCenter.dto.ClinicalCenterAdminEditDTO;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.ClinicalCenterAdminMapper;
import com.example.clinicalCenter.model.Authority;
import com.example.clinicalCenter.model.ClinicalCenterAdmin;
import com.example.clinicalCenter.model.User;
import com.example.clinicalCenter.model.enums.UserType;
import com.example.clinicalCenter.repository.UserRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClinicalCenterAdminService implements ServiceInterface<ClinicalCenterAdminDTO> {

    private ClinicalCenterAdminMapper mapper = new ClinicalCenterAdminMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationService authenticationService;

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        if (!findAllEntity().isEmpty()) {
            return;
        }

        int i = 1;
        while (userDetailsService.emailTaken("centerAdmin" + i + "@maildrop.cc")) {
            i++;
        }

        ClinicalCenterAdminDTO dto = new ClinicalCenterAdminDTO(null, "centerAdmin" + i + "@maildrop.cc",
                "123456789", "123456789", "Admir", "Admirović", "123456");

        try {
            this.create(dto);
        } catch (Exception ignored) {

        }
    }

    @Override
    public List<ClinicalCenterAdminDTO> findAll() {
        List<ClinicalCenterAdminDTO> list = new ArrayList<>();
        for (User u : findAllEntity()) {
            list.add(mapper.toDto((ClinicalCenterAdmin) u));
        }
        return list;
    }

    public List<User> findAllEntity() {
        return userRepository.findByType("CENTER_ADMIN");
    }

    @Override
    public ClinicalCenterAdminDTO create(ClinicalCenterAdminDTO dto) throws Exception {
        String validationString = authenticationService.validateClinicCenterAdmin(dto);
        if (!validationString.equals("OK")) {
            throw new ValidationException(validationString);
        }
        ClinicalCenterAdmin clinicalCenterAdmin = mapper.toEntity(dto);
        clinicalCenterAdmin.setPassword(userDetailsService.encodePassword(dto.password));
        List<Authority> authorities = new ArrayList<Authority>();
        Authority a = new Authority();
        a.setType(UserType.ROLE_CENTER_ADMIN);
        authorities.add(a);
        clinicalCenterAdmin.setAuthorities(authorities);
        clinicalCenterAdmin.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
        return this.mapper.toDto(this.userRepository.save(clinicalCenterAdmin));
    }

    @Override
    public ClinicalCenterAdminDTO update(ClinicalCenterAdminDTO dto) throws Exception {
        return null;
    }

    public ClinicalCenterAdminEditDTO update(ClinicalCenterAdminEditDTO dto) throws Exception {
        String validation = editValidation(dto);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        ClinicalCenterAdmin updatedAdmin = (ClinicalCenterAdmin) this.userRepository.findByEmail(dto.email);
        updatedAdmin.setName(dto.name);
        updatedAdmin.setSurname(dto.surname);
        updatedAdmin.setPhone(dto.phone);
        ClinicalCenterAdmin admin = this.userRepository.save(updatedAdmin);
        return new ClinicalCenterAdminEditDTO(admin.getId(), admin.getEmail(), admin.getName(), admin.getSurname(), admin.getPhone());
    }

    @Override
    public ClinicalCenterAdminDTO findOne(Long id) {
        return null;
    }

    public ClinicalCenterAdminEditDTO findOneForEdit(Long id) {
        ClinicalCenterAdmin admin = (ClinicalCenterAdmin) this.userRepository.findById(id).orElseGet(null);
        return this.mapper.toClinicAdminEdit(admin);
    }

    @Override
    public ClinicalCenterAdminDTO delete(Long id) {
        return null;
    }

    private String editValidation(ClinicalCenterAdminEditDTO dto) {
        if (dto.name == null) {
            return "Name cannot be empty.";
        }
        if (!(dto.name.matches("^[A-ZŠĐŽČĆ][a-zšđćčžA-ZŠĐŽČĆ ]*$"))) {
            return "Name cannot contain special characters.";
        }
        if (dto.name.length() > 30) {
            return "Name must not be longer than 30 characters.";
        }

        if (dto.surname == null) {
            return "Surname cannot be empty.";
        }
        if (!(dto.surname.matches("^[A-ZŠĐŽČĆ][a-zšđćčžA-ZŠĐŽČĆ ]*$"))) {
            return "Surname cannot contain special characters.";
        }
        if (dto.surname.length() > 30) {
            return "Surname must not be longer than 30 characters.";
        }

        if (dto.phone == null) {
            return "Phone cannot be empty.";
        }
        if (!(dto.phone.matches("^[0-9]+$"))) {
            return "Phone number can only contain numeric values.";
        }
        if (dto.phone.length() < 6 || dto.phone.length() > 10) {
            return "Phone number must be between 6 and 10 characters.";
        }

        return "OK";
    }
}
