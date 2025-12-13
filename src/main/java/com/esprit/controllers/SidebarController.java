package com.esprit.controllers;

import com.esprit.controllers.common.CategoryManagementController;
import com.esprit.enums.CategoryType;
import com.esprit.models.users.Admin;
import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.User;
import com.esprit.utils.NavigationManager;
import com.esprit.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the application sidebar navigation.
 * Dynamically creates and manages navigation buttons based on user role.
 */
@Log4j2
public class SidebarController implements Initializable {

    // User data
    private User currentUser;

    // Container references from FXML
    @FXML
    private VBox navigationContainer;
    @FXML
    private VBox bottomButtonsContainer;
    @FXML
    private ScrollPane navigationScrollPane;

    // Button references for navigation actions
    private Button homeButton;
    private Button movieButton;
    private Button serieButton;
    private Button productButton;
    private Button cinemaButton;
    private Button profileButton;
    private Button logoutButton;

    // Admin specific buttons
    private Button usersButton;
    private Button orderButton;
    private Button productCategoriesButton;
    private Button orderAnalyticsButton;
    private Button seriesStatisticsButton;
    private Button emailUsersButton;
    private Button smsUsersButton;

    // Cinema Manager specific buttons
    private Button actorButton;
    private Button filmCategorieButton;
    private Button moviesessionButton;
    private Button statestique_button;
    private Button seriesManagementButton;

    // Client specific buttons
    private Button favoritesButton;
    private Button shoppingCartButton;
    private Button myOrdersButton;
    private Button watchlistButton;
    private Button ticketsButton;

    // Styles
    private static final String NAV_BUTTON_STYLE = "-fx-background-color: linear-gradient(to right, rgba(139, 0, 0, 0.3), rgba(180, 0, 0, 0.2));"
            +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #8b0000;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 12 20 12 20;";

    private static final String NAV_BUTTON_ACTIVE_STYLE = "-fx-background-color: linear-gradient(to right, rgba(180, 0, 0, 0.4), rgba(220, 0, 0, 0.3));"
            +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #bb0000;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 12 20 12 20;";

    private static final String BOTTOM_BUTTON_STYLE = "-fx-background-color: linear-gradient(to right, rgba(80, 80, 80, 0.3), rgba(120, 120, 120, 0.2));"
            +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #666666;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 20 10 20;";

