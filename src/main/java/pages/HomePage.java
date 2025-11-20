package pages;

import java.time.LocalDate;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage extends BasePage {

    // Real locators based on Cleartrip's /flights page
    // Modal close button (top-right 'X')
    private final By closeModal = By.xpath("//*[@data-testid='closeIcon']");

    // 'Where from?' input field
    private final By fromInput = By.xpath("//input[@placeholder='Where from?']");

    // First suggestion in the 'from' dropdown (flexible: any li containing 'Delhi')
    private final By fromSuggestion = By.xpath("//li[contains(.,'Delhi')]");

    // 'Where to?' input field
    private final By toInput = By.xpath("//input[@placeholder='Where to?']");

    // First suggestion in the 'to' dropdown (flexible: any li containing 'Mumbai')
    private final By toSuggestion = By.xpath("//li[contains(.,'Mumbai')]");

    // Passenger dropdown (1 Adult, Economy)
    private final By passengerDropdown = By.xpath("//div[contains(@class,'iNidEX') and contains(.,'Adult')]");

    // Departure date element
    private final By departureDate = By.xpath("//div[contains(@class,'dSvAMK') and @class[contains(.,'mr-2')]]");

    // Return date element
    private final By returnDate = By.xpath("//div[@data-testid='dateSelectReturn']");

    // 'Search flights' button
    private final By searchButton = By.xpath("//div[contains(@class,'ibgoAF')]//h4[contains(text(),'Search flights')]");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void closeModal() {
        // Wait for the modal close button to be visible (if present), then click it and wait for it to disappear
        try {
            waitForElementVisible(closeModal);
            click(closeModal);
            waitForElementInvisible(closeModal);
        } catch (Exception e) {
            // Modal not present, continue
        }
    }


    public void enterFromCity(String city) {
        // Click and type city in 'Where from?' input
        click(fromInput);
        type(fromInput, city);

        // Wait for the first suggestion to be visible and click it
        waitForElementVisible(fromSuggestion);
        click(fromSuggestion);

        // Wait for the suggestion dropdown to disappear (to avoid overlay)
        waitForElementInvisible(fromSuggestion);
    }

    public void enterToCity(String city) {
        // Wait for 'Where to?' input to be clickable and scroll into view
        waitForElementVisible(toInput);
        scrollToElement(toInput);
        click(toInput);
        type(toInput, city);

        // Wait for the first suggestion to be visible and click it
        waitForElementVisible(toSuggestion);
        click(toSuggestion);

        // Wait for the suggestion dropdown to disappear
        waitForElementInvisible(toSuggestion);
    }

    public void selectDepartureDate() {
        scrollToElement(departureDate);
        jsClick(waitForElementVisible(departureDate));
        // Optionally, select a specific date from the calendar here
    }

    public void selectReturnDate() {
        scrollToElement(returnDate);
        jsClick(waitForElementVisible(returnDate));
        // Optionally, select a specific date from the calendar here
    }

    public void openPassengerDropdown() {
        click(passengerDropdown);
    }



    public void clickSearchFlights() {
        scrollToElement(searchButton);
        jsClick(waitForElementVisible(searchButton));
    }

    // Select a date in the calendar (for return date)
    public void selectDate(LocalDate date) {
        try {
            String day = String.valueOf(date.getDayOfMonth());
            String month = date.getMonth().toString().substring(0, 3); // e.g., "DEC"
            String year = String.valueOf(date.getYear());

            // XPath for the calendar's next month button (update if needed)
            By nextMonthBtn = By.xpath("//button[contains(@aria-label,'Next') or contains(@data-testid,'rightArrow') or contains(@class,'next')]");
            // XPath for the calendar's visible month/year label
            By monthYearLabel = By.xpath("//div[contains(@class,'DayPicker-Caption') or contains(@class,'monthTitle') or contains(@data-testid,'monthYear')]");

            // Wait for calendar to be visible
            Thread.sleep(1000);

            // Loop: check if the correct month/year is visible, else click next
            int maxTries = 18;
            boolean foundMonth = false;
            for (int i = 0; i < maxTries; i++) {
                List<WebElement> labels = driver.findElements(monthYearLabel);
                for (WebElement label : labels) {
                    String text = label.getText().toLowerCase();
                    System.out.println("Calendar visible: " + text);
                    if (text.contains(month.toLowerCase()) && text.contains(year)) {
                        foundMonth = true;
                        break;
                    }
                }
                if (foundMonth) break;
                // Click all visible next month buttons (to advance both panels if present)
                List<WebElement> nextBtns = driver.findElements(nextMonthBtn);
                boolean nextEnabled = false;
                for (WebElement btn : nextBtns) {
                    // Check if button is enabled (not disabled)
                    if (btn.isDisplayed() && btn.isEnabled() && !btn.getAttribute("class").toLowerCase().contains("disabled")) {
                        jsClick(btn);
                        Thread.sleep(300);
                        nextEnabled = true;
                    }
                }
                // If no next button is enabled, break to avoid infinite loop
                if (!nextEnabled) {
                    System.out.println("No further next month button available, reached end of calendar.");
                    break;
                }
            }

            // After navigation, if month not found, exit early
            if (!foundMonth) {
                System.out.println("Target month/year not found in calendar after navigation. Date selection aborted.");
                return;
            }

            // Now try to find and click the date
            // Try robust XPath: find any clickable day cell with the correct day, not disabled, in a visible calendar
            List<WebElement> dateElements = driver.findElements(
                    By.xpath("//div[contains(@aria-label,'" + month + "') and contains(@aria-label,'" + year + "')]//div[text()='" + day + "' and not(contains(@class,'disabled')) and (contains(@class,'DayPicker-Day') or contains(@class,'day') or @role='gridcell')]")
            );

            if (dateElements.isEmpty()) {
                // Try simpler approach - just find clickable dates with the day number
                dateElements = driver.findElements(
                        By.xpath("//div[text()='" + day + "' and not(contains(@class,'disabled')) and (contains(@class,'DayPicker-Day') or contains(@class,'day') or @role='gridcell')]"));
            }

            if (!dateElements.isEmpty()) {
                scrollToElement(dateElements.get(0));
                Thread.sleep(500);
                jsClick(dateElements.get(0));
                Thread.sleep(1000);
            } else {
                System.out.println("Date not found in the visible calendar month/year.");
            }
        } catch (Exception e) {
            System.out.println("Error in selectDate: " + e.getMessage());
        }
    }
}
