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
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DesignCategorieAdminController {

    @FXML
    private TableView<Categorie> categorie_tableView;

    @FXML
    private TableColumn<Categorie,String> tcDescriptionC;

    @FXML
    private TableColumn<Categorie,Void> tcDeleteC;

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
    private TextField tfRechercheC;

    private List<Categorie> l1=new ArrayList<>();

    @FXML
    void GestionCategorie(ActionEvent event) throws IOException {
        // Charger la nouvelle interface ListCinemaAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignCategorieAdmin.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Gestion d'evenements");
        stage.show();

    }

    @FXML
    void initialize(){

        CategorieService categorieservice=new CategorieService();
        l1=categorieservice.show();
        tfRechercheC.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            List<Categorie> cat;
            cat=recherchercat(l1,newValue);
            showCategorie(cat);
        });
        showCategorie(l1);
        initDeleteColumn();
    }


    @FXML
    void addCategorie(ActionEvent event) {
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
        });

    }



    @FXML
    void updateCategorie(Categorie categorie) {

        String nouveauNom = categorie.getNom_categorie();
        String nouvelleDescription = categorie.getDescription();



        // Enregistrez les modifications dans la base de données en utilisant un service approprié
        CategorieService cs = new CategorieService();
        cs.update(categorie);


    }

    @FXML
    void deleteCategorie(Categorie categorie) {
        Categorie selectedProduct = categorie_tableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            CategorieService ps = new CategorieService();
            ps.delete(selectedProduct);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Categorie supprimée");
            alert.setContentText("Categorie supprimée !");
            alert.show();

            // Rafraîchir la TableView après la suppression
            showCategorie(l1);
        }

    }

    private void initDeleteColumn() {

        // Créer une cellule de la colonne Supprimer
        Callback<TableColumn<Categorie, Void>, TableCell<Categorie, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Categorie, Void> call(final TableColumn<Categorie, Void> param) {
                final TableCell<Categorie, Void> cell = new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {

                        btnDelete.getStyleClass().add("delete-button");
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Categorie categorie = getTableView().getItems().get(getIndex());
                            deleteCategorie(categorie);
                            categorie_tableView.getItems().remove(categorie);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnDelete);
                        }
                    }
                };
                return cell;
            }
        };

        // Définir la cellule de la colonne Supprimer
        tcDeleteC.setCellFactory(cellFactory);

        // Ajouter la colonne Supprimer à la TableView
        categorie_tableView.getColumns().add(tcDeleteC);
    }
    @FXML
    void showCategorie(List<Categorie> listcategorie){
        categorie_tableView.getItems().clear();


        tcNomC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("nom_categorie"));
        tcNomC.setCellFactory(TextFieldTableCell.forTableColumn());
        tcNomC.setOnEditCommit(event -> {
            Categorie categorie = event.getRowValue();
            categorie.setNom_categorie(event.getNewValue());
            updateCategorie(categorie);
        });

        tcDescriptionC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("description"));
        tcDescriptionC.setCellFactory(TextFieldTableCell.forTableColumn());
        tcDescriptionC.setOnEditCommit(event -> {
            Categorie categorie = event.getRowValue();
            categorie.setDescription(event.getNewValue());
            updateCategorie(categorie);
        });


        // Activer l'édition en cliquant sur une ligne
        categorie_tableView.setEditable(true);

        // Gérer la modification du texte dans une cellule et le valider en appuyant sur Enter
        categorie_tableView.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Categorie selectedCategorie = categorie_tableView.getSelectionModel().getSelectedItem();
                if (selectedCategorie != null) {
                    updateCategorie(selectedCategorie);
                }
            }
        });

        // Activer la sélection de cellules
        categorie_tableView.getSelectionModel().setCellSelectionEnabled(true);
        categorie_tableView.getItems().addAll(listcategorie);


    }
    @FXML
    public static List<Categorie> recherchercat(List<Categorie> liste, String recherche) {
        List<Categorie> resultats = new ArrayList<>();

        for (Categorie element : liste) {
            if (element.getNom_categorie() != null && element.getNom_categorie().contains(recherche)) {
                resultats.add(element);
            }
        }

        return resultats;
    }



}
