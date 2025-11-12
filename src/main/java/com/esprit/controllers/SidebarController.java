package com.esprit.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.users.Admin;
import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Unified sidebar controller that manages navigation between different views
 * in the application for different user roles (Admin, CinemaManager, Client).
 * It provides role-based button visibility and handles navigation to various
 * sections such as users, events, movies, products, series, cinema, and
 * profile.
 */
public class SidebarController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(SidebarController.class.getName());

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

    // Cinema Manager specific buttons
    @FXML
    private Button actorButton;
    @FXML
    private Button filmCategorieButton;
    @FXML
    private Button moviesessionButton;
    @FXML
    private Button statestique_button;

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

    @FXML
    private WebView webView;

    /**
     * Sets the current user and configures the sidebar based on user role.
     *
     * @param user the current user (Admin, CinemaManager, or Client)
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user instanceof Admin) {
            configureForAdmin();
        }
 else if (user instanceof CinemaManager) {
            configureForCinemaManager();
        }
 else {
            configureForClient();
        }

    }


    /**
     * Sets admin data for backward compatibility.
     */
    public void setData(Admin admin) {
        this.currentUser = admin;
        configureForAdmin();
    }


    /**
     * Sets cinema manager data for backward compatibility.
     */
    public void setData(CinemaManager cinemaManager) {
        this.currentUser = cinemaManager;
        configureForCinemaManager();
    }


    /**
     * Configures the sidebar for Admin users.
     */
    private void configureForAdmin() {
        // Show admin-specific buttons
        usersButton.setVisible(true);
        orderButton.setVisible(true);
        orderIcon.setVisible(true);

        // Hide cinema manager specific buttons
        actorButton.setVisible(false);
        filmCategorieButton.setVisible(false);
        moviesessionButton.setVisible(false);
        statestique_button.setVisible(false);
        actorIcon.setVisible(false);
        filmCategorieIcon.setVisible(false);
        moviesessionIcon.setVisible(false);
        statisticsIcon.setVisible(false);

        // Show common buttons
        movieButton.setVisible(true);
        serieButton.setVisible(true);
        productButton.setVisible(true);
        cinemaButton.setVisible(true);
    }


    /**
     * Configures the sidebar for Cinema Manager users.
     */
    private void configureForCinemaManager() {
        // Hide admin-specific buttons
        usersButton.setVisible(false);
        orderButton.setVisible(false);
        orderIcon.setVisible(false);

        // Show cinema manager specific buttons
        actorButton.setVisible(true);
        filmCategorieButton.setVisible(true);
        moviesessionButton.setVisible(true);
        statestique_button.setVisible(true);
        actorIcon.setVisible(true);
        filmCategorieIcon.setVisible(true);
        moviesessionIcon.setVisible(true);
        statisticsIcon.setVisible(true);

        // Show common buttons
        movieButton.setVisible(true);
        serieButton.setVisible(true);
        productButton.setVisible(false); // Cinema managers don't see products
        cinemaButton.setVisible(true);
    }


    /**
     * Configures the sidebar for Client users.
     */
    private void configureForClient() {
        // Hide admin-specific buttons
        usersButton.setVisible(false);
        orderButton.setVisible(false);
        orderIcon.setVisible(false);

        // Hide cinema manager specific buttons
        actorButton.setVisible(false);
        filmCategorieButton.setVisible(false);
        moviesessionButton.setVisible(false);
        statestique_button.setVisible(false);
        actorIcon.setVisible(false);
        filmCategorieIcon.setVisible(false);
        moviesessionIcon.setVisible(false);
        statisticsIcon.setVisible(false);

        // Show common buttons
        movieButton.setVisible(true);
        serieButton.setVisible(true);
        productButton.setVisible(true);
        cinemaButton.setVisible(true);
    }


    // Navigation methods

    /**
     * Switches to the users management view (Admin only).
     */
    @FXML
    public void switchToUsers(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/AdminDashboard.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.usersButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
 catch (final Exception e) {
            SidebarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Switches to the events view.
     */
    @FXML
    void switchToEvent(final ActionEvent event) {
        // Implementation depends on role
    }


    /**
     * Switches to the movies view.
     */
    @FXML
    void switchToMovies(final ActionEvent event) {
        try {
            String fxmlPath;
            if (currentUser instanceof Admin) {
                fxmlPath = "/ui/produits/ListeOrder.fxml";
            }
 else if (currentUser instanceof CinemaManager) {
                fxmlPath = "/ui/films/InterfaceFilm.fxml";
            }
 else {
                fxmlPath = "/ui/films/filmuser.fxml";
            }


            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource(fxmlPath));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.movieButton.getScene().getWindow();

            stage.setScene(new Scene(root));
        }
 catch (final Exception e) {
            SidebarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Switches to the orders view (Admin only).
     */
    @FXML
    void switchToOrders(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/produits/ListeOrder.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.orderButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
 catch (final Exception e) {
            SidebarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Switches to the products view.
     */
    @FXML
    void switchToProducts(final ActionEvent event) {
        try {
            String fxmlPath;
            if (currentUser instanceof Admin) {
                fxmlPath = "/ui/produits/DesignProductAdmin.fxml";
            }
 else {
                fxmlPath = "/ui/produits/AfficherProductClient.fxml";
            }


            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource(fxmlPath));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.productButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
 catch (final Exception e) {
            SidebarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Switches to the series view.
     */
    @FXML
    void switchToSeries(final ActionEvent event) {
        try {
            String fxmlPath;
            if (currentUser instanceof Admin) {
                fxmlPath = "/ui/series/Categorie-view.fxml";
            }
 else if (currentUser instanceof CinemaManager) {
                fxmlPath = "/ui/series/Categorie-view.fxml";
            }
 else {
                fxmlPath = "/ui/series/SeriesClient.fxml";
            }


            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource(fxmlPath));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.serieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
 catch (final Exception e) {
            SidebarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Switches to the cinema view.
     */
    @FXML
    void switchToCinema(final ActionEvent event) {
        try {
            String fxmlPath;
            if (currentUser instanceof Admin) {
                fxmlPath = "/ui/cinemas/DashboardAdminCinema.fxml";
            }
 else if (currentUser instanceof CinemaManager) {
                fxmlPath = "/ui/cinemas/DashboardResponsableCinema.fxml";
            }
 else {
                fxmlPath = "/ui/cinemas/DashboardClientCinema.fxml";
            }


            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource(fxmlPath));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.cinemaButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
 catch (final Exception e) {
            SidebarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Switches to the actors view (Cinema Manager only).
     */
    @FXML
    void switchToActor(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/films/InterfaceActor.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.actorButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
 catch (final Exception e) {
            SidebarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Switches to the film categories view (Cinema Manager only).
     */
    @FXML
    void switchToFilmCategorie(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/films/InterfaceCategory.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.filmCategorieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
 catch (final Exception e) {
            SidebarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Switches to the movie sessions view (Cinema Manager only).
     */
    @FXML
    void switchToMovieSessions(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(
                    this.getClass().getResource("/ui/cinemas/DashboardResponsableCinema.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.moviesessionButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
 catch (final Exception e) {
            SidebarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Switches to the statistics view (Cinema Manager only).
     */
    @FXML
    void switchToStatistics(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/cinemas/statistiques.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.statestique_button.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
 catch (final Exception e) {
            SidebarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Switches to the profile view.
     */
    /**
     * Handles navigation to home page based on user role.
     */
    @FXML
    void switchToHome(final ActionEvent event) {
        try {
            String fxmlPath;
            if (currentUser instanceof Admin) {
                fxmlPath = "/ui/users/HomeAdmin.fxml";
            }
 else if (currentUser instanceof CinemaManager) {
                fxmlPath = "/ui/users/HomeCinemaManager.fxml";
            }
 else {
                fxmlPath = "/ui/users/HomeClient.fxml";
            }


            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource(fxmlPath));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.homeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
 catch (final Exception e) {
            SidebarController.LOGGER.log(Level.SEVERE, "Error navigating to home: " + e.getMessage(), e);
        }

    }


    /**
     * Handles profile navigation.
     */
    @FXML
    void switchToProfile(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Profile.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.profileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
 catch (final Exception e) {
            SidebarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Handles logout functionality.
     */
    @FXML
    void switchToLogout(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Login.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.logoutButton.getScene().getWindow();
            stage.setUserData(null);
            stage.setScene(new Scene(root));
        }
 catch (final Exception e) {
            SidebarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Initializes the controller after FXML loading.
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // Default configuration for client (will be overridden by setCurrentUser)
        configureForClient();
    }

}

