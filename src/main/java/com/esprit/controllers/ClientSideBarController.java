package com.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Is responsible for handling button clicks and navigating between different
 * views
 * in a client-side application. It provides methods for switching to various
 * sections
 * such as events, movies, products, series, cinema, logout, and profile. The
 * class
 * also initializes the application's user data.
 */
public class ClientSideBarController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(ClientSideBarController.class.getName());
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
     * Handles an `ActionEvent` and performs a specific action based on the event
     * type.
     *
     * @param event event that triggered the function execution, providing the
     *              necessary
     *              information for the function to perform its intended action.
     */
    @FXML
    void switchToEvent(final ActionEvent event) {
    }

    /**
     * Loads a new FXML file "/ui/films/filmuser.fxml" into the stage using the
     * `FXMLLoader`
     * class,
     * replacing the current scene with the newly loaded one.
     *
     * @param event user's action that triggered the function, and it is of type
     *              `ActionEvent`.
     *              <p>
     *              - It is an `ActionEvent`, indicating that the event occurred due
     *              to user action.
     *              - The event target is the `movieButton`, indicating the button
     *              that was clicked.
     */
    @FXML
    void switchToMovies(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/films/filmuser.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.movieButton.getScene().getWindow();
            ClientSideBarController.LOGGER.log(Level.INFO, "---------{0}", stage.getUserData());
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            ClientSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads an FXML file named `AfficherProduitClient.fxml`, and sets the scene of
     * a
     * Stage to the loaded root element, replacing the previous content.
     *
     * @param event occurrence of a button press, which triggers the execution of
     *              the
     *              `switchToProducts()` method.
     *              <p>
     *              Event: An action event that triggered the function execution.
     */
    @FXML
    void switchToProducts(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/produits/AfficherProduitClient.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.productButton.getScene().getWindow();
            ClientSideBarController.LOGGER.info("---------" + stage.getUserData());
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            ClientSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads a new FXML file "/ui/series/SeriesClient.fxml" when the " serieButton" action
     * is
     * triggered, replacing the current stage with a new scene containing the loaded
     * root
     * node.
     *
     * @param event `ActionEvent` that triggered the `switchToSeries()` method,
     *              providing
     *              the source of the event that initiated the method's execution.
     *              <p>
     *              - Type: `ActionEvent` (represents an action event occurring on a
     *              JavaFX component)
     *              - Source: Reference to the component that generated the event
     *              (usually a button
     *              or menu item)
     */
    @FXML
    void switchToSeries(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/series/SeriesClient.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.serieButton.getScene().getWindow();
            ClientSideBarController.LOGGER.info("---------" + stage.getUserData());
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            ClientSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads a new scene from an FXML file when the "Cinema" button is clicked.
     *
     * @param event event that triggered the `switchtcinema` function, which is an
     *              ActionEvent in this case.
     *              <p>
     *              - `event`: an `ActionEvent` object representing a user-initiated
     *              event.
     */
    @FXML
    void switchtcinema(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/cinemas/DashboardClientCinema.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.cinemaButton.getScene().getWindow();
            ClientSideBarController.LOGGER.info("---------" + stage.getUserData());
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            ClientSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads a new FXML file, replaces the current stage's scene with it, and sets
     * the
     * stage's user data to null.
     *
     * @param event click event on the logout button that triggered the function
     *              execution.
     *              <p>
     *              - `event`: An instance of the `ActionEvent` class, representing
     *              a user action
     *              (such as button press) that triggered the function execution.
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
            ClientSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads a new scene containing a `Parent` element with the FXML file
     * `"/ui/users/Profile.fxml"`
     * when the `profileButton` is clicked.
     *
     * @param event click event that triggered the function execution.
     *              <p>
     *              - Event type: `ActionEvent` indicating that the event was
     *              triggered by a user
     *              action on the associated control (in this case, the
     *              `profileButton`).
     */
    @FXML
    void switchToProfile(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Profile.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.profileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            ClientSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Is called when an instance of a class is created and initializes the object's
     * state
     * by calling its superclass's `initialize` method and performing any additional
     * initialization logic specified in the method body.
     *
     * @param location  URL of the web application's root directory.
     * @param resources resource bundle for the application, providing localized
     *                  strings
     *                  and other resources for the user interface and
     *                  functionality.
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
    }
}
