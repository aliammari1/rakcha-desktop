package com.esprit.controllers;

import com.esprit.models.Categorie;
import com.esprit.models.Produit;
import com.esprit.services.CategorieService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import com.esprit.services.ProduitService;

import java.awt.*;

public class AjouterProduitController {

    @FXML
    private TableView<?> Produit_tableview;

    @FXML
    private TextField descriptionP_textFiled;

    @FXML
    private TextField idP_textFiled;

    @FXML
    private TextField image_textFiled;

    @FXML
    private ComboBox<String> nomC_comboBox;

    @FXML
    private TextField nomP_textFiled;

    @FXML
    private TextField prix_textFiled;

    @FXML
    private TextField quantiteP_textFiled;

   @FXML
    void initialize() {
        CategorieService cs = new CategorieService();
        for (Categorie c : cs.read()) {
            nomC_comboBox.getItems().add(c.getNom_categorie());
        }
    }
    @FXML
    void add_produit(ActionEvent event) {
        ProduitService ps = new ProduitService();

        ps.create(new Produit(nomP_textFiled.getText(), prix_textFiled.getText(), image_textFiled.getText(), descriptionP_textFiled.getText(), new CategorieService().read().get(nomC_comboBox.getSelectionModel().getSelectedIndex()), Integer.parseInt(quantiteP_textFiled.getText())));



    }

}
