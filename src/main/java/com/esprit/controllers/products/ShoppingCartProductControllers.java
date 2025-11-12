package com.esprit.controllers.products;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kordamp.ikonli.javafx.FontIcon;

import com.esprit.models.products.*;
import com.esprit.models.users.Client;
import com.esprit.services.products.ProductService;
import com.esprit.services.products.ShoppingCartService;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Controller class for managing the shopping cart interface in the RAKCHA
 * application.
 * This controller handles displaying the products in the shopping cart,
 * allowing users
 * to modify quantities, delete items, and proceed to checkout.
 * 
 * <p>
 * The controller manages the cart flow pane, price total display, and provides
 * functionality for navigating to other parts of the application.
 * </p>
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ShoppingCartProductControllers implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(ShoppingCartProductControllers.class.getName());
    // Déclarez une structure de données pour stocker les VBox associés aux produits
    private final Map<Integer, VBox> produitVBoxMap = new HashMap<>();
    Order order = new Order();
    // Mettez à jour le prix total dans SharedData
    // SharedData sharedData = new SharedData();
    ShoppingCartService shoppingcartService = new ShoppingCartService();
    ShoppingCart shoppingcart = new ShoppingCart();
    @FXML
    private FlowPane cartFlowPane;
    @FXML
    private FlowPane prixtotaleFlowPane;
    private double prixTotal;
    @FXML
    private FontIcon retour;

    /**
     * Load and display the current user's shopping cart once the FXML UI has been initialized.
     *
     * This method schedules loading on the JavaFX application thread so UI components
     * are populated after the root element is fully processed.
     *
     * @param url            location used to resolve relative paths for the root object, or null if unknown
     * @param resourceBundle resources used to localize the root object, or null if not localized
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        Platform.runLater(new Runnable() {
            /**
             * Loads accepted shopping cart items into the UI.
             */
            @Override
            /**
             * Performs run operation.
             *
             * @return the result of the operation
             */
            public void run() {
                loadAcceptedShoppingCart();
            }

        }
);
    }


    /**
     * Load the current user's shopping cart items into the UI.
     *
     * Fetches shopping cart entries for the window's current Client, creates a product VBox
     * for each product that is not already present in the UI, adds the VBox to `cartFlowPane`,
     * and records the mapping from product ID to its VBox in `produitVBoxMap`.
     */
    private void loadAcceptedShoppingCart() {
        // Récupérer toutes les produits depuis le service
        final ShoppingCartService shoppingcartService = new ShoppingCartService();
        List<ShoppingCart> items = null;
        try {
            final Client client = (Client) this.cartFlowPane.getScene().getWindow().getUserData();
            items = shoppingcartService.readUserShoppingCart(client.getId());
        }
 catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        for (final ShoppingCart shoppingcart1 : items) {
            // Vérifier si le produit a déjà été ajouté
            if (!this.produitVBoxMap.containsKey(shoppingcart1.getProduct().getId())) {
                final VBox produitVBox = this.createProductVBox(shoppingcart1);
                this.cartFlowPane.getChildren().add(produitVBox);
                // Ajouter le produit à la map pour le marquer comme déjà ajouté
                this.produitVBoxMap.put(shoppingcart1.getProduct().getId().intValue(), produitVBox);
            }

        }

    }


    /**
     * Create a JavaFX Label that displays the total price.
     *
     * The label's text is formatted as "{prixTotal} DT" and styled with Verdana 20 font and a red text color.
     *
     * @param prixTotal the total price value to display
     * @return a Label containing the formatted total price with applied font and color styling
     */
    private Label createPrixTotalLabel(final double prixTotal) {
        // Créez le Label du prix total ici
        final Label prixTotalLabel = new Label(prixTotal + " DT");
        prixTotalLabel.setFont(Font.font("Verdana", 20));
        prixTotalLabel.setLayoutX(30);
        prixTotalLabel.setLayoutY(30);
        prixTotalLabel.setStyle("-fx-text-fill: #d72222;");
        return prixTotalLabel;
    }


    /**
     * Create a VBox card presenting a shopping-cart item with controls to adjust quantity,
     * select the item for ordering, view its total price, and delete it from the cart.
     *
     * @param ShoppingCart the ShoppingCart entry whose product, quantity, and related data
     *                     are displayed and manipulated by the returned UI card
     * @return a configured VBox containing the product image, labels, quantity controls,
     *         selection checkbox, and delete action
     */
    private VBox createProductVBox(final ShoppingCart ShoppingCart) {
        final VBox produitVBox = new VBox();
        produitVBox.setStyle("-fx-padding: 20px 0 0  10px;"); // Ajout de remplissage à gauche pour le décalage
        final AnchorPane card = new AnchorPane();
        // Image du Product
        final ImageView imageView = new ImageView();
        imageView.setLayoutX(10);
        imageView.setLayoutY(3);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        try {
            final String produitImage = ShoppingCart.getProduct().getImage();
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
            ShoppingCartProductControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        // Nom du Product
        final Label nameLabel = new Label(ShoppingCart.getProduct().getName());
        nameLabel.setLayoutX(175);
        nameLabel.setLayoutY(10);
        nameLabel.setFont(Font.font("Arial", 20));
        nameLabel.setStyle("-fx-text-fill: black;");
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique
        // Prix unitaire du Product
        final Label priceLabel = new Label(" " + ShoppingCart.getProduct().getPrice() + " DT");
        priceLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        priceLabel.setLayoutX(180);
        priceLabel.setLayoutY(140);
        // la quantité
        final TextField quantityTextField = new TextField("1");
        quantityTextField.setPromptText("Quantité");
        quantityTextField.setLayoutX(375);
        quantityTextField.setLayoutY(80);
        quantityTextField.setMaxWidth(50);
        quantityTextField.setEditable(false); // Empêcher l'édition manuelle
        final int totalQuantiteProduct = Integer.parseInt(quantityTextField.getText());
        final Label sommeTotaleLabel = new Label(
                "Somme totale : " + this.prixProduct(ShoppingCart.getProduct().getId(), totalQuantiteProduct) + " DT");
        sommeTotaleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        sommeTotaleLabel.setLayoutX(550);
        sommeTotaleLabel.setLayoutY(60);
        sommeTotaleLabel.setMaxWidth(150); // Ajuster la largeur max double sommeTotaleProduct =
        // ShoppingCartService.calculerSommeTotaleProduct(ShoppingCart.getProduct().getId());
        sommeTotaleLabel.setWrapText(true); // Activer le retour à la ligne automatique
        // CheckBox pour sélectionner le produit
        final CheckBox checkBox = new CheckBox("Select");
        checkBox.setLayoutX(650);
        checkBox.setLayoutY(150);
        checkBox.setSelected(false); // Vous pouvez définir l'état initial selon vos besoins
        final OrderItem orderItem = new OrderItem();
        orderItem.setProduct(ShoppingCart.getProduct());
        orderItem.setQuantity(Integer.parseInt(quantityTextField.getText()));
        // Ajout d'un gestionnaire d'événements pour réagir aux changements d'état de la
        // CheckBox
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // Réagir au changement d'état de la CheckBox (sélectionné ou non)
            if (newValue) {
                this.order.getOrderItems().add(orderItem);
                ShoppingCartProductControllers.LOGGER.info("Product sélectionné");
                this.updatePrixTotal();
            }
 else {
                this.order.getOrderItems().remove(orderItem);
                ShoppingCartProductControllers.LOGGER.info("Product non sélectionné");
                this.updatePrixTotal();
            }

        }
);
        // Icône de diminution de quantité
        final FontIcon decreaseIcon = new FontIcon("fa-minus");
        decreaseIcon.setIconSize(15);
        decreaseIcon.setLayoutX(350);
        decreaseIcon.setLayoutY(95);
        decreaseIcon.setOnMouseClicked(event -> {
            if (MouseButton.PRIMARY == event.getButton()) {
                this.decreaseQuantity(quantityTextField, ShoppingCart);
                sommeTotaleLabel.setText("Somme totale : " + this.prixProduct(ShoppingCart.getProduct().getId(),
                        Integer.parseInt(quantityTextField.getText())) + " DT");
                this.order.getOrderItems().get(this.order.getOrderItems().indexOf(orderItem))
                        .setQuantity(Integer.parseInt(quantityTextField.getText()));
                this.updatePrixTotal();
            }

        }
);
        // Icône d'augmentation de quantité
        final FontIcon increaseIcon = new FontIcon("fa-plus");
        increaseIcon.setIconSize(15);
        increaseIcon.setLayoutX(435);
        increaseIcon.setLayoutY(95);
        increaseIcon.setOnMouseClicked(event -> {
            if (MouseButton.PRIMARY == event.getButton()) {
                this.increaseQuantity(quantityTextField, ShoppingCart);
                sommeTotaleLabel.setText("Somme totale : " + this.prixProduct(ShoppingCart.getProduct().getId(),
                        Integer.parseInt(quantityTextField.getText())) + " DT");
                this.order.getOrderItems().get(this.order.getOrderItems().indexOf(orderItem))
                        .setQuantity(Integer.parseInt(quantityTextField.getText()));
                this.updatePrixTotal();
            }

        }
);
        final FontIcon deleteIcon = new FontIcon("fa-trash");
        deleteIcon.setIconSize(25);
        deleteIcon.setLayoutX(700); // Définissez la position X selon vos besoins
        deleteIcon.setLayoutY(30); // Définissez la position Y selon vos besoins
        deleteIcon.setOnMouseClicked(event -> {
            if (MouseButton.PRIMARY == event.getButton()) {
                final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation de suppression");
                alert.setHeaderText(null);
                alert.setContentText("Voulez-vous vraiment supprimer cet élément du shoppingcart?");
                final Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // L'utilisateur a confirmé la suppression
                    this.shoppingcartService.delete(ShoppingCart);
                    // Mettez à jour votre interface utilisateur ici
                    this.refreshUI();
                }

            }

        }
);
        card.setStyle("""
                -fx-background-color:#F6F2F2;
                 -fx-text-fill: #FFFFFF;
                    -fx-font-size: 12px;
                    -fx-font-weight: bold;
                    -fx-padding: 10px;
                    -fx-border-color:  #ae2d3c;/* Couleur de la bordure */
                    -fx-border-width: 2px; /* Largeur de la bordure */
                    -fx-border-radius: 5px; /* Rayon de la bordure pour arrondir les coins */\
                """);
        // Ajouter tous les éléments au VBox
        card.getChildren().addAll(imageView, nameLabel, priceLabel, decreaseIcon, quantityTextField, increaseIcon,
                sommeTotaleLabel, checkBox, deleteIcon);
        this.produitVBoxMap.put(ShoppingCart.getProduct().getId().intValue(), produitVBox);
        produitVBox.getChildren().add(card);
        return produitVBox;
    }


    /**
     * Clears the current elements from the shopping cart, reloads the accepted
     * items, and updates the total price.
     */
    private void refreshUI() {
        // Effacer les éléments actuels du shoppingcart
        this.cartFlowPane.getChildren().clear();
        this.produitVBoxMap.clear();
        // Charger à nouveau les produits du shoppingcart
        this.loadAcceptedShoppingCart();
        // Mettre à jour le prix total
        this.updatePrixTotal();
    }


    /**
     * Decrements the displayed and stored quantity for a cart item by one, respecting a minimum of 1.
     *
     * If the current quantity shown in the provided TextField is greater than 1, this method
     * reduces that value by one and updates the corresponding ShoppingCart object's quantity.
     *
     * @param quantityTextField the TextField that displays the current quantity for the cart item
     * @param shoppingcart the ShoppingCart item to update
     */
    private void decreaseQuantity(final TextField quantityTextField, final ShoppingCart shoppingcart) {
        // Diminuer la quantité
        final int quantity = Integer.parseInt(quantityTextField.getText());
        if (1 < quantity) {
            quantityTextField.setText(String.valueOf(quantity - 1));
            shoppingcart.setQuantity(shoppingcart.getQuantity() - 1);
        }

    }


    /**
         * Check whether the product has at least the requested quantity in stock.
         *
         * @param produit the product whose stock is being checked
         * @param quantity the number of units requested
         * @return `true` if the product's stock quantity is greater than or equal to `quantity`, `false` otherwise
         */
    private boolean isStockAvailable(final Product produit, final int quantity) {
        // Comparer la quantité demandée avec la quantité disponible en stock
        return produit.getQuantity() >= quantity;
    }


    /**
     * Increment the shopping cart item's quantity by one if stock permits.
     *
     * Updates the provided quantity TextField and the ShoppingCart's quantity when
     * stock is sufficient; otherwise displays a warning alert.
     *
     * @param quantityTextField the TextField that displays the current quantity for the item
     * @param shoppingcart      the ShoppingCart entry whose quantity will be incremented
     */
    private void increaseQuantity(final TextField quantityTextField, final ShoppingCart shoppingcart) {
        final int currentQuantity = Integer.parseInt(quantityTextField.getText());
        // Vérifier si le stock est disponible
        if (this.isStockAvailable(shoppingcart.getProduct(), currentQuantity + 1)) {
            quantityTextField.setText(String.valueOf(currentQuantity + 1));
            shoppingcart.setQuantity(shoppingcart.getQuantity() + 1);
        }
 else {
            // Afficher une alerte en cas de stock insuffisant
            final Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Stock Insuffisant");
            alert.setHeaderText("La quantité demandée dépasse la quantité en stock.");
            alert.setContentText("Veuillez réduire la quantité demandée.");
            alert.showAndWait();
        }

    }


    /**
     * Recomputes the order's total price and updates the displayed total.
     *
     * Updates the shared total in SharedData and replaces the price label shown
     * inside prixtotaleFlowPane to reflect the newly computed total.
     */
    private void updatePrixTotal() {
        this.prixTotal = 0.0;
        for (final OrderItem orderItem : this.order.getOrderItems()) {
            this.prixTotal += this.prixProduct(orderItem.getProduct().getId(), orderItem.getQuantity());
        }

        // Mettez à jour le prix total dans SharedData
        SharedData.getInstance().setTotalPrice(this.prixTotal);
        final Label prixTotalLabel = this.createPrixTotalLabel(this.prixTotal);
        this.prixtotaleFlowPane.getChildren().clear();
        this.prixtotaleFlowPane.getChildren().add(prixTotalLabel);
    }


    /**
         * Compute the total price for a product given its ID and quantity.
         *
         * @param idProduct ID of the product.
         * @param quantity  Number of units to price.
         * @return the total price for the specified quantity of the product.
         */
    private double prixProduct(final Long idProduct, final int quantity) {
        final ProductService produitService = new ProductService();
        final double prixUnitaire = produitService.getProductPrice(idProduct);
        return quantity * prixUnitaire;
    }


    /**
         * Open the order screen and pass the controller the current order.
         *
         * @param event the ActionEvent that triggered navigation to the order screen
         */
    @FXML
    void order(final ActionEvent event) {
        final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/produits/OrderClient.fxml"));
        final Stage stage = (Stage) this.cartFlowPane.getScene().getWindow();
        try {
            final Parent root = fxmlLoader.load();
            final OrderClientController controller = fxmlLoader.getController();
            controller.initialize(this.order);
            final Scene scene = new Scene(root);
            stage.setTitle("order");
            stage.setScene(scene);
            stage.show();
        }
 catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Placeholder handler for mouse events related to initiating payment.
     *
     * <p>Currently no action is performed; implement payment UI behavior here when needed.</p>
     *
     * @param event the MouseEvent that triggered this handler
     */
    public void Paiment(final MouseEvent event) {
    }


    /**
     * Opens the product comment UI in a new window and closes the current window.
     *
     * @param event the ActionEvent that triggered this navigation; used to locate the current window to close
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
            ShoppingCartProductControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * Opens the event display UI (AffichageEvenementClient.fxml) in a new window and closes the current window.
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
            ShoppingCartProductControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * Open the product listing view (AfficherProductClient.fxml) in a new window and close the current window.
     *
     * @param event the ActionEvent whose source node identifies the current window to be closed
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
            ShoppingCartProductControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * No-op event handler for the profile client action; reserved for future implementation.
     */
    @FXML
    void profilclient(final ActionEvent event) {
    }


    /**
     * Open the film user view and replace the current window with it.
     *
     * Loads /ui/films/filmuser.fxml, creates a new stage showing that scene, and closes
     * the stage associated with the triggering event.
     *
     * @param event the ActionEvent that triggered navigation; used to obtain the current window
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
            ShoppingCartProductControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * Opens the series view in a new window and closes the current window.
     *
     * @param event the action event whose source is used to locate and close the current window
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
            ShoppingCartProductControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * Open the product display UI in a modal window and close the current window.
     *
     * <p>
     * Loads the product-listing FXML, presents it as an application-modal dialog owned by the current window,
     * and then closes the current stage after the dialog is dismissed.
     * </p>
     *
     * @param mouseEvent the mouse event that triggered this action
     */
    public void afficherProduct(final MouseEvent mouseEvent) {
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
            ShoppingCartProductControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception selon vos
                                                                                        // besoins
        }

    }

}
