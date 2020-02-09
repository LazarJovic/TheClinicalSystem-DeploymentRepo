package com.example.clinicalCenter.repositories;

import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.model.User;
import com.example.clinicalCenter.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.example.clinicalCenter.constants.UserConstants.PATIENT_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
public class UserRepositoryUnitTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByEmailReturnsValue() {

        User user = this.userRepository.findByEmail(PATIENT_EMAIL);

        assertEquals(PATIENT_EMAIL, user.getEmail());
    }

    @Test
    public void testFindDoctorsOfClinicBySpecialty() {

        List<Doctor> doctors = this.userRepository.findDoctorsOfClinicBySpecialty(2L, 10L);

        assertEquals(2, doctors.size());
        assertEquals("doctor1@maildrop.cc", doctors.get(0).getEmail());
        assertEquals("doctor2@maildrop.cc", doctors.get(1).getEmail());
    }

    @Test
    public void testFindDoctorsOfClinic() {

        List<Doctor> doctors = this.userRepository.findDoctorsOfClinic(2L);

        assertEquals(3, doctors.size());
        assertEquals("doctor1@maildrop.cc", doctors.get(0).getEmail());
        assertEquals("doctor2@maildrop.cc", doctors.get(1).getEmail());
        assertEquals("doctor3@maildrop.cc", doctors.get(2).getEmail());
    }
}
