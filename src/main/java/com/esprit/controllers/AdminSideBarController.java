package com.esprit.controllers;

import com.esprit.controllers.cinemas.DashboardAdminController;
import com.esprit.controllers.evenements.DesignCategorieAdminController;
import com.esprit.controllers.films.FilmController;
import com.esprit.controllers.produits.DesignProduitAdminContoller;
import com.esprit.controllers.series.SerieClientController;
import com.esprit.models.users.Admin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminSideBarController {

    Admin admin;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignEvenementAdmin.fxml"));
            DesignCategorieAdminController seanceController = loader.getController();
            //seanceController.setData(admin);
            Parent root = loader.load();
            Stage stage = (Stage) eventButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void switchToMovies(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCommande.fxml"));
            FilmController seanceController = loader.getController();
            // seanceController.setData(admin);
            Parent root = loader.load();
            Stage stage = (Stage) movieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void switchToProducts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignProduitAdmin.fxml"));
            DesignProduitAdminContoller seanceController = loader.getController();
            // seanceController.setData(admin);
            Parent root = loader.load();
            Stage stage = (Stage) productButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void switchToSeries(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SeriesClient.fxml"));
            SerieClientController seanceController = loader.getController();
            // seanceController.setData(admin);
            Parent root = loader.load();
            Stage stage = (Stage) serieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    @FXML
    void switchtcinema(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardAdminCinema.fxml"));
            DashboardAdminController seanceController = loader.getController();
            //  seanceController.setData(admin);
            Parent root = loader.load();
            Stage stage = (Stage) cinemaButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
