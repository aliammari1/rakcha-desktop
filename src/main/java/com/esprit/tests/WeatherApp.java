package com.esprit.tests;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

//rebase
public class WeatherApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/MeteoEvent.fxml"));

            primaryStage.setTitle("Weather");

            primaryStage.getIcons().add(new Image("/images/icon.png"));
            primaryStage.setScene(new Scene(root, 1050, 670));
            primaryStage.getScene().getStylesheets().addAll(getClass().getResource("/styles/style.css").toExternalForm());
            primaryStage.show();
            primaryStage.setResizable(false);
            primaryStage.sizeToScene();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
