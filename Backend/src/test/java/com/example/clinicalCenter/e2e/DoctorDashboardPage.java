package com.example.clinicalCenter.e2e;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DoctorDashboardPage {
    private WebDriver driver;

    @FindBy(xpath = "//*[@id=\"div-send-request\"]")
    private WebElement createRequestSelect;

    public DoctorDashboardPage() {
    }

    public DoctorDashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    public void ensureIsDisplayedCreateRequestSelect() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(createRequestSelect));
    }

    public WebElement getCreateRequestSelect() {
        return createRequestSelect;
    }

}
