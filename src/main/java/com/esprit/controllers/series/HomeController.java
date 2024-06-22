package com.esprit.controllers.series;
import com.esprit.controllers.ClientSideBarController;
import com.esprit.models.users.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
/**
 * Defines three methods that handle user actions: Ocategories, Oseries, and Oepisode.
 * These methods load FXML views of categories, series, and episodes, respectively,
 * and display them in separate stages when the corresponding buttons are clicked.
 */
public class HomeController {
    /**
     * Loads a FXML file and displays it in a stage, using the `FXMLLoader` class to load
     * the view and the `Scene` and `Stage` classes to manage the scene and window.
     * 
     * @param event event that triggered the `Ocategories()` method to be called, providing
     * the necessary context for the method to perform its actions.
     * 
     * 	- `event`: An instance of `ActionEvent`, representing an action event that triggered
     * the function.
     */
    @FXML
    void Ocategories(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Categorie-view.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Loads an FXML file named "Serie-view.fxml" using the `FXMLLoader`, creates a scene
     * and sets it as the scene of a stage, and then shows the stage.
     * 
     * @param event event that triggered the execution of the `Oseries` method, which is
     * an ActionEvent containing information about the action that was performed, such
     * as the source of the event and the ID of the event.
     * 
     * 	- `event`: An `ActionEvent` object representing a user action that triggered the
     * function execution.
     */
    @FXML
    void Oseries(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Serie-view.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Loads an FXML file, creates a scene, and displays it in a JavaFX stage.
     * 
     * @param event event that triggered the execution of the `Oepisode` method, providing
     * the necessary information to handle the event appropriately.
     * 
     * Event: An action event that triggered the execution of the function.
     */
    @FXML
    void Oepisode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Episode-view.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
