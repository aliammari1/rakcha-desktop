package com.esprit.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class DetailsProduitClientController {
    @FXML
    private Label nomLabel;

    @FXML
    private Label prixLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private Button ajouterPanierButton;

    @FXML
    private Button retourButton;


    // Méthode pour initialiser les détails du produit
    public void initDetailsProduit(String nom, String prix, String description, Blob imageBlob) {
        nomLabel.setText("Nom : " + nom);
        prixLabel.setText("Prix : " + prix);
        descriptionLabel.setText("Description : " + description);
        try {
            // Utiliser SerialBlob pour gérer le Blob
            SerialBlob serialBlob = new SerialBlob((javax.sql.rowset.serial.SerialBlob) imageBlob);

            // Utiliser le Blob directement dans l'objet Image
            Image image = new Image(serialBlob.getBinaryStream());
            imageView.setImage(image);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}



