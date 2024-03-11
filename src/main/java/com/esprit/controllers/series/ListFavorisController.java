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

public class ListFavorisController implements Initializable {


    private final int idSession = 1;

    @FXML
    private ListView<Serie> listViewFav;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadSeriesFavList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void afficherliste(List<Serie> series) {
        listViewFav.getItems().clear();

        listViewFav.setCellFactory(param -> new ListCell<Serie>() {
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
                    AnchorPane.setTopAnchor(likesLabel, 60.0);
                    AnchorPane.setLeftAnchor(likesLabel, 180.0);
                    AnchorPane.setTopAnchor(HeartImageView, 65.0);
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


                    Image iconFav = new Image("pictures/films/heart.png");
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
                    AnchorPane.setTopAnchor(watchButton, 120.0);
                    AnchorPane.setLeftAnchor(watchButton, 180.0);
                    Label dislikesLabel = new Label("DisLikes: " + item.getNbDislikes());
                    dislikesLabel.setStyle("-fx-font-family: 'Helvetica'; -fx-font-size: 14.0px; -fx-font-weight: normal; -fx-text-fill: #666666;"); // Couleur de texte sombre plus claire
                    AnchorPane.setTopAnchor(dislikesLabel, 80.0);
                    AnchorPane.setLeftAnchor(dislikesLabel, 180.0);


                    // Ajoutez les composants à l'AnchorPane
                    //anchorPane.getChildren().addAll(imageView, nameLabel, summaryLabel, directorLabel, countryLabel, likesLabel, HeartImageView, likeButton, dislikeButton);
                    anchorPane.getChildren().addAll(imageView, nameLabel, likesLabel, HeartImageView, likeButton, dislikeButton, favButton, watchButton, dislikesLabel);
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
