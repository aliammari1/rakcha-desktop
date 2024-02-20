package com.esprit.controllers;

import com.esprit.models.Categorie;
import com.esprit.models.Produit;
import com.esprit.services.CategorieService;
import com.esprit.services.ProduitService;
import com.esprit.utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



public class AjouterProduitController {


    private File selectedFile; // pour stocké le fichier image selectionné

    @FXML
    private TableColumn<Produit, String> PrixP_tableC;

    @FXML
    private TableView<Produit> Produit_tableview;

    @FXML
    private TableColumn<Produit, String> descriptionP_tableC;

    @FXML
    private TextField descriptionP_textFiled;

    @FXML
    private TableColumn<Produit, Integer> idP_tableC;

    @FXML
    private TextField idP_textFiled;

    @FXML
    private TableColumn<Produit, Blob> image_tableC;


    @FXML
    private ComboBox<String> nomC_comboBox;

    @FXML
    private TableColumn<Produit,String> nomCP_tableC;

    @FXML
    private TableColumn<Produit,String> nomP_tableC;

    @FXML
    private TextField nomP_textFiled;

    @FXML
    private TextField prix_textFiled;

    @FXML
    private TableColumn<Produit, Integer> quantiteP_tableC;

    @FXML
    private TextField quantiteP_textFiled;

    @FXML
    private AnchorPane image_view;

    @FXML
    private ImageView image;


    @FXML
    void initialize() {
        CategorieService cs = new CategorieService();

        for (Categorie c : cs.read()) {
            nomC_comboBox.getItems().add(c.getNom_categorie());
        }
        afficher_produit();
        
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
    void selectImage(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            image.setImage(selectedImage);
        }

    }


    @FXML
    public void add_produit(ActionEvent actionEvent) {
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

                ProduitService ps = new ProduitService();

                ps.create(new Produit(nomP_textFiled.getText(), prix_textFiled.getText(), imageBlob, descriptionP_textFiled.getText(), new CategorieService().read().get(nomC_comboBox.getSelectionModel().getSelectedIndex()), Integer.parseInt(quantiteP_textFiled.getText())));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Produit ajoutée");
                alert.setContentText("Produit ajoutée !");
                alert.show();
                afficher_produit();
            } catch (SQLException | IOException e) {
                showAlert("Erreur lors de l'ajout du produit : " + e.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        showAlert("Erreur lors de la fermeture de la connexion : " + e.getMessage());
                    }
                }
            }
       } else {
            showAlert("Veuillez sélectionner une image d'abord !");
        }
        }





    @FXML
    void afficher_produit() {


        idP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("id_produit"));
        nomCP_tableC.setCellValueFactory(new PropertyValueFactory<Produit,String>("nom_categorie"));
        nomP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("nom"));
        PrixP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("prix"));
        image_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Blob>("image"));
        descriptionP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("description"));
        quantiteP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("quantiteP"));
        ObservableList<Produit> list = FXCollections.observableArrayList();
        ProduitService ps = new ProduitService();
        CategorieService cs = new CategorieService();
        list.addAll(ps.read());
        Produit_tableview.setItems(list);
        Produit_tableview.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Produit selectedUser = Produit_tableview.getSelectionModel().getSelectedItem();
                idP_textFiled.setText(String.valueOf(selectedUser.getId_produit()));
                nomC_comboBox.getValue();
                nomP_textFiled.setText(selectedUser.getNom());
                prix_textFiled.setText(selectedUser.getPrix());
                image_view.toString();
                descriptionP_textFiled.setText(selectedUser.getDescription());
                quantiteP_textFiled.setText(String.valueOf(selectedUser.getQuantiteP()));

                Blob imageBlob1 = selectedUser.getImage();
                try (InputStream inputStream = imageBlob1.getBinaryStream()) {
                    Image image1 = new Image(inputStream);
                    image.setImage(image1);
                }
                catch (SQLException | IOException e) {
                    e.printStackTrace();
                    showAlert("Erreur lors de la récupération de l'image : " + e.getMessage());
                }

            }
        });

        }
    @FXML
    void modifier_produit(ActionEvent event) {


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
         // modifier un produit


                ProduitService ps = new ProduitService();
                ps.update(new Produit(Integer.parseInt(idP_textFiled.getText()), nomP_textFiled.getText(),
                        prix_textFiled.getText(), imageBlob, descriptionP_textFiled.getText(),
                        new CategorieService().read().get(nomC_comboBox.getSelectionModel().getSelectedIndex()),
                        Integer.parseInt(quantiteP_textFiled.getText())));

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Produit modifiée");
                alert.setContentText("Produit modifiée !");
                 alert.show();
                afficher_produit();


    } catch (SQLException | IOException e) {
        showAlert("Erreur lors de la modification du produit : " + e.getMessage());
    } finally {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                showAlert("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}
        }



    @FXML
    void supprimer_produit(ActionEvent event) {
        ProduitService ps = new ProduitService();
        ps.delete(new Produit( Integer.parseInt(idP_textFiled.getText())));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Produit supprimée");
        alert.setContentText("Produit supprimée !");
        alert.show();
        afficher_produit();

    }


}



