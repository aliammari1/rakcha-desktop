package com.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardAdminController {

    @FXML
    void afficherCinemas(ActionEvent event) throws IOException {
        // Charger la nouvelle interface ListCinemaAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListCinemaAdmin.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }
}
