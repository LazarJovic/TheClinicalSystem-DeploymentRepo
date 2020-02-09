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

public class AssignRoomExaminationE2ETests {

    private WebDriver driver;

    private LoginPage loginPage;

    private ClinicAdminDashboardPage clinicAdminDashboardPage;

    @Before
    public void setUp() {

        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();

        driver.manage().window().maximize();
        loginPage = PageFactory.initElements(driver, LoginPage.class);
        clinicAdminDashboardPage = PageFactory.initElements(driver, ClinicAdminDashboardPage.class);
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void testAssignRoomExaminationSuccess() throws InterruptedException {
        driver.get("http://localhost:4200/login");
        loginPage.getEmail().click();
        loginPage.getEmail().sendKeys("admin1@maildrop.cc");
        loginPage.getPassword().click();
        loginPage.getPassword().sendKeys("asdasdasd");
        loginPage.getLoginBtn().click();
        {
            WebElement element = loginPage.getLoginBtn();
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }

        clinicAdminDashboardPage.ensureIsDisplayedMatIcon();
        clinicAdminDashboardPage.getMenuIcon().click();
        driver.findElement(By.cssSelector(".link-btn:nth-child(12) > .mat-list-item-content")).click();
        clinicAdminDashboardPage.ensureIsDisplayedChooseRoomButton();
        List<WebElement> rowsBefore = clinicAdminDashboardPage.getAppointmentsList().findElements(By.tagName("tr"));
        driver.findElement(By.cssSelector(".mat-row:nth-child(1) .mat-button-wrapper")).click();
        clinicAdminDashboardPage.ensureIsDisplayedRoomsList();
        driver.findElement(By.cssSelector("#btn-assign > .mat-button-wrapper")).click();

        clinicAdminDashboardPage.ensureIsNotVisibleRoomsList();
        clinicAdminDashboardPage.ensureIsDisplayedAppointmentsList();
        synchronized (driver)
        {
            driver.wait(3000);
        }
        List<WebElement> rowsAfter = clinicAdminDashboardPage.getAppointmentsList().findElements(By.tagName("tr"));

        Assertions.assertEquals(rowsBefore.size() - 1, rowsAfter.size());
    }

    @Test
    public void testAssignRoomExaminationSuccessChangedDateTime() throws InterruptedException {
        driver.get("http://localhost:4200/login");
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("admin1@maildrop.cc");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("asdasdasd");
        driver.findElement(By.id("login-btn")).click();
        {
            WebElement element = loginPage.getLoginBtn();
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        clinicAdminDashboardPage.ensureIsDisplayedMatIcon();
        clinicAdminDashboardPage.getMenuIcon().click();
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }
        driver.findElement(By.cssSelector(".link-btn:nth-child(12) > .mat-list-item-content")).click();
        clinicAdminDashboardPage.ensureIsDisplayedChooseRoomButton();
        List<WebElement> rowsBefore = clinicAdminDashboardPage.getAppointmentsList().findElements(By.tagName("tr"));
        driver.findElement(By.cssSelector(".mat-row:nth-child(1) .mat-button-wrapper")).click();
        clinicAdminDashboardPage.ensureIsDisplayedRoomsList();
        driver.findElement(By.cssSelector("#btn-assign > .mat-button-wrapper")).click();

        clinicAdminDashboardPage.ensureIsNotVisibleRoomsList();
        clinicAdminDashboardPage.ensureIsDisplayedAppointmentsList();
        synchronized (driver)
        {
            driver.wait(5000);
        }
        List<WebElement> rowsAfter = clinicAdminDashboardPage.getAppointmentsList().findElements(By.tagName("tr"));

        Assertions.assertEquals(rowsBefore.size() - 1, rowsAfter.size());
    }

    @Test
    public void testAssignRoomExaminationSuccessChangedDateTimeAndDoctor() throws InterruptedException {
        driver.get("http://localhost:4200/login");
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("admin1@maildrop.cc");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("asdasdasd");
        driver.findElement(By.id("login-btn")).click();
        {
            WebElement element = driver.findElement(By.id("login-btn"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        clinicAdminDashboardPage.ensureIsDisplayedMatIcon();
        clinicAdminDashboardPage.getMenuIcon().click();
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }
        driver.findElement(By.cssSelector(".link-btn:nth-child(12) > .mat-list-item-content")).click();
        clinicAdminDashboardPage.ensureIsDisplayedChooseRoomButton();
        List<WebElement> rowsBefore = clinicAdminDashboardPage.getAppointmentsList().findElements(By.tagName("tr"));
        driver.findElement(By.cssSelector(".mat-row:nth-child(1) .mat-button-wrapper")).click();
        clinicAdminDashboardPage.ensureIsDisplayedRoomsList();
        clinicAdminDashboardPage.ensureIsDisplayedChooseButton();
        clinicAdminDashboardPage.getChooseButton().click();
        synchronized (driver)
        {
            driver.wait(2000);
        }
        clinicAdminDashboardPage.ensureIsDisplayedDoctorSelect();
        clinicAdminDashboardPage.getDoctorSelect().click();
        clinicAdminDashboardPage.ensureIsDisplayedDoctorOption();
        driver.findElement(By.cssSelector("#opt-doctor")).click();
        clinicAdminDashboardPage.ensureIsDisplayedDoctorChosenButton();
        clinicAdminDashboardPage.getDoctorChosenButton().click();

        clinicAdminDashboardPage.ensureIsDisplayedAppointmentsList();
        synchronized (driver)
        {
            driver.wait(5000);
        }
        List<WebElement> rowsAfter = clinicAdminDashboardPage.getAppointmentsList().findElements(By.tagName("tr"));

        Assertions.assertEquals(rowsBefore.size() - 1, rowsAfter.size());
    }

}
