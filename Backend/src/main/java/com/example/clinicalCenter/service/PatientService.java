package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.PatientDTO;
import com.example.clinicalCenter.dto.PatientEditDTO;
import com.example.clinicalCenter.dto.SearchPatientDTO;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.PatientMapper;
import com.example.clinicalCenter.model.*;
import com.example.clinicalCenter.model.enums.UserType;
import com.example.clinicalCenter.repository.ExaminationRepository;
import com.example.clinicalCenter.repository.OperationRepository;
import com.example.clinicalCenter.repository.PatientRepository;
import com.example.clinicalCenter.repository.UserRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ComponentScan
@Service
public class PatientService implements ServiceInterface<PatientDTO> {

    @Autowired
    private PatientRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    private PatientMapper mapper = new PatientMapper();

    private PatientMapper patientMapper;

    public PatientService() {
        this.patientMapper = new PatientMapper();
    }

    @Override
    public PatientDTO findOne(Long id) {
        return mapper.toDto((Patient) userRepository.findById(id).get());
    }

    @Override
    public List<PatientDTO> findAll() {
        List<PatientDTO> list = new ArrayList<>();
        for (User u : findAllEntity()) {
            list.add(mapper.toDto((Patient) u));
        }
        return list;
    }

    public List<User> findAllEntity() {
        return userRepository.findByType("PATIENT");
    }

    @Override
    public PatientDTO create(PatientDTO dto) {
        Patient patient = this.patientMapper.toEntity(dto);
        patient.setPassword(userDetailsService.encodePassword(dto.password));
        List<Authority> authorities = new ArrayList<Authority>();
        Authority a = new Authority();
        a.setType(UserType.ROLE_PATIENT);
        authorities.add(a);
        patient.setAuthorities(authorities);
        patient.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
        //dodavanje medical recorda
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setPatient(patient);
        patient.setRecord(medicalRecord);
        return this.mapper.toDto(this.userRepository.save(patient));
    }

    @Override
    public PatientDTO update(PatientDTO dto) throws Exception {
        return null;
    }

    public PatientEditDTO update(PatientEditDTO dto) throws Exception {
        String validation = editValidation(dto);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        Patient updatedPatient = this.repository.findById(dto.id).orElseGet(null);
        updatedPatient.setName(dto.name);
        updatedPatient.setSurname(dto.surname);
        updatedPatient.setAddress(dto.address);
        updatedPatient.setCity(dto.city);
        updatedPatient.setCountry(dto.country);
        updatedPatient.setPhone(dto.phone);
        Patient patient = this.repository.save(updatedPatient);
        return this.patientMapper.toDtoForEdit(patient);
    }

    public PatientEditDTO findOneForEdit(Long id) {
        Patient patient = (Patient) this.userRepository.findById(id).orElseGet(null);
        return this.mapper.toDtoForEdit(patient);
    }

    public List<PatientDTO> findPatientsOfClinic() throws Exception {
        User loggedUser = userRepository.findByEmail(SecurityContextHolder.getContext()
                .getAuthentication().getName());
        Long clinic_id;
        if (loggedUser instanceof Nurse) {
            Nurse nurse = (Nurse) loggedUser;
            clinic_id = nurse.getClinic().getId();
        } else {
            Doctor doctor = (Doctor) loggedUser;
            clinic_id = doctor.getClinic().getId();
        }
        List<Operation> operations = operationRepository.findAllScheduledAndInProgressAndFinishedOperationsOfClinic(clinic_id);
        List<Examination> examinations = examinationRepository.findAllScheduledAndInProgressAndFinishedExaminationsOfClinic(clinic_id);
        List<PatientDTO> retVal = new ArrayList<>();
        for (Operation o : operations) {
            PatientDTO dto = patientMapper.toDto(o.getPatient());
            if (!retVal.contains(dto)) {
                retVal.add(dto);
            }
        }
        for (Examination e : examinations) {
            PatientDTO dto = patientMapper.toDto(e.getPatient());
            if (!retVal.contains(dto)) {
                retVal.add(dto);
            }
        }
        return retVal;
    }

    @Override
    public PatientDTO delete(Long id) {
        return null;
    }

    public PatientDTO findByEmail(String email) {
        Patient patient = this.repository.findByEmail(email);
        return patientMapper.toDto(patient);
    }

    public boolean medicalRecordAccessCheck(Long patientId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        if (user instanceof Doctor) {
            List<Examination> patientDoctorExaminations = this.examinationRepository.
                    findAllPatientDoctorFinishedOrInProgressExaminations(user.getId(), patientId);
            boolean isThere = false;
            List<Operation> patientDoctorOperations = this.operationRepository.
                    findFinishedAndInProgressOperationsPatientDoctor(user.getId(), patientId);
            for (Operation operation : patientDoctorOperations) {
                if (operation.getPatient().getId() == patientId) {
                    isThere = true;
                }
            }

            return !patientDoctorExaminations.isEmpty() || isThere;
        } else {
            List<Examination> patientNurseExaminations = this.examinationRepository.
                    findAllPatientNurseFinishedOrInProgressExaminations(user.getId(), patientId);
            return !patientNurseExaminations.isEmpty();
        }

    }

    private List<Patient> toEntityList(List<PatientDTO> dtoList) {
        List<Patient> retVal = new ArrayList<>();
        for (PatientDTO dto : dtoList) {
            retVal.add(this.patientMapper.toEntity(dto));
        }

        return retVal;
    }

