package com.esprit.utils;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import lombok.extern.slf4j.Slf4j;

/**
 * Manager for breadcrumb navigation display and management.
 * Provides methods to build, display, and clear breadcrumb trails.
 * Helps users understand their current location in the application.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class BreadcrumbManager {

    private static final String SEPARATOR = " / ";
    private static final String BREADCRUMB_BUTTON_STYLE = "-fx-background-color: transparent; -fx-text-fill: #0078d4; -fx-underline: false; -fx-font-size: 12;";
    private static final String BREADCRUMB_SEPARATOR_STYLE = "-fx-text-fill: #666; -fx-font-size: 12;";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private BreadcrumbManager() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Builds a breadcrumb trail for the given paths.
     *
     * @param breadcrumbContainer the HBox to populate with breadcrumbs
     * @param breadcrumbs         array of breadcrumb items (name, action pairs)
     */
    public static void buildBreadcrumbs(HBox breadcrumbContainer, BreadcrumbItem... breadcrumbs) {
        if (breadcrumbContainer == null || breadcrumbs == null || breadcrumbs.length == 0) {
            return;
        }

        breadcrumbContainer.getChildren().clear();
        breadcrumbContainer.setSpacing(0);
        breadcrumbContainer.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < breadcrumbs.length; i++) {
            BreadcrumbItem item = breadcrumbs[i];

            // Add breadcrumb button
            Button breadcrumbButton = new Button(item.getName());
            breadcrumbButton.setStyle(BREADCRUMB_BUTTON_STYLE);
            breadcrumbButton.setFont(new Font(12));
            breadcrumbButton.setCursor(javafx.scene.Cursor.HAND);

            if (item.getAction() != null) {
                breadcrumbButton.setOnAction(event -> item.getAction().run());
            }

            breadcrumbContainer.getChildren().add(breadcrumbButton);

            // Add separator if not last item
            if (i < breadcrumbs.length - 1) {
                Label separator = new Label(SEPARATOR);
                separator.setStyle(BREADCRUMB_SEPARATOR_STYLE);
                separator.setFont(new Font(12));
                breadcrumbContainer.getChildren().add(separator);
            }
        }
    }

    /**
     * Builds a simple breadcrumb trail from string paths.
     *
     * @param breadcrumbContainer the HBox to populate
     * @param paths               the breadcrumb path strings
     */
    public static void buildSimpleBreadcrumbs(HBox breadcrumbContainer, String... paths) {
        if (breadcrumbContainer == null || paths == null || paths.length == 0) {
            return;
        }

        breadcrumbContainer.getChildren().clear();
        breadcrumbContainer.setSpacing(0);
        breadcrumbContainer.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < paths.length; i++) {
            Label label = new Label(paths[i]);
            label.setStyle(i == paths.length - 1 ? "-fx-text-fill: #333; -fx-font-size: 12;" : "-fx-text-fill: #0078d4; -fx-font-size: 12;");
            label.setFont(new Font(12));
            breadcrumbContainer.getChildren().add(label);

            if (i < paths.length - 1) {
                Label separator = new Label(SEPARATOR);
                separator.setStyle(BREADCRUMB_SEPARATOR_STYLE);
                separator.setFont(new Font(12));
                breadcrumbContainer.getChildren().add(separator);
            }
        }
    }

    /**
     * Clears all breadcrumbs from the container.
     *
     * @param breadcrumbContainer the HBox to clear
     */
    public static void clearBreadcrumbs(HBox breadcrumbContainer) {
        if (breadcrumbContainer != null) {
            breadcrumbContainer.getChildren().clear();
        }
    }

    /**
     * Adds a single breadcrumb item to the container.
     *
     * @param breadcrumbContainer the HBox to add to
     * @param name                the display name
     * @param action              the action to perform when clicked, or null
     */
    public static void addBreadcrumb(HBox breadcrumbContainer, String name, Runnable action) {
        if (breadcrumbContainer == null) {
            return;
        }

        // Add separator if not first item
        if (!breadcrumbContainer.getChildren().isEmpty()) {
            Label separator = new Label(SEPARATOR);
            separator.setStyle(BREADCRUMB_SEPARATOR_STYLE);
            separator.setFont(new Font(12));
            breadcrumbContainer.getChildren().add(separator);
        }

        Button breadcrumbButton = new Button(name);
        breadcrumbButton.setStyle(BREADCRUMB_BUTTON_STYLE);
        breadcrumbButton.setFont(new Font(12));
        breadcrumbButton.setCursor(javafx.scene.Cursor.HAND);

        if (action != null) {
            breadcrumbButton.setOnAction(event -> action.run());
        }

        breadcrumbContainer.getChildren().add(breadcrumbButton);
    }

    /**
     * Represents a single breadcrumb item with name and optional action.
     */
    public static class BreadcrumbItem {

        private final String name;
        private final Runnable action;

        /**
         * Creates a breadcrumb item.
         *
         * @param name   the display name
         * @param action the action to run when clicked, or null
         */
        public BreadcrumbItem(String name, Runnable action) {
            this.name = name;
            this.action = action;
        }

        /**
         * Gets the breadcrumb name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the breadcrumb action.
         *
         * @return the action or null
         */
        public Runnable getAction() {
            return action;
        }
    }
}
