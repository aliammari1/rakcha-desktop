package com.esprit.controllers;

import com.esprit.models.Cinema;
import com.esprit.services.CinemaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ListCinemaResponsableController implements Initializable {

    @FXML
    private FlowPane cinemaFlowPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAcceptedCinemas();
    }

    private void loadAcceptedCinemas() {
        // Récupérer toutes les cinémas depuis le service
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();

        // Filtrer les cinémas avec le statut "Acceptée"
        List<Cinema> acceptedCinemas = cinemas.stream()
                .filter(cinema -> cinema.getStatut().equals("Acceptée"))
                .collect(Collectors.toList());

        if (acceptedCinemas.isEmpty()) {
            showAlert("Aucun cinéma accepté n'est disponible.");
            return;
        }

        for (Cinema cinema : acceptedCinemas) {
            AnchorPane card = createCinemaCard(cinema);
            cinemaFlowPane.getChildren().add(card);
        }
    }

    private AnchorPane createCinemaCard(Cinema cinema) {
        // Créer une carte pour le cinéma avec ses informations
        AnchorPane card = new AnchorPane();

        // Nom du cinéma
        Label nameLabel = new Label("Nom: " + cinema.getNom());
        nameLabel.setLayoutX(10);
        nameLabel.setLayoutY(10);
        card.getChildren().add(nameLabel);

        // Adresse du cinéma
        Label addressLabel = new Label("Adresse: " + cinema.getAdresse());
        addressLabel.setLayoutX(10);
        addressLabel.setLayoutY(30);
        card.getChildren().add(addressLabel);

        // Logo du cinéma
        ImageView logoImageView = new ImageView();
        logoImageView.setLayoutX(10);
        logoImageView.setLayoutY(50);
        logoImageView.setFitWidth(100); // Réglez la largeur de l'image selon vos préférences
        logoImageView.setFitHeight(100); // Réglez la hauteur de l'image selon vos préférences
        try {
            Blob logoBlob = cinema.getLogo();
            if (logoBlob != null) {
                byte[] logoBytes = logoBlob.getBytes(1, (int) logoBlob.length());
                Image logoImage = new Image(new ByteArrayInputStream(logoBytes));
                logoImageView.setImage(logoImage);
            } else {
                // Utiliser une image par défaut si le logo est null
                Image defaultImage = new Image(getClass().getResourceAsStream("default_logo.png"));
                logoImageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        card.getChildren().add(logoImageView);

        // Bouton Modifier
        Button editButton = new Button("Modifier");
        editButton.setLayoutX(10);
        editButton.setLayoutY(160);
// Définir l'action du bouton Modifier
        editButton.setOnAction(event -> {
            // Charger la nouvelle interface ModifierCinema.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCinema.fxml"));
            Parent root = null;
            try {
                root = loader.load();
                // Passer les données du cinéma à la nouvelle interface
                ModifierCinemaController controller = loader.getController();
                controller.initData(cinema); // Passer le cinéma correspondant
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la scène actuelle
            Stage currentStage = (Stage) cinemaFlowPane.getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);

            // Définir le titre de la nouvelle fenêtre
            stage.setTitle("Modifier Cinema");

            // Fermer la fenêtre actuelle lorsque la nouvelle fenêtre est affichée
            stage.setOnShown(e -> currentStage.close());

            // Afficher la nouvelle fenêtre
            stage.show();
        });
        card.getChildren().add(editButton);


        // Bouton Supprimer
        Button deleteButton = new Button("Supprimer");
        deleteButton.setLayoutX(80);
        deleteButton.setLayoutY(160);
        // Définir l'action du bouton Supprimer
        deleteButton.setOnAction(event -> {
            // Supprimer le cinéma de la base de données
            CinemaService cinemaService = new CinemaService();
            cinemaService.delete(cinema);
            // Supprimer la carte de cinéma de la FlowPane
            cinemaFlowPane.getChildren().remove(card);
        });
        card.getChildren().add(deleteButton);

        return card;
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
