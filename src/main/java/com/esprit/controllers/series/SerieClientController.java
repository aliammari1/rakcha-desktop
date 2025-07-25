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
     * setGraphic(imageView); } } }); }
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
     * Searches for Series in a list based on a specified search term and returns a
     * list of matching Series.
     *
     * @param liste
     *                  list of series that is searched for matches in the
     *                  `recherche`
     *                  string.
     *                  <p>
     *                  - `liste` is a list of `Serie` objects.
     * @param recherche
     *                  search query, which is used to filter the list of series and
     *                  return only the matches.
     * @returns a list of `Serie` objects that match the given search query.
     *          <p>
     *          - The output is a list of `Serie` objects, which represents the list
     *          of series that match the search query. - Each element in the list
     *          contains the `Nom` attribute, which contains the name of the series.
     *          - If the `Nom` attribute matches the search query, the element is
     *          included in the output list.
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
     * Loads an FXML file, `EpisodeClient.fxml`, and creates a new stage with its
     * scene.
     *
     * @param event
     *              event that triggered the function, specifically an action event
     *              related to the `watchEpisode` button.
     *              <p>
     *              Event: ActionEvent
     *              <p>
     *              - Target: The object that triggered the event (not shown)
     */
    @FXML
    void onWatch(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/series/EpisodeClient.fxml"));
        final Parent root = loader.load();
        final Stage stage = (Stage) this.watchEpisode.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    /**
     * Creates a Box object with a layout that displays an image and text
     * information for a series. It takes a serie object as input, generates a VBox
     * object with appropriate spacing, alignment, and padding, and adds three
     * children to it: an ImageView with the series' image, a Label with the series'
     * name, and another Label with the number of likes. The function then styles
     * the Box and returns it.
     *
     * @param serie
     *              data structure containing information about a specific series,
     *              which is used to create a graphical representation of the series
     *              within the `VBox`.
     *              <p>
     *              - `nom`: The name of the series. - `image`: The path to the
     *              image
     *              associated with the series. - `nbLikes`: The number of likes for
     *              the series.
     * @returns a vertical box container with a centered label, an image view, and a
     *          label displaying the number of likes.
     *          <p>
     *          1/ `vbox`: A `VBox` object that contains the series information and
     *          image. 2/ `spacing`: The spacing between the children in the `VBox`.
     *          3/ `alignment`: The alignment of the children in the `VBox`. 4/
     *          `padding`: The padding around the children in the `VBox`. 5/
     *          `minSize`: The minimum size of the `VBox`. 6/ `children`: A list of
     *          `Node` objects, including a `Label`, an `ImageView`, and another
     *          `Label`, which contain the series information and image.
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
     * Retrieves a list of categories from an implementation of the
     * `IServiceCategorieImpl` interface.
     */
    public void afficher() throws SQLException {
        final IServiceCategorieImpl iServiceCategorie = new IServiceCategorieImpl();
        PageRequest pageRequest = new PageRequest(0, 10);
        this.categorieList = iServiceCategorie.read(pageRequest).getContent();
    }

    /**
     * Sorts a list of `Serie` objects based on their `nom` attribute, using a
     * custom comparison method that ignores case and returns an integer value
     * indicating the result of the comparison.
     *
     * @param series
     *               list of series to be sorted, which is passed through the
     *               `Collections.sort()` method using a custom comparison algorithm
     *               based on the nomenclature of each series.
     */
    private void trierParNom(final List<Series> series) {
        Collections.sort(series, (serie1, serie2) -> serie1.getName().compareToIgnoreCase(serie2.getName()));
    }

    /**
     * Displays a list of series from an API, along with buttons to watch or dislike
     * each series, and a separator line after every element except the last one.
     *
     * @param series
     *               list of series that will be displayed in the client's profile,
     *               and
     *               it is used to populate the `listeSerie` observable list which
     *               is
     *               then passed as an argument to the `FXMLLoader` constructor to
     *               load
     *               the FXML file.
     *               <p>
     *               - `id_serie`: an integer representing the ID of the series. -
     *               `name`: a string representing the name of the series. -
     *               `description`: a string representing the description of the
     *               series. - `director`: a string representing the director of the
     *               series. - `country`: a string representing the country where
     *               the
     *               series is from. - `year`: an integer representing the year the
     *               series was released. - `poster_path`: a string representing the
     *               path to the poster image of the series. - `clicks`: an integer
     *               representing the number of clicks on the series. - `favoris`:
     *               an
     *               integer representing the number of favorites for the series. -
     *               `nb_dislikes`: an integer representing the number of dislikes
     *               for
     *               the series.
     */
    public void afficherliste(final List<Series> series) {
        this.listeSerie.getItems().clear();
        this.listeSerie.setCellFactory(param -> new ListCell<Series>() {
            /**
             * Updates the UI components displaying information about a movie, including its
             * image, name, director, country, likes, and dislikes. It also adds a "Watch"
             * button to watch the movie and a "Dislike" button to remove it from the
             * favorite list.
             *
             * @param item
             *              `Episode` object that will be used to display its details and
             *              likes/dislikes count on the stage, and it is passed as a
             *              parameter
             *              to the `initView()` method to facilitate the retrieval of the
             *              necessary data from the `EpisodeClientController` controller.
             *
             *              - `id`: the unique identifier for the episode - `name`: the name
             *              of the episode - `director`: the director of the episode -
             *              `country`: the country where the episode was produced - `likes`:
             *              the number of likes for the episode - `dislikes`: the number of
             *              dislikes for the episode - `HeartImageView`: an image view
             *              displaying a heart for liked episodes - `likeButton`: a button
             *              to
             *              like the episode - `dislikeButton`: a button to dislike the
             *              episode - `favButton`: a button to add the episode to the user's
             *              favorites - `watchButton`: a button to watch the episode.
             *
             * @param empty
             *              AnchorPanes' graphic element, which is set to an empty
             *              `AnchorPane` when the function is called with no arguments,
             *              causing the `likeButton`, `dislikeButton`, and `watchButton` to
             *              be
             *              displayed without any content.
             */
            @Override
            protected void updateItem(final Series item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    this.setGraphic(null);
                } else {
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
                         * Updates the number of likes for an item, adds or removes a like from the
                         * item's liked status, and enables or disables the dislike button based on the
                         * item's liked status.
                         *
                         * @param event
                         *              action event triggered by a user's click on the like button, and
                         *              it is passed to the function as an argument to enable the
                         *              updating
                         *              of the item's likes count.
                         *
                         *              - `item`: the current item being processed. - `clickLikes`: the
                         *              number of clicks on the item. - `NbLikes`: the total number of
                         *              likes for the item.
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
                                } else {
                                    item.setNumberOfLikes(item.getNumberOfLikes() - 1);
                                    if (0 == item.getNumberOfLikes()) {
                                        item.setLiked(0);
                                        ss.removeLike(item);
                                        dislikeButton.setDisable(false);
                                    } else {
                                        ss.addLike(item);
                                        dislikeButton.setDisable(true);
                                    }
                                }
                            } catch (final SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    dislikeButton.setOnAction(new EventHandler<ActionEvent>() {
                        /**
                         * Increments the number of dislikes for an item when the like button is
                         * clicked, and updates the item's liked status accordingly. It also calls a
                         * service method to add or remove a dislike from the item in the database.
                         *
                         * @param event
                         *              event that triggered the function, and it is used to increment
                         *              the
                         *              number of clicks on an item and print its current click dislike
                         *              count.
                         *
                         *              - `item`: The current item being processed, which contains
                         *              information such as its ID and click dislikes count.
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
                                } else {
                                    item.setNumberOfDislikes(item.getNumberOfDislikes() - 1);
                                    if (0 == item.getNumberOfDislikes()) {
                                        item.setDisliked(0);
                                        ss.removeDislike(item);
                                        likeButton.setDisable(false);
                                    } else {
                                        ss.addDislike(item);
                                        likeButton.setDisable(true);
                                    }
                                }
                            } catch (final SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    favButton.setOnAction(new EventHandler<ActionEvent>() {
                        /**
                         * Is an implementation of an action listener for a favorites button. It
                         * retrieves the client and series ID from the button's user data, creates a new
                         * or updates an existing favorite, and adds it to a service implementing the
                         * `IServiceFavoris` interface. The function also handles the favorite count and
                         * prints it to the console.
                         *
                         * @param event
                         *              ActionEvent object that triggered the handling of the method.
                         *
                         *              - `favButton`: The button that triggered the event, which
                         *              represents a client. - `item`: The object passed as an argument
                         *              to
                         *              the function, containing information about the favorite.
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
                                } else {
                                    final Favorite fav = sf.getByIdUserAndIdSerie(client.getId(), id_serie);
                                    sf.delete(fav);
                                    SerieClientController.LOGGER.info(String.valueOf(fav.getId().intValue()));
                                }
                            } catch (final SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
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
                    });
                }
            }
        });
        this.listeSerie.getItems().addAll(series);
    }

    /**
     * Loads a FXML file named `ListFavoris.fxml`, creates a new stage with the
     * loaded scene, and displays it in the stage.
     *
     * @param event
     *              event of the button click that triggered the function execution.
     *              <p>
     *              - Event type: `ActionEvent` - Target object: none (the function
     *              is
     *              called directly without any target object)
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
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
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
        });
        /// fonction recherche sur textfiled
        this.recherchefld.textProperty().addListener((observable, oldValue, newValue) -> {
            final List<Series> series;
            series = SerieClientController.rechercher(this.listerecherche, newValue);
            this.afficherliste(series);
        });
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
        });
    }

    /// gestion de menu
    /**
     * Loads and displays a FXML file named "/ui//ui/CategorieClient.fxml" using the
     * `FXMLLoader` class.
     *
     * @param event
     *              event that triggered the function execution, which in this case
     *              is
     *              a button click event.
     *              <p>
     *              - `event`: An `ActionEvent` object representing a user action
     *              that
     *              triggered the function to execute.
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
     * Loads a FXML file, creates a scene and sets it as the scene of a stage,
     * showing the stage.
     *
     * @param event
     *              event that triggered the function execution, which in this case
     *              is
     *              a button click.
     *              <p>
     *              - `event` represents an action event generated by the user's
     *              interaction with the application. - `getSource()` returns the
     *              source of the event, which is typically a button or other
     *              control
     *              in the user interface.
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
     * Loads an FXML file, creates a scene, sets it as the scene of a stage, and
     * displays the stage.
     *
     * @param event
     *              event that triggered the execution of the `Oepisode` method,
     *              providing the necessary information for the method to perform
     *              its
     *              intended action.
     *              <p>
     *              Event: An instance of the `ActionEvent` class, representing an
     *              action event generated by the user through mouse clicks or
     *              keyboard inputs.
     *              <p>
     *              Properties:
     *              <p>
     *              - `getSource()`: Returns the source of the event, which is
     *              usually
     *              a button or a menu item that was clicked/selected.
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
     * Retrieves a list of series from a database using an implementation of
     * `IServiceSerie`, then displays the list in a `ListView`. It also calls
     * another method, `findMostLiked`, on the same implementation to retrieve a
     * list of top-rated series and adds them to the `ListView`.
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
