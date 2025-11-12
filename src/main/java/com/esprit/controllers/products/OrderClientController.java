package com.esprit.controllers.products;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.esprit.models.products.Order;
import com.esprit.models.products.OrderItem;
import com.esprit.models.products.Product;
import com.esprit.models.products.SharedData;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.products.OrderItemService;
import com.esprit.services.products.OrderService;
import com.esprit.services.products.ProductService;
import com.esprit.services.users.UserService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Controller class for handling order processing in the RAKCHA application.
 * This controller manages the order checkout process, including address input,
 * payment processing via PayPal, and order finalization.
 * 
 * <p>
 * The controller validates user inputs, creates order records in the database,
 * updates product inventory, and handles the PayPal payment flow through their
 * API.
 * </p>
 * 
 * <p>
 * It also provides navigation capabilities to other parts of the application.
 * </p>
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class OrderClientController implements Initializable {
    private static final String CLIENT_ID = System.getenv("PAYPAL_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("PAYPAL_CLIENT_SECRET");
    private static final String SUCCESS_URL = OrderClientController.class.getResource("/success.html").toExternalForm();
    private static final String CANCEL_URL = OrderClientController.class.getResource("/cancel.html").toExternalForm();
    private static final Logger LOGGER = Logger.getLogger(OrderClientController.class.getName());
    private final OrderService orderService = new OrderService();
    private final UserService usersService = new UserService();
    Order order = new Order();
    double totalPrix = SharedData.getInstance().getTotalPrice();
    @FXML
    private TextField adresseTextField;
    @FXML
    private TextField numTelephoneTextField;
    @FXML
    private AnchorPane paimenet;
    private User connectedUser;
    @FXML
    private FlowPane prixtotaleFlowPane;
    @FXML
    private Button idpaymentenligne;

    /**
     * Sets up the order details and user interface for the selected order.
     * 
     * <p>
     * This method stores the selected order, retrieves the total price from
     * SharedData,
     * and creates a label to display the total price in the UI.
     * </p>
     *
     * @param orderselectionner The selected order to be processed
     */
    @FXML
    void initialize(final Order orderselectionner) {
        this.order = orderselectionner;
        Platform.runLater(new Runnable() {
            /**
             * Retrieves connected user information when the UI is ready.
             */
            @Override
            public void run() {
                connectedUser = usersService.getUserById(4L);
                OrderClientController.LOGGER
                        .info("User connected: " + connectedUser.getEmail());
            }

        }
);
        // Récupérer le prix total depuis SharedData et créer le Label correspondant
        final Label prixTotalLabel = this.createPrixTotalLabel(this.totalPrix);
        // Ajouter le Label au FlowPane
        this.prixtotaleFlowPane.getChildren().add(prixTotalLabel);
    }


    /**
     * Initializes the controller after its root element has been completely
     * processed.
     * 
     * <p>
     * This is a standard initialization method that is called automatically when
     * the
     * FXML file is loaded. It can be used to perform any necessary setup
     * operations.
     * </p>
     *
     * @param url            The location used to resolve relative paths for the
     *                       root object, or null if the location is not known
     * @param resourceBundle The resources used to localize the root object, or null
     *                       if the root object was not localized
     */
    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
    }


    /**
     * Creates a styled label to display the total price.
     * 
     * <p>
     * This method formats the price with the appropriate currency symbol
     * and applies styling to make it stand out in the UI.
     * </p>
     *
     * @param prixTotal The total price to display
     * @return A styled Label component showing the total price
     */
    private Label createPrixTotalLabel(final double prixTotal) {
        final Label prixTotalLabel = new Label(prixTotal + " DT");
        prixTotalLabel.setFont(Font.font("Verdana", 25));
        prixTotalLabel.setStyle("-fx-text-fill: #d72222;");
        return prixTotalLabel;
    }


    /**
     * Processes the order when the user clicks the order button.
     * 
     * <p>
     * This method:
     * 1. Validates the phone number and address
     * 2. Creates the order with customer details
     * 3. Saves the order to the database
     * 4. Updates inventory for each ordered item
     * 5. Makes the payment section visible
     * </p>
     *
     * @param event The action event that triggered this method
     */
    @FXML
    void order(final ActionEvent event) {
        final OrderItemService orderItemService = new OrderItemService();
        // Validation du numéro de téléphone
        final String numTelephone = this.numTelephoneTextField.getText();
        if (!this.isValidPhoneNumber(numTelephone)) {
            this.showAlert("Numéro de téléphone invalide",
                    "Veuillez entrer un numéro de téléphone valide (8 chiffres).");
            return;
        }

        // Validation de l'adresse
        final String adresse = this.adresseTextField.getText();
        if (adresse.isEmpty()) {
            this.showAlert("Adresse invalide", "Veuillez entrer une adresse valide.");
            return;
        }

        this.order.setAddress(this.adresseTextField.getText());
        this.order.setPhoneNumber(Integer.parseInt(this.numTelephoneTextField.getText()));
        this.order.setClient((Client) this.connectedUser);
        final LocalDate date1 = LocalDate.now();
        this.order.setOrderDate(Date.valueOf(date1));
        try {
            this.orderService.create(this.order);
            this.order.setStatus("en cours");
        }
 catch (final Exception e) {
            OrderClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        for (final OrderItem orderItem : this.order.getOrderItems()) {
            orderItem.setOrder(this.order);
            orderItemService.create(orderItem);
            // Décrémenter le stock
            this.decrementStock(orderItem.getProduct(), orderItem.getQuantity());
        }

        this.paimenet.setVisible(true);
    }


    /**
     * Displays an alert dialog with the specified title and content.
     * 
     * <p>
     * This method is used to show error messages to the user when input validation
     * fails.
     * </p>
     *
     * @param title   The title of the alert dialog
     * @param content The message content to display in the alert
     */
    private void showAlert(final String title, final String content) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    /**
     * Validates if a given string represents a valid phone number.
     * 
     * <p>
     * A valid phone number must consist of exactly 8 digits.
     * </p>
     *
     * @param phoneNumber The phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    private boolean isValidPhoneNumber(final String phoneNumber) {
        // Vérifier si le numéro de téléphone a exactement 8 chiffres et ne contient que
        // des chiffres
        return phoneNumber.matches("\\d{8}
");
    }


    /**
     * Decrements the stock quantity of a product after an order is placed.
     * 
     * <p>
     * This method updates the product's quantity in the database to reflect
     * that items have been purchased.
     * </p>
     *
     * @param produit  The product whose stock needs to be updated
     * @param quantity The quantity to decrement from the stock
     */
    private void decrementStock(final Product produit, final int quantity) {
        // Décrémenter le stock dans la base de données
        produit.setQuantity(produit.getQuantity() - quantity);
        final ProductService produitService = new ProductService();
        produitService.update(produit); // Assurez-vous que votre service de produit dispose d'une méthode de mise à
        // jour
    }


    /**
     * Navigates to the shopping cart interface.
     * 
     * <p>
     * Loads the ShoppingCartProduct.fxml file, creates a new scene and stage,
     * and replaces the current stage with the new one.
     * </p>
     *
     * @param event The mouse event that triggered this method
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
            stage.setTitle("ggggggShoppingCartProduct");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        }
 catch (final IOException e) {
            OrderClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }

    }


    /**
     * Initiates the PayPal payment process.
     * 
     * <p>
     * This method:
     * 1. Creates a PayPal API context
     * 2. Sets up the payment amount and details
     * 3. Configures redirect URLs for success and cancel scenarios
     * 4. Creates the payment and redirects the user to PayPal for approval
     * </p>
     *
     * @param event The action event that triggered this method
     */
    @FXML
    void payment(final ActionEvent event) {
        final APIContext apiContext = new APIContext(OrderClientController.CLIENT_ID,
                OrderClientController.CLIENT_SECRET, "sandbox");
        final Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.valueOf(this.totalPrix)); // totalPrix should be set to the total price of the order
        final Transaction transaction = new Transaction();
        transaction.setDescription("Your Purchase Description");
        transaction.setAmount(amount);
        final List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        final Payer payer = new Payer();
        payer.setPaymentMethod("paypal");
        final Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        final RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost/cancel");
        redirectUrls.setReturnUrl("http://localhost/success");
        payment.setRedirectUrls(redirectUrls);
        payment.setRedirectUrls(redirectUrls);
        try {
            final Payment createdPayment = payment.create(apiContext);
            OrderClientController.LOGGER.info("Created payment with id = " + createdPayment.getId() + " and status = "
                    + createdPayment.getState());
            // Extract approval URL
            String approvalUrl = null;
            final List<Links> links = createdPayment.getLinks();
            for (final Links link : links) {
                if ("approval_url".equalsIgnoreCase(link.getRel())) {
                    approvalUrl = link.getHref();
                    break;
                }

            }

            if (null != approvalUrl) {
                // Redirect to PayPal for payment approval
                this.redirectToPayPal(approvalUrl);
            }

        }
 catch (final PayPalRESTException e) {
            OrderClientController.LOGGER.log(Level.SEVERE, "Error creating PayPal payment", e);
        }

    }


    /**
     * Redirects the user to PayPal for payment approval.
     * 
     * <p>
     * This method opens a WebView with the PayPal approval URL and monitors
     * the URL changes to detect when the payment process is complete.
     * </p>
     *
     * @param approvalLink The PayPal approval URL
     */
    private void redirectToPayPal(final String approvalLink) {
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle("PayPal Payment");
            final WebView webView = new WebView();
            webView.getEngine().load(approvalLink);
            // Listen for URL changes to detect when payment is approved or canceled
            webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.startsWith("http://localhost/success")) {
                    // Payment was approved
                    OrderClientController.LOGGER.info("Payment approved. URL: " + newValue);
                    final String paymentId = this.extractQueryParameter(newValue, "paymentId");
                    final String payerId = this.extractQueryParameter(newValue, "PayerID");
                    if (null != paymentId && null != payerId) {
                        this.completePayment(paymentId, payerId);
                    }

                    webView.getEngine().loadContent(OrderClientController.SUCCESS_URL);
                    // Close the window after a delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000);
                            Platform.runLater(stage::close);
                        }
 catch (final InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                    }
).start();
                }
 else if (newValue.startsWith("http://localhost/cancel")) {
                    // Payment was canceled
                    OrderClientController.LOGGER.info("Payment canceled");
                    webView.getEngine().loadContent(OrderClientController.CANCEL_URL);
                    // Close the window after a delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000);
                            Platform.runLater(stage::close);
                        }
 catch (final InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                    }
).start();
                }

            }
);
            final Scene scene = new Scene(webView, 1024, 768);
            stage.setScene(scene);
            stage.show();
        }
);
    }


    /**
     * Extracts a query parameter from a URL.
     * 
     * <p>
     * This method parses the URL query string to find a specific parameter value.
     * </p>
     *
     * @param url           The URL containing query parameters
     * @param parameterName The name of the parameter to extract
     * @return The value of the parameter, or null if not found
     */
    private String extractQueryParameter(final String url, final String parameterName) {
        try {
            final List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), StandardCharsets.UTF_8);
            for (final NameValuePair param : params) {
                if (param.getName().equals(parameterName)) {
                    return param.getValue();
                }

            }

        }
 catch (final URISyntaxException e) {
            OrderClientController.LOGGER.log(Level.SEVERE, "Error parsing URL", e);
        }

        return null;
    }


    /**
     * Completes the payment process after PayPal approval.
     * 
     * <p>
     * This method executes the payment using the PayPal API and updates
     * the order status in the database based on the payment result.
     * </p>
     *
     * @param paymentId The PayPal payment ID
     * @param payerId   The PayPal payer ID
     */
    private void completePayment(final String paymentId, final String payerId) {
        final APIContext apiContext = new APIContext(OrderClientController.CLIENT_ID,
                OrderClientController.CLIENT_SECRET, "sandbox");
        final Payment payment = new Payment();
        payment.setId(paymentId);
        final PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        try {
            final Payment executedPayment = payment.execute(apiContext, paymentExecution);
            OrderClientController.LOGGER.info("Payment executed. Status: " + executedPayment.getState());
            if ("approved".equalsIgnoreCase(executedPayment.getState())) {
                // Update order status to paid
                this.order.setStatus("payee");
                this.orderService.update(this.order);
                OrderClientController.LOGGER.info("Order status updated to paid");
            }

        }
 catch (final PayPalRESTException e) {
            OrderClientController.LOGGER.log(Level.SEVERE, "Error executing payment", e);
        }

    }


    /**
     * Navigates to the cinema client interface.
     * 
     * <p>
     * Loads the CommentaireProduct.fxml file, creates a new scene and stage,
     * and replaces the current stage with the new one.
     * </p>
     *
     * @param event The action event that triggered this method
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
            OrderClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }

    }


    /**
     * Navigates to the event client interface.
     * 
     * <p>
     * Loads the AffichageEvenementClient.fxml file, creates a new scene and stage,
     * and replaces the current stage with the new one.
     * </p>
     *
     * @param event The action event that triggered this method
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
            OrderClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }

    }


    /**
     * Navigates to the product client interface.
     * 
     * <p>
     * Loads the AfficherProductClient.fxml file, creates a new scene and stage,
     * and replaces the current stage with the new one.
     * </p>
     *
     * @param event The action event that triggered this method
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
            OrderClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }

    }


    /**
     * Placeholder for client profile functionality.
     * 
     * @param event The action event that triggered this method
     */
    @FXML
    void profilclient(final ActionEvent event) {
    }


    /**
     * Placeholder for showing cinema functionality.
     * 
     * @param event The action event that triggered this method
     */
    @FXML
    void showcinema(final ActionEvent event) {
    }


    /**
     * Placeholder for showing event functionality.
     * 
     * @param event The action event that triggered this method
     */
    @FXML
    void showevenement(final ActionEvent event) {
    }


    /**
     * Placeholder for showing product functionality.
     * 
     * @param event The action event that triggered this method
     */
    @FXML
    void showproduit(final ActionEvent event) {
    }


    /**
     * Navigates to the movie client interface.
     * 
     * <p>
     * Loads the filmuser.fxml file, creates a new scene and stage,
     * and replaces the current stage with the new one.
     * </p>
     *
     * @param event The action event that triggered this method
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
            OrderClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }

    }


    /**
     * Navigates to the series client interface.
     * 
     * <p>
     * Loads the Series-view.fxml file, creates a new scene and stage,
     * and replaces the current stage with the new one.
     * </p>
     *
     * @param event The action event that triggered this method
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
            OrderClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }

    }

}

