package com.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEvenementClient.fxml"));
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/MeteoEvent.fxml"));
        // FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardClientCinema.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignEvenementAdmin.fxml"));
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignSponsorAdmin.fxml"));
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEvenementClient.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }


}
