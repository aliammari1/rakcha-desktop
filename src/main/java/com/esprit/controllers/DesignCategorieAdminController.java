package com.esprit.controllers;
import com.esprit.models.Categorie;

import com.esprit.services.CategorieService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.Blob;

public class DesignCategorieAdminController {

    @FXML
    private TableView<Categorie> categorie_tableview;

    @FXML
    private TextField SearchBar;


    @FXML
    private TableColumn<Categorie,Void> deleteColumn;

    @FXML
    private TextArea descriptionC_textArea;

    @FXML
    private TextField nomC_textFile;


    @FXML
    private TableColumn<Categorie,String> nomC_tableC;
    @FXML
    private TableColumn<Categorie, String> description_tableC;

    @FXML
    void GestionCategorie(ActionEvent event) throws IOException {
        // Charger la nouvelle interface ListCinemaAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignProduitAdmin.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

    }


    @FXML
    void initialize(){
        afficher_categorie();
        initDeleteColumn();
    }

    @FXML
    void ajouter_categorie(ActionEvent event) {
        CategorieService cs = new CategorieService();
        cs.create(new Categorie( nomC_textFile.getText(), descriptionC_textArea.getText()));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Categorie ajoutée");
        alert.setContentText("Categorie ajoutée !");
        alert.show();
        nomC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("nom_categorie"));
        description_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("description"));
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        list.addAll(cs.read());
        categorie_tableview.setItems(list);
        categorie_tableview.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Categorie selectedUser = categorie_tableview.getSelectionModel().getSelectedItem();
                nomC_textFile.setText(selectedUser.getNom_categorie());
                descriptionC_textArea.setText(selectedUser.getDescription());
            }
        });

    }


    @FXML
    void supprimer_categorie(Categorie categorie) {
        Categorie selectedProduct = categorie_tableview.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            CategorieService ps = new CategorieService();
            ps.delete(selectedProduct);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Categorie supprimé");
            alert.setContentText("Categorie supprimé !");
            alert.show();

            // Rafraîchir la TableView après la suppression
            afficher_categorie();
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
                            supprimer_categorie(categorie);
                            categorie_tableview.getItems().remove(categorie);
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
        deleteColumn.setCellFactory(cellFactory);

        // Ajouter la colonne Supprimer à la TableView
        categorie_tableview.getColumns().add(deleteColumn);
    }

    @FXML
    void modifier_categorie(Categorie categorie) {

        String nouveauNom = categorie.getNom_categorie();
        String nouvelleDescription = categorie.getDescription();



        // Enregistrez les modifications dans la base de données en utilisant un service approprié
        CategorieService ps = new CategorieService();
        ps.update(categorie);


    }


    @FXML
    void afficher_categorie() {


        nomC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("nom_categorie"));
        nomC_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        nomC_tableC.setOnEditCommit(event -> {
            Categorie categorie = event.getRowValue();
            categorie.setNom_categorie(event.getNewValue());
            modifier_categorie(categorie);
        });

        description_tableC.setCellValueFactory(new PropertyValueFactory<Categorie,String>("description"));
        description_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        description_tableC.setOnEditCommit(event -> {
            Categorie categorie = event.getRowValue();
            categorie.setDescription(event.getNewValue());
            modifier_categorie(categorie);
        });


        ObservableList<Categorie> list = FXCollections.observableArrayList();
        CategorieService cs = new CategorieService();
        list.addAll(cs.read());
        categorie_tableview.setItems(list);

        // Activer l'édition en cliquant sur une ligne
        categorie_tableview.setEditable(true);

        // Gérer la modification du texte dans une cellule et le valider en appuyant sur Enter
        categorie_tableview.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Categorie selectedCategorie = categorie_tableview.getSelectionModel().getSelectedItem();
                if (selectedCategorie != null) {
                    modifier_categorie(selectedCategorie);
                }
            }
        });

        // Activer la sélection de cellules
        categorie_tableview.getSelectionModel().setCellSelectionEnabled(true);


    }


}
