package com.esprit.controllers.series;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.esprit.models.series.Category;
import com.esprit.services.series.IServiceCategorieImpl;
import com.esprit.utils.PageRequest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Is responsible for handling user interactions related to categories, such as
 * displaying category information and handling menu events. The controller uses
 * an `IServiceCategorieImpl` interface to retrieve category data from a
 * database and displays the information in a tile pane. Additionally, it
 * handles menu events for different types of content (series, episodes) and
 * displays them in separate stages.
 */
public class CategoriesClientController {
    @FXML
    ListView<Category> listeV2;
    @FXML
    Label prixF;
    int id = 1;
    @FXML
    private TilePane tilepane;

    /**
     * Clears the children of a `tilepane`, retrieves a list of categories from an
     * `IServiceCategorieImpl`, loops through the list and adds a `VBox` for each
     * category, displaying its name and description.
     */
    public void afficher() {
        this.tilepane.getChildren().clear();
        final IServiceCategorieImpl iServiceCategorie = new IServiceCategorieImpl();
        final double imageWidth = 200; // Largeur fixe souhaitée
        final double imageHeight = 200; // Hauteur fixe souhaitée
        // recupuration de liste de plat ajouter au shoppingcart
        List<Category> categories = new ArrayList<>();
        try {
            PageRequest pageRequest = new PageRequest(0, 10);
            categories = iServiceCategorie.read(pageRequest).getContent();
        }
 catch (final Exception e) {
            throw new RuntimeException(e);
        }

        for (final Category c : categories) {
            final String titre = c.getName();
            final String description = c.getDescription();
            // Créer une boîte pour afficher les informations de l'oeuvre
            final Insets spacing = new Insets(10, 10, 10, 10); // 10 pixels d'espacement
            final HBox buttonBox = new HBox();
            buttonBox.setSpacing(10);
            final VBox oeuvreBox = new VBox();
            oeuvreBox.getChildren().add(new Label("Nom : " + titre));
            oeuvreBox.getChildren().add(new Label("Description : " + description));
            final Region espaceHorizontal = new Region();
            espaceHorizontal.setPrefWidth(10);
            this.tilepane.getChildren().addAll(oeuvreBox, espaceHorizontal);
        }

    }


    /**
     * Displays a message upon launching the application using FXML.
     */
    @FXML
    private void initialize() {
        this.afficher();
    }


    /**
     * @param event
     * @throws IOException
     */
    /// gestion de menu
    /**
     * Loads a FXML file named "/ui//ui/CategorieClient.fxml", creates a scene with
     * the root node, sets the scene on a stage, and displays the stage.
     *
     * @param event
     *              event that triggered the function, specifically the opening of a
     *              JavaFX application.
     *              <p>
     *              - `event`: An `ActionEvent` object representing the user's
     *              action
     *              that triggered the function execution.
     */
    @FXML
    void Ocategories(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui//ui/CategorieClient.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Loads a FXML file, creates a scene and stages it.
     *
     * @param event
     *              event that triggered the function execution, which in this case
     *              is
     *              a user action on the SeriesClient.fxml file.
     *              <p>
     *              - `event`: An `ActionEvent` object representing the user action
     *              that triggered the function.
     */
    @FXML
    void Oseries(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/SeriesClient.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Loads an FXML file, creates a scene and stages it in a window.
     *
     * @param event
     *              EventObject that triggered the execution of the `Oepisode()`
     *              method, providing information about the source of the event and
     *              its associated data.
     *              <p>
     *              Event: `ActionEvent event`
     *              <p>
     *              Main properties:
     *              <p>
     *              - Source: The object that triggered the action (not shown) -
     *              Event
     *              type: The specific action that was triggered (e.g., "SELECT",
     *              "SAVE")
     */
    @FXML
    void Oepisode(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/EpisodeClient.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}

