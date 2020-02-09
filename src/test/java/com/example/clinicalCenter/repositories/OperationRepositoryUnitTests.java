package com.example.clinicalCenter.repositories;

import com.example.clinicalCenter.model.Operation;
import com.example.clinicalCenter.repository.OperationRepository;
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
public class OperationRepositoryUnitTests {

    @Autowired
    private OperationRepository operationRepository;

    @Test
    public void testFindAllActiveExaminationsOfDoctor() {
        List<Operation> examinations = this.operationRepository.findAllActiveOperationsOfDoctor(DOCTOR_ID);
        assertEquals(0, examinations.size());
    }
}
