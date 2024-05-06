package com.esprit.controllers;

import com.esprit.controllers.evenements.AffichageEvenementClientController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ClientSideBarController {

    @FXML
    private Button cinemaButton;
    @FXML
    private Button eventButton;
    @FXML
    private Button movieButton;
    @FXML
    private Button productButton;
    @FXML
    private Button serieButton;


    @FXML
    void switchToEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEvenementClient.fxml"));
            Parent root = loader.load();
            AffichageEvenementClientController seanceController = loader.getController();
            Stage stage = (Stage) eventButton.getScene().getWindow();
            System.out.println("---------" + stage.getUserData());
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchToMovies(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/filmuser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) movieButton.getScene().getWindow();
            System.out.println("---------" + stage.getUserData());
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchToProducts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduitClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) productButton.getScene().getWindow();
            System.out.println("---------" + stage.getUserData());
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchToSeries(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SeriesClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) serieButton.getScene().getWindow();
            System.out.println("---------" + stage.getUserData());
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchtcinema(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardClientCinema.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cinemaButton.getScene().getWindow();
            System.out.println("---------" + stage.getUserData());
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
