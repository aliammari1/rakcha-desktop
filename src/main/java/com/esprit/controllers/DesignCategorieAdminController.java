package com.esprit.controllers;

import com.esprit.models.Categorie_evenement;
import com.esprit.services.CategorieService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DesignCategorieAdminController {

    @FXML
    private TableView<Categorie_evenement> categorie_tableView;

    @FXML
    private TableColumn<Categorie_evenement, String> tcDescriptionC;

    @FXML
    private TableColumn<Categorie_evenement, Void> tcDeleteC;

    @FXML
    private TextArea tfDescriptionC;

    @FXML
    private TableColumn<Categorie_evenement, String> tcNomC;

    @FXML
    private TextField tfNomC;

    @FXML
    private TextField tfRechercheC;

    @FXML
    void GestionCategorie(ActionEvent event) throws IOException {
        // Charger la nouvelle interface ListCinemaAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignCategorieAdmin.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Gestion d'evenements");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();

    }

    @FXML
    void initialize() {


        afficher_categorie();
        initDeleteColumn();
    }


    @FXML
    void ajouter_categorie(ActionEvent event) {
        // Récupérer les valeurs des champs de saisie
        String nomCategorie = tfNomC.getText().trim();
        String descriptionCategorie = tfDescriptionC.getText().trim();

        // Vérifier si les champs sont vides
        if (nomCategorie.isEmpty() || descriptionCategorie.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.show();
            return; // Arrêter l'exécution de la méthode si les champs sont vides
        }

        // Créer l'objet Categorie
        CategorieService cs = new CategorieService();
        Categorie_evenement nouvelleCategorieEvenement = new Categorie_evenement(nomCategorie, descriptionCategorie);

        // Ajouter le nouveau categorie à la liste existante
        cs.add(nouvelleCategorieEvenement);
        categorie_tableView.getItems().add(nouvelleCategorieEvenement);

        // Rafraîchir la TableView
        categorie_tableView.refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Categorie ajoutée");
        alert.setContentText("Categorie ajoutée !");
        alert.show();
    }

    private void initDeleteColumn() {

        Callback<TableColumn<Categorie_evenement, Void>, TableCell<Categorie_evenement, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Categorie_evenement, Void> call(final TableColumn<Categorie_evenement, Void> param) {
                final TableCell<Categorie_evenement, Void> cell = new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        btnDelete.getStyleClass().add("delete-button");
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Categorie_evenement categorieEvenement = getTableView().getItems().get(getIndex());
                            CategorieService cs = new CategorieService();
                            cs.delete(categorieEvenement);

                            categorie_tableView.getItems().remove(categorieEvenement);
                            categorie_tableView.refresh();

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

        tcDeleteC.setCellFactory(cellFactory);
        categorie_tableView.getColumns().add(tcDeleteC);
    }


    @FXML
    void modifier_categorie(Categorie_evenement categorieEvenement) {

        String nouveauNom = categorieEvenement.getNom_categorie();
        String nouvelleDescription = categorieEvenement.getDescription();

        // Enregistrez les modifications dans la base de données en utilisant un service approprié
        CategorieService cs = new CategorieService();
        cs.update(categorieEvenement);


    }

    @FXML
    void afficher_categorie(){



        tcNomC.setCellValueFactory(new PropertyValueFactory<Categorie_evenement,String>("nom_categorie"));
        tcNomC.setCellFactory(TextFieldTableCell.forTableColumn());
        tcNomC.setOnEditCommit(event -> {
            Categorie_evenement categorieEvenement = event.getRowValue();
            categorieEvenement.setNom_categorie(event.getNewValue());
            modifier_categorie(categorieEvenement);
        });

        tcDescriptionC.setCellValueFactory(new PropertyValueFactory<Categorie_evenement,String>("description"));
        tcDescriptionC.setCellFactory(TextFieldTableCell.forTableColumn());
        tcDescriptionC.setOnEditCommit(event -> {
            Categorie_evenement categorieEvenement = event.getRowValue();
            categorieEvenement.setDescription(event.getNewValue());
            modifier_categorie(categorieEvenement);
        });


        // Activer l'édition en cliquant sur une ligne
        categorie_tableView.setEditable(true);

        // Gérer la modification du texte dans une cellule et le valider en appuyant sur Enter
        categorie_tableView.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Categorie_evenement selectedCategorieEvenement = categorie_tableView.getSelectionModel().getSelectedItem();
                if (selectedCategorieEvenement != null) {
                    modifier_categorie(selectedCategorieEvenement);
                }
            }
        });
        // Utiliser une ObservableList pour stocker les éléments
        ObservableList<Categorie_evenement> list = FXCollections.observableArrayList();
        CategorieService cs = new CategorieService();
        list.addAll(cs.show());
        categorie_tableView.setItems(list);

        // Activer la sélection de cellules
        categorie_tableView.getSelectionModel().setCellSelectionEnabled(true);

    }

    @FXML
    public static List<Categorie_evenement> recherchercat(List<Categorie_evenement> liste, String recherche) {
        List<Categorie_evenement> resultats = new ArrayList<>();

        for (Categorie_evenement element : liste) {
            if (element.getNom_categorie() != null && element.getNom_categorie().contains(recherche)) {
                resultats.add(element);
            }
        }

        return resultats;
    }





}