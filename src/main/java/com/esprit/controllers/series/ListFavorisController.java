package com.esprit.controllers.series;

import com.esprit.models.series.Favorite;
import com.esprit.models.series.Series;
import com.esprit.services.series.FavoriteService;
import com.esprit.services.series.SeriesService;
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
import java.util.logging.Logger;

/**
 * Is responsible for handling user actions related to favorite series. It
 * displays a list of series in an AnchorPane and provides buttons to watch,
 * dislike, and add to favorites. The controller also loads the favorite series
 * list from the database using the IServiceSerieImpl and IServiceFavorisImpl
 * interfaces.
 */
public class ListFavorisController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ListFavorisController.class.getName());
    private final int idSession = 1;
    @FXML
    private ListView<Series> listViewFav;

    /**
     * Load the current session's favorite series into the ListView.
     *
     * @param url            location used to resolve relative paths for the root object, may be null
     * @param resourceBundle resources for localization, may be null
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        try {
            this.loadSeriesFavList();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Render the provided favorite Series objects into the ListView and attach per-item controls for like, dislike, favorite, and watch.
     *
     * @param series the list of Series to display; each element becomes a ListView cell with interactive controls that update and persist user actions
     */
    public void afficherliste(final List<Series> series) {
        this.listViewFav.getItems().clear();
        this.listViewFav.setCellFactory(param -> new ListCell<Series>() {
                /**
                 * Populate the cell's graphic to display the given Series and attach UI controls
                 * for liking, disliking, favoriting, and watching that series.
                 *
                 * The method updates the cell to show the series' image, title and current like/dislike
                 * counts and wires button handlers that persist user actions (likes, dislikes,
                 * favorites) and open the episode view when requested.
                 *
                 * @param item  the Series to display in the cell; may be null when the cell is empty
                 * @param empty true if the cell does not contain data and should be cleared
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

                        // Issue #16: Add null check before accessing image
                        final ImageView imageView = new ImageView();
                        imageView.setFitWidth(150);
                        imageView.setPreserveRatio(true);

                        if (item.getImageUrl() != null) {
                            try {
                                final File file = new File(item.getImageUrl());
                                final Image image = new Image(file.toURI().toString());
                                imageView.setImage(image);
                            } catch (Exception e) {
                                ListFavorisController.LOGGER.warning("Failed to load image for series: " + e.getMessage());
                            }
                        }

                        // Issue #16: Add null check for series name
                        final Label nameLabel = new Label("Name: " + (item.getName() != null ? item.getName() : "N/A"));
                        nameLabel.setStyle(
                            "-fx-font-family: 'Helvetica'; -fx-font-size: 18.0px; -fx-font-weight: bold; -fx-text-fill: #333333;"); // Couleur
                        // de
                        // texte
                        // sombre
                        // Label summaryLabel = new Label("Summary: " + item.getResume());
                        // Label directorLabel = new Label("Director: " + item.getDirecteur());
                        // Label countryLabel = new Label("Country: " + item.getPays());
                        final Image iconHeart = new Image(this.getClass().getResourceAsStream("/img/films/heart.png"));
                        final ImageView HeartImageView = new ImageView(iconHeart);
                        HeartImageView.setFitWidth(10.0);
                        HeartImageView.setFitHeight(10.0);
                        final Label likesLabel = new Label("Likes: " + item.getClickLikes());
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
                        final Image iconImage2 = new Image(this.getClass().getResourceAsStream("/img/films/dislike.png"));
                        final ImageView iconImageView2 = new ImageView(iconImage2);
                        iconImageView2.setFitWidth(20.0);
                        iconImageView2.setFitHeight(20.0);
                        final Button dislikeButton = new Button("", iconImageView2);
                        // Set the layout constraints for the Button in the AnchorPane
                        AnchorPane.setTopAnchor(dislikeButton, 10.0);
                        AnchorPane.setRightAnchor(dislikeButton, 50.0);
                        final Image iconImage = new Image(this.getClass().getResourceAsStream("/img/films/like.png"));
                        final ImageView iconImageView = new ImageView(iconImage);
                        iconImageView.setFitWidth(20.0);
                        iconImageView.setFitHeight(20.0);
                        final Button likeButton = new Button("", iconImageView);
                        // Set the layout constraints for the Button in the AnchorPane
                        AnchorPane.setTopAnchor(likeButton, 10.0);
                        AnchorPane.setRightAnchor(likeButton, 10.0);
                        final Image iconFav = new Image(this.getClass().getResourceAsStream("/img/films/heart.png"));
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
                                                    * Toggle the liked state of the associated series item, update its like count and UI state, and persist the change.
                                                    *
                                                    * @param event the ActionEvent that triggered this handler
                                                    * @throws RuntimeException if a database error occurs while updating the like state
                                                    */
                                                   @Override
                                                   /**
                                                    * Performs handle operation.
                                                    *
                                                    * @return the result of the operation
                                                    */
                                                   public void handle(final ActionEvent event) {
                                                       item.setClickLikes(item.getClickLikes() + 1);
                                                       ListFavorisController.LOGGER.info(String.valueOf(item.getClickLikes()));
                                                       final SeriesService ss = new SeriesService();
                                                       try {
                                                           if ((0 == item.getClickLikes()) || (0 != item.getClickLikes() % 2)) {
                                                               item.setClickLikes(item.getClickLikes() + 1);
                                                               ss.addLike(item);
                                                               dislikeButton.setDisable(true);
                                                           } else {
                                                               item.setClickLikes(item.getClickLikes() - 1);
                                                               if (0 == item.getClickLikes()) {
                                                                   // Like status removed from model
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

                                               }
                        );
                        dislikeButton.setOnAction(new EventHandler<ActionEvent>() {
                                                      /**
                                                       * Handle a dislike-button action for the current series item.
                                                       *
                                                       * Updates the item's click and dislike counters, persists the dislike state to the database,
                                                       * and enables or disables the like button to reflect the new state.
                                                       *
                                                       * @throws RuntimeException if a database error occurs while persisting the dislike state
                                                       */
                                                      @Override
                                                      /**
                                                       * Performs handle operation.
                                                       *
                                                       * @return the result of the operation
                                                       */
                                                      public void handle(final ActionEvent event) {
                                                          item.setClickDislikes(item.getClickDislikes() + 1);
                                                          ListFavorisController.LOGGER.info(String.valueOf(item.getClickDislikes()));
                                                          final SeriesService ss = new SeriesService();
                                                          try {
                                                              if ((0 == item.getClickDislikes()) || (0 != item.getClickDislikes() % 2)) {
                                                                  item.setClickDislikes(item.getClickDislikes() + 1);
                                                                  ss.addDislike(item);
                                                                  likeButton.setDisable(true);
                                                              } else {
                                                                  item.setClickDislikes(item.getClickDislikes() - 1);
                                                                  if (0 == item.getClickDislikes()) {
                                                                      // Dislike status removed from model
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

                                                  }
                        );
                        favButton.setOnAction(new EventHandler<ActionEvent>() {
                                                  /**
                                                   * Toggle the favorite state for the current series and persist the change for user ID 1.
                                                   *
                                                   * Increments the series' internal favorite-click counter and either creates or deletes a Favorite
                                                   * record for the session user and series; database errors are propagated as a RuntimeException.
                                                   *
                                                   * @param event the ActionEvent from the favorite button
                                                   * @throws RuntimeException if a database error occurs while creating or deleting the favorite
                                                   */
                                                  @Override
                                                  /**
                                                   * Performs handle operation.
                                                   *
                                                   * @return the result of the operation
                                                   */
                                                  public void handle(final ActionEvent event) {
                                                      final Long id_user = 1L;
                                                      final Long id_serie = item.getId();
                                                      final FavoriteService sf = new FavoriteService();
                                                      final Favorite f = new Favorite(id_user, id_serie);
                                                      item.setClickFavorites(item.getClickFavorites() + 1);
                                                      ListFavorisController.LOGGER.info(String.valueOf(item.getClickFavorites()));
                                                      try {
                                                          if ((0 == item.getClickFavorites()) || (0 != item.getClickFavorites() % 2)) {
                                                              sf.create(f);
                                                          } else {
                                                              final Favorite fav = sf.getByIdUserAndIdSerie(id_user, id_serie);
                                                              sf.delete(fav);
                                                              ListFavorisController.LOGGER.info(String.valueOf(fav.getId().intValue()));
                                                          }

                                                      } catch (final SQLException e) {
                                                          throw new RuntimeException(e);
                                                      }

                                                  }

                                              }
                        );
                        /* Button Watch Episode Declaration */
                        final Image iconImageWatch = new Image(this.getClass().getResourceAsStream("/img/films/watch.png"));
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
                        AnchorPane.setTopAnchor(watchButton, 120.0);
                        AnchorPane.setLeftAnchor(watchButton, 180.0);
                        final Label dislikesLabel = new Label("DisLikes: " + item.getClickDislikes());
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
                        this.setGraphic(anchorPane);
                        watchButton.setOnAction(event -> {
                                final FXMLLoader fxmlLoader = new FXMLLoader(
                                    this.getClass().getResource("/ui/series/EpisodeClient.fxml"));
                                final Stage stage = new Stage();
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

                            }
                        );
                    }

                }

            }
        );
        this.listViewFav.getItems().addAll(series);
    }


    /**
     * Load and display the current session's favorite series.
     * <p>
     * Fetches the favorites for the active session, resolves each favorite to its
     * corresponding Series object, and populates the UI with the resulting list.
     *
     * @throws SQLException if retrieving favorites or series from the data layer fails
     */
    @FXML
    private void loadSeriesFavList() throws SQLException {
        final SeriesService ss = new SeriesService();
        final FavoriteService sf = new FavoriteService();
        final List<Series> listSerieById = new ArrayList<>();
        final List<Favorite> listFav = sf.showFavoritesList(this.idSession);
        for (int i = 0; i < listFav.size(); i++) {
            final Series s = ss.getByIdSeries(listFav.get(i).getSeriesId());
            listSerieById.add(s);
        }

        this.afficherliste(listSerieById);
    }

}
