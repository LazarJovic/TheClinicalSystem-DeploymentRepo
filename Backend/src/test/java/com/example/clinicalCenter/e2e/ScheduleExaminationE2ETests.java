package com.example.clinicalCenter.e2e;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ScheduleExaminationE2ETests {

    private WebDriver driver;

    private PatientDashboardPage patientDashboardPage;

    @Before
    public void setUp() {

        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();

        driver.manage().window().maximize();
        patientDashboardPage = PageFactory.initElements(driver, PatientDashboardPage.class);
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void ScheduleExamination_Success() throws InterruptedException {
        driver.get("http://localhost:4200/login");
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("lazar.13jovic@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("zxczxczxc");
        driver.findElement(By.id("login-btn")).click();
        {
            WebElement element = driver.findElement(By.id("login-btn"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        patientDashboardPage.ensureIsDisplayedVisitButton();
        patientDashboardPage.getVisitButton().click();
        driver.findElement(By.id("mat-tab-label-0-2")).click();
        patientDashboardPage.ensureIsDisplayedTablePredefined();
        patientDashboardPage.ensureIsDisplayedScheduleButton();
        List<WebElement> rowsBefore = patientDashboardPage.getTablePredefined().findElements(By.tagName("tr"));
        patientDashboardPage.getScheduleButton().click();
        synchronized (driver)
        {
          driver.wait(5000);
        }
        patientDashboardPage.ensureIsDisplayedTablePredefined();
        List<WebElement> rowsAfter = patientDashboardPage.getTablePredefined().findElements(By.tagName("tr"));

        Assertions.assertEquals(rowsBefore.size() - 1, rowsAfter.size());

    }

}
