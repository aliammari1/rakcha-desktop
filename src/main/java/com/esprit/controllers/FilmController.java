package com.esprit.controllers;

import com.esprit.models.Category;
import com.esprit.models.Film;
import com.esprit.services.CategoryService;
import com.esprit.services.FilmService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public class FilmController {

    @FXML
    private TableColumn<Film, Integer> annederalisationFilm_tableColumn;

    @FXML
    private TextArea annederealisationFilm_textArea;

    @FXML
    private TableColumn<Film, String> descriptionFilm_tableColumn;

    @FXML
    private TextArea descriptionFilm_textArea;

    @FXML
    private TableColumn<Film, Time> dureeFilm_tableColumn;

    @FXML
    private TextArea dureeFilm_textArea;

    @FXML
    private TableView<Film> filmCategory_tableView1;

    @FXML
    private AnchorPane filmCrudInterface;

    @FXML
    private TableColumn<Film, Integer> idFilm_tableColumn;

    @FXML
    private TextArea idFilm_textArea;

    @FXML
    private TableColumn<Film, Integer> idacteurFilm_tableColumn;

    @FXML
    private ComboBox<String> idacteurFilm_comboBox;

    @FXML
    private TableColumn<Film, String> idcategoryFilm_tableColumn;

    @FXML
    private ComboBox<String> idcategoryFilm_comboBox;

    @FXML
    private TableColumn<Film, Integer> idcinemaFilm_tableColumn;

    @FXML
    private ComboBox<String> idcinemaFilm_comboBox;

    @FXML
    private ImageView imageFilm_ImageView;

    @FXML
    private TableColumn<Category, Blob> imageFilm_tableColumn;

    @FXML
    private TableColumn<Film, String> nomFilm_tableColumn;

    @FXML
    private TextArea nomFilm_textArea;

    @FXML
    void initialize() {
        readFilmTable(null);
        CategoryService cs = new CategoryService();
        FilmService fs = new FilmService();
        for (Category c : cs.read())
            idcategoryFilm_comboBox.getItems().add(c.getNom());

        for (Film f : fs.read())
            idacteurFilm_comboBox.getItems().add(String.valueOf(f.getIdacteur()));

        for (Film f : fs.read())
            idcinemaFilm_comboBox.getItems().add(String.valueOf(f.getIdcinema()));
    }


    @FXML
    void importFilmImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            imageFilm_ImageView.setImage(selectedImage);
        }
    }

    @FXML
    void deleteFilm(ActionEvent event) {

    }

    @FXML
    void insertFilm(ActionEvent event) {
        try {
            FilmService filmService = new FilmService();
            CategoryService categoryService = new CategoryService();
            Film film = new Film(nomFilm_textArea.getText(), "src/main/resources/Logo.png", new Time(Long.valueOf(dureeFilm_textArea.getText())), descriptionFilm_textArea.getText(), Integer.parseInt(annederealisationFilm_textArea.getText()), categoryService.getCategoryByNom(idcategoryFilm_comboBox.getValue()), Integer.parseInt(idacteurFilm_comboBox.getValue()), Integer.parseInt(idcinemaFilm_comboBox.getValue()));
            filmService.create(film);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("l'insertion est terminer");
            alert.setHeaderText("categorie");
            alert.setHeaderText("categorie");
            alert.show();
            readFilmTable(event);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    void readFilmTable(ActionEvent event) {
        try {
            idFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, Integer>("id"));
            nomFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, String>("nom"));
            imageFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Category, Blob>("image"));
            dureeFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, Time>("duree"));
            descriptionFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, String>("description"));
            annederalisationFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, Integer>("annederalisation"));
            idcategoryFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, String>("categoryNom"));
            idacteurFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, Integer>("idacteur"));
            idcinemaFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, Integer>("idcinema"));

            FilmService filmService = new FilmService();
            CategoryService categoryService = new CategoryService();
            System.out.println("passed");
            List<Film> lf = filmService.read();
            System.out.println("passed 2");
            for (Film ff : lf)
                System.out.println(ff);
            ObservableList<Film> obF = FXCollections.observableArrayList(filmService.read());
            filmCategory_tableView1.setItems(obF);
            filmCategory_tableView1.getSelectionModel().selectedItemProperty().addListener((observableValue, category, t1) -> {
                Film f = filmCategory_tableView1.getSelectionModel().getSelectedItem();
                if (f != null) {
                    idFilm_textArea.setText(String.valueOf(f.getId()));
                    nomFilm_textArea.setText(f.getNom());
                    try {
                        imageFilm_ImageView.setImage(new Image(f.getImage().getBinaryStream()));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    dureeFilm_textArea.setText(f.getDuree().toString());
                    descriptionFilm_textArea.setText(f.getDescription());
                    annederealisationFilm_textArea.setText(String.valueOf(f.getAnnederalisation()));
                    idcategoryFilm_comboBox.setValue(f.getCategoryNom());
                    idacteurFilm_comboBox.setValue(String.valueOf(f.getIdacteur()));
                    idcinemaFilm_comboBox.setValue(String.valueOf(f.getIdcinema()));
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void updateFilm(ActionEvent event) {

    }

}
