package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {
    protected WebDriver driver;
    private final int TIMEOUT = 15;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    // Wait for multiple elements to be visible, with timeout in seconds
    protected java.util.List<org.openqa.selenium.WebElement> waitForElementsVisible(By locator, int timeoutSeconds) {
        org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(timeoutSeconds));
        return wait.until(driver -> {
            java.util.List<org.openqa.selenium.WebElement> elements = driver.findElements(locator);
            java.util.List<org.openqa.selenium.WebElement> visibleElements = new java.util.ArrayList<>();
            for (org.openqa.selenium.WebElement el : elements) {
                if (el.isDisplayed()) {
                    visibleElements.add(el);
                }
            }
            return visibleElements.size() > 0 ? visibleElements : null;
        });
    }

    protected WebElement waitForElementVisible(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        waitForElementVisible(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waitForElementVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForElementVisible(locator).getText();
    }

    // Waits until the element is invisible or not present
    protected void waitForElementInvisible(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // Scrolls the element into view using JavaScript
    protected void scrollToElement(By locator) {
        WebElement element = waitForElementVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // Overload: Scrolls a WebElement into view
    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // Clicks a WebElement using JavaScript
    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}

