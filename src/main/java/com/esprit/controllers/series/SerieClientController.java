package com.esprit.controllers.series;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.esprit.models.series.Category;
import com.esprit.models.series.Favorite;
import com.esprit.models.series.Series;
import com.esprit.models.users.Client;
import com.esprit.services.series.IServiceCategorieImpl;
import com.esprit.services.series.IServiceFavoriteImpl;
import com.esprit.services.series.IServiceSeriesImpl;
import com.esprit.utils.PageRequest;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Is a controller for a series client application. It manages the display of a
 * list of series in a JavaFX ListView and provides functionalities such as
 * loading the series list, filtering the list based on user input, and
 * displaying additional information about each series. The class also handles
 * menu events for categories, episodes, and series.
 */
public class SerieClientController {
    private static final Logger LOGGER = Logger.getLogger(SerieClientController.class.getName());
    private final ObservableList<String> selectedCategories = FXCollections.observableArrayList();
    @FXML
    Button watchEpisode;
    @FXML
    private Label resultatLabel;
    @FXML
    private ComboBox<Category> CamboxCategorie;
    @FXML
    private ListView<Series> listeSerie;
    private List<Category> categorieList = new ArrayList<>();
    private List<Series> listerecherche;
    private List<Series> listeTop3;
    @FXML
    private TextField recherchefld;
    /*
     * public void afficherliste(List<Serie> series){ listeSerie.getItems().clear();
     *
     * listeSerie.getItems().addAll(series); listeSerie.setCellFactory(param -> new
     * ListCell<Serie>() {
     *
     * @Override protected void updateItem(Serie item, boolean empty) {
     * super.updateItem(item, empty); if (empty || item == null) { setText(null); }

     * else { double imageWidth = 200; // Largeur fixe souhaitée double imageHeight
     * = 200; // Hauteur fixe souhaitée String img =item.getImage(); File file = new
     * File(img); Image image = new Image(file.toURI().toString()); ImageView
     * imageView = new ImageView(image); imageView.setFitWidth(imageWidth);
     * imageView.setFitHeight(imageHeight); imageView.setPreserveRatio(true);
     * setText("\n   Name :"+item.getName()+"\n  Summary: "+item.getResume()+
     * "\n   Director : "+item.getDirecteur()+"\n   Country: " +item.getPays() );
     * setGraphic(imageView); }
 }
 }
); }

     */
    /*
     * @Override public void initialize(URL url, ResourceBundle resourceBundle) {
     * IServiceSerieImpl ss = new IServiceSerieImpl();
     *
     *
     * hboxTop3.setSpacing(20); // Set the spacing between VBox instances
     * hboxTop3.setPadding(new Insets(10)); List<Serie> listeTop3 =
     * ss.findMostLiked();
     *
     * for (Serie serie : listeTop3) { VBox vbox = createSeriesVBox(serie);
     * hboxTop3.getChildren().add(vbox); }

     *
     * }

     *
     */
    @FXML
    private HBox hboxTop3;

    /**
     * @param liste
     * @param recherche
     * @return List<Serie>
     */
    // FOCTION RECHERCHE

    /**
     * Finds series whose name contains the given search string.
     *
     * @param liste     the list of Series to search
     * @param recherche the substring to match against each series' name (case-sensitive)
     * @return          a list of Series whose name contains the search string; empty if no match
     */
    public static List<Series> rechercher(final List<Series> liste, final String recherche) {
        final List<Series> resultats = new ArrayList<>();
        for (final Series element : liste) {
            if (element.getName().contains(recherche)) {
                resultats.add(element);
            }

        }

        return resultats;
    }


