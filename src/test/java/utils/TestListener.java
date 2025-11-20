package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class TestListener implements ITestListener {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/ExtentReport.html");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("Cleartrip Automation Report");
        sparkReporter.config().setReportName("Flight Booking Automation");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test Passed");
        attachScreenshot(result, "Success");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, "Test Failed: " + result.getThrowable());
        attachScreenshot(result, "Failure");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "Test Skipped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    private void attachScreenshot(ITestResult result, String status) {
        Object currentClass = result.getInstance();
        WebDriver driver = ((utils.BaseTest) currentClass).driver;
        String screenshotPath = captureScreenshot(driver, result.getMethod().getMethodName() + "_" + status);
        if (screenshotPath != null) {
            test.get().addScreenCaptureFromPath(screenshotPath);
        }
    }

    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String dest = "test-output/screenshots/" + screenshotName + "_" + date + ".png";
            Files.createDirectories(Paths.get("test-output/screenshots/"));
            Files.copy(src.toPath(), Paths.get(dest));
            return dest;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ExtentTest getTest() {
        return test.get();
    }
}

