package com.esprit.controllers.films;

import com.esprit.models.films.Film;
import com.esprit.services.films.ActorfilmService;
import com.esprit.services.films.FilmService;
import com.esprit.services.films.FilmcategoryService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;

import java.util.List;

public class FilmUserController {

    @FXML
    private Hyperlink detailFilm;

    @FXML
    private FlowPane flowpaneFilm;
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


    public void initialize() {
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
        nomFilm.setLayoutY(219);
        nomFilm.setPrefSize(176, 32);
        nomFilm.setFont(new Font(18)); // Copy the font size
        nomFilm.getStyleClass().addAll("labeltext");

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


            }
        });

        // Copy CSS classes
        copyOfAnchorPane.getChildren().addAll(imageView, nomFilm, button, hyperlink);
        return copyOfAnchorPane;
    }
}

