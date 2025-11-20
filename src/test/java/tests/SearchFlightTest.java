package tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import pages.HomePage;
import pages.ReviewPage;
import pages.SearchResultsPage;
import utils.BaseTest;
import utils.TestListener;

@Listeners({utils.TestListener.class})
public class SearchFlightTest extends BaseTest {

    @Test(priority = 1, description = "Book round-trip flight: Bangalore to Kolkata with robust logging and reporting")
    public void bookRoundTripFlight_BangaloreToKolkata() {
        try {
            TestListener.getTest().info("Navigating to Cleartrip home page");
            driver.get("https://www.cleartrip.com/");
            TestListener.captureScreenshot(driver, "HomePageLoaded");

            HomePage homePage = new HomePage(driver);

            TestListener.getTest().info("Closing modal if present");
            homePage.closeModal();
            TestListener.captureScreenshot(driver, "ModalClosed");

            TestListener.getTest().info("Entering departure city: Bangalore");
            homePage.enterFromCity("Bangalore");
            TestListener.captureScreenshot(driver, "FromCityEntered");

            TestListener.getTest().info("Entering arrival city: Kolkata");
            homePage.enterToCity("Kolkata");
            TestListener.captureScreenshot(driver, "ToCityEntered");

            TestListener.getTest().info("Selecting departure date (30 days from today)");
            homePage.selectDepartureDate();
            homePage.selectDate(java.time.LocalDate.now().plusDays(30));
            TestListener.captureScreenshot(driver, "DepartureDateSelected");

            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

            TestListener.getTest().info("Selecting return date (45 days from today)");
            homePage.selectReturnDate();
            homePage.selectDate(java.time.LocalDate.now().plusDays(45));
            TestListener.captureScreenshot(driver, "ReturnDateSelected");

            TestListener.getTest().info("Clicking 'Search flights'");
            homePage.clickSearchFlights();
            TestListener.captureScreenshot(driver, "SearchClicked");

            TestListener.getTest().info("Verifying flight search results are displayed");
            SearchResultsPage resultsPage = new SearchResultsPage(driver);
            boolean resultsDisplayed = resultsPage.isResultsDisplayed();
            if (!resultsDisplayed) {
                TestListener.getTest().fail("No flight results found. Page source: " + driver.getPageSource());
            }
            Assert.assertTrue(resultsDisplayed, "Flight search results should be displayed.");
            TestListener.captureScreenshot(driver, "ResultsDisplayed");

            TestListener.getTest().info("Selecting lowest-priced non-stop flight for outbound leg");
            boolean outboundNonStop = resultsPage.selectLowestPricedNonStopFlight(false);
            if (!outboundNonStop) {
                TestListener.getTest().warning("No non-stop outbound flight found. Selected lowest-priced flight with stops.");
            }
            TestListener.captureScreenshot(driver, "OutboundFlightSelected");

            TestListener.getTest().info("Selecting lowest-priced non-stop flight for return leg");
            boolean returnNonStop = resultsPage.selectLowestPricedNonStopFlight(true);
            if (!returnNonStop) {
                TestListener.getTest().warning("No non-stop return flight found. Selected lowest-priced flight with stops.");
            }
            TestListener.captureScreenshot(driver, "ReturnFlightSelected");

            TestListener.getTest().info("Proceeding to review page and clicking 'Continue'");
            ReviewPage reviewPage = new ReviewPage(driver);
            reviewPage.clickContinue();
            TestListener.captureScreenshot(driver, "ReviewPageContinueClicked");

            TestListener.getTest().pass("Round-trip flight booking test completed successfully.");
        } catch (Exception e) {
            TestListener.getTest().fail("Test failed due to exception: " + e.getMessage());
            TestListener.captureScreenshot(driver, "Exception");
            Assert.fail("Test failed due to exception: " + e.getMessage());
        }
    }
}
