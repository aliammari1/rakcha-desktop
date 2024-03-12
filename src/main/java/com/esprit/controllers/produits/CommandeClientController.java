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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class CommandeClientController implements Initializable {

    private static final String CLIENT_ID = "Ac_87vQSawIKlwhFFCBiYH0VYygxg5MWi0xakK3w0FyJirTITgf5CqfaE65WLUlia16-D5deHq6XKWo8";
    private static final String CLIENT_SECRET = "EKDa_P0DqelT1SNHMbfbVS6Pqp25dvz3fVlf_nwPMRAnqMwe2c6vX6yV2iW8lBFdMr_aXG8FD8cDCMt7";
    private static final String SUCCESS_URL = CommandeClientController.class.getResource("/success.html").toExternalForm();
    private static final String CANCEL_URL = CommandeClientController.class.getResource("/cancel.html").toExternalForm();
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


    @FXML
    void initialize(Commande commandeselectionner) {
        commande = commandeselectionner;
        connectedUser = usersService.getUserById(4);


        // Récupérer le prix total depuis SharedData et créer le Label correspondant

        Label prixTotalLabel = createPrixTotalLabel(totalPrix);


        // Ajouter le Label au FlowPane
        prixtotaleFlowPane.getChildren().add(prixTotalLabel);


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private Label createPrixTotalLabel(double prixTotal) {
        Label prixTotalLabel = new Label(prixTotal + " DT");
        prixTotalLabel.setFont(Font.font("Verdana", 25));
        prixTotalLabel.setStyle("-fx-text-fill: #d72222;");
        return prixTotalLabel;
    }

    @FXML
    void order(ActionEvent event) {
        CommandeItemService commandeItemService = new CommandeItemService();
        int idcommande = 0;

        // Validation du numéro de téléphone
        String numTelephone = numTelephoneTextField.getText();
        if (!isValidPhoneNumber(numTelephone)) {
            showAlert("Erreur de validation", "Numéro de téléphone invalide.");
            return; // Arrêter le traitement de la commande
        }

        // Validation de l'adresse
        String adresse = adresseTextField.getText();
        if (adresse.isEmpty()) {
            showAlert("Erreur de validation", "L'adresse ne peut pas être vide.");
            return; // Arrêter le traitement de la commande
        }


        commande.setAdresse(adresseTextField.getText());
        commande.setNum_telephone(Integer.parseInt(numTelephoneTextField.getText()));
        commande.setIdClient((Client) connectedUser);
        LocalDate date1 = LocalDate.now();
        commande.setDateCommande(java.sql.Date.valueOf(date1));
        try {
            idcommande = commandeService.createcommande(commande);
            commande.setIdCommande(idcommande);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (CommandeItem commandeItem : commande.getCommandeItem()
        ) {
            System.out.println(commande.getIdCommande());
            commandeItem.setCommande(commande);

            commandeItemService.create(commandeItem);
            decrementStock(commandeItem.getProduit(), commandeItem.getQuantity());
            idpaymentenligne.setVisible(true);


        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private boolean isValidPhoneNumber(String phoneNumber) {
        // Vérifier si le numéro de téléphone a exactement 8 chiffres et ne contient que des chiffres
        return phoneNumber.matches("\\d{8}");
    }

    private void decrementStock(Produit produit, int quantity) {
        // Décrémenter le stock dans la base de données
        produit.setQuantiteP(produit.getQuantiteP() - quantity);
        ProduitService produitService = new ProduitService();
        produitService.update(produit); // Assurez-vous que votre service de produit dispose d'une méthode de mise à jour
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

    @FXML
    void payment(ActionEvent event) {
        APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, "sandbox");

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.valueOf(totalPrix)); // totalPrix should be set to the total price of the order

        Transaction transaction = new Transaction();
        transaction.setDescription("Your Purchase Description");
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost/cancel");
        redirectUrls.setReturnUrl("http://localhost/success");
        payment.setRedirectUrls(redirectUrls);
        payment.setRedirectUrls(redirectUrls);


        try {
            Payment createdPayment = payment.create(apiContext);
            for (Links link : createdPayment.getLinks()) {
                if (link.getRel().equalsIgnoreCase("approval_url")) {
                    System.out.println(link.getHref());
                    redirectToPayPal(link.getHref());
                    break;
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            showAlert("Error", "Could not initiate PayPal payment.");
        }
    }


    private void redirectToPayPal(String approvalLink) {
        Platform.runLater(() -> {
            Dialog<Void> dialog = new Dialog<>();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UNDECORATED);

            WebView webView = new WebView();
            webView.getEngine().load(approvalLink);

            // Add a progress bar to the web view
            ProgressBar progressBar = new ProgressBar();
            webView.getEngine().getLoadWorker().progressProperty().addListener((obs, oldProgress, newProgress) -> {
                progressBar.setProgress(newProgress.doubleValue());
            });

            // Create a close button
            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> {
                System.out.println("Close button clicked");
                dialog.hide();
            });

            VBox vBox = new VBox(5);
            vBox.getChildren().addAll(webView, progressBar, closeButton);

            // Handle successful redirection
            webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.contains("http://localhost/success")) {
                    String paymentId = extractQueryParameter(newValue, "paymentId");
                    String payerId = extractQueryParameter(newValue, "PayerID");
                    URL successUrl = getClass().getResource("/success.html");
                    webView.getEngine().load(successUrl.toExternalForm());
                    completePayment(paymentId, payerId);
                }
            });

            dialog.getDialogPane().setContent(vBox);
            dialog.getDialogPane().setPrefSize(800, 600);
            dialog.showAndWait();
        });
    }

    private String extractQueryParameter(String url, String parameterName) {
        try {
            List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), StandardCharsets.UTF_8);
            for (NameValuePair param : params) {
                if (param.getName().equals(parameterName)) {
                    return param.getValue();
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null; // Parameter not found
    }

    private void completePayment(String paymentId, String payerId) {
        APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, "sandbox");

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        try {
            Payment executedPayment = payment.execute(apiContext, paymentExecution);
            if (executedPayment.getState().equals("approved")) {

                commande.setStatu("Payee");


                commandeService.update(commande);


            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();

        }
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
    void showcinema(ActionEvent event) {


    }

    @FXML
    void showevenement(ActionEvent event) {


    }

    @FXML
    void showmovie(ActionEvent event) {
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
    void showproduit(ActionEvent event) {

    }

    @FXML
    void showserie(ActionEvent event) throws IOException {
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
            stage.setTitle("chat ");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }

    }



}
