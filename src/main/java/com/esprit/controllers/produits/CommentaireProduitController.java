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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Is responsible for handling user actions related to the "Commentaire Produit"
 * section of the application. It contains methods that handle the creation of
 * new
 * stages and scenes, as well as the closing of existing stages. The controller
 * also
 * handles events related to the "Event", "ProduitClient", "MovieClient", and
 * "SerieClient" sections.
 */
public class CommentaireProduitController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(CommentaireProduitController.class.getName());
    private final Chat chat = new Chat();
    @FXML
    private FlowPane CommentaireFlowPane;
    @FXML
    private TextArea monCommentaitreText;

    /*
     * void onKeyPressed(KeyEvent event) {
     * if (event.getCode() == KeyCode.ENTER) {
     * String commentaireText = monCommentaitreText.getText();
     *
     *
     *
     * String reponseChat = chat.chatGPT(commentaireText);
     * chatCommentaireText.appendText(reponseChat + "\n");
     *
     * monCommentaitreText.clear();
     * }
     *
     */

    /**
     * Takes a user message as input and processes it by detecting if it contains
     * any bad
     * words, and if so, displays an alert message. If the message is clean, it
     * creates
     * a new comment object and saves it to the database.
     *
     * @param actionEvent event that triggers the execution of the `addChat()`
     *                    method,
     *                    which in this case is a user pressing the "Enter" key
     *                    while focused on the chat field.
     *                    <p>
     *                    - Type: ActionEvent
     *                    - Target: FXML
     *                    - Origin: User interface element (not specified)
     */
    @FXML
    void addchat(final ActionEvent actionEvent) {
        // Check for bad words and prevent further processing if found
        final String userMessage = this.monCommentaitreText.getText();
        CommentaireProduitController.LOGGER.info("User Message: " + userMessage);
        try {
            // Convertir la chaîne de texte en objet Date
            final String dateString = this.monCommentaitreText.getText();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // ajustez le format selon vos
                                                                                    // besoins
            final Date datecommantaire = dateFormat.parse(dateString);
            // Affichez la date pour vérification
            CommentaireProduitController.LOGGER.info("datecommantaire: " + datecommantaire);
            final String badwordDetection = this.chat.badword(userMessage);
            CommentaireProduitController.LOGGER.info("Badword Detection Result: " + badwordDetection);
            final ProduitService produitService = new ProduitService();
            final Produit produit = new Produit();
            if ("1".equals(badwordDetection)) {
                final Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Commentaire non valide");
                alert.setContentText("Votre commentaire contient des gros mots et ne peut pas être publié.");
                alert.showAndWait();
            } else {
                // Créez un objet Commentaire
                final Commentaire commentaire = new Commentaire((Client) new UserService().getUserById(111),
                        userMessage,
                        produitService.getProduitById(produit.getId_produit()));
                final CommentaireService commentaireService = new CommentaireService();
                // Ajoutez le commentaire à la base de données
                commentaireService.create(commentaire);
                // Mettez à jour l'interface utilisateur
                // updateCommentaireFlowPane(commentaire);
            }
        } catch (final ParseException e) {
            // Gérez l'exception si la conversion échoue
            CommentaireProduitController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Initializes the application by loading a accepted commentaire resource.
     *
     * @param location  URL of the initial resource to be loaded and is used to set
     *                  the
     *                  starting point for the loading process.
     * @param resources ResourceBundle containing localized messages and values for
     *                  the
     *                  application, which is used by the
     *                  `loadAcceptedCommentaire()` method to access
     *                  translated strings and other resources.
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.loadAcceptedCommentaire();
    }

    /**
     * Retrieves comments from a service, creates a card for each comment, and adds
     * them
     * to a `FlowPane`.
     */
    private void loadAcceptedCommentaire() {
        // Récupérer toutes les produits depuis le service
        final CommentaireService commentaireservices = new CommentaireService();
        final List<Commentaire> commentaires = commentaireservices.read();
        // Créer une carte pour chaque produit et l'ajouter à la FlowPane
        for (final Commentaire comm : commentaires) {
            final HBox cardContainer = this.createcommentairecard(comm);
            this.CommentaireFlowPane.getChildren().add(cardContainer);
        }
    }

    /**
     * Creates a `HBox` containing the commentary and the author's name, using a
     * `CommentaireService` to retrieve the comments and a `FlowPane` to display the
     * `HBox`.
     *
     * @param comm `Commentaire` object that contains information about a particular
     *             comment, which is to be displayed on a card.
     *             <p>
     *             - `comm` is an instance of `Commentaire`, representing a comment
     *             made by a user
     *             on a website.
     *             - `comm.getClient()` returns a `Client` object, containing
     *             information about the
     *             user who made the comment.
     *             - `comm.getCommentaire()` returns the actual comment text made by
     *             the user.
     * @returns a customizable HBox containing the author's name and the content of
     *          a
     *          given comment.
     *          <p>
     *          1/ The output is an `HBox` object, which represents a container for
     *          other UI elements.
     *          2/ The `setStyle` method is used to set the padding of the `HBox` to
     *          5 pixels on
     *          all sides.
     *          3/ An `AnchorPane` object is created and set as the content of the
     *          `HBox`. This
     *          means that the `AnchorPane` will be the container for the other UI
     *          elements inside
     *          the `HBox`.
     *          4/ The `setPrefWidth` method is used to set the width of the
     *          `AnchorPane` to 200
     *          pixels.
     *          5/ A `Label` object representing the author's name is created and
     *          added as a child
     *          element of the `AnchorPane`.
     *          6/ The `setStyle` method is used again to set the font weight, font
     *          family, and
     *          text fill color of the label to bold, Verdana, and black,
     *          respectively.
     *          7/ The `setPrefWidth` method is used to set the width of the label
     *          to 230 pixels.
     *          8/ The `setLayoutX` and `setLayoutY` methods are used to position
     *          the label at
     *          (20, 30) on the anchor pane.
     *          9/ A second `Label` object representing the comment is created and
     *          added as a child
     *          element of the `AnchorPane`.
     *          10/ The same styling properties as before are applied to the second
     *          label.
     *          11/ The `setPrefWidth` method is used again to set the width of the
     *          second label
     *          to 230 pixels.
     *          12/ The `setLayoutX` and `setLayoutY` methods are used to position
     *          the second label
     *          at (20, 55) on the anchor pane.
     *          13/ Finally, the `getChildren` method is used to add both labels to
     *          the `AnchorPane`.
     */
    public HBox createcommentairecard(final Commentaire comm) {
        // Charger les commentaires depuis le service
        final CommentaireService commentaireservices = new CommentaireService();
        final List<Commentaire> commentaires = commentaireservices.read();
        // Afficher chaque commentaire
        // for (Commentaire comm : commentaires) {
        // Créer une VBox pour chaque commentaire
        final HBox commentaireVBox = new HBox();
        commentaireVBox.setStyle("-fx-padding: 5px 0 0 10px");
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
        // Contenu du commentaire
        final Label commentaireLabel = new Label(comm.getCommentaire());
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
        // CommentaireFlowPane.getChildren().add(commentaireVBox);
        return commentaireVBox;
    }

    /**
     * Loads a new user interface (`CommentaireProduit.fxml`) when the `ActionEvent`
     * is
     * triggered, creates a new stage with the loaded scene, and replaces the
     * current
     * stage with the new one.
     *
     * @param event ActionEvent object that triggered the function execution,
     *              providing
     *              the source of the event and allowing for proper handling of the
     *              corresponding action.
     *              <p>
     *              - `event`: An ActionEvent object representing a user event, such
     *              as clicking a
     *              button or pressing a key.
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
            CommentaireProduitController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                      // d'entrée/sortie
        }
    }

    /**
     * Loads a new FXML interface when an event is triggered, creates a new scene
     * with
     * the loaded interface, and attaches it to a new stage, replacing the current
     * stage.
     *
     * @param event ActionEvent object that triggered the event handler method,
     *              providing
     *              information about the source of the event and any associated
     *              data.
     *              <p>
     *              - It is an instance of `ActionEvent`, which represents an action
     *              event that
     *              occurred in the application.
     *              - It has various properties related to the event, such as the
     *              source of the event
     *              (e.g., a button click), the event type (e.g., "click"), and any
     *              additional data
     *              associated with the event.
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
            CommentaireProduitController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                      // d'entrée/sortie
        }
    }

    /**
     * Loads a new FXML interface, creates a new scene, and attaches it to a new
     * stage,
     * replacing the current stage.
     *
     * @param event ActionEvent object that triggered the function execution,
     *              providing
     *              information about the action that was performed, such as the
     *              source of the event
     *              and the ID of the action.
     *              <p>
     *              - Type: ActionEvent
     *              - Source: Node (the object that triggered the event)
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
            CommentaireProduitController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                      // d'entrée/sortie
        }
    }

    /**
     * Likely profiles client-side code execution, possibly monitoring performance
     * or
     * memory usage for optimization purposes.
     *
     * @param event occurrence of an action event that triggered the execution of
     *              the
     *              `profilclient` function.
     */
    @FXML
    void profilclient(final ActionEvent event) {
    }

    /**
     * Creates a new scene with an FXML file, loads it, and displays it as a new
     * stage.
     * It also closes the current stage.
     *
     * @param event ActionEvent that triggered the function execution, providing the
     *              necessary information to load and display the new FXML
     *              interface.
     *              <p>
     *              - `event`: An ActionEvent object representing a user action that
     *              triggered the
     *              function to run.
     *              - `source`: The source of the event, which is typically a button
     *              or other UI element.
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
            CommentaireProduitController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                      // d'entrée/sortie
        }
    }

    /**
     * Loads a new FXML interface, creates a new scene, and attaches it to a new
     * stage.
     * It then closes the current stage and displays the new one.
     *
     * @param event EventObject that triggered the `SeriesClient` method to be
     *              called,
     *              providing access to information about the event such as the
     *              source of the event.
     *              <p>
     *              - It is an instance of `ActionEvent`, which represents an action
     *              event occurring
     *              in the JavaFX application.
     *              - The source of the event is a `Node` object, which represents
     *              the element that
     *              triggered the event.
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
            CommentaireProduitController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                      // d'entrée/sortie
        }
    }
}
