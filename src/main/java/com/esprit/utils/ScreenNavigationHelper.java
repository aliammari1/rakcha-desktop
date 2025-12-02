package com.esprit.utils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.Stack;

/**
 * Base class for screen controllers that need navigation support.
 * Provides back button functionality, breadcrumb tracking, and navigation helpers.
 * Controllers extending this class can easily implement back navigation and breadcrumb trails.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class ScreenNavigationHelper {

    // Navigation history stack
    private static final Stack<NavigationState> navigationStack = new Stack<>();
    protected Stage stage;
    protected com.esprit.models.users.User currentUser;
    // Optional breadcrumb/header elements
    @FXML
    protected Label screenTitleLabel;
    @FXML
    protected Button backButton;
    @FXML
    protected HBox breadcrumbContainer;

    /**
     * Pushes a navigation state to the stack.
     *
     * @param screenName     the display name of the screen
     * @param controllerName the controller class name
     */
    private static void pushNavigationState(String screenName, String controllerName) {
        navigationStack.push(new NavigationState(screenName, controllerName));
    }

    /**
     * Gets the current navigation stack size (for testing/debugging).
     *
     * @return the size of the navigation stack
     */
    public static int getNavigationStackSize() {
        return navigationStack.size();
    }

    /**
     * Clears the navigation stack.
     */
    public static void clearNavigationStack() {
        navigationStack.clear();
    }

    /**
     * Initialize the screen with stage and user information.
     * Call this in your controller's initialize method.
     *
     * @param stage       the current Stage
     * @param currentUser the currently logged-in user
     * @param screenTitle the title to display for this screen
     */
    protected void initializeScreenNavigation(Stage stage, com.esprit.models.users.User currentUser, String screenTitle) {
        this.stage = stage;
        this.currentUser = currentUser;

        if (screenTitleLabel != null) {
            screenTitleLabel.setText(screenTitle);
        }

        if (backButton != null) {
            backButton.setOnAction(event -> handleBackNavigation());
        }

        // Track this screen in navigation history
        pushNavigationState(screenTitle, stage.getScene().getRoot().getClass().getSimpleName());
    }

    /**
     * Handles back button click - pops current screen and navigates to previous.
     */
    protected void handleBackNavigation() {
        if (!navigationStack.isEmpty()) {
            navigationStack.pop(); // Remove current screen

            if (!navigationStack.isEmpty()) {
                NavigationState previousState = navigationStack.peek();
                navigateTo(previousState.screenName, previousState.controllerName);
            } else {
                // Default to home if no history
                navigateToHome();
            }
        } else {
            navigateToHome();
        }
    }

    /**
     * Navigates to a screen by name.
     *
     * @param screenName the display name of the screen
     * @param fxmlPath   the FXML path to load
     */
    protected void navigateTo(String screenName, String fxmlPath) {
        if (NavigationManager.navigate(stage, fxmlPath)) {
            pushNavigationState(screenName, fxmlPath);
        } else {
            log.error("Failed to navigate to: " + screenName);
        }
    }

    /**
     * Navigates to home screen based on user role.
     */
    protected void navigateToHome() {
        if (currentUser != null) {
            NavigationManager.navigateToHome(stage, currentUser);
        }
    }

    /**
     * Navigates to profile.
     */
    protected void navigateToProfile() {
        NavigationManager.navigateToProfile(stage, currentUser);
    }

    /**
     * Navigates to dashboard.
     */
    protected void navigateToDashboard() {
        NavigationManager.navigateToDashboard(stage, currentUser);
    }

    /**
     * Navigates to movies.
     */
    protected void navigateToMovies() {
        NavigationManager.navigateToMovies(stage, currentUser);
    }

    /**
     * Navigates to series.
     */
    protected void navigateToSeries() {
        NavigationManager.navigateToSeries(stage, currentUser);
    }

    /**
     * Navigates to products.
     */
    protected void navigateToProducts() {
        NavigationManager.navigateToProducts(stage, currentUser);
    }

    /**
     * Navigates to cinema.
     */
    protected void navigateToCinema() {
        NavigationManager.navigateToCinema(stage, currentUser);
    }

    /**
     * Navigates to shopping cart.
     */
    protected void navigateToShoppingCart() {
        NavigationManager.navigateToShoppingCart(stage);
    }

    /**
     * Navigates to favorites.
     */
    protected void navigateToFavorites() {
        NavigationManager.navigateToFavorites(stage);
    }

    /**
     * Navigates to my orders.
     */
    protected void navigateToMyOrders() {
        NavigationManager.navigateToMyOrders(stage);
    }

    /**
     * Navigates to login and clears session.
     */
    protected void navigateToLogout() {
        navigationStack.clear(); // Clear history on logout
        NavigationManager.navigateToLogin(stage);
    }

    /**
     * Inner class to track navigation state.
     */
    private static class NavigationState {

        String screenName;
        String controllerName;

        NavigationState(String screenName, String controllerName) {
            this.screenName = screenName;
            this.controllerName = controllerName;
        }
    }
}
