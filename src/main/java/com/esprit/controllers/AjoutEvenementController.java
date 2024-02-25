package com.esprit.controllers;

import com.esprit.models.Categorie;
import com.esprit.models.Evenement;
import com.esprit.services.CategorieService;
import com.esprit.services.EvenementService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.Date;

public class AjoutEvenementController {

    @FXML
    private ComboBox<String> cbCategorie;

    @FXML
    private DatePicker dpDD;

    @FXML
    private DatePicker dpDF;

    @FXML
    private TextField tfDescription;

    @FXML
    private TextField tfEtat;

    @FXML
    private TextField tfID;

    @FXML
    private TextField tfLieu;

    @FXML
    private TextField tfNomEvenement;

    @FXML
    void initialize() {
        CategorieService cs = new CategorieService();

        for (Categorie c : cs.show()) {
            cbCategorie.getItems().add(c.getNom_categorie());
        }


    }

    @FXML
    void addEvenement(ActionEvent event) {
        EvenementService es = new EvenementService();
        es.add(new Evenement(Integer.parseInt(tfID.getText()),tfNomEvenement.getText(), Date.valueOf(dpDD.getValue()),Date.valueOf(dpDF.getValue()),tfLieu.getText(),new CategorieService().getCategorieByNom(String.valueOf(cbCategorie.getValue())),tfEtat.getText(),tfDescription.getText()));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Evenement ajouté");
        alert.setContentText("Evenement ajouté !");
        alert.show();
    }

}
