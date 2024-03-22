package com.esprit.controllers.produits;


import com.esprit.models.produits.Commentaire;
import com.esprit.models.produits.Panier;
import com.esprit.models.produits.Produit;
import com.esprit.models.users.Client;
import com.esprit.services.produits.CommentaireService;
import com.esprit.services.produits.PanierService;
import com.esprit.services.produits.ProduitService;
import com.esprit.services.users.UserService;
import com.esprit.utils.Chat;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class AfficherProduitClientControllers implements Initializable {

    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> statusCheckBoxes = new ArrayList<>();
    private final List<Produit> panierList = new ArrayList<Produit>();
    private final Chat chat = new Chat();
    @FXML
    public FontAwesomeIconView forwardMessage;
    @FXML

    public FlowPane panierFlowPane;
    @FXML
    public TextField SearchBar;
    @FXML
    private AnchorPane FilterAnchor;
    @FXML
    private FlowPane produitFlowPane;
    @FXML
    private FlowPane topproduitFlowPane;
    @FXML
    private FontAwesomeIconView idfilter;
    private List<Produit> l1 = new ArrayList<>();
    private int produitId;
    @FXML
    private AnchorPane top3anchorpane;


    @FXML
    private FlowPane topthreeVbox;

    @FXML
    private TextArea txtAreaComments;
    @FXML
    private ScrollPane idcomment;
    @FXML
    private AnchorPane AnchorComments;

    @FXML
    private AnchorPane produitAnchor;
    @FXML
    private ComboBox<String> tricomboBox;


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

    public void initialize(URL location, ResourceBundle resources) {

        loadAcceptedProduits();
        displayAllComments(produitId);
        loadAcceptedTop3();


        ProduitService produitService = new ProduitService();
        l1 = produitService.read();

        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Produit> produitsRecherches = rechercher(l1, newValue);
            // Effacer la FlowPane actuelle pour afficher les nouveaux résultats
            produitFlowPane.getChildren().clear();
            createProduitCards(produitsRecherches);
            filterCategorieProduits(newValue.trim());


        });
        tricomboBox.getItems().addAll("nom", "prix");
        tricomboBox.setValue("");
        tricomboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            produitFlowPane.getChildren().clear();
            List<Produit> filmList = new ProduitService().sort(t1);
            for (Produit film : filmList) {
                produitFlowPane.getChildren().add(createProduitCard(film));
            }

        });


    }

    private void loadAcceptedProduits() {

        ProduitService produitService = new ProduitService();

        List<Produit> produits = produitService.read();

        // Charger tous les produits dans produitFlowPane
        for (Produit produit : produits) {

            VBox cardContainer = createProduitCard(produit);

            AnchorPane produitNode = new AnchorPane();
            produitNode.setUserData(produit);
            produitNode.getChildren().add(cardContainer);

            produitFlowPane.getChildren().add(produitNode);
            produitFlowPane.setPadding(new Insets(10, 10, 10, 10));
        }


    }

    private VBox createProduitCard(Produit Produit) {
        // Créer une carte pour le produit avec ses informations

        VBox cardContainer = new VBox(5);

        cardContainer.setStyle("-fx-padding: 20px 0 0  30px;"); // Ajout de remplissage à gauche pour le décalage


        AnchorPane card = new AnchorPane();


        // Image du Produit
        ImageView imageView = new ImageView();
        imageView.setLayoutX(45);
        imageView.setLayoutY(30);
        imageView.setFitWidth(220);
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
        priceLabel.setLayoutY(300);
        priceLabel.setFont(Font.font("Arial", 15));
        priceLabel.setStyle("-fx-text-fill: black;");

        // Description du produit
        Label descriptionLabel = new Label(Produit.getDescription());
        descriptionLabel.setFont(Font.font("Arial", 14));
        descriptionLabel.setStyle("-fx-text-fill: black;"); // Définir la même couleur de texte que descriptionText
        descriptionLabel.setLayoutX(30);
        descriptionLabel.setLayoutY(220);
        descriptionLabel.setMaxWidth(230); // Ajuster la largeur maximale selon vos besoins
        descriptionLabel.setWrapText(true); // Activer le retour à la ligne automatique


        // Nom du Produit
        Label nameLabel = new Label(Produit.getNom());
        nameLabel.setLayoutX(23);
        nameLabel.setLayoutY(190);
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
        Button addToCartButton = new Button("Add to Cart", new FontAwesomeIconView(FontAwesomeIcon.CART_PLUS));
       /* addToCartButton.setStyle("-fx-background-color: #dd4f4d;\n" +
                "    -fx-text-fill: #FFFFFF;\n" +
                "    -fx-font-size: 12px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-padding: 10px 10px;");*/
        addToCartButton.setLayoutX(65);
        addToCartButton.setLayoutY(330);

        addToCartButton.getStyleClass().add("sale");


        addToCartButton.setOnAction(event -> {


            int produitId = Produit.getId_produit();
            int quantity = 1; // Vous pouvez ajuster la quantité en fonction de vos besoins
            ajouterAuPanier(produitId, quantity);
            top3anchorpane.setVisible(false);
            panierFlowPane.setVisible(true);


        });


        FontAwesomeIconView chatIcon = new FontAwesomeIconView();
        chatIcon.setGlyphName("COMMENTING");
        chatIcon.setSize("20");
        chatIcon.setLayoutX(240);
        chatIcon.setLayoutY(365);
        chatIcon.setOnMouseClicked(event -> {
            produitFlowPane.setOpacity(0.5);
            AnchorComments.setVisible(true);

            int produitId = Produit.getId_produit();

            displayAllComments(produitId);


            idcomment.setStyle("-fx-background-color: #fff;");


        });


        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel, addToCartButton, chatIcon);

        card.setStyle("-fx-background-color:#F6F2F2;\n" +
                " -fx-text-fill: #FFFFFF;\n" +
                "    -fx-font-size: 12px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-padding: 10px;\n" +
                "    -fx-border-color: linear-gradient(to top left, #624970, #ae2d3c);/* Couleur de la bordure */\n" +
                "    -fx-border-width: 2px; /* Largeur de la bordure */\n" +
                "    -fx-border-radius: 5px; /* Rayon de la bordure pour arrondir les coins */");
        card.getStyleClass().add("bg-white");


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
        // topproduitFlowPane.setVisible(false);
        top3anchorpane.setVisible(false);

        produitFlowPane.setOpacity(0.2);
    }

    private void ajouterAuPanier(int produitId, int quantity) {
        ProduitService produitService = new ProduitService();
        PanierService panierService = new PanierService();
        UserService usersService = new UserService();
        // Vérifier le stock disponible avant d'ajouter au panier
        if (produitService.verifierStockDisponible(produitId, quantity)) {
            Produit produit = produitService.getProduitById(produitId);
            Panier panier = new Panier();
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
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 23));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(10);
        nameLabel.setLayoutY(220);
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique


        // Champ de texte pour la quantité
        Label quantiteLabel = new Label("Quantité : 1");
        quantiteLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        quantiteLabel.setLayoutX(30);
        quantiteLabel.setLayoutY(250);

        // Récupérer la somme totale du prix du produit dans le panier
        //double sommeTotaleProduit = commandeItemService.calculerSommeTotaleProduit(produit.getId_produit());

        // Champ de texte pour la somme totale
        Label sommeTotaleLabel = new Label("Somme totale : " + produit.getPrix() + " DT");
        sommeTotaleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        sommeTotaleLabel.setLayoutX(30);
        sommeTotaleLabel.setLayoutY(270);


        // Bouton Ajouter au Panier
        Button commandebutton = new Button("Order", new FontAwesomeIconView(FontAwesomeIcon.CART_PLUS));
        commandebutton.setLayoutX(50);
        commandebutton.setLayoutY(300);
        commandebutton.setPrefWidth(120);
        commandebutton.setPrefHeight(50);
        commandebutton.setStyle("-fx-background-color: #624970;\n" +
                " -fx-text-fill: #FCE19A;" +
                "   -fx-font-size: 12px;\n" +
                "     -fx-font-weight: bold;\n" +
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
                        newStage.setScene(new Scene(root, 1280, 700));
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
        achatbutton.setLayoutY(370);
        achatbutton.setPrefHeight(50);


        achatbutton.setStyle(" -fx-background-color: #466288;\n" +
                "    -fx-text-fill: #FCE19A;\n" +
                "    -fx-font-size: 12px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-padding: 10px 10px;"); // Style du bouton
        achatbutton.setOnAction(
                event -> {

                    fermerPanierCard(panierContainer);


                });


        // Icône de fermeture (close)
        FontAwesomeIconView closeIcon = new FontAwesomeIconView();
        closeIcon.setGlyphName("TIMES_CIRCLE");
        closeIcon.setSize("20");
        closeIcon.setLayoutX(230);
        closeIcon.setLayoutY(10);
        closeIcon.getStyleClass().add("close"); // Style de l'icône


        // Attachez un gestionnaire d'événements pour fermer la carte du panier
        closeIcon.setOnMouseClicked(event -> {
            fermerPanierCard(panierContainer);


        });


        card.getChildren().addAll(cartLabel, closeIcon, imageView, nameLabel, quantiteLabel, sommeTotaleLabel, achatbutton, commandebutton);
        panierContainer.getChildren().add(card);
        return panierContainer;
    }

    private void fermerPanierCard(HBox panierContainer) {

        panierFlowPane.setVisible(false);

        // Rendre detailFlowPane visible et ajuster l'opacité à 1
        produitFlowPane.setVisible(true);
        produitFlowPane.setOpacity(1);
        top3anchorpane.setVisible(true);
        top3anchorpane.setOpacity(1);


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
        VBox addressCheckBoxesVBox = new VBox(5);
        Label addressLabel = new Label("Category");
        addressLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        addressCheckBoxesVBox.getChildren().add(addressLabel);
        for (String address : categorie) {
            CheckBox checkBox = new CheckBox(address);
            addressCheckBoxesVBox.getChildren().add(checkBox);
            addressCheckBoxes.add(checkBox);
        }
        addressCheckBoxesVBox.setLayoutX(25);
        addressCheckBoxesVBox.setLayoutY(50);


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


    @FXML
    void addCommentaire() {
        // Check for bad words and prevent further processing if found
        String userMessage = txtAreaComments.getText();
        System.out.println("User Message: " + userMessage);


        String badwordDetection = chat.badword(userMessage);
        System.out.println("Badword Detection Result: " + badwordDetection);

        ProduitService produitService = new ProduitService();


        if (badwordDetection.equals("1")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Commentaire non valide");
            alert.setContentText("Votre commentaire contient des gros mots et ne peut pas être publié.");
            alert.showAndWait();
        } else {
            // Créez un objet Commentaire
            Commentaire commentaire = new Commentaire((Client) new UserService().getUserById(4), userMessage, produitService.getProduitById(produitId), Date.valueOf(LocalDate.now()));
            CommentaireService commentaireService = new CommentaireService();
            // Ajoutez le commentaire à la base de données
            commentaireService.create(commentaire);


        }


    }


    private List<Commentaire> getAllComment(int idproduit) {
        CommentaireService commentaireService = new CommentaireService();
        List<Commentaire> allComments = commentaireService.read(); // Récupérer tous les commentaires
        List<Commentaire> comments = new ArrayList<>();

        // Filtrer les commentaires pour ne conserver que ceux du cinéma correspondant
        for (Commentaire comment : allComments) {
            if (comment.getProduit().getId_produit() == idproduit) {
                comments.add(comment);
            }
        }

        return comments;
    }

    private void displayAllComments(int idproduit) {
        List<Commentaire> comments = getAllComment(idproduit);
        VBox allCommentsContainer = new VBox();

        for (Commentaire comment : comments) {
            HBox commentView = addCommentToView(comment);
            allCommentsContainer.getChildren().add(commentView);
        }

        idcomment.setContent(allCommentsContainer);
    }


    @FXML
    void AddComment(MouseEvent event) {

        addCommentaire();
        displayAllComments(produitId);

    }


    private HBox addCommentToView(Commentaire commentaire) {
        // Création du cercle pour l'image de l'utilisateur
        Circle imageCircle = new Circle(20);
        imageCircle.setFill(Color.TRANSPARENT);
        imageCircle.setStroke(Color.BLACK);
        imageCircle.setStrokeWidth(2);

        // Image de l'utilisateur
        String imageUrl = commentaire.getClient().getPhoto_de_profil();
        Image userImage;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            userImage = new Image(imageUrl);
        } else {
            // Chargement de l'image par défaut
            userImage = new Image(getClass().getResourceAsStream("/Logo.png"));
        }

        // Création de l'image view avec l'image de l'utilisateur
        ImageView imageView = new ImageView(userImage);
        imageView.setFitWidth(50); // Ajuster la largeur de l'image
        imageView.setFitHeight(50); // Ajuster la hauteur de l'image

        // Positionner l'image au centre du cercle
        imageView.setTranslateX(2 - imageView.getFitWidth() / 2);
        imageView.setTranslateY(2 - imageView.getFitHeight() / 2);

        // Ajouter l'image au cercle
        Group imageGroup = new Group();
        imageGroup.getChildren().addAll(imageCircle, imageView);

        // Création de la boîte pour l'image et la bordure du cercle
        HBox imageBox = new HBox();
        imageBox.getChildren().add(imageGroup);

        // Création du conteneur pour la carte du commentaire
        HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-background-color: white; -fx-padding: 5px ; -fx-border-radius: 8px; -fx-border-color: #000; -fx-background-radius: 8px; ");

        // Nom de l'utilisateur
        Text userName = new Text(commentaire.getClient().getFirstName() + " " + commentaire.getClient().getLastName());
        userName.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-weight: bold;");


        // Commentaire
        Text commentText = new Text(commentaire.getCommentaire());
        commentText.setStyle("-fx-font-family: 'Arial'; -fx-max-width: 300 ;");
        commentText.setWrappingWidth(300); // Définir une largeur maximale pour le retour à la ligne automatique


        // Création de la boîte pour le texte du commentaire
        VBox textBox = new VBox();


        // Ajouter le nom d'utilisateur et le commentaire à la boîte de texte
        textBox.getChildren().addAll(userName, commentText);

        // Ajouter la boîte d'image et la boîte de texte à la carte du commentaire
        cardContainer.getChildren().addAll(textBox);

        // Création du conteneur pour l'image, le cercle et la carte du commentaire
        HBox contentContainer = new HBox();
        contentContainer.setPrefHeight(50);
        contentContainer.setStyle("-fx-background-color: transparent; -fx-padding: 10px"); // Arrière-plan transparent
        contentContainer.getChildren().addAll(imageBox, cardContainer);

        // Ajouter le conteneur principal au ScrollPane
        idcomment.setContent(contentContainer);
        return contentContainer;
    }


    public void Close(MouseEvent mouseEvent) {

        produitFlowPane.setOpacity(1);
        produitFlowPane.setVisible(true);

        AnchorComments.setVisible(false);


    }

    public void CloseFilter(MouseEvent mouseEvent) {

        produitFlowPane.setOpacity(1);
        produitFlowPane.setVisible(true);

        FilterAnchor.setVisible(false);


    }

    public void loadAcceptedTop3() {

        ProduitService produitService = new ProduitService();

        try {
            List<Produit> produits = produitService.getProduitsOrderByQuantityAndStatus();

            if (produits.size() < 3) {
                System.out.println("Pas assez de produits disponibles");
                return;
            }


            List<Produit> top3Produits = produits.subList(0, 3);
            int j = 0;
            for (Produit produit : top3Produits) {
                System.out.println(produit.getId_produit());
                VBox cardContainer = createtopthree(produit);
                System.out.println("------------------" + j + (cardContainer.getChildren()));
                topthreeVbox.getChildren().add(cardContainer);
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Une erreur est survenue lors du chargement des produits");
        }
    }


    public VBox createtopthree(Produit produit) {
        VBox cardContainer = new VBox(5);
        cardContainer.setStyle("-fx-padding: 20px 0 0 30px;"); // Add left padding

        cardContainer.setPrefWidth(255);


        AnchorPane card = new AnchorPane();
        card.setLayoutX(0);
        card.setLayoutY(0);
        card.getStyleClass().add("meilleurproduit");


        cardContainer.setSpacing(10);

        // Image of the product
        ImageView imageView = new ImageView();
        imageView.setLayoutX(5);
        imageView.setLayoutY(21);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        try {
            Blob blob = produit.getImage();
            if (blob != null) {
                byte[] bytes = blob.getBytes(1, (int) blob.length());
                Image image = new Image(new ByteArrayInputStream(bytes));
                imageView.setImage(image);
            } else {
                // Use a default image if Blob is null
                Image defaultImage = new Image(getClass().getResourceAsStream("defaultImage.png"));
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions during image loading
            System.out.println("Une erreur est survenue lors du chargement de l'image");
        }

        imageView.setOnMouseClicked(event -> {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsProduitClient.fxml"));

                Parent root = null;

                System.out.println("Clique sur le nom du produit. ID du produit : " + produit.getId_produit());
                root = loader.load();
                // Récupérez le contrôleur et passez l'id du produit lors de l'initialisation
                DetailsProduitClientController controller = loader.getController();
                controller.setProduitId(produit.getId_produit());

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


        // Product name
        Label nameLabel = new Label(produit.getNom());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(60); // Adjust X position
        nameLabel.setLayoutY(25);
        nameLabel.setMaxWidth(200); // Adjust max width
        nameLabel.setWrapText(true);
        nameLabel.setOnMouseClicked(event -> {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsProduitClient.fxml"));

                Parent root = null;

                System.out.println("Clique sur le nom du produit. ID du produit : " + produit.getId_produit());
                root = loader.load();
                // Récupérez le contrôleur et passez l'id du produit lors de l'initialisation
                DetailsProduitClientController controller = loader.getController();
                controller.setProduitId(produit.getId_produit());

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


        Label priceLabel = new Label(" " + produit.getPrix() + " DT");
        priceLabel.setLayoutX(60);
        priceLabel.setLayoutY(55);
        priceLabel.setFont(Font.font("Arial", 14));
        priceLabel.setStyle("-fx-text-fill: black;");


        card.getChildren().addAll(nameLabel, priceLabel, imageView);

        cardContainer.getChildren().addAll(card); // Add vertical space

        return cardContainer;
    }


}



