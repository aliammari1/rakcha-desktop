package com.esprit.controllers;

import com.esprit.models.Categorie;
import com.esprit.services.CategorieService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class AjoutSponsorController {

    @FXML
    private TextField tfID;

    @FXML
    private TextField tfLogo;

    @FXML
    private TextField tfNom;


    @FXML
    void ajouter_categorie(ActionEvent event) {
        CategorieService ps = new CategorieService();
        ps.add(new Categorie( Integer.parseInt(tfID.getText()), tfNom.getText(), tfLogo.getText()));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Categorie ajoutée");
        alert.setContentText("Categorie ajoutée !");
        alert.show();

    }

}
