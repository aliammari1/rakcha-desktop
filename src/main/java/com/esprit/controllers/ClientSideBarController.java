package com.esprit.controllers;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
/**
 * Is responsible for handling button clicks and navigating between different views
 * in a client-side application. It provides methods for switching to various sections
 * such as events, movies, products, series, cinema, logout, and profile. The class
 * also initializes the application's user data.
 */
public class ClientSideBarController implements Initializable {
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
     * Handles an `ActionEvent` and performs a specific action based on the event type.
     * 
     * @param event event that triggered the function execution, providing the necessary
     * information for the function to perform its intended action.
     */
    @FXML
    void switchToEvent(ActionEvent event) {
    }
    /**
     * Loads a new FXML file "filmuser.fxml" into the stage using the `FXMLLoader` class,
     * replacing the current scene with the newly loaded one.
     * 
     * @param event user's action that triggered the function, and it is of type `ActionEvent`.
     * 
     * 	- It is an `ActionEvent`, indicating that the event occurred due to user action.
     * 	- The event target is the `movieButton`, indicating the button that was clicked.
     */
    @FXML
    void switchToMovies(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/filmuser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) movieButton.getScene().getWindow();
            System.out.println("---------" + stage.getUserData());
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads an FXML file named `AfficherProduitClient.fxml`, and sets the scene of a
     * Stage to the loaded root element, replacing the previous content.
     * 
     * @param event occurrence of a button press, which triggers the execution of the
     * `switchToProducts()` method.
     * 
     * Event: An action event that triggered the function execution.
     */
    @FXML
    void switchToProducts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduitClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) productButton.getScene().getWindow();
            System.out.println("---------" + stage.getUserData());
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads a new FXML file "SeriesClient.fxml" when the " serieButton" action is
     * triggered, replacing the current stage with a new scene containing the loaded root
     * node.
     * 
     * @param event `ActionEvent` that triggered the `switchToSeries()` method, providing
     * the source of the event that initiated the method's execution.
     * 
     * 	- Type: `ActionEvent` (represents an action event occurring on a JavaFX component)
     * 	- Source: Reference to the component that generated the event (usually a button
     * or menu item)
     */
    @FXML
    void switchToSeries(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SeriesClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) serieButton.getScene().getWindow();
            System.out.println("---------" + stage.getUserData());
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads a new scene from an FXML file when the "Cinema" button is clicked.
     * 
     * @param event event that triggered the `switchtcinema` function, which is an
     * ActionEvent in this case.
     * 
     * 	- `event`: an `ActionEvent` object representing a user-initiated event.
     */
    @FXML
    void switchtcinema(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardClientCinema.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cinemaButton.getScene().getWindow();
            System.out.println("---------" + stage.getUserData());
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads a new FXML file, replaces the current stage's scene with it, and sets the
     * stage's user data to null.
     * 
     * @param event click event on the logout button that triggered the function execution.
     * 
     * 	- `event`: An instance of the `ActionEvent` class, representing a user action
     * (such as button press) that triggered the function execution.
     */
    @FXML
    void switchToLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setUserData(null);
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads a new scene containing a `Parent` element with the FXML file `"Profile.fxml"`
     * when the `profileButton` is clicked.
     * 
     * @param event click event that triggered the function execution.
     * 
     * 	- Event type: `ActionEvent` indicating that the event was triggered by a user
     * action on the associated control (in this case, the `profileButton`).
     */
    @FXML
    void switchToProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profile.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) profileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Is called when an instance of a class is created and initializes the object's state
     * by calling its superclass's `initialize` method and performing any additional
     * initialization logic specified in the method body.
     * 
     * @param location URL of the web application's root directory.
     * 
     * @param resources resource bundle for the application, providing localized strings
     * and other resources for the user interface and functionality.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
