package com.esprit.controllers;

import com.esprit.models.Cinema;
import com.esprit.services.CinemaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.sql.Blob;

public class AjoutCinemaController {

    @FXML
    private ImageView image;

    @FXML
    private TextField tfAdresse;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfResponsable;

    @FXML
    void addCinema(ActionEvent event) {

        CinemaService cs = new CinemaService();
        cs.create(new Cinema(tfNom.getText(), tfAdresse.getText(), tfResponsable.getText(), (Blob) image.getImage()));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cinéma ajoutée");
        alert.setContentText("Cinéma ajoutée!");
        alert.show ();

    }

}

