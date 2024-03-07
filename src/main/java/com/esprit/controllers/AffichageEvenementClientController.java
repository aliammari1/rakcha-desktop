package com.esprit.controllers;

import com.esprit.models.Categorie_evenement;
import com.esprit.models.Evenement;
import com.esprit.models.Feedback;
import com.esprit.services.CategorieService;
import com.esprit.services.EvenementService;
import com.esprit.services.FeedbackEvenementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class AffichageEvenementClientController {

    @FXML
    private FlowPane EventFlowPane;

    @FXML
    private Button bSend;

    @FXML
    private ComboBox<String> cbeventname;

    @FXML
    private TextArea taComment;


    @FXML
    private TableColumn<Evenement, String> tcCategorieE;

    @FXML
    private TableColumn<Evenement, Date> tcDDE;

    @FXML
    private TableColumn<Evenement, Date> tcDFE;

    @FXML
    private TableColumn<Evenement, String> tcDescriptionE;

    @FXML
    private TableColumn<Evenement, String> tcEtatE;

    @FXML
    private TableColumn<Evenement, String> tcLieuE;

    @FXML
    private TableColumn<Evenement, String> tcNomE;

    @FXML
    private TextField tfRechercheEc;

    @FXML
    private TableView<Evenement> tvEvenement;


        @FXML
        void initialize() {

            EvenementService es = new EvenementService();

            for (Evenement e : es.show()) {
                cbeventname.getItems().add(e.getNom());
            }


            afficher_evenements();


        }

    private void afficher_evenements() {
        // Initialiser les cellules de la TableView
        tcNomE.setCellValueFactory(new PropertyValueFactory<Evenement,String>("nom"));
        tcCategorieE.setCellValueFactory(new PropertyValueFactory<Evenement,String>("nomCategorie"));
        tcDDE.setCellValueFactory(new PropertyValueFactory<Evenement,Date>("dateDebut"));
        tcDFE.setCellValueFactory(new PropertyValueFactory<Evenement,Date>("dateFin"));
        tcLieuE.setCellValueFactory(new PropertyValueFactory<Evenement,String>("lieu"));
        tcEtatE.setCellValueFactory(new PropertyValueFactory<Evenement,String>("etat"));
        tcDescriptionE.setCellValueFactory(new PropertyValueFactory<Evenement,String>("description"));


        // Utiliser une ObservableList pour stocker les éléments
        ObservableList<Evenement> list = FXCollections.observableArrayList();
        EvenementService es = new EvenementService();
        list.addAll(es.show());
        tvEvenement.setItems(list);

    }

    @FXML
    void ajouterFeedback(ActionEvent event) {

        // Récupérer les valeurs des champs de saisie
        String nomEvenement = cbeventname.getValue();
        int id_user;
        String comment = taComment.getText().trim();

        // Vérifier si les champs sont vides
        if (nomEvenement.isEmpty() || comment.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Typing Error !");
            alert.setContentText("Please fill out the form");
            alert.show();
        }


        // Créer l'objet Feedback
        FeedbackEvenementService fs = new FeedbackEvenementService();
        EvenementService es = new EvenementService();
        Feedback nouveauFeedback = new Feedback(es.getEvenementByNom(nomEvenement), id_user =0, comment);
        fs.add(nouveauFeedback);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feedback Sent");
        alert.setContentText("Feedback Sent !");
        alert.show();

        taComment.clear();

    }

}






