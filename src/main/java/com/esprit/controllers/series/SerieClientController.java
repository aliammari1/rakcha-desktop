package com.esprit.controllers.series;

import com.esprit.models.series.Categorie;
import com.esprit.models.series.Favoris;
import com.esprit.models.series.Serie;
import com.esprit.services.series.IServiceCategorieImpl;
import com.esprit.services.series.IServiceFavorisImpl;
import com.esprit.services.series.IServiceSerie;
import com.esprit.services.series.IServiceSerieImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class SerieClientController {
    private final ObservableList<String> selectedCategories = FXCollections.observableArrayList();
    @FXML
    Button watchEpisode;
    @FXML
    private Label resultatLabel;
    @FXML
    private ComboBox<String> CamboxCategorie;
    @FXML
    private ListView<Serie> listeSerie;
    private List<Categorie> categorieList = new ArrayList<>();
    private List<Serie> listerecherche;
    private List<Serie> listeTop3;
    @FXML
    private TextField recherchefld;
    /*
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
    */
    /*
        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            IServiceSerieImpl ss = new IServiceSerieImpl();


            hboxTop3.setSpacing(20); // Set the spacing between VBox instances
            hboxTop3.setPadding(new Insets(10));
            List<Serie> listeTop3 = ss.findMostLiked();

            for (Serie serie : listeTop3) {
                VBox vbox = createSeriesVBox(serie);
                hboxTop3.getChildren().add(vbox);
            }

        }

     */
    @FXML
    private HBox hboxTop3;


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

    @FXML
    void onWatch(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EpisodeClient.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) watchEpisode.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private VBox createSeriesVBox(Serie serie) {
        VBox vbox = new VBox();
        vbox.setSpacing(8);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(8));
        vbox.setMinSize(250, 210);

        // Créez d'abord le Label avec le nom
        Label titleLabel = new Label(serie.getNom());
        titleLabel.setStyle("-fx-font-family: 'Helvetica'; -fx-font-size: 18.0px; -fx-font-weight: bold; -fx-text-fill: #FCE19A;");
        titleLabel.setAlignment(Pos.CENTER);


        // Ensuite, ajoutez l'ImageView avec l'image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(240);
        imageView.setFitHeight(140);

        // Chargez l'image depuis le fichier
        File file = new File(serie.getImage());
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
        // Créer le Label pour afficher le nombre de likes
        Label likesLabel = new Label("Likes: " + serie.getNbLikes());
        likesLabel.setStyle("-fx-font-family: 'Helvetica'; -fx-font-size: 14.0px; -fx-text-fill: #FCE19A;");
        likesLabel.setAlignment(Pos.CENTER);
        // Ajoutez d'abord le Label, puis l'ImageView à la liste des enfants
        //vbox.getChildren().addAll(titleLabel, imageView);
        vbox.getChildren().addAll(imageView, titleLabel, likesLabel);

        vbox.setStyle("-fx-background-color: linear-gradient(to top right, #ae2d3c, black);");
        return vbox;
    }

    public void afficher() throws SQLException {
        IServiceCategorieImpl iServiceCategorie = new IServiceCategorieImpl();
        categorieList = iServiceCategorie.recuperer();
    }

    private void trierParNom(List<Serie> series) {
        Collections.sort(series, (serie1, serie2) -> serie1.getNom().compareToIgnoreCase(serie2.getNom()));
    }

    public void afficherliste(List<Serie> series) {
        listeSerie.getItems().clear();

        listeSerie.setCellFactory(param -> new ListCell<Serie>() {
            @Override
            protected void updateItem(Serie item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // Créez un AnchorPane pour chaque série
                    AnchorPane anchorPane = new AnchorPane();
                    anchorPane.setPrefSize(400, 200); // Définissez la taille souhaitée

                    // Ajoutez une ImageView pour afficher l'image
                    ImageView imageView = new ImageView();
                    //imageView.setFitWidth(150);
                    //imageView.setFitHeight(150);
                    imageView.setFitWidth(150);
                    imageView.setFitWidth(150);

                    imageView.setPreserveRatio(true);

                    // Chargez l'image depuis le fichier
                    File file = new File(item.getImage());
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);

                    // Ajoutez des composants à l'AnchorPane (toutes les informations de la série)
                    Label nameLabel = new Label("Name: " + item.getNom());
                    nameLabel.setStyle("-fx-font-family: 'Helvetica'; -fx-font-size: 18.0px; -fx-font-weight: bold; -fx-text-fill: #333333;"); // Couleur de texte sombre
                    Label directorLabel = new Label("Director: " + item.getDirecteur());
                    directorLabel.setStyle("-fx-font-family: 'Helvetica'; -fx-font-size: 14.0px; -fx-font-weight: normal; -fx-text-fill: #666666;"); // Couleur de texte sombre plus claire
                    Label countryLabel = new Label("Country: " + item.getPays());
                    countryLabel.setStyle("-fx-font-family: 'Helvetica'; -fx-font-size: 14.0px; -fx-font-weight: normal; -fx-text-fill: #666666;"); // Couleur de texte sombre plus claire


                    //Label summaryLabel = new Label("Summary: " + item.getResume());
                    //Label directorLabel = new Label("Director: " + item.getDirecteur());
                    // Label countryLabel = new Label("Country: " + item.getPays());


                    Image iconHeart = new Image("pictures/films/heart.png");
                    ImageView HeartImageView = new ImageView(iconHeart);
                    HeartImageView.setFitWidth(10.0);
                    HeartImageView.setFitHeight(10.0);

                    Label likesLabel = new Label("Likes: " + item.getNbLikes());
                    likesLabel.setStyle("-fx-font-family: 'Helvetica'; -fx-font-size: 14.0px; -fx-font-weight: normal; -fx-text-fill: #666666;"); // Couleur de texte sombre plus claire


                    // Ajoutez d'autres composants selon vos besoins

                    // Positionnez les composants dans l'AnchorPane
                    AnchorPane.setTopAnchor(imageView, 10.0);
                    AnchorPane.setLeftAnchor(imageView, 10.0);
                    AnchorPane.setTopAnchor(nameLabel, 10.0);
                    AnchorPane.setLeftAnchor(nameLabel, 180.0);
                    AnchorPane.setTopAnchor(directorLabel, 40.0);
                    AnchorPane.setLeftAnchor(directorLabel, 180.0);
                    AnchorPane.setTopAnchor(countryLabel, 70.0);
                    AnchorPane.setLeftAnchor(countryLabel, 180.0);
                    AnchorPane.setTopAnchor(likesLabel, 100.0);
                    AnchorPane.setLeftAnchor(likesLabel, 180.0);
                    AnchorPane.setTopAnchor(HeartImageView, 105.0);
                    AnchorPane.setLeftAnchor(HeartImageView, 230.0);


                    // Positionnez d'autres composants selon vos besoins


                    /*Button Like + Dislike Declaration */
                    Image iconImage2 = new Image("pictures/films/dislike.png");
                    ImageView iconImageView2 = new ImageView(iconImage2);
                    iconImageView2.setFitWidth(20.0);
                    iconImageView2.setFitHeight(20.0);
                    Button dislikeButton = new Button("", iconImageView2);


                    // Set the layout constraints for the Button in the AnchorPane
                    AnchorPane.setTopAnchor(dislikeButton, 10.0);
                    AnchorPane.setRightAnchor(dislikeButton, 50.0);


                    Image iconImage = new Image("pictures/films/like.png");
                    ImageView iconImageView = new ImageView(iconImage);
                    iconImageView.setFitWidth(20.0);
                    iconImageView.setFitHeight(20.0);
                    Button likeButton = new Button("", iconImageView);


                    // Set the layout constraints for the Button in the AnchorPane
                    AnchorPane.setTopAnchor(likeButton, 10.0);
                    AnchorPane.setRightAnchor(likeButton, 10.0);


                    Image iconFav = new Image("pictures/films/favoris.png");
                    ImageView iconImageViewFav = new ImageView(iconFav);
                    iconImageViewFav.setFitWidth(20.0);
                    iconImageViewFav.setFitHeight(20.0);
                    Button favButton = new Button("", iconImageViewFav);


                    // Set the layout constraints for the Button in the AnchorPane
                    AnchorPane.setTopAnchor(favButton, 10.0);
                    AnchorPane.setRightAnchor(favButton, 90.0);







                    /*Button Like + Dislike's Functions */
                    likeButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            item.setClickLikes(item.getClickLikes() + 1);
                            System.out.println(item.getClickLikes());
                            IServiceSerieImpl ss = new IServiceSerieImpl();
                            try {
                                if ((item.getClickLikes() == 0) || (item.getClickLikes() % 2 != 0)) {
                                    item.setNbLikes(item.getNbLikes() + 1);
                                    ss.ajouterLike(item);
                                    dislikeButton.setDisable(true);
                                } else {
                                    item.setNbLikes(item.getNbLikes() - 1);
                                    if (item.getNbLikes() == 0) {
                                        item.setLiked(0);
                                        ss.removeLike(item);
                                        dislikeButton.setDisable(false);
                                    } else {
                                        ss.ajouterLike(item);
                                        dislikeButton.setDisable(true);
                                    }
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    });


                    dislikeButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            item.setClickDislikes(item.getClickDislikes() + 1);
                            System.out.println(item.getClickDislikes());
                            IServiceSerieImpl ss = new IServiceSerieImpl();
                            try {
                                if ((item.getClickDislikes() == 0) || (item.getClickDislikes() % 2 != 0)) {
                                    item.setNbDislikes(item.getNbDislikes() + 1);
                                    ss.ajouterDislike(item);
                                    likeButton.setDisable(true);
                                } else {
                                    item.setNbDislikes(item.getNbDislikes() - 1);
                                    if (item.getNbDislikes() == 0) {
                                        item.setDisliked(0);
                                        ss.removeDislike(item);
                                        likeButton.setDisable(false);
                                    } else {
                                        ss.ajouterDislike(item);
                                        likeButton.setDisable(true);
                                    }
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    });

                    favButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            int id_user = 1;
                            int id_serie = item.getIdserie();
                            IServiceFavorisImpl sf = new IServiceFavorisImpl();
                            Favoris f = new Favoris(id_user, id_serie);
                            item.setClickFavoris(item.getClickFavoris() + 1);
                            System.out.println(item.getClickFavoris());
                            try {
                                if ((item.getClickFavoris() == 0) || (item.getClickFavoris() % 2 != 0)) {
                                    sf.ajouter(f);
                                } else {
                                    Favoris fav = sf.getByIdUserAndIdSerie(id_user, id_serie);
                                    sf.supprimer(fav.getId());
                                    System.out.println(fav.getId());
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    });






                    /*Button Watch Episode Declaration */
                    Image iconImageWatch = new Image("pictures/films/watch.png");
                    ImageView iconImageViewWatch = new ImageView(iconImageWatch);
                    iconImageViewWatch.setFitWidth(10.0);
                    iconImageViewWatch.setFitHeight(10.0);
                    Button watchButton = new Button("Watch", iconImageViewWatch);
                    watchButton.setStyle("-fx-background-color: #ae2d3c; " +
                            "-fx-background-radius: 8px; " +
                            "-fx-text-fill: #FCE19A; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-family: 'Arial Rounded MT Bold'; " +
                            "-fx-cursor: hand;");                    // Set the layout constraints for the Watch Button in the AnchorPane
                    AnchorPane.setTopAnchor(watchButton, 150.0);
                    AnchorPane.setLeftAnchor(watchButton, 180.0);
                    Label dislikesLabel = new Label("DisLikes: " + item.getNbDislikes());
                    dislikesLabel.setStyle("-fx-font-family: 'Helvetica'; -fx-font-size: 14.0px; -fx-font-weight: normal; -fx-text-fill: #666666;"); // Couleur de texte sombre plus claire
                    AnchorPane.setTopAnchor(dislikesLabel, 120.0);
                    AnchorPane.setLeftAnchor(dislikesLabel, 180.0);


                    // Ajoutez les composants à l'AnchorPane
                    //anchorPane.getChildren().addAll(imageView, nameLabel, summaryLabel, directorLabel, countryLabel, likesLabel, HeartImageView, likeButton, dislikeButton);
                    anchorPane.getChildren().addAll(imageView, nameLabel, directorLabel, countryLabel, likesLabel, HeartImageView, likeButton, dislikeButton, favButton, watchButton, dislikesLabel);
                    // Ajoutez d'autres composants selon vos besoins

                    // Définissez l'AnchorPane en tant que graphique pour la cellule
                    setGraphic(anchorPane);
                    watchButton.setOnAction(event -> {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/EpisodeClient.fxml"));
                        Stage stage = new Stage();
                        try {
                            Parent root = fxmlLoader.load();
                            EpisodeClientController controller = fxmlLoader.getController();
                            controller.initialize(item);
                            Scene scene = new Scene(root);
                            stage.setTitle("");
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        // Ajoutez un séparateur après chaque élément sauf le dernier
                        Separator separator = new Separator();
                        separator.setPrefWidth(400); // Ajustez la largeur selon vos besoins
                        separator.setStyle("-fx-border-color: #ae2d3c; -fx-border-width: 2px;");
                        VBox vBoxWithSeparator = new VBox(anchorPane, separator);
                        setGraphic(vBoxWithSeparator);
                    });


                }
            }
        });

        listeSerie.getItems().addAll(series);
    }

    @FXML
    void AfficherFavList(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ListFavoris.fxml"));
        Stage stage = new Stage();
        try {
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Your Favorits");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @FXML
    private void initialize() throws SQLException {

        loadSeriesList();

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
                for (Categorie c : categorieList
                ) {
                    if (c.getNom() == selectedCategorie) {

                        try {
                            listerecherche = iServiceSerie.recuperes(c.getIdcategorie());
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
        recherchefld.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Serie> series;
            series = rechercher(listerecherche, newValue);
            afficherliste(series);
        });


        listeSerie.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() >= 0) {
                Serie selectedSerie = listeSerie.getItems().get(newValue.intValue());
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/EpisodeClient.fxml"));
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

    @FXML
    private void loadSeriesList() throws SQLException {
        IServiceSerie<Serie> serieService = new IServiceSerieImpl();
        List<Serie> series = serieService.recuperers();
        afficherliste(series); // Utilisez votre méthode d'affichage pour la ListView
        IServiceSerieImpl ss = new IServiceSerieImpl();

        hboxTop3.setSpacing(20); // Set the spacing between VBox instances
        hboxTop3.setPadding(new Insets(10));
        List<Serie> listeTop3 = ss.findMostLiked();

        for (Serie serie : listeTop3) {
            VBox vbox = createSeriesVBox(serie);
            hboxTop3.getChildren().add(vbox);
        }
    }


    // Function to display a list of series in the JavaFX ListView

}
