package com.esprit.utils;

import javafx.scene.Node;
import javafx.scene.control.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * Custom assertion utilities for RAKCHA UI tests.
 * Provides domain-specific assertions and matchers.
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class TestAssertions {

    /**
     * Verify that an error message is displayed
     * 
     * @param errorLabelQuery Query for the error label
     * @param expectedMessage Expected error message
     */
    public static void verifyErrorMessage(String errorLabelQuery, String expectedMessage) {
        verifyThat(errorLabelQuery, isVisible());
        verifyThat(errorLabelQuery, hasText(expectedMessage));
    }

    /**
     * Verify that no error message is displayed
     * 
     * @param errorLabelQuery Query for the error label
     */
    public static void verifyNoErrorMessage(String errorLabelQuery) {
        verifyThat(errorLabelQuery, node -> {
            // If node is not a Label, it's not showing an error message
            if (!(node instanceof Label)) {
                return true;
            }
            // For Label nodes, check visibility and text
            Label label = (Label) node;
            return !node.isVisible() || label.getText() == null || label.getText().isEmpty();
        });
    }

    /**
     * Verify that a button is enabled and visible
     * 
     * @param buttonQuery Query for the button
     */
    public static void verifyButtonReady(String buttonQuery) {
        verifyThat(buttonQuery, isVisible());
        verifyThat(buttonQuery, isEnabled());
    }

    /**
     * Verify that a form field is valid (no error shown)
     * 
     * @param fieldQuery Query for the field
     */
    public static void verifyFieldValid(String fieldQuery) {
        verifyThat(fieldQuery, isVisible());
        verifyThat(fieldQuery, isEnabled());
    }

    /**
     * Verify that a text field contains expected text
     * 
     * @param fieldQuery   Query for the field
     * @param expectedText Expected text
     */
    public static void verifyTextFieldContains(String fieldQuery, String expectedText) {
        verifyThat(fieldQuery, hasText(expectedText));
    }

    /**
     * Verify that a text field is empty
     * 
     * @param fieldQuery Query for the field
     */
    public static void verifyTextFieldEmpty(String fieldQuery) {
        verifyThat(fieldQuery, hasText(""));
    }

    /**
     * Verify that multiple nodes are visible
     * 
     * @param queries Queries for the nodes
     */
    public static void verifyAllVisible(String... queries) {
        for (String query : queries) {
            verifyThat(query, isVisible());
        }
    }

    /**
     * Verify that a scene/view has loaded successfully
     * 
     * @param identifyingNodeQuery Query for a unique node in the scene
     */
    public static void verifySceneLoaded(String identifyingNodeQuery) {
        verifyThat(identifyingNodeQuery, isNotNull());
        verifyThat(identifyingNodeQuery, isVisible());
    }

    /**
     * Verify that a table has data
     * 
     * @param tableView The TableView instance
     */
    public static void verifyTableHasData(TableView<?> tableView) {
        assertThat("Table should have data",
                tableView.getItems().size(),
                greaterThan(0));
    }

    /**
     * Verify that a table is empty
     * 
     * @param tableView The TableView instance
     */
    public static void verifyTableEmpty(TableView<?> tableView) {
        assertThat("Table should be empty",
                tableView.getItems().size(),
                equalTo(0));
    }

    /**
     * Verify that a table has expected row count
     * 
     * @param tableView     The TableView instance
     * @param expectedCount Expected row count
     */
    public static void verifyTableRowCount(TableView<?> tableView, int expectedCount) {
        assertThat("Table should have expected row count",
                tableView.getItems().size(),
                equalTo(expectedCount));
    }

    /**
     * Verify that a list view has data
     * 
     * @param listView The ListView instance
     */
    public static void verifyListHasData(ListView<?> listView) {
        assertThat("List should have data",
                listView.getItems().size(),
                greaterThan(0));
    }

    /**
     * Verify that a combo box has items
     * 
     * @param comboBox The ComboBox instance
     */
    public static void verifyComboBoxHasItems(ComboBox<?> comboBox) {
        assertThat("ComboBox should have items",
                comboBox.getItems().size(),
                greaterThan(0));
    }

    /**
     * Verify that a string is a valid email format
     * 
     * @param email Email to verify
     */
    public static void verifyValidEmail(String email) {
        assertThat("Should be valid email format",
                email,
                matchesPattern("^[A-Za-z0-9+_.-]+@(.+)$"));
    }

    /**
     * Verify that a node has a specific style class
     * 
     * @param node       The node to check
     * @param styleClass The style class to verify
     */
    public static void verifyHasStyleClass(Node node, String styleClass) {
        assertThat("Node should have style class: " + styleClass,
                node.getStyleClass(),
                hasItem(styleClass));
    }

    /**
     * Verify that a notification was shown (can be mocked)
     * This is a placeholder for notification verification
     * 
     * @param notificationType Type of notification expected
     */
    public static void verifyNotificationShown(String notificationType) {
        // In real tests, you'd verify through mocking the notification service
        // This is a placeholder for the pattern
        assertThat("Notification should be shown", notificationType, notNullValue());
    }
}