    private static final String SECTION_LABEL_STYLE = "-fx-text-fill: #888888;" +
            "-fx-font-family: 'Arial';" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;";

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
        buildNavigationMenu();
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
        buildNavigationMenu();
    }

    /**
     * Sets the current user to the provided CinemaManager and configures the
     * sidebar for the cinema manager role.
     *
     * @param cinemaManager the CinemaManager to set as the current user
     */
    public void setData(CinemaManager cinemaManager) {
        this.currentUser = cinemaManager;
        buildNavigationMenu();
    }

    /**
     * Clears and rebuilds the navigation menu based on the current user's role.
     */
    private void buildNavigationMenu() {
        // Clear existing buttons
        navigationContainer.getChildren().clear();
        bottomButtonsContainer.getChildren().clear();

        // Add explore section label
        Label exploreLabel = new Label("EXPLORE");
        exploreLabel.setStyle(SECTION_LABEL_STYLE);
        navigationContainer.getChildren().add(exploreLabel);

        // Add common navigation buttons
        addCommonButtons();

        // Add role-specific buttons
        if (currentUser instanceof Admin) {
            addAdminButtons();
        } else if (currentUser instanceof CinemaManager) {
            addCinemaManagerButtons();
        } else {
            addClientButtons();
        }

        // Add bottom buttons (Profile and Logout)
        addBottomButtons();
    }

    /**
     * Adds navigation buttons common to all user roles.
     */
    private void addCommonButtons() {
        // Home button with active style
        homeButton = createNavButton("Home", "mdi2h-home:24:#ff6666", NAV_BUTTON_ACTIVE_STYLE);
        homeButton.setOnAction(e -> switchToHome());
        navigationContainer.getChildren().add(homeButton);

        // Movies button
        movieButton = createNavButton("Movies", "mdi2v-video:24:#ff4444", NAV_BUTTON_STYLE);
        movieButton.setOnAction(e -> switchToMovies());
        navigationContainer.getChildren().add(movieButton);

        // Series button
        serieButton = createNavButton("Series", "mdi2t-television:24:#ff4444", NAV_BUTTON_STYLE);
        serieButton.setOnAction(e -> switchToSeries());
        navigationContainer.getChildren().add(serieButton);

        // Products button (visible for Admin and Client, not Cinema Manager)
        if (!(currentUser instanceof CinemaManager)) {
            productButton = createNavButton("Products", "mdi2s-shopping:24:#ff4444", NAV_BUTTON_STYLE);
            productButton.setOnAction(e -> switchToProducts());
            navigationContainer.getChildren().add(productButton);
        }

        // Cinema button
        cinemaButton = createNavButton("Cinema", "mdi2t-theater:24:#ff4444", NAV_BUTTON_STYLE);
        cinemaButton.setOnAction(e -> switchToCinema());
        navigationContainer.getChildren().add(cinemaButton);
    }

    /**
     * Adds navigation buttons specific to Admin users.
     */
    private void addAdminButtons() {
        // Admin section label
        Label adminLabel = new Label("ADMIN");
        adminLabel.setStyle(SECTION_LABEL_STYLE + "-fx-padding: 10 0 0 0;");
        navigationContainer.getChildren().add(adminLabel);

        // Users button
        usersButton = createNavButton("Users", "mdi2a-account-multiple:24:#ff4444", NAV_BUTTON_STYLE);
        usersButton.setOnAction(e -> switchToUsers());
        navigationContainer.getChildren().add(usersButton);

        // Orders button
        orderButton = createNavButton("Order", "mdi2s-shopping:24:#ff4444", NAV_BUTTON_STYLE);
        orderButton.setOnAction(e -> switchToOrders());
        navigationContainer.getChildren().add(orderButton);

        // Product Categories button
        productCategoriesButton = createNavButton("Categories", "mdi2f-folder-multiple:24:#ff4444", NAV_BUTTON_STYLE);
        productCategoriesButton.setOnAction(e -> switchToProductCategories());
        navigationContainer.getChildren().add(productCategoriesButton);

        // Order Analytics button
        orderAnalyticsButton = createNavButton("Analytics", "mdi2c-chart-line:24:#ff4444", NAV_BUTTON_STYLE);
        orderAnalyticsButton.setOnAction(e -> switchToOrderAnalytics());
        navigationContainer.getChildren().add(orderAnalyticsButton);

        // Series Statistics button
        seriesStatisticsButton = createNavButton("Stats", "mdi2c-chart-bar:24:#ff4444", NAV_BUTTON_STYLE);
        seriesStatisticsButton.setOnAction(e -> switchToSeriesStatistics());
        navigationContainer.getChildren().add(seriesStatisticsButton);

        // Email Users button
        emailUsersButton = createNavButton("Email", "mdi2e-email:24:#ff4444", NAV_BUTTON_STYLE);
        emailUsersButton.setOnAction(e -> switchToEmailUsers());
        navigationContainer.getChildren().add(emailUsersButton);

        // SMS Users button
        smsUsersButton = createNavButton("SMS", "mdi2m-message:24:#ff4444", NAV_BUTTON_STYLE);
        smsUsersButton.setOnAction(e -> switchToSMSUsers());
        navigationContainer.getChildren().add(smsUsersButton);
    }

    /**
     * Adds navigation buttons specific to Cinema Manager users.
     */
    private void addCinemaManagerButtons() {
        // Manager section label
        Label managerLabel = new Label("MANAGEMENT");
        managerLabel.setStyle(SECTION_LABEL_STYLE + "-fx-padding: 10 0 0 0;");
        navigationContainer.getChildren().add(managerLabel);

        // Actors button
        actorButton = createNavButton("Actors", "mdi2a-account-multiple:24:#ff4444", NAV_BUTTON_STYLE);
        actorButton.setOnAction(e -> switchToActor());
        navigationContainer.getChildren().add(actorButton);

        // Film Categories button
        filmCategorieButton = createNavButton("Film Categories", "mdi2f-folder:24:#ff4444", NAV_BUTTON_STYLE);
        filmCategorieButton.setOnAction(e -> switchToFilmCategorie());
        navigationContainer.getChildren().add(filmCategorieButton);

        // Movie Sessions button
        moviesessionButton = createNavButton("Seances", "mdi2t-ticket-confirmation:24:#ff4444", NAV_BUTTON_STYLE);
        moviesessionButton.setOnAction(e -> switchToMovieSessions());
        navigationContainer.getChildren().add(moviesessionButton);

        // Statistics button
        statestique_button = createNavButton("Statistics", "mdi2b-briefcase:24:#ff4444", NAV_BUTTON_STYLE);
        statestique_button.setOnAction(e -> switchToStatistics());
        navigationContainer.getChildren().add(statestique_button);

        // Series Management button
        seriesManagementButton = createNavButton("Series Mgmt", "mdi2t-television-classic:24:#ff4444",
                NAV_BUTTON_STYLE);
        seriesManagementButton.setOnAction(e -> switchToSeriesManagement());
        navigationContainer.getChildren().add(seriesManagementButton);
    }

    /**
     * Adds navigation buttons specific to Client users.
     */
    private void addClientButtons() {
        // Personal section label
        Label personalLabel = new Label("PERSONAL");
        personalLabel.setStyle(SECTION_LABEL_STYLE + "-fx-padding: 10 0 0 0;");
        navigationContainer.getChildren().add(personalLabel);

        // Favorites button
        favoritesButton = createNavButton("Favorites", "mdi2h-heart:24:#ff4444", NAV_BUTTON_STYLE);
        favoritesButton.setOnAction(e -> switchToFavorites());
        navigationContainer.getChildren().add(favoritesButton);

        // Shopping Cart button
        shoppingCartButton = createNavButton("Cart", "mdi2c-cart:24:#ff4444", NAV_BUTTON_STYLE);
        shoppingCartButton.setOnAction(e -> switchToShoppingCart());
        navigationContainer.getChildren().add(shoppingCartButton);

        // My Orders button
        myOrdersButton = createNavButton("My Orders", "mdi2p-package-variant:24:#ff4444", NAV_BUTTON_STYLE);
        myOrdersButton.setOnAction(e -> switchToMyOrders());
        navigationContainer.getChildren().add(myOrdersButton);

        // Watchlist button
        watchlistButton = createNavButton("Watchlist", "mdi2e-eye:24:#ff4444", NAV_BUTTON_STYLE);
        watchlistButton.setOnAction(e -> switchToWatchlist());
        navigationContainer.getChildren().add(watchlistButton);

        // Tickets button
        ticketsButton = createNavButton("My Tickets", "mdi2t-ticket:24:#ff4444", NAV_BUTTON_STYLE);
        ticketsButton.setOnAction(e -> switchToTickets());
        navigationContainer.getChildren().add(ticketsButton);
    }

    /**
     * Adds the bottom section buttons (Profile and Logout).
     */
    private void addBottomButtons() {
        // Profile button
        profileButton = createNavButton("Account", "mdi2a-account:20:#cccccc", BOTTOM_BUTTON_STYLE);
        profileButton.setOnAction(e -> switchToProfile());
        bottomButtonsContainer.getChildren().add(profileButton);

        // Logout button
        logoutButton = createNavButton("EXIT", "mdi2l-logout:20:#cccccc", BOTTOM_BUTTON_STYLE);
        logoutButton.setOnAction(e -> switchToLogout());
        bottomButtonsContainer.getChildren().add(logoutButton);
    }

    /**
     * Creates a styled navigation button with an icon and label.
     *
     * @param text        the button label text
     * @param iconLiteral the Ikonli icon literal (e.g., "mdi2h-home:24:#ff6666")
     * @param style       the CSS style string for the button
     * @return the configured Button
     */
    private Button createNavButton(String text, String iconLiteral, String style) {
        Button button = new Button();
        button.setStyle(style);
        button.setMaxWidth(200);
        button.getStyleClass().add("animated-nav-button");

        // Create icon
        FontIcon icon = new FontIcon();
        icon.setIconLiteral(iconLiteral);

        // Create label
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        // Create HBox for icon and label
        HBox graphic = new HBox(10);
        graphic.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        graphic.getChildren().addAll(icon, label);

        button.setGraphic(graphic);
        return button;
    }

    // Navigation methods (without ActionEvent parameter for lambda compatibility)

    private void switchToHome() {
        if (currentUser == null) {
            log.warn("Cannot navigate to home: current user is null");
            return;
        }
        Stage stage = (Stage) homeButton.getScene().getWindow();
        NavigationManager.navigateToHome(stage, currentUser);
    }

    private void switchToMovies() {
        if (currentUser == null) {
            log.warn("Cannot navigate to movies: current user is null");
            return;
        }
        Stage stage = (Stage) movieButton.getScene().getWindow();
        NavigationManager.navigateToMovies(stage, currentUser);
    }

    private void switchToSeries() {
        if (currentUser == null) {
            log.warn("Cannot navigate to series: current user is null");
            return;
        }
        Stage stage = (Stage) serieButton.getScene().getWindow();
        NavigationManager.navigateToSeries(stage, currentUser);
    }

    private void switchToProducts() {
        if (currentUser == null) {
            log.warn("Cannot navigate to products: current user is null");
            return;
        }
        Stage stage = (Stage) productButton.getScene().getWindow();
        NavigationManager.navigateToProducts(stage, currentUser);
    }

    private void switchToCinema() {
        if (currentUser == null) {
            log.warn("Cannot navigate to cinema: current user is null");
            return;
        }
        Stage stage = (Stage) cinemaButton.getScene().getWindow();
        NavigationManager.navigateToCinema(stage, currentUser);
    }

    private void switchToProfile() {
        if (currentUser == null) {
            log.warn("Cannot navigate to profile: current user is null");
            return;
        }
        Stage stage = (Stage) profileButton.getScene().getWindow();
        NavigationManager.navigateToProfile(stage, currentUser);
    }

    private void switchToLogout() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        NavigationManager.navigateToLogin(stage);
    }

    // Admin navigation methods

    private void switchToUsers() {
        Stage stage = (Stage) usersButton.getScene().getWindow();
        NavigationManager.navigateToUsers(stage);
    }

    private void switchToOrders() {
        Stage stage = (Stage) orderButton.getScene().getWindow();
        NavigationManager.navigateToOrders(stage);
    }

    private void switchToProductCategories() {
        Stage stage = (Stage) productCategoriesButton.getScene().getWindow();
        NavigationManager.navigateToCategories(stage);
    }

    private void switchToOrderAnalytics() {
        Stage stage = (Stage) orderAnalyticsButton.getScene().getWindow();
        NavigationManager.navigateToOrderAnalytics(stage);
    }

    private void switchToSeriesStatistics() {
        Stage stage = (Stage) seriesStatisticsButton.getScene().getWindow();
        NavigationManager.navigateToSeriesStatistics(stage);
    }

    private void switchToEmailUsers() {
        Stage stage = (Stage) emailUsersButton.getScene().getWindow();
        NavigationManager.navigateToEmailComposer(stage);
    }

    private void switchToSMSUsers() {
        Stage stage = (Stage) smsUsersButton.getScene().getWindow();
        NavigationManager.navigateToSMSComposer(stage);
    }

    // Cinema Manager navigation methods

    private void switchToActor() {
        Stage stage = (Stage) actorButton.getScene().getWindow();
        NavigationManager.navigate(stage, "/ui/films/InterfaceActor.fxml");
    }

    private void switchToFilmCategorie() {
        navigateToCategoryManagement(CategoryType.MOVIE);
    }

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

            final Stage stage = (Stage) filmCategorieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            log.error("Error navigating to category management: {}", e.getMessage(), e);
        }
    }

    private void switchToMovieSessions() {
        Stage stage = (Stage) moviesessionButton.getScene().getWindow();
        NavigationManager.navigate(stage, "/ui/cinemas/DashboardResponsableCinema.fxml");
    }

    private void switchToStatistics() {
        Stage stage = (Stage) statestique_button.getScene().getWindow();
        NavigationManager.navigate(stage, "/ui/cinemas/statistiques.fxml");
    }

    private void switchToSeriesManagement() {
        Stage stage = (Stage) seriesManagementButton.getScene().getWindow();
        NavigationManager.navigateToSeries(stage, currentUser);
    }

    // Client navigation methods

    private void switchToFavorites() {
        Stage stage = (Stage) favoritesButton.getScene().getWindow();
        NavigationManager.navigateToFavorites(stage);
    }

    private void switchToShoppingCart() {
        Stage stage = (Stage) shoppingCartButton.getScene().getWindow();
        NavigationManager.navigateToShoppingCart(stage);
    }

    private void switchToMyOrders() {
        Stage stage = (Stage) myOrdersButton.getScene().getWindow();
        NavigationManager.navigateToMyOrders(stage);
    }

    private void switchToWatchlist() {
        Stage stage = (Stage) watchlistButton.getScene().getWindow();
        NavigationManager.navigateToWatchlist(stage);
    }

    private void switchToTickets() {
        Stage stage = (Stage) ticketsButton.getScene().getWindow();
        NavigationManager.navigateToTicketHistory(stage);
    }

    /**
     * Invoked after FXML loading; initializes the sidebar with current user from
     * session.
     *
     * @param location  the location used to resolve relative paths for the root
     *                  object, or null
     * @param resources the resources used to localize the root object, or null
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setCurrentUser(SessionManager.getCurrentUser());
        log.debug("SidebarController initialized with dynamically created navigation buttons");
    }

}
