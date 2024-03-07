package com.example.rakcha1.controllers.series;

import com.example.rakcha1.HelloApplication;
import com.example.rakcha1.modeles.series.Categorie;
import com.example.rakcha1.modeles.series.Serie;
import com.example.rakcha1.service.series.IServiceCategorieImpl;
import com.example.rakcha1.service.series.IServiceSerieImpl;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.Stage;



import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.collections.ObservableList;


public class SerieClientController {
    @FXML
    private Label resultatLabel;
    private ObservableList<String> selectedCategories = FXCollections.observableArrayList();
    @FXML
    private ComboBox<String> CamboxCategorie;
    @FXML
   private ListView<Serie> listeSerie;
   private List<Categorie>categorieList=new ArrayList<>();
    private List<Serie> listerecherche;
    @FXML
    private TextField recherchefld;


    public void afficher() throws SQLException {
        IServiceCategorieImpl iServiceCategorie=new IServiceCategorieImpl();
        categorieList=iServiceCategorie.recuperer();
    }
    private void trierParNom(List<Serie> series) {
        Collections.sort(series, (serie1, serie2) -> serie1.getNom().compareToIgnoreCase(serie2.getNom()));
    }



    //FOCTION RECHERCHE
    public static List<Serie> rechercher(List<Serie> liste, String recherche) {
        List<Serie> resultats = new ArrayList<>();

        for (Serie element : liste) {
            if (element.getNom().contains(recherche)) {
                resultats.add(element);
            }
        }

        return resultats;
    }
    public void afficherliste(List<Serie> series){
        listeSerie.getItems().clear();

        listeSerie.getItems().addAll(series);
        listeSerie.setCellFactory(param -> new ListCell<Serie>() {
            @Override
            protected void updateItem(Serie item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    double imageWidth = 200; // Largeur fixe souhaitée
                    double imageHeight = 200; // Hauteur fixe souhaitée
                    String img =item.getImage();
                    File file = new File(img);
                    Image image = new Image(file.toURI().toString());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(imageWidth);
                    imageView.setFitHeight(imageHeight);
                    imageView.setPreserveRatio(true);
                    setText("\n   Name :"+item.getNom()+"\n  Summary: "+item.getResume()+ "\n   Director : "+item.getDirecteur()+"\n   Country: " +item.getPays() );
                    setGraphic(imageView);
                }
            }
        });
    }


    @FXML
    private void initialize() throws SQLException {
        IServiceSerieImpl iServiceSerie = new IServiceSerieImpl();
        IServiceCategorieImpl iServiceCategorie = new IServiceCategorieImpl();
        List<Categorie> categorieList = iServiceCategorie.recuperer();
        ObservableList<String> categorieNames = FXCollections.observableArrayList();
        for (Categorie categorie : categorieList) {
            categorieNames.add(categorie.getNom());
        }
        CamboxCategorie.setItems(categorieNames);
        CamboxCategorie.setOnAction(event -> {
            String selectedCategorie = CamboxCategorie.getValue();
            if (!selectedCategories.contains(selectedCategorie)) {
                for (Categorie c: categorieList
                ) {
                    if(c.getNom()==selectedCategorie){

                        try {
                            listerecherche=iServiceSerie.recuperes(c.getIdcategorie());
                            trierParNom(listerecherche); // Tri des séries par nom
                            afficherliste(listerecherche);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        ///fonction recherche sur textfiled
        recherchefld.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            List<Serie> series;
            series=rechercher(listerecherche,newValue);
            afficherliste(series);
        });
        listeSerie.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() >= 0) {
                Serie selectedSerie = listeSerie.getItems().get(newValue.intValue());
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/EpisodeClient.fxml"));
                Stage stage = new Stage();
                try {
                    Parent root = fxmlLoader.load();
                    EpisodeClientController controller = fxmlLoader.getController();
                    controller.initialize(selectedSerie);
                    Scene scene = new Scene(root);
                    stage.setTitle("");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


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