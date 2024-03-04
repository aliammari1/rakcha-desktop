package com.esprit.tests;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

      FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignProduitAdmin.fxml"));
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCategorie.fxml"));
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduitClient.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root,1280,700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Afficher Produit Client" );
        primaryStage.show();

    }
}
