package com.esprit.utils;

import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * Quick navigation helper for simple one-click navigation scenarios.
 * Provides convenient methods for setting up navigation on UI elements.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class QuickNavigationHelper {

    /**
     * Private constructor to prevent instantiation.
     */
    private QuickNavigationHelper() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Attaches navigation to a button.
     *
     * @param button   the button to attach navigation to
     * @param stage    the Stage to navigate on
     * @param fxmlPath the FXML path to navigate to
     */
    public static void setButtonNavigation(Button button, Stage stage, String fxmlPath) {
        if (button == null || stage == null || fxmlPath == null) {
            log.warn("Cannot set button navigation: null parameter provided");
            return;
        }
        button.setOnAction(event -> NavigationManager.navigate(stage, fxmlPath));
    }

    /**
     * Attaches role-based home navigation to a button.
     *
     * @param button the button to attach navigation to
     * @param stage  the Stage to navigate on
     * @param user   the current user
     */
    public static void setHomeNavigation(Button button, Stage stage, com.esprit.models.users.User user) {
        if (button == null || stage == null || user == null) {
            log.warn("Cannot set home navigation: null parameter provided");
            return;
        }
        button.setOnAction(event -> NavigationManager.navigateToHome(stage, user));
    }

    /**
     * Attaches dashboard navigation to a button.
     *
     * @param button the button to attach navigation to
     * @param stage  the Stage to navigate on
     * @param user   the current user
     */
    public static void setDashboardNavigation(Button button, Stage stage, com.esprit.models.users.User user) {
        if (button == null || stage == null || user == null) {
            log.warn("Cannot set dashboard navigation: null parameter provided");
            return;
        }
        button.setOnAction(event -> NavigationManager.navigateToDashboard(stage, user));
    }

    /**
     * Attaches profile navigation to a button.
     *
     * @param button the button to attach navigation to
     * @param stage  the Stage to navigate on
     * @param user   the current user
     */
    public static void setProfileNavigation(Button button, Stage stage, com.esprit.models.users.User user) {
        if (button == null || stage == null || user == null) {
            log.warn("Cannot set profile navigation: null parameter provided");
            return;
        }
        button.setOnAction(event -> NavigationManager.navigateToProfile(stage, user));
    }

    /**
     * Attaches movie navigation to a button.
     *
     * @param button the button to attach navigation to
     * @param stage  the Stage to navigate on
     * @param user   the current user
     */
    public static void setMovieNavigation(Button button, Stage stage, com.esprit.models.users.User user) {
        if (button == null || stage == null || user == null) {
            log.warn("Cannot set movie navigation: null parameter provided");
            return;
        }
        button.setOnAction(event -> NavigationManager.navigateToMovies(stage, user));
    }

    /**
     * Attaches series navigation to a button.
     *
     * @param button the button to attach navigation to
     * @param stage  the Stage to navigate on
     * @param user   the current user
     */
    public static void setSeriesNavigation(Button button, Stage stage, com.esprit.models.users.User user) {
        if (button == null || stage == null || user == null) {
            log.warn("Cannot set series navigation: null parameter provided");
            return;
        }
        button.setOnAction(event -> NavigationManager.navigateToSeries(stage, user));
    }

    /**
     * Attaches products navigation to a button.
     *
     * @param button the button to attach navigation to
     * @param stage  the Stage to navigate on
     * @param user   the current user
     */
    public static void setProductsNavigation(Button button, Stage stage, com.esprit.models.users.User user) {
        if (button == null || stage == null || user == null) {
            log.warn("Cannot set products navigation: null parameter provided");
            return;
        }
        button.setOnAction(event -> NavigationManager.navigateToProducts(stage, user));
    }

    /**
     * Attaches cinema navigation to a button.
     *
     * @param button the button to attach navigation to
     * @param stage  the Stage to navigate on
     * @param user   the current user
     */
    public static void setCinemaNavigation(Button button, Stage stage, com.esprit.models.users.User user) {
        if (button == null || stage == null || user == null) {
            log.warn("Cannot set cinema navigation: null parameter provided");
            return;
        }
        button.setOnAction(event -> NavigationManager.navigateToCinema(stage, user));
    }

    /**
     * Attaches shopping cart navigation to a button.
     *
     * @param button the button to attach navigation to
     * @param stage  the Stage to navigate on
     */
    public static void setShoppingCartNavigation(Button button, Stage stage) {
        if (button == null || stage == null) {
            log.warn("Cannot set shopping cart navigation: null parameter provided");
            return;
        }
        button.setOnAction(event -> NavigationManager.navigateToShoppingCart(stage));
    }

    /**
     * Attaches favorites navigation to a button.
     *
     * @param button the button to attach navigation to
     * @param stage  the Stage to navigate on
     */
    public static void setFavoritesNavigation(Button button, Stage stage) {
        if (button == null || stage == null) {
            log.warn("Cannot set favorites navigation: null parameter provided");
            return;
        }
        button.setOnAction(event -> NavigationManager.navigateToFavorites(stage));
    }

    /**
     * Attaches my orders navigation to a button.
     *
     * @param button the button to attach navigation to
     * @param stage  the Stage to navigate on
     */
    public static void setMyOrdersNavigation(Button button, Stage stage) {
        if (button == null || stage == null) {
            log.warn("Cannot set my orders navigation: null parameter provided");
            return;
        }
        button.setOnAction(event -> NavigationManager.navigateToMyOrders(stage));
    }

    /**
     * Attaches logout navigation to a button.
     *
     * @param button the button to attach navigation to
     * @param stage  the Stage to navigate on
     */
    public static void setLogoutNavigation(Button button, Stage stage) {
        if (button == null || stage == null) {
            log.warn("Cannot set logout navigation: null parameter provided");
            return;
        }
        button.setOnAction(event -> NavigationManager.navigateToLogin(stage));
    }

    /**
     * Attaches navigation to a hyperlink.
     *
     * @param link     the hyperlink to attach navigation to
     * @param stage    the Stage to navigate on
     * @param fxmlPath the FXML path to navigate to
     */
    public static void setHyperlinkNavigation(Hyperlink link, Stage stage, String fxmlPath) {
        if (link == null || stage == null || fxmlPath == null) {
            log.warn("Cannot set hyperlink navigation: null parameter provided");
            return;
        }
        link.setOnAction(event -> NavigationManager.navigate(stage, fxmlPath));
    }

    /**
     * Attaches navigation to a menu item.
     *
     * @param menuItem the menu item to attach navigation to
     * @param stage    the Stage to navigate on
     * @param fxmlPath the FXML path to navigate to
     */
    public static void setMenuItemNavigation(MenuItem menuItem, Stage stage, String fxmlPath) {
        if (menuItem == null || stage == null || fxmlPath == null) {
            log.warn("Cannot set menu item navigation: null parameter provided");
            return;
        }
        menuItem.setOnAction(event -> NavigationManager.navigate(stage, fxmlPath));
    }

    /**
     * Creates a navigation action that can be used with buttons, hyperlinks, etc.
     *
     * @param stage    the Stage to navigate on
     * @param fxmlPath the FXML path to navigate to
     * @return a Runnable that performs the navigation
     */
    public static Runnable createNavigationAction(Stage stage, String fxmlPath) {
        return () -> NavigationManager.navigate(stage, fxmlPath);
    }

    /**
     * Creates a home navigation action for a user.
     *
     * @param stage the Stage to navigate on
     * @param user  the current user
     * @return a Runnable that performs the navigation
     */
    public static Runnable createHomeNavigationAction(Stage stage, com.esprit.models.users.User user) {
        return () -> NavigationManager.navigateToHome(stage, user);
    }

    /**
     * Creates a logout navigation action.
     *
     * @param stage the Stage to navigate on
     * @return a Runnable that performs the navigation
     */
    public static Runnable createLogoutNavigationAction(Stage stage) {
        return () -> NavigationManager.navigateToLogin(stage);
    }
}
