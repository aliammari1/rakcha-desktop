package com.esprit.controllers.produits;

import com.esprit.models.produits.CommandeItem;
import com.esprit.models.produits.Produit;
import com.esprit.services.produits.CommandeItemService;
import com.esprit.services.produits.ProduitService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
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

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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

    public FlowPane panierFlowPane;
    @FXML
    public TextField SearchBar;

    @FXML
    private FlowPane produitFlowPane;

    @FXML
    private FlowPane topproduitFlowPane;

    private List<Produit> l1 = new ArrayList<>();

    private int produitId;

    private List<Produit> panierList = new ArrayList<Produit>();







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

        cardContainer.setStyle("-fx-padding: 20px 0 0  30px;"); // Ajout de remplissage à gauche pour le décalage

        AnchorPane card = new AnchorPane();


        // Image du Produit
        ImageView imageView = new ImageView();
        imageView.setLayoutX(20);
        imageView.setLayoutY(20);
        imageView.setFitWidth(250);
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
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 1280, 700));
                stage.setTitle("Détails du Produit");
                stage.show();
                currentStage.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });


        // Prix du Produit
        Label priceLabel = new Label(" " + Produit.getPrix() + " DT");
        priceLabel.setLayoutX(20);
        priceLabel.setLayoutY(270);
        priceLabel.setFont(Font.font("Arial", 16));
        priceLabel.setStyle("-fx-text-fill: black;");

        // Description du produit
        Label descriptionLabel = new Label(Produit.getDescription());
        descriptionLabel.setFont(Font.font("Arial", 14));
        descriptionLabel.setTextFill(Color.web("#867e7e")); // Définir la même couleur de texte que descriptionText
        descriptionLabel.setLayoutX(30);
        descriptionLabel.setLayoutY(190);
        descriptionLabel.setMaxWidth(250); // Ajuster la largeur maximale selon vos besoins
        descriptionLabel.setWrapText(true); // Activer le retour à la ligne automatique



        // Nom du Produit
        Label nameLabel = new Label(Produit.getNom());
        nameLabel.setLayoutX(50);
        nameLabel.setLayoutY(170);
        nameLabel.setFont(Font.font("Verdana", 15));
        nameLabel.setStyle("-fx-text-fill: black;");
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique


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
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 1280, 700));
                stage.setTitle("Détails du Produit");
                stage.show();
                currentStage.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });




        // Bouton Ajouter au Panier
        Button addToCartButton = new Button("Ajouter au panier", new FontAwesomeIconView(FontAwesomeIcon.CART_PLUS));
        addToCartButton.setStyle("-fx-background-color: #dd4f4d;\n" +
                "    -fx-text-fill: #FFFFFF;\n" +
                "    -fx-font-size: 12px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-padding: 10px 10px;");
        addToCartButton.setLayoutX(50);
        addToCartButton.setLayoutY(300);

        addToCartButton.setOnAction(event -> {


                int produitId = Produit.getId_produit();
                int quantity = 1; // Vous pouvez ajuster la quantité en fonction de vos besoins
                ajouterAuPanier(produitId, quantity);
            });



        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel, addToCartButton);
        card.setStyle("-fx-background-color:#F6F2F2;\n" +
                " -fx-text-fill: #FFFFFF;\n" +
                "    -fx-font-size: 12px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-padding: 10px;\n" +
                "    -fx-border-color: linear-gradient(to top left, #624970, #ae2d3c);/* Couleur de la bordure */\n" +
                "    -fx-border-width: 2px; /* Largeur de la bordure */\n" +
                "    -fx-border-radius: 5px; /* Rayon de la bordure pour arrondir les coins */");



        cardContainer.getChildren().add(card);

        return cardContainer;
    }



    private void afficherPanier(Produit produitAjoute) {
        // Effacez la FlowPane du panier actuelle pour afficher les nouveaux résultats
        panierFlowPane.getChildren().clear();

        // Ajoutez le produit ajouté au panier à la FlowPane du panier
        HBox panierCard = createPanierCard(produitAjoute);
        panierFlowPane.getChildren().add(panierCard);

        // Affichez la FlowPane du panier et masquez les autres FlowPanes
        panierFlowPane.setVisible(true);
        produitFlowPane.setVisible(false);
        topproduitFlowPane.setVisible(false);
    }

    private void ajouterAuPanier(int produitId, int quantity) {
        ProduitService produitService = new ProduitService();

        // Vérifier le stock disponible avant d'ajouter au panier
        if (produitService.verifierStockDisponible(produitId, quantity)) {
            Produit produit = produitService.getProduitById(produitId);

            // Créer un objet CommandeItem pour représenter l'élément du panier
            CommandeItem commandeItem = new CommandeItem(produit, quantity);

            // Ajouter l'élément au panier
            CommandeItemService commandeItemservice = new CommandeItemService();
            commandeItemservice.create(commandeItem);

            // Afficher le panier mis à jour
            afficherPanier(produit); // Utilisez le produit ajouté pour afficher dans le panier
        } else {
            // Afficher un message d'avertissement sur le stock insuffisant
            System.out.println("Stock insuffisant pour le produit avec l'ID : " + produitId);
        }
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

    @FXML
    void panier(MouseEvent event) {

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
        commandebutton.setStyle("-fx-background-color: #624970;\n" +
                " -fx-text-fill: #FCE19A;" +
                "   -fx-font-size: 12px;\n" +
                "     -fx-font-weight: bold;\n"+
                " -fx-background-color: #6f7b94");
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
                        newStage.setScene(new Scene(root,1280,700));
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
        achatbutton.setStyle(" -fx-background-color: #466288;\n" +
                "    -fx-text-fill: #FCE19A;\n" +
                "    -fx-font-size: 12px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-padding: 10px 10px;"); // Style du bouton
        achatbutton.setOnAction(
                event -> {


                    panierFlowPane.setVisible(false);


                });



        card.getChildren().addAll(imageView, nameLabel, priceLabel,quantiteLabel,sommeTotaleLabel,achatbutton,commandebutton);
        panierContainer.getChildren().add(card);
        return panierContainer;
    }















}



