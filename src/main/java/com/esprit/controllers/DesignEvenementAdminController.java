package com.esprit.controllers;

import com.esprit.models.Categorie_evenement;
import com.esprit.models.Evenement;
import com.esprit.services.CategorieService;
import com.esprit.services.EvenementService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.w3c.dom.events.EventException;
import com.esprit.utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import java.sql.Connection;
import java.io.*;

import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

public class DesignEvenementAdminController {

    @FXML
    private ComboBox<String> cbCategorie;

    @FXML
    private DatePicker dpDD;

    @FXML
    private DatePicker dpDF;

    @FXML
    private TableColumn<Evenement, String> tcCategorieE;

    @FXML
    private TableColumn<Evenement, Date> tcDDE;

    @FXML
    private TableColumn<Evenement, Date> tcDFE;

    @FXML
    private TableColumn<Evenement, Void> tcDeleteE;

    @FXML
    private TableColumn<Evenement, String> tcDescriptionE;

    @FXML
    private TableColumn<Evenement, String> tcEtatE;

    @FXML
    private TableColumn<Evenement, String> tcLieuE;

    @FXML
    private TableColumn<Evenement, String> tcNomE;

    @FXML
    private TextArea taDescription;

    @FXML
    private TextField tfEtat;

    @FXML
    private TextField tfLieu;

    @FXML
    private TextField tfNomEvenement;

    @FXML
    private TextField tfRechercheE;

    @FXML
    private TableView<Evenement> tvEvenement;


    @FXML
    void initialize() {
        CategorieService cs = new CategorieService();

        for (Categorie_evenement c : cs.show()) {
            cbCategorie.getItems().add(c.getNom_categorie());
        }


        afficher_evenement();
        initDeleteColumn();

    }

    private void initDeleteColumn() {
        Callback<TableColumn<Evenement, Void>, TableCell<Evenement, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Evenement, Void> call(final TableColumn<Evenement, Void> param) {
                final TableCell<Evenement, Void> cell = new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        btnDelete.getStyleClass().add("delete-button");
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Evenement evenement = getTableView().getItems().get(getIndex());
                            EvenementService es = new EvenementService();
                            es.delete(evenement);

                            // Mise à jour de la TableView après la suppression de la base de données
                            tvEvenement.getItems().remove(evenement);
                            tvEvenement.refresh();
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

        tcDeleteE.setCellFactory(cellFactory);
        tvEvenement.getColumns().add(tcDeleteE);
    }


    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }


    @FXML
    void ajouterEvenement(ActionEvent event) {

        // Récupérer les valeurs des champs de saisie
        String nomEvenement = tfNomEvenement.getText().trim();
        LocalDate dateDebut = dpDD.getValue();
        LocalDate dateFin = dpDF.getValue();
        String lieu = tfLieu.getText().trim();
        String nomCategorie = cbCategorie.getValue();
        String etat = tfEtat.getText().trim();
        String description = taDescription.getText().trim();

        // Vérifier si les champs sont vides
        if (nomEvenement.isEmpty() || lieu.isEmpty() || nomCategorie.isEmpty() || etat.isEmpty() || description.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.show();}
             // Arrêter l'exécution de la méthode si les champs sont vides

        if (!nomEvenement.matches("[a-zA-Z0-9]*")) {
                showAlert("Veuillez entrer un nom valide sans caractères spéciaux.");
                 // Arrêter l'exécution de la méthode si le nom n'est pas valide
            }

        // Créer l'objet Evenement
        EvenementService es = new EvenementService();
        CategorieService cs = new CategorieService();
        Evenement nouvelEvenement = new Evenement(nomEvenement, Date.valueOf(dateDebut),Date.valueOf(dateFin), lieu, cs.getCategorieByNom(nomCategorie), etat, description);
        es.add(nouvelEvenement);

        // Ajouter le nouvel evenement à la liste existante
        tvEvenement.getItems().add(nouvelEvenement);

        // Rafraîchir la TableView
        tvEvenement.refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Event added");
        alert.setContentText("Event added !");
        alert.show();
        }

    @FXML
    void afficher_evenement(){


        tcCategorieE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("nom_categorie"));
        tcCategorieE.setCellFactory(column -> new TableCell<Evenement, String>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }

                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        CategorieService cs = new CategorieService();

                        // Créer un ComboBox contenant les noms des catégories
                        ComboBox<String> evenemenetComboBox = new ComboBox<>();

                        // Obtenez la liste des noms de catégories
                        List<String> categorieNames = cs.getAllCategoriesNames();

                        // Ajoutez les noms de catégories au ComboBox
                        evenemenetComboBox.getItems().addAll(categorieNames);

