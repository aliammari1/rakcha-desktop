package com.esprit.controllers;

import com.esprit.models.Categorie;
import com.esprit.models.Evenement;
import com.esprit.services.CategorieService;
import com.esprit.services.EvenementService;
import com.esprit.utils.DataSource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import javax.sql.rowset.serial.SerialBlob;

public class DesignEvenementAdminController {

    @FXML
    private ComboBox<String> cbCategorieE;

    @FXML
    private DatePicker dpDDE;

    @FXML
    private DatePicker dpDFE;

    @FXML
    private TextField tfDescriptionE;

    @FXML
    private TextField tfEtatE;

    @FXML
    private TextField tfIDE;

    @FXML
    private TextField tfLieuE;

    @FXML
    private TextField tfNomEvenement;

    @FXML
    private TableView<Evenement> tvEvenement;

    @FXML
    private TableColumn<Evenement, Categorie> tcCategorieE;

    @FXML
    private TableColumn<Evenement, Date > tcDDE;

    @FXML
    private TableColumn<Evenement, Date > tcDFE;

    @FXML
    private TableColumn<Evenement, String> tcDescriptionE;

    @FXML
    private TableColumn<Evenement, String> tcEtatE;

    @FXML
    private TableColumn<Evenement, Integer> tcIDE;

    @FXML
    private TableColumn<Evenement, String> tcLieuE;

    @FXML
    private TableColumn<Evenement, String> tcNomE;

    @FXML
    private TextField tfRechercheE;

    @FXML
    private TableColumn<Evenement,Void> tcDeleteE;

    private List<Evenement> l1=new ArrayList<>();
    @FXML
    void initialize() {
        EvenementService es =new EvenementService();
        l1=es.show();
        tfRechercheE.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            List<Evenement> eve;
            eve=rechercher(l1,newValue);
            showEvenement(eve);
        });
        CategorieService cs = new CategorieService();

        for (Categorie c : cs.show()) {
            cbCategorieE.getItems().add(c.getNom_categorie());
        }
        //searchFilter();
        showEvenement(l1);
        initDeleteColumn();

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
    void addEvenement(ActionEvent event) {
        Connection connection = null;
        connection = DataSource.getInstance().getConnection();
        EvenementService es = new EvenementService();
        es.add(new Evenement(Integer.parseInt(tfIDE.getText()),tfNomEvenement.getText(), Date.valueOf(dpDDE.getValue()),Date.valueOf(dpDFE.getValue()),tfLieuE.getText(),new CategorieService().getCategorieByNom(String.valueOf(cbCategorieE.getValue())),tfEtatE.getText(),tfDescriptionE.getText()));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Evenement ajouté");
        alert.setContentText("Evenement ajouté !");
        alert.show();
        showEvenement(l1);
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                showAlert("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }

        }
        showEvenement(l1);
    }


    @FXML
    void updateEvenement(Evenement evenement) {

        // Récupérez les valeurs modifiées depuis la ligne
        String nouvelleCategorie = evenement.getNom_categorieEvenement();
        String nouveauNom = evenement.getNom();
        Date nouvelleDateDebut = evenement.getDateDebut();
        Date nouvelleDateFin = evenement.getDateFin();
        String nouveauLieu = evenement.getLieu();
        String nouvelEtat = evenement.getEtat();
        String nouvelleDescription = evenement.getDescription();
        int id = evenement.getId();

        // Enregistrez les modifications dans la base de données en utilisant un service approprié
        EvenementService es = new EvenementService();
        es.update(evenement);
    }

    @FXML
    void deleteEvenement(Evenement evenement) {
        Evenement selectedProduct = tvEvenement.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            EvenementService ps = new EvenementService();
            ps.delete(selectedProduct);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Evenement supprimé");
            alert.setContentText("Evenement supprimé !");
            alert.show();



        }
        // Rafraîchir la TableView après la suppression
        showEvenement(l1);
    }

    private void initDeleteColumn() {

        // Créer une cellule de la colonne Supprimer
        Callback<TableColumn<Evenement, Void>, TableCell<Evenement, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Evenement, Void> call(final TableColumn<Evenement, Void> param) {
                final TableCell<Evenement, Void> cell = new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {

                        btnDelete.getStyleClass().add("delete-button");
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Evenement evenement = getTableView().getItems().get(getIndex());
                            deleteEvenement(evenement);
                            tvEvenement.getItems().remove(evenement);
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
        tcDeleteE.setCellFactory(cellFactory);

        // Ajouter la colonne Supprimer à la TableView
        tvEvenement.getColumns().add(tcDeleteE);
    }

    void showEvenement(List<Evenement> listevenement){
        tvEvenement.getItems().clear();


        // Créer un nouveau ComboBox

        tcCategorieE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("nom_categorie"));


// Définissez le rendu de la cellule en utilisant le ComboBox
        tcCategorieE.setCellFactory(column -> new TableCell<Evenement, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    CategorieService cs = new CategorieService();
                    ComboBox<String> newComboBox = new ComboBox<>();

                    // Utilisez la méthode getCategorieByNom pour obtenir la catégorie
                    Categorie categorie = cs.getCategorieByNom(item);
                    // ComboBox newComboBox =new ComboBox<>();
                    // Ajoutez le nom de la catégorie au ComboBox
                    newComboBox.setItems(cbCategorieE.getItems());
                    newComboBox.setValue(categorie.getNom_categorie());

                    // Afficher le ComboBox nouvellement créé dans la cellule
                    setGraphic(newComboBox);
                    newComboBox.setOnAction(event ->{
                        Evenement evenement = getTableView().getItems().get(getIndex());
                        evenement.setCategorie( new CategorieService().getCategorieByNom(newComboBox.getValue()));
                        updateEvenement(evenement);
                        newComboBox.getStyleClass().add("combo-box-red");
                    });
                }
            }
        });

        tcNomE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("nom"));
        tcNomE.setCellFactory(TextFieldTableCell.forTableColumn());
        tcNomE.setOnEditCommit(event -> {
            Evenement evenement = event.getRowValue();
            evenement.setNom(event.getNewValue());
            updateEvenement(evenement);
        });

        tcDDE.setCellValueFactory(new PropertyValueFactory<Evenement, Date>("Date Debut"));
        tcDDE.setCellFactory(TextFieldTableCell.forTableColumn());
        tcDDE.setOnEditCommit(event -> {
            Evenement evenement = event.getRowValue();
            evenement.setDateDebut(event.getNewValue());
            updateEvenement(evenement);
        });


        void GestionCategorie(ActionEvent event) throws IOException {

        // Charger la nouvelle interface ListCinemaAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignCategorieAdmin.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Gestion des categories");
        stage.show();

    }

        @FXML
        public static List<Evenement> rechercher(List<Evenement> liste, String recherche) {
            List<Evenement> resultats = new ArrayList<>();

            for (Evenement element : liste) {
                if (element.getNom() != null && element.getNom().contains(recherche)) {
                    resultats.add(element);
                }
            }

            return resultats;
        }







    }
