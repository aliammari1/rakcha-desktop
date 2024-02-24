package com.esprit.controllers;

import com.esprit.models.Categorie;
import com.esprit.models.Produit;
import com.esprit.services.CategorieService;
import com.esprit.services.ProduitService;
import com.esprit.utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import java.util.Objects;
import java.util.function.Predicate;

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
    private TableColumn<Produit, ImageView> image_tableC;


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
    private TextField SearchBar;

Blob imageBlob;



    @FXML
    void initialize() {
        CategorieService cs = new CategorieService();

        for (Categorie c : cs.read()) {
            nomC_comboBox.getItems().add(c.getNom_categorie());
        }

        searchFilter();
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
                ps.create(new Produit(nomP_textFiled.getText(), prix_textFiled.getText(), imageBlob, descriptionP_textFiled.getText(), (Categorie) new CategorieService().getCategorieByNom(nomC_comboBox.getValue()), Integer.parseInt(quantiteP_textFiled.getText())));
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
    void searchFilter() {
        ObservableList<Produit> list = FXCollections.observableArrayList();
        // Wrap the ObservableList in a FilteredList
        FilteredList<Produit> filteredData = new FilteredList<>(list, p -> true);

        // Add a listener to the search bar text property to update the filteredData when the text changes
        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(produit -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Show all items when the search bar is empty
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Customize the conditions for searching based on your requirements
                return produit.getNom().toLowerCase().contains(lowerCaseFilter)
                        || produit.getPrix().toLowerCase().contains(lowerCaseFilter)
                        || produit.getDescription().toLowerCase().contains(lowerCaseFilter)
                        || produit.getNom_categorie().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Wrap the FilteredList in a SortedList
        SortedList<Produit> sortedData = new SortedList<>(filteredData);

        // Bind the SortedList comparator to the TableView comparator
        sortedData.comparatorProperty().bind(Produit_tableview.comparatorProperty());

        // Set the TableView items to the sorted and filtered list
        Produit_tableview.setItems(sortedData);


    }

    @FXML
    void afficher_produit() {


        idP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("id_produit"));
        nomCP_tableC.setCellValueFactory(new PropertyValueFactory<Produit,String>("nom_categorie"));
        nomP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("nom"));
        PrixP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("prix"));
        //image_tableC.setCellValueFactory(new PropertyValueFactory<Produit,ImageView>("image"));
        descriptionP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("description"));
        quantiteP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("quantiteP"));

        // Configurer la cellule de la colonne Logo pour afficher l'image
        image_tableC.setCellValueFactory(cellData -> {
            Produit p = cellData.getValue();
            ImageView imageView = new ImageView();
            imageView.setFitWidth(100); // Réglez la largeur de l'image selon vos préférences
            imageView.setFitHeight(50); // Réglez la hauteur de l'image selon vos préférences
            try {
                Blob blob = p.getImage();
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
        ObservableList<Produit> list = FXCollections.observableArrayList();
        ProduitService ps = new ProduitService();
        list.addAll(ps.read());
        Produit_tableview.setItems(list);
        Produit_tableview.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Produit selectedUser = Produit_tableview.getSelectionModel().getSelectedItem();
                idP_textFiled.setText(String.valueOf(selectedUser.getId_produit()));
                nomC_comboBox.setValue(selectedUser.getNom_categorie());;
                nomP_textFiled.setText(selectedUser.getNom());
                prix_textFiled.setText(selectedUser.getPrix());
                image_view.toString();
                descriptionP_textFiled.setText(selectedUser.getDescription());
                quantiteP_textFiled.setText(String.valueOf(selectedUser.getQuantiteP()));

                imageBlob = selectedUser.getImage();
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
        if (imageBlob != null) { // Vérifier si une image a été sélectionnée
            Connection connection = null;
            try {
                if(selectedFile != null)
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

                ProduitService ps = new ProduitService();
                ps.update(new Produit(Integer.parseInt(idP_textFiled.getText()), nomP_textFiled.getText(),
                        prix_textFiled.getText(), imageBlob, descriptionP_textFiled.getText(),
                        (Categorie) new CategorieService().getCategorieByNom(nomC_comboBox.getValue()),
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
        afficher_produit();
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



