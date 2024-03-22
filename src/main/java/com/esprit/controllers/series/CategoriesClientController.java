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

public class CategoriesClientController {

    @FXML
    ListView<Categorie> listeV2;
    @FXML
    Label prixF;
    int id = 1;
    @FXML
    private TilePane tilepane;


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

    @FXML
    private void initialize() {
        afficher();
    }

    ///gestion de menu
    @FXML
    void Ocategories(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/CategorieClient.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void Oseries(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/SeriesClient.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void Oepisode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/EpisodeClient.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
