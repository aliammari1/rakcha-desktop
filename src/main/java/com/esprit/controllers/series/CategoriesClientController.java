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
     * Populate the controller's TilePane with category entries retrieved from the service.
     *
     * Retrieves the first page of categories (page 0, size 10), clears the TilePane, and for each
     * category adds a VBox containing labels for its name and description.
     *
     * @throws RuntimeException if category retrieval fails
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
        } catch (final Exception e) {
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
     * Initializes the controller and populates the category view.
     *
     * Populates the TilePane with categories retrieved from the category service so the UI is ready after FXML loading.
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
     * Navigates to the Categorie client view by loading its FXML and replacing the current stage's scene.
     *
     * @param event the ActionEvent that triggered the navigation
     * @throws IOException if the FXML resource cannot be loaded
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
     * Switches the current window to the series client view.
     *
     * @param event the ActionEvent from the control that triggered the navigation
     * @throws IOException if the FXML resource for the series client cannot be loaded
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
     * Open the Episode client view by loading its FXML and replacing the current stage's scene.
     *
     * @param event the ActionEvent from the triggering UI control
     * @throws IOException if the EpisodeClient FXML resource cannot be loaded
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
