package com.example.clinicalCenter.e2e;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchDoctorsPage {
    private WebDriver driver;

    @FindBy(xpath = "//*[@id=\"div-search-doctors\"]")
    private WebElement searchDoctorsSelect;

    @FindBy(xpath = "//*[@id=\"div-search-button\"]")
    private WebElement searchButton;

    public SearchDoctorsPage() {
    }

    public SearchDoctorsPage(WebDriver driver) {
        this.driver = driver;
    }

    public void ensureIsDisplayedSearchDoctorsSelect() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(searchDoctorsSelect));
    }

    public void ensureIsDisplayedSearchButton() {
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.elementToBeClickable(searchButton));
    }

    public WebElement getSearchDoctorsSelect() {
        return searchDoctorsSelect;
    }

    public WebElement getSearchButton() {
        return searchButton;
    }
}
