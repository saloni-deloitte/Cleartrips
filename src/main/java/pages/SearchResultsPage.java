package pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SearchResultsPage extends BasePage {

    // Locators (update as needed for your DOM)
    // Each flight card (outbound or return)
    private final By flightCards = By.xpath("//div[contains(@class,'dczbns') and contains(@class,'bg-white')]");
    // Non-stop indicator within a flight card
    private final By nonStopTag = By.xpath(".//*[contains(text(),'Non-stop') or contains(text(),'Non stop') or contains(text(),'NonStop')]");
    // Price within a flight card (h2 with ₹)
    private final By priceTag = By.xpath(".//h2[contains(text(),'₹') or contains(text(),'?')]");
    // "Book" button within a flight card
    private final By bookNowButton = By.xpath(".//button[.//h4[contains(text(),'Book')]]");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isResultsDisplayed() {
        try {
            // Wait up to 20 seconds for at least one flight card to appear
            List<WebElement> cards = waitForElementsVisible(flightCards, 20);
            if (cards != null && cards.size() > 0) {
                return true;
            }
            // Fallback: try a more generic card/container
            List<WebElement> fallbackCards = driver.findElements(By.xpath("//*[contains(@class,'card') or contains(@class,'result')]"));
            return fallbackCards != null && fallbackCards.size() > 0;
        } catch (Exception e) {
            // Optionally, check for a "no flights found" message
            try {
                WebElement noFlights = driver.findElement(By.xpath("//*[contains(text(),'No flights') or contains(text(),'not available')]"));
                if (noFlights.isDisplayed()) {
                    return false;
                }
            } catch (Exception ignore) {}
            return false;
        }
    }


    public boolean selectLowestPricedNonStopFlight(boolean isReturnLeg) {
        // If isReturnLeg, scroll to return section (if needed)
        if (isReturnLeg) {
            try {
                WebElement returnSection = driver.findElement(By.xpath("//*[contains(text(),'Return') or contains(text(),'return')]"));
                scrollToElement(returnSection);
            } catch (Exception e) {
                // Section not found, continue
            }
        }

        List<WebElement> cards = driver.findElements(flightCards);
        WebElement lowestCard = null;
        int lowestPrice = Integer.MAX_VALUE;

        for (WebElement card : cards) {
            try {
                // Check for non-stop tag
                List<WebElement> nonStop = card.findElements(nonStopTag);
                if (nonStop.isEmpty()) continue;

                // Get price
                List<WebElement> priceElements = card.findElements(priceTag);
                if (priceElements.isEmpty()) continue;
                String priceText = priceElements.get(0).getText().replaceAll("[^0-9]", "");
                if (priceText.isEmpty()) continue;
                int price = Integer.parseInt(priceText);

                if (price < lowestPrice) {
                    lowestPrice = price;
                    lowestCard = card;
                }
            } catch (Exception e) {
                // Skip problematic card
            }
        }

        if (lowestCard != null) {
            scrollToElement(lowestCard);
            // Click "Book Now" inside this card
            List<WebElement> bookBtns = lowestCard.findElements(bookNowButton);
            if (!bookBtns.isEmpty()) {
                jsClick(bookBtns.get(0));
            } else {
                // Fallback: click the card itself
                jsClick(lowestCard);
            }
            return true;
        } else {
            // Fallback: select the lowest-priced flight regardless of stops
            WebElement lowestAnyCard = null;
            int lowestAnyPrice = Integer.MAX_VALUE;
            for (WebElement card : cards) {
                try {
                    List<WebElement> priceElements = card.findElements(priceTag);
                    if (priceElements.isEmpty()) continue;
                    String priceText = priceElements.get(0).getText().replaceAll("[^0-9]", "");
                    if (priceText.isEmpty()) continue;
                    int price = Integer.parseInt(priceText);

                    if (price < lowestAnyPrice) {
                        lowestAnyPrice = price;
                        lowestAnyCard = card;
                    }
                } catch (Exception e) {
                    // Skip problematic card
                }
            }
            if (lowestAnyCard != null) {
                scrollToElement(lowestAnyCard);
                List<WebElement> bookBtns = lowestAnyCard.findElements(bookNowButton);
                if (!bookBtns.isEmpty()) {
                    jsClick(bookBtns.get(0));
                } else {
                    jsClick(lowestAnyCard);
                }
                return false;
            } else {
                throw new RuntimeException("No flights found for this leg.");
            }
        }
    }
}
