package com.esprit.controllers.produits;

import com.esprit.models.produits.Commande;
import com.esprit.models.produits.CommandeItem;
import com.esprit.models.produits.Produit;
import com.esprit.models.produits.SharedData;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.produits.CommandeItemService;
import com.esprit.services.produits.CommandeService;
import com.esprit.services.produits.ProduitService;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Is responsible for handling user commands related to the cinema and event
 * modules.
 * It provides an interface for loading new fxml files based on user actions,
 * such
 * as showing a movie or an event. The controller also handles closing the
 * current
 * stage and displaying a new one. Additionally, it includes methods for
 * handling
 * Movie and Serie client interactions.
 */
public class CommandeClientController implements Initializable {
    private static final String CLIENT_ID = System.getenv("PAYPAL_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("PAYPAL_CLIENT_SECRET");
    private static final String SUCCESS_URL = CommandeClientController.class.getResource("/success.html")
            .toExternalForm();
    private static final String CANCEL_URL = CommandeClientController.class.getResource("/cancel.html")
            .toExternalForm();
    private static final Logger LOGGER = Logger.getLogger(CommandeClientController.class.getName());
    private final CommandeService commandeService = new CommandeService();
    private final UserService usersService = new UserService();
    Commande commande = new Commande();
    double totalPrix = SharedData.getInstance().getTotalPrix();
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
     * Sets the value of a `Commande` object and runs a lambda action to retrieve
     * and
     * print the user's name, then creates a label with the total price and adds it
     * to a
     * `FlowPane`.
     *
     * @param commandeselectionner selected command that triggers the function to
     *                             execute,
     *                             which is used to retrieve the total price from
     *                             SharedData and create a label
     *                             displaying the result.
     *                             <p>
     *                             `commandeselectionner`: An instance of
     *                             `Commande`, representing the selected command.
     */
    @FXML
    void initialize(final Commande commandeselectionner) {
        this.commande = commandeselectionner;
        Platform.runLater(
                new Runnable() {
                    /**
                     * Retrieves and prints the connected user's data to the console.
                     */
                    @Override
                    public void run() {
                        CommandeClientController.this.connectedUser = (Client) CommandeClientController.this.prixtotaleFlowPane
                                .getScene().getWindow().getUserData();
                        CommandeClientController.LOGGER
                                .info("produits:    user:  " + CommandeClientController.this.connectedUser);
                    }
                });
        // Récupérer le prix total depuis SharedData et créer le Label correspondant
        final Label prixTotalLabel = this.createPrixTotalLabel(this.totalPrix);
        // Ajouter le Label au FlowPane
        this.prixtotaleFlowPane.getChildren().add(prixTotalLabel);
    }

    /**
     * Is called when an URL and a resource bundle are provided to initialize the
     * application. It sets up the resources and prepares the application for usage.
     *
     * @param url            URL of the web application being initialized by the
     *                       `initialize()` method.
     * @param resourceBundle collection of translated keys and their corresponding
     *                       values
     */
    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
    }

    /**
     * Creates a new `Label` component with a double value as its text and sets
     * various
     * styling properties to display the number in a specific format.
     *
     * @param prixTotal total price of an item, which is used to create a label with
     *                  the
     *                  value displayed.
     * @returns a label displaying the price total in bold font with a specific
     *          color.
     *          <p>
     *          - The output is a `Label` object named `prixTotalLabel`.
     *          - The label's text value is the sum of `prixTotal` and "DT".
     *          - The font of the label is set to Verdana with a size of 25.
     *          - The text fill color of the label is set to #d72222.
     */
    private Label createPrixTotalLabel(final double prixTotal) {
        final Label prixTotalLabel = new Label(prixTotal + " DT");
        prixTotalLabel.setFont(Font.font("Verdana", 25));
        prixTotalLabel.setStyle("-fx-text-fill: #d72222;");
        return prixTotalLabel;
    }

