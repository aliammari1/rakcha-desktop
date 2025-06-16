package com.esprit.e2e.utils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

/**
 * JUnit 5 extension for automatic screenshot capture during E2E tests.
 * <p>
 * This extension automatically captures screenshots when tests fail,
 * providing valuable debugging information and creating demo materials
 * for successful test runs.
 * </p>
 * 
 * @author RAKCHA Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class E2ETestExtension implements TestWatcher {

    /**
     * List to store screenshot paths for report generation.
     */
    private static final List<String> capturedScreenshots = new ArrayList<>();

    /**
     * List to store screenshot descriptions.
     */
    private static final List<String> screenshotDescriptions = new ArrayList<>();

    /**
     * Called when a test succeeds.
     * <p>
     * Captures a success screenshot that can be used for demo materials
     * and documentation purposes.
     * </p>
     * 
     * @param context the extension context
     */
    @Override
    public void testSuccessful(ExtensionContext context) {
        captureTestScreenshot(context, "success");
    }

    /**
     * Called when a test fails.
     * <p>
     * Captures a failure screenshot for debugging purposes, showing
     * the state of the application when the test failed.
     * </p>
     * 
     * @param context the extension context
     * @param cause   the throwable that caused the failure
     */
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        captureTestScreenshot(context, "failure");
    }

    /**
     * Called when a test is aborted.
     * <p>
     * Captures a screenshot showing the state when the test was aborted,
     * which can help with debugging test infrastructure issues.
     * </p>
     * 
     * @param context the extension context
     * @param cause   the throwable that caused the abort
     */
    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        captureTestScreenshot(context, "aborted");
    }

    /**
     * Captures a screenshot for the current test.
     * <p>
     * This method attempts to capture a screenshot if the test instance
     * is an ApplicationTest, which provides access to the JavaFX scene.
     * </p>
     * 
     * @param context the extension context containing test information
     * @param status  the test status (success, failure, aborted)
     */
    private void captureTestScreenshot(ExtensionContext context, String status) {
        try {
            Object testInstance = context.getTestInstance().orElse(null);

            if (testInstance instanceof ApplicationTest) {
                ApplicationTest appTest = (ApplicationTest) testInstance;
                String testMethod = context.getDisplayName();
                String description = String.format("%s_%s", status, testMethod);

                String screenshotPath = ScreenshotUtils.captureScreenshot(
                        appTest, testMethod, description);

                if (screenshotPath != null) {
                    synchronized (capturedScreenshots) {
                        capturedScreenshots.add(screenshotPath);
                        screenshotDescriptions.add(description);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to capture test screenshot: " + e.getMessage());
        }
    }

    /**
     * Gets all captured screenshot paths.
     * <p>
     * Returns a copy of the list of screenshot paths captured during
     * the test session. Useful for generating reports.
     * </p>
     * 
     * @return list of screenshot file paths
     */
    public static List<String> getCapturedScreenshots() {
        synchronized (capturedScreenshots) {
            return new ArrayList<>(capturedScreenshots);
        }
    }

    /**
     * Gets all screenshot descriptions.
     * <p>
     * Returns a copy of the list of screenshot descriptions corresponding
     * to the captured screenshots.
     * </p>
     * 
     * @return list of screenshot descriptions
     */
    public static List<String> getScreenshotDescriptions() {
        synchronized (screenshotDescriptions) {
            return new ArrayList<>(screenshotDescriptions);
        }
    }

    /**
     * Clears the captured screenshots list.
     * <p>
     * Useful for cleaning up between test runs or sessions.
     * </p>
     */
    public static void clearCapturedScreenshots() {
        synchronized (capturedScreenshots) {
            capturedScreenshots.clear();
            screenshotDescriptions.clear();
        }
    }

    /**
     * Generates a comprehensive screenshot report.
     * <p>
     * Creates a markdown report with all screenshots captured during
     * the test session, including their descriptions and status.
     * </p>
     * 
     * @param testClass the test class that captured screenshots
     */
    public static void generateReport(ApplicationTest testClass) {
        List<String> screenshots = getCapturedScreenshots();
        List<String> descriptions = getScreenshotDescriptions();

        if (!screenshots.isEmpty()) {
            ScreenshotUtils.createScreenshotReport(
                    testClass,
                    screenshots.toArray(new String[0]),
                    descriptions.toArray(new String[0]));
        }
    }
}
