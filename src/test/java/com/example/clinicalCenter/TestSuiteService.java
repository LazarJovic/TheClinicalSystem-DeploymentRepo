package com.example.clinicalCenter;

import com.example.clinicalCenter.services.ClinicServiceUnitTests;
import com.example.clinicalCenter.services.DoctorServiceUnitTests;
import com.example.clinicalCenter.services.ExaminationRequestServiceUnitTests;
import com.example.clinicalCenter.services.ExaminationServiceUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ClinicServiceUnitTests.class, DoctorServiceUnitTests.class,
        ExaminationRequestServiceUnitTests.class, ExaminationServiceUnitTests.class})
public class TestSuiteService {
}
