package com.esprit.controllers;

import com.esprit.controllers.cinemas.DashboardResponsableController;
import com.esprit.controllers.films.ActorController;
import com.esprit.controllers.films.FilmController;
import com.esprit.controllers.series.CategorieController;
import com.esprit.models.users.Client;
import com.esprit.models.users.Responsable_de_cinema;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ResponsableDeCinemaSideBarController {

    Responsable_de_cinema resp;
    @FXML
    private Button actorButton;
    @FXML
    private Button cinemaButton;
    @FXML
    private Button filmCategorieButton;
    @FXML
    private Button movieButton;
    @FXML
    private Button seanceButton;


    @FXML
    void switchToActor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceActor.fxml"));
            ActorController seanceController = loader.getController();
            //seanceController.setData(resp);
            Parent root = loader.load();
            Stage stage = (Stage) actorButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void switchToCinema(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardResponsableCinema.fxml"));
            DashboardResponsableController seanceController = loader.getController();
            seanceController.setData(resp);
            Parent root = loader.load();
            Stage stage = (Stage) cinemaButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void switchToFilmCategorie(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceCategory.fxml"));
            CategorieController seanceController = loader.getController();
            //  seanceController.setData(resp);
            Parent root = loader.load();
            Stage stage = (Stage) filmCategorieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void switchToMovies(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceFilm.fxml"));
            FilmController seanceController = loader.getController();
            //seanceController.setData(resp);
            Parent root = loader.load();
            Stage stage = (Stage) movieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void switchToSeances(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardResponsableCinema.fxml"));
            DashboardResponsableController seanceController = loader.getController();
            seanceController.setData(resp);
            Parent root = loader.load();
            Stage stage = (Stage) seanceButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void setData(Responsable_de_cinema resp) {
        this.resp = resp;
    }

}
