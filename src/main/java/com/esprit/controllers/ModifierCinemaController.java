package com.esprit.controllers;

import com.esprit.models.Cinema;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

public class ModifierCinemaController implements Initializable {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfAdresse;

    @FXML
    private ImageView tfLogo;

    private Cinema cinema;

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
}
