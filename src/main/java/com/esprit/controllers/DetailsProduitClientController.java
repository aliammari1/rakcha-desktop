package com.esprit.controllers;
import com.esprit.models.Produit;
import com.esprit.services.ProduitService;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;



import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.net.URL;
import java.sql.Blob;

import java.util.Optional;
import java.util.ResourceBundle;



public class DetailsProduitClientController implements Initializable {

@FXML
    public FlowPane detailtFlowPane;
  @FXML
    public TextField SearchBar;


    private int produitId;

    // Méthode pour initialiser l'ID du produit
    public void setProduitId(int produitId) {
        this.produitId = produitId;
        // Initialiser les détails du produit après avoir défini l'ID
        initDetailsProduit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Aucune initialisation spécifique nécessaire dans cette méthode

    }

    private void initDetailsProduit() {
        // Récupérer le produit depuis le service en utilisant l'ID
        ProduitService produitService = new ProduitService();
        Optional<Produit> produitOptional = Optional.ofNullable(produitService.getProduitById(produitId));

        // Vérifier si le produit a été trouvé
        produitOptional.ifPresentOrElse(
                produit -> {
                    // Effacer les anciennes cartes avant d'ajouter la nouvelle carte
                    detailtFlowPane.getChildren().clear();

                    // Créer la carte pour le produit trouvé et l'ajouter à la FlowPane
                    HBox cardContainer = createProduitCard(produit);
                    detailtFlowPane.getChildren().add(cardContainer);
                },
                () -> System.err.println("Produit non trouvé avec l'ID : " + produitId)
        );
    }


    private HBox createProduitCard (Produit Produit) {
        // Créer une carte pour le produit avec ses informations

        HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-padding: 30px 0 0  50px;"); // Ajout de remplissage à gauche pour le décalage

        AnchorPane card = new AnchorPane();

        // Image du Produit
        ImageView imageView = new ImageView();
        imageView.setLayoutX(10);
        imageView.setLayoutY(10);
        imageView.setFitWidth(400);
        imageView.setFitHeight(400);


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



        // Nom du Produit
        Label nameLabel = new Label(Produit.getNom());

        nameLabel.setFont(Font.font("Arial", 20)); // Définir la police et la taille
        nameLabel.setStyle("-fx-text-fill: black;"); // Définir la couleur du texte
        nameLabel.setLayoutX(450);
        nameLabel.setLayoutY(30);


        // Prix du Produit
        Label priceLabel = new Label(" " + Produit.getPrix()+" DT");

        priceLabel.setFont(Font.font("Helvetica", 20)); // Définir la police et la taillepriceLabel.setLayoutX(10);
        priceLabel.setLayoutX(450);
        priceLabel.setLayoutY(200);


        //description produit
        Label descriptionlabel = new Label(Produit.getDescription());

        descriptionlabel.setFont(Font.font("Arial", 12)); // Définir la police et la taille
        descriptionlabel.setStyle("-fx-text-fill: black;"); // Définir la couleur du texte
        descriptionlabel.setLayoutX(470);
        descriptionlabel.setLayoutY(70);

       /* // Spinner pour la quantité
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1); // Valeurs min, max, valeur initiale
        quantitySpinner.setLayoutX(10);
        quantitySpinner.setLayoutY(300);
        quantitySpinner.setPrefWidth(70); // Largeur souhaitée
        quantitySpinner.setPrefHeight(25); // Hauteur souhaitée

        card.getChildren().add(quantitySpinner);*/

        // Bouton Ajouter au Panier
        Button addToCartButton = new Button("Ajouter au panier");
        addToCartButton.setLayoutX(450);
        addToCartButton.setLayoutY(300);
        addToCartButton.setStyle("-fx-background-color: #C62828; -fx-text-fill: White;"); // Style du bouton
        addToCartButton.setOnAction(event -> {
            // Récupérer la quantité sélectionnée
           /* int quantity = quantitySpinner.getValue();

            // Créer un objet représentant l'élément du panier
            PanierProduitControllers commandeItem = new PanierProduitControllers(produit, quantity);

            // Ajouter l'élément à la commande
            Commande.getInstance().ajouterAuPanier(commandeItem);

            // Vous pouvez également mettre à jour l'interface utilisateur pour refléter l'ajout à la commande
            // Par exemple, afficher un message de confirmation
            afficherMessageConfirmation("Produit ajouté à la commande!");*/
        });

        card.getChildren().addAll(imageView,nameLabel,descriptionlabel,priceLabel,addToCartButton);

        cardContainer.getChildren().add(card);

        return cardContainer;
    }
    @FXML
    public void afficherProduit(ActionEvent actionEvent) throws IOException {
        // Charger la nouvelle interface ListCinemaAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduitClient.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root,1280,700);

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("afficher les Produit");
        stage.show();

    }


}

