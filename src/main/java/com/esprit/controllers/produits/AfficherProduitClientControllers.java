package com.esprit.controllers.produits;



import com.esprit.models.produits.Avis;
import com.esprit.models.produits.Panier;
import com.esprit.models.produits.Produit;


import com.esprit.services.produits.AvisService;
import com.esprit.services.produits.PanierService;
import com.esprit.services.produits.ProduitService;
import com.esprit.services.users.UserService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
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
import org.controlsfx.control.Rating;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class AfficherProduitClientControllers implements Initializable {


    @FXML
    private AnchorPane FilterAnchor;
    @FXML

    public FlowPane panierFlowPane;
    @FXML
    public TextField SearchBar;

    @FXML
    private FlowPane produitFlowPane;

    @FXML
    private FlowPane topproduitFlowPane;

    @FXML
    private FontAwesomeIconView idfilter;


    private List<Produit> l1 = new ArrayList<>();

    private int produitId;

    @FXML
    private ScrollPane produitscrollpane;

    private List<Produit> panierList = new ArrayList<Produit>();







    public void initialize(URL location, ResourceBundle resources) {

        loadAcceptedProduits();


        ProduitService produitService = new ProduitService();
        l1 = produitService.read();

        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Produit> produitsRecherches = rechercher(l1, newValue);
            // Effacer la FlowPane actuelle pour afficher les nouveaux résultats
            produitFlowPane.getChildren().clear();
            createProduitCards(produitsRecherches);
            filterCategorieProduits(newValue.trim());


        });

    }

    private void loadAcceptedProduits() {
        // Récupérer toutes les produits depuis le service
        ProduitService produitService = new ProduitService();
        List<Produit> Produits = produitService.read();



        // Créer une carte pour chaque produit et l'ajouter à la FlowPane
        for (Produit produit : Produits) {
            VBox cardContainer = createProduitCard(produit);

            // Créer un AnchorPane pour le cardContainer et ajouter le produit comme UserData
            AnchorPane produitNode = new AnchorPane();
            produitNode.setUserData(produit);
            produitNode.getChildren().add(cardContainer);

            produitFlowPane.getChildren().add(produitNode);


        }
       /* for (int i = 0; i < 3; i++) {
            topproduitFlowPane.getChildren().add(createTopThree(i));
        }*/


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
        produitFlowPane.setVisible(true);
        topproduitFlowPane.setVisible(false);

        produitFlowPane.setOpacity(0.2);
    }

    private void ajouterAuPanier(int produitId, int quantity) {
        ProduitService produitService = new ProduitService();
        PanierService panierService=new PanierService();
        UserService usersService=new UserService();
        // Vérifier le stock disponible avant d'ajouter au panier
        if (produitService.verifierStockDisponible(produitId, quantity)) {
            Produit produit = produitService.getProduitById(produitId);
            Panier panier=new Panier();
            panier.setProduit(produit);
            panier.setQuantity(quantity);
            panier.setUser(usersService.getUserById(4));
            panierService.create(panier);
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



    private HBox createPanierCard(Produit produit) {
        // Créer une carte pour le produit avec ses informations

        HBox panierContainer = new HBox();
        panierContainer.setStyle("-fx-padding: 10px 0 0 20px;"); // Ajout de remplissage à gauche pour le décalage

        AnchorPane card = new AnchorPane();


        // Ajouter un label "Card"
        Label cartLabel = new Label("My Cart");
        cartLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 25));
        cartLabel.setTextFill(Color.web("#B40C0C")); // Définir la couleur du texte
        cartLabel.setPrefWidth(230);
        cartLabel.setLayoutY(30);

        // Centrer le texte dans le label
        cartLabel.setAlignment(Pos.CENTER);





        // Image du Produit
        ImageView imageView = new ImageView();
        imageView.setLayoutX(50);
        imageView.setLayoutY(70);
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
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(10);
        nameLabel.setLayoutY (220);
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique




        // Champ de texte pour la quantité
        Label quantiteLabel = new Label("Quantité : 1"  );
        quantiteLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        quantiteLabel.setLayoutX(30);
        quantiteLabel.setLayoutY(330);

        // Récupérer la somme totale du prix du produit dans le panier
        //double sommeTotaleProduit = commandeItemService.calculerSommeTotaleProduit(produit.getId_produit());

        // Champ de texte pour la somme totale
        Label sommeTotaleLabel = new Label("Somme totale : " + produit.getPrix() + " DT");
        sommeTotaleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        sommeTotaleLabel.setLayoutX(30);
        sommeTotaleLabel.setLayoutY(350);




        // Bouton Ajouter au Panier
        Button commandebutton = new Button("Order", new FontAwesomeIconView(FontAwesomeIcon.CART_PLUS));
        commandebutton.setLayoutX(50);
        commandebutton.setLayoutY(380);
        commandebutton.setPrefWidth(120);
        commandebutton.setPrefHeight(30);
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
        achatbutton.setLayoutY(440);
        commandebutton.setPrefHeight(50);


        achatbutton.setStyle(" -fx-background-color: #466288;\n" +
                "    -fx-text-fill: #FCE19A;\n" +
                "    -fx-font-size: 12px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-padding: 10px 10px;"); // Style du bouton
        achatbutton.setOnAction(
                event -> {

                    // Rendre panierFlowPane invisible
                    panierFlowPane.setVisible(false);

                    // Rendre detailFlowPane visible et ajuster l'opacité à 1

                    topproduitFlowPane.setVisible(true);
                    produitFlowPane.setVisible(true);
                    produitFlowPane.setOpacity(1);




                });



        // Icône de fermeture (close)
        FontAwesomeIconView closeIcon = new FontAwesomeIconView();
        closeIcon.setGlyphName("TIMES_CIRCLE");
        closeIcon.setSize("20");
        closeIcon.setLayoutX(230);
        closeIcon.setLayoutY(10);
        closeIcon.getStyleClass().add("close"); // Style de l'icône


        // Attachez un gestionnaire d'événements pour fermer la carte du panier
        closeIcon.setOnMouseClicked(event -> fermerPanierCard(panierContainer));




        card.getChildren().addAll(cartLabel,closeIcon,imageView, nameLabel,quantiteLabel,sommeTotaleLabel,achatbutton,commandebutton);
        panierContainer.getChildren().add(card);
        return panierContainer;
    }


    private void fermerPanierCard(HBox panierContainer) {
        // Rendre la carte du panier invisible
        // Rendre panierFlowPane invisible
        panierFlowPane.setVisible(false);

        // Rendre detailFlowPane visible et ajuster l'opacité à 1
        topproduitFlowPane.setVisible(true);
        produitFlowPane.setVisible(true);
        produitFlowPane.setOpacity(1);



    }


    @FXML
    void panier(MouseEvent event) {

        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PanierProduit.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Panier des produits");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }


    }


    @FXML
    void commentaire(MouseEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentaireProduit.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("chat ");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }








    }


    private void filterCategorieProduits(String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des produits pour ne garder que ceux dont le nom contient le texte saisi
            ObservableList<Node> filteredList = FXCollections.observableArrayList();
            for (Node node : produitFlowPane.getChildren()) {
                Produit produit = (Produit) node.getUserData(); // Récupérer le Produit associé au Node
                if (produit.getCategorie().getNom_categorie().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(node);
                }
            }
            // Mettre à jour le FlowPane avec la liste filtrée
            produitFlowPane.getChildren().setAll(filteredList);
        } else {
            // Si le champ de recherche est vide, afficher tous les produits
            loadAcceptedProduits();
        }
    }


    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> statusCheckBoxes = new ArrayList<>();



    private List<Produit> getAllCategories() {
        ProduitService categorieservice = new ProduitService();
        List<Produit> categorie = categorieservice.read();
        return categorie;
    }
    @FXML
    void filtrer(MouseEvent event) {

        produitFlowPane.setOpacity(0.5);

        FilterAnchor.setVisible(true);

        // Nettoyer les listes des cases à cocher
        addressCheckBoxes.clear();
        statusCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        List<String> categorie = getCategorie_Produit();


        // Créer des VBox pour les adresses
        VBox addressCheckBoxesVBox = new VBox();
        Label addressLabel = new Label("Adresse");
        addressLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        addressCheckBoxesVBox.getChildren().add(addressLabel);
        for (String address : categorie) {
            CheckBox checkBox = new CheckBox(address);
            addressCheckBoxesVBox.getChildren().add(checkBox);
            addressCheckBoxes.add(checkBox);
        }
        addressCheckBoxesVBox.setLayoutX(25);
        addressCheckBoxesVBox.setLayoutY(60);


        // Ajouter les VBox dans le FilterAnchor
        FilterAnchor.getChildren().addAll(addressCheckBoxesVBox);
        FilterAnchor.setVisible(true);
    }




    public List<String> getCategorie_Produit() {
        // Récupérer tous les cinémas depuis la base de données
        List<Produit> categories = getAllCategories();



        List<String> categorie = categories.stream()
                .map(c -> c.getCategorie().getNom_categorie())
                .distinct()
                .collect(Collectors.toList());

        return categorie;
    }

    @FXML
    public void filtercinema(ActionEvent event) {

        produitFlowPane.setOpacity(1);

        FilterAnchor.setVisible(false);
        produitFlowPane.setVisible(true);

        // Récupérer les catégories sélectionnées
        List<String> selectedCategories = getSelectedCategories();

        // Filtrer les produits en fonction des catégories sélectionnées
        List<Produit> filteredProducts = getAllCategories().stream()
                .filter(produit -> selectedCategories.contains(produit.getCategorie().getNom_categorie()))
                .collect(Collectors.toList());

        // Mettre à jour la liste des produits affichés
        updateProduitFlowPane(filteredProducts);
    }

    private void updateProduitFlowPane(List<Produit> filteredProducts) {
        produitFlowPane.getChildren().clear(); // Effacez les éléments existants

        for (Produit produit : filteredProducts) {
            VBox cardContainer = createProduitCard(produit);
            produitFlowPane.getChildren().add(cardContainer);
        }
    }


    private List<String> getSelectedCategories() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return addressCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    @FXML
    void cinemaclient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentaireProduit.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("cinema ");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }


    }

    @FXML
    void eventClient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEvenementClient.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Event ");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }


    }

    @FXML
    void produitClient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduitClient.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("products ");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }


    }

    @FXML
    void profilclient(ActionEvent event) {



    }


    @FXML
    void MovieClient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/filmuser.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("movie ");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }
    }

    @FXML
    void SerieClient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Series-view.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("series ");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }

    }




}



