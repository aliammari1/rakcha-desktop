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


public class AjoutCategorieController {

    @FXML
    private TableView<Categorie> categorie_tableView;

    @FXML
    private TableColumn<Categorie,String> tcDescriptionC;

    @FXML
    private TextField tfDescriptionC;

    @FXML
    private TableColumn<Categorie,Integer> tcIDC;

    @FXML
    private TextField tfIDC;

    @FXML
    private TableColumn<Categorie,String> tcNomC;

    @FXML
    private TextField tfNomC;

    @FXML
    void ajouter_categorie(ActionEvent event) {
        CategorieService cs = new CategorieService();
        cs.add(new Categorie( Integer.parseInt(tfIDC.getText()), tfNomC.getText(), tfDescriptionC.getText()));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Categorie ajoutée");
        alert.setContentText("Categorie ajoutée !");
        alert.show();
        tcIDC.setCellValueFactory(new PropertyValueFactory<Categorie,Integer>("id_categorie"));
        tcNomC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("nom_categorie"));
        tcDescriptionC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("description"));
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        list.addAll(cs.show());
        categorie_tableView.setItems(list);
        categorie_tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Categorie selectedUser = categorie_tableView.getSelectionModel().getSelectedItem();
                tfIDC.setText(String.valueOf(selectedUser.getId_categorie()));
                tfNomC.setText(selectedUser.getNom_categorie());
                tfDescriptionC.setText(selectedUser.getDescription());
            }
        }

    }



    @FXML
    void addCategorie() {

        tcIDC.setCellValueFactory(new PropertyValueFactory<Categorie,Integer>("id_categorie"));
        tfNomC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("nom_categorie"));
        tfDescriptionC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("description"));
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        CategorieService cs = new CategorieService();
        list.addAll(cs.show());
        categorie_tableView.setItems(list);
        categorie_tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Categorie selectedUser = categorie_tableView.getSelectionModel().getSelectedItem();
                tfIDC.setText(String.valueOf(selectedUser.getId_categorie()));
                tfNomC.setText(selectedUser.getNom_categorie());
                tfDescriptionC.setText(selectedUser.getDescription());
            }
        });

    }

    @FXML
    void updateCategorie(ActionEvent event) {
        CategorieService cs = new CategorieService();
        cs.update(new Categorie(Integer.parseInt(tfIDC.getText()), tfNomC.getText(), tfDescriptionC.getText()));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Categorie modifiée");
        alert.setContentText("Categorie modifiée !");
        alert.show();
        tcIDC.setCellValueFactory(new PropertyValueFactory<Categorie,Integer>("id_categorie"));
        tcNomC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("nom_categorie"));
        tcDescriptionC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("description"));
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        list.addAll(cs.show());
        categorie_tableView.setItems(list);
        categorie_tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Categorie selectedUser = categorie_tableView.getSelectionModel().getSelectedItem();
                tfIDC.setText(String.valueOf(selectedUser.getId_categorie()));
                tcNomC.setText(selectedUser.getNom_categorie());
                tcDescriptionC.setText(selectedUser.getDescription());
            }
        });



    }

    @FXML
    void deleteCategorie(ActionEvent event) {
        CategorieService cs = new CategorieService();
        cs.delete(new Categorie(Integer.parseInt(tfIDC.getText()), tfNomC.getText(), tfDescriptionC.getText()));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Categorie supprimée");
        alert.setContentText("Categorie supprimée !");
        alert.show();
        tcIDC.setCellValueFactory(new PropertyValueFactory<Categorie,Integer>("id_categorie"));
        tcNomC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("nom_categorie"));
        tcDescriptionC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("description"));
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        list.addAll(cs.show());
        categorie_tableView.setItems(list);
        categorie_tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Categorie selectedUser = categorie_tableView.getSelectionModel().getSelectedItem();
                tfIDC.setText(String.valueOf(selectedUser.getId_categorie()));
                tfNomC.setText(selectedUser.getNom_categorie());
                tfDescriptionC.setText(selectedUser.getDescription());
            }
        });

}
}