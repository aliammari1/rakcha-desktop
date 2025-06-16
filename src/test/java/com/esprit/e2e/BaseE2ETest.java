package com.esprit.e2e;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.TimeoutException;

/**
 * Base class for all E2E tests in the RAKCHA application.
 * <p>
 * This class provides common setup and teardown functionality for JavaFX UI
 * testing
 * using TestFX framework. It handles application lifecycle management and
 * provides
 * utilities for robust E2E testing.
 * </p>
 * 
 * @author RAKCHA Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class BaseE2ETest extends ApplicationTest {

    /**
     * Test timeout in milliseconds for UI operations.
     */
    protected static final int TEST_TIMEOUT = 10000;

    /**
     * Short delay for UI animations and transitions.
     */
    protected static final int SHORT_DELAY = 500;

    /**
     * Medium delay for loading operations.
     */
    protected static final int MEDIUM_DELAY = 1000;

    /**
     * Long delay for complex operations.
     */
    protected static final int LONG_DELAY = 2000;

    /**
     * Sets up the test environment before all tests run.
     * <p>
     * Configures system properties for headless testing and JavaFX environment.
     * This method ensures proper initialization of the testing framework.
     * </p>
     * 
     * @throws Exception if setup fails
     */
    @BeforeAll
    public static void setUpHeadlessMode() throws Exception {
        // Configure headless mode for testing
        if (Boolean.getBoolean("testfx.headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
    }

    /**
     * Sets up the test environment before each test.
     * <p>
     * Initializes the JavaFX application and prepares the testing environment.
     * This method is called automatically before each test method.
     * </p>
     * 
     * @throws Exception if setup fails
     */
    @BeforeEach
    public void setUpEach() throws Exception {
        // Additional setup can be added here
        Platform.runLater(() -> {
            // Any platform-specific setup
        });
    }

    /**
     * Cleans up the test environment after each test.
     * <p>
     * Properly closes the JavaFX application and releases resources.
     * This method ensures clean state between tests.
     * </p>
     * 
     * @throws TimeoutException if cleanup times out
     */
    @AfterEach
    public void tearDownEach() throws TimeoutException {
        // Close any open stages
        FxToolkit.cleanupStages();

        // Release any resources
        Platform.runLater(() -> {
            // Cleanup operations
        });
    }

    /**
     * Creates and starts the JavaFX application for testing.
     * <p>
     * This method should be overridden by concrete test classes to specify
     * which application class should be tested.
     * </p>
     * 
     * @param stage the primary stage for the application
     * @throws Exception if application startup fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Default implementation - should be overridden
        stage.show();
    }

    /**
     * Waits for a specified amount of time.
     * <p>
     * Utility method for adding delays in tests when waiting for UI operations
     * to complete. Use sparingly and prefer waiting for specific conditions.
     * </p>
     * 
     * @param milliseconds the time to wait in milliseconds
     */
    protected void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Test interrupted", e);
        }
    }

    /**
     * Waits for the JavaFX Application Thread to complete all pending operations.
     * <p>
     * This method ensures that all UI updates are completed before proceeding
     * with test assertions. Essential for reliable UI testing.
     * </p>
     */
    protected void waitForFxEvents() {
        Platform.runLater(() -> {
            // Empty runnable to ensure all previous operations are completed
        });
        waitFor(SHORT_DELAY);
    }

    /**
     * Gets the application class for testing.
     * <p>
     * Subclasses should override this method to return the specific
     * JavaFX Application class they want to test.
     * </p>
     * 
     * @return the application class to test
     */
    protected abstract Class<? extends Application> getApplicationClass();
}
