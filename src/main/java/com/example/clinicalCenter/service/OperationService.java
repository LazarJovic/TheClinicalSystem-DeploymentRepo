package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.exception.AlreadyInProgressException;
import com.example.clinicalCenter.exception.BadRequestException;
import com.example.clinicalCenter.exception.CannotStartException;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.OperationForListMapper;
import com.example.clinicalCenter.mapper.OperationMapper;
import com.example.clinicalCenter.model.*;
import com.example.clinicalCenter.model.enums.ExaminationRequestStatus;
import com.example.clinicalCenter.repository.*;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional(readOnly = false)
public class OperationService implements ServiceInterface<OperationDTO> {

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private OperationTypeRepository operationTypeRepository;

    @Autowired
    private OperationRequestRepository operationRequestRepository;

    @Autowired
    private ClinicRatingRepository clinicRatingRepository;

    @Autowired
    private EmailService emailService;

    private OperationForListMapper operationForListMapper;

    private OperationMapper operationMapper = new OperationMapper();

    public OperationService() {
        this.operationForListMapper = new OperationForListMapper();
    }

    @Override
    public List<OperationDTO> findAll() {
        return null;
    }

    @Override
    public OperationDTO findOne(Long id) {
        return operationMapper.toDto(findOneEntity(id));
    }

    public Operation findOneEntity(Long id) {
        return operationRepository.findById(id).get();
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public OperationDTO create(OperationDTO dto) throws Exception {
        String validateTime = validateTime(dto.startTime, dto.endTime);
        String validateDate = validateDate(dto.examDate);

        if (!validateTime.equals("OK")) {
            throw new ValidationException(validateTime);
        } else if (!validateDate.equals("OK")) {
            throw new ValidationException(validateDate);
        }

        Operation operation = new Operation();

        LocalDate date = LocalDate.parse(dto.examDate);
        LocalTime startTime = LocalTime.parse(dto.startTime);
        LocalTime endTime = LocalTime.parse(dto.endTime);

        operation.setStartDateTime(LocalDateTime.of(date, startTime));
        operation.setEndDateTime(LocalDateTime.of(date, endTime));
        for (Long doctorId : dto.doctors) {
            Doctor doctor = (Doctor) this.userRepository.findById(doctorId).orElseGet(null);
            operation.setDoctors(new ArrayList<>());
            operation.getDoctors().add(doctor);
            if (operation.getClinic() == null)
                operation.setClinic(doctor.getClinic());
        }

        //operation.setRoom(this.roomRepository.findById(dto.room).orElseGet(null));
        operation.setRoom(this.roomRepository.findChosenRoom(dto.room));
        operation.setType(operationTypeRepository.findById(dto.type).get());
        operation.setStatus(Status.WAITING_FOR_PATIENT);
        Patient patient = (Patient) this.userRepository.findById(dto.patient).orElseGet(null);
        operation.setPatient(patient);

        OperationRequest request = this.operationRequestRepository.findById(dto.id).orElseGet(null);
        if (request != null) {
            request.setStatus(ExaminationRequestStatus.CONFIRMED);
            this.operationRequestRepository.save(request);
        }

        this.emailService.sendToPatientForOperation(patient.getEmail(), dto.examDate, dto.startTime, dto.endTime);

        return this.operationMapper.toDto(this.operationRepository.save(operation));
    }

    private String validateTime(String startTime, String endTime) {
        try {
            LocalTime.parse(startTime);
            LocalTime.parse(endTime);
        } catch (Exception e) {
            return "Time input is not correct.";
        }
        return "OK";
    }

    private String validateDate(String examDate) {
        try {
            LocalDate.parse(examDate);
        } catch (Exception e) {
            return "Date input is not correct.";
        }
        return "OK";
    }

    @Override
    public OperationDTO update(OperationDTO dto) throws Exception {
        return null;
    }

    public OperationDTO updateEntity(Operation o) {
        return operationMapper.toDto(operationRepository.save(o));
    }

    @Override
    public OperationDTO delete(Long id) {
        return null;
    }

    public List<Operation> getAllScheduledAndInProgressOperationsOfOperationType(Long typeId) {
        return this.operationRepository.findScheduledAndInProgressOperationsOfType(typeId);
    }

    public List<OperationCalendarDTO> getOperationsOfLoggedIn() throws Exception {
        User user = this.userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user instanceof Doctor) {
            List<Operation> examinations = operationRepository.findScheduledAndInProgressOperationsOfDoctor(user.getId());
            List<OperationCalendarDTO> retVal = new ArrayList<OperationCalendarDTO>();
            for (Operation e : examinations) {
                retVal.add(operationMapper.toCalendarDto(e));
            }
            return retVal;
        } else {
            throw new BadRequestException();
        }
    }

