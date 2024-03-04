package com.esprit.controllers;

import com.esprit.models.CommandeItem;
import com.esprit.models.Produit;
import com.esprit.services.CommandeItemService;
import com.esprit.services.ProduitService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;



public class DetailsProduitClientController implements Initializable {

    @FXML

    public FlowPane detailFlowPane;


    @FXML
    private AnchorPane anchordetail;
    @FXML
    private AnchorPane panierAnchorPane;

    @FXML

    public FlowPane panierFlowPane;


    @FXML
    public TextField SearchBar;

    @FXML
    private FontAwesomeIconView retour;

    private int produitId;

    private Button addToCartButton;





    // Méthode pour initialiser l'ID du produit
    public void setProduitId(int produitId) throws IOException {
        this.produitId = produitId;

        // Initialiser les détails du produit après avoir défini l'ID
        initDetailsProduit();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        // Attacher un gestionnaire d'événements au clic de la souris sur l'icône de retour
        retour.setOnMouseClicked(event -> afficherProduit());



        }






    private void initDetailsProduit() {
        // Récupérer le produit depuis le service en utilisant l'ID
        ProduitService produitService = new ProduitService();
        Optional<Produit> produitOptional = Optional.ofNullable(produitService.getProduitById(produitId));

        // Vérifier si le produit a été trouvé
        produitOptional.ifPresentOrElse(
                produit -> {
                    // Effacer les anciennes cartes avant d'ajouter la nouvelle carte
                    detailFlowPane.getChildren().clear();

                    // Créer la carte pour le produit trouvé et l'ajouter à la FlowPane
                    HBox cardContainer = createProduitCard(produit);
                    HBox panierContainer = createPanierCard(produit);
                    detailFlowPane.getChildren().add(cardContainer);
                    panierFlowPane.getChildren().add(panierContainer);
                },
                () -> System.err.println("Produit non trouvé avec l'ID : " + produitId)
        );


    }


