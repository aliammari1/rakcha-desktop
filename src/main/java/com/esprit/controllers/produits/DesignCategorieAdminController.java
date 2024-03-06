package com.esprit.controllers.produits;

import com.esprit.models.produits.Categorie_Produit;

import com.esprit.services.produits.CategorieService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
    private TableView<Categorie_Produit> categorie_tableview;

    @FXML
    private TextField SearchBar;


    @FXML
    private TableColumn<Categorie_Produit,Void> deleteColumn;

    @FXML
    private TextArea descriptionC_textArea;

    @FXML
    private TextField nomC_textFile;


    @FXML
    private TableColumn<Categorie_Produit,String> nomC_tableC;
    @FXML
    private TableColumn<Categorie_Produit, String> description_tableC;


    @FXML
    void GestionCategorie(ActionEvent event) throws IOException {

            // Charger la nouvelle interface ListproduitAdmin.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignProduitAdmin.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Gestion des categories");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        }





    @FXML
    void initialize(){


        afficher_categorie();
        initDeleteColumn();
    }

    @FXML
    void ajouter_categorie(ActionEvent event) {
        // Récupérer les valeurs des champs de saisie
        String nomCategorie = nomC_textFile.getText().trim();
        String descriptionCategorie = descriptionC_textArea.getText().trim();

        // Vérifier si les champs sont vides
        if (nomCategorie.isEmpty() || descriptionCategorie.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.show();
            return; // Arrêter l'exécution de la méthode si les champs sont vides
        }

        // Vérifier si la description a au moins 20 caractères
        if (descriptionCategorie.length() < 20) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("La description doit contenir au moins 20 caractères.");
            alert.show();
            return; // Arrêter l'exécution de la méthode si les champs sont vides
        }

        // Créer l'objet Categorie
        CategorieService cs = new CategorieService();
        Categorie_Produit nouvelleCategorieProduit = new Categorie_Produit(nomCategorie, descriptionCategorie);

        // Ajouter le nouveau categorie à la liste existante
        cs.create(nouvelleCategorieProduit);
        categorie_tableview.getItems().add(nouvelleCategorieProduit);

        // Rafraîchir la TableView
        categorie_tableview.refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Categorie ajouté");
        alert.setContentText("Categorie ajouté !");
        alert.show();
    }





    private void initDeleteColumn() {

        Callback<TableColumn<Categorie_Produit, Void>, TableCell<Categorie_Produit, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Categorie_Produit, Void> call(final TableColumn<Categorie_Produit, Void> param) {
                final TableCell<Categorie_Produit, Void> cell = new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        btnDelete.getStyleClass().add("delete-button");
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Categorie_Produit categorieProduit = getTableView().getItems().get(getIndex());
                            CategorieService cs = new CategorieService();
                            cs.delete(categorieProduit);

                            categorie_tableview.getItems().remove(categorieProduit);
                            categorie_tableview.refresh();

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

        deleteColumn.setCellFactory(cellFactory);
        categorie_tableview.getColumns().add(deleteColumn);
    }




    @FXML
    void modifier_categorie(Categorie_Produit categorieProduit) {

        String nouveauNom = categorieProduit.getNom_categorie();
        String nouvelleDescription = categorieProduit.getDescription();

        // Enregistrez les modifications dans la base de données en utilisant un service approprié
        CategorieService ps = new CategorieService();
        ps.update(categorieProduit);


    }


    @FXML
    void afficher_categorie(){



        nomC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie_Produit,String>("nom_categorie"));
        nomC_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        nomC_tableC.setOnEditCommit(event -> {
            Categorie_Produit categorieProduit = event.getRowValue();
            categorieProduit.setNom_categorie(event.getNewValue());
            modifier_categorie(categorieProduit);
        });

        description_tableC.setCellValueFactory(new PropertyValueFactory<Categorie_Produit,String>("description"));
        description_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        description_tableC.setOnEditCommit(event -> {
            Categorie_Produit categorieProduit = event.getRowValue();
            categorieProduit.setDescription(event.getNewValue());
            modifier_categorie(categorieProduit);
        });


        // Activer l'édition en cliquant sur une ligne
        categorie_tableview.setEditable(true);

        // Gérer la modification du texte dans une cellule et le valider en appuyant sur Enter
        categorie_tableview.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Categorie_Produit selectedCategorieProduit = categorie_tableview.getSelectionModel().getSelectedItem();
                if (selectedCategorieProduit != null) {
                    modifier_categorie(selectedCategorieProduit);
                }
            }
        });
        // Utiliser une ObservableList pour stocker les éléments
        ObservableList<Categorie_Produit> list = FXCollections.observableArrayList();
        CategorieService cs = new CategorieService();
        list.addAll(cs.read());
        categorie_tableview.setItems(list);

        // Activer la sélection de cellules
        categorie_tableview.getSelectionModel().setCellSelectionEnabled(true);

    }
    @FXML
    public static List<Categorie_Produit> recherchercat(List<Categorie_Produit> liste, String recherche) {
        List<Categorie_Produit> resultats = new ArrayList<>();

        for (Categorie_Produit element : liste) {
            if (element.getNom_categorie() != null && element.getNom_categorie().contains(recherche)) {
                resultats.add(element);
            }
        }

        return resultats;
    }



}