                        // Sélectionnez le nom de la catégorie associée au produit
                        evenemenetComboBox.setValue(getItem());

                        // Définir un EventHandler pour le changement de sélection dans le ComboBox
                        evenemenetComboBox.setOnAction(e -> {
                            Evenement evenement = getTableView().getItems().get(getIndex());
                            // Mise à jour de la catégorie associée au produit
                            Categorie_evenement selectedCategorieProduit = cs.getCategorieByNom(evenemenetComboBox.getValue());
                            evenement.setCategorie(selectedCategorieProduit);
                            evenemenetComboBox.getStyleClass().add("combo-box-red");

                            // Mise à jour de la cellule à partir du ComboBox
                            commitEdit(evenemenetComboBox.getValue());

                            // Rétablir la classe CSS pour afficher le texte
                            getStyleClass().remove("cell-hide-text");
                        });

                        // Appliquer la classe CSS pour masquer le texte
                        getStyleClass().add("cell-hide-text");

                        // Afficher le ComboBox dans la cellule
                        setGraphic(evenemenetComboBox);
                    }
                });





        }
        });




        tcNomE.setCellValueFactory(new PropertyValueFactory<Evenement,String>("nom"));
        tcNomE.setCellFactory(TextFieldTableCell.forTableColumn());
        tcNomE.setOnEditCommit(event -> {
            Evenement evenement = event.getRowValue();
            evenement.setNom(event.getNewValue());
            modifier_evenement(evenement);
        });


        tcDDE.setCellValueFactory(cellData -> {
            SimpleObjectProperty<Date> property = new SimpleObjectProperty<>(cellData.getValue().getDateDebut());
            return property;
        });

        tcDDE.setCellFactory(column -> new TableCell<Evenement, Date>() {
            private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(format.format(date));
                }
            }
        });


        tcDFE.setCellValueFactory(cellData -> {
            SimpleObjectProperty<Date> property = new SimpleObjectProperty<>(cellData.getValue().getDateFin());
            return property;
        });

        tcDFE.setCellFactory(column -> new TableCell<Evenement, Date>() {
            private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(format.format(date));
                }
            }
        });

        tcLieuE.setCellValueFactory(new PropertyValueFactory<Evenement,String>("lieu"));
        tcLieuE.setCellFactory(TextFieldTableCell.forTableColumn());
        tcLieuE.setOnEditCommit(event -> {
            Evenement evenement = event.getRowValue();
            evenement.setLieu(event.getNewValue());
            modifier_evenement(evenement);
        });

        tcEtatE.setCellValueFactory(new PropertyValueFactory<Evenement,String>("etat"));
        tcEtatE.setCellFactory(TextFieldTableCell.forTableColumn());
        tcEtatE.setOnEditCommit(event -> {
            Evenement evenement = event.getRowValue();
            evenement.setEtat(event.getNewValue());
            modifier_evenement(evenement);
        });

        tcDescriptionE.setCellValueFactory(new PropertyValueFactory<Evenement,String>("description"));
        tcDescriptionE.setCellFactory(TextFieldTableCell.forTableColumn());
        tcDescriptionE.setOnEditCommit(event -> {
            Evenement evenement = event.getRowValue();
            evenement.setDescription(event.getNewValue());
            modifier_evenement(evenement);
        });


        // Activer l'édition en cliquant sur une ligne
        tvEvenement.setEditable(true);

        // Gérer la modification du texte dans une cellule et le valider en appuyant sur Enter
        tvEvenement.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Evenement selectedEvent = tvEvenement.getSelectionModel().getSelectedItem();
                if (selectedEvent != null) {
                    modifier_evenement(selectedEvent);
                }
            }
        });

        // Utiliser une ObservableList pour stocker les éléments
        ObservableList<Evenement> list = FXCollections.observableArrayList();
        EvenementService es = new EvenementService();
        list.addAll(es.show());
        tvEvenement.setItems(list);

        // Activer la sélection de cellules
        tvEvenement.getSelectionModel().setCellSelectionEnabled(true);

    }



    @FXML
    void modifier_evenement(Evenement evenement) {

        // Récupérez les valeurs modifiées depuis la ligne
        String nouvelleCategorie = evenement.getNom_categorieEvenement();
        String nouveauNom = evenement.getNom();
        Date nouvelledateD = evenement.getDateDebut();
        Date nouvelledateF = evenement.getDateFin();
        String nouveauLieu = evenement.getLieu();
        String nouvelEtat = evenement.getEtat();
        String nouvelleDescription = evenement.getDescription();
        int id = evenement.getId();

        // Enregistrez les modifications dans la base de données en utilisant un service approprié
        EvenementService es = new EvenementService();
        es.update(evenement);
    }




}



