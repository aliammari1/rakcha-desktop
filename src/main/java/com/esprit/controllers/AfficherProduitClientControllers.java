package com.esprit.controllers;

import com.esprit.models.Produit;
import com.esprit.services.ProduitService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;

import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherProduitClientControllers implements Initializable {


    @FXML
    public TextField SearchBar;
    @FXML
    private FlowPane produitFlowPane;

    private FlowPane FlowPane;
    private List<Produit> l1 = new ArrayList<>();


    public void initialize(URL location, ResourceBundle resources) {

        ProduitService produitService = new ProduitService();
        l1 = produitService.read();

        SearchBar.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            List<Produit> produitsRecherches = rechercher(l1, newValue);
            // Effacer la FlowPane actuelle pour afficher les nouveaux résultats
            produitFlowPane.getChildren().clear();
            createProduitCards(produitsRecherches);
        });
        loadAcceptedProduits();

    }

    private void loadAcceptedProduits() {
        // Récupérer toutes les produits depuis le service
        ProduitService produitService = new ProduitService();
        List<Produit> Produits = produitService.read();

        // Créer une carte pour chaque produit et l'ajouter à la FlowPane
        for (Produit produit : Produits) {
            VBox cardContainer = createProduitCard(produit);

            produitFlowPane.getChildren().add(cardContainer);


        }
    }

    private VBox createProduitCard(Produit Produit) {
        // Créer une carte pour le produit avec ses informations

        VBox cardContainer = new VBox(5);

        // Image du Produit
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);


        try {
            Blob blob = Produit.getImage();
            if (blob != null) {
                byte[] bytes = blob.getBytes(1, (int) blob.length());
                Image image = new Image(new ByteArrayInputStream(bytes));
                imageView.setImage(image);
            } else {
                // Utiliser une image par défaut si le Blob est null
                Image defaultImage = new Image(getClass().getResourceAsStream("defaultImage.png"));
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setOnMouseClicked(event -> {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsProduitClient.fxml"));

                Parent root = null;
                System.out.println("Clique sur le nom du produit. ID du produit : " + Produit.getId_produit());
                root = loader.load();
                // Récupérez le contrôleur et passez l'id du produit lors de l'initialisation
                DetailsProduitClientController controller = loader.getController();
                controller.setProduitId(Produit.getId_produit());

                // Afficher la nouvelle interface
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 1280, 700));
                stage.setTitle("Détails du Produit");
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });


        // Prix du Produit
        Label priceLabel = new Label(" " + Produit.getPrix() + " DT");
        /*priceLabel.setLayoutX(10);
        priceLabel.setLayoutY(300);*/
        priceLabel.setFont(Font.font("Arial", 15));

        // Description du produit
        Label descriptionLabel = new Label(Produit.getDescription());
        descriptionLabel.setFont(Font.font("Arial", 14));
        descriptionLabel.setStyle("-fx-text-fill: #666666;");

        // Nom du Produit
        Label nameLabel = new Label(Produit.getNom());
       /*nameLabel.setLayoutX(70);
        nameLabel.setLayoutY(310);*/
        nameLabel.setFont(Font.font("Verdana", 15));
        nameLabel.setStyle("-fx-text-fill: black;");


        // Bouton Ajouter au Panier
        Button addToCartButton = new Button("Ajouter au panier", new FontAwesomeIconView(FontAwesomeIcon.CART_PLUS));
        addToCartButton.setStyle("-fx-background-color: #dd4f4d; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 10px 10px;");
        //addToCartButton.setStyle("-fx-background-color: #624970; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 5px 10px;");
        addToCartButton.setOnAction(event -> {

        });

        cardContainer.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel, addToCartButton);
        //cardContainer.getChildren().add(card);

        cardContainer.setStyle("-fx-padding: 20px 30 20  30px;");
       cardContainer.getStyleClass().add("card-container");



        return cardContainer;
    }


    @FXML
    public static List<Produit> rechercher(List<Produit> liste, String recherche) {
        List<Produit> resultats = new ArrayList<>();

        for (Produit element : liste) {
            if (element.getNom() != null && element.getNom().contains(recherche)) {
                resultats.add(element);
            }
        }

        return resultats;
    }

    private void createProduitCards(List<Produit> produits) {
        for (Produit produit : produits) {
            VBox cardContainer = createProduitCard(produit);
            produitFlowPane.getChildren().add(cardContainer);


        }

    }
}



