package com.example.clinicalCenter.e2e;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PatientDashboardPage {
    private WebDriver driver;

    @FindBy(xpath = "//*[@id=\"btn-visit\"]")
    private WebElement visitButton;

    @FindBy(xpath = "//*[@id=\"btn-schedule\"]")
    private WebElement scheduleButton;

    @FindBy(xpath = "//*[@id=\"table-predefined\"]")
    private WebElement tablePredefined;

    @FindBy(xpath = "//*[@id=\"schedule-exam-button\"]")
    private WebElement scheduleButtonPatient;

    @FindBy(xpath = "//*[@id=\"searchDate\"]")
    private WebElement searchDateInput;

    @FindBy(xpath = "//*[@id=\"search-clinic-button\"]")
    private WebElement searchClinicButton;

    @FindBy(xpath = "//*[@id=\"doctors-button\"]")
    private WebElement doctorsButton;

    public PatientDashboardPage() {
    }

    public PatientDashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    public void ensureIsDisplayedVisitButton() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(visitButton));
    }

    public void ensureIsDisplayedScheduleButton() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(scheduleButton));
    }

    public void ensureIsDisplayedTablePredefined() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(tablePredefined));
    }

    public void ensureIsDisplayedScheduledButtonPatient() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(scheduleButtonPatient));
    }

    public void ensureIsDisplayedSearchDateInput() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(searchDateInput));
    }

    public void ensureIsDisplayedSearchClinicButton() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(searchClinicButton));
    }

    public void ensureIsDisplayedDoctorsButton() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(doctorsButton));
    }

    public WebElement getVisitButton() {
        return visitButton;
    }

    public WebElement getScheduleButton() {
        return scheduleButton;
    }

    public WebElement getTablePredefined() { return tablePredefined; }

    public WebElement getScheduleButtonPatient() { return scheduleButtonPatient; }

    public WebElement getSearchDateInput() {
        return searchDateInput;
    }

    public WebElement getSearchClinicButton() {
        return searchClinicButton;
    }

    public WebElement getDoctorsButton() {
        return doctorsButton;
    }
}
