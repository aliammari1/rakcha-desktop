package com.esprit.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class DetailsProduitClientController {
    public TextField idP_textFiled;
    public TextField nomP_textFiled;
    public TextField prix_textFiled;
    public TextField descriptionP_textFiled;
    public TextField quantiteP_textFiled;
    public TableView Produit_tableview;
    public TableColumn idP_tableC;
    public TableColumn nomCP_tableC;
    public TableColumn nomP_tableC;
    public TableColumn PrixP_tableC;
    public TableColumn image_tableC;
    public TableColumn descriptionP_tableC;
    public TableColumn quantiteP_tableC;
    public ComboBox nomC_comboBox;
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



