package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.PatientDTO;
import com.example.clinicalCenter.dto.RegisterRequestDTO;
import com.example.clinicalCenter.dto.UserDTO;
import com.example.clinicalCenter.exception.BadRequestException;
import com.example.clinicalCenter.exception.GenericConflictException;
import com.example.clinicalCenter.mapper.RegisterRequestMapper;
import com.example.clinicalCenter.model.RegisterRequest;
import com.example.clinicalCenter.model.User;
import com.example.clinicalCenter.model.enums.RegisterRequestStatus;
import com.example.clinicalCenter.repository.RegisterRequestRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = false)
public class RegisterRequestService implements ServiceInterface<RegisterRequestDTO> {

    @Autowired
    private RegisterRequestRepository repository;

    private RegisterRequestMapper mapper = new RegisterRequestMapper();

    @Autowired
    private EmailService emailService;

    private RegisterRequestMapper registerRequestMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PatientService patientService;


    @Autowired
    private AuthenticationService authenticationService;

    public List<RegisterRequest> findAllEntity() {
        return this.repository.findAll();
    }

    @Override
    public RegisterRequestDTO findOne(Long id) {
        RegisterRequest found = this.repository.findById(id).get();
        return this.mapper.toDto(found);
    }

    public RegisterRequestService() {
        this.registerRequestMapper = new RegisterRequestMapper();
    }

    public RegisterRequest save(RegisterRequest request) {
        return repository.save(request);
    }

    @Override
    public List<RegisterRequestDTO> findAll() {

        ArrayList<RegisterRequest> requests = (ArrayList<RegisterRequest>) this.findAllEntity();
        ArrayList<RegisterRequestDTO> retVal = new ArrayList<>();
        for (RegisterRequest request : requests) {
            retVal.add(registerRequestMapper.toDto(request));
        }
        return retVal;

    }

    public List<RegisterRequestDTO> findStatus(RegisterRequestStatus status) {
        ArrayList<RegisterRequestDTO> retVal = new ArrayList<RegisterRequestDTO>();
        for (RegisterRequest r : this.findAllEntity()) {
            if (r.getStatus().equals(status))
                retVal.add(registerRequestMapper.toDto(r));
        }
        return retVal;
    }

    public RegisterRequest findOneEntity(Long id) {
        return this.repository.findById(id).orElseGet(null);
    }

    @Override
    public RegisterRequestDTO create(RegisterRequestDTO dto) {
        if (!validInput(dto)) {
            return null;
        }
        RegisterRequest entity = this.mapper.toEntity(dto);
        RegisterRequest createdEntity = this.repository.save(entity);
        this.emailService.sendVerificationMail(createdEntity.getEmail());
        return this.mapper.toDto(createdEntity);
    }

    @Override
    public RegisterRequestDTO update(RegisterRequestDTO dto) {
        RegisterRequest registerRequest = this.mapper.toEntity(dto);
        RegisterRequest saved = this.repository.save(registerRequest);
        return this.mapper.toDto(saved);
    }

    @Override
    public RegisterRequestDTO delete(Long id) {
        return null;
    }

    public RegisterRequestDTO changeStatus(Long id) {
        try {
            RegisterRequest request = this.findOneEntity(id);
            if (request.getStatus() != RegisterRequestStatus.WAITING_FOR_USER) {
                return null;
            }
            request.setStatus(RegisterRequestStatus.WAITING_FOR_ADMIN);
            RegisterRequestDTO dto = this.mapper.toDto(this.repository.save(request));

            List<User> centerAdmins = this.userService.getAll("CENTER_ADMIN");
            for (User u : centerAdmins) {
                this.emailService.sendGenericAdminMail(u.getEmail());
            }

            return dto;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public boolean validInput(RegisterRequestDTO dto) {
        UserDTO userDTO = new UserDTO(dto.id, dto.email, dto.password, dto.verifiedPassword, dto.name, dto.surname,
                dto.address, dto.city, dto.country, dto.phone, dto.socialSecurityNumber);
        userDTO.type = "PATIENT";
        String result = authenticationService.validateInput(userDTO);
        return result.equals("OK");
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public RegisterRequestDTO confirmRequest(Long id) throws Exception {
        //RegisterRequest request = this.findOneEntity(id);
        RegisterRequest request = repository.findOneLocked(id);
        if (request == null)
            throw new BadRequestException("An error occured while denying request!");
        if (request.getStatus().equals(RegisterRequestStatus.WAITING_FOR_ADMIN)) {
            request.setStatus(RegisterRequestStatus.CONFIRMED);
            this.patientService.create(new PatientDTO(request));
            emailService.sendSimpleMessage(request.getEmail(),
                    "Your registration request has been approved",
                    "Your registration request for the clinical center has been approved by an administrator.");

            this.save(request);
            return registerRequestMapper.toDto(request);
        } else {
            if (request.getStatus().equals(RegisterRequestStatus.DENIED)) {
                throw new GenericConflictException("Register request has already been denied.");
            }
            if (request.getStatus().equals(RegisterRequestStatus.CONFIRMED)) {
                throw new GenericConflictException("Register request has already been confirmed.");
            }
        }
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public RegisterRequestDTO denyRequest(RegisterRequestDTO dto) throws Exception {
        //RegisterRequest request = this.findOneEntity(dto.id);
        RegisterRequest request = repository.findOneLocked(dto.id);
        if (request == null)
            throw new BadRequestException("An error occured while denying request!");
        if (request.getStatus().equals(RegisterRequestStatus.WAITING_FOR_ADMIN)) {
            request.setStatus(RegisterRequestStatus.DENIED);
            request.setReason(dto.reason);
            String message = "Your registration request for the clinical center has been denied by an administrator.\n";
            if (!dto.reason.isEmpty())
                message += "Reason: " + request.getReason();
            else
                message += "The administrator did not enter a reason.";
            emailService.sendSimpleMessage(request.getEmail(),
                    "Your registration request has been denied", message);
            this.save(request);
            return registerRequestMapper.toDto(request);
        } else {
            if (request.getStatus().equals(RegisterRequestStatus.DENIED)) {
                throw new GenericConflictException("Register request has already been denied.");
            }
            if (request.getStatus().equals(RegisterRequestStatus.CONFIRMED)) {
                throw new GenericConflictException("Register request has already been confirmed.");
            }
        }
        return null;
    }
}
