package com.example.clinicalCenter;

import com.example.clinicalCenter.repositories.DoctorAbsenceRepositoryUnitTests;
import com.example.clinicalCenter.repositories.OperationRepositoryUnitTests;
import com.example.clinicalCenter.repositories.RoomRepositoryUnitTests;
import com.example.clinicalCenter.repositories.UserRepositoryUnitTests;
import com.example.clinicalCenter.services.ExaminationServiceUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({DoctorAbsenceRepositoryUnitTests.class, ExaminationServiceUnitTests.class,
        OperationRepositoryUnitTests.class, RoomRepositoryUnitTests.class, UserRepositoryUnitTests.class})
public class TestSuiteRepository {
}
