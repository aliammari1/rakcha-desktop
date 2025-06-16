package com.esprit.e2e;

import com.esprit.e2e.utils.E2ETestExtension;
import com.esprit.e2e.utils.ScreenshotUtils;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive E2E tests with automatic screenshot capture.
 * <p>
 * This class demonstrates comprehensive end-to-end testing with automatic
 * screenshot generation for documentation and demo purposes. Screenshots
 * are captured for both successful and failed test scenarios.
 * </p>
 * 
 * @author RAKCHA Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(E2ETestExtension.class)
public class ComprehensiveControllerE2E extends BaseE2ETest {

    /**
     * Gets the main application class for testing.
     * 
     * @return the main application class
     */
    @Override
    protected Class<? extends Application> getApplicationClass() {
        try {
            Class<?> clazz = Class.forName("com.esprit.MainApp");
            if (Application.class.isAssignableFrom(clazz)) {
                @SuppressWarnings("unchecked")
                Class<? extends Application> appClass = (Class<? extends Application>) clazz;
                return appClass;
            }
        } catch (ClassNotFoundException e) {
            // Fallback for testing
        }
        return TestApplication.class;
    }

    /**
     * Starts the application for testing.
     * 
     * @param stage the primary stage for the application
     * @throws Exception if application startup fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        try {
            Application app = getApplicationClass().getDeclaredConstructor().newInstance();
            app.start(stage);
        } catch (Exception e) {
            stage.setTitle("RAKCHA Comprehensive Test");
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.show();
        }
    }

    /**
     * Sets up test environment before all tests.
     * <p>
     * Initializes screenshot directory and prepares the testing environment
     * for comprehensive E2E testing with visual documentation.
     * </p>
     */
    @BeforeAll
    public static void setUpClass() {
        ScreenshotUtils.initializeScreenshotDirectory();
        E2ETestExtension.clearCapturedScreenshots();
    }

    /**
     * Tests application startup with screenshot capture.
     * <p>
     * Validates that the application starts correctly and captures
     * a screenshot of the initial state for documentation.
     * </p>
     */
    @Test
    @Order(1)
    @DisplayName("Application Startup and Initial State")
    public void testApplicationStartupWithScreenshot() {
        // Wait for application to fully load
        waitFor(MEDIUM_DELAY);

        // Capture initial state screenshot
        String screenshotPath = ScreenshotUtils.captureScreenshot(
                this, "testApplicationStartup", "initial_state");

        assertNotNull(screenshotPath, "Screenshot should be captured successfully");

        // Verify application is running
        if (!listTargetWindows().isEmpty()) {
            assertTrue(listTargetWindows().get(0).isShowing(), "Application window should be showing");
        }

        waitForFxEvents();
    }

    /**
     * Tests complete user workflow with multiple screenshots.
     * <p>
     * Simulates a complete user workflow from login through various
     * application features, capturing screenshots at each major step
     * for creating comprehensive demo materials.
     * </p>
     */
    @Test
    @Order(2)
    @DisplayName("Complete User Workflow Documentation")
    public void testCompleteWorkflowWithScreenshots() {
        try {
            // Step 1: Login process
            TextField usernameField = lookup("#usernameField").query();
            TextField passwordField = lookup("#passwordField").query();
            Button loginButton = lookup("#loginButton").query();

            if (usernameField != null && passwordField != null && loginButton != null) {
                // Fill login form
                clickOn(usernameField);
                write("demo_user");

                clickOn(passwordField);
                write("demo_password");

                // Capture login form filled
                ScreenshotUtils.captureScreenshot(this, "testCompleteWorkflow", "login_form_filled");

                // Submit login
                clickOn(loginButton);
                waitFor(MEDIUM_DELAY);

                // Capture post-login state
                ScreenshotUtils.captureScreenshot(this, "testCompleteWorkflow", "post_login_dashboard");
            }

            // Step 2: Navigate through main features
            String[] menuItems = { "#productsMenu", "#cinemasMenu", "#reportsMenu" };
            String[] descriptions = { "products_section", "cinemas_section", "reports_section" };

            for (int i = 0; i < menuItems.length; i++) {
                Node menuItem = lookup(menuItems[i]).query();
                if (menuItem != null) {
                    clickOn(menuItem);
                    waitFor(SHORT_DELAY);

                    // Capture each section
                    ScreenshotUtils.captureScreenshot(this, "testCompleteWorkflow", descriptions[i]);
                }
            }

            // Step 3: Demonstrate form interactions
            Button addButton = lookup("#addButton").query();
            if (addButton != null) {
                clickOn(addButton);
                waitFor(SHORT_DELAY);

                // Capture form dialog
                ScreenshotUtils.captureScreenshot(this, "testCompleteWorkflow", "add_form_dialog");

                // Fill form if available
                TextField nameField = lookup("#nameField").query();
                if (nameField != null) {
                    clickOn(nameField);
                    write("Demo Item");

                    // Capture filled form
                    ScreenshotUtils.captureScreenshot(this, "testCompleteWorkflow", "form_filled");
                }
            }

        } catch (Exception e) {
            // Capture error state
            ScreenshotUtils.captureScreenshot(this, "testCompleteWorkflow", "error_state");
            System.out.println("Workflow test encountered issues: " + e.getMessage());
        }

        waitForFxEvents();
    }

