package com.esprit.controllers.products;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.Rating;
import org.kordamp.ikonli.javafx.FontIcon;

import com.esprit.models.products.Product;
import com.esprit.models.products.Review;
import com.esprit.models.products.ShoppingCart;
import com.esprit.models.users.Client;
import com.esprit.services.products.ProductService;
import com.esprit.services.products.ReviewService;
import com.esprit.services.products.ShoppingCartService;
import com.esprit.services.users.UserService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Is used to display details of a product when the user clicks on its name in
 * the list view. It retrieves the product ID from the event, and then loads an
 * FXML file to display additional information such as price and image. The
 * controller also handles mouse clicks on the image and displays a new stage
 * with additional information.
 */
public class DetailsProductClientController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(DetailsProductClientController.class.getName());
    private final int quantiteSelectionnee = 1; // Initialiser à 1 par défaut
    /**
     * FlowPane for displaying detailed product information.
     */
    @FXML
    public FlowPane detailFlowPane;

    /**
     * FlowPane for displaying shopping cart items.
     */
    @FXML
    public FlowPane shoppingcartFlowPane;

    /**
     * TextField for product search functionality.
     */
    @FXML
    public TextField SearchBar;
    ShoppingCartService shoppingcartService = new ShoppingCartService();
    ShoppingCart shoppingcart = new ShoppingCart();
    @FXML
    private AnchorPane anchordetail;
    @FXML
    private AnchorPane shoppingcartAnchorPane;
    @FXML
    private FontIcon retour;
    private Long produitId;
    private Button addToCartButton;
    @FXML
    private AnchorPane top3anchorpane;
    @FXML
    private FlowPane topthreeVbox;

    /**
     * @param produitId
     * @throws IOException
     */
    // Méthode pour initialiser l'ID du produit

    /**
     * Set the current product ID and initialize the product details view for that ID.
     *
     * @param produitId the product identifier to display
     * @throws IOException if an I/O error occurs while initializing product details
     */
    public void setProductId(final Long produitId) throws IOException {
        this.produitId = produitId;
        // Initialiser les détails du produit après avoir défini l'ID
        this.initDetailsProduct();
    }


    /**
     * Initialize the controller by loading the top-three accepted products and wiring the back icon to show the product list.
     *
     * @param location  the location used to resolve relative paths (may be null)
     * @param resources the ResourceBundle for localized strings (may be null)
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL location, final ResourceBundle resources) {
        this.loadAcceptedTop3();
        // Attacher un gestionnaire d'événements au clic de la souris sur l'icône de
        // retour
        this.retour.setOnMouseClicked(event -> this.afficherProduct());
    }


    /**
     * Retrieve the selected quantity.
     *
     * @return the currently selected quantity
     */
    public int getQuantiteSelectionnee() {
        return this.quantiteSelectionnee;
    }


    /**
     * Load the product identified by this.produitId and populate the UI with its detail and shopping-cart cards.
     *
     * Clears existing children from the detail FlowPane, adds a product detail card and a shopping-cart card when the
     * product is found, and logs an informational message if no product matches the ID.
     */
    private void initDetailsProduct() {
        // Récupérer le produit depuis le service en utilisant l'ID
        final ProductService produitService = new ProductService();
        final Optional<Product> produitOptional = Optional.ofNullable(produitService.getProductById(this.produitId));
        // Vérifier si le produit a été trouvé
        produitOptional.ifPresentOrElse(produit -> {
            // Effacer les anciennes cartes avant d'ajouter la nouvelle carte
            this.detailFlowPane.getChildren().clear();
            // Créer la carte pour le produit trouvé et l'ajouter à la FlowPane
            final HBox cardContainer = this.createProductCard(produit);
            final HBox shoppingcartContainer = this.createShoppingCartCard(produit);
            this.detailFlowPane.getChildren().add(cardContainer);
            this.shoppingcartFlowPane.getChildren().add(shoppingcartContainer);
        }
, () -> DetailsProductClientController.LOGGER.info("Product non trouvé avec l'ID : " + this.produitId));
    }


    /**
     * Create an HBox containing a product detail card with image, name, description,
     * price, a star rating control, and an "Add to Cart" button.
     *
     * The rating control reflects the product's average rating and lets the current
     * user submit or update their rating; the "Add to Cart" button adds one unit
     * of the product to the shopping cart and updates the visible cart/detail panes.
     *
     * @param produit the Product to display (used to populate image, text, price, and rating)
     * @return an HBox containing the assembled product detail card
     */
    private HBox createProductCard(final Product produit) {
        // Créer une carte pour le produit avec ses informations
        final HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-padding: 10px 0 0  50px;"); // Ajout de remplissage à gauche pour le décalage
        final AnchorPane card = new AnchorPane();
        // Image du Product
        final ImageView imageView = new ImageView();
        imageView.setLayoutX(10);
        imageView.setLayoutY(3);
        imageView.setFitWidth(370);
        imageView.setFitHeight(400);
        try {
            final String produitImage = produit.getImage();
            if (null != produitImage) {
                final Image image = new Image(produitImage);
                imageView.setImage(image);
            }
 else {
                // Utiliser une image par défaut si le Blob est null
                final Image defaultImage = new Image(this.getClass().getResourceAsStream("defaultImage.png"));
                imageView.setImage(defaultImage);
            }

        }
 catch (final Exception e) {
            DetailsProductClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        // Nom du Product
        final Label nameLabel = new Label(produit.getName());
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(400);
        nameLabel.setLayoutY(30);
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique
        // Prix du Product
        final Label priceLabel = new Label(" " + produit.getPrice() + " DT");
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        priceLabel.setLayoutX(410);
        priceLabel.setLayoutY(200);
        final Label descriptionLabel = new Label(produit.getDescription());
        descriptionLabel.setFont(Font.font("Arial", 14));
        descriptionLabel.setTextFill(Color.web("#392c2c")); // Définir la même couleur de texte que descriptionText
        descriptionLabel.setLayoutX(410);
        descriptionLabel.setLayoutY(95);
        descriptionLabel.setMaxWidth(250); // Ajuster la largeur maximale selon vos besoins
        descriptionLabel.setWrapText(true); // Activer le retour à la ligne automatique
        // Bouton Ajouter au ShoppingCart
        final Button addToCartButton = new Button("Add to Cart", new FontIcon("fa-cart-plus"));
        addToCartButton.setLayoutX(435);
        addToCartButton.setLayoutY(300);
        // addToCartButton.getStyleClass().add("sale"); // Style du bouton
        addToCartButton.setStyle("""
                -fx-background-color: #dd4f4d;
                    -fx-text-fill: #FFFFFF;
                    -fx-font-size: 12px;
                    -fx-font-weight: bold;
                    -fx-padding: 10px 10px;\
                """);
        addToCartButton.setOnAction(event -> {
            final long produitId = produit.getId();
            final int quantity = 1; // Vous pouvez ajuster la quantité en fonction de vos besoins
            this.ajouterAuShoppingCart(produitId, quantity);
            this.shoppingcartAnchorPane.setVisible(true);
            this.detailFlowPane.setVisible(true);
            this.detailFlowPane.setOpacity(0.2);
            this.top3anchorpane.setVisible(false);
        }
);
        final Review avis = new Review();
        final double rate = new ReviewService().getAverageRating(produit.getId());
        DetailsProductClientController.LOGGER.info(BigDecimal.valueOf(rate).setScale(1, RoundingMode.FLOOR).toString());
        // Champ de notation (Rating)
        final Rating rating = new Rating();
        rating.setLayoutX(410);
        rating.setLayoutY(390);
        rating.setMax(5);
        rating.setRating(avis.getRating()); // Vous pouvez ajuster en fonction de la valeur du produit
        final String format = "%.1f/5"
                .formatted(BigDecimal.valueOf(rate).setScale(1, RoundingMode.FLOOR).doubleValue());
        final Label etoilelabel = new Label(format);
        etoilelabel.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        etoilelabel.setStyle("-fx-text-fill: #333333;");
        etoilelabel.setLayoutX(410);
        etoilelabel.setLayoutY(230);
        final FontIcon iconeetoile = new FontIcon();
        iconeetoile.setIconLiteral("fab-star");
        iconeetoile.setFill(Color.YELLOW);
        iconeetoile.setIconSize(20);
        iconeetoile.setLayoutX(465);
        iconeetoile.setLayoutY(250);
        final Review avi = new ReviewService().ratingExists(produit.getId(), /* (Client) stage.getUserData() */ 4L);
        rating.setRating(null != avi ? avi.getRating() : 0);
        // Stage stage = (Stage) hyperlink.getScene().getWindow();
        rating.ratingProperty().addListener((observableValue, number, t1) -> {
            final ReviewService avisService = new ReviewService();
            final Review avi1 = avisService.ratingExists(produit.getId(), 4L /* (Client) stage.getUserData() */);
            if (null != avi) {
                avisService.delete(avi1);
            }

            avisService.create(new Review(/* (Client) stage.getUserData() */(Client) new UserService().getUserById(4L),
                    t1.intValue(), produit));
            final double rate1 = new ReviewService().getAverageRating(produit.getId());
            // Formater le texte avec une seule valeur après la virgule
            final String formattedRate = "%.1f/5"
                    .formatted(BigDecimal.valueOf(rate1).setScale(1, RoundingMode.FLOOR).doubleValue());
            DetailsProductClientController.LOGGER.info(formattedRate);
            etoilelabel.setText(formattedRate);
        }
);
        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel, rating, etoilelabel,
                addToCartButton, iconeetoile);
        cardContainer.getChildren().add(card);
        return cardContainer;
    }


    /**
         * Add the specified product and quantity to the shopping cart when stock is available.
         *
         * Creates and persists a ShoppingCart entry associated with the product and a user (ID 4),
         * then updates the shopping cart view. If available stock is insufficient, logs an informational message and does not modify the cart.
         *
         * @param produitId ID of the product to add
         * @param quantity  Quantity to add to the cart
         */
    private void ajouterAuShoppingCart(final long produitId, final int quantity) {
        final ProductService produitService = new ProductService();
        final ShoppingCartService shoppingcartService = new ShoppingCartService();
        final UserService usersService = new UserService();
        // Vérifier le stock disponible avant d'ajouter au shoppingcart
        if (produitService.checkAvailableStock(produitId, quantity)) {
            final Product produit = produitService.getProductById(produitId);
            final ShoppingCart shoppingcart = new ShoppingCart();
            shoppingcart.setProduct(produit);
            shoppingcart.setQuantity(quantity);
            shoppingcart.setUser(usersService.getUserById(4L));
            shoppingcartService.create(shoppingcart);
            this.affichershoppingcart(); // Utilisez le produit ajouté pour afficher dans le shoppingcart
        }
 else {
            // Afficher un message d'avertissement sur le stock insuffisant
            DetailsProductClientController.LOGGER.info("Stock insuffisant pour le produit avec l'ID : " + produitId);
        }

    }


    /**
     * Open the product listing view in a modal window and then close the current window.
     *
     * Loads the "/ui/produits/AfficherProductClient.fxml" FXML, creates a modal Stage owned by the current window,
     * shows it blocking (waits until it closes), and closes the original stage afterward.
     */
    @FXML
    /**
     * Performs afficherProduct operation.
     *
     * @return the result of the operation
     */
    public void afficherProduct() {
        // Obtenir la fenêtre précédente
        final Window previousWindow = this.retour.getScene().getWindow();
        // Charger le fichier FXML de la page "/ui//ui/AfficherProduct.fxml"
        final FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/ui/produits/AfficherProductClient.fxml"));
        try {
            final Parent rootNode = fxmlLoader.load();
            final Scene scene = new Scene(rootNode);
            // Créer une nouvelle fenêtre pour la page "/ui//ui/AfficherProduct.fxml"
            final Stage previousStage = new Stage();
            // Configurer la fenêtre précédente avec les propriétés nécessaires
            previousStage.setScene(scene);
            previousStage.setTitle(" Afficher Product");
            // Afficher la fenêtre précédente de manière bloquante
            previousStage.initModality(Modality.APPLICATION_MODAL);
            previousStage.initOwner(previousWindow);
            previousStage.showAndWait();
            // Fermer la fenêtre actuelle
            final Stage currentStage = (Stage) this.retour.getScene().getWindow();
            currentStage.close();
        }
 catch (final Exception e) {
            DetailsProductClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception selon vos
                                                                                        // besoins
        }

    }


    /**
     * Show the shopping cart pane and dim the product details pane.
     *
     * Makes the shopping cart and details FlowPanes visible and sets the details pane opacity to 0.2.
     */
    private void affichershoppingcart() {
        // Initialiser la visibilité des AnchorPane
        this.shoppingcartFlowPane.setVisible(true);
        this.detailFlowPane.setVisible(true);
        this.detailFlowPane.setOpacity(0.2);
        // top3anchorpane.setVisible(false);
    }


    /**
     * Builds a shopping-cart card UI for the given product, including image, name,
     * price, quantity, total and action controls (order, continue shopping, close).
     *
     * @param produit the product whose details (image, name, price) populate the card
     * @return an HBox containing the shopping-cart card nodes and their event handlers
     */
    private HBox createShoppingCartCard(final Product produit) {
        // Créer une carte pour le produit avec ses informations
        final HBox shoppingcartContainer = new HBox();
        shoppingcartContainer.setStyle("-fx-padding: 0px 0 0 20px;"); // Ajout de remplissage à gauche pour le décalage
        final AnchorPane card = new AnchorPane();
        // Ajouter un label "Card"
        final Label cartLabel = new Label("My Cart");
        cartLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 25));
        cartLabel.setTextFill(Color.web("#B40C0C")); // Définir la couleur du texte
        cartLabel.setPrefWidth(230);
        cartLabel.setLayoutY(30);
        // Centrer le texte dans le label
        cartLabel.setAlignment(Pos.CENTER);
        // Image du Product
        final ImageView imageView = new ImageView();
        imageView.setLayoutX(50);
        imageView.setLayoutY(75);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        try {
            final String produitImage = produit.getImage();
            if (null != produitImage) {
                final Image image = new Image(produitImage);
                imageView.setImage(image);
            }
 else {
                // Utiliser une image par défaut si le Blob est null
                final Image defaultImage = new Image(this.getClass().getResourceAsStream("defaultImage.png"));
                imageView.setImage(defaultImage);
            }

        }
 catch (final Exception e) {
            DetailsProductClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        // Nom du Product
        final Label nameLabel = new Label(produit.getName());
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(10);
        nameLabel.setLayoutY(235);
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique
        // Prix du Product
        final Label priceLabel = new Label(" " + produit.getPrice() + " DT");
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        priceLabel.setLayoutX(10);
        priceLabel.setLayoutY(270);
        // Champ de texte pour la quantité
        final Label quantiteLabel = new Label("Quantité : 1 ");
        quantiteLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        quantiteLabel.setLayoutX(30);
        quantiteLabel.setLayoutY(290);
        final Label sommeTotaleLabel = new Label("Somme totale : " + produit.getPrice() + " DT");
        sommeTotaleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        sommeTotaleLabel.setLayoutX(30);
        sommeTotaleLabel.setLayoutY(320);
        // Bouton Ajouter au ShoppingCart
        final Button orderbutton = new Button("Order", new FontIcon("fa-cart-plus"));
        orderbutton.setLayoutX(50);
        orderbutton.setLayoutY(350);
        orderbutton.setPrefWidth(120);
        orderbutton.setPrefHeight(35);
        orderbutton.setStyle("""
                -fx-background-color: #624970;
                 -fx-text-fill: #FCE19A;\
                   -fx-font-size: 12px;
                     -fx-font-weight: bold;
                 -fx-background-color: #6f7b94\
                """); // Style du bouton
        orderbutton.setOnAction(event -> {
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    this.getClass().getResource("/ui/produits/DesignProductAdmin.fxml"));
            try {
                Parent root = null;
                root = fxmlLoader.load();
                // Parent rootNode = fxmlLoader.load();
                // Scene scene = new Scene(rootNode);
                final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                final Stage newStage = new Stage();
                newStage.setScene(new Scene(root, 1280, 700));
                newStage.setTitle("my cart");
                newStage.show();
                // Fermer la fenêtre actuelle
                currentStage.close();
            }
 catch (final IOException e) {
                DetailsProductClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Affiche l'erreur
                                                                                            // dans la console
                                                                                            // (vous pourriez
                // le
                // remplacer par une boîte de dialogue)
                DetailsProductClientController.LOGGER
                        .info("Erreur lors du chargement du fichier FXML : " + e.getMessage());
            }

        }
);
        // Bouton Ajouter au ShoppingCart
        final Button achatbutton = new Button("continue shopping");
        achatbutton.setLayoutX(50);
        achatbutton.setLayoutY(400);
        achatbutton.setPrefHeight(30);
        achatbutton.setStyle("""
                 -fx-background-color: #466288;
                    -fx-text-fill: #FCE19A;
                    -fx-font-size: 12px;
                    -fx-font-weight: bold;
                    -fx-padding: 10px 10px;\
                """);
        achatbutton.setOnAction(event -> {
            this.fermerShoppingCartCard(shoppingcartContainer);
        }
);
        // Icône de fermeture (close)
        final FontIcon closeIcon = new FontIcon();
        closeIcon.setIconLiteral("fa-times-circle");
        closeIcon.setIconSize(20);
        closeIcon.setLayoutX(220);
        closeIcon.setLayoutY(20);
        closeIcon.getStyleClass().add("close"); // Style de l'icône
        // Attachez un gestionnaire d'événements pour fermer la carte du shoppingcart
        closeIcon.setOnMouseClicked(event -> this.fermerShoppingCartCard(shoppingcartContainer));
        card.getChildren().addAll(cartLabel, imageView, nameLabel, priceLabel, quantiteLabel, sommeTotaleLabel,
                achatbutton, orderbutton, closeIcon);
        shoppingcartContainer.getChildren().add(card);
        return shoppingcartContainer;
    }


    /**
     * Compute the total price for a product given its ID and quantity.
     *
     * @param idProduct ID of the product to price
     * @param quantity  number of units to purchase
     * @return total price as unit price multiplied by quantity
     */
    private double prixProduct(final Long idProduct, final int quantity) {
        final ProductService produitService = new ProductService();
        final double prixUnitaire = produitService.getProductPrice(idProduct);
        return quantity * prixUnitaire;
    }


    /**
     * Restore the main product/detail view and hide the shopping cart panel.
     *
     * @param shoppingcartContainer the HBox that contains the shopping cart panel being closed
     */
    private void fermerShoppingCartCard(final HBox shoppingcartContainer) {
        // Rendre la carte du shoppingcart invisible
        // Rendre shoppingcartFlowPane invisible
        this.shoppingcartFlowPane.setVisible(false);
        // Rendre detailFlowPane visible et ajuster l'opacité à 1
        this.detailFlowPane.setVisible(true);
        this.detailFlowPane.setOpacity(1);
        this.anchordetail.setVisible(true);
        this.anchordetail.setOpacity(1);
        this.top3anchorpane.setVisible(true);
        this.top3anchorpane.setOpacity(1);
    }


    /**
     * Open the shopping cart view in a new window and close the current window.
     *
     * Loads the ShoppingCartProduct.fxml UI, displays it in a new Stage titled
     * "ShoppingCart des produits", and closes the stage that contains the event's source.
     *
     * @param event the MouseEvent that triggered this navigation; its source is used to locate and close the current window
     */
    @FXML
    void shoppingcart(final MouseEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(
                    this.getClass().getResource("/ui/produits/ShoppingCartProduct.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("ShoppingCart des produits");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        }
 catch (final IOException e) {
            DetailsProductClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * Opens the product comments view in a new window and closes the current window.
     *
     * @param event the MouseEvent that triggered this navigation
     */
    @FXML
    void commentaire(final MouseEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(
                    this.getClass().getResource("/ui/produits/CommentaireProduct.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Commentaire des produits");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        }
 catch (final IOException e) {
            DetailsProductClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * Opens the product comment view (CommentaireProduct.fxml) in a new window titled "cinema" and closes the current window.
     */
    @FXML
    void cinemaclient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(
                    this.getClass().getResource("/ui/produits/CommentaireProduct.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("cinema ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        }
 catch (final IOException e) {
            DetailsProductClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
         * Open the "AffichageEvenementClient" view in a new window and close the current window.
         *
         * @param event the ActionEvent whose source node is used to obtain and close the current Stage
         */
    @FXML
    void eventClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(
                    this.getClass().getResource("/ui//ui/AffichageEvenementClient.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Event ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        }
 catch (final IOException e) {
            DetailsProductClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * Open the product listing view in a new window and close the current window.
     *
     * Loads "AfficherProductClient.fxml", attaches it to a new Stage titled "products",
     * shows that stage, and closes the stage obtained from the provided event's source.
     *
     * @param event the ActionEvent whose source node provides the current Window to be closed
     */
    @FXML
    void produitClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(
                    this.getClass().getResource("/ui/produits/AfficherProductClient.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("products ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        }
 catch (final IOException e) {
            DetailsProductClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * Handle the client profile action triggered by the UI.
     *
     * @param event the ActionEvent that triggered this handler
     */
    @FXML
    void profilclient(final ActionEvent event) {
    }


    /**
     * Opens the film user view (filmuser.fxml) in a new window and closes the current window.
     *
     * @param event the action event that triggered the navigation
     */
    @FXML
    void MovieClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/films/filmuser.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("movie ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        }
 catch (final IOException e) {
            DetailsProductClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
         * Opens the Series view in a new window and closes the current stage.
         *
         * @param event the ActionEvent that triggered navigation to the Series view
         */
    @FXML
    void SerieClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui//ui/Series-view.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("chat ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        }
 catch (final IOException e) {
            DetailsProductClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * Load and display the top three best-selling products with accepted status into the controller's topthreeVbox.
     *
     * <p>The method queries ProductService for products ordered by quantity and status, creates a UI card for each
     * of the first three products via createtopthree(...), and appends those cards to topthreeVbox. If fewer than
     * three products are available, the method logs an informational message and returns without modifying the UI.
     * Any exceptions encountered during loading are logged at SEVERE level.</p>
     */
    public void loadAcceptedTop3() {
        final ProductService produitService = new ProductService();
        try {
            final List<Product> produits = produitService.getProductsOrderByQuantityAndStatus();
            if (3 > produits.size()) {
                DetailsProductClientController.LOGGER.info("Pas assez de produits disponibles");
                return;
            }

            final List<Product> top3Products = produits.subList(0, 3);
            int j = 0;
            for (final Product produit : top3Products) {
                DetailsProductClientController.LOGGER.info(String.valueOf(produit.getId()));
                final VBox cardContainer = this.createtopthree(produit);
                DetailsProductClientController.LOGGER.log(Level.INFO, "------------------{0}
{1}
",
                        new Object[] { j, cardContainer.getChildren() }
);
                this.topthreeVbox.getChildren().add(cardContainer);
                j++;
            }

        }
 catch (final Exception e) {
            DetailsProductClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            DetailsProductClientController.LOGGER.info("Une erreur est survenue lors du chargement des produits");
        }

    }


    /**
     * Creates a compact top-three product card showing the product image, name, and price.
     *
     * The card displays a product image (falls back to a default image if the product image is unavailable)
     * and shows the product name and price. Clicking the image or the name opens the product details view
     * for this product in a new window.
     *
     * @param produit the product to represent; its id, name, price, and image URL are used to populate the card
     * @return a VBox containing the assembled product card (image, name label, and price label)
     */
    public VBox createtopthree(final Product produit) {
        final VBox cardContainer = new VBox(5);
        cardContainer.setStyle("-fx-padding: 20px 0 0 30px;"); // Add left padding
        final AnchorPane card = new AnchorPane();
        card.setLayoutX(0);
        card.setLayoutY(0);
        cardContainer.setPrefWidth(255);
        card.getStyleClass().add("meilleurproduit");
        cardContainer.setSpacing(10);
        // Image of the product
        final ImageView imageView = new ImageView();
        imageView.setLayoutX(5);
        imageView.setLayoutY(21);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        try {
            final String produitImage = produit.getImage();
            if (null != produitImage) {
                final Image image = new Image(produitImage);
                imageView.setImage(image);
            }
 else {
                // Use a default image if Blob is null
                final Image defaultImage = new Image(this.getClass().getResourceAsStream("defaultImage.png"));
                imageView.setImage(defaultImage);
            }

        }
 catch (final Exception e) {
            DetailsProductClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            // Handle any exceptions during image loading
            DetailsProductClientController.LOGGER.info("Une erreur est survenue lors du chargement de l'image");
        }

        imageView.setOnMouseClicked(event -> {
            try {
                final FXMLLoader loader = new FXMLLoader(
                        this.getClass().getResource("/ui/produits/DetailsProductClient.fxml"));
                Parent root = null;
                DetailsProductClientController.LOGGER
                        .info("Clique sur le nom du produit. ID du produit : " + produit.getId());
                root = loader.load();
                // Récupérez le contrôleur et passez l'id du produit lors de l'initialisation
                final DetailsProductClientController controller = loader.getController();
                controller.setProductId(produit.getId());
                // Afficher la nouvelle interface
                final Stage stage = new Stage();
                final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 1280, 700));
                stage.setTitle("Détails du Product");
                stage.show();
                currentStage.close();
            }
 catch (final IOException e) {
                throw new RuntimeException(e);
            }

        }
);
        // Product name
        final Label nameLabel = new Label(produit.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(60); // Adjust X position
        nameLabel.setLayoutY(25);
        nameLabel.setMaxWidth(200); // Adjust max width
        nameLabel.setWrapText(true);
        nameLabel.setOnMouseClicked(event -> {
            try {
                final FXMLLoader loader = new FXMLLoader(
                        this.getClass().getResource("/ui/produits/DetailsProductClient.fxml"));
                Parent root = null;
                DetailsProductClientController.LOGGER
                        .info("Clique sur le nom du produit. ID du produit : " + produit.getId());
                root = loader.load();
                // Récupérez le contrôleur et passez l'id du produit lors de l'initialisation
                final DetailsProductClientController controller = loader.getController();
                controller.setProductId(produit.getId());
                // Afficher la nouvelle interface
                final Stage stage = new Stage();
                final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 1280, 700));
                stage.setTitle("Détails du Product");
                stage.show();
                currentStage.close();
            }
 catch (final IOException e) {
                throw new RuntimeException(e);
            }

        }
);
        final Label priceLabel = new Label(" " + produit.getPrice() + " DT");
        priceLabel.setLayoutX(60);
        priceLabel.setLayoutY(55);
        priceLabel.setFont(Font.font("Arial", 14));
        priceLabel.setStyle("-fx-text-fill: black;");
        card.getChildren().addAll(nameLabel, priceLabel, imageView);
        cardContainer.getChildren().addAll(card); // Add vertical space
        return cardContainer;
    }

}
