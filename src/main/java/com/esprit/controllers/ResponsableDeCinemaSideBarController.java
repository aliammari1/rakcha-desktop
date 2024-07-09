package com.esprit.controllers;
import com.esprit.controllers.cinemas.CinemaStatisticsController;
import com.esprit.controllers.films.ActorController;
import com.esprit.controllers.films.FilmController;
import com.esprit.controllers.series.CategorieController;
import com.esprit.models.users.Responsable_de_cinema;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
/**
 * Is responsible for managing the navigation between different views in a cinema
 * management application. It provides buttons for switching between different
 * categories of films, seances, and statistics, as well as a logout button. The
 * controller also sets data for the current user and handles actions such as switching
 * to profiles or films.
 */
public class ResponsableDeCinemaSideBarController implements Initializable {
    Responsable_de_cinema resp;
    @FXML
    private Button actorButton;
    @FXML
    private Button cinemaButton;
    @FXML
    private Button filmCategorieButton;
    @FXML
    private Button movieButton;
    @FXML
    private Button seanceButton;
    @FXML
    private Button statestique_button;
    @FXML
    private Button logoutButton;
    @FXML
    private Button profileButton;
    /**
     * Loads and displays an actor's interface using the `FXMLLoader` class, passing in
     * a reference to the actor's data as an argument.
     * 
     * @param event user's action of clicking on the button that triggers the function.
     * 
     * 	- `event`: An `ActionEvent` object representing a user interaction with the stage
     * button.
     */
    @FXML
    void switchToActor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceActor.fxml"));
            ActorController seanceController = loader.getController();
            // seanceController.setData(resp);
            Parent root = loader.load();
            Stage stage = (Stage) actorButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads an FXML file named "statistiques.fxml" using the `FXMLLoader` class, creates
     * a new controller instance from the loaded file, and sets the scene of a stage to
     * display the loaded content.
     * 
     * @param event event of a button click that triggered the function execution.
     * 
     * 	- Event is an ActionEvent object, representing a user action related to the stage
     * or window where the function is called.
     */
    @FXML
    void switchstatestique(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/statistiques.fxml"));
            CinemaStatisticsController seanceController = loader.getController();
            // seanceController.setData(resp);
            Parent root = loader.load();
            Stage stage = (Stage) statestique_button.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads and displays a FXML file named "DashboardResponsableCinema.fxml" in a JavaFX
     * stage, replacing the existing content.
     * 
     * @param event click event on the cinema button that triggered the function execution.
     * 
     * Event: An ActionEvent object representing a button press.
     */
    @FXML
    void switchToCinema(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardResponsableCinema.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) cinemaButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
    /**
     * Loads an FXML file and displays its contents on a stage, replacing the current scene.
     * 
     * @param event clicked button that triggered the function call.
     * 
     * Event: An action event object representing the user's interaction with the button.
     */
    @FXML
    void switchToFilmCategorie(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceCategory.fxml"));
            CategorieController seanceController = loader.getController();
            // seanceController.setData(resp);
            Parent root = loader.load();
            Stage stage = (Stage) filmCategorieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads an FXML file and creates a new scene in a Stage using the FXMLLoader class.
     * 
     * @param event click event that triggered the `switchToMovies()` method to be executed.
     * 
     * 	- Event type: `ActionEvent` indicating a user action on the movie button
     * 	- Target object: `movieButton` providing the context of the event
     */
    @FXML
    void switchToMovies(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceFilm.fxml"));
            FilmController seanceController = loader.getController();
            // seanceController.setData(resp);
            Parent root = loader.load();
            Stage stage = (Stage) movieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads and displays a new FXML document when the "Seance" button is clicked, using
     * the `FXMLLoader` class and the `Stage` class to switch between scenes.
     * 
     * @param event occurrence of a button press event that triggers the function to
     * switch to the `DashboardResponsableCinema.fxml` scene.
     * 
     * 	- `event`: An `ActionEvent` object representing the user's action that triggered
     * the function.
     */
    @FXML
    void switchToSeances(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardResponsableCinema.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) seanceButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Sets the value of the `resp` field to a provided `Responsable_de_cinema` object.
     * 
     * @param resp Responsable_de_cinema object that will store the data.
     */
    public void setData(Responsable_de_cinema resp) {
        this.resp = resp;
    }
    /**
     * Is called when the application starts and initializes its resources by taking a
     * URL and resource bundle as arguments.
     * 
     * @param location URL of the initial resource to be processed by the function.
     * 
     * @param resources resource bundle for the application, providing localized strings
     * and formatting data to the initialize method.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    /**
     * Loads a new FXML file `/Login.fxml` when the logout button is clicked, replacing
     * the current scene with a new one containing the loaded stage.
     * 
     * @param event event of button click, which triggers the function to switch to the
     * login screen.
     * 
     * 	- `event`: an `ActionEvent` object representing a user action on the logout button.
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
     * Loads a new FXML file, replacing the current scene with the new one.
     * 
     * @param event occurrence of a button press event that triggers the execution of the
     * `switchToProfile()` method.
     * 
     * 	- `event`: An `ActionEvent` object representing the user's action of pressing the
     * profile button.
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
