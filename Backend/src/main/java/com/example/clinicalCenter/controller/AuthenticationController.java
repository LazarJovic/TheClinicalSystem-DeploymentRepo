package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.PasswordChangingDTO;
import com.example.clinicalCenter.model.UserTokenState;
import com.example.clinicalCenter.security.TokenUtils;
import com.example.clinicalCenter.security.auth.JwtAuthenticationRequest;
import com.example.clinicalCenter.service.AuthenticationService;
import com.example.clinicalCenter.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(value = "/login")
    public ResponseEntity<UserTokenState> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
                                                                    HttpServletResponse response) throws AuthenticationException, IOException {
        UserTokenState userToken = authenticationService.createAuthenticationToken(authenticationRequest, response);

        //return new ResponseEntity<Object>(authenticationService.createAuthenticationToken(authenticationRequest, response), HttpStatus.OK);
        return new ResponseEntity<UserTokenState>(userToken, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_CENTER_ADMIN', 'ROLE_CLINIC_ADMIN', 'ROLE_DOCTOR', 'ROLE_NURSE', 'ROLE_PATIENT')")
    @PostMapping(value = "/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangingDTO passwordChanging) {
        userDetailsService.changePassword(passwordChanging.oldPassword, passwordChanging.newPassword);
        passwordChanging.oldPassword = "result";
        passwordChanging.newPassword = "success";
        return new ResponseEntity<>(passwordChanging, HttpStatus.OK);  // sta vratiti?
    }

}
