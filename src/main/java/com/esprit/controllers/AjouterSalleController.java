package com.esprit.controllers;

import com.esprit.models.Salle;
import com.esprit.services.SalleService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import static java.lang.Integer.parseInt;


public class AjouterSalleController {

    @FXML
    private TextField tfNbrPlaces;

    @FXML
    private TextField tfNomSalle;

    private int cinemaId;

    public void initData(int cinemaId) {
        this.cinemaId = cinemaId;
    }

    @FXML
    void AjouterSalle(ActionEvent event) {
        SalleService ss = new SalleService();
        ss.create(new Salle( cinemaId, parseInt(tfNbrPlaces.getText()), tfNomSalle.getText()));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Salle ajoutée");
        alert.setContentText("Salle ajoutée !");
        alert.show();
    }

}

