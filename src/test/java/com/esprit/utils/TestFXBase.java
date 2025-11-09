package com.esprit.utils;

import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import org.testfx.util.WaitForAsyncUtils;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

/**
 * Base class for TestFX-based UI tests in the RAKCHA application.
 * Provides common utility methods and setup/teardown functionality.
 * 
 * Best Practices Implemented:
 * - Proper JavaFX thread management
 * - Headless testing support
 * - Cleanup after each test
 * - Reusable utility methods
 * - Proper waiting for async operations
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class TestFXBase extends ApplicationTest {

    /**
     * Initialize JavaFX toolkit for headless testing
     */
    @BeforeAll
    public static void setUpHeadlessMode() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    /**
     * Clean up after each test to ensure test isolation
     */
    @AfterEach
    public void afterEachTest() throws TimeoutException {
        // Release all keys and mouse buttons
        release(new KeyCode[] {});
        release(new MouseButton[] {});

        // Close all windows except the primary stage
        FxToolkit.cleanupStages();

        // Wait for all pending JavaFX events to complete
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Wait for JavaFX events to complete
     */
    protected void waitForFxEvents() {
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Wait for a node to be visible and ready
     * Useful for waiting for UI elements to load
     * 
     * @param query CSS query or node ID
     * @param timeoutSeconds Timeout in seconds (default: 5)
     */
    protected void waitForNode(String query, int timeoutSeconds) {
        try {
            org.testfx.util.WaitForAsyncUtils.waitFor(timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS, () -> {
                try {
                    Node node = lookup(query).query();
                    return node != null && node.isVisible();
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (Exception e) {
            // Node not found or timeout - continue anyway
        }
    }

    /**
     * Click on a node and wait for events to complete
     * 
     * @param query CSS query or node ID
     */
    protected void clickOnAndWait(String query) {
        clickOn(query);
        waitForFxEvents();
    }

    /**
     * Type text in a field and wait for events to complete
     * 
     * @param text Text to type
     */
    protected void writeAndWait(String text) {
        write(text);
        waitForFxEvents();
    }

    /**
     * Clear a text field by selecting all and deleting
     * Supports macOS by using META key instead of CONTROL
     * 
     * @param query CSS query or node ID
     */
    protected void clearTextField(String query) {
        clickOn(query);
        // Detect if running on macOS
        String osName = System.getProperty("os.name", "").toLowerCase();
        boolean isMac = osName.contains("mac");
        
        if (isMac) {
            push(KeyCode.META, KeyCode.A);
        } else {
            push(KeyCode.CONTROL, KeyCode.A);
        }
        push(KeyCode.DELETE);
        waitForFxEvents();
    }

    /**
     * Fill a text field with new content
     * 
     * @param query CSS query or node ID
     * @param text  Text to fill
     */
    protected void fillTextField(String query, String text) {
        clearTextField(query);
        clickOn(query);
        writeAndWait(text);
    }

    /**
     * Verify that a node is visible
     * 
     * @param query CSS query or node ID
     */
    protected void verifyVisible(String query) {
        verifyThat(query, isVisible());
    }

    /**
     * Verify that a node is not visible
     * 
     * @param query CSS query or node ID
     */
    protected void verifyNotVisible(String query) {
        Node node = lookup(query).query();
        assertThat(node).isNotNull();
        assertThat(node.isVisible()).isFalse();
    }

    /**
     * Verify that a node is enabled
     * 
     * @param query CSS query or node ID
     */
    protected void verifyEnabled(String query) {
        verifyThat(query, isEnabled());
    }

    /**
     * Verify that a node is disabled
     * 
     * @param query CSS query or node ID
     */
    protected void verifyDisabled(String query) {
        verifyThat(query, isDisabled());
    }

    /**
     * Verify that a labeled node has specific text
     * 
     * @param query        CSS query or node ID
     * @param expectedText Expected text content
     */
    protected void verifyText(String query, String expectedText) {
        verifyThat(query, hasText(expectedText));
    }

    /**
     * Get a node by its query
     * 
     * @param query CSS query or node ID
     * @param <T>   Node type
     * @return The found node
     */
    protected <T extends Node> T getNode(String query) {
        return lookup(query).query();
    }

    /**
     * Get the text from a TextField or TextInputControl
     * 
     * @param query CSS query or node ID
     * @return Text content
     */
    protected String getTextFieldValue(String query) {
        TextInputControl field = lookup(query).query();
        return field.getText();
    }

    /**
     * Get the text from a Label
     * 
     * @param query CSS query or node ID
     * @return Label text
     */
    protected String getLabelText(String query) {
        Label label = lookup(query).query();
        return label.getText();
    }

    /**
     * Check if a checkbox is selected
     * 
     * @param query CSS query or node ID
     * @return true if selected
     */
    protected boolean isCheckBoxSelected(String query) {
        CheckBox checkBox = lookup(query).query();
        return checkBox.isSelected();
    }

    /**
     * Select a checkbox
     * 
     * @param query CSS query or node ID
     */
    protected void selectCheckBox(String query) {
        CheckBox checkBox = lookup(query).query();
        if (!checkBox.isSelected()) {
            clickOnAndWait(query);
        }
    }

    /**
     * Deselect a checkbox
     * 
     * @param query CSS query or node ID
     */
    protected void deselectCheckBox(String query) {
        CheckBox checkBox = lookup(query).query();
        if (checkBox.isSelected()) {
            clickOnAndWait(query);
        }
    }

    /**
     * Select an item from a ComboBox
     * 
     * @param query CSS query or node ID
     * @param item  Item to select
     * @param <T>   Item type
     */
    protected <T> void selectComboBoxItem(String query, T item) {
        clickOn(query);
        waitForFxEvents();
        ComboBox<T> comboBox = lookup(query).query();
        interact(() -> comboBox.getSelectionModel().select(item));
        waitForFxEvents();
    }

    /**
     * Press Enter key and wait
     */
    protected void pressEnter() {
        push(KeyCode.ENTER);
        waitForFxEvents();
    }

    /**
     * Press Escape key and wait
     */
    protected void pressEscape() {
        push(KeyCode.ESCAPE);
        waitForFxEvents();
    }

    /**
     * Press Tab key and wait
     */
    protected void pressTab() {
        push(KeyCode.TAB);
        waitForFxEvents();
    }

    /**
     * Execute a task on the JavaFX Application Thread
     * 
     * @param runnable Task to execute
     */
    protected void runOnFxThread(Runnable runnable) {
        interact(runnable);
        waitForFxEvents();
    }

    /**
     * Get a test image file for testing image upload functionality
     * 
     * @return A test image file
     */
    protected java.io.File getTestImageFile() {
        try {
            // Create a test image in the temp directory
            java.io.File tempDir = new java.io.File(System.getProperty("java.io.tmpdir"));
            java.io.File testImage = new java.io.File(tempDir, "test_actor_image.png");
            
            // If test image doesn't exist, create a simple PNG file
            if (!testImage.exists()) {
                // Create a minimal valid PNG file (1x1 pixel)
                byte[] pngHeader = new byte[] {
                    (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                    0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                    0x08, 0x02, 0x00, 0x00, 0x00, (byte) 0x90, 0x77, 0x53,
                    (byte) 0xDE, 0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41,
                    0x54, 0x08, (byte) 0x99, 0x63, (byte) 0xF8, (byte) 0xCF,
                    (byte) 0xC0, 0x00, 0x00, 0x03, 0x01, 0x01, 0x00,
                    0x18, (byte) 0xDD, (byte) 0x8D, (byte) 0xB4, 0x00, 0x00,
                    0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, (byte) 0xAE,
                    0x42, 0x60, (byte) 0x82
                };
                
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(testImage)) {
                    fos.write(pngHeader);
                    fos.flush();
                }
                testImage.deleteOnExit();
            }
            
            return testImage;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test image file", e);
        }
    }
}