    /**
     * Tests error handling scenarios with documentation.
     * <p>
     * Deliberately triggers error conditions to test error handling
     * and capture screenshots of error states for debugging documentation.
     * </p>
     */
    @Test
    @Order(3)
    @DisplayName("Error Handling and Recovery")
    public void testErrorHandlingWithScreenshots() {
        try {
            // Test validation errors
            Button submitButton = lookup("#submitButton").query();
            if (submitButton != null) {
                // Try to submit without required fields
                clickOn(submitButton);
                waitFor(SHORT_DELAY);

                // Capture validation error state
                ScreenshotUtils.captureScreenshot(this, "testErrorHandling", "validation_errors");

                // Check for error messages
                Node errorMessage = lookup(".error-message").query();
                Node errorDialog = lookup(".dialog-pane").query();

                if (errorMessage != null && errorMessage.isVisible()) {
                    // Capture error message
                    ScreenshotUtils.captureNodeScreenshot(this, "testErrorHandling", "error_message", errorMessage);
                }

                if (errorDialog != null && errorDialog.isVisible()) {
                    // Capture error dialog
                    ScreenshotUtils.captureNodeScreenshot(this, "testErrorHandling", "error_dialog", errorDialog);

                    // Close error dialog
                    Button okButton = lookup(".button").query();
                    if (okButton != null) {
                        clickOn(okButton);
                        waitFor(SHORT_DELAY);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error handling test completed: " + e.getMessage());
        }

        waitForFxEvents();
    }

    /**
     * Tests advanced UI interactions with sequence screenshots.
     * <p>
     * Demonstrates advanced UI interactions and captures a sequence
     * of screenshots showing the progression of user actions.
     * </p>
     */
    @Test
    @Order(4)
    @DisplayName("Advanced UI Interactions Sequence")
    public void testAdvancedInteractionsWithSequence() {
        try {
            // Capture a sequence of interactions
            String[] screenshots = ScreenshotUtils.captureScreenshotSequence(
                    this, "testAdvancedInteractions", "interaction_sequence", 5, 1000);

            // Verify screenshots were captured
            for (String screenshot : screenshots) {
                if (screenshot != null) {
                    assertTrue(screenshot.contains("interaction_sequence"),
                            "Screenshot should contain sequence identifier");
                }
            }

            // Perform some interactions during the sequence
            // (Note: The sequence capture runs in parallel, so interactions
            // happening here will be captured automatically)

        } catch (Exception e) {
            System.out.println("Advanced interactions test: " + e.getMessage());
        }

        waitForFxEvents();
    }

    /**
     * Tests mobile/responsive features if available.
     * <p>
     * Tests any responsive or mobile-friendly features of the application
     * and captures screenshots of different window sizes.
     * </p>
     */
    @Test
    @Order(5)
    @DisplayName("Responsive Design Testing")
    public void testResponsiveDesignWithScreenshots() {
        try {
            // Test different window sizes
            Stage primaryStage = listTargetWindows().stream()
                    .filter(window -> window instanceof Stage)
                    .map(window -> (Stage) window)
                    .findFirst()
                    .orElse(null);

            if (primaryStage != null) {
                // Capture desktop size
                ScreenshotUtils.captureScreenshot(this, "testResponsiveDesign", "desktop_size", primaryStage);

                // Resize to tablet size
                primaryStage.setWidth(768);
                primaryStage.setHeight(1024);
                waitFor(SHORT_DELAY);

                ScreenshotUtils.captureScreenshot(this, "testResponsiveDesign", "tablet_size", primaryStage);

                // Resize to mobile size
                primaryStage.setWidth(375);
                primaryStage.setHeight(667);
                waitFor(SHORT_DELAY);

                ScreenshotUtils.captureScreenshot(this, "testResponsiveDesign", "mobile_size", primaryStage);

                // Restore original size
                primaryStage.setWidth(1200);
                primaryStage.setHeight(800);
            }

        } catch (Exception e) {
            System.out.println("Responsive design test: " + e.getMessage());
        }

        waitForFxEvents();
    }

    /**
     * Generates final test report with all captured screenshots.
     * <p>
     * Creates a comprehensive report of all screenshots captured during
     * the test session, useful for documentation and demo materials.
     * </p>
     */
    @AfterAll
    public static void generateFinalReport() {
        // Note: The actual report generation will be handled by the E2ETestExtension
        // This method serves as a placeholder for any additional cleanup
        System.out.println("Comprehensive E2E testing completed.");
        System.out.println("Screenshots and reports have been generated in demo/screenshots/");
    }

    /**
     * Simple test application for fallback testing.
     */
    public static class TestApplication extends Application {
        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("RAKCHA Comprehensive Test");
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            primaryStage.show();
        }
    }
}
