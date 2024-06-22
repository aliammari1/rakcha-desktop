package com.esprit.controllers.series;
import com.esprit.controllers.ClientSideBarController;
import com.esprit.models.series.Categorie;
import com.esprit.models.users.Client;
import com.esprit.services.series.IServiceCategorieImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * Is responsible for handling user interactions related to categories, such as
 * displaying category information and handling menu events. The controller uses an
 * `IServiceCategorieImpl` interface to retrieve category data from a database and
 * displays the information in a tile pane. Additionally, it handles menu events for
 * different types of content (series, episodes) and displays them in separate stages.
 */
public class CategoriesClientController {
    @FXML
    ListView<Categorie> listeV2;
    @FXML
    Label prixF;
    int id = 1;
    @FXML
    private TilePane tilepane;
    /**
     * Clears the children of a `tilepane`, retrieves a list of categories from an
     * `IServiceCategorieImpl`, loops through the list and adds a `VBox` for each category,
     * displaying its name and description.
     */
    public void afficher() {
        tilepane.getChildren().clear();
        IServiceCategorieImpl iServiceCategorie = new IServiceCategorieImpl();
        double imageWidth = 200; // Largeur fixe souhaitée
        double imageHeight = 200; // Hauteur fixe souhaitée
        //recupuration de liste de plat ajouter au panier
        List<Categorie> categories = new ArrayList<>();
        try {
            categories = iServiceCategorie.recuperer();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (Categorie c : categories) {
            String titre = c.getNom();
            String description = c.getDescription();
            // Créer une boîte pour afficher les informations de l'oeuvre
            Insets spacing = new Insets(10, 10, 10, 10); // 10 pixels d'espacement
            HBox buttonBox = new HBox();
            buttonBox.setSpacing(10);
            VBox oeuvreBox = new VBox();
            oeuvreBox.getChildren().add(new Label("Nom : " + titre));
            oeuvreBox.getChildren().add(new Label("Description : " + description));
            Region espaceHorizontal = new Region();
            espaceHorizontal.setPrefWidth(10);
            tilepane.getChildren().addAll(oeuvreBox, espaceHorizontal);
        }
    }
    /**
     * Displays a message upon launching the application using FXML.
     */
    @FXML
    private void initialize() {
        afficher();
    }
    /** 
     * @param event
     * @throws IOException
     */
    ///gestion de menu
    /**
     * Loads a FXML file named "CategorieClient.fxml", creates a scene with the root node,
     * sets the scene on a stage, and displays the stage.
     * 
     * @param event event that triggered the function, specifically the opening of a
     * JavaFX application.
     * 
     * 	- `event`: An `ActionEvent` object representing the user's action that triggered
     * the function execution.
     */
    @FXML
    void Ocategories(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/CategorieClient.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Loads a FXML file, creates a scene and stages it.
     * 
     * @param event event that triggered the function execution, which in this case is a
     * user action on the SeriesClient.fxml file.
     * 
     * 	- `event`: An `ActionEvent` object representing the user action that triggered
     * the function.
     */
    @FXML
    void Oseries(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/SeriesClient.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Loads an FXML file, creates a scene and stages it in a window.
     * 
     * @param event EventObject that triggered the execution of the `Oepisode()` method,
     * providing information about the source of the event and its associated data.
     * 
     * Event: `ActionEvent event`
     * 
     * Main properties:
     * 
     * 	- Source: The object that triggered the action (not shown)
     * 	- Event type: The specific action that was triggered (e.g., "SELECT", "SAVE")
     */
    @FXML
    void Oepisode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/EpisodeClient.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
