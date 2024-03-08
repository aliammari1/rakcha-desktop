package com.esprit.controllers.evenements;

import com.esprit.models.evenements.Evenement;
import com.esprit.models.evenements.Feedback;
import com.esprit.services.evenements.EvenementService;
import com.esprit.services.evenements.FeedbackEvenementService;
import com.esprit.services.evenements.SmsService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Date;

public class AffichageEvenementClientController {

    private final ObservableList<Evenement> masterData = FXCollections.observableArrayList();
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

        for (Evenement e : es.read()) {
            cbeventname.getItems().add(e.getNom());
        }


        afficher_evenements();
        setupSearchFilter();


    }

    private void afficher_evenements() {
        // Initialiser les cellules de la TableView
        tcNomE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("nom"));
        //tcCategorieE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("nomCategorie"));

        tcCategorieE.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Evenement, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Evenement, String> evenementStringCellDataFeatures) {
                return new SimpleStringProperty(evenementStringCellDataFeatures.getValue().getNom_categorieEvenement());
            }
        });

        tcDDE.setCellValueFactory(new PropertyValueFactory<Evenement, Date>("dateDebut"));
        tcDFE.setCellValueFactory(new PropertyValueFactory<Evenement, Date>("dateFin"));
        tcLieuE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("lieu"));
        tcEtatE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("etat"));
        tcDescriptionE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("description"));


        // Utiliser une ObservableList pour stocker les éléments
        EvenementService es = new EvenementService();
        masterData.addAll(es.read());
        tvEvenement.setItems(masterData);


    }

    private void setupSearchFilter() {
        FilteredList<Evenement> filteredData = new FilteredList<>(masterData, p -> true);

        tfRechercheEc.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(evenement -> {
                // If filter text is empty, display all events.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare event name, category, and description of every event with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (evenement.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches event name.
                } else if (evenement.getNom_categorieEvenement().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches category.
                } else if (evenement.getLieu().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches description.
                } else // Filter matches description.
                    if (evenement.getEtat().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches description.
                    } else return evenement.getDescription().toLowerCase().contains(lowerCaseFilter);// Does not match.
            });
        });

        // Wrap the FilteredList in a SortedList.
        SortedList<Evenement> sortedData = new SortedList<>(filteredData);

        // Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tvEvenement.comparatorProperty());

        // Add sorted (and filtered) data to the table.
        tvEvenement.setItems(sortedData);
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
        Feedback nouveauFeedback = new Feedback(es.getEvenementByNom(nomEvenement), id_user = 0, comment);
        fs.create(nouveauFeedback);
        SmsService.sendSms("+21622757828", "Thank You ! We appreciate your feedback, may this be the starting point for a better RAKCHA :) ");


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feedback Sent");
        alert.setContentText("Feedback Sent !");
        alert.show();

        taComment.clear();

    }

    @FXML
    void showFilms(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/filmuser.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Movies List");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    @FXML
    void showProduits(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduitClient.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Products List");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    @FXML
    void showSeries(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Seriesclient.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Series List");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    @FXML
    void showCinemas(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Seriesclient.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Series List");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    @FXML
    void showEvents(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEvenementClient.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Events List");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

}






