package com.example.clinicalCenter;

import com.example.clinicalCenter.controllers.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ClinicControllerUnitTests.class, ClinicControllerIntegrationTests.class,
        ExaminationControllerIntegrationTests.class, ExaminationControllerUnitTests.class,
        ExaminationRequestControllerIntegrationTests.class, ExaminationControllerUnitTests.class})
public class TestSuiteController {
}
