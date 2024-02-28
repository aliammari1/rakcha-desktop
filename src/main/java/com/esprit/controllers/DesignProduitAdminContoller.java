package com.esprit.controllers;
import com.esprit.models.Categorie;
import com.esprit.models.Produit;
import com.esprit.services.CategorieService;
import com.esprit.services.ProduitService;
import com.esprit.utils.DataSource;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import javax.sql.rowset.serial.SerialBlob;


public class DesignProduitAdminContoller {

    private File selectedFile; // pour stocke le fichier image selectionné

    @FXML
    private TableColumn<Produit, String> PrixP_tableC;

    @FXML
    private TableView<Produit> Produit_tableview;

    @FXML
    private TableColumn<Produit, String> descriptionP_tableC;

    @FXML
    private TextArea descriptionP_textArea;

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
    private TableColumn<Produit, Void> deleteColumn;

    private List<Produit> l1=new ArrayList<>();

    @FXML
    void initialize() {
        ProduitService produitService=new ProduitService();
        l1=produitService.read();
        SearchBar.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            List<Produit> pro;
            pro=rechercher(l1,newValue);
            afficher_produit(pro);
        });


        CategorieService cs = new CategorieService();

        for (Categorie c : cs.read()) {
            nomC_comboBox.getItems().add(c.getNom_categorie());
        }
        //searchFilter();
        afficher_produit(l1);
        initDeleteColumn();

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
    void selectImage(MouseEvent event) {

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

                // Créer l'objet Produit avec l'image Blob

                ProduitService ps = new ProduitService();
                ps.create(new Produit(nomP_textFiled.getText(), prix_textFiled.getText(), imageBlob, descriptionP_textArea.getText(),  new CategorieService().getCategorieByNom(nomC_comboBox.getValue()), Integer.parseInt(quantiteP_textFiled.getText())));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Produit ajoutée");
                alert.setContentText("Produit ajoutée !");
                alert.show();
                afficher_produit(l1);
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
        afficher_produit(l1);
    }





    @FXML
    void modifier_produit(Produit produit) {

        // Récupérez les valeurs modifiées depuis la ligne
        String nouvelleCategorie = produit.getNom_categorie();
        String nouveauNom = produit.getNom();
        String nouveauPrix = produit.getPrix();
        String nouvelleDescription = produit.getDescription();
        Blob img = produit.getImage();
        int nouvelleQuantite = produit.getQuantiteP();
        int id = produit.getId_produit();

        // Enregistrez les modifications dans la base de données en utilisant un service approprié
        ProduitService ps = new ProduitService();
        ps.update(produit);
    }



    @FXML
    void supprimer_produit(Produit produit) {
        Produit selectedProduct = Produit_tableview.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            ProduitService ps = new ProduitService();
            ps.delete(selectedProduct);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Produit supprimé");
            alert.setContentText("Produit supprimé !");
            alert.show();



        }
        // Rafraîchir la TableView après la suppression
afficher_produit(l1);
    }

    private void initDeleteColumn() {

        // Créer une cellule de la colonne Supprimer
        Callback<TableColumn<Produit, Void>, TableCell<Produit, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Produit, Void> call(final TableColumn<Produit, Void> param) {
                final TableCell<Produit, Void> cell = new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {

                        btnDelete.getStyleClass().add("delete-button");
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Produit produit = getTableView().getItems().get(getIndex());
                            supprimer_produit(produit);
                            Produit_tableview.getItems().remove(produit);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnDelete);
                        }
                    }
                };
                return cell;
            }
        };

        // Définir la cellule de la colonne Supprimer
        deleteColumn.setCellFactory(cellFactory);

        // Ajouter la colonne Supprimer à la TableView
        Produit_tableview.getColumns().add(deleteColumn);
    }



    void afficher_produit(List<Produit> listproduit){
        Produit_tableview.getItems().clear();


        // Créer un nouveau ComboBox
        ImageView imageView = new ImageView();

        nomCP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("nom_categorie"));


