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
import org.kordamp.ikonli.javafx.FontIcon;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
/**
 * Displays a card containing the name, price, and image of a product. The class
 * creates an AnchorPane and adds a Label, a PriceLabel, and an ImageView to it. The
 * ImageView is clickable and will show the details of the product when clicked.
 */
public class AfficherProduitClientControllers implements Initializable {
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> statusCheckBoxes = new ArrayList<>();
    private final List<Produit> panierList = new ArrayList<Produit>();
    private final Chat chat = new Chat();
    @FXML
    public FlowPane panierFlowPane;
    @FXML
    public TextField SearchBar;
    @FXML
    public Button forward_message;
    @FXML
    private AnchorPane FilterAnchor;
    @FXML
    private FlowPane produitFlowPane;
    @FXML
    private FlowPane topproduitFlowPane;
    @FXML
    private FontIcon idfilter;
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
    /**
     * Searches for products in a list based on a given search term and returns a list
     * of matching products.
     * 
     * @param liste list of products that will be searched for matches with the given
     * search query.
     * 
     * 	- It is a list of `Produit` objects.
     * 
     * @param recherche search query used to filter the list of `Produit` objects returned
     * by the function.
     * 
     * @returns a list of `Produit` objects that match the given search query.
     * 
     * 	- The output is a list of `Produit` objects, representing the matching products
     * in the input list.
     * 	- The list contains only the elements from the input list that match the search
     * query.
     * 	- Each element in the list has a `Nom` attribute that contains the search query.
     */
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
    /**
     * Sets up UI components for searching and displaying produits, including a search
     * bar, a filter category combo box, and a flow pane to display results. It also
     * initializes a produitService object and sets up listeners for the search bar and
     * category combo box to update the displayed produits.
     * 
     * @param location URL of the web page that the function is initializing, which is
     * used to load the accepted products and display them on the screen.
     * 
     * 	- `URL location`: This represents a URL that provides information about the
     * accepted products.
     * 
     * @param resources ResourceBundle object that provides localized messages and key
     * for displaying comments and top3 produits.
     * 
     * 	- `location`: The URL of the location where the application is running.
     * 	- `resources`: A `ResourceBundle` object containing key-value pairs of resources
     * used in the application.
     */
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
    /**
     * Loads all products from a service and adds them to a flow pane using a recursive
     * method.
     */
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
    /**
     * Creates a Card component that displays the details of a product, including its
     * image, name, description, price, and buttons for adding to cart or viewing comments.
     * 
     * @param Produit Produit object that contains the details of the product being
     * displayed, and it is used to access the product's properties and methods throughout
     * the function, such as retrieving its name, price, and ID.
     * 
     * 	- `id_produit`: The unique identifier for the product
     * 	- `nom`: The product name
     * 	- `description`: The product description
     * 	- `prix`: The product price
     * 	- `image`: The product image URL
     * 
     * @returns a `Pane` object containing the UI components for displaying a single
     * product in a shopping cart.
     * 
     * 	- `card`: The card container that displays information about the selected product,
     * including an image view, name label, description label, price label, add to cart
     * button, and chat icon.
     * 	- `imageView`: An image view that displays a picture of the selected product.
     * 	- `nameLabel`: A label that displays the name of the selected product.
     * 	- `descriptionLabel`: A label that displays a brief description of the selected
     * product.
     * 	- `priceLabel`: A label that displays the price of the selected product.
     * 	- `addToCartButton`: A button that allows users to add the selected product to
     * their cart.
     * 	- `chatIcon`: An icon that represents the chat function, which displays all
     * comments for the selected product when clicked.
     * 
     * The main attributes of the returned output are:
     * 
     * 	- The card container has a white background with a gradient border from the top
     * left corner to the bottom right corner.
     * 	- The image view, name label, description label, price label, and add to cart
     * button have a font size of 12px, bold font weight, and a padding of 10px.
     * 	- The chat icon has an icon literal of "fa-commenting" and an icon size of 20px.
     */
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
            String produitImage = Produit.getImage();
            if (!produitImage.isEmpty()) {
                Image image = new Image(produitImage);
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
        Button addToCartButton = new Button("Add to Cart", new FontIcon("fa-cart-plus"));
        /*
         * addToCartButton.setStyle("-fx-background-color: #dd4f4d;\n" +
         * "    -fx-text-fill: #FFFFFF;\n" +
         * "    -fx-font-size: 12px;\n" +
         * "    -fx-font-weight: bold;\n" +
         * "    -fx-padding: 10px 10px;");
         */
        addToCartButton.setLayoutX(65);
        addToCartButton.setLayoutY(330);
        addToCartButton.getStyleClass().add("sale");
        addToCartButton.setOnAction(event -> {
            Stage stage = (Stage) panierFlowPane.getScene().getWindow();
            System.out.println(stage.getUserData());
            int produitId = Produit.getId_produit();
            int quantity = 1; // Vous pouvez ajuster la quantité en fonction de vos besoins
            ajouterAuPanier(produitId, quantity);
            top3anchorpane.setVisible(false);
            panierFlowPane.setVisible(true);
        });
        FontIcon chatIcon = new FontIcon();
        chatIcon.setIconLiteral("fa-commenting");
        chatIcon.setIconSize(20);
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
    /**
     * Clears the current pane, adds the newly added product to a new pane, and makes the
     * updated pane visible while hiding other panes.
     * 
     * @param produitAjoute product that is being added to the shopping cart, which is
     * used to create a new `HBox` representing the product card and add it to the `panierFlowPane`.
     * 
     * 	- `Produit produitAjoute`: This is an instance of the `Produit` class, representing
     * a product added to the cart.
     */
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
    /**
     * Adds a product to the shopping cart based on the product ID and quantity. It first
     * verifies if the product is available, then creates a new panier object with the
     * product details and user data, and finally adds it to the panier service for display
     * in the user interface.
     * 
     * @param produitId ID of the product to be added to the cart.
     * 
     * @param quantity number of items to be added to the shopping cart.
     */
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
            Client client = (Client) panierFlowPane.getScene().getWindow().getUserData();
            panier.setUser(client);
            panierService.create(panier);
            afficherPanier(produit); // Utilisez le produit ajouté pour afficher dans le panier
        } else {
            // Afficher un message d'avertissement sur le stock insuffisant
            System.out.println("Stock insuffisant pour le produit avec l'ID : " + produitId);
        }
    }
    /**
     * Creates a card for each `Produit` object in the `produits` list and adds them to
     * the `produitFlowPane`.
     * 
     * @param produits list of products to create cards for, and is used to iterate over
     * the products in the list to create the cards.
     * 
     * 	- `produits`: A list of `Produit` objects, containing information about individual
     * products.
     */
    private void createProduitCards(List<Produit> produits) {
        for (Produit produit : produits) {
            VBox cardContainer = createProduitCard(produit);
            produitFlowPane.getChildren().add(cardContainer);
        }
    }
    /**
     * Generates a card for the shopping cart, containing the total quantity and price
     * of each product, a "Continue Shopping" button, and an "Order Now" button. When the
     * "Continue Shopping" button is clicked, the stage closes and the order summary is
     * displayed.
     * 
     * @param produit products to be added to the shopping cart, and it is used to display
     * the product name and quantity in the `PanierProduit` FXML file.
     * 
     * 	- `name`: the name of the product
     * 	- `quantite`: the quantity of the product in the cart
     * 	- `sommeTotale`: the total cost of the product in the cart
     * 	- `imageView`: an image view of the product
     * 	- `commander`: a button to add the product to the cart or continue shopping.
     * 
     * @returns a `Node` object representing a card with the contents of the shopping cart.
     * 
     * 	- `panierContainer`: The parent Node for the entire card, which contains all the
     * children components.
     * 	- `cartLabel`: A Label component displaying the word "Panier" in bold and a larger
     * font size.
     * 	- `closeIcon`: A FontIcon component displaying an icon of a times circle, used
     * for closing the card.
     * 	- `imageView`: An ImageView component displaying a shopping cart icon.
     * 	- `nameLabel`: A Label component displaying the text "Nom du produits".
     * 	- `quantiteLabel`: A Label component displaying the text "Quantité".
     * 	- `sommeTotaleLabel`: A Label component displaying the text "Somme totale".
     * 	- `achatbutton`: A Button component displaying the text "Continuer les achats"
     * and used for navigating to the next stage.
     * 	- `commandebutton`: A Button component displaying the text "Order" and used for
     * adding an item to the cart.
     */
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
            String produitImage = produit.getImage();
            if (!produitImage.isEmpty()) {
                Image image = new Image(produitImage);
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
        // double sommeTotaleProduit =
        // commandeItemService.calculerSommeTotaleProduit(produit.getId_produit());
        // Champ de texte pour la somme totale
        Label sommeTotaleLabel = new Label("Somme totale : " + produit.getPrix() + " DT");
        sommeTotaleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        sommeTotaleLabel.setLayoutX(30);
        sommeTotaleLabel.setLayoutY(270);
        // Bouton Ajouter au Panier
        Button commandebutton = new Button("Order", new FontIcon("fa-cart-plus"));
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
                        // Parent rootNode = fxmlLoader.load();
                        // Scene scene = new Scene(rootNode);
                        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        Stage newStage = new Stage();
                        currentStage.setScene(new Scene(root, 1280, 700));
                        currentStage.setTitle("my cart");
                        currentStage.show();
                        // Fermer la fenêtre actuelle
                    } catch (IOException e) {
                        e.printStackTrace(); // Affiche l'erreur dans la console (vous pourriez le
                        // remplacer par une boîte de dialogue)
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
        FontIcon closeIcon = new FontIcon();
        closeIcon.setIconLiteral("fa-times-circle");
        closeIcon.setIconSize(20);
        closeIcon.setLayoutX(230);
        closeIcon.setLayoutY(10);
        closeIcon.getStyleClass().add("close"); // Style de l'icône
        // Attachez un gestionnaire d'événements pour fermer la carte du panier
        closeIcon.setOnMouseClicked(event -> {
            fermerPanierCard(panierContainer);
        });
        card.getChildren().addAll(cartLabel, closeIcon, imageView, nameLabel, quantiteLabel, sommeTotaleLabel,
                achatbutton, commandebutton);
        panierContainer.getChildren().add(card);
        return panierContainer;
    }
    /**
     * Makes a panel invisible and sets another panel's visibility and opacity to 1,
     * effectively hiding the panel that was previously visible.
     * 
     * @param panierContainer HBox component that contains the panel containing the
     * shopping cart.
     * 
     * 	- `panierContainer`: A `HBox` component representing the container for the panier
     * (basket) display.
     */
    private void fermerPanierCard(HBox panierContainer) {
        panierFlowPane.setVisible(false);
        // Rendre detailFlowPane visible et ajuster l'opacité à 1
        produitFlowPane.setVisible(true);
        produitFlowPane.setOpacity(1);
        top3anchorpane.setVisible(true);
        top3anchorpane.setOpacity(1);
    }
    /**
     * Loads a new UI component (`PanierProduit.fxml`) when a button is clicked, creates
     * a new scene with the loaded component, and attaches it to a new stage. The new
     * stage is then displayed and the previous stage is closed.
     * 
     * @param event MouseEvent object that triggered the function, providing information
     * about the mouse button pressed, the screen position of the click, and other details.
     * 
     * 	- Event source: The element that triggered the event (not specified).
     */
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
    /**
     * Filters a list of products based on a search text, by adding to an observable list
     * only those products whose category name contains the search text.
     * 
     * @param searchText search term used to filter the list of products.
     */
    private void filterCategorieProduits(String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des produits pour ne garder que ceux dont le nom contient le
            // texte saisi
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
    /**
     * Retrieves a list of all product categories from the service layer using the
     * `ProduitService`. The list is then returned to the caller.
     * 
     * @returns a list of `Produit` objects retrieved from the service.
     */
    private List<Produit> getAllCategories() {
        ProduitService categorieservice = new ProduitService();
        List<Produit> categorie = categorieservice.read();
        return categorie;
    }
    /**
     * Sets the opacity of a pane to 0.5, makes a panel visible, clears lists of check
     * boxes and recieves unique addresses from a database for each category. It then
     * creates VBoxes for the addresses and adds them to a parent pane, making the parent
     * pane visible.
     * 
     * @param event mouse event that triggered the filtrer method, providing the source
     * of the event and any relevant data.
     * 
     * 	- `event`: A MouseEvent object representing the mouse event that triggered the function.
     * 	- `MouseEvent.getX()` and `MouseEvent.getY()`: The coordinates of the mouse event
     * in the parent coordinate system.
     */
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
    /**
     * Retrieves a list of category names from a database by mapping the categories to
     * their respective names using the `getNom_categorie()` method.
     * 
     * @returns a list of distinct category names obtained from the database.
     * 
     * The function returns a list of strings, where each string represents a category name.
     * The list contains all unique category names that were retrieved from the database
     * using the `getAllCategories()` method.
     * The categories are obtained by calling the `map()` method on the list of `Produit`
     * objects, which applies the `getCategorie().getNom_categorie()` method to each
     * object and returns a stream of category names.
     * The `distinct()` method is then called on the stream to remove any duplicates,
     * resulting in a list of unique category names.
     */
    public List<String> getCategorie_Produit() {
        // Récupérer tous les cinémas depuis la base de données
        List<Produit> categories = getAllCategories();
        List<String> categorie = categories.stream()
                .map(c -> c.getCategorie().getNom_categorie())
                .distinct()
                .collect(Collectors.toList());
        return categorie;
    }
    /**
     * Filters a list of products based on selected categories, updates the visible state
     * of an anchor and a flow pane, and updates the list of displayed products.
     * 
     * @param event an action event triggered by the user, which initiates the filtering
     * process of products based on selected categories.
     * 
     * 	- Type: ActionEvent
     * 	- Target: Unknown (since it's not explicitly specified)
     * 	- Code: Unknown (since it's not explicitly specified)
     */
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
    /**
     * Clears and re-adds a list of products to a flow pane, using a `VBox` container for
     * each product.
     * 
     * @param filteredProducts list of products that have been filtered based on user
     * input, and it is used to populate the `produitFlowPane` with only the relevant products.
     * 
     * 	- `filteredProducts`: A list of `Produit` objects that have been filtered based
     * on some criteria.
     */
    private void updateProduitFlowPane(List<Produit> filteredProducts) {
        produitFlowPane.getChildren().clear(); // Effacez les éléments existants
        for (Produit produit : filteredProducts) {
            VBox cardContainer = createProduitCard(produit);
            produitFlowPane.getChildren().add(cardContainer);
        }
    }
    /**
     * Retrieves a list of selected addresses from an `AnchorPane` component and filters
     * them based on the selected state of CheckBoxes within the pane.
     * 
     * @returns a list of selected category names.
     * 
     * 	- The output is a list of strings (type `List<String>`).
     * 	- The list contains the selected addresses from the `addressCheckBoxes` stream,
     * where each address is represented by a string.
     * 	- The addresses are filtered based on whether the corresponding CheckBox is
     * selected or not using the `filter()` method.
     * 	- The text of each selected CheckBox is mapped to a string using the `map()` method.
     */
    private List<String> getSelectedCategories() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return addressCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }
    /**
     * Charges a new FXML interface, creates a new scene, and attaches it to a new stage,
     * while also closing the current stage.
     * 
     * @param event ActionEvent object that triggered the function execution, providing
     * the source of the event and allowing the code to determine the appropriate action
     * to take.
     * 
     * 	- It is an `ActionEvent`, indicating that it represents an action taken on the
     * user interface.
     * 	- The source of the event is a `Node`, which represents the element in the user
     * interface that triggered the event.
     */
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
    /**
     * Loads a new FXML interface, creates a new scene, and attaches it to a new stage
     * when an event is triggered. The new stage replaces the current one, and the previous
     * stage is closed.
     * 
     * @param event event that triggered the `eventClient()` method to be called, providing
     * the necessary information for the method to perform its actions.
     * 
     * 	- Event source: The object that generated the event, which is typically a button
     * or other user interface element.
     * 	- Event type: The type of event that was generated, such as a click or a key press.
     */
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
    /**
     * Loads a new FXML file, creates a new scene and stage, and replaces the current
     * stage with the new one.
     * 
     * @param event ActionEvent object that triggered the function execution and provides
     * access to the source element that caused the event, which in this case is an button
     * click.
     * 
     * 	- `event`: This represents an action event that occurred in the application. It
     * provides information about the source of the event and its associated actions.
     */
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
    /**
     * Likely profiles a client application, possibly collecting data on its performance
     * or behavior.
     * 
     * @param event occurrence of an action event that triggered the execution of the
     * `profilclient` function.
     */
    @FXML
    void profilclient(ActionEvent event) {
    }
    /**
     * Loads a new FXML interface using `FXMLLoader`, creates a new scene with it, and
     * attaches the scene to a new stage. It also closes the current stage.
     * 
     * @param event ActionEvent object that triggered the function execution, providing
     * the source of the event and allowing the code to access the relevant information
     * related to the event.
     * 
     * 	- It is an `ActionEvent` representing a user interaction with the application.
     * 	- The source of the event is the `FXMLLoader` instance that loaded the `filmuser.fxml`
     * file.
     * 	- The event provides access to the stage and scene associated with the event,
     * which are used to create a new window and replace the current one.
     */
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
    /**
     * Loads a new FXML interface, creates a new scene with it, and attaches the scene
     * to a new stage, while closing the current stage.
     * 
     * @param event ActionEvent object that triggered the function execution, providing
     * information about the source of the event and allowing the code to handle the
     * appropriate action.
     * 
     * 	- It is an `ActionEvent` object representing a user action that triggered the
     * function execution.
     * 	- The source of the event is typically a button or other widget in the user interface.
     * 	- The event may carry additional information such as the ID of the button pressed,
     * the modifiers used, and so on.
     */
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
    /**
     * Allows users to input a comment on a product, checks for bad words and prevents
     * further processing if found. If no bad words are detected, it creates a new
     * `Commentaire` object with the client information and product ID, and adds it to
     * the database using the `CommentaireService`.
     */
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
            Client client = (Client) txtAreaComments.getScene().getWindow().getUserData();
            Commentaire commentaire = new Commentaire(client, userMessage, produitService.getProduitById(produitId));
            CommentaireService commentaireService = new CommentaireService();
            // Ajoutez le commentaire à la base de données
            commentaireService.create(commentaire);
        }
    }
    /**
     * Retrieves all comments for a given product ID, filters them to keep only those
     * related to the specified cinema, and returns the filtered list of comments.
     * 
     * @param idproduit id of the product for which the comments are to be filtered and
     * returned.
     * 
     * @returns a list of commentaries filtered based on the product ID.
     * 
     * 	- The `List<Commentaire>` returned represents all comments for a specific product
     * with the provided ID.
     * 	- The list contains `Commentaire` objects that have been filtered based on the
     * product ID.
     * 	- Each `Commentaire` object in the list has a `Produit` field with the ID of the
     * matching product.
     */
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
    /**
     * Retrieves and displays all comments associated with a product ID using a `VBox`
     * container to hold the comment views and a `getChildren()` method to add them.
     * 
     * @param idproduit product ID used to retrieve all comments associated with it.
     */
    private void displayAllComments(int idproduit) {
        List<Commentaire> comments = getAllComment(idproduit);
        VBox allCommentsContainer = new VBox();
        for (Commentaire comment : comments) {
            HBox commentView = addCommentToView(comment);
            allCommentsContainer.getChildren().add(commentView);
        }
        idcomment.setContent(allCommentsContainer);
    }
    /**
     * Adds a new comment to an item and displays all comments for that item upon button
     * click.
     * 
     * @param event mouse event that triggered the `AddComment` method, providing the
     * context for the comment creation and display.
     * 
     * 	- `MouseEvent event`: This parameter represents an event object that contains
     * information about the mouse action that triggered the function. Specifically, it
     * provides details on the button pressed (left or right), the location of the click
     * within the parent container, and the state of other buttons.
     */
    @FXML
    void AddComment(MouseEvent event) {
        addCommentaire();
        displayAllComments(produitId);
    }
    /**
     * Adds an comment to a view by creating an image view with the user's profile picture,
     * adding it to a container with an image circle and a card for the comment, and then
     * adding the container to the ScrollPane.
     * 
     * @param commentaire Commentaire object containing information about the user's
     * comment, including the client's name and the comment text.
     * 
     * 	- `client`: The client who made the comment (a `CommentaireClient`)
     * 	- `photo_de_profil`: The image URL of the user who made the comment (a `String`)
     * 
     * @returns a HBox container containing an ImageView, a Group of images and text, and
     * a VBox for the comment text.
     * 
     * 1/ `HBox contentContainer`: This is the container that holds the image and the
     * text of the comment. It has a prefheight of 50 pixels to set the maximum height
     * of the container.
     * 2/ `imageBox`: This is the group containing the image of the user and the image view.
     * 3/ `cardContainer`: This is the group containing the text box with the name of the
     * user and the comment, as well as any additional children added to it.
     * 4/ `textBox`: This is the VBox that contains the text box with the name of the
     * user and the comment.
     */
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
        cardContainer.setStyle(
                "-fx-background-color: white; -fx-padding: 5px ; -fx-border-radius: 8px; -fx-border-color: #000; -fx-background-radius: 8px; ");
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
    /**
     * Sets the opacity and visibility of a `FlowPane` component to control its appearance
     * and accessibility.
     * 
     * @param mouseEvent MouseEvent object that triggered the `Close` method.
     * 
     * 	- The `mouseEvent` instance represents an event triggered by a mouse action, such
     * as a click or a drag.
     * 	- It contains information about the event, including the location of the event
     * on the screen and the type of event that occurred.
     */
    public void Close(MouseEvent mouseEvent) {
        produitFlowPane.setOpacity(1);
        produitFlowPane.setVisible(true);
        AnchorComments.setVisible(false);
    }
    /**
     * Sets the opacity and visibility of a `FlowPane` and its child elements, and hides
     * an `Anchor` element.
     * 
     * @param mouseEvent mouse event that triggered the `CloseFilter()` method, providing
     * information about the location and nature of the event.
     * 
     * 	- `mouseEvent`: The event object representing the mouse action that triggered the
     * function.
     */
    public void CloseFilter(MouseEvent mouseEvent) {
        produitFlowPane.setOpacity(1);
        produitFlowPane.setVisible(true);
        FilterAnchor.setVisible(false);
    }
    /**
     * Retrieves and displays the top 3 products with the highest quantity and status
     * from a service, creating a VBox for each product and adding it to a parent container.
     */
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
    /**
     * Generates a `VBox` container with three components: an image view, a label with
     * the product name, and a label with the price. The image view displays an image of
     * the product, while the labels display the product name and price.
     * 
     * @param produit `Produit` object that contains information about the product to be
     * displayed, including its name, image, and price.
     * 
     * 	- `nom`: the product name
     * 	- `image`: the image URL or a default image if null
     * 	- `prix`: the product price in DT (Djibouti Francs)
     * 
     * @returns a `VBox` container with three elements: an image, a product name, and a
     * price label.
     * 
     * 1/ `cardContainer`: This is the top-level container for the three components that
     * make up the card. It has a style class of `-fx-padding: 20px 0 0 30px;` which adds
     * left padding to the container.
     * 2/ `imageView`: This is an `ImageView` component that displays the image of the
     * product. The image view has a layoutX of 5 and a layoutY of 21, and its fit width
     * and height are set to 50. It also has an OnMouseClicked event listener that triggers
     * when the image is clicked.
     * 3/ `nameLabel`: This is a `Label` component that displays the name of the product.
     * The label has a font size of 15 and a style class of `-fx-text-fill: #333333;`,
     * which sets the text fill color to black. It also has a layoutX of 60 and a layoutY
     * of 25.
     * 4/ `priceLabel`: This is another `Label` component that displays the price of the
     * product. The label has a font size of 14 and a style class of `-fx-text-fill:
     * black;`, which sets the text fill color to black. It also has a layoutX of 60 and
     * a layoutY of 55.
     * 
     * In summary, the `createtopthree` function returns a container with three components
     * – an image view, a label for the product name, and another label for the price –
     * with specific properties and attributes set to display the information about the
     * product.
     */
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
            String produitImage = produit.getImage();
            if (!produitImage.isEmpty()) {
                Image image = new Image(produitImage);
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