    /**
     * Validates input fields, creates a new command object, and calls a service
     * method
     * to create the command in the database. It then iterates through each item in
     * the
     * command and creates a new item object, updates the inventory of the product,
     * and
     * sets the payment line visible.
     *
     * @param event action event triggered by clicking on the "Order" button, which
     *              initiates the processing of the command and calls the various
     *              methods to create
     *              and update objects and database records.
     *              <p>
     *              - `event`: an ActionEvent object representing the user's action
     *              of clicking on
     *              the "Order" button.
     */
    @FXML
    void order(final ActionEvent event) {
        final CommandeItemService commandeItemService = new CommandeItemService();
        int idcommande = 0;
        // Validation du numéro de téléphone
        final String numTelephone = this.numTelephoneTextField.getText();
        if (!this.isValidPhoneNumber(numTelephone)) {
            this.showAlert("Erreur de validation", "Numéro de téléphone invalide.");
            return; // Arrêter le traitement de la commande
        }
        // Validation de l'adresse
        final String adresse = this.adresseTextField.getText();
        if (adresse.isEmpty()) {
            this.showAlert("Erreur de validation", "L'adresse ne peut pas être vide.");
            return; // Arrêter le traitement de la commande
        }
        this.commande.setAdresse(this.adresseTextField.getText());
        this.commande.setNum_telephone(Integer.parseInt(this.numTelephoneTextField.getText()));
        this.commande.setIdClient((Client) this.connectedUser);
        final LocalDate date1 = LocalDate.now();
        this.commande.setDateCommande(Date.valueOf(date1));
        try {
            idcommande = this.commandeService.createcommande(this.commande);
            this.commande.setIdCommande(idcommande);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        for (final CommandeItem commandeItem : this.commande.getCommandeItem()) {
            CommandeClientController.LOGGER.info(String.valueOf(this.commande.getIdCommande()));
            commandeItem.setCommande(this.commande);
            commandeItemService.create(commandeItem);
            this.decrementStock(commandeItem.getProduit(), commandeItem.getQuantity());
            this.idpaymentenligne.setVisible(true);
        }
    }

    /**
     * Creates an `Alert` object with specified title and content, then displays it
     * using
     * the `showAndWait()` method.
     *
     * @param title   title of an alert message displayed by the `showAlert()`
     *                method.
     * @param content text to be displayed in the alert window.
     */
    private void showAlert(final String title, final String content) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Verifies if a given string represents a valid phone number with exactly 8
     * digits
     * and only containing numbers.
     *
     * @param phoneNumber 8-digit phone number to be validated for its correct
     *                    format,
     *                    consisting only of digits.
     * @returns a boolean value indicating whether the provided string represents a
     *          valid
     *          phone number.
     */
    private boolean isValidPhoneNumber(final String phoneNumber) {
        // Vérifier si le numéro de téléphone a exactement 8 chiffres et ne contient que
        // des chiffres
        return phoneNumber.matches("\\d{8}");
    }

    /**
     * Decrements the stock of a product in the database by subtracting the
     * specified
     * quantity from the product's current quantity, and updates the product record
     * in
     * the database using an instance of `ProduitService`.
     *
     * @param produit  product object whose stock is being decremented.
     *                 <p>
     *                 - `produit`: A `Produit` object representing a product.
     *                 - `quantity`: An integer representing the quantity of the
     *                 product to be decremented.
     * @param quantity amount of stock to be decremented from the product's current
     *                 quantity in the database.
     */
    private void decrementStock(final Produit produit, final int quantity) {
        // Décrémenter le stock dans la base de données
        produit.setQuantiteP(produit.getQuantiteP() - quantity);
        final ProduitService produitService = new ProduitService();
        produitService.update(produit); // Assurez-vous que votre service de produit dispose d'une méthode de mise à
        // jour
    }

    /**
     * Loads a new UI scene from an FXML file, creates a new stage with the scene,
     * and
     * replaces the current stage with the new one, closing the original stage.
     *
     * @param event MouseEvent object that triggered the function, providing
     *              information
     *              about the mouse button pressed, the screen position of the
     *              click, and other details.
     *              <p>
     *              - Type: `MouseEvent` indicating that the event was triggered by
     *              a mouse action.
     *              - Source: The object that generated the event, which is
     *              typically a button or
     *              link in the user interface.
     *              - Event type: The specific type of event, such as
     *              `MOUSE_CLICKED` or `MOUSE_OVER`.
     */
    @FXML
    void panier(final MouseEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/produits/PanierProduit.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Panier des produits");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (final IOException e) {
            CommandeClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }

    /**
     * Creates a PayPal payment request and redirects the user to PayPal for
     * approval.
     * It retrieves the payment details and redirects the user to the appropriate
     * URL
     * based on the payment method.
     *
     * @param event event that triggered the `payment()` method and provides the
     *              necessary
     *              context for the payment creation process.
     *              <p>
     *              - `event`: An ActionEvent object representing a payment action
     *              triggered by the
     *              user.
     */
    @FXML
    void payment(final ActionEvent event) {
        final APIContext apiContext = new APIContext(CommandeClientController.CLIENT_ID,
                CommandeClientController.CLIENT_SECRET, "sandbox");
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
            for (final Links link : createdPayment.getLinks()) {
                if ("approval_url".equalsIgnoreCase(link.getRel())) {
                    CommandeClientController.LOGGER.info(link.getHref());
                    this.redirectToPayPal(link.getHref());
                    break;
                }
            }
        } catch (final PayPalRESTException e) {
            CommandeClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            this.showAlert("Error", "Could not initiate PayPal payment.");
        }
    }