    public ArrayList<OperationForListDTO> getAllScheduledOperations() {

        ArrayList<OperationForListDTO> operations = new ArrayList<OperationForListDTO>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long userId = (long) 0;
        if (user instanceof Patient) {
            userId = user.getId();
        }
        List<Operation> listOfScheduled = this.operationRepository.findAll();
        for (Operation o : listOfScheduled) {
            if (o.getStatus() == Status.SCHEDULED && userId == o.getPatient().getId()) {
                OperationForListDTO dto = this.operationForListMapper.toDto(o);
                operations.add(dto);
            }
        }

        return operations;
    }

    public ArrayList<OperationForListDTO> getAllFinishedOperations() {

        ArrayList<OperationForListDTO> examinations = new ArrayList<OperationForListDTO>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long userId = (long) 0;
        if (user instanceof Patient) {
            userId = user.getId();
        }

        for (Operation e : this.operationRepository.findAllFinishedOperationsOfPatient(userId)) {
            examinations.add(this.operationForListMapper.toDto(e));
        }

        return examinations;
    }

    public OperationCalendarDetailedDTO findOneForCalendar(Long id) {
        return operationMapper.toCalendarDetailedDto(operationRepository.findById(id).get());
    }

    public List<BusinessBarDTO> getDailyNumbers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }
        List<Operation> finishedClinicOperations = this.operationRepository.findAllFinishedOperationsOfClinic(clinicId);
        int[] intArray = new int[]{0, 0, 0, 0, 0, 0, 0};
        for (Operation o : finishedClinicOperations) {
            if (o.getStartDateTime().getDayOfWeek() == DayOfWeek.MONDAY) {
                intArray[0]++;
            } else if (o.getStartDateTime().getDayOfWeek() == DayOfWeek.TUESDAY) {
                intArray[1]++;
            } else if (o.getStartDateTime().getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                intArray[2]++;
            } else if (o.getStartDateTime().getDayOfWeek() == DayOfWeek.THURSDAY) {
                intArray[3]++;
            } else if (o.getStartDateTime().getDayOfWeek() == DayOfWeek.FRIDAY) {
                intArray[4]++;
            } else if (o.getStartDateTime().getDayOfWeek() == DayOfWeek.SATURDAY) {
                intArray[5]++;
            } else {
                intArray[6]++;
            }
        }

        List<BusinessBarDTO> operationBars = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            operationBars.add(new BusinessBarDTO("Operation", intArray[i]));
        }

        return operationBars;
    }

    public List<BusinessBarDTO> getWeeklyNumbers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }
        List<Operation> finishedClinicOperations = this.operationRepository.findAllFinishedOperationsOfClinic(clinicId);
        int[] intArray = new int[]{0, 0, 0, 0, 0};
        for (Operation o : finishedClinicOperations) {
            if (o.getStartDateTime().getDayOfMonth() >= 1 && o.getStartDateTime().getDayOfMonth() <= 7) {
                intArray[0]++;
            } else if (o.getStartDateTime().getDayOfMonth() >= 8 && o.getStartDateTime().getDayOfMonth() <= 14) {
                intArray[1]++;
            } else if (o.getStartDateTime().getDayOfMonth() >= 15 && o.getStartDateTime().getDayOfMonth() <= 21) {
                intArray[2]++;
            } else if (o.getStartDateTime().getDayOfMonth() >= 22 && o.getStartDateTime().getDayOfMonth() <= 28) {
                intArray[3]++;
            } else if (o.getStartDateTime().getDayOfMonth() >= 29 && o.getStartDateTime().getDayOfMonth() <= 31) {
                intArray[4]++;
            }
        }

        List<BusinessBarDTO> operationBars = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            operationBars.add(new BusinessBarDTO("Operation", intArray[i]));
        }

        return operationBars;
    }

    public List<BusinessBarDTO> getMonthlyNumbers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }
        List<Operation> finishedClinicOperations = this.operationRepository.findAllFinishedOperationsOfClinic(clinicId);
        int[] intArray = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (Operation o : finishedClinicOperations) {
            if (o.getStartDateTime().getMonth() == Month.JANUARY) {
                intArray[0]++;
            } else if (o.getStartDateTime().getMonth() == Month.FEBRUARY) {
                intArray[1]++;
            } else if (o.getStartDateTime().getMonth() == Month.MARCH) {
                intArray[2]++;
            } else if (o.getStartDateTime().getMonth() == Month.APRIL) {
                intArray[3]++;
            } else if (o.getStartDateTime().getMonth() == Month.MAY) {
                intArray[4]++;
            } else if (o.getStartDateTime().getMonth() == Month.JUNE) {
                intArray[5]++;
            } else if (o.getStartDateTime().getMonth() == Month.JULY) {
                intArray[6]++;
            } else if (o.getStartDateTime().getMonth() == Month.AUGUST) {
                intArray[7]++;
            } else if (o.getStartDateTime().getMonth() == Month.SEPTEMBER) {
                intArray[8]++;
            } else if (o.getStartDateTime().getMonth() == Month.OCTOBER) {
                intArray[9]++;
            } else if (o.getStartDateTime().getMonth() == Month.NOVEMBER) {
                intArray[10]++;
            } else if (o.getStartDateTime().getMonth() == Month.DECEMBER) {
                intArray[11]++;
            }
        }

        List<BusinessBarDTO> operationBars = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            operationBars.add(new BusinessBarDTO("Operation", intArray[i]));
        }

        return operationBars;
    }

    public double getIncomeFromOperations(LocalDate startDate, LocalDate endDate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }
        List<Operation> finishedClinicOperations = this.operationRepository.findAllFinishedOperationsOfClinic(clinicId);
        double sum = 0;
        if (startDate != null && endDate == null) {
            for (Operation o : finishedClinicOperations) {
                if (o.getStartDateTime().toLocalDate().isAfter(startDate)) {
                    sum += o.getType().getPrice();
                }
            }
        }
        if (startDate == null && endDate != null) {
            for (Operation o : finishedClinicOperations) {
                if (o.getStartDateTime().toLocalDate().isBefore(endDate)) {
                    sum += o.getType().getPrice();
                }
            }
        }
        if (startDate != null && endDate != null) {
            for (Operation o : finishedClinicOperations) {
                if (o.getStartDateTime().toLocalDate().isAfter(startDate) && o.getEndDateTime().toLocalDate().isBefore(endDate)) {
                    sum += o.getType().getPrice();
                }
            }
        }

        return sum;
    }

    public List<AppointmentDTO> getAllUpcomingOperations() {
        List<AppointmentDTO> upcomingOperations = new ArrayList<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        List<Operation> operationList = this.operationRepository.getAllScheduledOperationsOfDoctor(user.getId());
        for (Operation o : operationList) {
            long minutes = o.getStartDateTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);
            if (minutes > 15) {
                o.setStatus(Status.CANCELED);
                this.operationRepository.save(o);
                continue;
            }
            upcomingOperations.add(this.operationMapper.toAppointmentDTO(o, user));
        }

        return upcomingOperations;
    }

    public AppointmentDTO doctorCanceling(AppointmentDTO dto) {
        Operation o = this.operationRepository.findById(dto.id).get();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime examinationDateTime = o.getStartDateTime();

        long hours = now.until(examinationDateTime, ChronoUnit.HOURS);

        if (hours < 24) {
            return null;
        }

        o.setStatus(Status.CANCELED);

        Operation operation = this.operationRepository.save(o);
        return this.operationMapper.toAppointmentDTO(operation, user);
    }

    public RatingDoctorAndClinicODTO getInfoForRating(Long id) {

        Operation o = this.findOneEntity(id);

        Patient p = o.getPatient();
        Clinic c = o.getClinic();
        int clinicRating = 0;

        String doctorNames = "";
        List<Long> ids = new ArrayList<>();
        List<Doctor> doctors = o.getDoctors();
        for (int i = 0; i < doctors.size(); i++) {
            ids.add(doctors.get(i).getId());
            if (i < doctors.size() - 1) {
                doctorNames += doctors.get(i).getName() + " " + doctors.get(i).getSurname() + ", ";
            } else {
                doctorNames += doctors.get(i).getName() + " " + doctors.get(i).getSurname();
            }
        }

        if (this.clinicRatingRepository.findByPatientAndClinic(c.getId(), p.getId()) != null) {
            ClinicRating cr = this.clinicRatingRepository.findByPatientAndClinic(c.getId(), p.getId());
            clinicRating = cr.getRating();
        }

        return new RatingDoctorAndClinicODTO(id, ids, doctorNames, (int) o.getAvgRating(), c.getId(), c.getName(), c.getCity(),
                c.getAddress(), clinicRating, p.getId());
    }

    public AppointmentDTO startOperation(AppointmentDTO dto) throws Exception {
        return startOperationFromId(dto.id);
    }

    public AppointmentDTO startOperationFromId(Long id) throws Exception {
        Operation o = this.operationRepository.findById(id).get();

        if (!this.canOperationStart(o.getId())) {
            throw new CannotStartException("You can only start operation in the period of 15 minutes before or after the operation start time.");
        }
        User user = this.userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!examinationRepository.findInProgressExaminationsOfDoctor(user.getId()).isEmpty()) {
            throw new AlreadyInProgressException("Cannot start because you already have an examination in progress!");
        }
        if (!operationRepository.findInProgressOperationsOfDoctor(user.getId()).isEmpty()) {
            throw new AlreadyInProgressException("Cannot start because you already have an operation in progress!");
        }
        o.setStatus(Status.IN_PROGRESS);

        Operation operation = this.operationRepository.save(o);
        return this.operationMapper.toAppointmentCancelDTO(operation);
    }

    public boolean canOperationStart(Long operationId) {
        Operation o = this.operationRepository.findById(operationId).get();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime operationDateTime = o.getStartDateTime();

        long minutes = 0;

        if (now.isBefore(operationDateTime)) {
            minutes = now.until(operationDateTime, ChronoUnit.MINUTES);
        } else {
            minutes = operationDateTime.until(now, ChronoUnit.MINUTES);
        }

        return minutes <= 15 && o.getStatus() == Status.SCHEDULED;

    }

    public List<AppointmentDTO> getWaitingForPatientOperations() {
        List<AppointmentDTO> waitingForPatient = new ArrayList<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Patient patient = (Patient) this.userRepository.findByEmail(username);
        List<Operation> list = this.operationRepository.findAllWaitingForPatientOfPatient(patient.getId());

        for (Operation operation : list) {

            List<Long> operationDoctors = this.operationRepository.findAllDoctorsOfOperation(operation.getId());
            User user = this.userRepository.findById(operationDoctors.get(0)).orElseGet(null);
            waitingForPatient.add(this.operationMapper.toAppointmentDTO(operation, user));
        }

        return waitingForPatient;
    }

    public AppointmentDTO confirmOperation(Long id) {
        Operation operation = this.operationRepository.findById(id).orElseGet(null);
        if (operation != null && operation.getStatus().equals(Status.WAITING_FOR_PATIENT)) {
            operation.setStatus(Status.SCHEDULED);
            List<Long> operationDoctors = this.operationRepository.findAllDoctorsOfOperation(operation.getId());
            User user = this.userRepository.findById(operationDoctors.get(0)).orElseGet(null);
            emailService.sendSimpleMessage(user.getEmail(),
                    "Operation scheduled",
                    "Operation on " + operation.getStartDateTime().toLocalDate().toString() + " at " +
                            operation.getStartDateTime().toLocalTime().toString() + " has been scheduled");

            this.operationRepository.save(operation);
            return operationMapper.toAppointmentDTO(operation, user);
        }
        return null;
    }

    public AppointmentDTO denyOperation(Long id) {
        Operation operation = this.operationRepository.findById(id).orElseGet(null);
        if (operation != null && operation.getStatus().equals(Status.WAITING_FOR_PATIENT)) {
            operation.setStatus(Status.CANCELED);
            List<Long> operationDoctors = this.operationRepository.findAllDoctorsOfOperation(operation.getId());
            User user = this.userRepository.findById(operationDoctors.get(0)).orElseGet(null);

            this.operationRepository.save(operation);
            return operationMapper.toAppointmentDTO(operation, user);
        }
        return null;
    }

    public boolean isRoomAvailableForGivenTime(Room room, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Operation> operationsOfRoom = this.operationRepository.findAllActiveOperationsOfRoom(room.getId());

        Iterator i = operationsOfRoom.iterator();
        while (i.hasNext()) {
            Operation e = (Operation) i.next();
            if (e.getStartDateTime().toLocalDate().compareTo(date) != 0) {
                i.remove();
                continue;
            }
            if (!this.isThereConflictBetweenTheseTwoTimes(startTime, endTime, e.getStartDateTime().toLocalTime(), e.getEndDateTime().toLocalTime())) {
                i.remove();
                continue;
            }
        }

        return operationsOfRoom.isEmpty();
    }

    public boolean isThereConflictBetweenTheseTwoTimes(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
        return (isAfterOrEqual(startTime1, startTime2) && isBeforeOrEqual(startTime1, endTime2)
                || isAfterOrEqual(endTime1, startTime2) && isBeforeOrEqual(endTime1, endTime2))
                || (startTime1.isBefore(startTime2) && endTime1.isAfter(endTime2));
    }

    public boolean isBeforeOrEqual(LocalTime time1, LocalTime time2) {
        return (time1.isBefore(time2)/* || time1.compareTo(time2) == 0*/);
    }

    public boolean isAfterOrEqual(LocalTime time1, LocalTime time2) {
        return (time1.isAfter(time2) /*|| time1.compareTo(time2) == 0*/);
    }

    public List<Operation> getSortedOperationsOfRoom(Long roomId, LocalDate date, LocalTime endTime) {
        LocalDateTime dateTime = LocalDateTime.of(date, endTime);
        List<Operation> examinationsAfter = new ArrayList<>();
        for (Operation e : this.operationRepository.findAllActiveOperationsOfRoomSorted(roomId)) {
            if (!dateTime.isAfter(e.getEndDateTime())) {
                examinationsAfter.add(e);
            }
        }

        return examinationsAfter;
    }

    public List<OperationDTO> getOperationsInProgressOfLoggedIn() {
        User user = this.userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Operation> operations = operationRepository.findInProgressOperationsOfDoctor(user.getId());
        ArrayList<OperationDTO> retVal = new ArrayList<>();
        for (Operation e : operations) {
            retVal.add(operationMapper.toDto(e));
        }
        return retVal;
    }
}