// Définissez le rendu de la cellule en utilisant le ComboBox
        nomCP_tableC.setCellFactory(column -> new TableCell<Produit, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    CategorieService cs = new CategorieService();
                    ComboBox<String> newComboBox = new ComboBox<>();

                    // Utilisez la méthode getCategorieByNom pour obtenir la catégorie
                    Categorie categorie = cs.getCategorieByNom(item);
                   // ComboBox newComboBox =new ComboBox<>();
                    // Ajoutez le nom de la catégorie au ComboBox
                    newComboBox.setItems(nomC_comboBox.getItems());
                    newComboBox.setValue(categorie.getNom_categorie());

                    // Afficher le ComboBox nouvellement créé dans la cellule
                    setGraphic(newComboBox);
                    newComboBox.setOnAction(event ->{
                        Produit produit = getTableView().getItems().get(getIndex());
                        produit.setCategorie( new CategorieService().getCategorieByNom(newComboBox.getValue()));
                        modifier_produit(produit);
                        newComboBox.getStyleClass().add("combo-box-red");
                    });
                }
            }
        });





        nomP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("nom"));
        nomP_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        nomP_tableC.setOnEditCommit(event -> {
            Produit produit = event.getRowValue();
            produit.setNom(event.getNewValue());
            modifier_produit(produit);
        });



        PrixP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("prix"));
        PrixP_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        PrixP_tableC.setOnEditCommit(event -> {
            Produit produit = event.getRowValue();
            produit.setPrix(event.getNewValue());
            modifier_produit(produit);
        });

        descriptionP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("description"));
        descriptionP_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionP_tableC.setOnEditCommit(event -> {
            Produit produit = event.getRowValue();
            produit.setDescription(event.getNewValue());
            modifier_produit(produit);
        });

        quantiteP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("quantiteP"));
        quantiteP_tableC.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantiteP_tableC.setOnEditCommit(event -> {
            Produit produit = event.getRowValue();
            produit.setQuantiteP(event.getNewValue());
            modifier_produit(produit);
        });

        // Configurer la colonne Logo pour afficher et changer l'image
        image_tableC.setCellValueFactory(cellData -> {
            Produit p = cellData.getValue();

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

        // Configurer l'événement de clic pour changer l'image
        image_tableC.setCellFactory(col -> new TableCell<Produit, ImageView>() {

            private final ImageView imageView = new ImageView();
            private Produit produit;

            {
                setOnMouseClicked(event -> {
                    if (!isEmpty()) {
                        changerImage(produit);
                        afficher_produit(l1);
                    }
                });
            }

            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    imageView.setImage(item.getImage());
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(50);
                    setGraphic(imageView);
                    setText(null);
                }

                produit = getTableRow().getItem();
            }
        });


        // Activer l'édition en cliquant sur une ligne
        Produit_tableview.setEditable(true);

        // Gérer la modification du texte dans une cellule et le valider en appuyant sur Enter
        Produit_tableview.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Produit selectedProduit = Produit_tableview.getSelectionModel().getSelectedItem();
                if (selectedProduit != null) {
                    modifier_produit(selectedProduit);
                }
            }
        });

        // Activer la sélection de cellules
        Produit_tableview.getSelectionModel().setCellSelectionEnabled(true);
        Produit_tableview.getItems().addAll(listproduit);
    }

    // Méthode pour changer l'image
    private void changerImage(Produit produit) {
         FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        selectedFile = fileChooser.showOpenDialog(null);

           Image selectedImage = new Image(selectedFile.toURI().toString());

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
                    produit.setImage(imageBlob);
                    modifier_produit(produit);

        } catch ( SQLException | IOException e) {
                e.printStackTrace();
                showAlert("Erreur lors du chargement de la nouvelle image : " + e.getMessage());
            }
        }
    }

    @FXML
    void GestionCategorie(ActionEvent event) throws IOException {

        // Charger la nouvelle interface ListCinemaAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignCategorieAdmin.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Gestion des categories");
        stage.show();

    }

    @FXML
    public static List<Produit> rechercher(List<Produit> liste, String recherche) {
        List<Produit> resultats = new ArrayList<>();

        for (Produit element : liste) {
            if (element.getNom() != null && element.getNom().contains(recherche)) {
                resultats.add(element);
            }
        }

        return resultats;
    }




}