    private List<PatientDTO> toPatientDTOList(List<Patient> entityList) {
        List<PatientDTO> retVal = new ArrayList<>();
        for (Patient entity : entityList) {
            retVal.add(this.patientMapper.toDto(entity));
        }

        return retVal;
    }

    public List<PatientDTO> searchPatients(SearchPatientDTO searchPatient) throws Exception {
        String validation = dtoSearchValid(searchPatient);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);

        List<Patient> patientsList = this.toEntityList(findPatientsOfClinic());

        Iterator i = patientsList.iterator();
        while (i.hasNext()) {
            Patient patient = (Patient) i.next();
            if (isThereName(searchPatient.searchName)) {
                if (!this.containsName(patient, searchPatient.searchName)) {
                    i.remove();
                    continue;
                }
            }

            if (isThereSurname(searchPatient.searchSurname)) {
                if (!this.containsSurname(patient, searchPatient.searchSurname)) {
                    i.remove();
                    continue;
                }
            }

            if (isTherePhone(searchPatient.searchPhone)) {
                if (!this.containsPhone(patient, searchPatient.searchPhone)) {
                    i.remove();
                    continue;
                }
            }

            if (isThereAddress(searchPatient.searchAddress)) {
                if (!this.containsAddress(patient, searchPatient.searchAddress)) {
                    i.remove();
                    continue;
                }
            }

            if (isThereCity(searchPatient.searchCity)) {
                if (!this.containsCity(patient, searchPatient.searchCity)) {
                    i.remove();
                    continue;
                }
            }

            if (isThereSocialNumber(searchPatient.searchSocialNumber)) {
                if (!patient.getSocialSecurityNumber().equals(searchPatient.searchSocialNumber)) {
                    i.remove();
                    continue;
                }
            }

        }

        return this.toPatientDTOList(patientsList);
    }

    private boolean containsName(Patient patient, String searchName) {
        return patient.getName().toLowerCase().contains(searchName.toLowerCase());
    }

    private boolean containsSurname(Patient patient, String searchSurname) {
        return patient.getSurname().toLowerCase().contains(searchSurname.toLowerCase());
    }

    private boolean containsPhone(Patient patient, String phone) {
        return patient.getPhone().toLowerCase().contains(phone.toLowerCase());
    }

    private boolean containsAddress(Patient patient, String address) {
        return patient.getAddress().toLowerCase().contains(address.toLowerCase());
    }

    private boolean containsCity(Patient patient, String city) {
        return patient.getCity().toLowerCase().contains(city.toLowerCase());
    }

    private String dtoSearchValid(SearchPatientDTO dto) {
        if (isThereName(dto.searchName) && dto.searchName.length() > 30) {
            return "Patient's name cannot be longer than 30 characters.";
        }

        if (isThereSurname(dto.searchSurname) && dto.searchSurname.length() > 30) {
            return "Patient's surname cannot be longer than 30 characters.";
        }

        if (isTherePhone(dto.searchPhone)) {
            if (!(dto.searchPhone.matches("^[0-9]+$"))) {
                return "Phone number can only contain numeric values.";
            }
            if (dto.searchPhone.length() > 10) {
                return "Phone number must be max 10 characters length.";
            }
        }

        if (isThereAddress(dto.searchAddress) && dto.searchAddress.length() > 50) {
            return "Patient's address cannot be longer than 50 characters.";
        }

        if (isThereCity(dto.searchCity) && dto.searchCity.length() > 50) {
            return "Patient's city cannot be longer than 30 characters.";
        }

        if (isThereSocialNumber(dto.searchSocialNumber) && dto.searchSocialNumber.length() > 6) {
            return "Patient's social security number cannot be longer than 6 characters.";
        }

        return "OK";
    }

    private boolean isThereName(String searchName) {
        return searchName != null && !searchName.equals("");
    }

    private boolean isThereSurname(String searchSurname) {
        return searchSurname != null && !searchSurname.equals("");
    }

    private boolean isTherePhone(String searchPhone) {
        return searchPhone != null && !searchPhone.equals("");
    }

    private boolean isThereAddress(String searchAddress) {
        return searchAddress != null && !searchAddress.equals("");
    }

    private boolean isThereCity(String searchCity) {
        return searchCity != null && !searchCity.equals("");
    }

    private boolean isThereSocialNumber(String searchSocialNumber) {
        return searchSocialNumber != null && !searchSocialNumber.equals("");
    }

    private String editValidation(PatientEditDTO dto) {
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

        if (dto.address == null) {
            return "Address cannot be empty.";
        }
        if (!(dto.address.matches("^[A-ZŠĐŽČĆ][a-zšđćčžA-ZŠĐŽČĆ0-9 ]*$"))) {
            return "Address cannot contain special characters.";
        }
        if (dto.address.length() > 50) {
            return "Address must not be longer than 50 characters.";
        }

        if (dto.city == null) {
            return "City cannot be empty.";
        }
        if (!(dto.city.matches("^[A-ZŠĐŽČĆ][a-zšđćčžA-ZŠĐŽČĆ ]*$"))) {
            return "City cannot contain special characters.";
        }
        if (dto.city.length() > 30) {
            return "City must not be longer than 30 characters.";
        }

        if (dto.country == null) {
            return "Country cannot be empty.";
        }
        if (!(dto.country.matches("^[A-ZŠĐŽČĆ][a-zšđćčžA-ZŠĐŽČĆ ]*$"))) {
            return "Country cannot contain special characters.";
        }
        if (dto.country.length() > 30) {
            return "Country must not be longer than 30 characters.";
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
