package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.exception.GenericConflictException;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.NurseMapper;
import com.example.clinicalCenter.mapper.PatientMapper;
import com.example.clinicalCenter.mapper.ReportMapper;
import com.example.clinicalCenter.model.*;
import com.example.clinicalCenter.model.enums.UserType;
import com.example.clinicalCenter.repository.*;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class NurseService implements ServiceInterface<NurseDTO> {

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private NurseAbsenceService nurseAbsenceService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private EmailService emailService;

    private NurseMapper nurseMapper;

    private PatientMapper patientMapper;

    private ReportMapper reportMapper;

    public NurseService() {
        this.nurseMapper = new NurseMapper();
        this.patientMapper = new PatientMapper();
        this.reportMapper = new ReportMapper();
    }

    @Override
    public List<NurseDTO> findAll() {
        return null;
    }

    public List<PatientDTO> findPatientsOfClinic() throws Exception {
        //return patientService.findByClinic(
        Long clinic_id = ((Nurse) userRepository.findByEmail(SecurityContextHolder.getContext()
                .getAuthentication().getName())).getClinic().getId();
        List<Operation> operations = operationRepository.findByClinic(clinic_id);
        List<Examination> examinations = examinationRepository.findByClinic(clinic_id);
        List<PatientDTO> retVal = new ArrayList<>();
        for (Operation o : operations) {
            PatientDTO dto = patientMapper.toDto(o.getPatient());
            if (!retVal.contains(dto)) {
                retVal.add(dto);
            }
        }
        for (Examination e : examinations) {
            if (e.getPatient() != null) {
                PatientDTO dto = patientMapper.toDto(e.getPatient());
                if (!retVal.contains(dto)) {
                    retVal.add(dto);
                }
            }
        }
        return retVal;
    }

    @Override
    public NurseDTO findOne(Long id) {
        return null;
    }

    public NurseEditDTO findOneForEdit(Long id) {
        Nurse nurse = (Nurse) this.userRepository.findById(id).orElseGet(null);
        return this.nurseMapper.toNurseEdit(nurse);
    }

    @Override
    public NurseDTO create(NurseDTO dto) throws Exception {
        String validationString = authenticationService.validateNurse(dto);
        if (!validationString.equals("OK")) {
            throw new ValidationException(validationString);
        }
        Nurse nurse = nurseMapper.toEntity(dto);
        nurse.setPassword(userDetailsService.encodePassword(dto.password));
        List<Authority> authorities = new ArrayList<Authority>();
        Authority a = new Authority();
        a.setType(UserType.ROLE_NURSE);
        authorities.add(a);
        nurse.setAuthorities(authorities);
        nurse.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            nurse.setClinic(clinicAdmin.getClinic());
        }

        this.emailService.sendToCreatedUser(nurse.getEmail(), dto.password);
        return this.nurseMapper.toDto(this.userRepository.save(nurse));
    }

    public ArrayList<NurseDTO> getFreeNurses(ExaminationParamsDTO examinationParams) {
        ArrayList<NurseDTO> freeNurses = new ArrayList<>();
        LocalDate examDate = LocalDate.parse(examinationParams.examDate);
        LocalTime startTime = LocalTime.parse(examinationParams.startTime);
        LocalTime endTime = LocalTime.parse(examinationParams.endTime);
        List<User> list = this.userRepository.findByType("NURSE");

        HashSet<Nurse> examinationNurses = this.examinationService.getNursesOnExaminations(
                examinationParams.type, examDate, startTime, endTime);

        list.removeAll(examinationNurses);

        List<Long> stuffOnAbsence = this.nurseAbsenceService.stuffOnAbsence(examDate);

        for (User u : list) {
            if (!(u instanceof Nurse)) {
                continue;
            }

            if (stuffOnAbsence.contains(u.getId())) {
                continue;
            }

            freeNurses.add(this.nurseMapper.toDto((Nurse) u));
        }

        return freeNurses;
    }

    public List<Nurse> freeClinicNursesAtGivenTime(Long clinicId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Nurse> freeNurses = new ArrayList<>();

        List<Nurse> allNursesOfClinic = this.userRepository.findNursesOfClinic(clinicId);

        HashSet<Nurse> examinationNurses = this.examinationService.getNursesOnExaminations(
                (long) 0, date, startTime, endTime);

        allNursesOfClinic.removeAll(examinationNurses);

        List<Long> stuffOnAbsence = this.nurseAbsenceService.stuffOnAbsence(date);

        for (Nurse n : allNursesOfClinic) {

            if (stuffOnAbsence.contains(n.getId())) {
                continue;
            }

            freeNurses.add(n);
        }

        return freeNurses;
    }

    @Override
    public NurseDTO update(NurseDTO dto) throws Exception {
        return null;
    }

    public NurseEditDTO update(NurseEditDTO dto) throws Exception {
        String validation = editValidation(dto);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        Nurse updatedNurse = this.nurseRepository.findById(dto.id).get();
        updatedNurse.setName(dto.name);
        updatedNurse.setSurname(dto.surname);
        updatedNurse.setPhone(dto.phone);
        Nurse nurse = this.nurseRepository.save(updatedNurse);
        return new NurseEditDTO(nurse.getId(), nurse.getEmail(), nurse.getName(), nurse.getSurname(), nurse.getPhone());
    }


    @Override
    public NurseDTO delete(Long id) throws Exception {
        User user = this.userRepository.findById(id).orElseGet(null);
        if (user != null && user instanceof Nurse) {
            if (!this.examinationService.getAlAvailableAndScheduledAndInProgressExaminationsOfNurse(id).isEmpty()) {
                throw new GenericConflictException("Selected nurse has some examinations and cannot be deleted.");
            }
            if (!user.isDeleted()) {
                user.delete();
            } else {
                throw new GenericConflictException("Nurse already deleted!");
            }
        }

        return this.nurseMapper.toDto((Nurse) this.userRepository.save(user));
    }

    public NurseDTO findByEmail(String email) {
        Nurse entity = this.nurseRepository.findByEmail(email);
        return this.nurseMapper.toDto(entity);
    }

    public ArrayList<NurseEditDTO> getNursesOfClinic() {
        ArrayList<NurseEditDTO> nursesOfClinic = new ArrayList<NurseEditDTO>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }
        for (User n : this.userRepository.findNursesOfClinic(clinicId)) {
            Nurse nurse = (Nurse) n;
            nursesOfClinic.add(this.nurseMapper.toNurseEdit(nurse));
        }

        return nursesOfClinic;
    }

    private List<NurseEditDTO> toNurseEditDTOList(List<Nurse> entityList) {
        List<NurseEditDTO> retVal = new ArrayList<>();
        for (Nurse nurse : entityList) {
            retVal.add(this.nurseMapper.toNurseEdit(nurse));
        }

        return retVal;
    }

    public List<NurseEditDTO> searchNurses(SearchNurseDTO searchNurse) throws Exception {
        String validation = dtoSearchValid(searchNurse);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);

        List<Nurse> nursesList = this.nurseRepository.findAll();

        Iterator i = nursesList.iterator();
        while (i.hasNext()) {
            Nurse nurse = (Nurse) i.next();
            if (isThereName(searchNurse.searchName)) {
                if (!this.containsName(nurse, searchNurse.searchName)) {
                    i.remove();
                    continue;
                }
            }

            if (isThereSurname(searchNurse.searchSurname)) {
                if (!this.containsSurname(nurse, searchNurse.searchSurname)) {
                    i.remove();
                    continue;
                }
            }

            if (isTherePhone(searchNurse.searchPhone)) {
                if (!this.containsPhone(nurse, searchNurse.searchPhone)) {
                    i.remove();
                    continue;
                }
            }

        }

        return this.toNurseEditDTOList(nursesList);
    }

    private boolean containsName(Nurse nurse, String searchName) {
        return nurse.getName().toLowerCase().contains(searchName.toLowerCase());
    }

    private boolean containsSurname(Nurse nurse, String searchSurname) {
        return nurse.getSurname().toLowerCase().contains(searchSurname.toLowerCase());
    }

    private boolean containsPhone(Nurse nurse, String phone) {
        return nurse.getPhone().toLowerCase().contains(phone.toLowerCase());
    }

    private String dtoSearchValid(SearchNurseDTO dto) {
        if (isThereName(dto.searchName) && dto.searchName.length() > 30) {
            return "Nurse's name cannot be longer than 30 characters.";
        }

        if (isThereSurname(dto.searchSurname) && dto.searchSurname.length() > 30) {
            return "Nurse's surname cannot be longer than 30 characters.";
        }

        if (isTherePhone(dto.searchPhone)) {
            if (!(dto.searchPhone.matches("^[0-9]+$"))) {
                return "Phone number can only contain numeric values.";
            }
            if (dto.searchPhone.length() > 10) {
                return "Phone number must be max 10 characters length.";
            }
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

    private String editValidation(NurseEditDTO dto) {
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

    public NurseDTO getLoggedIn() {
        if (!(userRepository.findByEmail(SecurityContextHolder.getContext()
                .getAuthentication().getName()) instanceof Nurse)) {
            return null;
        }
        return nurseMapper.toDto((Nurse) userRepository.findByEmail(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
    }

    public Nurse getRandomFreeNurse(LocalDate date, LocalTime startTime, LocalTime endTime, Clinic clinic) {
//        String username =  SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = this.userRepository.findByEmail(username);
//        Long clinicId = (long)0;
//        if(user instanceof ClinicAdmin) {
//            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
//            clinicId = clinicAdmin.getClinic().getId();
//        }

        Random random = new Random();
        List<Nurse> freeNurses = this.freeClinicNursesAtGivenTime(clinic.getId(), date, startTime, endTime);
        if (!freeNurses.isEmpty()) {
            int nurseIndex = random.nextInt(freeNurses.size());
            return freeNurses.get(nurseIndex);
        } else {
            String[] names = {"Sofija", "Marija", "Milica", "Jelena", "Bojana", "Vukosava", "Lena", "Aleksandra", "Martina", "Maja"};
            String[] surnames = {"Mikic", "Vunic", "Milosevic", "Bajic", "Prastalo", "Kobajagic", "Kesic", "Rogic", "Savic", "Jankovic"};
            int nameIndex = random.nextInt(names.length);
            int surnameIndex = random.nextInt(surnames.length);
            int phone = random.nextInt(9000000) + 1000000;
            Nurse nurse = new Nurse(getRandomEmail(), "qweqweqwe", names[nameIndex], surnames[surnameIndex], Integer.toString(phone));
            nurse.setClinic(clinic);
            this.userRepository.save(nurse);
            return nurse;
        }
    }

    private String getRandomEmail() {
        String charArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * charArray.length());
            salt.append(charArray.charAt(index));
        }
        salt.append("@maildrop.cc");
        return salt.toString();
    }


    public List<ReportReviewDTO> findReportsToReview() {
        List<Report> reports = reportRepository.findUnreviewedReportsOfNurse(userRepository.findByEmail(SecurityContextHolder.getContext()
                .getAuthentication().getName()).getId());
        List<ReportReviewDTO> retVal = new ArrayList<>();
        for (Report r : reports) {
            retVal.add(reportMapper.toReviewDTO(r));
        }
        return retVal;
    }

}