    private HBox createProduitCard(Produit produit) {
        // Créer une carte pour le produit avec ses informations

        HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-padding: 10px 0 0  50px;"); // Ajout de remplissage à gauche pour le décalage

        AnchorPane card = new AnchorPane();

        // Image du Produit
        ImageView imageView = new ImageView();
        imageView.setLayoutX(10);
        imageView.setLayoutY(3);
        imageView.setFitWidth(370);
        imageView.setFitHeight(400);


        try {
            Blob blob = produit.getImage();
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
        Label nameLabel = new Label(produit.getNom());
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(400);
        nameLabel.setLayoutY(30);
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique




        // Prix du Produit
        Label priceLabel = new Label(" " + produit.getPrix() + " DT");
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        priceLabel.setLayoutX(450);
        priceLabel.setLayoutY(200);


        Label descriptionLabel = new Label(produit.getDescription());
        descriptionLabel.setFont(Font.font("Arial", 14));
        descriptionLabel.setTextFill(Color.web("#392c2c")); // Définir la même couleur de texte que descriptionText
        descriptionLabel.setLayoutX(410);
        descriptionLabel.setLayoutY(95);
        descriptionLabel.setMaxWidth(250); // Ajuster la largeur maximale selon vos besoins
        descriptionLabel.setWrapText(true); // Activer le retour à la ligne automatique


        // Champ de texte pour la quantité
        TextField quantityTextField = new TextField();
        quantityTextField.setPromptText("Quantité");
        quantityTextField.setLayoutX(450);
        quantityTextField.setLayoutY(250);
        quantityTextField.setMaxWidth(100);
        quantityTextField.setText("1"); // Définir la valeur par défaut à 1


        // Icône de diminution de quantité
        FontAwesomeIconView decreaseIcon = new FontAwesomeIconView();
        decreaseIcon.setGlyphName("MINUS");
        decreaseIcon.setSize("15");
        decreaseIcon.setLayoutX(425);
        decreaseIcon.setLayoutY(270);
        decreaseIcon.setOnMouseClicked(event -> decreaseQuantity(quantityTextField));

        // Icône d'augmentation de quantité
        FontAwesomeIconView increaseIcon = new FontAwesomeIconView();
        increaseIcon.setGlyphName("PLUS");
        increaseIcon.setSize("15");
        increaseIcon.setLayoutX(565);
        increaseIcon.setLayoutY(270);
        increaseIcon.setOnMouseClicked(event -> increaseQuantity(quantityTextField));


        // Bouton Ajouter au Panier
        Button addToCartButton = new Button("Ajouter au panier", new FontAwesomeIconView(FontAwesomeIcon.CART_PLUS));
        addToCartButton.setLayoutX(450);
        addToCartButton.setLayoutY(300);
        addToCartButton.getStyleClass().add("ajouter-panier"); // Style du bouton
        addToCartButton.setOnAction(
                event -> {

                    System.out.println("Bouton Ajouter au panier cliqué");

                    afficherpanier();
                // Récupérer la quantité sélectionnée
                int quantity = Integer.parseInt(quantityTextField.getText());

                 // Vérifier si la quantité demandée est disponible en stock
                  if (isStockAvailable(produit, quantity)) {
                // Mettre à jour le stock dans la base de données
                //decrementStock(produit, quantity);


                ProduitService ps = new ProduitService();

                // Créer un objet représentant l'élément du panier
                CommandeItem commandeItem = new CommandeItem(ps.getProduitById(produitId), quantity);


                CommandeItemService commandeItemservice = new CommandeItemService();
                // Ajouter l'élément au panier
                //commandeItemservice.create(commandeItem);




                // Afficher un message de confirmation
                System.out.println("Produit ajouté au panier!");



            } else {
                // Afficher un message d'erreur si la quantité demandée n'est pas disponible en stock
                System.out.println("Quantité non disponible en stock!");
            }
        });

        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel, increaseIcon, quantityTextField, decreaseIcon, addToCartButton);

        cardContainer.getChildren().add(card);

        return cardContainer;
    }

    private boolean isStockAvailable(Produit produit, int quantity) {
        // Comparer la quantité demandée avec la quantité disponible en stock
        return produit.getQuantiteP() >= quantity;
    }

    private void decrementStock(Produit produit, int quantity) {
        // Décrémenter le stock dans la base de données
        produit.setQuantiteP(produit.getQuantiteP() - quantity);
        ProduitService produitService = new ProduitService();
        produitService.update(produit); // Assurez-vous que votre service de produit dispose d'une méthode de mise à jour
    }

    private void decreaseQuantity(TextField quantityTextField) {
        // Diminuer la quantité
        int quantity = Integer.parseInt(quantityTextField.getText());
        if (quantity > 1) {
            quantityTextField.setText(String.valueOf(quantity - 1));
        }
    }

    private void increaseQuantity(TextField quantityTextField) {
        // Augmenter la quantité
        int quantity = Integer.parseInt(quantityTextField.getText());
        quantityTextField.setText(String.valueOf(quantity + 1));
    }


