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
     * Searches for products in a list based on a given search term and returns a
     * list of matching products.
     *
     * @param liste
     *                  list of products that will be searched for matches with the
     *                  given
     *                  search query.
     *                  <p>
     *                  - It is a list of `Product` objects.
     * @param recherche
     *                  search query used to filter the list of `Product` objects
     *                  returned
     *                  by the function.
     * @returns a list of `Product` objects that match the given search query.
     *          <p>
     *          - The output is a list of `Product` objects, representing the
     *          matching products in the input list. - The list contains only the
     *          elements from the input list that match the search query. - Each
     *          element in the list has a `Nom` attribute that contains the search
     *          query.
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
     * Sets up UI components for searching and displaying produits, including a
     * search bar, a filter category combo box, and a flow pane to display results.
     * It also initializes a produitService object and sets up listeners for the
     * search bar and category combo box to update the displayed produits.
     *
     * @param location
     *                  URL of the web page that the function is initializing, which
     *                  is
     *                  used to load the accepted products and display them on the
     *                  screen.
     *                  <p>
     *                  - `URL location`: This represents a URL that provides
     *                  information
     *                  about the accepted products.
     * @param resources
     *                  ResourceBundle object that provides localized messages and
     *                  key for
     *                  displaying comments and top3 produits.
     *                  <p>
     *                  - `location`: The URL of the location where the application
     *                  is
     *                  running. - `resources`: A `ResourceBundle` object containing
     *                  key-value pairs of resources used in the application.
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
     * Loads all products from a service and adds them to a flow pane using a
     * recursive method.
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
     * Creates a Card component that displays the details of a product, including
     * its image, name, description, price, and buttons for adding to cart or
     * viewing comments.
     *
     * @param Product
     *                Product object that contains the details of the product being
     *                displayed, and it is used to access the product's properties
     *                and
     *                methods throughout the function, such as retrieving its name,
     *                price, and ID.
     *                <p>
     *                - `id_produit`: The unique identifier for the product - `nom`:
     *                The
     *                product name - `description`: The product description -
     *                `prix`:
     *                The product price - `image`: The product image URL
     * @returns a `Pane` object containing the UI components for displaying a single
     *          product in a shopping cart.
     *          <p>
     *          - `card`: The card container that displays information about the
     *          selected product, including an image view, name label, description
     *          label, price label, add to cart button, and chat icon. -
     *          `imageView`: An image view that displays a picture of the selected
     *          product. - `nameLabel`: A label that displays the name of the
     *          selected product. - `descriptionLabel`: A label that displays a
     *          brief description of the selected product. - `priceLabel`: A label
     *          that displays the price of the selected product. -
     *          `addToCartButton`: A button that allows users to add the selected
     *          product to their cart. - `chatIcon`: An icon that represents the
     *          chat function, which displays all comments for the selected product
     *          when clicked.
     *          <p>
     *          The main attributes of the returned output are:
     *          <p>
     *          - The card container has a white background with a gradient border
     *          from the top left corner to the bottom right corner. - The image
     *          view, name label, description label, price label, and add to cart
     *          button have a font size of 12px, bold font weight, and a padding of
     *          10px. - The chat icon has an icon literal of "fa-commenting" and an
     *          icon size of 20px.
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

        }
 catch (final Exception e) {
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
            }
 catch (final IOException e) {
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
            }
 catch (final IOException e) {
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
     * Clears the current pane, adds the newly added product to a new pane, and
     * makes the updated pane visible while hiding other panes.
     *
     * @param produitAjoute
     *                      product that is being added to the shopping cart, which
     *                      is used to
     *                      create a new `HBox` representing the product card and
     *                      add it to
     *                      the `shoppingcartFlowPane`.
     *                      <p>
     *                      - `Product produitAjoute`: This is an instance of the
     *                      `Product`
     *                      class, representing a product added to the cart.
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
     * Adds a product to the shopping cart based on the product ID and quantity. It
     * first verifies if the product is available, then creates a new shoppingcart
     * object with the product details and user data, and finally adds it to the
     * shoppingcart service for display in the user interface.
     *
     * @param produitId
     *                  ID of the product to be added to the cart.
     * @param quantity
     *                  number of items to be added to the shopping cart.
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
     * Creates a card for each `Product` object in the `produits` list and adds them
     * to the `produitFlowPane`.
     *
     * @param produits
     *                 list of products to create cards for, and is used to iterate
     *                 over
     *                 the products in the list to create the cards.
     *                 <p>
     *                 - `produits`: A list of `Product` objects, containing
     *                 information
     *                 about individual products.
     */
    private void createProductCards(final List<Product> produits) {
        for (final Product produit : produits) {
            final VBox cardContainer = this.createProductCard(produit);
            this.produitFlowPane.getChildren().add(cardContainer);
        }

    }


    /**
     * Generates a card for the shopping cart, containing the total quantity and
     * price of each product, a "Continue Shopping" button, and an "Order Now"
     * button. When the "Continue Shopping" button is clicked, the stage closes and
     * the order summary is displayed.
     *
     * @param produit
     *                products to be added to the shopping cart, and it is used to
     *                display the product name and quantity in the
     *                `ShoppingCartProduct`
     *                FXML file.
     *                <p>
     *                - `name`: the name of the product - `quantite`: the quantity
     *                of
     *                the product in the cart - `sommeTotale`: the total cost of the
     *                product in the cart - `imageView`: an image view of the
     *                product -
     *                `orderr`: a button to add the product to the cart or continue
     *                shopping.
     * @returns a `Node` object representing a card with the contents of the
     *          shopping cart.
     *          <p>
     *          - `shoppingcartContainer`: The parent Node for the entire card,
     *          which contains all the children components. - `cartLabel`: A Label
     *          component displaying the word "ShoppingCart" in bold and a larger
     *          font size. - `closeIcon`: A FontIcon component displaying an icon of
     *          a times circle, used for closing the card. - `imageView`: An
     *          ImageView component displaying a shopping cart icon. - `nameLabel`:
     *          A Label component displaying the text "Nom du produits". -
     *          `quantiteLabel`: A Label component displaying the text "Quantité". -
     *          `sommeTotaleLabel`: A Label component displaying the text "Somme
     *          totale". - `achatbutton`: A Button component displaying the text
     *          "Continuer les achats" and used for navigating to the next stage. -
     *          `orderbutton`: A Button component displaying the text "Order" and
     *          used for adding an item to the cart.
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

        }
 catch (final Exception e) {
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
            }
 catch (final IOException e) {
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
     * Makes a panel invisible and sets another panel's visibility and opacity to 1,
     * effectively hiding the panel that was previously visible.
     *
     * @param shoppingcartContainer
     *                              HBox component that contains the panel
     *                              containing the shopping
     *                              cart.
     *                              <p>
     *                              - `shoppingcartContainer`: A `HBox` component
     *                              representing the
     *                              container for the shoppingcart (basket) display.
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
     * Loads a new UI component (`ShoppingCartProduct.fxml`) when a button is
     * clicked, creates a new scene with the loaded component, and attaches it to a
     * new stage. The new stage is then displayed and the previous stage is closed.
     *
     * @param event
     *              MouseEvent object that triggered the function, providing the
     *              source of the event and any relevant data.
     *              <p>
     *              - Event source: The element that triggered the event (not
     *              specified).
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
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                          // d'entrée/sortie
        }

    }


    /**
     * Filters a list of products based on a search text, by adding to an observable
     * list only those products whose category name contains the search text.
     *
     * @param searchText
     *                   search term used to filter the list of products.
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
     * Retrieves a list of all product categories from the service layer using the
     * `ProductService`. The list is then returned to the caller.
     *
     * @returns a list of `Product` objects retrieved from the service.
     */
    private List<Product> getAllCategories() {
        final ProductService categoryservice = new ProductService();
        PageRequest pageRequest = new PageRequest(0, 10);
        return categoryservice.read(pageRequest).getContent();
    }


    /**
     * Sets the opacity of a pane to 0.5, makes a panel visible, clears lists of
     * check boxes and recieves unique addresses from a database for each category.
     * It then creates VBoxes for the addresses and adds them to a parent pane,
     * making the parent pane visible.
     *
     * @param event
     *              mouse event that triggered the filtrer method, providing the
     *              source of the event and any relevant data.
     *              <p>
     *              - `event`: A MouseEvent object representing the mouse event that
     *              triggered the function. - `MouseEvent.getX()` and
     *              `MouseEvent.getY()`: The coordinates of the mouse event in the
     *              parent coordinate system.
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
     * Retrieves a list of category names from a database by mapping the categories
     * to their respective names using the `getName_categorie()` method.
     *
     * @returns a list of distinct category names obtained from the database.
     *          <p>
     *          The function returns a list of strings, where each string represents
     *          a category name. The list contains all unique category names that
     *          were retrieved from the database using the `getAllCategories()`
     *          method. The categories are obtained by calling the `map()` method on
     *          Gets a list of unique product category names.
     *          This method retrieves all products and extracts their category
     *          names,
     *          removing duplicates to return a list of unique category names.
     *
     * @return a list of unique product category names
     */
    public List<String> getProductCategory() {
        // Récupérer tous les cinémas depuis la base de données
        final List<Product> categories = this.getAllCategories();
        return categories.stream().flatMap(produit -> produit.getCategories().stream()).map(c -> c.getCategoryName())
                .distinct().collect(Collectors.toList());
    }


    /**
     * Filters a list of products based on selected categories, updates the visible
     * state of an anchor and a flow pane, and updates the list of displayed
     * products.
     *
     * @param event
     *              an action event triggered by the user, which initiates the
     *              filtering process of products based on selected categories.
     *              <p>
     *              - Type: ActionEvent - Target: Unknown (since it's not explicitly
     *              specified) - Code: Unknown (since it's not explicitly specified)
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
     * Clears and re-adds a list of products to a flow pane, using a `VBox`
     * container for each product.
     *
     * @param filteredProducts
     *                         list of products that have been filtered based on
     *                         user input, and
     *                         it is used to populate the `produitFlowPane` with
     *                         only the
     *                         relevant products.
     *                         <p>
     *                         - `filteredProducts`: A list of `Product` objects
     *                         that have been
     *                         filtered based on some criteria.
     */
    private void updateProductFlowPane(final List<Product> filteredProducts) {
        this.produitFlowPane.getChildren().clear(); // Effacez les éléments existants
        for (final Product produit : filteredProducts) {
            final VBox cardContainer = this.createProductCard(produit);
            this.produitFlowPane.getChildren().add(cardContainer);
        }

    }


    /**
     * Retrieves a list of selected addresses from an `AnchorPane` component and
     * filters them based on the selected state of CheckBoxes within the pane.
     *
     * @returns a list of selected category names.
     *          <p>
     *          - The output is a list of strings (type `List<String>`). - The list
     *          contains the selected addresses from the `addressCheckBoxes` stream,
     *          where each address is represented by a string. - The addresses are
     *          filtered based on whether the corresponding CheckBox is selected or
     *          not using the `filter()` method. - The text of each selected
     *          CheckBox is mapped to a string using the `map()` method.
     */
    private List<String> getSelectedCategories() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return this.addressCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                .collect(Collectors.toList());
    }


    /**
     * Charges a new FXML interface, creates a new scene, and attaches it to a new
     * stage, while also closing the current stage.
     *
     * @param event
     *              ActionEvent object that triggered the function execution,
     *              providing the source of the event and allowing the code to
     *              determine the appropriate action to take.
     *              <p>
     *              - It is an `ActionEvent`, indicating that it represents an
     *              action
     *              taken on the user interface. - The source of the event is a
     *              `Node`, which represents the element in the user interface that
     *              triggered the event.
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
        }
 catch (final IOException e) {
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                          // d'entrée/sortie
        }

    }


    /**
     * Loads a new FXML interface, creates a new scene, and attaches it to a new
     * stage when an event is triggered. The new stage replaces the current one, and
     * the previous stage is closed.
     *
     * @param event
     *              event that triggered the `eventClient()` method to be called,
     *              providing the necessary information for the method to perform
     *              its
     *              actions.
     *              <p>
     *              - Event source: The object that generated the event, which is
     *              typically a button or other user interface element. - Event
     *              type:
     *              The type of event that was generated, such as a click or a key
     *              press.
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
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                          // d'entrée/sortie
        }

    }


    /**
     * Loads a new FXML file, creates a new scene and stage, and replaces the
     * current stage with the new one.
     *
     * @param event
     *              ActionEvent object that triggered the function execution and
     *              provides access to the source element that caused the event,
     *              which
     *              in this case is an button click.
     *              <p>
     *              - `event`: This represents an action event that occurred in the
     *              application. It provides information about the source of the
     *              event
     *              and its associated actions.
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
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                          // d'entrée/sortie
        }

    }


    /**
     * Likely profiles a client application, possibly collecting data on its
     * performance or behavior.
     *
     * @param event
     *              occurrence of an action event that triggered the execution of
     *              the
     *              `profilclient` function.
     */
    @FXML
    void profilclient(final ActionEvent event) {
    }


    /**
     * Loads a new FXML interface using `FXMLLoader`, creates a new scene with it,
     * and attaches the scene to a new stage. It also closes the current stage.
     *
     * @param event
     *              ActionEvent object that triggered the function execution,
     *              providing the source of the event and allowing the code to
     *              access
     *              the relevant information related to the event.
     *              <p>
     *              - It is an `ActionEvent` representing a user interaction with
     *              the
     *              application. - The source of the event is the `FXMLLoader`
     *              instance that loaded the `filmuser.fxml` file. - The event
     *              provides access to the stage and scene associated with the
     *              event,
     *              which are used to create a new window and replace the current
     *              one.
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
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                          // d'entrée/sortie
        }

    }


    /**
     * Loads a new FXML interface, creates a new scene with it, and attaches the
     * scene to a new stage, while closing the current stage.
     *
     * @param event
     *              ActionEvent object that triggered the function execution,
     *              providing information about the source of the event and allowing
     *              the code to handle the appropriate action.
     *              <p>
     *              - It is an `ActionEvent` object representing a user action that
     *              triggered the function execution. - The source of the event is
     *              typically a button or other widget in the user interface. - The
     *              event may carry additional information such as the ID of the
     *              button pressed, the modifiers used, and so on.
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
        }
 catch (final IOException e) {
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                          // d'entrée/sortie
        }

    }


    /**
     * Allows users to input a comment on a product, checks for bad words and
     * prevents further processing if found. If no bad words are detected, it
     * creates a new `Comment` object with the client information and product ID,
     * and adds it to the database using the `CommentService`.
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
     * Retrieves all comments for a given product ID, filters them to keep only
     * those related to the specified cinema, and returns the filtered list of
     * comments.
     *
     * @param idproduit
     *                  id of the product for which the comments are to be filtered
     *                  and
     *                  returned.
     * @returns a list of commentaries filtered based on the product ID.
     *          <p>
     *          - The `List<Comment>` returned represents all comments for a
     *          specific product with the provided ID. - The list contains `Comment`
     *          objects that have been filtered based on the product ID. - Each
     *          `Comment` object in the list has a `Product` field with the ID of
     *          the matching product.
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
     * Retrieves and displays all comments associated with a product ID using a
     * `VBox` container to hold the comment views and a `getChildren()` method to
     * add them.
     *
     * @param idproduit
     *                  product ID used to retrieve all comments associated with it.
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
     * Adds a new comment to an item and displays all comments for that item upon
     * button click.
     *
     * @param event
     *              mouse event that triggered the `AddComment` method, providing
     *              the
     *              context for the comment creation and display.
     *              <p>
     *              - `MouseEvent event`: This parameter represents an event object
     *              that contains information about the mouse action that triggered
     *              the function. Specifically, it provides details on the button
     *              pressed (left or right), the location of the click within the
     *              parent container, and the state of other buttons.
     */
    @FXML
    void AddComment(final MouseEvent event) {
        this.addComment();
        this.displayAllComments(this.produitId);
    }


    /**
     * Adds an comment to a view by creating an image view with the user's profile
     * picture, adding it to a container with an image circle and a card for the
     * comment, and then adding the container to the ScrollPane.
     *
     * @param comment
     *                Comment object containing information about the user's
     *                comment,
     *                including the client's name and the comment text.
     *                <p>
     *                - `client`: The client who made the comment (a
     *                `CommentClient`) -
     *                `photo_de_profil`: The image URL of the user who made the
     *                comment
     *                (a `String`)
     * @returns a HBox container containing an ImageView, a Group of images and
     *          text, and a VBox for the comment text.
     *          <p>
     *          1/ `HBox contentContainer`: This is the container that holds the
     *          image and the text of the comment. It has a prefheight of 50 pixels
     *          to set the maximum height of the container. 2/ `imageBox`: This is
     *          the group containing the image of the user and the image view. 3/
     *          `cardContainer`: This is the group containing the text box with the
     *          name of the user and the comment, as well as any additional children
     *          added to it. 4/ `textBox`: This is the VBox that contains the text
     *          box with the name of the user and the comment.
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
     * Sets the opacity and visibility of a `FlowPane` component to control its
     * appearance and accessibility.
     *
     * @param mouseEvent
     *                   MouseEvent object that triggered the `Close` method.
     *                   <p>
     *                   - The `mouseEvent` instance represents an event triggered
     *                   by a
     *                   mouse action, such as a click or a drag. - It contains
     *                   information
     *                   about the event, including the location of the event on the
     *                   screen
     *                   and the type of event that occurred.
     */
    public void Close(final MouseEvent mouseEvent) {
        this.produitFlowPane.setOpacity(1);
        this.produitFlowPane.setVisible(true);
        this.AnchorComments.setVisible(false);
    }


    /**
     * Sets the opacity and visibility of a `FlowPane` and its child elements, and
     * hides an `Anchor` element.
     *
     * @param mouseEvent
     *                   mouse event that triggered the `CloseFilter()` method,
     *                   providing
     *                   information about the location and nature of the event.
     *                   <p>
     *                   - `mouseEvent`: The event object representing the mouse
     *                   action
     *                   that triggered the function.
     */
    public void CloseFilter(final MouseEvent mouseEvent) {
        this.produitFlowPane.setOpacity(1);
        this.produitFlowPane.setVisible(true);
        this.filterAnchor.setVisible(false);
    }


    /**
     * Retrieves and displays the top 3 products with the highest quantity and
     * status from a service, creating a VBox for each product and adding it to a
     * parent container.
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

        }
 catch (final Exception e) {
            AfficherProductClientControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            AfficherProductClientControllers.LOGGER.info("Une erreur est survenue lors du chargement des produits");
        }

    }


    /**
     * Generates a `VBox` container with three components: an image view, a label
     * with the product name, and a label with the price. The image view displays an
     * image of the product, while the labels display the product name and price.
     *
     * @param produit
     *                `Product` object that contains information about the product
     *                to be
     *                displayed, including its name, image, and price.
     *                <p>
     *                - `nom`: the product name - `image`: the image URL or a
     *                default
     *                image if null - `prix`: the product price in DT (Djibouti
     *                Francs)
     * @returns a `VBox` container with three elements: an image, a product name,
     *          and a price label.
     *          <p>
     *          1/ `cardContainer`: This is the top-level container for the three
     *          components that make up the card. It has a style class of
     *          `-fx-padding: 20px 0 0 30px;` which adds left padding to the
     *          container. 2/ `imageView`: This is an `ImageView` component that
     *          displays the image of the product. The image view has a layoutX of 5
     *          and a layoutY of 21, and its fit width and height are set to 50. It
     *          also has an OnMouseClicked event listener that triggers when the
     *          image is clicked. 3/ `nameLabel`: This is a `Label` component that
     *          displays the name of the product. The label has a font size of 15
     *          and a style class of `-fx-text-fill: #333333;`, which sets the text
     *          fill color to black. It also has a layoutX of 60 and a layoutY of
     *          25. 4/ `priceLabel`: This is another `Label` component that displays
     *          the price of the product. The label has a font size of 14 and a
     *          Creates a VBox container for displaying top three products.
     *          This method creates a styled container with product image, name, and
     *          price
     *          components arranged vertically.
     *
     * @param produit the product to display in the container
     * @return a VBox container with the product information displayed
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

        }
 catch (final Exception e) {
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

