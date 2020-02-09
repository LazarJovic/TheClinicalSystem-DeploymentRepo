package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.model.*;
import com.example.clinicalCenter.model.enums.UserType;
import com.example.clinicalCenter.security.TokenUtils;
import com.example.clinicalCenter.security.auth.JwtAuthenticationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalTime;

@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenUtils tokenUtils;

    public AuthenticationService() {
    }

    public UserDTO registerUser(UserDTO dto) throws Exception {
        if (!validateInput(dto).equals("OK")) {
            throw new ValidationException(validateInput(dto));
        }

        return this.userService.create(dto);

    }

    public UserTokenState createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
                                                    HttpServletResponse response) throws AuthenticationException, IOException {

        final Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return null;
        }
        User user = (User) authentication.getPrincipal();

        user.setNumLogins(user.getNumLogins() + 1);
        user = this.userService.update(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenUtils.generateToken(user.getUsername());
        int expiresIn = tokenUtils.getExpiredIn();
        UserType userType = null;
        boolean valid = false;

        if (user instanceof Doctor) {
            userType = UserType.ROLE_DOCTOR;
            valid = true;
        } else if (user instanceof ClinicalCenterAdmin) {
            userType = UserType.ROLE_CENTER_ADMIN;
        } else if (user instanceof ClinicAdmin) {
            userType = UserType.ROLE_CLINIC_ADMIN;
        } else if (user instanceof Nurse) {
            userType = UserType.ROLE_NURSE;
        } else if (user instanceof Patient) {
            userType = UserType.ROLE_PATIENT;
        }

        return new UserTokenState(jwt, expiresIn, user.getId(), userType.toString(), valid, user.getNumLogins());
    }


    public String validateInput(UserDTO dto) {
        String retVal = validateEmail(dto.email);
        if (!retVal.equals("OK")) return retVal;
        retVal = validateName(dto.name);
        if (!retVal.equals("OK")) return retVal;
        retVal = validateSurname(dto.surname);
        if (!retVal.equals("OK")) return retVal;
        retVal = validatePhone(dto.phone);
        if (!retVal.equals("OK")) return retVal;
        retVal = validatePassword(dto.password, dto.confirmPassword);
        if (!retVal.equals("OK")) return retVal;
        switch (dto.type) {
            case ("PATIENT"):
                retVal = validateAdress(dto.address, dto.city);
                if (!retVal.equals("OK")) return retVal;
                retVal = validateCountry(dto.country);
                if (!retVal.equals("OK")) return retVal;
                retVal = validateSocialSecurityNumber(dto.socialSecurityNumber);
                if (!retVal.equals("OK")) return retVal;
                break;
            case ("DOCTOR"):
                retVal = validateShift(dto.shiftStart, dto.shiftEnd);
                if (!retVal.equals("OK")) return retVal;
                retVal = validateSpecialty(dto.specialty);
                if (!retVal.equals("OK")) return retVal;
                break;
        }
        return "OK";
    }

    public String validateClinicAdmin(ClinicAdminDTO dto) {
        String retVal = validateEmail(dto.email);
        if (!retVal.equals("OK")) return retVal;
        retVal = validateName(dto.name);
        if (!retVal.equals("OK")) return retVal;
        retVal = validateSurname(dto.surname);
        if (!retVal.equals("OK")) return retVal;
        retVal = validatePhone(dto.phone);
        if (!retVal.equals("OK")) return retVal;
        retVal = validatePassword(dto.password, dto.confirmPassword);
        if (!retVal.equals("OK")) return retVal;

        return "OK";
    }

    public String validateClinicCenterAdmin(ClinicalCenterAdminDTO dto) {
        String retVal = validateEmail(dto.email);
        if (!retVal.equals("OK")) return retVal;
        retVal = validateName(dto.name);
        if (!retVal.equals("OK")) return retVal;
        retVal = validateSurname(dto.surname);
        if (!retVal.equals("OK")) return retVal;
        retVal = validatePhone(dto.phone);
        if (!retVal.equals("OK")) return retVal;
        retVal = validatePassword(dto.password, dto.confirmPassword);
        if (!retVal.equals("OK")) return retVal;

        return "OK";
    }

    public String validateNurse(NurseDTO dto) {
        String retVal = validateEmail(dto.email);
        if (!retVal.equals("OK")) return retVal;
        retVal = validateName(dto.name);
        if (!retVal.equals("OK")) return retVal;
        retVal = validateSurname(dto.surname);
        if (!retVal.equals("OK")) return retVal;
        retVal = validatePhone(dto.phone);
        if (!retVal.equals("OK")) return retVal;
        retVal = validatePassword(dto.password, dto.confirmPassword);
        if (!retVal.equals("OK")) return retVal;

        return "OK";
    }

    public String validateDoctor(DoctorDTO dto) {
        String retVal = validateEmail(dto.email);
        if (!retVal.equals("OK")) return retVal;
        retVal = validateName(dto.name);
        if (!retVal.equals("OK")) return retVal;
        retVal = validateSurname(dto.surname);
        if (!retVal.equals("OK")) return retVal;
        retVal = validatePhone(dto.phone);
        if (!retVal.equals("OK")) return retVal;
        retVal = validatePassword(dto.password, dto.confirmPassword);
        if (!retVal.equals("OK")) return retVal;
        if (dto.specialty == null) {
            return "Specialty is not choosen.";
        }
        retVal = validateShift(dto.shiftStart, dto.shiftEnd);
        if (!retVal.equals("OK")) return retVal;
        retVal = validateSpecialty(dto.specialty);
        if (!retVal.equals("OK")) return retVal;

        return "OK";
    }

    private String validatePassword(String password, String confirmPassword) {
        if (password == null) {
            return "Password cannot be empty.";
        }
        if (password.length() < 8) {
            return "Password must be longer than 8 characters.";
        }

        if (!password.equals(confirmPassword)) {
            return "Password and Confirm password fields must have the same value.";
        }
        return "OK";
    }

    private String validateEmail(String email) {
        String emailFormatErr = "Email is in the incorrect format.";
        if (email == null) {
            return "Email cannot be empty.";
        }
        if (!email.contains("@")) {
            return emailFormatErr;
        } else {
            String[] parts = email.split("@");
            if (parts.length == 0 || parts[0].isEmpty() || parts.length != 2) {
                return emailFormatErr;
            }
            if (!parts[1].contains(".")) {
                return emailFormatErr;
            } else {
                String before = parts[1].substring(0, parts[1].lastIndexOf("."));
                String after = parts[1].substring(parts[1].lastIndexOf(".") + 1);
                if (before.isEmpty() || after.isEmpty()) {
                    return emailFormatErr;
                }
            }
        }
        return "OK";
    }

    private String validateName(String name) {
        if (name == null) {
            return "Name cannot be empty.";
        }
        if (!(name.matches("^[A-ZŠĐŽČĆ][a-zšđćčžA-ZŠĐŽČĆ ]*$"))) {
            return "Name is not in the correct format.";
        }
        if (name.length() > 30) {
            return "Name must not be longer than 30 characters.";
        }
        return "OK";
    }

    private String validateSurname(String surname) {
        if (surname == null) {
            return "Surname cannot be empty.";
        }
        if (!(surname.matches("^[A-ZŠĐŽČĆ][a-zšđćčžA-ZŠĐŽČĆ ]*$"))) {
            return "Surname is not in the correct format.";
        }
        if (surname.length() > 30) {
            return "Surname must not be longer than 30 characters.";
        }
        return "OK";
    }

    private String validatePhone(String phone) {
        if (phone == null) {
            return "Phone cannot be empty.";
        }
        if (!(phone.matches("^[0-9]+$"))) {
            return "Phone number can only contain numeric values.";
        }
        if (phone.length() < 6 || phone.length() > 10) {
            return "Phone number must be between 6 and 10 characters.";
        }
        return "OK";
    }

    private String validateShift(String shiftStart, String shiftEnd) {
        try {
            LocalTime.parse(shiftStart);
            LocalTime.parse(shiftEnd);
        } catch (Exception e) {
            return "Date input is not correct.";
        }
        return "OK";
    }

    private String validateSpecialty(Long specialty) {
        if (specialty == null) {
            return "Doctor's specialty is not chosen correctly.";
        }
        return "OK";
    }

    private String validateAdress(String address, String city) {
        if (address == null) {
            return "Address cannot be empty.";
        }
        if (!(address.matches("^[A-ZŠĐŽČĆ][a-zšđćčžA-ZŠĐŽČĆ0-9 ]*$"))) {
            return "Address is not in the correct format.";
        }
        if (address.length() > 50) {
            return "Address cannot be longer than 50 characters.";
        }
        if (city == null) {
            return "City cannot be empty.";
        }
        if (!(city.matches("^[A-ZŠĐŽČĆ][a-zšđćčžA-ZŠĐŽČĆ ]*$"))) {
            return "City is not in the correct format.";
        }
        if (city.length() > 30) {
            return "City name cannot be longer than 30 characters.";
        }
        return "OK";
    }

    private String validateCountry(String country) {
        if (country == null) {
            return "false";
        }
        if (!(country.matches("^[A-ZŠĐŽČĆ][a-zšđćčžA-ZŠĐŽČĆ ]*$"))) {
            return "Country is not in the correct format.";
        }
        if (country.length() > 30) {
            return "Country name cannot be longer than 30 characters.";
        }
        return "OK";
    }

    private String validateSocialSecurityNumber(String socialSecurityNumber) {
        if (socialSecurityNumber == null || socialSecurityNumber.isEmpty()) {
            return "Social security number cannot be empty.";
        }
        if (!(socialSecurityNumber.matches("^[a-zšđćčžA-ZŠĐŽČĆ0-9]*$"))) {
            return "Social security number is not in the correct format.";
        }
        if (socialSecurityNumber.length() > 6) {
            return "Social security number cannot be longer than 6 characters.";
        }
        return "OK";
    }
}
