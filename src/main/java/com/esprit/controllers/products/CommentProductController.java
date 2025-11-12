package com.esprit.controllers.products;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.products.Comment;
import com.esprit.models.products.Product;
import com.esprit.models.users.Client;
import com.esprit.services.products.CommentService;
import com.esprit.services.products.ProductService;
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

/**
 * Controller class for managing product comments in the RAKCHA application.
 * This controller handles the display of existing comments and provides
 * functionality
 * for users to add new comments.
 * 
 * <p>
 * The controller provides validation for comments, checking for inappropriate
 * content
 * before storing them in the database. It also manages navigation between
 * different
 * parts of the application.
 * </p>
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class CommentProductController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(CommentProductController.class.getName());
    private final Chat chat = new Chat();
    @FXML
    private FlowPane CommentFlowPane;
    @FXML
    private TextArea monCommentaitreText;

    /**
     * Processes a new comment submitted by the user.
     * 
     * <p>
     * This method:
     * 1. Extracts the comment text from the text area
     * 2. Validates the content using a bad word filter
     * 3. If the content is appropriate, creates a new Comment object and saves it
     * to the database
     * 4. Otherwise, displays an alert warning the user about inappropriate content
     * </p>
     *
     * @param actionEvent The action event that triggered this method
     */
    @FXML
    void addchat(final ActionEvent actionEvent) {
        // Check for bad words and prevent further processing if found
        final String userMessage = this.monCommentaitreText.getText();
        CommentProductController.LOGGER.info("User Message: " + userMessage);
        try {
            // Convertir la chaîne de texte en objet Date
            final String dateString = this.monCommentaitreText.getText();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // ajustez le format selon vos
                                                                                    // besoins
            final Date datecommantaire = dateFormat.parse(dateString);
            // Affichez la date pour vérification
            CommentProductController.LOGGER.info("datecommantaire: " + datecommantaire);
            final String badwordDetection = this.chat.badword(userMessage);
            CommentProductController.LOGGER.info("Badword Detection Result: " + badwordDetection);
            final ProductService produitService = new ProductService();
            final Product produit = new Product();
            if ("1".equals(badwordDetection)) {
                final Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Comment non valide");
                alert.setContentText("Votre comment contient des gros mots et ne peut pas être publié.");
                alert.showAndWait();
            }
 else {
                // Créez un objet Comment
                final Comment comment = new Comment((Client) new UserService().getUserById(4L), userMessage,
                        produitService.getProductById(produit.getId()));
                final CommentService commentService = new CommentService();
                // Ajoutez le comment à la base de données
                commentService.create(comment);
                // Mettez à jour l'interface utilisateur
                // updateCommentFlowPane(comment);
            }

        } catch (final ParseException e) {
            // Gérez l'exception si la conversion échoue
            CommentProductController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Initialize the controller and populate the UI with accepted comments.
     *
     * Loads accepted comments and adds a card for each to the CommentFlowPane.
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL location, final ResourceBundle resources) {
        this.loadAcceptedComment();
    }


    /**
     * Load and display accepted comments in the CommentFlowPane.
     *
     * Retrieves comments from CommentService, creates a UI card for each comment via
     * createcommentcard(Comment), and adds those cards to the CommentFlowPane.
     */
    private void loadAcceptedComment() {
        // Récupérer toutes les produits depuis le service
        final CommentService commentservices = new CommentService();
        final List<Comment> comments = commentservices.read();
        // Créer une carte pour chaque produit et l'ajouter à la FlowPane
        for (final Comment comm : comments) {
            final HBox cardContainer = this.createcommentcard(comm);
            this.CommentFlowPane.getChildren().add(cardContainer);
        }

    }


    /**
     * Creates a card-like UI component to display a comment.
     * 
     * <p>
     * The card includes the name of the comment author and the comment text,
     * with appropriate styling for readability.
     * </p>
     *
     * @param comm The Comment object to create a card for
     * @return An HBox containing the styled comment card
     */
    public HBox createcommentcard(final Comment comm) {
        // Créer une VBox pour chaque comment
        final HBox commentVBox = new HBox();
        commentVBox.setStyle("-fx-padding: 5px 0 0 10px");
        final AnchorPane card = new AnchorPane();
        card.setPrefWidth(200);
        // Nom de l'auteur
        final Label nomLabel = new Label(comm.getClient().getFirstName() + " " + comm.getClient().getLastName() + ":");
        nomLabel.setStyle("-fx-font-weight: bold");
        nomLabel.setFont(Font.font("Verdana", 15));
        nomLabel.setStyle("-fx-text-fill: black;");
        nomLabel.setPrefWidth(230);
        nomLabel.setLayoutX(20);
        nomLabel.setLayoutY(30);
        nomLabel.setMaxWidth(300);
        nomLabel.setWrapText(true);
        // Contenu du comment
        final Label commentLabel = new Label(comm.getCommentText());
        commentLabel.setFont(Font.font("Arial", 12));
        commentLabel.setTextFill(Color.web("black"));
        commentLabel.setPrefWidth(230);
        commentLabel.setLayoutX(20);
        commentLabel.setLayoutY(55);
        commentLabel.setMaxWidth(300);
        commentLabel.setWrapText(true);
        // Ajout des éléments à la VBox
        card.getChildren().addAll(nomLabel, commentLabel);
        commentVBox.getChildren().add(card);
        // Ajout de la VBox au FlowPane
        // CommentFlowPane.getChildren().add(commentVBox);
        return commentVBox;
    }


    /**
     * Open the cinema client view in a new window and close the current window.
     *
     * @param event the ActionEvent that triggered the navigation
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
            CommentProductController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                  // d'entrée/sortie
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
        } catch (final IOException e) {
            CommentProductController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                  // d'entrée/sortie
        }

    }


    /**
     * Opens the product client view in a new window and closes the current window.
     *
     * @param event the action event that triggered the navigation
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
            CommentProductController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                  // d'entrée/sortie
        }

    }


    /**
     * No-op handler for client profile action.
     *
     * Currently a placeholder and performs no action.
     */
    @FXML
    void profilclient(final ActionEvent event) {
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
        } catch (final IOException e) {
            CommentProductController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                  // d'entrée/sortie
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
        } catch (final IOException e) {
            CommentProductController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                  // d'entrée/sortie
        }

    }

}
