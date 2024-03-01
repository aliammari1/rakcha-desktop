package com.esprit.controllers;

import com.esprit.models.Cinema;
import com.esprit.services.CinemaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.io.ByteArrayInputStream;

import java.sql.Blob;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardClientController {
    @FXML
    private FlowPane cinemaFlowPane;

    @FXML
    private AnchorPane listCinemaClient;

    @FXML
    void showListCinema(ActionEvent event) {
        HashSet<Cinema> acceptedCinemas = loadAcceptedCinemas();
        listCinemaClient.setVisible(true);
    }

    private HashSet<Cinema> loadAcceptedCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();

        List<Cinema> acceptedCinemasList = cinemas.stream()
                .filter(cinema -> cinema.getStatut().equals("Acceptée"))
                .collect(Collectors.toList());

        if (acceptedCinemasList.isEmpty()) {
            showAlert("Aucun cinéma accepté n'est disponible.");
        }

        HashSet<Cinema> acceptedCinemasSet = new HashSet<>(acceptedCinemasList);

        for (Cinema cinema : acceptedCinemasSet) {
            HBox cardContainer = createCinemaCard(cinema);
            cinemaFlowPane.getChildren().add(cardContainer);
        }

        return acceptedCinemasSet;
    }

    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private HBox createCinemaCard(Cinema cinema) {
        HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-padding: 25px 0 0 8px ;"); // Ajout de remplissage à gauche pour le décalage


        AnchorPane card = new AnchorPane();
        card.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #000000; -fx-background-radius: 10px; -fx-border-width: 2px;  ");
        card.setPrefWidth(450);
        card.setPrefHeight(150);

        ImageView logoImageView = new ImageView();
        logoImageView.setFitWidth(140); // la largeur de l'image
        logoImageView.setFitHeight(100);
        logoImageView.setLayoutX(15); // Padding à droite
        logoImageView.setLayoutY(15); // Padding en haut
        logoImageView.setStyle("-fx-border-color: #000000 ; -fx-border-width: 2px; -fx-border-radius: 5px;");

        try {
            Blob logoBlob = cinema.getLogo();
            if (logoBlob != null) {
                byte[] logoBytes = logoBlob.getBytes(1, (int) logoBlob.length());
                Image logoImage = new Image(new ByteArrayInputStream(logoBytes));
                logoImageView.setImage(logoImage);
            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("default_logo.png"));
                logoImageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        card.getChildren().add(logoImageView);

        Label NomLabel = new Label("Name: ");
        NomLabel.setLayoutX(155);
        NomLabel.setLayoutY(45);
        NomLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        card.getChildren().add(NomLabel);

        Label nameLabel = new Label(cinema.getNom());
        nameLabel.setLayoutX(210);
        nameLabel.setLayoutY(45);
        nameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        card.getChildren().add(nameLabel);

        Label AdrsLabel = new Label("Adresse: ");
        AdrsLabel.setLayoutX(155);
        AdrsLabel.setLayoutY(85);
        AdrsLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(AdrsLabel);

        Label adresseLabel = new Label(cinema.getAdresse());
        adresseLabel.setLayoutX(220);
        adresseLabel.setLayoutY(85);
        adresseLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(adresseLabel);


        Line verticalLine = new Line();
        verticalLine.setStartX(290);
        verticalLine.setStartY(10);
        verticalLine.setEndX(290);
        verticalLine.setEndY(140);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(3);

        card.getChildren().add(verticalLine);





        Button moviesButton = new Button("Show Movies");
        moviesButton.setLayoutX(310);
        moviesButton.setLayoutY(35);
        moviesButton.getStyleClass().add("movies-btn");

        card.getChildren().add(moviesButton);



        Button planningButton = new Button("Show planning");
        planningButton.setLayoutX(302);
        planningButton.setLayoutY(77);
        planningButton.getStyleClass().add("movies-btn");




        card.getChildren().addAll(planningButton);
        cardContainer.getChildren().add(card);
        return cardContainer;
    }

}

