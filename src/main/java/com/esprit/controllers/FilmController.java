package com.esprit.controllers;

import com.esprit.models.Category;
import com.esprit.models.Film;
import com.esprit.services.CategoryService;
import com.esprit.services.FilmService;
import com.esprit.utils.DataSource;
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

import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Objects;

public class FilmController {
    Blob imageBlob;
    private File selectedFile;
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
    private TableColumn<Film, ImageView> imageFilm_tableColumn;
    @FXML
    private TableColumn<Film, String> nomFilm_tableColumn;
    @FXML
    private TextArea nomFilm_textArea;
    @FXML
    private AnchorPane image_view;

    @FXML
    void initialize() {
        readFilmTable();
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
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    void importFilmImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            imageFilm_ImageView.setImage(selectedImage);
        }
    }

    @FXML
    void deleteFilm(ActionEvent event) {
        FilmService fs = new FilmService();
        fs.delete(new Film(Integer.parseInt(idFilm_textArea.getText())));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Produit supprimée");
        alert.setContentText("Produit supprimée !");
        alert.show();
        readFilmTable();
        clear();
    }

    @FXML
    void insertFilm(ActionEvent event) {

        if (selectedFile != null) { // Vérifier si une image a été sélectionnée
            Connection connection = null;
            try {
                // Convertir le fichier en un objet Blob
                FileInputStream fis = new FileInputStream(selectedFile);
                connection = DataSource.getInstance().getConnection();
                Blob imageBlob = connection.createBlob();

                // Définir le flux d'entrée de l'image dans l'objet Blob
                try (OutputStream outputStream = imageBlob.setBinaryStream(1)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                // Créer l'objet Cinema avec l'image Blob
                System.out.println("passed the image");
                FilmService fs = new FilmService();
                fs.create(new Film(nomFilm_textArea.getText(), imageBlob, Time.valueOf(dureeFilm_textArea.getText()), descriptionFilm_textArea.getText(), Integer.parseInt(annederealisationFilm_textArea.getText()), new CategoryService().getCategoryByNom(idcategoryFilm_comboBox.getValue()), Integer.parseInt(idacteurFilm_comboBox.getValue()), Integer.parseInt(idcinemaFilm_comboBox.getValue())));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Film ajoutée");
                alert.setContentText("Film ajoutée !");
                alert.show();
                readFilmTable();
                clear();
            } catch (SQLException | IOException e) {
                showAlert("Erreur lors de l'ajout du Film : " + e.getMessage());
            }
        } else {
            showAlert("Veuillez sélectionner une image d'abord !");
        }
    }

    void clear() {
        idFilm_textArea.setText("");
        nomFilm_textArea.setText("");
        dureeFilm_textArea.setText("");
        descriptionFilm_textArea.setText("");
        annederealisationFilm_textArea.setText("");
        idcategoryFilm_comboBox.setValue("");
        idacteurFilm_comboBox.setValue("");
        idcinemaFilm_comboBox.setValue("");

    }

    void readFilmTable() {
        try {
            idFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, Integer>("id"));
            nomFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, String>("nom"));
            // imageFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, ImageView>("image"));
            dureeFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, Time>("duree"));
            descriptionFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, String>("description"));
            annederalisationFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, Integer>("annederalisation"));
            idcategoryFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, String>("categoryNom"));
            idacteurFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, Integer>("idacteur"));
            idcinemaFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, Integer>("idcinema"));
            // Configurer la cellule de la colonne Logo pour afficher l'image
            imageFilm_tableColumn.setCellValueFactory(cellData -> {
                Film film = cellData.getValue();
                ImageView imageView = new ImageView();
                imageView.setFitWidth(100); // Réglez la largeur de l'image selon vos préférences
                imageView.setFitHeight(50); // Réglez la hauteur de l'image selon vos préférences
                try {
                    Blob blob = film.getImage();
                    if (blob != null) {
                        Image image = new Image(blob.getBinaryStream());
                        imageView.setImage(image);
                    } else {
                        // Afficher une image par défaut si le logo est null
                        Image defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("default_image.png")));
                        imageView.setImage(defaultImage);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return new javafx.beans.property.SimpleObjectProperty<>(imageView);
            });
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
                    imageFilm_ImageView.toString();
                    dureeFilm_textArea.setText(f.getDuree().toString());
                    descriptionFilm_textArea.setText(f.getDescription());
                    annederealisationFilm_textArea.setText(String.valueOf(f.getAnnederalisation()));
                    idcategoryFilm_comboBox.setValue(f.getCategoryNom());
                    idacteurFilm_comboBox.setValue(String.valueOf(f.getIdacteur()));
                    idcinemaFilm_comboBox.setValue(String.valueOf(f.getIdcinema()));
                    imageBlob = f.getImage();
                    Blob imageBlob1 = f.getImage();
                    try (InputStream inputStream = imageBlob1.getBinaryStream()) {
                        Image image1 = new Image(inputStream);
                        imageFilm_ImageView.setImage(image1);
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                        showAlert("Erreur lors de la récupération de l'image : " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void updateFilm(ActionEvent event) {
        if (imageBlob != null) { // Vérifier si une image a été sélectionnée
            Connection connection = null;
            try {
                if (selectedFile != null)
                // Convertir le fichier en un objet Blob
                {
                    FileInputStream fis = new FileInputStream(selectedFile);

                    connection = DataSource.getInstance().getConnection();

                    // Définir le flux d'entrée de l'image dans l'objet Blob
                    try (OutputStream outputStream = imageBlob.setBinaryStream(1)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
                // modifier un produit

                FilmService fs = new FilmService();
// Assign value to imageBlob
                /* assign the blob value here */
                fs.update(new Film(
                        Integer.parseInt(idFilm_textArea.getText()),
                        nomFilm_textArea.getText(),
                        imageBlob,
                        Time.valueOf(dureeFilm_textArea.getText()),
                        descriptionFilm_textArea.getText(),
                        Integer.parseInt(annederealisationFilm_textArea.getText()), // Comma added here
                        new CategoryService().getCategoryByNom(idcategoryFilm_comboBox.getValue()),
                        Integer.parseInt(idacteurFilm_comboBox.getValue()),
                        Integer.parseInt(idcinemaFilm_comboBox.getValue())));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Produit modifiée");
                alert.setContentText("Produit modifiée !");
                alert.show();
                readFilmTable();
                clear();

            } catch (SQLException | IOException e) {
                showAlert("Erreur lors de la modification du produit : " + e.getMessage());
            }
        }
        readFilmTable();

    }

}
