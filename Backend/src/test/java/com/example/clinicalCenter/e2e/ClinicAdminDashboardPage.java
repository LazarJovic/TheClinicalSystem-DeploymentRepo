package com.example.clinicalCenter.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ClinicAdminDashboardPage {

    private WebDriver driver;

    @FindBy(xpath = "//*[@id=\"menu-icon\"]")
    private WebElement icon;

    @FindBy(xpath = "//*[@id=\"btn-room\"]")
    private WebElement chooseRoomButton;

    @FindBy(xpath = "//*[@id=\"btn-choose\"]")
    private WebElement chooseButton;

    @FindBy(xpath = "//*[@id=\"list-rooms\"]")
    private WebElement roomsList;

    @FindBy(xpath = "//*[@id=\"btn-assign\"]")
    private WebElement assignButton;

    @FindBy(xpath = "//*[@id=\"list-appointments\"]")
    private WebElement appointmentsList;

    @FindBy(xpath = "//*[@id=\"select-choose-doctor\"]")
    private WebElement doctorSelect;

    @FindBy(xpath = "//*[@id=\"btn-doctor-chosen\"]")
    private WebElement doctorChosenButton;

    @FindBy(xpath = "//*[@id=\"opt-doctor\"]")
    private WebElement doctorOption;


    public ClinicAdminDashboardPage() {
    }

    public ClinicAdminDashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    public void ensureIsDisplayedMatIcon() {
        (new WebDriverWait(driver, 5)).until(ExpectedConditions.elementToBeClickable(icon));
    }

    public void ensureIsDisplayedChooseRoomButton() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(chooseRoomButton));
    }

    public void ensureIsDisplayedChooseButton() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(chooseButton));
    }

    public void ensureIsDisplayedRoomsList() {
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(roomsList));
    }

    public void ensureIsNotVisibleRoomsList() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.invisibilityOfElementLocated(By.id("list-rooms")));
    }

    public void ensureIsDisplayedAppointmentsList() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.visibilityOf(appointmentsList));
    }

    public void ensureIsDisplayedDoctorSelect() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.visibilityOf(doctorSelect));
    }

    public void ensureIsDisplayedDoctorOption() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(doctorOption));
    }

    public void ensureIsDisplayedAssignRoomButton() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(assignButton));
    }

    public void ensureIsDisplayedDoctorChosenButton() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(doctorChosenButton));
    }

    public WebElement getMenuIcon() { return  icon; }

    public WebElement getChooseRoomButton() {
        return chooseRoomButton;
    }

    public WebElement getRoomsList() {
        return roomsList;
    }

    public WebElement getAppointmentsList() { return appointmentsList; }

    public WebElement getTablePredefined() { return assignButton; }

    public WebElement getDoctorSelect() { return doctorSelect; }

    public WebElement getDoctorOption() { return doctorOption; }

    public WebElement getDoctorChosenButton() { return doctorChosenButton; }

    public WebElement getChooseButton() { return chooseButton; }

}
