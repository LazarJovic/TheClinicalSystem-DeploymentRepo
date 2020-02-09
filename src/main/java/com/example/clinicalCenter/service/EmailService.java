package com.example.clinicalCenter.service;

import com.example.clinicalCenter.model.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private RegisterRequestService registerRequestService;

    public void sendSimpleMessage(
            String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendVerificationMail(String email) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Email address verification");
        Long idReg = (long) 0;
        List<RegisterRequest> requests = this.registerRequestService.findAllEntity();
        for (RegisterRequest rr : requests) {
            if (rr.getEmail().equals(email)) {
                idReg = rr.getId();
            }
        }
        mail.setText("This is a verification mail. Please do visit this link:  http://localhost:4200/confirming-email/" + idReg + " for confirming your email address.");

        emailSender.send(mail);
    }

    public void sendGenericAdminMail(String emailAddress) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(emailAddress);
        mail.setSubject("Registration request received");
        mail.setText("New registration request is sent to you. Please check your request list.");

        emailSender.send(mail);
    }

    public void sendMailForExaminationRequest(String emailAddress) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(emailAddress);
        mail.setSubject("Examination request received");
        mail.setText("New examination request is sent to you. Please check your request list.");

        emailSender.send(mail);
    }

    public void sendWhenSchedulingPredefinedExamination(String emailAddress) {

        //System.out.println("Transaction email open: " + TransactionSynchronizationManager.isActualTransactionActive());

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(emailAddress);
        mail.setSubject("Predefined examination scheduled");
        mail.setText("You have scheduled a predefined examination. Please check your scheduled examinations list.");

        emailSender.send(mail);
    }

    public void sendMailForOperationRequest(String emailAddress) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(emailAddress);
        mail.setSubject("Operation request received");
        mail.setText("New operation request is sent to you. Please check your request list.");

        emailSender.send(mail);
    }

    public void sendToPatientForExamination(String patientMail, String date, String startTime, String endTime) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(patientMail);
        mail.setSubject("Examination request confirmed");
        mail.setText("A room has been assigned to the examination request for " + date + ", at time: " + startTime + "-" + endTime + ". " +
                "Please, confirm appointment by going to 'Unconfirmed appointments' on your dashboard. Log in here: http://localhost:4200");

        emailSender.send(mail);
    }

    public void sendToPatientForOperation(String patientMail, String date, String startTime, String endTime) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(patientMail);
        mail.setSubject("Operation request confirmed");
        mail.setText("A room has been assigned to the operation request for " + date + ", at time: " + startTime + "-" + endTime + ". " +
                "Please, confirm appointment by going to 'Unconfirmed appointments' on your dashboard. Log in here: http://localhost:4200.");

        emailSender.send(mail);
    }

    public void sendToCreatedUser(String email, String password) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Welcome to the clinical center");
        mail.setText("An administrator has registered you as employee at clinical center. Your password is - " + password + ".");

        emailSender.send(mail);
    }
}
