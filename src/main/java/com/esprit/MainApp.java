package com.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * MainApp class for the RAKCHA JavaFX desktop application.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class MainApp extends Application {
    /**
     * Launches the JavaFX application using the provided command-line arguments.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(final String[] args) {
            Application.launch(args);

    }


    /**
     * Initialize the primary stage with the login UI and display it.
     *
     * @param primaryStage the primary JavaFX Stage to set the login scene on and display
     * @throws Exception if the login FXML resource cannot be loaded or the scene cannot be created
     */
    @Override
    /**
     * Performs start operation.
     *
     * @return the result of the operation
     */
    public void start(final Stage primaryStage) throws Exception {
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Login.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}