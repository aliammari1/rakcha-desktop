package com.esprit.controllers.series;
import com.esprit.models.series.Favoris;
import com.esprit.models.series.Serie;
import com.esprit.services.series.IServiceFavorisImpl;
import com.esprit.services.series.IServiceSerieImpl;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
/**
 * Is responsible for handling user actions related to favorite series. It displays
 * a list of series in an AnchorPane and provides buttons to watch, dislike, and add
 * to favorites. The controller also loads the favorite series list from the database
 * using the IServiceSerieImpl and IServiceFavorisImpl interfaces.
 */
public class ListFavorisController implements Initializable {
    private final int idSession = 1;
    @FXML
    private ListView<Serie> listViewFav;
    /**
     * Initializes a series fav list by loading it from a database using a SQLException-catching
     * mechanism if an error occurs.
     * 
     * @param url URL of the web page from which the user's favorite series should be loaded.
     * 
     * 	- `URL`: represents a web address or a URL, which provides access to a specific
     * resource or service on the internet.
     * 
     * @param resourceBundle localized data for the application, which is used to load
     * the series favor list.
     * 
     * 	- `URL`: represents the URL of the web page that contains the series favorites
     * list data
     * 	- `ResourceBundle`: is a collection of culturally-specific data, including messages,
     * labels, and other information.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadSeriesFavList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Displays a list of series with buttons for liking, disliking, and watching each
     * series. When a button is clicked, the corresponding action is performed on the
     * series data model.
     * 
     * @param series 2D array of series data that will be displayed in the list view, and
     * it is used to populate the list view with the appropriate series items.
     * 
     * 	- `id`: an integer representing the unique identifier of the series.
     * 	- `nom`: a string representing the name of the series.
     * 	- `genre`: a string representing the genre of the series (e.g., "drama", "comedy",
     * etc.).
     * 	- `description`: a string representing a brief description of the series.
     * 	- `poster`: a URL or image path representing the poster image for the series.
     * 	- `nbEpisodes`: an integer representing the number of episodes in the series.
     */
    public void afficherliste(List<Serie> series) {
        listViewFav.getItems().clear();
        listViewFav.setCellFactory(param -> new ListCell<Serie>() {
            /**
             * Updates an item's information based on user interactions with like and dislike
             * buttons, favorites button, and a watch button. It retrieves data from a database
             * and updates the item's likes and dislikes counts, favorites count, and watch status
             * accordingly.
             * 
             * @param item current episode being displayed, and it is used to access its properties
             * such as title, image, likes count, dislikes count, and favorite status, which are
             * then displayed on the user interface.
             * 
             * 	- `id serie`: The id of the series to which the item belongs.
             * 	- `name`: The name of the episode.
             * 	- `summary`: A brief summary of the episode.
             * 	- `director`: The director of the episode.
             * 	- `country`: The country where the episode was produced.
             * 	- `likes`: The number of likes received by the episode.
             * 	- `dislikes`: The number of dislikes received by the episode.
             * 	- `clickDislikes`: The number of times the dislike button has been clicked.
             * 	- `NbDislikes`: The total number of dislikes received by the episode.
             * 	- `clickFavoris`: The number of times the favorite button has been clicked.
             * 	- `NbFavoris`: The total number of favorites for the episode.
             * 
             * @param empty empty stage, which is used to display the watch episode button when
             * the user clicks on it.
             */
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
                    // imageView.setFitWidth(150);
                    // imageView.setFitHeight(150);
                    imageView.setFitWidth(150);
                    imageView.setFitWidth(150);
                    imageView.setPreserveRatio(true);
                    // Chargez l'image depuis le fichier
                    File file = new File(item.getImage());
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                    // Ajoutez des composants à l'AnchorPane (toutes les informations de la série)
                    Label nameLabel = new Label("Name: " + item.getNom());
                    nameLabel.setStyle(
                            "-fx-font-family: 'Helvetica'; -fx-font-size: 18.0px; -fx-font-weight: bold; -fx-text-fill: #333333;"); // Couleur
                                                                                                                                    // de
                                                                                                                                    // texte
                                                                                                                                    // sombre
                    // Label summaryLabel = new Label("Summary: " + item.getResume());
                    // Label directorLabel = new Label("Director: " + item.getDirecteur());
                    // Label countryLabel = new Label("Country: " + item.getPays());
                    Image iconHeart = new Image("img/films/heart.png");
                    ImageView HeartImageView = new ImageView(iconHeart);
                    HeartImageView.setFitWidth(10.0);
                    HeartImageView.setFitHeight(10.0);
                    Label likesLabel = new Label("Likes: " + item.getNbLikes());
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
                    AnchorPane.setTopAnchor(likesLabel, 60.0);
                    AnchorPane.setLeftAnchor(likesLabel, 180.0);
                    AnchorPane.setTopAnchor(HeartImageView, 65.0);
                    AnchorPane.setLeftAnchor(HeartImageView, 230.0);
                    // Positionnez d'autres composants selon vos besoins
                    /* Button Like + Dislike Declaration */
                    Image iconImage2 = new Image("img/films/dislike.png");
                    ImageView iconImageView2 = new ImageView(iconImage2);
                    iconImageView2.setFitWidth(20.0);
                    iconImageView2.setFitHeight(20.0);
                    Button dislikeButton = new Button("", iconImageView2);
                    // Set the layout constraints for the Button in the AnchorPane
                    AnchorPane.setTopAnchor(dislikeButton, 10.0);
                    AnchorPane.setRightAnchor(dislikeButton, 50.0);
                    Image iconImage = new Image("img/films/like.png");
                    ImageView iconImageView = new ImageView(iconImage);
                    iconImageView.setFitWidth(20.0);
                    iconImageView.setFitHeight(20.0);
                    Button likeButton = new Button("", iconImageView);
                    // Set the layout constraints for the Button in the AnchorPane
                    AnchorPane.setTopAnchor(likeButton, 10.0);
                    AnchorPane.setRightAnchor(likeButton, 10.0);
                    Image iconFav = new Image("img/films/heart.png");
                    ImageView iconImageViewFav = new ImageView(iconFav);
                    iconImageViewFav.setFitWidth(20.0);
                    iconImageViewFav.setFitHeight(20.0);
                    Button favButton = new Button("", iconImageViewFav);
                    // Set the layout constraints for the Button in the AnchorPane
                    AnchorPane.setTopAnchor(favButton, 10.0);
                    AnchorPane.setRightAnchor(favButton, 90.0);
                    /* Button Like + Dislike's Functions */
                    likeButton.setOnAction(new EventHandler<ActionEvent>() {
                        /**
                         * Increments or decrements the number of likes for a given item based on its current
                         * like count and parity, and updates the liked status of the item in the database.
                         * 
                         * @param event ActionEvent object that triggered the function, providing the context
                         * and event information for the handling of likes and dislikes.
                         * 
                         * 	- `item`: an instance of the `Item` class representing the item that triggered
                         * the event
                         * 	- `likes`: the total number of likes for the item
                         * 
                         * These properties are used in the function to increment the number of likes and
                         * print it, as well as to check if the item has been liked or disliked.
                         */
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
                        /**
                         * Updates the number of dislikes for an item based on a condition, adds or removes
                         * the item from a list, and disables the like button accordingly.
                         * 
                         * @param event event generated by the click of a button, which triggers the execution
                         * of the `handle()` method and the updating of the item's dislike count.
                         * 
                         * 	- `event` represents an action event that triggers the function's execution.
                         * 	- `item` is the object on which the action was performed, containing information
                         * such as click count and likes/dislikes counter.
                         */
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
                        /**
                         * Updates a `Favoris` object with an id_user and id_serie, adds or removes it from
                         * a database based on a condition, and prints the updated value to the console.
                         * 
                         * @param event ActionEvent object that triggered the function execution, providing
                         * the necessary information to handle the corresponding action.
                         * 
                         * 	- `event`: an instance of `ActionEvent`, representing an action event triggered
                         * by a user interaction with the application.
                         */
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
                    /* Button Watch Episode Declaration */
                    Image iconImageWatch = new Image("img/films/watch.png");
                    ImageView iconImageViewWatch = new ImageView(iconImageWatch);
                    iconImageViewWatch.setFitWidth(10.0);
                    iconImageViewWatch.setFitHeight(10.0);
                    Button watchButton = new Button("Watch", iconImageViewWatch);
                    watchButton.setStyle("-fx-background-color: #ae2d3c; " +
                            "-fx-background-radius: 8px; " +
                            "-fx-text-fill: #FCE19A; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-family: 'Arial Rounded MT Bold'; " +
                            "-fx-cursor: hand;"); // Set the layout constraints for the Watch Button in the AnchorPane
                    AnchorPane.setTopAnchor(watchButton, 120.0);
                    AnchorPane.setLeftAnchor(watchButton, 180.0);
                    Label dislikesLabel = new Label("DisLikes: " + item.getNbDislikes());
                    dislikesLabel.setStyle(
                            "-fx-font-family: 'Helvetica'; -fx-font-size: 14.0px; -fx-font-weight: normal; -fx-text-fill: #666666;"); // Couleur
                                                                                                                                      // de
                                                                                                                                      // texte
                                                                                                                                      // sombre
                                                                                                                                      // plus
                                                                                                                                      // claire
                    AnchorPane.setTopAnchor(dislikesLabel, 80.0);
                    AnchorPane.setLeftAnchor(dislikesLabel, 180.0);
                    // Ajoutez les composants à l'AnchorPane
                    // anchorPane.getChildren().addAll(imageView, nameLabel, summaryLabel,
                    // directorLabel, countryLabel, likesLabel, HeartImageView, likeButton,
                    // dislikeButton);
                    anchorPane.getChildren().addAll(imageView, nameLabel, likesLabel, HeartImageView, likeButton,
                            dislikeButton, favButton, watchButton, dislikesLabel);
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
                    });
                }
            }
        });
        listViewFav.getItems().addAll(series);
    }
    /**
     * Retrieves a list of series from a database based on the current session ID, and
     * then loops through the list of favorite series to retrieve the corresponding series
     * object from a separate service implementation, finally displaying the combined
     * list of series in the UI.
     */
    @FXML
    private void loadSeriesFavList() throws SQLException {
        IServiceSerieImpl ss = new IServiceSerieImpl();
        IServiceFavorisImpl sf = new IServiceFavorisImpl();
        List<Serie> listSerieById = new ArrayList<>();
        List<Favoris> listFav = sf.afficherListeFavoris(idSession);
        for (int i = 0; i < listFav.size(); i++) {
            Serie s = ss.getByIdSerie(listFav.get(i).getId_serie());
            listSerieById.add(s);
        }
        afficherliste(listSerieById);
    }
}
