package com.esprit.controllers;

import com.esprit.models.Evenement;
import com.esprit.services.EvenementService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import java.util.List;
import java.util.ResourceBundle;

public class AffichageEvenementClientController implements Initializable  {


    @FXML
    public TextField SearchBar;
    @FXML
    private  FlowPane evenementFlowPane;



    public void initialize(URL location, ResourceBundle resources) {
        loadAcceptedEvents();

    }
    private void loadAcceptedEvents() {
        // Récupérer toutes les evenements depuis le service
        EvenementService es = new EvenementService();
        List<Evenement>Evenements =es.show();
        // Créer une carte pour chaque évenement et l'ajouter à la FlowPane
        for (Evenement evenement : Evenements){
            //VBox card = createEventCard(evenement);
            VBox cardContainer = createEvenementCard(evenement);

            evenementFlowPane.getChildren().add(cardContainer);
        }
    }

    private VBox createEvenementCard (Evenement evenement)  {
        // Créer une carte pour l'evenement avec ses informations

        VBox cardContainer = new VBox(5);
        cardContainer.setStyle("-fx-padding: 50px 0 0  50px;"); // Ajout de remplissage à gauche pour le décalage

        AnchorPane card = new AnchorPane();


            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsEvenementClient.fxml"));

                Parent root = null;
                System.out.println("Clique sur le nom de l'évenement. ID de l'evenement : " + evenement.getId());
                root = loader.load();
                // Récupérez le contrôleur et passez l'id du produit lors de l'initialisation
                DetailsEvenementClientController controller = loader.getController();
                controller.setId(evenement.getId());

                // Afficher la nouvelle interface
                Stage stage = new Stage();
                stage.setScene(new Scene(root,1280,700));
                stage.setTitle("Détails de l'evenemnt");
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });



        // Lieu de l'evenement
        Label lieuLabel = new Label(" " + Evenement.getLieu());
        /*priceLabel.setLayoutX(10);
        priceLabel.setLayoutY(300);*/
        priceLabel.setFont(Font.font("Helvetica", 16)); // Définir la police et la taille



        // Nom de l'evenement
        Label nameLabel = new Label(Evenement.getNom());
       /*nameLabel.setLayoutX(70);
        nameLabel.setLayoutY(310);*/
        nameLabel.setFont(Font.font("Arial", 12)); // Définir la police et la taille
        nameLabel.setStyle("-fx-text-fill: black;"); // Définir la couleur du texte
        nameLabel.setOnMouseClicked(event -> {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsProduitClient.fxml"));

                Parent root = null;
                System.out.println("Clique sur le nom du produit. ID de l'evenement : " + evenement.getId_produit());
                root = loader.load();
                // Récupérez le contrôleur et passez l'id du produit lors de l'initialisation
                DetailsEvenementClientController controller = loader.getController();
                controller.setId(Evenement.getId());

                // Afficher la nouvelle interface
                Stage stage = new Stage();
                stage.setScene(new Scene(root,1280,700));
                stage.setTitle("Détails de l'evenement");
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        };

    private void showAlert (String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}