package com.esprit.controllers.products;

import com.esprit.models.products.Order;
import com.esprit.models.products.OrderItem;
import com.esprit.models.products.Product;
import com.esprit.models.products.SharedData;
import com.esprit.models.products.ShoppingCart;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * Initializes the controller after its root element has been completely
     * processed.
     * This method loads the shopping cart contents from the database and displays
     * them in the UI.
     *
     * <p>
     * The method uses Platform.runLater to ensure that the UI loading happens on
     * the JavaFX
     * application thread after the FXML has been fully loaded.
     * </p>
     *
     * @param url            The location used to resolve relative paths for the
     *                       root object, or null if the location is not known
     * @param resourceBundle The resources used to localize the root object, or null
     *                       if the root object was not localized
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        Platform.runLater(new Runnable() {
            /**
             * Loads accepted shopping cart items when the UI is ready.
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
        });
    }

    /**
     * Retrieves a list of products from a service, verifies if each product has
     * already been added to the cart, and adds it to the cart if not. It also marks
     * the product as added in a map for future reference.
     */
    private void loadAcceptedShoppingCart() {
        // Vérifier que cartFlowPane est initialisé
        if (this.cartFlowPane == null) {
            LOGGER.log(Level.WARNING, "cartFlowPane is null - cannot load shopping cart");
            return;
        }

        // Récupérer toutes les produits depuis le service
        final ShoppingCartService shoppingcartService = new ShoppingCartService();
        List<ShoppingCart> items = null;
        try {
            // Try to get the scene first
            if (this.cartFlowPane.getScene() == null || this.cartFlowPane.getScene().getWindow() == null) {
                LOGGER.log(Level.WARNING, "Scene or Window is not initialized yet");
                return;
            }

            final Client client = (Client) this.cartFlowPane.getScene().getWindow().getUserData();
            if (client == null) {
                LOGGER.log(Level.WARNING, "Client user data not found");
                return;
            }

            items = shoppingcartService.readUserShoppingCart(client.getId());
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL Error loading shopping cart: " + e.getMessage(), e);
            return;
        } catch (final NullPointerException e) {
            LOGGER.log(Level.WARNING, "NullPointerException loading shopping cart: " + e.getMessage(), e);
            return;
        }

        if (items == null || items.isEmpty()) {
            LOGGER.log(Level.INFO, "No shopping cart items found");
            return;
        }

        for (final ShoppingCart shoppingcart1 : items) {
            // Vérifier si le produit a déjà été ajouté
            if (!this.produitVBoxMap.containsKey(shoppingcart1.getProduct().getId())) {
                final VBox produitVBox = this.createProductVBox(shoppingcart1);
                if (produitVBox != null) {
                    this.cartFlowPane.getChildren().add(produitVBox);
                    // Ajouter le produit à la map pour le marquer comme déjà ajouté
                    this.produitVBoxMap.put(shoppingcart1.getProduct().getId().intValue(), produitVBox);
                }
            }
        }
    }

    /**
     * /** Creates a new Label element with the total price displayed as a double
     * value, using the specified font size, position, and styling options.
     *
     * @param prixTotal total price of the product, which is used to create and set
     *                  the
     *                  label's text value.
     * @returns a label with the price total value displayed in bold font.
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
     * Generates a `VBox` container for each product in the shopping cart, with
     * buttons to decrease or increase the quantity and a label to display the total
     * price. It also provides delete confirmation pop-up for removing items from
     * the cart.
     *
     * @param ShoppingCart shoppingcart object that contains the details of the
     *                     products,
     *                     quantities, and total price, which are used to populate
     *                     the UI
     *                     elements in the `generateProductCard()` function.
     * @returns a VBox container that displays a product's details and allows users
     * to select it for their order.
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
            // Add null check to prevent NPE when accessing product image
            final Product product = ShoppingCart.getProduct();
            if (product != null) {
                final String produitImage = product.getImageUrl();
                if (produitImage != null && !produitImage.isEmpty()) {
                    final Image image = new Image(produitImage);
                    imageView.setImage(image);
                } else {
                    // Utiliser une image par défaut si le Blob est null
                    final Image defaultImage = new Image(this.getClass().getResourceAsStream("defaultImage.png"));
                    imageView.setImage(defaultImage);
                }
            } else {
                // Utiliser une image par défaut si le produit est null
                final Image defaultImage = new Image(this.getClass().getResourceAsStream("defaultImage.png"));
                imageView.setImage(defaultImage);
            }
        } catch (final Exception e) {
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
            } else {
                this.order.getOrderItems().remove(orderItem);
                ShoppingCartProductControllers.LOGGER.info("Product non sélectionné");
                this.updatePrixTotal();
            }
        });
        // Icône de diminution de quantité
        final FontIcon decreaseIcon = new FontIcon("mdi2m-minus");
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
        });
        // Icône d'augmentation de quantité
        final FontIcon increaseIcon = new FontIcon("mdi2p-plus");
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
        });
        final FontIcon deleteIcon = new FontIcon("mdi2t-trash-can");
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
        });
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
     * Decreases the quantity of items in a shopping cart by one unit when the user
     * types a negative value into a text field. The updated quantity is then saved
     * in the shoppingcart object and reflected in the cart's total quantity.
     *
     * @param quantityTextField quantity to be decreased, which is obtained from the
     *                          text field of
     *                          the same name.
     * @param shoppingcart      ShoppingCart object whose quantity is being updated
     *                          by the
     *                          function.
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
     * Compares the requested quantity with the available quantity of stock for a
     * given product and returns true if there is enough stock, otherwise false.
     *
     * @param produit  product for which the availability of stock is being checked.
     * @param quantity amount of units of the product that are required or desired
     *                 by the
     *                 user, which is compared with the available stock quantity to
     *                 determine if the product is available for purchase.
     * @returns a boolean value indicating whether the requested quantity of stock
     * is available or not.
     */
    private boolean isStockAvailable(final Product produit, final int quantity) {
        // Comparer la quantité demandée avec la quantité disponible en stock
        return produit.getStockQuantity() >= quantity;
    }

    /**
     * Increases the quantity of an item in a shopping cart by 1, checking if the
     * stock is available and displaying an alert if it's not.
     *
     * @param quantityTextField quantity of the product to be updated in the
     *                          shoppingcart, as
     *                          indicated by its name.
     * @param shoppingcart      containing the products that the user wishes to
     *                          increase the
     *                          quantity of.
     */
    private void increaseQuantity(final TextField quantityTextField, final ShoppingCart shoppingcart) {
        final int currentQuantity = Integer.parseInt(quantityTextField.getText());
        // Vérifier si le stock est disponible
        if (this.isStockAvailable(shoppingcart.getProduct(), currentQuantity + 1)) {
            quantityTextField.setText(String.valueOf(currentQuantity + 1));
            shoppingcart.setQuantity(shoppingcart.getQuantity() + 1);
        } else {
            // Afficher une alerte en cas de stock insuffisant
            final Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Stock Insuffisant");
            alert.setHeaderText("La quantité demandée dépasse la quantité en stock.");
            alert.setContentText("Veuillez réduire la quantité demandée.");
            alert.showAndWait();
        }
    }

    /**
     * Updates the total price label based on the items in a given order by
     * multiplying the product prices by their quantities and storing the result in
     * a shared data instance, then adding it to the flow pane with a created label.
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
     * Calculates the total price of a product based on its ID and quantity by
     * multiplying the unitaire price fetched from the `ProductService`.
     *
     * @param idProduct ID of the product for which the price is being calculated.
     * @param quantity  number of units of the product to be priced, which is
     *                  multiplied
     *                  by the unit price returned by the `ProductService` to
     *                  compute the
     *                  total price.
     * @returns the total price of a product in units of quantity.
     */
    private double prixProduct(final Long idProduct, final int quantity) {
        final ProductService produitService = new ProductService();
        final double prixUnitaire = produitService.getProductPrice(idProduct);
        return quantity * prixUnitaire;
    }

    /**
     * Loads a FXML file named `/OrderClient.fxml` into a Stage, initializes a
     * `OrderClientController`, and displays the scene on the Stage.
     *
     * @param event order action event that triggered the function, providing the
     *              necessary context for the code to operate properly.
     */
    @FXML
    void order(final ActionEvent event) {
        final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/products/CommandeClient.fxml"));
        final Stage stage = (Stage) this.cartFlowPane.getScene().getWindow();
        try {
            final Parent root = fxmlLoader.load();
            final OrderClientController controller = fxmlLoader.getController();
            controller.initialize(this.order);
            final Scene scene = new Scene(root);
            stage.setTitle("order");
            stage.setScene(scene);
            stage.show();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Is a handling function for mouse events. It does not perform any specific
     * action or have any distinctive features beyond processing mouse input.
     *
     * @param event mouse event that triggered the execution of the `Paiment()`
     *              function.
     */
    public void Paiment(final MouseEvent event) {
    }

    /**
     * Loads a new user interface, creates a new stage and attaches it to the
     * existing stage, replacing the original interface, and finally closes the
     * original stage.
     *
     * @param event ActionEvent object that triggered the `cinemaclient` method,
     *              providing the necessary information to update the FXML layout of
     *              the stage.
     */
    @FXML
    void cinemaclient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(
                this.getClass().getResource("/ui/products/CommentaireProduit.fxml"));
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
            ShoppingCartProductControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
            // d'entrée/sortie
        }
    }

    /**
     * Loads a new UI fragment (`AffichageEvenementClient.fxml`) and replaces the
     * current scene with it, creating a new stage and closing the original one.
     *
     * @param event event object that triggered the function, providing information
     *              about the event, such as its source and details, which can be
     *              used
     *              to handle the event appropriately.
     */
    @FXML
    void eventClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(
                this.getClass().getResource("/ui/AffichageEvenementClient.fxml"));
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
            ShoppingCartProductControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
            // d'entrée/sortie
        }
    }

    /**
     * Loads a new FXML interface, creates a new scene and stage, and replaces the
     * current stage with the new one, closing the old stage upon execution.
     *
     * @param event ActionEvent that triggers the function and provides access to
     *              information about the action that was performed, such as the
     *              source of the event and the stage where the action occurred.
     */
    @FXML
    void produitClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(
                this.getClass().getResource("/ui/products/AfficherProduitClient.fxml"));
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
            ShoppingCartProductControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
            // d'entrée/sortie
        }
    }

    /**
     * Is expected to perform some actions or calculations upon receiving an event
     * call.
     *
     * @param event triggered event that initiated the call to the `profilclient`
     *              function.
     */
    @FXML
    void profilclient(final ActionEvent event) {
    }

    /**
     * Loads a new FXML interface using `FXMLLoader`, creates a new scene and stage,
     * and replaces the current stage with the new one, closing the previous stage.
     *
     * @param event ActionEvent object that triggered the function execution,
     *              providing access to information about the event such as its
     *              source
     *              and target.
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
            ShoppingCartProductControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
            // d'entrée/sortie
        }
    }

    /**
     * Loads a new FXML file, creates a new scene, and attaches it to a new stage.
     * It also closes the current stage and shows the new stage.
     *
     * @param event ActionEvent that triggers the `SerieClient()` method and
     *              provides
     *              information about the source of the event.
     */
    @FXML
    void SerieClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/Series-view.fxml"));
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
        } catch (final IOException e) {
            ShoppingCartProductControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
            // d'entrée/sortie
        }
    }

    /**
     * Returns to the product display interface.
     *
     * <p>
     * Loads the AfficherProductClient.fxml file, creates a new scene and stage,
     * and displays it modally while closing the current stage.
     * </p>
     *
     * @param mouseEvent The mouse event that triggered this method
     */
    public void afficherProduct(final MouseEvent mouseEvent) {
        // Obtenir la fenêtre précédente
        final Window previousWindow = this.retour.getScene().getWindow();
        // Charger le fichier FXML de la page "/ui/products/AfficherProduitClient.fxml"
        final FXMLLoader fxmlLoader = new FXMLLoader(
            this.getClass().getResource("/ui/products/AfficherProduitClient.fxml"));
        try {
            final Parent rootNode = fxmlLoader.load();
            final Scene scene = new Scene(rootNode);
            // Créer une nouvelle fenêtre pour la page "/ui/AfficherProduct.fxml"
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
        } catch (final Exception e) {
            ShoppingCartProductControllers.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception selon vos
            // besoins
        }
    }
}
