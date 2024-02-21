package com.esprit.controllers;

import com.esprit.models.Cinema;
import com.esprit.services.CinemaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class ModifierCinemaController implements Initializable {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfAdresse;

    @FXML
    private ImageView tfLogo;

    private Cinema cinema;

    private File selectedFile; // Pour stocker le fichier image sélectionné




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des éléments de l'interface
    }

    // Méthode pour initialiser les données du cinéma
    public void initData(Cinema cinema) {
        this.cinema = cinema;
        // Pré-remplir les champs avec les données du cinéma
        tfNom.setText(cinema.getNom());
        tfAdresse.setText(cinema.getAdresse());

        // Afficher l'image du logo du cinéma s'il existe
        Blob logoBlob = cinema.getLogo();
        if (logoBlob != null) {
            try {
                byte[] logoBytes = logoBlob.getBytes(1, (int) logoBlob.length());
                ByteArrayInputStream inputStream = new ByteArrayInputStream(logoBytes);
                Image image = new Image(inputStream);
                tfLogo.setImage(image);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Afficher une image par défaut si le logo n'est pas défini
            tfLogo.setImage(new Image(getClass().getResourceAsStream("default_logo.png")));
        }
    }

    @FXML
    void modifier(ActionEvent event) {
        // Vérifier si un cinéma est sélectionné
        if (cinema == null) {
            showAlert("Veuillez sélectionner un cinéma.");
            return;
        }

        // Récupérer les nouvelles valeurs des champs
        String nouveauNom = tfNom.getText().trim();
        String nouvelleAdresse = tfAdresse.getText().trim();

        // Vérifier si les champs obligatoires sont remplis
        if (nouveauNom.isEmpty() || nouvelleAdresse.isEmpty()) {
            showAlert("Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Mettre à jour les informations du cinéma
        cinema.setNom(nouveauNom);
        cinema.setAdresse(nouvelleAdresse);

        // Mettre à jour l'image du logo si elle a été modifiée
        if (selectedFile != null) {
            try {
                // Convertir le fichier en tableau de bytes
                byte[] logoBytes = Files.readAllBytes(selectedFile.toPath());
                // Créer un objet Blob à partir du tableau de bytes
                Blob blob = new javax.sql.rowset.serial.SerialBlob(logoBytes);
                // Mettre à jour le logo du cinéma
                cinema.setLogo(blob);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }

        // Mettre à jour le cinéma dans la base de données
        CinemaService cinemaService = new CinemaService();
        cinemaService.update(cinema);

        showAlert("Les modifications ont été enregistrées avec succès.");
    }


    @FXML
    void select(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            tfLogo.setImage(selectedImage);
        }
    }


    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}


