package com.esprit.controllers.produits;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import org.controlsfx.control.Rating;
import org.kordamp.ikonli.javafx.FontIcon;
import com.esprit.models.produits.Avis;
import com.esprit.models.produits.Panier;
import com.esprit.models.produits.Produit;
import com.esprit.models.users.Client;
import com.esprit.services.produits.AvisService;
import com.esprit.services.produits.PanierService;
import com.esprit.services.produits.ProduitService;
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
 * Is used to display details of a product when the user clicks on its name in the
 * list view. It retrieves the product ID from the event, and then loads an FXML file
 * to display additional information such as price and image. The controller also
 * handles mouse clicks on the image and displays a new stage with additional information.
 */
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
    private FontIcon retour;
    private int produitId;
    private Button addToCartButton;
    @FXML
    private AnchorPane top3anchorpane;
    @FXML
    private FlowPane topthreeVbox;
    private int quantiteSelectionnee = 1; // Initialiser à 1 par défaut
    PanierService panierService = new PanierService();
    Panier panier = new Panier();
    /** 
     * @param produitId
     * @throws IOException
     */
    // Méthode pour initialiser l'ID du produit
    /**
     * Sets the `produitId` field to a given value, then initializes details of the product
     * based on the set ID.
     * 
     * @param produitId ID of the product to which the method is being called, and it is
     * used to store the value in the field `this.produitId`.
     */
    public void setProduitId(int produitId) throws IOException {
        this.produitId = produitId;
        // Initialiser les détails du produit après avoir défini l'ID
        initDetailsProduit();
    }
    /**
     * Loads a list of top-3 accepted products and attaches an event handler to the icon's
     * mouse click, which displays the product when clicked.
     * 
     * @param location URL of the resource to be initialized, which is used to load the
     * accepted top-3 products when the function is called.
     * 
     * Location refers to an URL that provides the root resource of the application. It
     * represents the base URL of the application and is used in loading accepted top 3.
     * 
     * @param resources ResourceBundle containing keys for localized strings, which are
     * used to display product information when the user clicks on the icon.
     * 
     * 	- `location`: A URL object representing the location of the application.
     * 	- `resources`: A ResourceBundle object providing key-value pairs of resource
     * strings in different languages.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAcceptedTop3();
        // Attacher un gestionnaire d'événements au clic de la souris sur l'icône de
        // retour
        retour.setOnMouseClicked(event -> afficherProduit());
    }
    /**
     * Returns the value of the `quantiteSelectionnee` field.
     * 
     * @returns the value of the `quantiteSelectionnee` variable.
     */
    public int getQuantiteSelectionnee() {
        return quantiteSelectionnee;
    }
    /**
     * Retrieves a product from a service using its ID, verifies if the product is found,
     * and adds a card for the product to a `FlowPane` if it exists, and also adds a card
     * for the product's cart to another `FlowPane`.
     */
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
                () -> System.err.println("Produit non trouvé avec l'ID : " + produitId));
    }
    /**
     * Creates a scene containing a card for a product, with an image, name, description,
     * price, rating, and a "Add to Cart" button. The rating is calculated based on the
     * average rating of the product and displayed as a number of stars out of 5. When
     * the "Add to Cart" button is clicked, the function deletes any existing rating for
     * the product and creates a new rating with the current rating value.
     * 
     * @param produit Product object that is being displayed in the detail view, and it
     * is used to retrieve the product's ID, name, image URL, price, and rating for display
     * in the corresponding Card element.
     * 
     * 	- `id_produit`: an integer representing the product ID
     * 	- `nom`: a string representing the product name
     * 	- `description`: a string representing the product description
     * 	- `prix`: a double representing the product price
     * 	- `imagePath`: a string representing the path to the product image
     * 
     * Note: These properties are not explained in detail as they are already defined in
     * the function.
     * 
     * @returns a stage with a card containing information about a product, including an
     * image, name, description, price, and rating.
     * 
     * 	- `card`: The `Card` object that represents the product card, containing various
     * elements such as an image view, name label, description label, price label, rating,
     * and a "Add to cart" button.
     * 	- `imageView`: An `Image` view that displays the product image.
     * 	- `nameLabel`: A `Label` that displays the product name.
     * 	- `descriptionLabel`: A `Label` that displays the product description.
     * 	- `priceLabel`: A `Label` that displays the product price.
     * 	- `rating`: A `Rating` widget that displays the average rating of the product
     * based on customer reviews.
     * 	- `etoilelabel`: A `Label` that displays the current rating out of 5, using a
     * yellow font and formatting.
     * 	- `addToCartButton`: An `Hyperlink` button that allows users to add the product
     * to their cart.
     * 	- `iconeetoile`: A `FontIcon` widget that displays an orange "star" icon when the
     * product has no reviews, and a yellow "star" icon when the product has at least one
     * review.
     */
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
            String produitImage = produit.getImage();
            if (produitImage != null) {
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
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(400);
        nameLabel.setLayoutY(30);
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique
        // Prix du Produit
        Label priceLabel = new Label(" " + produit.getPrix() + " DT");
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        priceLabel.setLayoutX(410);
        priceLabel.setLayoutY(200);
        Label descriptionLabel = new Label(produit.getDescription());
        descriptionLabel.setFont(Font.font("Arial", 14));
        descriptionLabel.setTextFill(Color.web("#392c2c")); // Définir la même couleur de texte que descriptionText
        descriptionLabel.setLayoutX(410);
        descriptionLabel.setLayoutY(95);
        descriptionLabel.setMaxWidth(250); // Ajuster la largeur maximale selon vos besoins
        descriptionLabel.setWrapText(true); // Activer le retour à la ligne automatique
        // Bouton Ajouter au Panier
        Button addToCartButton = new Button("Add to Cart", new FontIcon("fa-cart-plus"));
        addToCartButton.setLayoutX(435);
        addToCartButton.setLayoutY(300);
        // addToCartButton.getStyleClass().add("sale"); // Style du bouton
        addToCartButton.setStyle("-fx-background-color: #dd4f4d;\n"
                + "    -fx-text-fill: #FFFFFF;\n"
                + "    -fx-font-size: 12px;\n"
                + "    -fx-font-weight: bold;\n"
                + "    -fx-padding: 10px 10px;");
        addToCartButton.setOnAction(
                event -> {
                    int produitId = produit.getId_produit();
                    int quantity = 1; // Vous pouvez ajuster la quantité en fonction de vos besoins
                    ajouterAuPanier(produitId, quantity);
                    panierAnchorPane.setVisible(true);
                    detailFlowPane.setVisible(true);
                    detailFlowPane.setOpacity(0.2);
                    top3anchorpane.setVisible(false);
                });
        Avis avis = new Avis();
        double rate = new AvisService().getavergerating(produit.getId_produit());
        System.out.println(BigDecimal.valueOf(rate).setScale(1, RoundingMode.FLOOR));
        // Champ de notation (Rating)
        Rating rating = new Rating();
        rating.setLayoutX(410);
        rating.setLayoutY(390);
        rating.setMax(5);
        rating.setRating(avis.getNote()); // Vous pouvez ajuster en fonction de la valeur du produit
        String format = String.format("%.1f/5", BigDecimal.valueOf(rate).setScale(1, RoundingMode.FLOOR).doubleValue());
        Label etoilelabel = new Label(format);
        etoilelabel.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        etoilelabel.setStyle("-fx-text-fill: #333333;");
        etoilelabel.setLayoutX(410);
        etoilelabel.setLayoutY(230);
        FontIcon iconeetoile = new FontIcon();
        iconeetoile.setIconLiteral("fab-star");
        iconeetoile.setFill(Color.YELLOW);
        iconeetoile.setIconSize(20);
        iconeetoile.setLayoutX(465);
        iconeetoile.setLayoutY(250);
        Avis avi = new AvisService().ratingExiste(produit.getId_produit(), /* (Client) stage.getUserData() */ 4);
        rating.setRating(avi != null ? avi.getNote() : 0);
        // Stage stage = (Stage) hyperlink.getScene().getWindow();
        rating.ratingProperty().addListener((observableValue, number, t1) -> {
            AvisService avisService = new AvisService();
            Avis avi1 = avisService.ratingExiste(produit.getId_produit(), 4 /* (Client) stage.getUserData() */);
            if (avi != null) {
                avisService.delete(avi1);
            }
            avisService.create(new Avis(/* (Client) stage.getUserData() */(Client) new UserService().getUserById(4),
                    t1.intValue(), produit));
            double rate1 = new AvisService().getavergerating(produit.getId_produit());
            // Formater le texte avec une seule valeur après la virgule
            String formattedRate = String.format("%.1f/5",
                    BigDecimal.valueOf(rate1).setScale(1, RoundingMode.FLOOR).doubleValue());
            System.out.println(formattedRate);
            etoilelabel.setText(formattedRate);
        });
        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel, rating, etoilelabel,
                addToCartButton, iconeetoile);
        cardContainer.getChildren().add(card);
        return cardContainer;
    }
    /**
     * Adds a product to the shopping cart based on available stock, retrieves product
     * details and user information, creates a new panier object, and displays the updated
     * panier.
     * 
     * @param produitId ID of the product to be added to the cart.
     * 
     * @param quantity quantity of the product that the user wants to add to the cart.
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
            panier.setUser(usersService.getUserById(4));
            panierService.create(panier);
            afficherpanier(); // Utilisez le produit ajouté pour afficher dans le panier
        } else {
            // Afficher un message d'avertissement sur le stock insuffisant
            System.out.println("Stock insuffisant pour le produit avec l'ID : " + produitId);
        }
    }
    /**
     * Loads an FXML page, creates a new stage for it, and shows it modally, closing the
     * previous stage.
     */
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
    /**
     * Sets the visibility of a `FlowPane` and its child elements to true, with the
     * `opacity` of one of the child elements set to 0.2.
     */
    private void afficherpanier() {
        // Initialiser la visibilité des AnchorPane
        panierFlowPane.setVisible(true);
        detailFlowPane.setVisible(true);
        detailFlowPane.setOpacity(0.2);
        // top3anchorpane.setVisible(false);
    }
    /**
     * Creates a `Card` object that displays the cart contents, includes a "Continue
     * Shopping" button and an icon for closing the card. It also sets up event handlers
     * for closing the card and the "Continue Shopping" button.
     * 
     * @param produit product to be displayed in the shopping cart card, which is used
     * to set the corresponding labels and images for each product in the panier.
     * 
     * 	- `name`: The name of the product.
     * 	- `imageUrl`: The URL of the product image.
     * 	- `price`: The price of the product in euros.
     * 	- `quantite`: The quantity of the product in stock.
     * 	- `sommeTotale`: The total cost of the product in euros, calculated by multiplying
     * the quantity by the price.
     * 
     * @returns a `Pane` object containing all the elements of the shopping cart.
     * 
     * 	- `panierContainer`: The Parent Node that holds all the children components for
     * the panier card.
     * 	- `card`: A `Node` object that represents the panier card component.
     * 	- `cartLabel`: A `Text` object that displays the total number of items in the panier.
     * 	- `imageView`: An `Image` object that displays a cart icon.
     * 	- `nameLabel`: A `Text` object that displays the name of the product.
     * 	- `priceLabel`: A `Text` object that displays the price of the product.
     * 	- `quantiteLabel`: A `Text` object that displays the quantity of the product in
     * the panier.
     * 	- `sommeTotaleLabel`: A `Text` object that displays the total cost of the products
     * in the panier.
     * 	- `achatbutton`: A `Button` object that allows the user to continue shopping.
     * 	- `commandebutton`: A `Button` object that allows the user to add the product to
     * the panier.
     * 	- `closeIcon`: A `FontIcon` object that displays a close icon for the panier card.
     */
    private HBox createPanierCard(Produit produit) {
        // Créer une carte pour le produit avec ses informations
        HBox panierContainer = new HBox();
        panierContainer.setStyle("-fx-padding: 0px 0 0 20px;"); // Ajout de remplissage à gauche pour le décalage
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
        imageView.setLayoutY(75);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        try {
            String produitImage = produit.getImage();
            if (produitImage != null) {
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
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(10);
        nameLabel.setLayoutY(235);
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique
        // Prix du Produit
        Label priceLabel = new Label(" " + produit.getPrix() + " DT");
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        priceLabel.setLayoutX(10);
        priceLabel.setLayoutY(270);
        // Champ de texte pour la quantité
        Label quantiteLabel = new Label("Quantité : 1 ");
        quantiteLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        quantiteLabel.setLayoutX(30);
        quantiteLabel.setLayoutY(290);
        Label sommeTotaleLabel = new Label("Somme totale : " + produit.getPrix() + " DT");
        sommeTotaleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        sommeTotaleLabel.setLayoutX(30);
        sommeTotaleLabel.setLayoutY(320);
        // Bouton Ajouter au Panier
        Button commandebutton = new Button("Order", new FontIcon("fa-cart-plus"));
        commandebutton.setLayoutX(50);
        commandebutton.setLayoutY(350);
        commandebutton.setPrefWidth(120);
        commandebutton.setPrefHeight(35);
        commandebutton.setStyle("-fx-background-color: #624970;\n"
                + " -fx-text-fill: #FCE19A;"
                + "   -fx-font-size: 12px;\n"
                + "     -fx-font-weight: bold;\n"
                + " -fx-background-color: #6f7b94"); // Style du bouton
        commandebutton.setOnAction(
                event -> {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/DesignProduitAdmin.fxml"));
                    try {
                        Parent root = null;
                        root = fxmlLoader.load();
                        // Parent rootNode = fxmlLoader.load();
                        // Scene scene = new Scene(rootNode);
                        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        Stage newStage = new Stage();
                        newStage.setScene(new Scene(root, 1280, 700));
                        newStage.setTitle("my cart");
                        newStage.show();
                        // Fermer la fenêtre actuelle
                        currentStage.close();
                    } catch (IOException e) {
                        e.printStackTrace(); // Affiche l'erreur dans la console (vous pourriez le
                        // remplacer par une boîte de dialogue)
                        System.out.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
                    }
                });
        // Bouton Ajouter au Panier
        Button achatbutton = new Button("continue shopping");
        achatbutton.setLayoutX(50);
        achatbutton.setLayoutY(400);
        achatbutton.setPrefHeight(30);
        achatbutton.setStyle(" -fx-background-color: #466288;\n"
                + "    -fx-text-fill: #FCE19A;\n"
                + "    -fx-font-size: 12px;\n"
                + "    -fx-font-weight: bold;\n"
                + "    -fx-padding: 10px 10px;");
        achatbutton.setOnAction(
                event -> {
                    fermerPanierCard(panierContainer);
                });
        // Icône de fermeture (close)
        FontIcon closeIcon = new FontIcon();
        closeIcon.setIconLiteral("fa-times-circle");
        closeIcon.setIconSize(20);
        closeIcon.setLayoutX(220);
        closeIcon.setLayoutY(20);
        closeIcon.getStyleClass().add("close"); // Style de l'icône
        // Attachez un gestionnaire d'événements pour fermer la carte du panier
        closeIcon.setOnMouseClicked(event -> fermerPanierCard(panierContainer));
        card.getChildren().addAll(cartLabel, imageView, nameLabel, priceLabel, quantiteLabel, sommeTotaleLabel,
                achatbutton, commandebutton, closeIcon);
        panierContainer.getChildren().add(card);
        return panierContainer;
    }
    /**
     * Calculates the total price of a product based on its ID and quantity. It retrieves
     * the unitarian price of the product from the `ProduitService` class and multiplies
     * it by the input quantity to obtain the total price.
     * 
     * @param idProduit ID of the product for which the price is being calculated.
     * 
     * @param quantity number of products to be priced, and is multiplied by the unit
     * price returned by the `getPrixProduit()` method to calculate the total price for
     * the specified quantity of products.
     * 
     * @returns the product of the quantity and the price of the product for the given ID.
     */
    private double prixProduit(int idProduit, int quantity) {
        ProduitService produitService = new ProduitService();
        double prixUnitaire = produitService.getPrixProduit(idProduit);
        return quantity * prixUnitaire;
    }
    /**
     * Renders the cart card invisible, makes the `panierFlowPane` and `detailFlowPane`
     * visible and sets their opacity to 1. Additionally, it makes the `anchordetail` and
     * `top3anchorpane` visible and sets their opacity to 1.
     * 
     * @param panierContainer `HBox` container that holds the `FlowPane` representing the
     * shopping cart, which is rendered invisible when the function is called.
     * 
     * 	- `panierContainer` is an instance of `HBox`.
     * 	- It contains a `FlowPane` and other components.
     */
    private void fermerPanierCard(HBox panierContainer) {
        // Rendre la carte du panier invisible
        // Rendre panierFlowPane invisible
        panierFlowPane.setVisible(false);
        // Rendre detailFlowPane visible et ajuster l'opacité à 1
        detailFlowPane.setVisible(true);
        detailFlowPane.setOpacity(1);
        anchordetail.setVisible(true);
        anchordetail.setOpacity(1);
        top3anchorpane.setVisible(true);
        top3anchorpane.setOpacity(1);
    }
    /**
     * Loads a new FXML interface, creates a new scene and attaches it to a new stage,
     * replacing the current stage, and closes the original stage.
     * 
     * @param event MouseEvent object that triggered the function execution, providing
     * information about the mouse click or other event that occurred.
     * 
     * 	- Event source: The element that triggered the event (not shown)
     * 	- Type: The type of event (not shown)
     * 	- X and Y coordinates: The position of the event relative to the element (not shown)
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
     * Loads a new user interface using an FXML loader, creates a new scene, and attaches
     * it to a new stage when an event is triggered. It also closes the current stage
     * upon execution.
     * 
     * @param event MouseEvent that triggered the function execution, providing information
     * about the mouse click or other event that occurred in the FXML document.
     * 
     * 	- It is a `MouseEvent` object representing a mouse event that triggered the
     * function's execution.
     * 	- The source of the event is the element that was clicked or hovered over, which
     * is not explicitly stated in the code snippet provided.
     */
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
            stage.setTitle("Commentaire des produits");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }
    }
    /**
     * Loads a new UI scene (`CommentaireProduit.fxml`) into an existing stage when an
     * action event is triggered, replacing the current scene and closing the previous stage.
     * 
     * @param event ActionEvent object that triggered the function execution, providing
     * the source of the event and allowing the code to access its related information.
     * 
     * 	- `event`: an ActionEvent object representing the event triggered by the user's
     * action, such as clicking on a button or entering text in a field.
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
     * Loads a new FXML file, creates a new scene and stage, and replaces the current
     * stage with the new one.
     * 
     * @param event EventObject that triggered the method, providing access to information
     * about the event, such as its source and details, which can be used to handle the
     * event appropriately.
     * 
     * 	- Event object contains information about the event that triggered the function,
     * such as the source element and the type of event.
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
     * Loads a new FXML file, creates a new scene, and attaches it to a new stage, replacing
     * the current stage. It also closes the current stage.
     * 
     * @param event ActionEvent object that triggered the function execution, providing
     * the source of the action and allowing the code to access the event details.
     * 
     * 	- Event source: The object that triggered the event (in this case, a button press)
     * 	- Type of event: The type of event that was triggered (in this case, an action event)
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
     * Appears to be a Java method that handles an `ActionEvent`. It does not provide any
     * information about the code author or licensing, and its purpose is not explicitly
     * stated in the provided code snippet.
     * 
     * @param event client profile data that triggered the execution of the `profilclient`
     * method.
     */
    @FXML
    void profilclient(ActionEvent event) {
    }
    /**
     * Loads a new user interface (`filmuser.fxml`) using the `FXMLLoader`, creates a new
     * scene with it, and attaches it to a new stage. It also closes the current stage
     * and displays the new one.
     * 
     * @param event ActionEvent object that triggered the function execution, providing
     * information about the source of the event and the type of action performed.
     * 
     * 	- Event source: The object that generated the event, which is typically a button
     * or other user interface element.
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
     * Loads a new FXML file, creates a new scene and attaches it to a new stage, replacing
     * the current stage.
     * 
     * @param event ActionEvent object that triggered the `SeriesClient()` method, providing
     * information about the source of the event and allowing the method to determine the
     * appropriate action to take.
     * 
     * 	- Event source: The origin of the event, which is an instance of the `ActionEvent`
     * class.
     * 	- Parameter: An optional parameter associated with the event, which can be of any
     * type.
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
            stage.setTitle("chat ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }
    }
    /**
     * Retrieves and displays a list of the top 3 best-selling products based on their
     * quantity and status, using a ProduitService instance to retrieve the data and
     * create card containers for each product.
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
     * Generates a `VBox` container that displays a product's name, price, and image. The
     * image is loaded from an URL or a default image if null, and the name and price are
     * displayed with font style and size. When the product name is clicked, a new stage
     * displaying details of the product is opened.
     * 
     * @param produit Produit object that contains information about the product, including
     * its name, image, and price, which are used to create the UI elements and display
     * them in the card container.
     * 
     * 	- `id_produit`: an integer representing the product ID.
     * 	- `nom`: the product name.
     * 	- `prix`: the product price.
     * 	- `image`: a Blob object containing the image of the product (optional).
     * 
     * @returns a `VBox` container containing an `ImageView`, a `Label`, and a `Label`,
     * representing the product's name, price, and image.
     * 
     * 1/ `cardContainer`: A `VBox` object that contains three components: an `Label`,
     * an `ImageView`, and another `Label`.
     * 2/ `imageView`: An `ImageView` component that displays an image of the product.
     * 3/ `nameLabel`: A `Label` component that displays the product name.
     * 4/ `priceLabel`: A `Label` component that displays the product price.
     * 5/ `card`: The root `Node` of the `VBox` container, which contains the three
     * components mentioned above.
     */
    public VBox createtopthree(Produit produit) {
        VBox cardContainer = new VBox(5);
        cardContainer.setStyle("-fx-padding: 20px 0 0 30px;"); // Add left padding
        AnchorPane card = new AnchorPane();
        card.setLayoutX(0);
        card.setLayoutY(0);
        cardContainer.setPrefWidth(255);
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
            if (produitImage != null) {
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
