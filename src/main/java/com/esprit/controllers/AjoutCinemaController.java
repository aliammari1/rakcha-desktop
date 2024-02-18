package com.esprit.controllers;

import com.esprit.models.Cinema;
import com.esprit.services.CinemaService;
import com.esprit.utils.DataSource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;

public class AjoutCinemaController {

    @FXML
    private ImageView image;

    @FXML
    private TextField tfAdresse;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfResponsable;

    @FXML
    void addCinema(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image à ajouter");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                // Convertir le fichier en un objet Blob
                FileInputStream fis = new FileInputStream(file);
                Connection connection = DataSource.getInstance().getConnection();
                Blob imageBlob = connection.createBlob();
                imageBlob.setBinaryStream(1);

                // Créer l'objet Cinema avec l'image Blob
                Cinema cinema = new Cinema(tfNom.getText(), tfAdresse.getText(), tfResponsable.getText(), imageBlob);

                // Ajouter le cinéma à la base de données
                CinemaService cs = new CinemaService();
                cs.create(cinema);
                showAlert("Cinéma ajouté avec succès !");
                connection.close(); // Fermer la connexion après utilisation
            } catch (FileNotFoundException | SQLException e) {
                showAlert("Erreur lors de l'ajout du cinéma : " + e.getMessage());
            }
        }
    }

    @FXML
    void selectImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image selectedImage = new Image(file.toURI().toString());
            image.setImage(selectedImage);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
