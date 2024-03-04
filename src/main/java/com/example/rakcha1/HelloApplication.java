package com.example.rakcha1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //afiicahge cote client
       //FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/SeriesClient.fxml"));
        //affichage cote backend
      FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/Categorie-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Rakcha");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}