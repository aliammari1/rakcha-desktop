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
 * Manages menu options for clients, including category, series, and episode
 * management
 * through FXML loading.
 */
public class HommeClientController {
    /**
     * @param event
     * @throws IOException
     */
    /// gestion de menu
    /**
     * Loads a FXML file, creates a scene and stages it, displaying the content in a
     * new
     * window.
     *
     * @param event ActionEvent object that triggered the function execution,
     *              providing
     *              information about the source of the event and any relevant data.
     *              <p>
     *              Event: An event object that represents an action event occurred
     *              in the JavaFX application.
     *              <p>
     *              Properties:
     *              <p>
     *              - `getSource()`: Returns the source of the event, which is
     *              typically a button or
     *              a menu item.
     *              - `getEventType()`: Returns the type of the event, such as
     *              `ACTION`, `ENTER`, or
     *              `EXIT`.
     */
    @FXML
    void Ocategories(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui//ui/CategorieClient.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads an FXML file, creates a scene from it, and displays it on the primary
     * stage
     * using the `show()` method.
     *
     * @param event event that triggered the function, specifically the opening of
     *              the
     *              SeriesClient.fxml file.
     *              <p>
     *              - `event`: an instance of `ActionEvent`, indicating that the
     *              method has been
     *              triggered by a user action.
     */
    @FXML
    void Oseries(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/SeriesClient.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads an FXML file, creates a scene and sets it as the scene of a stage. The
     * stage
     * is then shown.
     *
     * @param event event that triggered the execution of the `Oepisode` method,
     *              providing
     *              the source of the event as an `ActionEvent`.
     *              <p>
     *              - `event` is an instance of `ActionEvent`, indicating that the
     *              method was invoked
     *              by a user action.
     *              - The source of the event is a `Node` object representing the
     *              origin of the event
     *              (in this case, the `EpisodeClient` stage).
     */
    @FXML
    void Oepisode(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/EpisodeClient.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
