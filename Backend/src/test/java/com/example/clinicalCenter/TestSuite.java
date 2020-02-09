package com.example.clinicalCenter;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestSuiteController.class, TestSuiteService.class,
        TestSuiteRepository.class})
public class TestSuite {
}
