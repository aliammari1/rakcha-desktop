package com.esprit.controllers.products;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;

import com.esprit.models.products.Comment;
import com.esprit.models.products.Product;
import com.esprit.models.products.ShoppingCart;
import com.esprit.models.users.Client;
import com.esprit.services.products.CommentService;
import com.esprit.services.products.ProductService;
import com.esprit.services.products.ShoppingCartService;
import com.esprit.services.users.UserService;
import com.esprit.utils.Chat;
import com.esprit.utils.PageRequest;

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

/**
 * Displays a card containing the name, price, and image of a product. The class
 * creates an AnchorPane and adds a Label, a PriceLabel, and an ImageView to it.
 * The ImageView is clickable and will show the details of the product when
 * clicked.
 */
public class AfficherProductClientControllers implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(AfficherProductClientControllers.class.getName());
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> statusCheckBoxes = new ArrayList<>();
    private final List<Product> shoppingcartList = new ArrayList<>();
    private final Chat chat = new Chat();

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

    /**
     * Button for forwarding messages in chat.
     */
    @FXML
    public Button forward_message;
    @FXML
    private AnchorPane filterAnchor;
    @FXML
    private FlowPane produitFlowPane;
    @FXML
    private FlowPane topproduitFlowPane;
    @FXML
    private FontIcon idfilter;
    private List<Product> l1 = new ArrayList<>();
    private long produitId;
    @FXML
    private VBox top3anchorpane;
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
     * Filter a list of products by checking whether each product's name contains the given search term.
     *
     * Matches are performed using String.contains on the product name; product entries with a null name are ignored.
     *
     * @param liste the list of products to search
     * @param recherche the substring to match against each product's name (case-sensitive)
     * @return a list of products whose name contains {@code recherche} (order preserved)
     */
    @FXML
    /**
     * Performs rechercher operation.
     *
     * Searches for products in a list based on the search term.
     *
     * @param liste     the list of products to search through
     * @param recherche the search term to match against product names
     * @return a list of products that match the search criteria
     */
    public static List<Product> rechercher(final List<Product> liste, final String recherche) {
        final List<Product> resultats = new ArrayList<>();
        for (final Product element : liste) {
            if (null != element.getName() && element.getName().contains(recherche)) {
                resultats.add(element);
            }

        }

        return resultats;
    }


    /**
     * Initialize the controller UI: load accepted products, display comments and top-three products,
     * and wire the search bar and sort combo box to update the product view.
     *
     * Sets up listeners that filter products as the search text changes and that reorder the displayed
     * products when a sort option is selected.
     *
     * @param location  the location used to resolve relative paths for the root object, may be null
     * @param resources the ResourceBundle for localized resources, may be null
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL location, final ResourceBundle resources) {
        this.loadAcceptedProducts();
        this.displayAllComments(this.produitId);
        this.loadAcceptedTop3();
        final ProductService produitService = new ProductService();
        PageRequest pageRequest = new PageRequest(0, 10);
        this.l1 = produitService.read(pageRequest).getContent();
        this.SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            final List<Product> produitsRecherches = AfficherProductClientControllers.rechercher(this.l1, newValue);
            // Effacer la FlowPane actuelle pour afficher les nouveaux résultats
            this.produitFlowPane.getChildren().clear();
            this.createProductCards(produitsRecherches);
            this.filterCategorieProducts(newValue.trim());
        }
);
        this.tricomboBox.getItems().addAll("nom", "prix");
        this.tricomboBox.setValue("");
        this.tricomboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            this.produitFlowPane.getChildren().clear();
            final List<Product> filmList = new ProductService().sort(t1);
            for (final Product film : filmList) {
                this.produitFlowPane.getChildren().add(this.createProductCard(film));
            }

        }
);
    }


    /**
     * Populate the produitFlowPane with product cards fetched from the product service.
     *
     * Fetches the first page of products, creates a product card node for each product,
     * attaches the product as node userData, and adds the nodes to produitFlowPane.
     */
    private void loadAcceptedProducts() {
        final ProductService produitService = new ProductService();
        PageRequest pageRequest = new PageRequest(0, 10);
        final List<Product> produits = produitService.read(pageRequest).getContent();
        // Charger tous les produits dans produitFlowPane
        for (final Product produit : produits) {
            final VBox cardContainer = this.createProductCard(produit);
            final AnchorPane produitNode = new AnchorPane();
            produitNode.setUserData(produit);
            produitNode.getChildren().add(cardContainer);
            this.produitFlowPane.getChildren().add(produitNode);
            this.produitFlowPane.setPadding(new Insets(10, 10, 10, 10));
        }

    }


    /**
     * Create a UI card displaying a product's image, name, description, price, and actions for adding to cart or viewing comments.
     *
     * @param Product product whose details are shown and whose id is used by action handlers (open details view, add to cart, load comments)
     * @return a VBox containing the constructed product card node ready to be inserted into the UI
     */
    private VBox createProductCard(final Product Product) {
        // Créer une carte pour le produit avec ses informations
        final VBox cardContainer = new VBox(5);
        cardContainer.setStyle("-fx-padding: 20px 0 0  30px;"); // Ajout de remplissage à gauche pour le décalage
        final AnchorPane card = new AnchorPane();
        // Image du Product
        final ImageView imageView = new ImageView();
        imageView.setLayoutX(45);
        imageView.setLayoutY(30);
        imageView.setFitWidth(220);
        imageView.setFitHeight(150);
        try {
            final String produitImage = Product.getImage();
            if (!produitImage.isEmpty()) {
                final Image image = new Image(produitImage);
                imageView.setImage(image);
            }
 else {
                // Utiliser une image par défaut si le Blob est null
                final Image defaultImage = new Image(this.getClass().getResourceAsStream("defaultImage.png"));
                imageView.setImage(defaultImage);
            }

        } catch (final Exception e) {
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        imageView.setOnMouseClicked(event -> {
            try {
                final FXMLLoader loader = new FXMLLoader(
                        this.getClass().getResource("/ui/produits/DetailsProductClient.fxml"));
                Parent root = null;
                AfficherProductClientControllers.LOGGER
                        .info("Clique sur le nom du produit. ID du produit : " + Product.getId());
                root = loader.load();
                // Récupérez le contrôleur et passez l'id du produit lors de l'initialisation
                final DetailsProductClientController controller = loader.getController();
                controller.setProductId(Product.getId());
                // Afficher la nouvelle interface
                final Stage stage = new Stage();
                final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 1280, 700));
                stage.setTitle("Détails du Product");
                stage.show();
                currentStage.close();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

        }
);
        // Prix du Product
        final Label priceLabel = new Label(" " + Product.getPrice() + " DT");
        priceLabel.setLayoutX(20);
        priceLabel.setLayoutY(300);
        priceLabel.setFont(Font.font("Arial", 15));
        priceLabel.setStyle("-fx-text-fill: black;");
        // Description du produit
        final Label descriptionLabel = new Label(Product.getDescription());
        descriptionLabel.setFont(Font.font("Arial", 14));
        descriptionLabel.setStyle("-fx-text-fill: black;"); // Définir la même couleur de texte que descriptionText
        descriptionLabel.setLayoutX(30);
        descriptionLabel.setLayoutY(220);
        descriptionLabel.setMaxWidth(230); // Ajuster la largeur maximale selon vos besoins
        descriptionLabel.setWrapText(true); // Activer le retour à la ligne automatique
        // Nom du Product
        final Label nameLabel = new Label(Product.getName());
        nameLabel.setLayoutX(23);
        nameLabel.setLayoutY(190);
        nameLabel.setFont(Font.font("Verdana", 15));
        nameLabel.setStyle("-fx-text-fill: black;");
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique
        nameLabel.setOnMouseClicked(event -> {
            try {
                final FXMLLoader loader = new FXMLLoader(
                        this.getClass().getResource("/ui/produits/DetailsProductClient.fxml"));
                Parent root = null;
                AfficherProductClientControllers.LOGGER
                        .info("Clique sur le nom du produit. ID du produit : " + Product.getId());
                root = loader.load();
                // Récupérez le contrôleur et passez l'id du produit lors de l'initialisation
                final DetailsProductClientController controller = loader.getController();
                controller.setProductId(Product.getId());
                // Afficher la nouvelle interface
                final Stage stage = new Stage();
                final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 1280, 700));
                stage.setTitle("Détails du Product");
                stage.show();
                currentStage.close();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

        }
);
        // Bouton Ajouter au ShoppingCart
        final Button addToCartButton = new Button("Add to Cart", new FontIcon("fa-cart-plus"));
        /*
         * addToCartButton.setStyle("-fx-background-color: #dd4f4d;\n" +
         * "    -fx-text-fill: #FFFFFF;\n" + "    -fx-font-size: 12px;\n" +
         * "    -fx-font-weight: bold;\n" + "    -fx-padding: 10px 10px;");
         */
        addToCartButton.setLayoutX(65);
        addToCartButton.setLayoutY(330);
        addToCartButton.getStyleClass().add("sale");
        addToCartButton.setOnAction(event -> {
            final Stage stage = (Stage) this.shoppingcartFlowPane.getScene().getWindow();
            AfficherProductClientControllers.LOGGER.info(stage.getUserData().toString());
            final long produitId = Product.getId();
            final int quantity = 1; // Vous pouvez ajuster la quantité en fonction de vos besoins
            this.ajouterAuShoppingCart(produitId, quantity);
            this.top3anchorpane.setVisible(false);
            this.shoppingcartFlowPane.setVisible(true);
        }
);
        final FontIcon chatIcon = new FontIcon();
        chatIcon.setIconLiteral("fa-commenting");
        chatIcon.setIconSize(20);
        chatIcon.setLayoutX(240);
        chatIcon.setLayoutY(365);
        chatIcon.setOnMouseClicked(event -> {
            this.produitFlowPane.setOpacity(0.5);
            this.AnchorComments.setVisible(true);
            final long produitId = Product.getId();
            this.displayAllComments(produitId);
            this.idcomment.setStyle("-fx-background-color: #fff;");
        }
);
        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel, addToCartButton, chatIcon);
        card.setStyle("""
                -fx-background-color:#F6F2F2;
                 -fx-text-fill: #FFFFFF;
                    -fx-font-size: 12px;
                    -fx-font-weight: bold;
                    -fx-padding: 10px;
                    -fx-border-color: linear-gradient(to top left, #624970, #ae2d3c);/* Couleur de la bordure */
                    -fx-border-width: 2px; /* Largeur de la bordure */
                    -fx-border-radius: 5px; /* Rayon de la bordure pour arrondir les coins */\
                """);
        card.getStyleClass().add("bg-white");
        cardContainer.getChildren().add(card);
        return cardContainer;
    }


    /**
     * Shows the shopping cart with the newly added product and updates pane visibility.
     *
     * Clears the current shopping cart view, adds a shopping-cart card for the provided product,
     * makes the shopping cart visible, hides the top-3 anchor pane, and reduces the product
     * list pane opacity.
     *
     * @param produitAjoute the product to display in the shopping cart view
     */
    private void afficherShoppingCart(final Product produitAjoute) {
        // Effacez la FlowPane du shoppingcart actuelle pour afficher les nouveaux
        // résultats
        this.shoppingcartFlowPane.getChildren().clear();
        // Ajoutez le produit ajouté au shoppingcart à la FlowPane du shoppingcart
        final HBox shoppingcartCard = this.createShoppingCartCard(produitAjoute);
        this.shoppingcartFlowPane.getChildren().add(shoppingcartCard);
        // Affichez la FlowPane du shoppingcart et masquez les autres FlowPanes
        this.shoppingcartFlowPane.setVisible(true);
        this.produitFlowPane.setVisible(true);
        // topproduitFlowPane.setVisible(false);
        this.top3anchorpane.setVisible(false);
        this.produitFlowPane.setOpacity(0.2);
    }


    /**
     * Add a product to the current user's shopping cart if sufficient stock is available.
     *
     * Verifies available stock for the given product and quantity, creates and persists
     * a ShoppingCart entry for the current user, and updates the UI to show the added product.
     *
     * @param produitId ID of the product to add to the cart.
     * @param quantity  Number of units to add.
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
            final Client client = (Client) this.shoppingcartFlowPane.getScene().getWindow().getUserData();
            shoppingcart.setUser(client);
            shoppingcartService.create(shoppingcart);
            this.afficherShoppingCart(produit); // Utilisez le produit ajouté pour afficher dans le shoppingcart
        }
 else {
            // Afficher un message d'avertissement sur le stock insuffisant
            AfficherProductClientControllers.LOGGER.info("Stock insuffisant pour le produit avec l'ID : " + produitId);
        }

    }


    /**
     * Create and add UI cards for each product in the given list to the produitFlowPane.
     *
     * @param produits list of products whose cards will be created and appended to produitFlowPane
     */
    private void createProductCards(final List<Product> produits) {
        for (final Product produit : produits) {
            final VBox cardContainer = this.createProductCard(produit);
            this.produitFlowPane.getChildren().add(cardContainer);
        }

    }


    /**
     * Creates a shopping-cart card UI for a single product showing image, name,
     * quantity, total price, and actions to place the order, continue shopping, or
     * close the card.
     *
     * @param produit the product whose details (image, name, price) are displayed in the card
     * @return an HBox containing the composed shopping-cart card UI
     */
    private HBox createShoppingCartCard(final Product produit) {
        // Créer une carte pour le produit avec ses informations
        final HBox shoppingcartContainer = new HBox();
        shoppingcartContainer.setStyle("-fx-padding: 10px 0 0 20px;"); // Ajout de remplissage à gauche pour le décalage
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
        imageView.setLayoutY(70);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        try {
            final String produitImage = produit.getImage();
            if (!produitImage.isEmpty()) {
                final Image image = new Image(produitImage);
                imageView.setImage(image);
            }
 else {
                // Utiliser une image par défaut si le Blob est null
                final Image defaultImage = new Image(this.getClass().getResourceAsStream("defaultImage.png"));
                imageView.setImage(defaultImage);
            }

        } catch (final Exception e) {
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        // Nom du Product
        final Label nameLabel = new Label(produit.getName());
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 23));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(10);
        nameLabel.setLayoutY(220);
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique
        // Champ de texte pour la quantité
        final Label quantiteLabel = new Label("Quantité : 1");
        quantiteLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        quantiteLabel.setLayoutX(30);
        quantiteLabel.setLayoutY(250);
        // Récupérer la somme totale du prix du produit dans le shoppingcart
        // double sommeTotaleProduct =
        // orderItemService.calculerSommeTotaleProduct(produit.getId());
        // Champ de texte pour la somme totale
        final Label sommeTotaleLabel = new Label("Somme totale : " + produit.getPrice() + " DT");
        sommeTotaleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        sommeTotaleLabel.setLayoutX(30);
        sommeTotaleLabel.setLayoutY(270);
        // Bouton Ajouter au ShoppingCart
        final Button orderbutton = new Button("Order", new FontIcon("fa-cart-plus"));
        orderbutton.setLayoutX(50);
        orderbutton.setLayoutY(300);
        orderbutton.setPrefWidth(120);
        orderbutton.setPrefHeight(50);
        orderbutton.setStyle("""
                -fx-background-color: #624970;
                 -fx-text-fill: #FCE19A;\
                   -fx-font-size: 12px;
                     -fx-font-weight: bold;
                 -fx-background-color: #6f7b94\
                """);
        orderbutton.setOnAction(event -> {
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    this.getClass().getResource("/ui/produits/ShoppingCartProduct.fxml"));
            try {
                Parent root = null;
                root = fxmlLoader.load();
                // Parent rootNode = fxmlLoader.load();
                // Scene scene = new Scene(rootNode);
                final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                final Stage newStage = new Stage();
                currentStage.setScene(new Scene(root, 1280, 700));
                currentStage.setTitle("my cart");
                currentStage.show();
                // Fermer la fenêtre actuelle
            } catch (final IOException e) {
                AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Affiche
                                                                                              // l'erreur dans
                                                                                              // la console
                                                                                              // (vous pourriez
                // le
                // remplacer par une boîte de dialogue)
                AfficherProductClientControllers.LOGGER
                        .info("Erreur lors du chargement du fichier FXML : " + e.getMessage());
            }

        }
);
        // Bouton Ajouter au ShoppingCart
        final Button achatbutton = new Button("continue shopping");
        achatbutton.setLayoutX(50);
        achatbutton.setLayoutY(370);
        achatbutton.setPrefHeight(50);
        achatbutton.setStyle("""
                 -fx-background-color: #466288;
                    -fx-text-fill: #FCE19A;
                    -fx-font-size: 12px;
                    -fx-font-weight: bold;
                    -fx-padding: 10px 10px;\
                """); // Style du bouton
        achatbutton.setOnAction(event -> {
            this.fermerShoppingCartCard(shoppingcartContainer);
        }
);
        // Icône de fermeture (close)
        final FontIcon closeIcon = new FontIcon();
        closeIcon.setIconLiteral("fa-times-circle");
        closeIcon.setIconSize(20);
        closeIcon.setLayoutX(230);
        closeIcon.setLayoutY(10);
        closeIcon.getStyleClass().add("close"); // Style de l'icône
        // Attachez un gestionnaire d'événements pour fermer la carte du shoppingcart
        closeIcon.setOnMouseClicked(event -> {
            this.fermerShoppingCartCard(shoppingcartContainer);
        }
);
        card.getChildren().addAll(cartLabel, closeIcon, imageView, nameLabel, quantiteLabel, sommeTotaleLabel,
                achatbutton, orderbutton);
        shoppingcartContainer.getChildren().add(card);
        return shoppingcartContainer;
    }


    /**
     * Hides the shopping cart pane and restores the product and top-3 panes to full visibility and full opacity.
     */
    private void fermerShoppingCartCard(final HBox shoppingcartContainer) {
        this.shoppingcartFlowPane.setVisible(false);
        // Rendre detailFlowPane visible et ajuster l'opacité à 1
        this.produitFlowPane.setVisible(true);
        this.produitFlowPane.setOpacity(1);
        this.top3anchorpane.setVisible(true);
        this.top3anchorpane.setOpacity(1);
    }


    /**
     * Opens the ShoppingCartProduct UI in a new window and closes the window that triggered the event.
     *
     * @param event the MouseEvent whose source stage will be closed after the shopping cart window is shown
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
        } catch (final IOException e) {
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                          // d'entrée/sortie
        }

    }


    /**
     * Filters the products shown in the product flow pane to those whose first category name
     * contains the given search text (case-insensitive); if the search text is empty, reloads all accepted products.
     *
     * @param searchText text to match against each product's first category name (case-insensitive)
     */
    private void filterCategorieProducts(final String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des produits pour ne garder que ceux dont le nom contient le
            // texte saisi
            final ObservableList<Node> filteredList = FXCollections.observableArrayList();
            for (final Node node : this.produitFlowPane.getChildren()) {
                final Product produit = (Product) node.getUserData(); // Récupérer le Product associé au Node
                if (produit.getCategories().get(0).getCategoryName().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(node);
                }

            }

            // Mettre à jour le FlowPane avec la liste filtrée
            this.produitFlowPane.getChildren().setAll(filteredList);
        }
 else {
            // Si le champ de recherche est vide, afficher tous les produits
            this.loadAcceptedProducts();
        }

    }


    /**
     * Retrieve the first page of products from ProductService (used to obtain category data).
     *
     * @return a list of Product objects from page 0 with page size 10
     */
    private List<Product> getAllCategories() {
        final ProductService categoryservice = new ProductService();
        PageRequest pageRequest = new PageRequest(0, 10);
        return categoryservice.read(pageRequest).getContent();
    }


    /**
     * Shows the category filter panel and populates it with a checkbox for each unique product category.
     *
     * Sets the product flow pane opacity to 0.5, makes the filter anchor visible, clears existing
     * checkbox lists, creates a labeled VBox of CheckBox nodes for each category, stores those
     * checkboxes in `addressCheckBoxes`, and adds the VBox to the filter anchor.
     *
     * @param event the MouseEvent that triggered displaying the filter panel
     */
    @FXML
    void filtrer(final MouseEvent event) {
        this.produitFlowPane.setOpacity(0.5);
        this.filterAnchor.setVisible(true);
        // Nettoyer les listes des cases à cocher
        this.addressCheckBoxes.clear();
        this.statusCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        final List<String> categorie = this.getProductCategory();
        // Créer des VBox pour les adresses
        final VBox addressCheckBoxesVBox = new VBox(5);
        final Label addressLabel = new Label("Category");
        addressLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        addressCheckBoxesVBox.getChildren().add(addressLabel);
        for (final String address : categorie) {
            final CheckBox checkBox = new CheckBox(address);
            addressCheckBoxesVBox.getChildren().add(checkBox);
            this.addressCheckBoxes.add(checkBox);
        }

        addressCheckBoxesVBox.setLayoutX(25);
        addressCheckBoxesVBox.setLayoutY(50);
        // Ajouter les VBox dans le filterAnchor
        this.filterAnchor.getChildren().addAll(addressCheckBoxesVBox);
        this.filterAnchor.setVisible(true);
    }


    /**
     * Get unique product category names present in the stored products.
     *
     * Duplicates are removed; the iteration order is unspecified.
     *
     * @return a List of unique category name strings
     */
    public List<String> getProductCategory() {
        // Récupérer tous les cinémas depuis la base de données
        final List<Product> categories = this.getAllCategories();
        return categories.stream().flatMap(produit -> produit.getCategories().stream()).map(c -> c.getCategoryName())
                .distinct().collect(Collectors.toList());
    }


    /**
     * Filter displayed products to those whose first category matches any selected category and update the product view.
     *
     * Updates produitFlowPane visibility and opacity, hides the filterAnchor, and replaces the products shown in the flow pane with the filtered list.
     */
    @FXML
    /**
     * Performs filtercinema operation.
     *
     * @return the result of the operation
     */
    public void filtercinema(final ActionEvent event) {
        this.produitFlowPane.setOpacity(1);
        this.filterAnchor.setVisible(false);
        this.produitFlowPane.setVisible(true);
        // Récupérer les catégories sélectionnées
        final List<String> selectedCategories = this.getSelectedCategories();
        // Filtrer les produits en fonction des catégories sélectionnées
        final List<Product> filteredProducts = this.getAllCategories().stream()
                .filter(produit -> selectedCategories.contains(produit.getCategories().get(0).getCategoryName()))
                .collect(Collectors.toList());
        // Mettre à jour la liste des produits affichés
        this.updateProductFlowPane(filteredProducts);
    }


    /**
     * Replace the produitFlowPane contents with product cards built from the provided list.
     *
     * @param filteredProducts list of Product objects to display as individual cards in the produitFlowPane
     */
    private void updateProductFlowPane(final List<Product> filteredProducts) {
        this.produitFlowPane.getChildren().clear(); // Effacez les éléments existants
        for (final Product produit : filteredProducts) {
            final VBox cardContainer = this.createProductCard(produit);
            this.produitFlowPane.getChildren().add(cardContainer);
        }

    }


    /**
     * Get the texts of checkboxes currently selected in the category filter.
     *
     * @return a List<String> containing the selected category names; an empty list if none are selected.
     */
    private List<String> getSelectedCategories() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return this.addressCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                .collect(Collectors.toList());
    }


    /**
         * Opens the product comment view (CommentProduct.fxml) in a new window and closes the current window.
         *
         * @param event the ActionEvent originating from the UI control that triggered opening the comment view
         */
    @FXML
    void cinemaclient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/produits/CommentProduct.fxml"));
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
        } catch (final IOException e) {
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                          // d'entrée/sortie
        }

    }


    /**
     * Opens the AffichageEvenementClient view in a new window and closes the current window.
     *
     * @param event the ActionEvent whose source node provides the current window to close and from which the new stage is created
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
        } catch (final IOException e) {
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                          // d'entrée/sortie
        }

    }


    /**
     * Opens the product listing view (AfficherProductClient.fxml) in a new window and closes the current window.
     *
     * @param event the ActionEvent from the UI control that triggered this navigation
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
        } catch (final IOException e) {
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                          // d'entrée/sortie
        }

    }


    /**
     * Placeholder action handler for the client profile action; currently a no-op.
     *
     * <p>Implemented later to navigate to or display the client's profile view.</p>
     */
    @FXML
    void profilclient(final ActionEvent event) {
    }


    /**
     * Opens the film user interface (filmuser.fxml) in a new window and closes the current window.
     *
     * @param event the ActionEvent from the triggering control; used to obtain the current stage to close it
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
        } catch (final IOException e) {
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                          // d'entrée/sortie
        }

    }


    /**
         * Opens the "Series" view in a new window and closes the current window.
         *
         * @param event the ActionEvent that triggered navigation; used to locate and close the current window
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
            stage.setTitle("series ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (final IOException e) {
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                          // d'entrée/sortie
        }

    }


    /**
     * Submit the current text-area value as a comment for the active product after validating for disallowed words.
     *
     * If the message contains prohibited language, a warning alert is shown and the comment is not persisted.
     * Otherwise a Comment is created using the current window's Client and the active product, and the comment is saved via CommentService.
     */
    @FXML
    void addComment() {
        // Check for bad words and prevent further processing if found
        final String userMessage = this.txtAreaComments.getText();
        AfficherProductClientControllers.LOGGER.info("User Message: " + userMessage);
        final String badwordDetection = this.chat.badword(userMessage);
        AfficherProductClientControllers.LOGGER.info("Badword Detection Result: " + badwordDetection);
        final ProductService produitService = new ProductService();
        if ("1".equals(badwordDetection)) {
            final Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Comment non valide");
            alert.setContentText("Votre comment contient des gros mots et ne peut pas être publié.");
            alert.showAndWait();
        }
 else {
            // Créez un objet Comment
            final Client client = (Client) this.txtAreaComments.getScene().getWindow().getUserData();
            final Comment comment = new Comment(client, userMessage, produitService.getProductById(this.produitId));
            final CommentService commentService = new CommentService();
            // Ajoutez le comment à la base de données
            commentService.create(comment);
        }

    }


    /**
     * Get all comments associated with the specified product ID.
     *
     * @param idproduit the product ID to filter comments by
     * @return a list of Comment objects whose associated product's id equals {@code idproduit}
     */
    private List<Comment> getAllComment(final Long idproduit) {
        final CommentService commentService = new CommentService();
        final List<Comment> allComments = commentService.read(); // Récupérer tous les comments
        final List<Comment> comments = new ArrayList<>();
        // Filtrer les comments pour ne conserver que ceux du cinéma correspondant
        for (final Comment comment : allComments) {
            if (comment.getProduct().getId() == idproduit) {
                comments.add(comment);
            }

        }

        return comments;
    }


    /**
     * Populates the comments UI with all comments for the specified product.
     *
     * @param idproduit the product ID whose comments should be loaded and displayed
     */
    private void displayAllComments(final Long idproduit) {
        final List<Comment> comments = this.getAllComment(idproduit);
        final VBox allCommentsContainer = new VBox();
        for (final Comment comment : comments) {
            final HBox commentView = this.addCommentToView(comment);
            allCommentsContainer.getChildren().add(commentView);
        }

        this.idcomment.setContent(allCommentsContainer);
    }


    /**
     * Add the current comment for the active product and refresh the product's comment list in the UI.
     */
    @FXML
    void AddComment(final MouseEvent event) {
        this.addComment();
        this.displayAllComments(this.produitId);
    }


    /**
     * Create a UI node that displays a single comment with the user's avatar, name, and text.
     *
     * @param comment the Comment to render; its client provides the user's first/last name and optional profile image, and the comment supplies the text
     * @return an HBox containing the user's avatar (circle + ImageView) and a card-like container with the user's name and comment text
     */
    private HBox addCommentToView(final Comment comment) {
        // Création du cercle pour l'image de l'utilisateur
        final Circle imageCircle = new Circle(20);
        imageCircle.setFill(Color.TRANSPARENT);
        imageCircle.setStroke(Color.BLACK);
        imageCircle.setStrokeWidth(2);
        // Image de l'utilisateur
        final String imageUrl = comment.getClient().getPhotoDeProfil();
        final Image userImage;
        if (null != imageUrl && !imageUrl.isEmpty()) {
            userImage = new Image(imageUrl);
        }
 else {
            // Chargement de l'image par défaut
            userImage = new Image(this.getClass().getResourceAsStream("/Logo.png"));
        }

        // Création de l'image view avec l'image de l'utilisateur
        final ImageView imageView = new ImageView(userImage);
        imageView.setFitWidth(50); // Ajuster la largeur de l'image
        imageView.setFitHeight(50); // Ajuster la hauteur de l'image
        // Positionner l'image au centre du cercle
        imageView.setTranslateX(2 - imageView.getFitWidth() / 2);
        imageView.setTranslateY(2 - imageView.getFitHeight() / 2);
        // Ajouter l'image au cercle
        final Group imageGroup = new Group();
        imageGroup.getChildren().addAll(imageCircle, imageView);
        // Création de la boîte pour l'image et la bordure du cercle
        final HBox imageBox = new HBox();
        imageBox.getChildren().add(imageGroup);
        // Création du conteneur pour la carte du comment
        final HBox cardContainer = new HBox();
        cardContainer.setStyle(
                "-fx-background-color: white; -fx-padding: 5px ; -fx-border-radius: 8px; -fx-border-color: #000; -fx-background-radius: 8px; ");
        // Nom de l'utilisateur
        final Text userName = new Text(comment.getClient().getFirstName() + " " + comment.getClient().getLastName());
        userName.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-weight: bold;");
        // Comment
        final Text commentText = new Text(comment.getCommentText());
        commentText.setStyle("-fx-font-family: 'Arial'; -fx-max-width: 300 ;");
        commentText.setWrappingWidth(300); // Définir une largeur maximale pour le retour à la ligne automatique
        // Création de la boîte pour le texte du comment
        final VBox textBox = new VBox();
        // Ajouter le nom d'utilisateur et le comment à la boîte de texte
        textBox.getChildren().addAll(userName, commentText);
        // Ajouter la boîte d'image et la boîte de texte à la carte du comment
        cardContainer.getChildren().addAll(textBox);
        // Création du conteneur pour l'image, le cercle et la carte du comment
        final HBox contentContainer = new HBox();
        contentContainer.setPrefHeight(50);
        contentContainer.setStyle("-fx-background-color: transparent; -fx-padding: 10px"); // Arrière-plan transparent
        contentContainer.getChildren().addAll(imageBox, cardContainer);
        // Ajouter le conteneur principal au ScrollPane
        this.idcomment.setContent(contentContainer);
        return contentContainer;
    }


    /**
     * Restore the product list view and hide the comments panel.
     *
     * Sets the product flow pane to fully visible (opacity 1) and makes it visible, then hides the comments anchor.
     */
    public void Close(final MouseEvent mouseEvent) {
        this.produitFlowPane.setOpacity(1);
        this.produitFlowPane.setVisible(true);
        this.AnchorComments.setVisible(false);
    }


    /**
     * Restores the product flow pane's visibility and opacity and hides the filter pane.
     *
     * @param mouseEvent the MouseEvent that triggered closing the filter pane
     */
    public void CloseFilter(final MouseEvent mouseEvent) {
        this.produitFlowPane.setOpacity(1);
        this.produitFlowPane.setVisible(true);
        this.filterAnchor.setVisible(false);
    }


    /**
     * Load and display the top three products ordered by quantity and status.
     *
     * Fetches products from ProductService ordered by quantity and status, takes the
     * first three results, creates a compact card for each product, and adds those
     * cards to the controller's topthreeVbox. If fewer than three products are
     * available the method logs that condition and returns; on error it logs the
     * exception.
     */
    public void loadAcceptedTop3() {
        final ProductService produitService = new ProductService();
        try {
            final List<Product> produits = produitService.getProductsOrderByQuantityAndStatus();
            if (3 > produits.size()) {
                AfficherProductClientControllers.LOGGER.info("Pas assez de produits disponibles");
                return;
            }

            final List<Product> top3Products = produits.subList(0, 3);
            int j = 0;
            for (final Product produit : top3Products) {
                AfficherProductClientControllers.LOGGER.info(String.valueOf(produit.getId()));
                final VBox cardContainer = this.createtopthree(produit);
                AfficherProductClientControllers.LOGGER.info("------------------" + j + (cardContainer.getChildren()));
                this.topthreeVbox.getChildren().add(cardContainer);
                j++;
            }

        } catch (final Exception e) {
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            AfficherProductClientControllers.LOGGER.info("Une erreur est survenue lors du chargement des produits");
        }

    }


    /**
     * Create a compact VBox card displaying a product's image, name, and price for the top-three list.
     *
     * @param produit the Product to display (provides image URL, name, and price)
     * @return a VBox containing a styled product card with the product image, name, and price
     */
    public VBox createtopthree(final Product produit) {
        final VBox cardContainer = new VBox(5);
        cardContainer.setStyle("-fx-padding: 20px 0 0 30px;"); // Add left padding
        cardContainer.setPrefWidth(255);
        final AnchorPane card = new AnchorPane();
        card.setLayoutX(0);
        card.setLayoutY(0);
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
            if (!produitImage.isEmpty()) {
                final Image image = new Image(produitImage);
                imageView.setImage(image);
            }
 else {
                // Use a default image if Blob is null
                final Image defaultImage = new Image(this.getClass().getResourceAsStream("defaultImage.png"));
                imageView.setImage(defaultImage);
            }

        } catch (final Exception e) {
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            // Handle any exceptions during image loading
            AfficherProductClientControllers.LOGGER.info("Une erreur est survenue lors du chargement de l'image");
        }

        imageView.setOnMouseClicked(event -> {
            try {
                final FXMLLoader loader = new FXMLLoader(
                        this.getClass().getResource("/ui/produits/DetailsProductClient.fxml"));
                Parent root = null;
                AfficherProductClientControllers.LOGGER
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
            } catch (final IOException e) {
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
                AfficherProductClientControllers.LOGGER
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
            } catch (final IOException e) {
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
