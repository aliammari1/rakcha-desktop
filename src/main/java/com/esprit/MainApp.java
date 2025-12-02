package com.esprit;

import com.esprit.controllers.SplashScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * MainApp class for the RAKCHA JavaFX desktop application.
 * Features a cinematic splash screen experience before loading the main application.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class MainApp extends Application {

    private Stage mainStage;

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
     * Starts the application with a cinematic splash screen.
     *
     * @return the result of the operation
     */
    public void start(final Stage primaryStage) throws Exception {
        this.mainStage = primaryStage;

        // Configure main stage (hidden initially)
        mainStage.setTitle("RAKCHA - Cinema Experience");
        try {
            mainStage.getIcons().add(new Image(getClass().getResourceAsStream("/Logo.png")));
        } catch (Exception e) {
            System.out.println("Could not load application icon: " + e.getMessage());
        }

        // Create and show splash screen
        showSplashScreen();
    }

    /**
     * Displays the cinematic splash screen.
     */
    private void showSplashScreen() throws Exception {
        // Create splash stage
        Stage splashStage = new Stage();
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.setTitle("RAKCHA");

        try {
            splashStage.getIcons().add(new Image(getClass().getResourceAsStream("/Logo.png")));
        } catch (Exception e) {
            System.out.println("Could not load splash icon: " + e.getMessage());
        }

        // Load splash screen FXML
        FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/ui/splash/SplashScreen.fxml"));
        Parent splashRoot = splashLoader.load();

        // Get controller and set stage references
        SplashScreenController splashController = splashLoader.getController();
        splashController.setSplashStage(splashStage);
        splashController.setMainStage(mainStage);

        // Create scene with transparent fill for cinematic effect
        Scene splashScene = new Scene(splashRoot, 1400, 800);
        splashScene.setFill(Color.BLACK);

        splashStage.setScene(splashScene);
        splashStage.centerOnScreen();
        splashStage.setResizable(false);
        splashStage.show();
    }

}
