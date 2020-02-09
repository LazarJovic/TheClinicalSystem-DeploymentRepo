package com.example.clinicalCenter.e2e;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

public class CreateExaminationRequestE2ETests {

    private WebDriver driver;

    private DoctorDashboardPage doctorDashboardPage;

    @Before
    public void setUp() {

        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();

        driver.manage().window().maximize();
        doctorDashboardPage = PageFactory.initElements(driver, DoctorDashboardPage.class);
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void CreateExaminationRequest_Success() throws InterruptedException {
        driver.get("http://localhost:4200/login");
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("doctor2@maildrop.cc");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("123456789");
        driver.findElement(By.id("password")).sendKeys(Keys.ENTER);
        justWait();
        {
            WebElement element = driver.findElement(By.cssSelector(".mat-icon"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        driver.findElement(By.cssSelector(".mat-icon")).click();
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }
        driver.findElement(By.cssSelector(".ng-star-inserted > .mat-list-item-content")).click();
        justWait();
        doctorDashboardPage.ensureIsDisplayedCreateRequestSelect();
        doctorDashboardPage.getCreateRequestSelect().click();
        justWait();
        driver.findElement(By.id("mat-input-2")).click();
        justWait();
        driver.findElement(By.id("mat-input-2")).sendKeys("2802");
        driver.findElement(By.id("mat-input-2")).sendKeys(Keys.TAB);
        driver.findElement(By.id("mat-input-2")).sendKeys("2020");
        driver.findElement(By.id("mat-input-3")).click();
        driver.findElement(By.id("mat-input-3")).sendKeys("10:00");
        driver.findElement(By.id("mat-input-4")).click();
        driver.findElement(By.id("mat-input-4")).sendKeys("11:00");
        driver.findElement(By.cssSelector(".send-button")).click();
        synchronized (driver)
        {
            driver.wait(3000);
        }
        Assertions.assertNotNull(driver.findElement(By.cssSelector(".toast-success")));
    }

    private void justWait() throws InterruptedException {
        synchronized (driver)
        {
            driver.wait(1000);
        }
    }
}
