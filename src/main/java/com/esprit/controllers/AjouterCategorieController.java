package com.esprit.controllers;

import com.esprit.models.Categorie;
import com.esprit.services.CategorieService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AjouterCategorieController {

    @FXML
    private TableView<Categorie> categorie_tableView;

    @FXML
    private TableColumn<Categorie,String> descriptionC_tableC;

    @FXML
    private TextField descriptionC_textFile;

    @FXML
    private TableColumn<Categorie,Integer> idC_tableC;

    @FXML
    private TextField idC_textFile;

    @FXML
    private TableColumn<Categorie,String> nomC_tableC;

    @FXML
    private TextField nomC_textFile;

    @FXML
    void ajouter_categorie(ActionEvent event) {
        CategorieService cs = new CategorieService();
        cs.create(new Categorie( nomC_textFile.getText(), descriptionC_textFile.getText()));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Categorie ajoutée");
        alert.setContentText("Categorie ajoutée !");
        alert.show();
        idC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,Integer>("id_categorie"));
        nomC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("nom_categorie"));
        descriptionC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("description"));
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        list.addAll(cs.read());
        categorie_tableView.setItems(list);
        categorie_tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Categorie selectedUser = categorie_tableView.getSelectionModel().getSelectedItem();
                idC_textFile.setText(String.valueOf(selectedUser.getId_categorie()));
                nomC_textFile.setText(selectedUser.getNom_categorie());
                descriptionC_textFile.setText(selectedUser.getDescription());
            }
        });


    }

    @FXML
    void modifier_categorie(ActionEvent event) {
        CategorieService cs = new CategorieService();
        cs.update(new Categorie(Integer.parseInt(idC_textFile.getText()), nomC_textFile.getText(), descriptionC_textFile.getText()));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Categorie modifiée");
        alert.setContentText("Categorie modifiée !");
        alert.show();
        idC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,Integer>("id_categorie"));
        nomC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("nom_categorie"));
        descriptionC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("description"));
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        list.addAll(cs.read());
        categorie_tableView.setItems(list);
        categorie_tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Categorie selectedUser = categorie_tableView.getSelectionModel().getSelectedItem();
                idC_textFile.setText(String.valueOf(selectedUser.getId_categorie()));
                nomC_textFile.setText(selectedUser.getNom_categorie());
                descriptionC_textFile.setText(selectedUser.getDescription());
            }
        });



    }

    @FXML
    void supprimer_categorie(ActionEvent event) {
        CategorieService cs = new CategorieService();
        cs.delete(new Categorie(Integer.parseInt(idC_textFile.getText()), nomC_textFile.getText(), descriptionC_textFile.getText()));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Categorie supprimée");
        alert.setContentText("Categorie supprimée !");
        alert.show();
        idC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,Integer>("id_categorie"));
        nomC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("nom_categorie"));
        descriptionC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("description"));
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        list.addAll(cs.read());
        categorie_tableView.setItems(list);
        categorie_tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Categorie selectedUser = categorie_tableView.getSelectionModel().getSelectedItem();
                idC_textFile.setText(String.valueOf(selectedUser.getId_categorie()));
                nomC_textFile.setText(selectedUser.getNom_categorie());
                descriptionC_textFile.setText(selectedUser.getDescription());
            }
        });

    }



    @FXML
    void afficher_categorie() {

        idC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,Integer>("id_categorie"));
        nomC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("nom_categorie"));
        descriptionC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("description"));
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        CategorieService cs = new CategorieService();
        list.addAll(cs.read());
        categorie_tableView.setItems(list);
        categorie_tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
               Categorie selectedUser = categorie_tableView.getSelectionModel().getSelectedItem();
                idC_textFile.setText(String.valueOf(selectedUser.getId_categorie()));
                nomC_textFile.setText(selectedUser.getNom_categorie());
                descriptionC_textFile.setText(selectedUser.getDescription());
            }
        });

    }
    @FXML
    void initialize() {
        afficher_categorie();
    }

}
