package com.esprit.controllers;

import com.esprit.models.Produit;
import com.esprit.services.ProduitService;
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

public class AfficherProduitClientControllers implements Initializable  {


    @FXML
    public TextField SearchBar;
    @FXML
   private  FlowPane produitFlowPane;



    public void initialize(URL location, ResourceBundle resources) {
        loadAcceptedProduits();

    }
    private void loadAcceptedProduits() {
        // Récupérer toutes les produits depuis le service
    ProduitService produitService = new ProduitService();
    List<Produit>Produits =produitService.read();
    // Créer une carte pour chaque produit et l'ajouter à la FlowPane
    for (Produit produit : Produits){
        //VBox card = createProduitCard(produit);
        VBox cardContainer = createProduitCard(produit);

        produitFlowPane.getChildren().add(cardContainer);
    }
}

    private VBox createProduitCard (Produit Produit)  {
        // Créer une carte pour le produit avec ses informations

        VBox cardContainer = new VBox(5);
        cardContainer.setStyle("-fx-padding: 50px 0 0  50px;"); // Ajout de remplissage à gauche pour le décalage

        AnchorPane card = new AnchorPane();

        // Image du Produit
        ImageView imageView = new ImageView();
        /*imageView.setLayoutX(10);
        imageView.setLayoutY(140);*/
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
                stage.setScene(new Scene(root,1280,700));
                stage.setTitle("Détails du Produit");
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });



        // Prix du Produit
        Label priceLabel = new Label(" " + Produit.getPrix()+" DT");
        /*priceLabel.setLayoutX(10);
        priceLabel.setLayoutY(300);*/
        priceLabel.setFont(Font.font("Helvetica", 16)); // Définir la police et la taille



        // Nom du Produit
        Label nameLabel = new Label(Produit.getNom());
       /*nameLabel.setLayoutX(70);
        nameLabel.setLayoutY(310);*/
        nameLabel.setFont(Font.font("Arial", 12)); // Définir la police et la taille
        nameLabel.setStyle("-fx-text-fill: black;"); // Définir la couleur du texte
        nameLabel.setOnMouseClicked(event -> {

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
                stage.setScene(new Scene(root,1280,700));
                stage.setTitle("Détails du Produit");
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });


       /* // Spinner pour la quantité
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1); // Valeurs min, max, valeur initiale
        quantitySpinner.setLayoutX(10);
        quantitySpinner.setLayoutY(300);
        quantitySpinner.setPrefWidth(70); // Largeur souhaitée
        quantitySpinner.setPrefHeight(25); // Hauteur souhaitée

        card.getChildren().add(quantitySpinner);*/

        // Bouton Ajouter au Panier
        Button addToCartButton = new Button("Ajouter au panier");
        /*addToCartButton.setLayoutX(10);
        addToCartButton.setLayoutY(330);*/
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

        cardContainer.getChildren().addAll(imageView,nameLabel,priceLabel,addToCartButton);




       /* // Ajoutez un gestionnaire d'événements pour ouvrir la nouvelle interface lors du clic sur la carte
        nameLabel.setOnMouseClicked(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DetailsProduitClient.fxml"));
            Parent root = null;
            try {
                root = loader.load();
             // Récupérez le contrôleur et passez l'id du produit lors de l'initialisation
                DetailsProduitClientController controller = loader.getController();
                controller.setProduitId(Produit.getId_produit());

                // Afficher la nouvelle interface
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Détails du Produit");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });*/
        cardContainer.getChildren().add(card);

        return cardContainer;
    }


    private void showAlert (String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
