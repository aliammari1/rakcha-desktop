package com.esprit.controllers;

import com.esprit.models.users.Admin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the navigation between different views in an application for an
 * administrator.
 * It provides buttons to switch between views such as users, events, movies,
 * products,
 * series, cinema, and profile. The controller also includes a web view and
 * handles
 * the login and logout functionality.
 */
public class AdminSideBarController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(AdminSideBarController.class.getName());
    @FXML
    public Button usersButton;
    Admin admin;
    @FXML
    private WebView webView;
    @FXML
    private Button cinemaButton;
    @FXML
    private Button eventButton;
    @FXML
    private Button movieButton;
    @FXML
    private Button productButton;
    @FXML
    private Button serieButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button profileButton;

    /**
     * Is called when an action event occurs, indicating a change in the state of
     * the
     * application. It performs no additional actions and does not modify any
     * variables.
     *
     * @param event occurrence of an action that triggered the `switchToEvent`
     *              method to
     *              be called.
     */
    @FXML
    void switchToEvent(final ActionEvent event) {
    }

    /**
     * Loads an fxml file named `ListeCommande.fxml` and sets the stage scene to
     * display
     * its root element.
     *
     * @param event triggered action that initiated the code execution in the
     *              `switchToMovies()`
     *              method.
     *              <p>
     *              - Event type: `ActionEvent`
     *              - Target: `movieButton` (a button object)
     */
    @FXML
    void switchToMovies(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/produits/ListeCommande.fxml"));
            // FilmController seanceController = loader.getController();
            // seanceController.setData(admin);
            final Parent root = loader.load();
            final Stage stage = (Stage) this.movieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            AdminSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads an FXML file and sets the stage scene to display the contents of the
     * file.
     *
     * @param event click event of a button, which triggers the execution of the
     *              `switchToProducts()` method.
     *              <p>
     *              Event: An event object representing a button press.
     *              <p>
     *              Properties:
     *              <p>
     *              - `getSource()`: Returns the source of the event (in this case,
     *              the `ProductButton`).
     *              - `getButton()`: Returns the button that was pressed (e.g.,
     *              "Product").
     */
    @FXML
    void switchToProducts(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/produits/DesignProduitAdmin.fxml"));
            // DesignProduitAdminContoller seanceController = loader.getController();
            // seanceController.setData(admin);
            final Parent root = loader.load();
            final Stage stage = (Stage) this.productButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            AdminSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads a FXML file named "/ui/series/Categorie-view.fxml" into a Stage using the
     * `FXMLLoader`.
     * It sets the data for the controller and then sets the scene of the Stage to
     * the
     * loaded root node.
     *
     * @param event click event of the `switchToSeries` button, triggering the
     *              execution
     *              of the code inside the function.
     *              <p>
     *              - `event`: An `ActionEvent` object representing a user-initiated
     *              action.
     */
    @FXML
    void switchToSeries(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/series/Categorie-view.fxml"));
            // SerieClientController seanceController = loader.getController();
            // seanceController.setData(admin);
            final Parent root = loader.load();
            final Stage stage = (Stage) this.serieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            AdminSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads a new FXML file `DashboardAdminCinema.fxml` when the `cinemaButton` is
     * clicked. It creates a new stage and sets its scene to the loaded root
     * element.
     *
     * @param event click event on the cinema button that triggered the function
     *              execution.
     *              <p>
     *              - `event`: An `ActionEvent` object representing the user's
     *              action of switching
     *              to the cinema view.
     */
    @FXML
    void switchtcinema(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/cinemas/DashboardAdminCinema.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.cinemaButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            AdminSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads a new user interface using the FXMLLoader and replaces the existing
     * stage
     * with the new scene.
     *
     * @param event user action that triggered the call to the `switchToUsers()`
     *              method.
     *              <p>
     *              - Event type: ActionEvent
     */
    @FXML
    public void switchToUsers(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/AdminDashboard.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.usersButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            AdminSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Is called when an instance of a class is created and initializes some
     * resources.
     *
     * @param location  URL of the application's resource bundle, which is used to
     *                  load
     *                  the appropriate resources for the application.
     * @param resources resource bundle for the current application.
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
    }

    /**
     * Loads a new scene with a FXML file when the logout button is clicked,
     * replacing
     * the current stage's scene with the new one.
     *
     * @param event click event on the logout button that triggers the function
     *              execution.
     *              <p>
     *              - Event type: `ActionEvent`, indicating that the event was
     *              triggered by a user action.
     *              - Target object: `logoutButton`, indicating the button that was
     *              clicked to trigger
     *              the event.
     */
    @FXML
    void switchToLogout(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Login.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.logoutButton.getScene().getWindow();
            stage.setUserData(null);
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            AdminSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads and displays the `Profile.fxml` stage when the `profileButton` is
     * clicked.
     *
     * @param event mouse click event that triggered the function execution.
     *              <p>
     *              - `event`: An `ActionEvent` object representing the user's
     *              action of clicking on
     *              the "Switch to Profile" button.
     */
    @FXML
    void switchToProfile(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Profile.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.profileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            AdminSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
