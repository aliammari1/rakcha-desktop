package com.esprit.controllers.produits;

import com.esprit.models.produits.Commentaire;
import com.esprit.models.produits.Produit;
import com.esprit.models.users.Client;
import com.esprit.services.produits.CommentaireService;
import com.esprit.services.produits.ProduitService;
import com.esprit.services.users.UserService;
import com.esprit.utils.Chat;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


public class CommentaireProduitController implements Initializable {

    @FXML
    private FlowPane CommentaireFlowPane;


    @FXML
    private TextArea monCommentaitreText;


    private final Chat chat = new Chat();


    /*
    void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String commentaireText = monCommentaitreText.getText();



            String reponseChat = chat.chatGPT(commentaireText);
            chatCommentaireText.appendText(reponseChat + "\n");

            monCommentaitreText.clear();
        }

*/


    @FXML
    void addchat(ActionEvent actionEvent) {
        // Check for bad words and prevent further processing if found
        String userMessage = monCommentaitreText.getText();
        System.out.println("User Message: " + userMessage);

        try {
            // Convertir la chaîne de texte en objet Date
            String dateString = monCommentaitreText.getText();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // ajustez le format selon vos besoins
            Date datecommantaire = dateFormat.parse(dateString);

            // Affichez la date pour vérification
            System.out.println("datecommantaire: " + datecommantaire);

            String badwordDetection = chat.badword(userMessage);
            System.out.println("Badword Detection Result: " + badwordDetection);

            ProduitService produitService = new ProduitService();
            Produit produit = new Produit();

            if (badwordDetection.equals("1")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Commentaire non valide");
                alert.setContentText("Votre commentaire contient des gros mots et ne peut pas être publié.");
                alert.showAndWait();
            } else {
                // Créez un objet Commentaire
                Commentaire commentaire = new Commentaire((Client) new UserService().getUserById(4), userMessage, produitService.getProduitById(produit.getId_produit()), datecommantaire);

                CommentaireService commentaireService = new CommentaireService();
                // Ajoutez le commentaire à la base de données
                commentaireService.create(commentaire);

                // Mettez à jour l'interface utilisateur
                //updateCommentaireFlowPane(commentaire);
            }
        } catch (ParseException e) {
            // Gérez l'exception si la conversion échoue
            e.printStackTrace();
        }
    }


    public void initialize(URL location, ResourceBundle resources) {

        loadAcceptedCommentaire();

    }


    private void loadAcceptedCommentaire() {
        // Récupérer toutes les produits depuis le service
        CommentaireService commentaireservices = new CommentaireService();
        List<Commentaire> commentaires = commentaireservices.read();


        // Créer une carte pour chaque produit et l'ajouter à la FlowPane
        for (Commentaire comm : commentaires) {
            HBox cardContainer = createcommentairecard(comm);

            CommentaireFlowPane.getChildren().add(cardContainer);


        }

    }

    public HBox createcommentairecard(Commentaire comm) {

        // Charger les commentaires depuis le service
        CommentaireService commentaireservices = new CommentaireService();
        List<Commentaire> commentaires = commentaireservices.read();

        // Afficher chaque commentaire
        // for (Commentaire comm : commentaires) {
        // Créer une VBox pour chaque commentaire
        HBox commentaireVBox = new HBox();


        commentaireVBox.setStyle("-fx-padding: 5px 0 0 10px");
        AnchorPane card = new AnchorPane();
        card.setPrefWidth(200);

        // Nom de l'auteur
        Label nomLabel = new Label(comm.getClient().getFirstName() + " " + comm.getClient().getLastName() + ":");
        nomLabel.setStyle("-fx-font-weight: bold");
        nomLabel.setFont(Font.font("Verdana", 15));
        nomLabel.setStyle("-fx-text-fill: black;");

        nomLabel.setPrefWidth(230);
        nomLabel.setLayoutX(20);
        nomLabel.setLayoutY(30);
        nomLabel.setMaxWidth(300);
        nomLabel.setWrapText(true);

        // Contenu du commentaire
        Label commentaireLabel = new Label(comm.getCommentaire());
        commentaireLabel.setFont(Font.font("Arial", 12));
        commentaireLabel.setTextFill(Color.web("black"));
        commentaireLabel.setPrefWidth(230);
        commentaireLabel.setLayoutX(20);
        commentaireLabel.setLayoutY(55);
        commentaireLabel.setMaxWidth(300);
        commentaireLabel.setWrapText(true);

        // Ajout des éléments à la VBox
        card.getChildren().addAll(nomLabel, commentaireLabel);


        commentaireVBox.getChildren().add(card);
        // Ajout de la VBox au FlowPane
        //CommentaireFlowPane.getChildren().add(commentaireVBox);
        return commentaireVBox;
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







