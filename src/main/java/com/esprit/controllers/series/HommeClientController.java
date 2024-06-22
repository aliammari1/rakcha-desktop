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
 * Manages menu options for clients, including category, series, and episode management
 * through FXML loading.
 */
public class HommeClientController {
    /** 
     * @param event
     * @throws IOException
     */
    ///gestion de menu
    /**
     * Loads a FXML file, creates a scene and stages it, displaying the content in a new
     * window.
     * 
     * @param event ActionEvent object that triggered the function execution, providing
     * information about the source of the event and any relevant data.
     * 
     * Event: An event object that represents an action event occurred in the JavaFX application.
     * 
     * Properties:
     * 
     * 	- `getSource()`: Returns the source of the event, which is typically a button or
     * a menu item.
     * 	- `getEventType()`: Returns the type of the event, such as `ACTION`, `ENTER`, or
     * `EXIT`.
     */
    @FXML
    void Ocategories(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/CategorieClient.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Loads an FXML file, creates a scene from it, and displays it on the primary stage
     * using the `show()` method.
     * 
     * @param event event that triggered the function, specifically the opening of the
     * SeriesClient.fxml file.
     * 
     * 	- `event`: an instance of `ActionEvent`, indicating that the method has been
     * triggered by a user action.
     */
    @FXML
    void Oseries(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/SeriesClient.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Loads an FXML file, creates a scene and sets it as the scene of a stage. The stage
     * is then shown.
     * 
     * @param event event that triggered the execution of the `Oepisode` method, providing
     * the source of the event as an `ActionEvent`.
     * 
     * 	- `event` is an instance of `ActionEvent`, indicating that the method was invoked
     * by a user action.
     * 	- The source of the event is a `Node` object representing the origin of the event
     * (in this case, the `EpisodeClient` stage).
     */
    @FXML
    void Oepisode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/EpisodeClient.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
