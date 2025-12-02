package com.esprit.controllers;

import com.esprit.controllers.common.CategoryManagementController;
import com.esprit.enums.CategoryType;
import com.esprit.models.users.Admin;
import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.User;
import com.esprit.utils.NavigationManager;
import com.esprit.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class SidebarController implements Initializable {

    // User data
    private User currentUser;

    // Common buttons
    @FXML
    private Button homeButton;
    @FXML
    private Button movieButton;
    @FXML
    private Button serieButton;
    @FXML
    private Button productButton;
    @FXML
    private Button cinemaButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;

    // Admin specific buttons
    @FXML
    private Button usersButton;
    @FXML
    private Button orderButton;
    @FXML
    private Button productCategoriesButton;
    @FXML
    private Button orderAnalyticsButton;
    @FXML
    private Button seriesStatisticsButton;
    @FXML
    private Button emailUsersButton;
    @FXML
    private Button smsUsersButton;

    // Cinema Manager specific buttons
    @FXML
    private Button actorButton;
    @FXML
    private Button filmCategorieButton;
    @FXML
    private Button moviesessionButton;
    @FXML
    private Button statestique_button;
    @FXML
    private Button seriesManagementButton;

    // Icons
    @FXML
    private FontIcon movieIcon;
    @FXML
    private FontIcon serieIcon;
    @FXML
    private FontIcon productIcon;
    @FXML
    private FontIcon cinemaIcon;
    @FXML
    private FontIcon actorIcon;
    @FXML
    private FontIcon filmCategorieIcon;
    @FXML
    private FontIcon moviesessionIcon;
    @FXML
    private FontIcon statisticsIcon;
    @FXML
    private FontIcon orderIcon;

    // Client specific buttons
    @FXML
    private Button favoritesButton;
    @FXML
    private Button shoppingCartButton;
    @FXML
    private Button myOrdersButton;
    @FXML
    private Button watchlistButton;
    @FXML
    private Button ticketsButton;

    @FXML
    private WebView webView;

    /**
     * Sets the current user and configures the sidebar based on user role.
     *
     * @param user the current user (Admin, CinemaManager, or Client)
     */
    public void setCurrentUser(User user) {
        if (user == null) {
            log.warn("Attempted to set null user");
            return;
        }
        this.currentUser = user;
        if (user instanceof Admin) {
            configureForAdmin();
        } else if (user instanceof CinemaManager) {
            configureForCinemaManager();
        } else {
            configureForClient();
        }
    }

    /**
     * Set the current user to the provided Admin and configure the sidebar for the
     * admin role.
     *
     * @param admin the Admin to use as the current user; triggers admin-specific
     *              sidebar configuration
     */
    public void setData(Admin admin) {
        this.currentUser = admin;
        configureForAdmin();
    }

    /**
     * Sets the current user to the provided CinemaManager and configures the
     * sidebar for the cinema manager role.
     *
     * @param cinemaManager the CinemaManager to set as the current user
     */
    public void setData(CinemaManager cinemaManager) {
        this.currentUser = cinemaManager;
        configureForCinemaManager();
    }

    /**
     * Adjusts sidebar control visibility for an Admin user.
     * <p>
     * Shows admin-specific navigation controls, hides cinema-manager specific
     * controls, and ensures common navigation buttons are visible.
     */
    private void configureForAdmin() {
        // Show admin-specific buttons
        if (usersButton != null)
            usersButton.setVisible(true);
        if (orderButton != null)
            orderButton.setVisible(true);
        if (orderIcon != null)
            orderIcon.setVisible(true);
        if (productCategoriesButton != null)
            productCategoriesButton.setVisible(true);
        if (orderAnalyticsButton != null)
            orderAnalyticsButton.setVisible(true);
        if (seriesStatisticsButton != null)
            seriesStatisticsButton.setVisible(true);
        if (emailUsersButton != null)
            emailUsersButton.setVisible(true);
        if (smsUsersButton != null)
            smsUsersButton.setVisible(true);

        // Hide cinema manager specific buttons
        if (actorButton != null)
            actorButton.setVisible(false);
        if (filmCategorieButton != null)
            filmCategorieButton.setVisible(false);
        if (moviesessionButton != null)
            moviesessionButton.setVisible(false);
        if (statestique_button != null)
            statestique_button.setVisible(false);
        if (actorIcon != null)
            actorIcon.setVisible(false);
        if (filmCategorieIcon != null)
            filmCategorieIcon.setVisible(false);
        if (moviesessionIcon != null)
            moviesessionIcon.setVisible(false);
        if (statisticsIcon != null)
            statisticsIcon.setVisible(false);
        if (seriesManagementButton != null)
            seriesManagementButton.setVisible(false);

        // Hide client specific buttons
        if (favoritesButton != null)
            favoritesButton.setVisible(false);
        if (shoppingCartButton != null)
            shoppingCartButton.setVisible(false);
        if (myOrdersButton != null)
            myOrdersButton.setVisible(false);
        if (watchlistButton != null)
            watchlistButton.setVisible(false);
        if (ticketsButton != null)
            ticketsButton.setVisible(false);

        // Show common buttons
        if (movieButton != null)
            movieButton.setVisible(true);
        if (serieButton != null)
            serieButton.setVisible(true);
        if (productButton != null)
            productButton.setVisible(true);
        if (cinemaButton != null)
            cinemaButton.setVisible(true);
    }

    /**
     * Configure sidebar visibility for a user with the Cinema Manager role.
     * <p>
     * Shows cinema-manager-specific and common controls while hiding admin-only
     * controls.
     */
    private void configureForCinemaManager() {
        // Hide admin-specific buttons
        if (usersButton != null)
            usersButton.setVisible(false);
        if (orderButton != null)
            orderButton.setVisible(false);
        if (orderIcon != null)
            orderIcon.setVisible(false);
        if (productCategoriesButton != null)
            productCategoriesButton.setVisible(false);
        if (orderAnalyticsButton != null)
            orderAnalyticsButton.setVisible(false);
        if (seriesStatisticsButton != null)
            seriesStatisticsButton.setVisible(false);
        if (emailUsersButton != null)
            emailUsersButton.setVisible(false);
        if (smsUsersButton != null)
            smsUsersButton.setVisible(false);

        // Show cinema manager specific buttons
        if (actorButton != null)
            actorButton.setVisible(true);
        if (filmCategorieButton != null)
            filmCategorieButton.setVisible(true);
        if (moviesessionButton != null)
            moviesessionButton.setVisible(true);
        if (statestique_button != null)
            statestique_button.setVisible(true);
        if (actorIcon != null)
            actorIcon.setVisible(true);
        if (filmCategorieIcon != null)
            filmCategorieIcon.setVisible(true);
        if (moviesessionIcon != null)
            moviesessionIcon.setVisible(true);
        if (statisticsIcon != null)
            statisticsIcon.setVisible(true);
        if (seriesManagementButton != null)
            seriesManagementButton.setVisible(true);

        // Hide client specific buttons
        if (favoritesButton != null)
            favoritesButton.setVisible(false);
        if (shoppingCartButton != null)
            shoppingCartButton.setVisible(false);
        if (myOrdersButton != null)
            myOrdersButton.setVisible(false);
        if (watchlistButton != null)
            watchlistButton.setVisible(false);
        if (ticketsButton != null)
            ticketsButton.setVisible(false);

        // Show common buttons
        if (movieButton != null)
            movieButton.setVisible(true);
        if (serieButton != null)
            serieButton.setVisible(true);
        if (productButton != null)
            productButton.setVisible(false); // Cinema managers don't see products
        if (cinemaButton != null)
            cinemaButton.setVisible(true);
    }

    /**
     * Configure the sidebar for a client user.
     * <p>
     * Hides admin-only and cinema-manager-only controls (users, orders, actor, film
     * category,
     * movie sessions, statistics, and their icons) and shows common navigation
     * buttons
     * (movies, series, products, cinemas).
     */
    private void configureForClient() {
        // Hide admin-specific buttons
        if (usersButton != null)
            usersButton.setVisible(false);
        if (orderButton != null)
            orderButton.setVisible(false);
        if (orderIcon != null)
            orderIcon.setVisible(false);
        if (productCategoriesButton != null)
            productCategoriesButton.setVisible(false);
        if (orderAnalyticsButton != null)
            orderAnalyticsButton.setVisible(false);
        if (seriesStatisticsButton != null)
            seriesStatisticsButton.setVisible(false);
        if (emailUsersButton != null)
            emailUsersButton.setVisible(false);
        if (smsUsersButton != null)
            smsUsersButton.setVisible(false);

        // Hide cinema manager specific buttons
        if (actorButton != null)
            actorButton.setVisible(false);
        if (filmCategorieButton != null)
            filmCategorieButton.setVisible(false);
        if (moviesessionButton != null)
            moviesessionButton.setVisible(false);
        if (statestique_button != null)
            statestique_button.setVisible(false);
        if (actorIcon != null)
            actorIcon.setVisible(false);
        if (filmCategorieIcon != null)
            filmCategorieIcon.setVisible(false);
        if (moviesessionIcon != null)
            moviesessionIcon.setVisible(false);
        if (statisticsIcon != null)
            statisticsIcon.setVisible(false);
        if (seriesManagementButton != null)
            seriesManagementButton.setVisible(false);

        // Show client specific buttons
        if (favoritesButton != null)
            favoritesButton.setVisible(true);
        if (shoppingCartButton != null)
            shoppingCartButton.setVisible(true);
        if (myOrdersButton != null)
            myOrdersButton.setVisible(true);
        if (watchlistButton != null)
            watchlistButton.setVisible(true);
        if (ticketsButton != null)
            ticketsButton.setVisible(true);

        // Show common buttons
        if (movieButton != null)
            movieButton.setVisible(true);
        if (serieButton != null)
            serieButton.setVisible(true);
        if (productButton != null)
            productButton.setVisible(true);
        if (cinemaButton != null)
            cinemaButton.setVisible(true);
    }

    // Navigation methods

    /**
     * Switches to the users management view (Admin only).
     */
    @FXML
    public void switchToUsers(final ActionEvent event) {
        Stage stage = (Stage) this.usersButton.getScene().getWindow();
        NavigationManager.navigateToUsers(stage);
    }

    /**
     * Switches to the events view.
     */
    @FXML
    void switchToEvent(final ActionEvent event) {
        // Implementation depends on role
    }

    /**
     * Navigate to the movies view appropriate for the current user's role.
     */
    @FXML
    void switchToMovies(final ActionEvent event) {
        if (currentUser == null) {
            log.warn("Cannot navigate to movies: current user is null");
            return;
        }
        Stage stage = (Stage) this.movieButton.getScene().getWindow();
        NavigationManager.navigateToMovies(stage, currentUser);
    }

    /**
     * Switches the current scene to the orders view for administrators.
     */
    @FXML
    void switchToOrders(final ActionEvent event) {
        Stage stage = (Stage) this.orderButton.getScene().getWindow();
        NavigationManager.navigateToOrders(stage);
    }

    /**
     * Open the products view appropriate for the current user's role.
     */
    @FXML
    void switchToProducts(final ActionEvent event) {
        if (currentUser == null) {
            log.warn("Cannot navigate to products: current user is null");
            return;
        }
        Stage stage = (Stage) this.productButton.getScene().getWindow();
        NavigationManager.navigateToProducts(stage, currentUser);
    }

    /**
     * Switches the application's scene to the role-appropriate series view.
     */
    @FXML
    void switchToSeries(final ActionEvent event) {
        if (currentUser == null) {
            log.warn("Cannot navigate to series: current user is null");
            return;
        }
        Stage stage = (Stage) this.serieButton.getScene().getWindow();
        NavigationManager.navigateToSeries(stage, currentUser);
    }

    /**
     * Navigate to the cinema dashboard appropriate for the current user's role.
     */
    @FXML
    void switchToCinema(final ActionEvent event) {
        if (currentUser == null) {
            log.warn("Cannot navigate to cinema: current user is null");
            return;
        }
        Stage stage = (Stage) this.cinemaButton.getScene().getWindow();
        NavigationManager.navigateToCinema(stage, currentUser);
    }

    /**
     * Switches the UI to the actors view intended for cinema managers.
     */
    @FXML
    void switchToActor(final ActionEvent event) {
        Stage stage = (Stage) this.actorButton.getScene().getWindow();
        NavigationManager.navigate(stage, "/ui/films/InterfaceActor.fxml");
    }

    /**
     * Navigate the application to the unified category management view.
     */
    @FXML
    void switchToFilmCategorie(final ActionEvent event) {
        navigateToCategoryManagement(CategoryType.MOVIE);
    }

    /**
     * Navigate to the unified category management screen with optional type filter.
     */
    private void navigateToCategoryManagement(CategoryType initialType) {
        try {
            final FXMLLoader loader = new FXMLLoader(
                this.getClass().getResource("/ui/common/CategoryManagement.fxml"));
            final Parent root = loader.load();

            // Set initial type filter if specified
            CategoryManagementController controller = loader.getController();
            if (controller != null && initialType != null) {
                controller.setInitialType(initialType);
            }

            final Stage stage = (Stage) (filmCategorieButton != null ?
                filmCategorieButton.getScene().getWindow() :
                productCategoriesButton.getScene().getWindow());
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            log.error("Error navigating to category management: {}", e.getMessage(), e);
        }
    }

    /**
     * Navigate to the movie sessions dashboard for cinema managers.
     */
    @FXML
    void switchToMovieSessions(final ActionEvent event) {
        Stage stage = (Stage) this.moviesessionButton.getScene().getWindow();
        NavigationManager.navigate(stage, "/ui/cinemas/DashboardResponsableCinema.fxml");
    }

    /**
     * Navigate to the cinema statistics view.
     */
    @FXML
    void switchToStatistics(final ActionEvent event) {
        Stage stage = (Stage) this.statestique_button.getScene().getWindow();
        NavigationManager.navigate(stage, "/ui/cinemas/statistiques.fxml");
    }

    /**
     * Handles navigation to home page based on user role.
     */
    @FXML
    void switchToHome(final ActionEvent event) {
        if (currentUser == null) {
            log.warn("Cannot navigate to home: current user is null");
            return;
        }
        Stage stage = (Stage) this.homeButton.getScene().getWindow();
        NavigationManager.navigateToHome(stage, currentUser);
    }

    /**
     * Navigate the application window to the user's profile view.
     */
    @FXML
    void switchToProfile(final ActionEvent event) {
        if (currentUser == null) {
            log.warn("Cannot navigate to profile: current user is null");
            return;
        }
        Stage stage = (Stage) this.profileButton.getScene().getWindow();
        NavigationManager.navigateToProfile(stage, currentUser);
    }

    /**
     * Clears the current stage's user data and replaces the window scene with the
     * login screen.
     */
    @FXML
    void switchToLogout(final ActionEvent event) {
        Stage stage = (Stage) this.logoutButton.getScene().getWindow();
        NavigationManager.navigateToLogin(stage);
    }

    // ============= ADMIN SPECIFIC NAVIGATION METHODS =============

    /**
     * Switches to unified category management (Admin only - shows all types including PRODUCT).
     */
    @FXML
    void switchToProductCategories(final ActionEvent event) {
        Stage stage = (Stage) this.productCategoriesButton.getScene().getWindow();
        NavigationManager.navigateToCategories(stage);
    }

    /**
     * Switches to order analytics view (Admin only).
     */
    @FXML
    void switchToOrderAnalytics(final ActionEvent event) {
        Stage stage = (Stage) this.orderAnalyticsButton.getScene().getWindow();
        NavigationManager.navigateToOrderAnalytics(stage);
    }

    /**
     * Switches to series statistics view (Admin only).
     */
    @FXML
    void switchToSeriesStatistics(final ActionEvent event) {
        Stage stage = (Stage) this.seriesStatisticsButton.getScene().getWindow();
        NavigationManager.navigateToSeriesStatistics(stage);
    }

    /**
     * Switches to email composer (Admin only).
     */
    @FXML
    void switchToEmailUsers(final ActionEvent event) {
        Stage stage = (Stage) this.emailUsersButton.getScene().getWindow();
        NavigationManager.navigateToEmailComposer(stage);
    }

    /**
     * Switches to SMS sender (Admin only).
     */
    @FXML
    void switchToSMSUsers(final ActionEvent event) {
        Stage stage = (Stage) this.smsUsersButton.getScene().getWindow();
        NavigationManager.navigateToSMSComposer(stage);
    }

    // ============= CINEMA MANAGER SPECIFIC NAVIGATION METHODS =============

    /**
     * Switches to series management (Cinema Manager only).
     */
    @FXML
    void switchToSeriesManagement(final ActionEvent event) {
        Stage stage = (Stage) this.seriesManagementButton.getScene().getWindow();
        NavigationManager.navigateToSeries(stage, currentUser);
    }

    // ============= CLIENT SPECIFIC NAVIGATION METHODS =============

    /**
     * Switches to favorites list (Client only).
     */
    @FXML
    void switchToFavorites(final ActionEvent event) {
        Stage stage = (Stage) this.favoritesButton.getScene().getWindow();
        NavigationManager.navigateToFavorites(stage);
    }

    /**
     * Switches to shopping cart (Client only).
     */
    @FXML
    void switchToShoppingCart(final ActionEvent event) {
        Stage stage = (Stage) this.shoppingCartButton.getScene().getWindow();
        NavigationManager.navigateToShoppingCart(stage);
    }

    /**
     * Switches to client's order history (Client only).
     */
    @FXML
    void switchToMyOrders(final ActionEvent event) {
        Stage stage = (Stage) this.myOrdersButton.getScene().getWindow();
        NavigationManager.navigateToMyOrders(stage);
    }

    /**
     * Switches to Watchlist (Client only).
     */
    @FXML
    void switchToWatchlist(final ActionEvent event) {
        Stage stage = (Stage) this.watchlistButton.getScene().getWindow();
        NavigationManager.navigateToWatchlist(stage);
    }

    /**
     * Switches to Ticket History (Client only).
     */
    @FXML
    void switchToTickets(final ActionEvent event) {
        Stage stage = (Stage) this.ticketsButton.getScene().getWindow();
        NavigationManager.navigateToTicketHistory(stage);
    }

    /**
     * Invoked after FXML loading; applies the default client sidebar configuration.
     *
     * @param location  the location used to resolve relative paths for the root
     *                  object, or null
     * @param resources the resources used to localize the root object, or null
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // Don't configure until user is set to avoid NullPointerException
        // Configuration will happen when setCurrentUser() is called
        setCurrentUser(SessionManager.getCurrentUser());
        log.debug("SidebarController initialized, awaiting user configuration");
    }

}
