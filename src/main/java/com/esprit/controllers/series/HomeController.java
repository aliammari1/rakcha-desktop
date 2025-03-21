package com.esprit.controllers.series;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Defines three methods that handle user actions: Ocategories, Oseries, and
 * Oepisode.
 * These methods load FXML views of categories, series, and episodes,
 * respectively,
 * and display them in separate stages when the corresponding buttons are
 * clicked.
 */
public class HomeController {
    /**
     * Loads a FXML file and displays it in a stage, using the `FXMLLoader` class to
     * load
     * the view and the `Scene` and `Stage` classes to manage the scene and window.
     *
     * @param event event that triggered the `Ocategories()` method to be called,
     *              providing
     *              the necessary context for the method to perform its actions.
     *              <p>
     *              - `event`: An instance of `ActionEvent`, representing an action
     *              event that triggered
     *              the function.
     */
    @FXML
    void Ocategories(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/Categorie-view.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads an FXML file named "/ui/series/Serie-view.fxml" using the `FXMLLoader`,
     * creates a scene
     * and sets it as the scene of a stage, and then shows the stage.
     *
     * @param event event that triggered the execution of the `Oseries` method,
     *              which is
     *              an ActionEvent containing information about the action that was
     *              performed, such
     *              as the source of the event and the ID of the event.
     *              <p>
     *              - `event`: An `ActionEvent` object representing a user action
     *              that triggered the
     *              function execution.
     */
    @FXML
    void Oseries(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("/ui/series/Serie-view.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads an FXML file, creates a scene, and displays it in a JavaFX stage.
     *
     * @param event event that triggered the execution of the `Oepisode` method,
     *              providing
     *              the necessary information to handle the event appropriately.
     *              <p>
     *              Event: An action event that triggered the execution of the
     *              function.
     */
    @FXML
    void Oepisode(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/Episode-view.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
