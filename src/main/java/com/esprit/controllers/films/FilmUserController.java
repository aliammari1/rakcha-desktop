package com.esprit.controllers.films;

import com.esprit.models.films.Film;
import com.esprit.models.films.RatingFilm;
import com.esprit.models.users.Client;
import com.esprit.services.films.ActorfilmService;
import com.esprit.services.films.FilmService;
import com.esprit.services.films.FilmcategoryService;
import com.esprit.services.films.RatingFilmService;
import com.esprit.services.users.UserService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import org.controlsfx.control.Rating;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class FilmUserController {
    FlowPane flowpaneFilm;
    @FXML
    private Rating filmRate;
    @FXML
    private Button trailer_Button;
    @FXML
    private AnchorPane anchorPane_Trailer;
    @FXML
    private Label labelavregeRate;
    @FXML
    private AnchorPane anchorPaneFilm;
    @FXML
    private AnchorPane detalAnchorPane;
    @FXML
    private Button closeDetailFilm;
    @FXML
    private TextArea descriptionDETAILfilm;
    @FXML
    private ImageView imagefilmDetail;
    @FXML
    private Label filmNomDetail;
    @FXML
    private VBox topthreeVbox;
    @FXML
    private ScrollPane filmScrollPane;

    public void initialize() {
        flowpaneFilm = new FlowPane();
        filmScrollPane.setContent(flowpaneFilm);
        filmScrollPane.setFitToWidth(true);
        filmScrollPane.setFitToHeight(true);
        topthreeVbox.setSpacing(10);
        String trailerURL = new FilmService().getTrailerFilm("garfield");
        System.out.println(trailerURL);
        flowpaneFilm.setHgap(10); // Set the horizontal gap
        flowpaneFilm.setVgap(10); // Set the vertical gap.
        closeDetailFilm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                detalAnchorPane.setVisible(false);
                anchorPaneFilm.setOpacity(1);
                anchorPaneFilm.setDisable(false);
            }
        });
        // Set the padding
        flowpaneFilm.setPadding(new Insets(10, 10, 10, 10));
        List<Film> filmList = new FilmService().read();
        for (Film film : filmList) {
            System.out.println(film);
            flowpaneFilm.getChildren().add(createFilmCard(film));
        }

        for (int i = 0; i < 3; i++) {
            topthreeVbox.getChildren().add(createtopthree(i));
        }
    }


    private AnchorPane createFilmCard(Film film) {
        AnchorPane copyOfAnchorPane = new AnchorPane();

// Copy properties
        copyOfAnchorPane.setLayoutX(0);
        copyOfAnchorPane.setLayoutY(0);
        copyOfAnchorPane.setPrefSize(213, 334);
        copyOfAnchorPane.getStyleClass().add("anchorfilm");
// Copy children nodes
        ImageView imageView = new ImageView();
        try {
            System.out.println("here");
            if (!film.getImage().isEmpty())
                imageView.setImage(new Image(film.getImage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        imageView.setLayoutX(45);
        imageView.setLayoutY(7);
        imageView.setFitHeight(193);
        imageView.setFitWidth(132);
        imageView.getStyleClass().addAll("bg-white");

        Label nomFilm = new Label(film.getNom());
        nomFilm.setLayoutX(23);
        nomFilm.setLayoutY(200);
        nomFilm.setPrefSize(176, 32);
        nomFilm.setFont(new Font(18)); // Copy the font size
        nomFilm.getStyleClass().addAll("labeltext");

        Label ratefilm = new Label(film.getNom());
        ratefilm.setLayoutX(9);
        ratefilm.setLayoutY(222);
        ratefilm.setPrefSize(176, 32);
        ratefilm.setFont(new Font(18)); // Copy the font size
        ratefilm.getStyleClass().addAll("labeltext");
        double rate = new RatingFilmService().getavergerating(film.getId());
        System.out.println(BigDecimal.valueOf(rate).setScale(1, RoundingMode.FLOOR));
        ratefilm.setText(rate + "/5");
        RatingFilm ratingFilm = new RatingFilmService().ratingExiste(film.getId(),/*(Client) stage.getUserData()*/1);
        filmRate.setRating(ratingFilm != null ? ratingFilm.getRate() : 0);
        FontAwesomeIconView etoile = new FontAwesomeIconView();
        etoile.setGlyphName("STAR");
        etoile.setLayoutX(128);
        etoile.setLayoutY(247);
        etoile.setSize("25");
        etoile.setFill(Color.web("#f2b604"));

        Button button = new Button("reserve");
        button.setLayoutX(22);
        button.setLayoutY(278);
        button.setPrefSize(172, 42);
        button.getStyleClass().addAll("sale");

        Hyperlink hyperlink = new Hyperlink("Details");
        hyperlink.setLayoutX(89);
        hyperlink.setLayoutY(251);
        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                detalAnchorPane.setVisible(true);
                anchorPaneFilm.setOpacity(0.26);
                anchorPaneFilm.setDisable(true);
                Film film1 = new Film(film);
                filmNomDetail.setText(film1.getNom());
                descriptionDETAILfilm.setText(film1.getDescription() + "\nTime:" + film1.getDuree() + "\nYear:" + film1.getAnnederalisation() + "\nCategories: " + new FilmcategoryService().getCategoryNames(film1.getId()) + "\nActors: " + new ActorfilmService().getActorsNames(film1.getId()));
                imagefilmDetail.setImage(new Image("Logo.png"));
                double rate = new RatingFilmService().getavergerating(film1.getId());
                System.out.println(BigDecimal.valueOf(rate).setScale(1, RoundingMode.FLOOR));
                labelavregeRate.setText(rate + "/5");
                RatingFilm ratingFilm = new RatingFilmService().ratingExiste(film1.getId(),/*(Client) stage.getUserData()*/1);
                filmRate.setRating(ratingFilm != null ? ratingFilm.getRate() : 0);
                //Stage stage = (Stage) hyperlink.getScene().getWindow();
                filmRate.ratingProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                        RatingFilmService ratingFilmService = new RatingFilmService();
                        RatingFilm ratingFilm = ratingFilmService.ratingExiste(film1.getId(), 1 /*(Client) stage.getUserData()*/);
                        if (ratingFilm != null)
                            ratingFilmService.delete(ratingFilm);
                        ratingFilmService.create(new RatingFilm(film1, /*(Client) stage.getUserData()*/(Client) new UserService().getUserById(1), t1.intValue()));
                        double rate = new RatingFilmService().getavergerating(film1.getId());
                        System.out.println(BigDecimal.valueOf(rate).setScale(1, RoundingMode.FLOOR));
                        labelavregeRate.setText(rate + "/5");
                        List<Film> filmList = new FilmService().read();
                        flowpaneFilm.getChildren().clear();
                        for (Film film : filmList) {
                            System.out.println(film);
                            flowpaneFilm.getChildren().add(createFilmCard(film));
                        }
                        topthreeVbox.getChildren().clear();
                        for (int i = 0; i < 3; i++) {
                            topthreeVbox.getChildren().add(createtopthree(i));
                        }
                    }
                });
                trailer_Button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        anchorPane_Trailer.getChildren().forEach(node -> {
                            node.setDisable(false);
                        });
                        WebView webView = new WebView();
                        System.out.println(film1.getNom());
                        webView.getEngine().load(new FilmService().getTrailerFilm(film1.getNom()));
                        anchorPane_Trailer.setVisible(true);
                        anchorPane_Trailer.getChildren().add(webView);
                        anchorPane_Trailer.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent keyEvent) {
                                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                                    anchorPane_Trailer.getChildren().forEach(node -> {
                                        node.setDisable(true);
                                    });
                                    anchorPane_Trailer.setVisible(false);
                                }
                            }
                        });
                    }
                });

            }
        });
        // Copy CSS classes
        copyOfAnchorPane.getChildren().addAll(imageView, nomFilm, button, hyperlink, ratefilm, etoile);
        return copyOfAnchorPane;
    }

    public AnchorPane createtopthree(int filmRank) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setLayoutX(0);
        anchorPane.setLayoutY(0);
        anchorPane.setPrefSize(544, 226);
        anchorPane.getStyleClass().add("meilleurfilm");
        RatingFilm ratingFilm = new RatingFilmService().getavergeratingSorted().get(filmRank);
        System.out.println(ratingFilm);
        ImageView imageView = new ImageView();
        try {
            System.out.println("here");
            if (!ratingFilm.getId_film().getImage().isEmpty())
                imageView.setImage(new Image(ratingFilm.getId_film().getImage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        imageView.setLayoutX(21);
        imageView.setLayoutY(21);
        imageView.setFitHeight(167);
        imageView.setFitWidth(122);
        imageView.getStyleClass().addAll("bg-white");

        Label nomFilm = new Label(ratingFilm.getId_film().getNom());
        nomFilm.setLayoutX(153);
        nomFilm.setLayoutY(87);
        nomFilm.setPrefSize(205, 35);
        nomFilm.setFont(new Font(22));
        nomFilm.setTextFill(Color.WHITE);// Copy the font size

        Button button = new Button("reserve");
        button.setLayoutX(346);
        button.setLayoutY(154);
        button.setPrefSize(172, 42);
        button.getStyleClass().addAll("sale");

        Rating rating = new Rating();
        rating.setLayoutX(344);
        rating.setLayoutY(38);
        rating.setPrefSize(176, 32);
        rating.setRating(ratingFilm.getRate());
        rating.setDisable(true);

        Hyperlink hyperlink = new Hyperlink("detail");
        hyperlink.setLayoutX(404);
        hyperlink.setLayoutY(118);
        hyperlink.setPrefSize(56, 35);
        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                detalAnchorPane.setVisible(true);
                anchorPaneFilm.setOpacity(0.26);
                anchorPaneFilm.setDisable(true);
                Film film1 = new Film(ratingFilm.getId_film());
                filmNomDetail.setText(film1.getNom());
                descriptionDETAILfilm.setText(film1.getDescription() + "\nTime:" + film1.getDuree() + "\nYear:" + film1.getAnnederalisation() + "\nCategories: " + new FilmcategoryService().getCategoryNames(film1.getId()) + "\nActors: " + new ActorfilmService().getActorsNames(film1.getId()));
                imagefilmDetail.setImage(new Image("Logo.png"));
                double rate = new RatingFilmService().getavergerating(film1.getId());
                System.out.println(BigDecimal.valueOf(rate).setScale(1, RoundingMode.FLOOR));
                labelavregeRate.setText(rate + "/5");
                RatingFilm ratingFilm = new RatingFilmService().ratingExiste(film1.getId(),/*(Client) stage.getUserData()*/1);
                filmRate.setRating(ratingFilm != null ? ratingFilm.getRate() : 0);
                //Stage stage = (Stage) hyperlink.getScene().getWindow();
                filmRate.ratingProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                        RatingFilmService ratingFilmService = new RatingFilmService();
                        RatingFilm ratingFilm = ratingFilmService.ratingExiste(film1.getId(), 1 /*(Client) stage.getUserData()*/);
                        if (ratingFilm != null)
                            ratingFilmService.delete(ratingFilm);
                        ratingFilmService.create(new RatingFilm(film1, /*(Client) stage.getUserData()*/(Client) new UserService().getUserById(1), t1.intValue()));
                        double rate = new RatingFilmService().getavergerating(film1.getId());
                        System.out.println(BigDecimal.valueOf(rate).setScale(1, RoundingMode.FLOOR));
                        labelavregeRate.setText(rate + "/5");
                        List<Film> filmList = new FilmService().read();
                        flowpaneFilm.getChildren().clear();
                        for (Film film : filmList) {
                            System.out.println(film);
                            flowpaneFilm.getChildren().add(createFilmCard(film));
                        }

                    }
                });
                trailer_Button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        anchorPane_Trailer.getChildren().forEach(node -> {
                            node.setDisable(false);
                        });
                        WebView webView = new WebView();
                        System.out.println(film1.getNom());
                        webView.getEngine().load(new FilmService().getTrailerFilm(film1.getNom()));
                        anchorPane_Trailer.setVisible(true);
                        anchorPane_Trailer.getChildren().add(webView);
                        anchorPane_Trailer.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent keyEvent) {
                                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                                    anchorPane_Trailer.getChildren().forEach(node -> {
                                        node.setDisable(true);
                                    });
                                    anchorPane_Trailer.setVisible(false);
                                }
                            }
                        });
                    }
                });
            }
        });


        anchorPane.getChildren().addAll(nomFilm, button, rating, imageView, hyperlink);
        return anchorPane;
    }
}