    @FXML
    public void afficherProduit() {


        // Obtenir la fenêtre précédente
        Window previousWindow = retour.getScene().getWindow();

        // Charger le fichier FXML de la page "AfficherProduit.fxml"
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AfficherProduitClient.fxml"));

        try {
            Parent rootNode = fxmlLoader.load();
            Scene scene = new Scene(rootNode);

            // Créer une nouvelle fenêtre pour la page "AfficherProduit.fxml"
            Stage previousStage = new Stage();

            // Configurer la fenêtre précédente avec les propriétés nécessaires
            previousStage.setScene(scene);
            previousStage.setTitle(" Afficher Produit");

            // Afficher la fenêtre précédente de manière bloquante
            previousStage.initModality(Modality.APPLICATION_MODAL);
            previousStage.initOwner(previousWindow);
            previousStage.showAndWait();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) retour.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace(); // Gérer l'exception selon vos besoins
        }
    }



    private void afficherpanier (){

        // Initialiser la visibilité des AnchorPane

        panierFlowPane.setVisible(true);
        detailFlowPane.setVisible(true);
        detailFlowPane.setOpacity(0.2);

    }


    private HBox createPanierCard(Produit produit) {
        // Créer une carte pour le produit avec ses informations

        HBox panierContainer = new HBox();
        panierContainer.setStyle("-fx-padding: 30px 0 0 20px;"); // Ajout de remplissage à gauche pour le décalage

        AnchorPane card = new AnchorPane();
        // Image du Produit
        ImageView imageView = new ImageView();
        imageView.setLayoutX(50);
        imageView.setLayoutY(40);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);


        try {
            Blob blob = produit.getImage();
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
        Label nameLabel = new Label(produit.getNom());
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(10);
        nameLabel.setLayoutY(190);
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique

        // Prix du Produit
        Label priceLabel = new Label(" " + produit.getPrix() + " DT");
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        priceLabel.setLayoutX(10);
        priceLabel.setLayoutY(230);
        CommandeItemService commandeItemService = new CommandeItemService();

        // Récupérer la quantité du produit dans le panier
        int totalQuantiteProduit = commandeItemService.calculerQuantiteProduitDansPanier(produit.getId_produit());

       // Champ de texte pour la quantité
        Label quantiteLabel = new Label("Quantité : " + totalQuantiteProduit);
        quantiteLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        quantiteLabel.setLayoutX(30);
        quantiteLabel.setLayoutY(260);

        // Récupérer la somme totale du prix du produit dans le panier
        double sommeTotaleProduit = commandeItemService.calculerSommeTotaleProduit(produit.getId_produit());

        // Champ de texte pour la somme totale
        Label sommeTotaleLabel = new Label("Somme totale : " + sommeTotaleProduit + " DT");
        sommeTotaleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        sommeTotaleLabel.setLayoutX(30);
        sommeTotaleLabel.setLayoutY(280);




        // Bouton Ajouter au Panier
        Button commandebutton = new Button("Order", new FontAwesomeIconView(FontAwesomeIcon.CART_PLUS));
        commandebutton.setLayoutX(50);
        commandebutton.setLayoutY(330);
        commandebutton.setPrefWidth(120);
        commandebutton.getStyleClass().add("commande-button"); // Style du bouton
        commandebutton.setOnAction(
                event -> {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/PanierProduit.fxml"));

                    try {

                        Parent root = null;
                        root = fxmlLoader.load();
                        //Parent rootNode = fxmlLoader.load();
                        //Scene scene = new Scene(rootNode);

                        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        Stage newStage = new Stage();
                        newStage.setScene(new Scene(root,1200,700));
                        newStage.setTitle("my cart");
                        newStage.show();

                        // Fermer la fenêtre actuelle
                        currentStage.close();
                    } catch (IOException e) {
                        e.printStackTrace(); // Affiche l'erreur dans la console (vous pourriez le remplacer par une boîte de dialogue)
                        System.out.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
                    }


                });


        // Bouton Ajouter au Panier
        Button achatbutton = new Button("continue shopping");
        achatbutton.setLayoutX(50);
        achatbutton.setLayoutY(380);
        achatbutton.getStyleClass().add("achat-button"); // Style du bouton
        achatbutton.setOnAction(
                event -> {

                    anchordetail.setVisible(true);
                    anchordetail.setOpacity(1);
                    panierFlowPane.setVisible(false);



                });




        card.getChildren().addAll(imageView, nameLabel, priceLabel,quantiteLabel,sommeTotaleLabel,achatbutton,commandebutton);
        panierContainer.getChildren().add(card);
        return panierContainer;
    }



}

