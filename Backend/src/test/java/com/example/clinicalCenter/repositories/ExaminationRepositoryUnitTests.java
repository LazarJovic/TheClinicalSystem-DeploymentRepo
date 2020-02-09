package com.example.clinicalCenter.repositories;

import com.example.clinicalCenter.model.Examination;
import com.example.clinicalCenter.repository.ExaminationRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.example.clinicalCenter.constants.UserConstants.DOCTOR_ID;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
public class ExaminationRepositoryUnitTests {

    @Autowired
    private ExaminationRepository examinationRepository;

    @Test
    public void testFindAllActiveExaminationsOfDoctor() {
        List<Examination> examinations = this.examinationRepository.findAllActiveExaminationsOfDoctor(DOCTOR_ID);
        assertEquals(2, examinations.size());
        Long[] actuals = {examinations.get(0).getId(), examinations.get(1).getId()};
        Long[] expecteds = {12L, 14L};
        Assert.assertArrayEquals(expecteds, actuals);
    }

}
