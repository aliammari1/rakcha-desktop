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
     * Switches the current window to the scene defined in EpisodeClient.fxml.
     *
     * @throws IOException if the EpisodeClient.fxml resource cannot be loaded
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
     * Populate the controller's categorieList by fetching categories from the data source.
     *
     * Fetches the first page (page 0) with a page size of 10 and assigns the page content to the
     * categorieList field.
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
     * Render the provided series into the ListView and configure each cell's display and interactive controls.
     *
     * <p>Each list cell shows the series image and metadata (name, director, country, likes/dislikes) and provides
     * controls for liking, disliking, favoriting, and watching. Control actions update the cell UI state and persist
     * changes through the application's services; activating "Watch" opens the episode view for that series and the
     * cell may include a visual separator when the episode view is opened.</p>
     *
     * @param series the list of Series instances to display in the ListView
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
                         * Toggle the current series' like state, persist the change, and update related UI controls.
                         *
                         * Increments or decrements the series' like count and enables or disables the corresponding dislike button to reflect the new state.
                         *
                         * @param event the action event that triggered this handler
                         * @throws RuntimeException if persisting the like change fails
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

                            } catch (final SQLException e) {
                                throw new RuntimeException(e);
                            }

                        }

                    }
);
                    dislikeButton.setOnAction(new EventHandler<ActionEvent>() {
                        /**
                         * Toggle the series item's dislike state, persist the change, and update related UI controls.
                         *
                         * Updates the item's internal click counter and dislike count, calls the series service to add or remove a dislike,
                         * and enables or disables the like button to reflect the current state.
                         *
                         * @param event the action event that triggered the dislike handling
                         * @throws RuntimeException if persisting the dislike change fails
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

                            } catch (final SQLException e) {
                                throw new RuntimeException(e);
                            }

                        }

                    }
);
                    favButton.setOnAction(new EventHandler<ActionEvent>() {
                        /**
                         * Toggle the favorite state for the current client on the associated series.
                         *
                         * Updates the series' local favorite click counter and creates or deletes a Favorite record
                         * so the persistent favorite state matches the toggled value.
                         *
                         * @param event the ActionEvent triggered by the favorite button
                         * @throws RuntimeException if a persistence error occurs while creating or deleting the favorite
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

                            } catch (final SQLException e) {
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
                        } catch (final IOException e) {
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
        } catch (final IOException e) {
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
     * Initialize the controller's UI state by loading series, populating the category ComboBox,
     * and wiring handlers for category selection, live search, and series selection.
     *
     * <p>Loads initial series and category data and configures UI listeners so category changes
     * filter the displayed series, the search field filters results dynamically, and selecting
     * a series opens the episode view for that series.</p>
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
                        } catch (final SQLException e) {
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
                } catch (final IOException e) {
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
         * Load and display the initial series list and populate the top-3 most-liked series area.
         *
         * Populates the controller's ListView with the primary series page and fills the top-3 HBox
         * with VBoxes representing the most-liked series.
         *
         * @throws SQLException if an error occurs while retrieving series from the data source
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