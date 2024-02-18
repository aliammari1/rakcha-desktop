package com.esprit.controllers;

import com.esprit.models.Categorie;
import com.esprit.models.Produit;
import com.esprit.models.Produit;
import com.esprit.services.CategorieService;
import com.esprit.services.ProduitService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.esprit.services.ProduitService;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.sql.Blob;

public class AjouterProduitController {

    @FXML
    private TableColumn<Produit, String> PrixP_tableC;

    @FXML
    private TableView<Produit> Produit_tableview;

    @FXML
    private TableColumn<Produit, String> descriptionP_tableC;

    @FXML
    private TextField descriptionP_textFiled;

    @FXML
    private TableColumn<Produit, Integer> idP_tableC;

    @FXML
    private TextField idP_textFiled;

    @FXML
    private TableColumn<Produit, Blob> image_tableC;


    @FXML
    private TextField image_textFiled;

    @FXML
    private ComboBox<String> nomC_comboBox;

    @FXML
    private TableColumn<Produit,Categorie> nomCP_tableC;

    @FXML
    private TableColumn<Produit,String> nomP_tableC;

    @FXML
    private TextField nomP_textFiled;

    @FXML
    private TextField prix_textFiled;

    @FXML
    private TableColumn<Produit, Integer> quantiteP_tableC;

    @FXML
    private TextField quantiteP_textFiled;


    @FXML
    void initialize() {
        //ProduitService cs = new ProduitService();
        CategorieService cs = new CategorieService();

        for (Categorie c : cs.read()) {
            nomC_comboBox.getItems().add(c.getNom_categorie());
        }
        afficher_produit();





    }

    @FXML
    void add_produit(ActionEvent event) {
        ProduitService ps = new ProduitService();

        ps.create(new Produit(nomP_textFiled.getText(), prix_textFiled.getText(), image_textFiled.getText(), descriptionP_textFiled.getText(), new CategorieService().read().get(nomC_comboBox.getSelectionModel().getSelectedIndex()), Integer.parseInt(quantiteP_textFiled.getText())));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Produit ajoutée");
        alert.setContentText("Produit ajoutée !");
        alert.show();
        idP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("id_produit"));
        nomCP_tableC.setCellValueFactory(new PropertyValueFactory<Produit,Categorie>("id_categorieProduit"));
        nomP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("nom"));
        PrixP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("prix"));
        image_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Blob>("image"));
        descriptionP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("description"));
        quantiteP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("quantiteP"));
        ObservableList<Produit> list = FXCollections.observableArrayList();
        list.addAll(ps.read());
        Produit_tableview.setItems(list);
        Produit_tableview.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Produit selectedUser = Produit_tableview.getSelectionModel().getSelectedItem();
                idP_textFiled.setText(String.valueOf(selectedUser.getId_produit()));
                nomCP_tableC.setText(selectedUser.getCategorie().getNom_categorie());
                nomP_tableC.setText(selectedUser.getNom());
                prix_textFiled.setText(selectedUser.getPrix());
                image_textFiled.setText(selectedUser.getImage().toString());
                descriptionP_textFiled.setText(selectedUser.getDescription());
                quantiteP_textFiled.setText(String.valueOf(selectedUser.getQuantiteP()));
            }
        });





    }


    @FXML
    void afficher_produit() {

        idP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("id_produit"));
        nomCP_tableC.setCellValueFactory(new PropertyValueFactory<Produit,Categorie>("id_categorieProduit"));
        nomP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("nom"));
        PrixP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("prix"));
        image_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Blob>("image"));
        descriptionP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("description"));
        quantiteP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("quantiteP"));
        ObservableList<Produit> list = FXCollections.observableArrayList();
        ProduitService ps = new ProduitService();
        CategorieService cs = new CategorieService();
        list.addAll(ps.read());
        Produit_tableview.setItems(list);
        Produit_tableview.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Produit selectedUser = Produit_tableview.getSelectionModel().getSelectedItem();
                idP_textFiled.setText(String.valueOf(selectedUser.getId_produit()));
                nomP_tableC.setText(selectedUser.getNom());
                nomCP_tableC.setText(selectedUser.getCategorie().getNom_categorie());
                prix_textFiled.setText(selectedUser.getPrix());
                image_textFiled.setText(selectedUser.getImage().toString());
                descriptionP_textFiled.setText(selectedUser.getDescription());
                quantiteP_textFiled.setText(String.valueOf(selectedUser.getQuantiteP()));

            }
        });




        }
    @FXML
    void modifier_produit(ActionEvent event) {
     ProduitService ps = new ProduitService();

        ps.update(new Produit(Integer.parseInt(idP_textFiled.getText()), nomP_textFiled.getText(), prix_textFiled.getText(), image_textFiled.getText(), descriptionP_textFiled.getText(),new CategorieService().read().get(nomC_comboBox.getSelectionModel().getSelectedIndex()), Integer.parseInt(quantiteP_textFiled.getText())));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Produit modifiée");
        alert.setContentText("Produit modifiée !");
        alert.show();
        idP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("id_produit"));
        nomCP_tableC.setCellValueFactory(new PropertyValueFactory<Produit,Categorie>("id_categorieProduit"));
        nomP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("nom"));
        PrixP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("prix"));
        image_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Blob>("image"));
        descriptionP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("description"));
        quantiteP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("quantiteP"));
        ObservableList<Produit> list = FXCollections.observableArrayList();
        list.addAll(ps.read());
        Produit_tableview.setItems(list);
        Produit_tableview.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Produit selectedUser = Produit_tableview.getSelectionModel().getSelectedItem();
                idP_textFiled.setText(String.valueOf(selectedUser.getId_produit()));
                nomCP_tableC.setText(selectedUser.getCategorie().getNom_categorie());
                nomP_tableC.setText(selectedUser.getNom());
                prix_textFiled.setText(selectedUser.getPrix());
                image_textFiled.setText(selectedUser.getImage().toString());
                descriptionP_textFiled.setText(selectedUser.getDescription());
                quantiteP_textFiled.setText(String.valueOf(selectedUser.getQuantiteP()));
            }
        });



    }

    @FXML
    void supprimer_produit(ActionEvent event) {
        ProduitService ps = new ProduitService();
        ps.delete(new Produit( Integer.parseInt(idP_textFiled.getText())));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Produit supprimée");
        alert.setContentText("Produit supprimée !");
        alert.show();
        idP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("id_produit"));
        nomCP_tableC.setCellValueFactory(new PropertyValueFactory<Produit,Categorie>("id_categorieProduit"));
        nomP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("nom"));
        PrixP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("prix"));
        image_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Blob>("image"));
        descriptionP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("description"));
        quantiteP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("quantiteP"));
        ObservableList<Produit> list = FXCollections.observableArrayList();
        list.addAll(ps.read());
        Produit_tableview.setItems(list);
        Produit_tableview.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Produit selectedUser = Produit_tableview.getSelectionModel().getSelectedItem();
                idP_textFiled.setText(String.valueOf(selectedUser.getId_produit()));
                nomCP_tableC.setText(selectedUser.getCategorie().getNom_categorie());
                nomP_tableC.setText(selectedUser.getNom());
                prix_textFiled.setText(selectedUser.getPrix());
                image_textFiled.setText(selectedUser.getImage().toString());
                descriptionP_textFiled.setText(selectedUser.getDescription());
                quantiteP_textFiled.setText(String.valueOf(selectedUser.getQuantiteP()));
            }
        });

    }


    }



