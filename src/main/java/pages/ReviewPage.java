package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReviewPage extends BasePage {

    // Locator for the "Continue" button on the review page
    private final By continueButton = By.xpath("//button[contains(.,'Continue') or contains(.,'Proceed') or contains(.,'Next')]");

    public ReviewPage(WebDriver driver) {
        super(driver);
    }

    public void clickContinue() {
        // Wait for the button, then scroll and click using the WebElement
        org.openqa.selenium.WebElement btn = waitForElementVisible(continueButton);
        scrollToElement(btn);
        jsClick(btn);
    }
}
