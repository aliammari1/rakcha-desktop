package com.esprit.controllers.produits;

import com.esprit.models.produits.*;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.produits.CommandeItemService;
import com.esprit.services.produits.CommandeService;
import com.esprit.services.produits.ProduitService;
import com.esprit.services.users.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;



public class CommandeClientController implements Initializable {

    @FXML
    private TextField adresseTextField;

    @FXML
    private TextField numTelephoneTextField;

    @FXML
    private AnchorPane paimenet;
    Commande commande = new Commande();

    private final CommandeService commandeService = new CommandeService();
    private final UserService usersService = new UserService();

    private User connectedUser;

    @FXML
    private FlowPane prixtotaleFlowPane;

    @FXML
    private Button idpayment;

    // PayPal API credentials
    private static final String CLIENT_ID = "YOUR_PAYPAL_CLIENT_ID";
    private static final String CLIENT_SECRET = "YOUR_PAYPAL_CLIENT_SECRET";

    double totalPrix = SharedData.getInstance().getTotalPrix();


    @FXML
    void initialize(Commande commandeselectionner) {
     commande=commandeselectionner;
     connectedUser=usersService.getUserById(4);


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
        int idcommande=0;

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
            idcommande=commandeService.createcommande(commande);
            commande.setIdCommande(idcommande);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (CommandeItem commandeItem: commande.getCommandeItem()
             ) {
            System.out.println(commande.getIdCommande());
            commandeItem.setCommande(commande);

            commandeItemService.create(commandeItem);
            decrementStock(commandeItem.getProduit(),commandeItem.getQuantity());
            idpayment.setVisible(true);

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



    }


}
