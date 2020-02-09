package com.example.clinicalCenter.e2e;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

public class ScheduleExaminationLongE2ETests {
    private WebDriver driver;

    private PatientDashboardPage patientDashboardPage;

    private SearchDoctorsPage searchDoctorsPage;

    @Before
    public void setUp() {

        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();

        driver.manage().window().maximize();
        patientDashboardPage = PageFactory.initElements(driver, PatientDashboardPage.class);
        searchDoctorsPage = PageFactory.initElements(driver, SearchDoctorsPage.class);
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void ScheduleExaminationLongTestSuccess() throws InterruptedException {
        driver.get("http://localhost:4200/login");
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("patient1@maildrop.cc");
        driver.findElement(By.id("password")).sendKeys("123456789");
        driver.findElement(By.id("password")).sendKeys(Keys.ENTER);
        patientDashboardPage.ensureIsDisplayedSearchDateInput();
        patientDashboardPage.getSearchDateInput().click();
        driver.findElement(By.id("searchDate")).sendKeys("2502");
        driver.findElement(By.id("searchDate")).sendKeys(Keys.TAB);
        driver.findElement(By.id("searchDate")).sendKeys("2020");
        driver.findElement(By.id("searchStartTime")).sendKeys("12:00");
        driver.findElement(By.id("searchEndTime")).sendKeys("13:00");
        driver.findElement(By.cssSelector("#search-examination-type-field .mat-form-field-infix")).click();
        synchronized (driver)
        {
            driver.wait(500);
        }
        driver.findElement(By.cssSelector("#mat-option-0 > .mat-option-text")).click();
        synchronized (driver)
        {
            driver.wait(500);
        }
        patientDashboardPage.ensureIsDisplayedSearchClinicButton();
        patientDashboardPage.getSearchClinicButton().click();
        synchronized (driver)
        {
            driver.wait(500);
        }
        patientDashboardPage.ensureIsDisplayedDoctorsButton();
        patientDashboardPage.getDoctorsButton().click();
        synchronized (driver)
        {
            driver.wait(500);
        }
        searchDoctorsPage.ensureIsDisplayedSearchDoctorsSelect();
        searchDoctorsPage.getSearchDoctorsSelect().click();
        synchronized (driver)
        {
            driver.wait(500);
        }
        driver.findElement(By.id("mat-input-5")).click();
        driver.findElement(By.id("mat-input-5")).sendKeys("Do");
        synchronized (driver)
        {
            driver.wait(500);
        }
        searchDoctorsPage.ensureIsDisplayedSearchButton();
        searchDoctorsPage.getSearchButton().click();
        synchronized (driver)
        {
            driver.wait(500);
        }
        driver.findElement(By.cssSelector(".mat-stroked-button > .mat-button-wrapper")).click();
        patientDashboardPage.ensureIsDisplayedScheduledButtonPatient();
        patientDashboardPage.getScheduleButtonPatient().click();
        synchronized (driver)
        {
            driver.wait(3000);
        }
        Assertions.assertNotNull(driver.findElement(By.cssSelector(".toast-success")));
    }
}
