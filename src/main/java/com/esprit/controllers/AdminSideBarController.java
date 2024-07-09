package com.esprit.controllers;
import java.net.URL;
import java.util.ResourceBundle;
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
/**
 * Manages the navigation between different views in an application for an administrator.
 * It provides buttons to switch between views such as users, events, movies, products,
 * series, cinema, and profile. The controller also includes a web view and handles
 * the login and logout functionality.
 */
public class AdminSideBarController implements Initializable {
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
     * Is called when an action event occurs, indicating a change in the state of the
     * application. It performs no additional actions and does not modify any variables.
     * 
     * @param event occurrence of an action that triggered the `switchToEvent` method to
     * be called.
     */
    @FXML
    void switchToEvent(ActionEvent event) {
    }
    /**
     * Loads an fxml file named `ListeCommande.fxml` and sets the stage scene to display
     * its root element.
     * 
     * @param event triggered action that initiated the code execution in the `switchToMovies()`
     * method.
     * 
     * 	- Event type: `ActionEvent`
     * 	- Target: `movieButton` (a button object)
     */
    @FXML
    void switchToMovies(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCommande.fxml"));
            // FilmController seanceController = loader.getController();
            // seanceController.setData(admin);
            Parent root = loader.load();
            Stage stage = (Stage) movieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads an FXML file and sets the stage scene to display the contents of the file.
     * 
     * @param event click event of a button, which triggers the execution of the
     * `switchToProducts()` method.
     * 
     * Event: An event object representing a button press.
     * 
     * Properties:
     * 
     * 	- `getSource()`: Returns the source of the event (in this case, the `ProductButton`).
     * 	- `getButton()`: Returns the button that was pressed (e.g., "Product").
     */
    @FXML
    void switchToProducts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignProduitAdmin.fxml"));
            // DesignProduitAdminContoller seanceController = loader.getController();
            // seanceController.setData(admin);
            Parent root = loader.load();
            Stage stage = (Stage) productButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads a FXML file named "Categorie-view.fxml" into a Stage using the `FXMLLoader`.
     * It sets the data for the controller and then sets the scene of the Stage to the
     * loaded root node.
     * 
     * @param event click event of the `switchToSeries` button, triggering the execution
     * of the code inside the function.
     * 
     * 	- `event`: An `ActionEvent` object representing a user-initiated action.
     */
    @FXML
    void switchToSeries(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Categorie-view.fxml"));
            // SerieClientController seanceController = loader.getController();
            // seanceController.setData(admin);
            Parent root = loader.load();
            Stage stage = (Stage) serieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads a new FXML file `DashboardAdminCinema.fxml` when the `cinemaButton` is
     * clicked. It creates a new stage and sets its scene to the loaded root element.
     * 
     * @param event click event on the cinema button that triggered the function execution.
     * 
     * 	- `event`: An `ActionEvent` object representing the user's action of switching
     * to the cinema view.
     */
    @FXML
    void switchtcinema(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardAdminCinema.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cinemaButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads a new user interface using the FXMLLoader and replaces the existing stage
     * with the new scene.
     * 
     * @param event user action that triggered the call to the `switchToUsers()` method.
     * 
     * 	- Event type: ActionEvent
     */
    @FXML
    public void switchToUsers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usersButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Is called when an instance of a class is created and initializes some resources.
     * 
     * @param location URL of the application's resource bundle, which is used to load
     * the appropriate resources for the application.
     * 
     * @param resources resource bundle for the current application.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    /**
     * Loads a new scene with a FXML file when the logout button is clicked, replacing
     * the current stage's scene with the new one.
     * 
     * @param event click event on the logout button that triggers the function execution.
     * 
     * 	- Event type: `ActionEvent`, indicating that the event was triggered by a user action.
     * 	- Target object: `logoutButton`, indicating the button that was clicked to trigger
     * the event.
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
     * Loads and displays the `Profile.fxml` stage when the `profileButton` is clicked.
     * 
     * @param event mouse click event that triggered the function execution.
     * 
     * 	- `event`: An `ActionEvent` object representing the user's action of clicking on
     * the "Switch to Profile" button.
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
}
