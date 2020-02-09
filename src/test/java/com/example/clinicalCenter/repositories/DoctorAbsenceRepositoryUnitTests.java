package com.example.clinicalCenter.repositories;

import com.example.clinicalCenter.model.DoctorAbsence;
import com.example.clinicalCenter.repository.DoctorAbsenceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
public class DoctorAbsenceRepositoryUnitTests {

    @Autowired
    private DoctorAbsenceRepository doctorAbsenceRepository;

    @Test
    public void testFindByStaff() {

        List<DoctorAbsence> absences = this.doctorAbsenceRepository.findByStaff(17L);

        assertEquals(1, absences.size());
        assertEquals(18, absences.get(0).getId());
    }
}