    /**
     * Replaces the current window's scene with the scene defined in EpisodeClient.fxml.
     *
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML
    void onWatch(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/series/EpisodeClient.fxml"));
        final Parent root = loader.load();
        final Stage stage = (Stage) this.watchEpisode.getScene().getWindow();
        stage.setScene(new Scene(root));
    }


    /**
     * Builds a vertically arranged UI box that presents a series with its image, title, and a likes label.
     *
     * @param serie the Series instance whose image and text are shown in the returned box
     * @return a configured {@code VBox} containing an ImageView, a title Label, and a likes Label
     */
    private VBox createSeriesVBox(final Series serie) {
        final VBox vbox = new VBox();
        vbox.setSpacing(8);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(8));
        vbox.setMinSize(250, 210);
        // Créez d'abord le Label avec le nom
        final Label titleLabel = new Label(serie.getName());
        titleLabel.setStyle(
                "-fx-font-family: 'Helvetica'; -fx-font-size: 18.0px; -fx-font-weight: bold; -fx-text-fill: #FCE19A;");
        titleLabel.setAlignment(Pos.CENTER);
        // Ensuite, ajoutez l'ImageView avec l'image
        final ImageView imageView = new ImageView();
        imageView.setFitWidth(240);
        imageView.setFitHeight(140);
        // Chargez l'image depuis le fichier
        final File file = new File(serie.getImage());
        final Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
        // Créer le Label pour afficher le nombre de likes
        final Label likesLabel = new Label("Likes: " + serie.getCountry());
        likesLabel.setStyle("-fx-font-family: 'Helvetica'; -fx-font-size: 14.0px; -fx-text-fill: #FCE19A;");
        likesLabel.setAlignment(Pos.CENTER);
        // Ajoutez d'abord le Label, puis l'ImageView à la liste des enfants
        // vbox.getChildren().addAll(titleLabel, imageView);
        vbox.getChildren().addAll(imageView, titleLabel, likesLabel);
        vbox.setStyle("-fx-background-color: linear-gradient(to top right, #ae2d3c, black);");
        return vbox;
    }


    /**
     * Load the controller's categorieList with categories fetched from the data source.
     *
     * Fetches page 0 with a size of 10 and assigns the page content to the categorieList field.
     *
     * @throws SQLException if reading categories from the data source fails
     */
    public void afficher() throws SQLException {
        final IServiceCategorieImpl iServiceCategorie = new IServiceCategorieImpl();
        PageRequest pageRequest = new PageRequest(0, 10);
        this.categorieList = iServiceCategorie.read(pageRequest).getContent();
    }


    /**
     * Sorts the given list of Series in-place by their name, case-insensitively.
     *
     * @param series the list of Series to sort by name (modified in-place)
     */
    private void trierParNom(final List<Series> series) {
        Collections.sort(series, (serie1, serie2) -> serie1.getName().compareToIgnoreCase(serie2.getName()));
    }


    /**
     * Populate the ListView with the given series and configure each list cell's UI and action handlers.
     *
     * @param series list of Series instances to display; each item is rendered with its image, metadata
     *               (name, director, country, likes/dislikes), and controls for like, dislike, favorite,
     *               and watch. The method appends separators between items when opening a series for watch.
     */
    public void afficherliste(final List<Series> series) {
        this.listeSerie.getItems().clear();
        this.listeSerie.setCellFactory(param -> new ListCell<Series>() {
            /**
             * Populate the cell's graphic with the given Series' details and interactive controls.
             *
             * When {@code empty} is true or {@code item} is null the cell graphic is cleared;
             * otherwise the method builds and sets a UI containing the series image, metadata
             * (name, director, country, likes/dislikes) and interactive buttons (like, dislike,
             * favorite, watch) with their associated handlers.
             *
             * @param item  the Series to render in this cell (may be null)
             * @param empty true if the cell should be empty and its graphic cleared
             */
            @Override
            protected void updateItem(final Series item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    this.setGraphic(null);
                }
 else {
                    // Créez un AnchorPane pour chaque série
                    final AnchorPane anchorPane = new AnchorPane();
                    anchorPane.setPrefSize(400, 200); // Définissez la taille souhaitée
                    // Ajoutez une ImageView pour afficher l'image
                    final ImageView imageView = new ImageView();
                    // imageView.setFitWidth(150);
                    // imageView.setFitHeight(150);
                    imageView.setFitWidth(150);
                    imageView.setFitWidth(150);
                    imageView.setPreserveRatio(true);
                    // Chargez l'image depuis le fichier
                    final File file = new File(item.getImage());
                    final Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                    // Ajoutez des composants à l'AnchorPane (toutes les informations de la série)
                    final Label nameLabel = new Label("Name: " + item.getName());
                    nameLabel.setStyle(
                            "-fx-font-family: 'Helvetica'; -fx-font-size: 18.0px; -fx-font-weight: bold; -fx-text-fill: #333333;"); // Couleur
                    // de
                    // texte
                    // sombre
                    final Label directorLabel = new Label("Director: " + item.getDirector());
                    directorLabel.setStyle(
                            "-fx-font-family: 'Helvetica'; -fx-font-size: 14.0px; -fx-font-weight: normal; -fx-text-fill: #666666;"); // Couleur
                    // de
                    // texte
                    // sombre
                    // plus
                    // claire
                    final Label countryLabel = new Label("Country: " + item.getCountry());
                    countryLabel.setStyle(
                            "-fx-font-family: 'Helvetica'; -fx-font-size: 14.0px; -fx-font-weight: normal; -fx-text-fill: #666666;"); // Couleur
                    // de
                    // texte
                    // sombre
                    // plus
                    // claire
                    // Label summaryLabel = new Label("Summary: " + item.getResume());
                    // Label directorLabel = new Label("Director: " + item.getDirecteur());
                    // Label countryLabel = new Label("Country: " + item.getPays());
                    final Image iconHeart = new Image("img/films/heart.png");
                    final ImageView HeartImageView = new ImageView(iconHeart);
                    HeartImageView.setFitWidth(10.0);
                    HeartImageView.setFitHeight(10.0);
                    final Label likesLabel = new Label("Likes: " + item.getNumberOfLikes());
                    likesLabel.setStyle(
                            "-fx-font-family: 'Helvetica'; -fx-font-size: 14.0px; -fx-font-weight: normal; -fx-text-fill: #666666;"); // Couleur
                    // de
                    // texte
                    // sombre
                    // plus
                    // claire
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
                    /* Button Like + Dislike Declaration */
                    final Image iconImage2 = new Image("img/films/dislike.png");
                    final ImageView iconImageView2 = new ImageView(iconImage2);
                    iconImageView2.setFitWidth(20.0);
                    iconImageView2.setFitHeight(20.0);
                    final Button dislikeButton = new Button("", iconImageView2);
                    // Set the layout constraints for the Button in the AnchorPane
                    AnchorPane.setTopAnchor(dislikeButton, 10.0);
                    AnchorPane.setRightAnchor(dislikeButton, 50.0);
                    final Image iconImage = new Image("img/films/like.png");
                    final ImageView iconImageView = new ImageView(iconImage);
                    iconImageView.setFitWidth(20.0);
                    iconImageView.setFitHeight(20.0);
                    final Button likeButton = new Button("", iconImageView);
                    // Set the layout constraints for the Button in the AnchorPane
                    AnchorPane.setTopAnchor(likeButton, 10.0);
                    AnchorPane.setRightAnchor(likeButton, 10.0);
                    final Image iconFav = new Image("img/films/favoris.png");
                    final ImageView iconImageViewFav = new ImageView(iconFav);
                    iconImageViewFav.setFitWidth(20.0);
                    iconImageViewFav.setFitHeight(20.0);
                    final Button favButton = new Button("", iconImageViewFav);
                    // Set the layout constraints for the Button in the AnchorPane
                    AnchorPane.setTopAnchor(favButton, 10.0);
                    AnchorPane.setRightAnchor(favButton, 90.0);
                    /* Button Like + Dislike's Functions */
                    likeButton.setOnAction(new EventHandler<ActionEvent>() {
                        /**
                         * Toggle and persist a like for the current series item and update the related UI state.
                         *
                         * Increments the item's local click counter, adjusts its number of likes, persists the change
                         * through the series service, and enables or disables the corresponding dislike button.
                         */
                        @Override
                        /**
                         * Performs handle operation.
                         *
                         * @return the result of the operation
                         */
                        public void handle(final ActionEvent event) {
                            item.setClickLikes(item.getClickLikes() + 1);
                            SerieClientController.LOGGER.info(String.valueOf(item.getClickLikes()));
                            final IServiceSeriesImpl ss = new IServiceSeriesImpl();
                            try {
                                if ((0 == item.getClickLikes()) || (0 != item.getClickLikes() % 2)) {
                                    item.setNumberOfLikes(item.getNumberOfLikes() + 1);
                                    ss.addLike(item);
                                    dislikeButton.setDisable(true);
                                }
 else {
                                    item.setNumberOfLikes(item.getNumberOfLikes() - 1);
                                    if (0 == item.getNumberOfLikes()) {
                                        item.setLiked(0);
                                        ss.removeLike(item);
                                        dislikeButton.setDisable(false);
                                    }
 else {
                                        ss.addLike(item);
                                        dislikeButton.setDisable(true);
                                    }

                                }

                            }
 catch (final SQLException e) {
                                throw new RuntimeException(e);
                            }

                        }

                    }
);
                    dislikeButton.setOnAction(new EventHandler<ActionEvent>() {
                        /**
                         * Update the item's dislike state and persist the change when the dislike action is invoked.
                         *
                         * Increments the item's click-dislike counter, adjusts the item's numberOfDislikes and disliked flag,
                         * calls the series service to add or remove a dislike as appropriate, and toggles the like button's disabled state.
                         */
                        @Override
                        /**
                         * Performs handle operation.
                         *
                         * @return the result of the operation
                         */
                        public void handle(final ActionEvent event) {
                            item.setClickDislikes(item.getClickDislikes() + 1);
                            SerieClientController.LOGGER.info(String.valueOf(item.getClickDislikes()));
                            final IServiceSeriesImpl ss = new IServiceSeriesImpl();
                            try {
                                if ((0 == item.getClickDislikes()) || (0 != item.getClickDislikes() % 2)) {
                                    item.setNumberOfDislikes(item.getNumberOfDislikes() + 1);
                                    ss.addDislike(item);
                                    likeButton.setDisable(true);
                                }
 else {
                                    item.setNumberOfDislikes(item.getNumberOfDislikes() - 1);
                                    if (0 == item.getNumberOfDislikes()) {
                                        item.setDisliked(0);
                                        ss.removeDislike(item);
                                        likeButton.setDisable(false);
                                    }
 else {
                                        ss.addDislike(item);
                                        likeButton.setDisable(true);
                                    }

                                }

                            }
 catch (final SQLException e) {
                                throw new RuntimeException(e);
                            }

                        }

                    }
);
                    favButton.setOnAction(new EventHandler<ActionEvent>() {
                        /**
                         * Toggle the favorite state for the current client on the associated series.
                         *
                         * Reads the current Client from the window userData, updates the series' local favorite-click counter,
                         * and creates or removes a Favorite record via the favorite service so the persistent favorite state
                         * matches the toggled value.
                         *
                         * @param event the ActionEvent that triggered this handler
                         * @throws RuntimeException if a database error occurs while creating or deleting the favorite
                         */
                        @Override
                        /**
                         * Performs handle operation.
                         *
                         * @return the result of the operation
                         */
                        public void handle(final ActionEvent event) {
                            final Client client = (Client) favButton.getScene().getWindow().getUserData();
                            final Long id_serie = item.getId();
                            final IServiceFavoriteImpl sf = new IServiceFavoriteImpl();
                            final Favorite f = new Favorite(client.getId(), id_serie);
                            item.setClickFavorites(item.getClickFavorites() + 1);
                            SerieClientController.LOGGER.info(String.valueOf(item.getClickFavorites()));
                            try {
                                if ((0 == item.getClickFavorites()) || (0 != item.getClickFavorites() % 2)) {
                                    sf.create(f);
                                }
 else {
                                    final Favorite fav = sf.getByIdUserAndIdSerie(client.getId(), id_serie);
                                    sf.delete(fav);
                                    SerieClientController.LOGGER.info(String.valueOf(fav.getId().intValue()));
                                }

                            }
 catch (final SQLException e) {
                                throw new RuntimeException(e);
                            }

                        }

                    }
);
                    /* Button Watch Episode Declaration */
                    final Image iconImageWatch = new Image("img/films/watch.png");
                    final ImageView iconImageViewWatch = new ImageView(iconImageWatch);
                    iconImageViewWatch.setFitWidth(10.0);
                    iconImageViewWatch.setFitHeight(10.0);
                    final Button watchButton = new Button("Watch", iconImageViewWatch);
                    watchButton.setStyle("""
                            -fx-background-color: #ae2d3c; \
                            -fx-background-radius: 8px; \
                            -fx-text-fill: #FCE19A; \
                            -fx-font-size: 16px; \
                            -fx-font-family: 'Arial Rounded MT Bold'; \
                            -fx-cursor: hand;\
                            """); // Set the layout constraints for the Watch Button in the AnchorPane
                    AnchorPane.setTopAnchor(watchButton, 150.0);
                    AnchorPane.setLeftAnchor(watchButton, 180.0);
                    final Label dislikesLabel = new Label("DisLikes: " + item.getNumberOfDislikes());
                    dislikesLabel.setStyle(
                            "-fx-font-family: 'Helvetica'; -fx-font-size: 14.0px; -fx-font-weight: normal; -fx-text-fill: #666666;"); // Couleur
                    // de
                    // texte
                    // sombre
                    // plus
                    // claire
                    AnchorPane.setTopAnchor(dislikesLabel, 120.0);
                    AnchorPane.setLeftAnchor(dislikesLabel, 180.0);
                    // Ajoutez les composants à l'AnchorPane
                    // anchorPane.getChildren().addAll(imageView, nameLabel, summaryLabel,
                    // directorLabel, countryLabel, likesLabel, HeartImageView, likeButton,
                    // dislikeButton);
                    anchorPane.getChildren().addAll(imageView, nameLabel, directorLabel, countryLabel, likesLabel,
                            HeartImageView, likeButton, dislikeButton, favButton, watchButton, dislikesLabel);
                    // Ajoutez d'autres composants selon vos besoins
                    // Définissez l'AnchorPane en tant que graphique pour la cellule
                    this.setGraphic(anchorPane);
                    watchButton.setOnAction(event -> {
                        final FXMLLoader fxmlLoader = new FXMLLoader(
                                this.getClass().getResource("/ui/series/EpisodeClient.fxml"));
                        final Stage stage = (Stage) watchButton.getScene().getWindow();
                        try {
                            final Parent root = fxmlLoader.load();
                            final EpisodeClientController controller = fxmlLoader.getController();
                            controller.initialize(item);
                            final Scene scene = new Scene(root);
                            stage.setTitle("");
                            stage.setScene(scene);
                            stage.show();
                        }
 catch (final IOException e) {
                            throw new RuntimeException(e);
                        }

                        // Ajoutez un séparateur après chaque élément sauf le dernier
                        final Separator separator = new Separator();
                        separator.setPrefWidth(400); // Ajustez la largeur selon vos besoins
                        separator.setStyle("-fx-border-color: #ae2d3c; -fx-border-width: 2px;");
                        final VBox vBoxWithSeparator = new VBox(anchorPane, separator);
                        this.setGraphic(vBoxWithSeparator);
                    }
);
                }

            }

        }
);
        this.listeSerie.getItems().addAll(series);
    }


    /**
     * Opens a new window titled "Your Favorits" and displays the UI defined in /ui/series/ListFavoris.fxml.
     */
    @FXML
    void AfficherFavList(final ActionEvent event) {
        final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/series/ListFavoris.fxml"));
        final Stage stage = new Stage();
        try {
            final Parent root = fxmlLoader.load();
            final Scene scene = new Scene(root);
            stage.setTitle("Your Favorits");
            stage.setScene(scene);
            stage.show();
        }
 catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Loads a list of series, sets an observable list of category names, and
     * implements an OnAction event to retrieve and display a specific series when
     * the category is selected from the dropdown menu. It also adds a listener to
     * the text field for searching series by name.
     */

    /**
     * Set up the controller's initial UI state: load series, populate the category combobox,
     * and attach listeners for category selection, live search, and series selection.
     *
     * <p>Loads the initial series list and category data, configures the category ComboBox action
     * to filter series by category, adds a text-change listener on the search field to filter
     * displayed series, and adds a selection listener on the series ListView to open the
     * episode view for the selected series.</p>
     *
     * @throws SQLException if loading series or category data from the service layer fails
     */
    public void initialize() throws SQLException {
        this.loadSeriesList();
        final IServiceSeriesImpl iServiceSerie = new IServiceSeriesImpl();
        final IServiceCategorieImpl iServiceCategorie = new IServiceCategorieImpl();
        PageRequest pageRequest = new PageRequest(0, 10);
        final List<Category> categorieList = iServiceCategorie.read(pageRequest).getContent();
        final ObservableList<Category> categorieObservableList = FXCollections.observableArrayList();
        if (CamboxCategorie != null) {
            this.CamboxCategorie.setItems(categorieObservableList);
        }

        this.CamboxCategorie.setOnAction(event -> {
            final Category selectedCategorie = this.CamboxCategorie.getValue();
            if (!this.selectedCategories.contains(selectedCategorie)) {
                for (final Category c : categorieList) {
                    if (c.getName() == selectedCategorie.getName()) {
                        try {
                            this.listerecherche = iServiceSerie.retrieveByCategory(selectedCategorie.getId());
                            this.trierParNom(this.listerecherche); // Tri des séries par nom
                            this.afficherliste(this.listerecherche);
                        }
 catch (final SQLException e) {
                            throw new RuntimeException(e);
                        }

                    }

                }

            }

        }
);
        /// fonction recherche sur textfiled
        this.recherchefld.textProperty().addListener((observable, oldValue, newValue) -> {
            final List<Series> series;
            series = SerieClientController.rechercher(this.listerecherche, newValue);
            this.afficherliste(series);
        }
);
        this.listeSerie.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (0 <= newValue.intValue()) {
                final Series selectedSerie = this.listeSerie.getItems().get(newValue.intValue());
                final FXMLLoader fxmlLoader = new FXMLLoader(
                        this.getClass().getResource("/ui/series/EpisodeClient.fxml"));
                SerieClientController.LOGGER.info("serieee " + this.listeSerie.getScene().getWindow().getUserData());
                final Stage stage = (Stage) this.listeSerie.getScene().getWindow();
                try {
                    final Parent root = fxmlLoader.load();
                    final EpisodeClientController controller = fxmlLoader.getController();
                    controller.initialize(selectedSerie);
                    final Scene scene = new Scene(root);
                    stage.setTitle("");
                    stage.setScene(scene);
                    stage.show();
                }
 catch (final IOException e) {
                    throw new RuntimeException(e);
                }

            }

        }
);
    }


    /// gestion de menu
    /**
     * Replaces the current window's scene with the CategorieClient FXML view.
     *
     * @throws IOException if the FXML resource cannot be loaded or parsed
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
     * Switches the current window to the SeriesClient scene.
     *
     * @param event the action event that triggered the scene change (typically a UI control)
     * @throws IOException if the SeriesClient FXML resource cannot be loaded
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
     * Replaces the current window's scene with the EpisodeClient view and shows it.
     *
     * @param event the action event whose source Node is used to obtain the Window whose scene will be replaced
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


    /**
     * Load series from the service and populate the main list and top-3 UI area.
     *
     * Fetches the first page of series and displays them in the controller's list view,
     * then retrieves the most-liked series and adds a generated VBox for each into
     * the top-3 HBox container.
     *
     * @throws SQLException if an error occurs while retrieving series from the database
     */
    @FXML
    private void loadSeriesList() throws SQLException {
        final IServiceSeriesImpl serieService = new IServiceSeriesImpl();
        PageRequest pageRequest = new PageRequest(0, 10);
        final List<Series> series = serieService.read(pageRequest).getContent();
        this.afficherliste(series); // Utilisez votre méthode d'affichage pour la ListView
        final IServiceSeriesImpl ss = new IServiceSeriesImpl();
        this.hboxTop3.setSpacing(20); // Set the spacing between VBox instances
        this.hboxTop3.setPadding(new Insets(10));
        final List<Series> listeTop3 = ss.findMostLiked();
        for (final Series serie : listeTop3) {
            final VBox vbox = this.createSeriesVBox(serie);
            this.hboxTop3.getChildren().add(vbox);
        }

    }

}
