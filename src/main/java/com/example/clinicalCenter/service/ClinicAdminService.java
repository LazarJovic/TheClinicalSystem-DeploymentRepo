package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.ClinicAdminMapper;
import com.example.clinicalCenter.model.Authority;
import com.example.clinicalCenter.model.ClinicAdmin;
import com.example.clinicalCenter.model.enums.UserType;
import com.example.clinicalCenter.repository.ClinicAdminRepository;
import com.example.clinicalCenter.repository.UserRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClinicAdminService implements ServiceInterface<ClinicAdminDTO> {

    @Autowired
    private ClinicAdminRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ClinicService clinicService;

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private OperationService operationService;

    @Autowired
    private EmailService emailService;

    private ClinicAdminMapper mapper;

    public ClinicAdminService() {
        this.mapper = new ClinicAdminMapper();
    }

    @Override
    public List<ClinicAdminDTO> findAll() {
        List<ClinicAdmin> admins = repository.findAll();
        List<ClinicAdminDTO> retVal = new ArrayList<>();
        for (ClinicAdmin admin : admins) {
            retVal.add(mapper.toDto(admin));
        }
        return retVal;
    }

    @Override
    public ClinicAdminDTO findOne(Long id) {
        return null;
    }

    public ClinicAdminEditDTO findOneForEdit(Long id) {
        ClinicAdmin admin = (ClinicAdmin) this.userRepository.findById(id).orElseGet(null);
        return this.mapper.toClinicAdminEdit(admin);
    }

    @Override
    public ClinicAdminDTO create(ClinicAdminDTO dto) throws Exception {
        String validationString = authenticationService.validateClinicAdmin(dto);
        if (!validationString.equals("OK")) {
            throw new ValidationException(validationString);
        }
        ClinicAdmin admin = mapper.toEntity(dto);
        admin.setClinic(clinicService.findOneEntity(dto.clinic));
        admin.setPassword(userDetailsService.encodePassword(dto.password));
        List<Authority> authorities = new ArrayList<Authority>();
        Authority a = new Authority();
        a.setType(UserType.ROLE_CLINIC_ADMIN);
        authorities.add(a);
        admin.setAuthorities(authorities);
//        Clinic clinic = clinicService.findOneEntity(dto.clinic); uradjeno u mapperu i konstruktoru admina
//        admin.setClinic(clinic);
        admin.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));

        this.emailService.sendToCreatedUser(admin.getEmail(), dto.password);
        return this.mapper.toDto(this.userRepository.save(admin));
    }

    @Override
    public ClinicAdminDTO update(ClinicAdminDTO dto) throws Exception {
        return null;
    }

    public ClinicAdminEditDTO update(ClinicAdminEditDTO dto) throws Exception {
        String validation = editValidation(dto);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        ClinicAdmin updatedAdmin = this.repository.findById(dto.id).get();
        updatedAdmin.setName(dto.name);
        updatedAdmin.setSurname(dto.surname);
        updatedAdmin.setPhone(dto.phone);
        ClinicAdmin admin = this.repository.save(updatedAdmin);
        return new ClinicAdminEditDTO(admin.getId(), admin.getEmail(), admin.getName(), admin.getSurname(), admin.getPhone());
    }

    public ClinicAdminDTO findByEmail(String email) {
        ClinicAdmin entity = this.repository.findByEmail(email);
        return mapper.toDto(entity);
    }

    public List<BusinessChartDTO> getDailyReview() {
        List<BusinessBarDTO> examinationsDailyNumbers = this.examinationService.getDailyNumbers();
        List<BusinessBarDTO> operationsDailyNumbers = this.operationService.getDailyNumbers();

        List<BusinessChartDTO> chartsList = new ArrayList<>();
        String[] days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < 7; i++) {
            List<BusinessBarDTO> bars = new ArrayList<>();
            bars.add(examinationsDailyNumbers.get(i));
            bars.add(operationsDailyNumbers.get(i));
            chartsList.add(new BusinessChartDTO(days[i], bars));
        }

        return chartsList;
    }

    public List<BusinessChartDTO> getWeeklyReview() {
        List<BusinessBarDTO> examinationsWeeklyNumbers = this.examinationService.getWeeklyNumbers();
        List<BusinessBarDTO> operationsWeeklyNumbers = this.operationService.getWeeklyNumbers();

        List<BusinessChartDTO> chartsList = new ArrayList<>();
        String[] days = new String[]{"Week 1", "Week 2", "Week 3", "Week 4", "Week 5"};
        for (int i = 0; i < 5; i++) {
            List<BusinessBarDTO> bars = new ArrayList<>();
            bars.add(examinationsWeeklyNumbers.get(i));
            bars.add(operationsWeeklyNumbers.get(i));
            chartsList.add(new BusinessChartDTO(days[i], bars));
        }

        return chartsList;
    }

    public List<BusinessChartDTO> getMonthlyReview() {
        List<BusinessBarDTO> examinationsMonthlyNumbers = this.examinationService.getMonthlyNumbers();
        List<BusinessBarDTO> operationsMonthlyNumbers = this.operationService.getMonthlyNumbers();

        List<BusinessChartDTO> chartsList = new ArrayList<>();
        String[] days = new String[]{"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        for (int i = 0; i < 12; i++) {
            List<BusinessBarDTO> bars = new ArrayList<>();
            bars.add(examinationsMonthlyNumbers.get(i));
            bars.add(operationsMonthlyNumbers.get(i));
            chartsList.add(new BusinessChartDTO(days[i], bars));
        }

        return chartsList;
    }

    public double getClinicIncome(ClinicIncomeDatesDTO incomePeriod) {
        LocalDate startDate = null;
        LocalDate endDate = null;
        if (isThereIncomeStart(incomePeriod.incomeStart)) {
            try {
                startDate = LocalDate.parse(incomePeriod.incomeStart);
            } catch (Exception e) {
                return -1;
            }
        }
        if (isThereIncomeEnd(incomePeriod.incomeEnd)) {
            try {
                endDate = LocalDate.parse(incomePeriod.incomeEnd);
            } catch (Exception e) {
                return -1;
            }
        }

        if (!isThereIncomeStart(incomePeriod.incomeStart) && !isThereIncomeEnd(incomePeriod.incomeEnd)) {
            return -2;
        }
        double examinationsIncome = this.examinationService.getIncomeFromExaminations(startDate, endDate);
        double operationsIncome = this.operationService.getIncomeFromOperations(startDate, endDate);

        return examinationsIncome + operationsIncome;
    }

    private boolean isThereIncomeStart(String incomeStart) {
        return incomeStart != null && !incomeStart.equals("");
    }

    private boolean isThereIncomeEnd(String incomeEnd) {
        return incomeEnd != null && !incomeEnd.equals("");
    }

    @Override
    public ClinicAdminDTO delete(Long id) {
        return null;
    }

    private String editValidation(ClinicAdminEditDTO dto) {
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
