package com.esprit.controllers.produits;

import com.esprit.models.produits.Commande;
import com.esprit.models.produits.CommandeItem;
import com.esprit.models.produits.SharedData;
import com.esprit.services.produits.CommandeItemService;
import com.esprit.services.produits.CommandeService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RecuCommandeController {

    @FXML
    private FlowPane detailCommande;

    double totalPrix = SharedData.getInstance().getTotalPrix();

    public void initialize(URL location, ResourceBundle resources) {
        loadLatestCommande();
    }

    private void loadLatestCommande() {
        // Récupérer toutes les commandes depuis le service
        CommandeService commandeService = new CommandeService();
        List<Commande> commandes = commandeService.getCommandesPayees();

        //  Map pour stocker la dernière commande de chaque client
        Map<String, Commande> dernieresCommandes = new HashMap<>();

        for (Commande commande : commandes) {
            // Mettre à jour la dernière commande pour chaque client
            dernieresCommandes.put(commande.getIdClient().getLastName(), commande);
        }

        //  une carte uniquement pour les dernières commandes et les ajouter à la FlowPane
        for (Commande derniereCommande : dernieresCommandes.values()) {
            VBox cardContainer = createRecuCard(derniereCommande);
            detailCommande.getChildren().add(cardContainer);
        }
    }

    private VBox createRecuCard(Commande commande) {
        VBox cardContainer = new VBox(5);
        cardContainer.setStyle("-fx-padding: 20px 0 0  30px;"); // Ajout de remplissage à gauche pour le décalage

        AnchorPane card = new AnchorPane();

        Label nomlabel = new Label("Name: " + commande.getIdClient().getLastName() + " " + commande.getIdClient().getFirstName());
        nomlabel.setFont(Font.font("Arial", 16));
        nomlabel.setStyle("-fx-text-fill: black;");
        nomlabel.setLayoutX(20);
        nomlabel.setLayoutY(30);

        Label datenaissance = new Label("Birthdate: " + commande.getIdClient().getBirthDate());
        datenaissance.setFont(Font.font("Arial", 16));
        datenaissance.setStyle("-fx-text-fill: black;");
        datenaissance.setLayoutX(20);
        datenaissance.setLayoutY(55);

        Label numtel = new Label("Telephone 1: " + commande.getIdClient().getPhoneNumber());
        numtel.setFont(Font.font("Arial", 16));
        numtel.setStyle("-fx-text-fill: black;");
        numtel.setLayoutX(20);
        numtel.setLayoutY(70);

        Label numtel1 = new Label("Telephone 2: " + commande.getNum_telephone());
        numtel1.setFont(Font.font("Arial", 16));
        numtel1.setStyle("-fx-text-fill: black;");
        numtel1.setLayoutX(20);
        numtel1.setLayoutY(90);

        CommandeItem commandeItem= new CommandeItem();
        CommandeItemService commandeItemService = new CommandeItemService();

        Label produit = new Label("Products: " + commandeItemService.getItemsByCommande(commande.getIdCommande()));
        produit.setFont(Font.font("Arial", 16));
        produit.setStyle("-fx-text-fill: black;");
        produit.setLayoutX(20);
        produit.setLayoutY(110);


        Label prixtotale = new Label(totalPrix + " DT");
        prixtotale.setFont(Font.font("Arial", 16));
        prixtotale.setStyle("-fx-text-fill: black;");
        prixtotale.setLayoutX(20);
        prixtotale.setLayoutY(150);

        Label adressse = new Label("Adresse: " + commande.getAdresse());
        adressse.setFont(Font.font("Arial", 16));
        adressse.setStyle("-fx-text-fill: black;");
        adressse.setLayoutX(20);
        adressse.setLayoutY(130);






        card.getChildren().addAll(nomlabel, datenaissance, numtel, adressse, prixtotale, numtel1);

        cardContainer.getChildren().add(card);

        return cardContainer;
    }
}