    /**
     * Redirect users to PayPal's redirection page for successful payments, passing
     * payment
     * ID and payer ID as query parameters.
     *
     * @param approvalLink URL of the PayPal approval page, which is loaded into a
     *                     WebView
     *                     component and monitored for the successful redirection to
     *                     a predefined URL indicating
     *                     the completion of the payment process.
     */
    private void redirectToPayPal(final String approvalLink) {
        Platform.runLater(() -> {
            final Dialog<Void> dialog = new Dialog<>();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UNDECORATED);
            final WebView webView = new WebView();
            webView.getEngine().load(approvalLink);
            // Add a progress bar to the web view
            final ProgressBar progressBar = new ProgressBar();
            webView.getEngine().getLoadWorker().progressProperty().addListener((obs, oldProgress, newProgress) -> {
                progressBar.setProgress(newProgress.doubleValue());
            });
            // Create a close button
            final Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> {
                CommandeClientController.LOGGER.info("Close button clicked");
                dialog.hide();
            });
            final VBox vBox = new VBox(5);
            vBox.getChildren().addAll(webView, progressBar, closeButton);
            // Handle successful redirection
            webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.contains("http://localhost/success")) {
                    final String paymentId = this.extractQueryParameter(newValue, "paymentId");
                    final String payerId = this.extractQueryParameter(newValue, "PayerID");
                    final URL successUrl = this.getClass().getResource("/success.html");
                    webView.getEngine().load(successUrl.toExternalForm());
                    this.completePayment(paymentId, payerId);
                }
            });
            dialog.getDialogPane().setContent(vBox);
            dialog.getDialogPane().setPrefSize(800, 600);
            dialog.showAndWait();
        });
    }

    /**
     * Parses a URL's query parameters and returns the value of a specified
     * parameter
     * name if it exists, or `null` otherwise.
     *
     * @param url           URL to be analyzed and is used to extract the query
     *                      parameter value
     *                      using `URLEncodedUtils.parse()`.
     * @param parameterName name of the query parameter to be extracted from the
     *                      URL.
     * @returns a string representing the value of a query parameter found in a URL.
     */
    private String extractQueryParameter(final String url, final String parameterName) {
        try {
            final List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), StandardCharsets.UTF_8);
            for (final NameValuePair param : params) {
                if (param.getName().equals(parameterName)) {
                    return param.getValue();
                }
            }
        } catch (final URISyntaxException e) {
            CommandeClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null; // Parameter not found
    }

    /**
     * Executes a PayPal payment using the `execute()` method, checking the payment
     * state
     * and updating the commande status accordingly.
     *
     * @param paymentId unique identifier of the payment to be executed.
     * @param payerId   identifier of the payer in the PayPal payment execution,
     *                  which is
     *                  used to identify the user making the payment.
     */
    private void completePayment(final String paymentId, final String payerId) {
        final APIContext apiContext = new APIContext(CommandeClientController.CLIENT_ID,
                CommandeClientController.CLIENT_SECRET, "sandbox");
        final Payment payment = new Payment();
        payment.setId(paymentId);
        final PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        try {
            final Payment executedPayment = payment.execute(apiContext, paymentExecution);
            if ("approved".equals(executedPayment.getState())) {
                this.commande.setStatu("Payee");
                this.commandeService.update(this.commande);
            }
        } catch (final PayPalRESTException e) {
            CommandeClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Charges a new `FXML` file, creates a new scene, and attaches it to a new
     * stage,
     * replacing the current stage.
     *
     * @param event ActionEvent that triggered the function execution and provides
     *              access
     *              to information about the event, such as the source of the event.
     *              <p>
     *              - It is an `ActionEvent`, which means it carries information
     *              about the action
     *              that triggered the function.
     *              - The source of the event is a `Node`, which represents the
     *              element in the user
     *              interface that triggered the event.
     */
    @FXML
    void cinemaclient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/produits/CommentaireProduit.fxml"));
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
            CommandeClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }

    /**
     * Charges a new FXML file (`AffichageEvenementClient.fxml`) and creates a new
     * scene,
     * then attaches it to an existing stage and replaces the current stage with the
     * new
     * one, closing the original stage afterward.
     *
     * @param event ActionEvent object that triggered the event handler method,
     *              providing
     *              information about the action that was performed, such as the
     *              source of the event
     *              and the type of event.
     *              <p>
     *              - It is an instance of `ActionEvent`, which indicates that the
     *              event was triggered
     *              by a user action (e.g., clicking a button).
     */
    @FXML
    void eventClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui//ui/AffichageEvenementClient.fxml"));
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
            CommandeClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }

    /**
     * Loads a new FXML interface using `FXMLLoader`, creates a new scene with it,
     * and
     * attaches it to a new stage. It then closes the current stage to display the
     * new one.
     *
     * @param event ActionEvent object that triggered the function execution,
     *              providing
     *              the source of the event and allowing for handling the
     *              corresponding action.
     *              <p>
     *              - `event`: an ActionEvent object representing a user action
     *              (e.g., mouse click
     *              or key press) that triggered the function execution.
     */
    @FXML
    void produitClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/produits/AfficherProduitClient.fxml"));
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
            CommandeClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }

    /**
     * Likely profiles a client-side application using Java's built-in profiling
     * tools
     * to gather performance data on various aspects of the application, such as CPU
     * usage, memory allocation, and method execution time.
     *
     * @param event occurrence of an action event that triggered the function
     *              execution.
     */
    @FXML
    void profilclient(final ActionEvent event) {
    }

    /**
     * Is triggered when the user clicks a button and displays a cinema-related
     * interface.
     *
     * @param event occurrence of a button click event that triggered the
     *              `showcinema()`
     *              method invocation.
     */
    @FXML
    void showcinema(final ActionEvent event) {
    }

    /**
     * Is called when the `showEvenement` action is triggered and performs an
     * unspecified
     * action.
     *
     * @param event AnimationEvent object that triggered the execution of the
     *              `showEvenement`
     *              method.
     */
    @FXML
    void showevenement(final ActionEvent event) {
    }

    /**
     * Loads a new UI scene from an FXML file, creates a new stage with the scene,
     * and
     * replaces the current stage with the new one, closing the original stage.
     *
     * @param event ActionEvent object that triggered the showmovie method,
     *              providing
     *              information about the source of the event and allowing the
     *              method to handle the
     *              appropriate action accordingly.
     *              <p>
     *              - It is an instance of `ActionEvent`, which represents an action
     *              event occurred
     *              in the JavaFX application.
     *              - The source of the event is a `Node`, which can be any element
     *              in the FXML
     *              document, such as a button or a label.
     */
    @FXML
    void showmovie(final ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
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
            CommandeClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }

    /**
     * Is triggered by an ActionEvent and performs unspecified action related to
     * showing
     * a product.
     *
     * @param event occurrence of an action, triggered by the user, that calls the
     *              `showproduit()` method.
     */
    @FXML
    void showproduit(final ActionEvent event) {
    }

    /**
     * Loads a new FXML file, creates a new scene and stage, and replaces the
     * current
     * stage with the new one.
     *
     * @param event ActionEvent that triggered the function, providing the source of
     *              the
     *              event and allowing for proper handling of the action taken.
     *              <p>
     *              - `event` represents an ActionEvent object containing
     *              information about the action
     *              that triggered the function's execution.
     */
    @FXML
    void showserie(final ActionEvent event) throws IOException {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
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
            CommandeClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }

    /**
     * Loads a new FXML interface, creates a new scene with it, and attaches the
     * scene
     * to a new stage. It also closes the current stage.
     *
     * @param event event that triggers the execution of the `MovieClient` method,
     *              which
     *              is an ActionEvent object containing information about the action
     *              that triggered
     *              the method call.
     *              <p>
     *              - `event` is an `ActionEvent` object representing the user's
     *              action that triggered
     *              the function.
     *              - The source of the event is a `Node` object, which provides
     *              information about
     *              the element that was clicked or selected.
     */
    @FXML
    void MovieClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
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
            CommandeClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }

    /**
     * Loads a new FXML interface, creates a new scene, and attaches it to a new
     * stage,
     * while closing the current stage.
     *
     * @param event ActionEvent object that triggered the `SerieClient()` method to
     *              be
     *              called, providing the source of the event and any related data.
     *              <p>
     *              - It is an instance of `ActionEvent`, representing an action
     *              performed on the
     *              JavaFX application.
     *              - Its source field references the object that triggered the
     *              event, which in this
     *              case is a `Node` (i.e., a FXML component).
     */
    @FXML
    void SerieClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
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
        } catch (final IOException e) {
            CommandeClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }
}
