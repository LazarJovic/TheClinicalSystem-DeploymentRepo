package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.exception.GenericConflictException;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.RoomMapper;
import com.example.clinicalCenter.model.*;
import com.example.clinicalCenter.repository.DoctorRepository;
import com.example.clinicalCenter.repository.RoomRepository;
import com.example.clinicalCenter.repository.UserRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoomService implements ServiceInterface<RoomDTO> {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private OperationService operationService;

    @Autowired
    private DoctorService doctorService;

    private RoomMapper roomMapper;

    public RoomService() {
        this.roomMapper = new RoomMapper();
    }

    @Override
    public List<RoomDTO> findAll() {
        return null;
    }

    @Override
    public RoomDTO findOne(Long id) {
        Room entity = this.roomRepository.findById(id).get();
        return this.roomMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = false)
    public RoomDTO create(RoomDTO dto) throws Exception {
        String validation = dtoValid(dto);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        Room entity = roomMapper.toEntity(dto);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            entity.setClinic(clinicAdmin.getClinic());
            clinicId = clinicAdmin.getClinic().getId();
        }

        if (this.nameTaken(entity.getName(), clinicId)) {
            throw new GenericConflictException("Room name is already taken.");
        }
        Room createdEntity = this.roomRepository.save(entity);
        return roomMapper.toDto(createdEntity);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public RoomDTO update(RoomDTO dto) throws Exception {
        String validation = dtoValid(dto);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        if (!this.examinationService.getAllScheduledAndInProgressExaminationsOfRoom(dto.id).isEmpty()) {
            throw new GenericConflictException("Selected room has some scheduled or in progress examinations.");
        }
        Room updatedRoom = this.roomRepository.findById(dto.id).orElseGet(null);
        updatedRoom.setName(dto.name);
        if (dto.type.equals("ordination")) {
            updatedRoom.setRoomType(RoomType.ORDINATION);
        } else {
            updatedRoom.setRoomType(RoomType.OPERATION_ROOM);
        }

        Room room = this.roomRepository.save(updatedRoom);
        return this.roomMapper.toDto(room);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public RoomDTO delete(Long id) throws Exception {
        Room room = this.roomRepository.findById(id).orElseGet(null);
        if (!this.examinationService.getAllScheduledAndInProgressExaminationsOfRoom(id).isEmpty()) {
            throw new GenericConflictException("Selected room has some scheduled or in progress examinations.");
        }
        if (!room.isDeleted()) {
            room.delete();
        } else {
            throw new GenericConflictException("Room already deleted!");
        }
        return this.roomMapper.toDto(this.roomRepository.save(room));
    }

    public ArrayList<RoomDTO> getFreeRooms(ExaminationParamsDTO examinationParams) {
        ArrayList<RoomDTO> freeRooms = new ArrayList<>();
        LocalDate examDate = LocalDate.parse(examinationParams.examDate);
        LocalTime startTime = LocalTime.parse(examinationParams.startTime);
        LocalTime endTime = LocalTime.parse(examinationParams.endTime);
        List<Room> list = this.roomRepository.findAllByType(0);
        HashSet<Room> examinationRooms = this.examinationService.getRoomsOnExaminations(
                examinationParams.type, examDate, startTime, endTime);

        list.removeAll(examinationRooms);

        for (Room r : list) {
            freeRooms.add(this.roomMapper.toDto(r));
        }

        return freeRooms;
    }

    public ArrayList<RoomDTO> getRoomsOfClinic() {
        ArrayList<RoomDTO> doctorsOfClinic = new ArrayList<RoomDTO>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }
        for (Room r : this.roomRepository.findRoomsOfClinic(clinicId)) {
            Room room = r;
            doctorsOfClinic.add(this.roomMapper.toDto(room));
        }

        return doctorsOfClinic;
    }

    private Long getClinicOfLoggedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }

        return clinicId;
    }

    private List<RoomDTO> toRoomDTOList(List<Room> entityList) {
        List<RoomDTO> retVal = new ArrayList<>();
        for (Room type : entityList) {
            retVal.add(this.roomMapper.toDto(type));
        }

        return retVal;
    }

    private List<Room> toRoomEntityList(List<RoomDTO> dtoList) {
        List<Room> retVal = new ArrayList<>();
        for (RoomDTO dto : dtoList) {
            retVal.add(this.roomMapper.toEntity(dto));
        }

        return retVal;
    }

    public List<RoomDTO> searchRooms(SearchRoomDTO searchRoom) throws Exception {
        String validation = dtoSearchValid(searchRoom);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);

        List<Room> roomsList = this.roomRepository.findRoomsOfClinic(getClinicOfLoggedUser());

        Iterator i = roomsList.iterator();
        while (i.hasNext()) {
            Room room = (Room) i.next();
            if (isThereName(searchRoom.searchName)) {
                if (!this.containsName(room, searchRoom.searchName)) {
                    i.remove();
                    continue;
                }
            }

            if (isThereType(searchRoom.searchType)) {
                if (searchRoom.searchType.equals("ordination")) {
                    if (room.getRoomType() != RoomType.ORDINATION)
                        i.remove();
                } else if (searchRoom.searchType.equals("operation_room")) {
                    if (room.getRoomType() != RoomType.OPERATION_ROOM)
                        i.remove();
                }
            }

        }

        return this.toRoomDTOList(roomsList);
    }


    public List<RoomTimeDTO> searchRoomsForRequestAtGivenTime(AppointmentForListDTO dto, boolean isExamination, boolean isAutomatic) {
        LocalDate date = LocalDate.parse(dto.date);
        LocalTime startTime = LocalTime.parse(dto.startTime);
        LocalTime endTime = LocalTime.parse(dto.endTime);
        List<Room> availableRooms = new ArrayList<>();
        Long clinicId = (long) 0;
        if (!isAutomatic) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = this.userRepository.findByEmail(username);
            if (user instanceof ClinicAdmin) {
                ClinicAdmin clinicAdmin = (ClinicAdmin) user;
                clinicId = clinicAdmin.getClinic().getId();
            }
        } else {
            clinicId = ((Doctor) Hibernate.unproxy(this.doctorRepository.findById(dto.doctorId).get())).getClinic().getId();
        }

        if (isExamination) {
            List<Room> rooms = this.roomRepository.findAllByTypeAndClinic(RoomType.ORDINATION.ordinal(), clinicId);

            for (Room room : rooms) {
                if (this.examinationService.isRoomAvailableForGivenTime(room, date, startTime, endTime)) {
                    availableRooms.add(room);
                }
            }
        } else { //is operation
            List<Room> rooms = this.roomRepository.findAllByTypeAndClinic(RoomType.OPERATION_ROOM.ordinal(), clinicId);

            for (Room room : rooms) {
                if (this.operationService.isRoomAvailableForGivenTime(room, date, startTime, endTime)) {
                    availableRooms.add(room);
                }
            }
        }
        return this.roomMapper.toRoomTimeDTOList(availableRooms, dto.date, dto.startTime, dto.endTime);
    }

    public List<RoomTimeDTO> searchRoomsForRequestExtended(AppointmentForListDTO dto, boolean isExamination, boolean isAutomatic) {
        LocalDate date = LocalDate.parse(dto.date);
        LocalTime startTime = LocalTime.parse(dto.startTime);
        LocalTime endTime = LocalTime.parse(dto.endTime);
        List<RoomTimeDTO> roomTimes = new ArrayList<>();

        Long clinicId = (long) 0;
        if (!isAutomatic) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = this.userRepository.findByEmail(username);
            if (user instanceof ClinicAdmin) {
                ClinicAdmin clinicAdmin = (ClinicAdmin) user;
                clinicId = clinicAdmin.getClinic().getId();
            }
        } else {
            clinicId = ((Doctor) Hibernate.unproxy(this.doctorRepository.findById(dto.doctorId).get())).getClinic().getId();
        }

        if (isExamination) {
            List<Room> rooms = this.roomRepository.findAllByTypeAndClinic(RoomType.ORDINATION.ordinal(), clinicId);

            long minutes = startTime.until(endTime, ChronoUnit.MINUTES);

            for (Room room : rooms) {
                List<Examination> roomExaminations = this.examinationService.getSortedExaminationsOfRoom(room.getId(), date, endTime);
                String newDate = "";
                String newStartTime = "";
                String newEndTime = "";
                boolean flag = false;
                for (int i = 0; i < roomExaminations.size() - 1; i++) {
                    if (flag)
                        break;
                    if (minutes < roomExaminations.get(i).getEndDateTime().until(roomExaminations.get(i + 1).getStartDateTime(), ChronoUnit.MINUTES)) {
                        newDate = roomExaminations.get(i).getEndDateTime().toLocalDate().toString();
                        newStartTime = roomExaminations.get(i).getEndDateTime().toLocalTime().toString();
                        newEndTime = roomExaminations.get(i).getEndDateTime().toLocalTime().plusMinutes(minutes).toString();
                        flag = true;
                    }
                }
                if (!flag) {
                    int i = roomExaminations.size() - 1;
                    newDate = roomExaminations.get(i).getEndDateTime().toLocalDate().toString();
                    newStartTime = roomExaminations.get(i).getEndDateTime().toLocalTime().toString();
                    newEndTime = roomExaminations.get(i).getEndDateTime().toLocalTime().plusMinutes(minutes).toString();
                }
                RoomTimeDTO roomTimeDTO = roomMapper.toRoomTimeDTO(room, newDate, newStartTime, newEndTime);
                Doctor doctor = doctorRepository.findById(dto.doctorId).get();
                Doctor realDoctor = (Doctor) Hibernate.unproxy(doctor);
                roomTimeDTO.isDoctorAvailable = doctorService.isDoctorFreeAtGivenTime(LocalDate.parse(newDate), LocalTime.parse(newStartTime),
                        LocalTime.parse(newEndTime), realDoctor).equals("OK");
                roomTimes.add(roomTimeDTO);
            }
        } else {
            List<Room> rooms = this.roomRepository.findAllByTypeAndClinic(RoomType.OPERATION_ROOM.ordinal(), clinicId);

            long minutes = startTime.until(endTime, ChronoUnit.MINUTES);

            for (Room room : rooms) {
                List<Operation> roomOperations = this.operationService.getSortedOperationsOfRoom(room.getId(), date, endTime);
                String newDate = "";
                String newStartTime = "";
                String newEndTime = "";
                boolean flag = false;
                for (int i = 0; i < roomOperations.size() - 1; i++) {
                    if (minutes < roomOperations.get(i).getEndDateTime().until(roomOperations.get(i + 1).getStartDateTime(), ChronoUnit.MINUTES)) {
                        newDate = roomOperations.get(i).getEndDateTime().toLocalDate().toString();
                        newStartTime = roomOperations.get(i).getEndDateTime().toLocalTime().toString();
                        newEndTime = roomOperations.get(i).getEndDateTime().toLocalTime().plusMinutes(minutes).toString();
                        flag = true;
                    }
                }
                if (!flag) {
                    int i = roomOperations.size() - 1;
                    newDate = roomOperations.get(i).getEndDateTime().toLocalDate().toString();
                    newStartTime = roomOperations.get(i).getEndDateTime().toLocalTime().toString();
                    newEndTime = roomOperations.get(i).getEndDateTime().toLocalTime().plusMinutes(minutes).toString();
                }
                RoomTimeDTO roomTimeDTO = roomMapper.toRoomTimeDTO(room, newDate, newStartTime, newEndTime);
                Doctor doctor = doctorRepository.findById(dto.doctorId).get();
                Doctor realDoctor = (Doctor) Hibernate.unproxy(doctor);
                roomTimeDTO.isDoctorAvailable = doctorService.isDoctorFreeAtGivenTime(LocalDate.parse(newDate), LocalTime.parse(newStartTime),
                        LocalTime.parse(newEndTime), realDoctor).equals("OK");
                roomTimes.add(roomTimeDTO);
            }
        }
        return roomTimes;
    }


    private boolean containsName(Room room, String searchName) {
        return room.getName().toLowerCase().contains(searchName.toLowerCase());
    }

    private String dtoSearchValid(SearchRoomDTO dto) {
        if (isThereName(dto.searchName) && dto.searchName.length() > 30) {
            return "Room's name cannot be longer than 30 characters.";
        }

        if (isThereType(dto.searchType) && !dto.searchType.equals("ordination") && !dto.searchType.equals("operation_room")) {
            return "Room's type must be ordination or operation room.";
        }

        return "OK";
    }

    private boolean isThereName(String searchName) {
        return searchName != null && !searchName.equals("");
    }

    private boolean isThereType(String searchType) {
        return searchType != null && !searchType.equals("");
    }

    private boolean nameTaken(String roomName, Long clinicId) {
        List<Room> clinicRooms = this.roomRepository.findRoomsOfClinic(clinicId);
        for (Room room : clinicRooms) {
            if (room.getName().equals(roomName))
                return true;
        }

        return false;
    }

    private String dtoValid(RoomDTO dto) {

        if (dto.name.isEmpty()) {
            return "Room name cannot be empty.";
        }


        if (dto.type.isEmpty()) {
            return "Room type cannot be empty.";
        }

        if (dto.name.length() > 30) {
            return "Room name cannot be longer than 30 characters.";
        }

        if (dto.type == null) {
            return "You must choose room type.";
        }

        if (!dto.type.equals("ordination") && !dto.type.equals("operation_room")) {
            return "Room type is not chosen correctly.";
        }

        return "OK";
    }
}
