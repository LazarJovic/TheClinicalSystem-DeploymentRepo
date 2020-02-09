package com.example.clinicalCenter.service;


import com.example.clinicalCenter.converter.ObjectsForRequest;
import com.example.clinicalCenter.dto.AppointmentForListDTO;
import com.example.clinicalCenter.dto.ExaminationDTO;
import com.example.clinicalCenter.dto.OperationDTO;
import com.example.clinicalCenter.dto.RoomTimeDTO;
import com.example.clinicalCenter.mapper.ExaminationRequestMapper;
import com.example.clinicalCenter.mapper.OperationRequestMapper;
import com.example.clinicalCenter.model.*;
import com.example.clinicalCenter.model.enums.ExaminationRequestStatus;
import com.example.clinicalCenter.repository.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PeriodicCheckService {

    @Autowired
    private ExaminationRequestRepository examinationRequestRepository;

    @Autowired
    private OperationRequestRepository operationRequestRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private OperationService operationService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private NurseService nurseService;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private OperationRepository operationRepository;

    private ExaminationRequestMapper examinationRequestMapper = new ExaminationRequestMapper();

    private OperationRequestMapper operationRequestMapper = new OperationRequestMapper();

    @Scheduled(cron = "${room.cron}")
    @Transactional
    public void checkExaminationRequests() {
        List<ExaminationRequest> waitingRequests = this.examinationRequestRepository.findAllWaitingForAdmin();
        Random random = new Random();
        if (!waitingRequests.isEmpty()) {
            waitingRequests.forEach((request) ->
                    {
                        AppointmentForListDTO requestDTO = this.examinationRequestMapper.toForListDTO(request);
                        List<RoomTimeDTO> freeRoomsForChosenTime = this.roomService.searchRoomsForRequestAtGivenTime
                                (requestDTO, true, true);
                        if (freeRoomsForChosenTime.isEmpty()) {
                            List<RoomTimeDTO> freeRoomsExtended = this.roomService.searchRoomsForRequestExtended(requestDTO, true, true);
                            List<RoomTimeDTO> roomsWithChosenDoctor = freeRoomsExtended.stream()
                                    .filter(roomTimeDTO -> roomTimeDTO.isDoctorAvailable)
                                    .collect(Collectors.toList());
                            if (!roomsWithChosenDoctor.isEmpty()) {
                                int roomIndex = random.nextInt(roomsWithChosenDoctor.size());
                                RoomTimeDTO roomTimeDTO = roomsWithChosenDoctor.get(roomIndex);
                                Room chosenRoom = this.roomRepository.findById(roomTimeDTO.id).orElseGet(null);
                                ExaminationDTO examinationDTO = new ExaminationDTO((long) 0, (long) 0, roomTimeDTO.date, roomTimeDTO.startTime, roomTimeDTO.endTime,
                                        "", requestDTO.doctorId, requestDTO.id, chosenRoom.getId(), requestDTO.patientId);

                                try {
                                    this.examinationService.create(examinationDTO);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                ObjectsForRequest requestData = this.doctorService.checkForDoctorInRequestList(freeRoomsExtended, request.getType(), true);
                                RoomTimeDTO roomTimeDTO = requestData.getRoomTimeDTO();
                                Doctor doctor = requestData.getDoctor();
                                ExaminationDTO examinationDTO = new ExaminationDTO((long) 0, (long) 0, roomTimeDTO.date, roomTimeDTO.startTime, roomTimeDTO.endTime,
                                        "", doctor.getId(), requestDTO.id, roomTimeDTO.id, requestDTO.patientId);

                                try {
                                    this.examinationService.create(examinationDTO);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            int roomIndex = random.nextInt(freeRoomsForChosenTime.size());
                            Room chosenRoom = this.roomRepository.findById(freeRoomsForChosenTime.get(roomIndex).id).orElseGet(null);
//                            ExaminationDTO examinationDTO = new ExaminationDTO((long)0, (long)0, requestDTO.date, requestDTO.startTime, requestDTO.endTime,
//                                    "", requestDTO.doctorId, requestDTO.id, chosenRoom.getId(), requestDTO.patientId);
//
//                            try {
//                                this.examinationService.create(examinationDTO);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                            Examination examination = new Examination();
                            examination.setStartDateTime(LocalDateTime.of(request.getExaminationDate(), request.getStartTime()));
                            examination.setEndDateTime(LocalDateTime.of(request.getExaminationDate(), request.getEndTime()));
                            examination.setType(request.getType());
                            examination.setRoom(chosenRoom);
                            examination.setDoctor(request.getDoctor());
                            examination.setNurse(this.nurseService.getRandomFreeNurse(request.getExaminationDate(), request.getStartTime(), request.getEndTime(), request.getClinic()));
                            examination.setPatient(request.getPatient());
                            examination.setStatus(Status.WAITING_FOR_PATIENT);
                            examination.setDiscount(0);
                            examination.setPredefined(false);
                            Clinic clinic = request.getClinic();
                            examination.setClinic((Clinic) Hibernate.unproxy(clinic));


                            this.examinationRepository.save(examination);


                            request.setStatus(ExaminationRequestStatus.CONFIRMED);
                            this.examinationRequestRepository.save(request);
                        }
                    }
            );

        }
    }

    @Scheduled(cron = "${room.cron}")
    @Transactional
    public void checkOperationRequests() {
        List<OperationRequest> waitingRequests = this.operationRequestRepository.findAllWaitingForAdmin();
        Random random = new Random();
        if (!waitingRequests.isEmpty()) {
            waitingRequests.forEach((request) ->
                    {
                        AppointmentForListDTO requestDTO = this.operationRequestMapper.toForListDTO(request);
                        List<RoomTimeDTO> freeRoomsForChosenTime = this.roomService.searchRoomsForRequestAtGivenTime
                                (requestDTO, false, true);
                        if (freeRoomsForChosenTime.isEmpty()) {
                            List<RoomTimeDTO> freeRoomsExtended = this.roomService.searchRoomsForRequestExtended(requestDTO, false, true);
                            List<RoomTimeDTO> roomsWithChosenDoctor = freeRoomsExtended.stream()
                                    .filter(roomTimeDTO -> roomTimeDTO.isDoctorAvailable)
                                    .collect(Collectors.toList());
                            if (!roomsWithChosenDoctor.isEmpty()) {
                                int roomIndex = random.nextInt(roomsWithChosenDoctor.size());
                                RoomTimeDTO roomTimeDTO = roomsWithChosenDoctor.get(roomIndex);
                                ArrayList<Long> doctorIds = new ArrayList<>();
                                doctorIds.add(requestDTO.doctorId);
                                Room chosenRoom = this.roomRepository.findById(roomTimeDTO.id).orElseGet(null);
                                OperationDTO operationDTO = new OperationDTO(requestDTO.id, request.getType().getId(), roomTimeDTO.date, roomTimeDTO.startTime, roomTimeDTO.endTime,
                                        doctorIds, chosenRoom.getId(), requestDTO.patientId);
//Long id, Long type, String examDate, String startTime, String endTime, ArrayList<Long> doctors, Long room, Long patient
                                try {
                                    this.operationService.create(operationDTO);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                ObjectsForRequest requestData = this.doctorService.checkForDoctorInRequestList(freeRoomsExtended, null, false);
                                RoomTimeDTO roomTimeDTO = requestData.getRoomTimeDTO();
                                Doctor doctor = requestData.getDoctor();
                                ArrayList<Long> doctorIds = new ArrayList<>();
                                doctorIds.add(doctor.getId());
                                OperationDTO operationDTO = new OperationDTO(requestDTO.id, request.getType().getId(), roomTimeDTO.date, roomTimeDTO.startTime, roomTimeDTO.endTime,
                                        doctorIds, roomTimeDTO.id, requestDTO.patientId);

                                try {
                                    this.operationService.create(operationDTO);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            int roomIndex = random.nextInt(freeRoomsForChosenTime.size());
                            Room chosenRoom = this.roomRepository.findById(freeRoomsForChosenTime.get(roomIndex).id).orElseGet(null);

                            Operation operation = new Operation();
                            operation.setStartDateTime(LocalDateTime.of(request.getOperationDate(), request.getStartTime()));
                            operation.setEndDateTime(LocalDateTime.of(request.getOperationDate(), request.getEndTime()));
                            operation.setType(request.getType());
                            operation.setRoom(chosenRoom);
                            operation.setDoctors(new ArrayList<>());
                            operation.getDoctors().add(request.getDoctor());
                            operation.setPatient(request.getPatient());
                            operation.setStatus(Status.WAITING_FOR_PATIENT);
                            Clinic clinic = request.getClinic();
                            operation.setClinic((Clinic) Hibernate.unproxy(clinic));


                            this.operationRepository.save(operation);


                            request.setStatus(ExaminationRequestStatus.CONFIRMED);
                            this.operationRequestRepository.save(request);
                        }
                    }
            );

        }
    }

}
