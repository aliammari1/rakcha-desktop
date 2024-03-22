package com.esprit.controllers.produits;

import com.esprit.models.produits.Categorie_Produit;
import com.esprit.models.produits.Produit;
import com.esprit.services.produits.CategorieService;
import com.esprit.services.produits.ProduitService;
import com.esprit.utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;



public class DesignProduitAdminContoller {

    private File selectedFile; // pour stocke le fichier image selectionné

    @FXML
    private TableColumn<Produit, Integer> PrixP_tableC;

    @FXML
    private TableView<Produit> Produit_tableview;

    @FXML
    private TableColumn<Produit, String> descriptionP_tableC;

    @FXML
    private TextArea descriptionP_textArea;

    @FXML
    private TableColumn<Produit, ImageView> image_tableC;


    @FXML
    private ComboBox<String> nomC_comboBox;

    @FXML
    private TableColumn<Produit, String> nomCP_tableC;

    @FXML
    private TableColumn<Produit, String> nomP_tableC;

    @FXML
    private TextField nomP_textFiled;

    @FXML
    private TextField prix_textFiled;

    @FXML
    private TableColumn<Produit, Integer> quantiteP_tableC;

    @FXML
    private TextField quantiteP_textFiled;


    @FXML
    private ImageView image;

    @FXML
    private TextField SearchBar;
    @FXML
    private AnchorPane categorieList;
    @FXML
    private AnchorPane FilterAnchor;

    @FXML
    private AnchorPane formulaire;





    @FXML
    private TableColumn<Produit, Void> deleteColumn;

    private List<Produit> l1 = new ArrayList<>();


    @FXML
    void initialize() {
        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            search(newValue);
            filterCategorieProduits(newValue.trim());
        });

        CategorieService cs = new CategorieService();

        for (Categorie_Produit c : cs.read()) {
            nomC_comboBox.getItems().add(c.getNom_categorie());
        }


        afficher_produit();
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

        // Vérifier si une image a été sélectionnée
        if (selectedFile != null) {
            // Récupérer les valeurs des champs de saisie
            String nomProduit = nomP_textFiled.getText().trim();
            String prixText = prix_textFiled.getText().trim();
            String descriptionProduit = descriptionP_textArea.getText().trim();
            String nomCategorie = nomC_comboBox.getValue();
            String quantiteText = quantiteP_textFiled.getText().trim();

            // Vérifier si les champs sont vides
            if (nomProduit.isEmpty() || prixText.isEmpty() || descriptionProduit.isEmpty() || nomCategorie == null || quantiteText.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setContentText("Veuillez remplir tous les champs.");
                alert.show();
            }

            try {
                // Convertir le prix et la quantité en entiers
                int prix = Integer.parseInt(prixText);
                int quantite = Integer.parseInt(quantiteText);

                // Vérifier si le prix et la quantité sont des valeurs valides
                if (prix <= 0 || quantite <= 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur de saisie");
                    alert.setContentText("Veuillez entrer des valeurs valides pour le prix et la quantité (supérieures à zéro)");
                    alert.show(); // Arrêter l'exécution de la méthode si les valeurs ne sont pas valides
                }


                // Vérifier si le nom ne contient que des alphabets et des chiffres
                if (!nomProduit.matches("[a-zA-Z0-9]*")) {
                    showAlert("Veuillez entrer un nom valide sans caractères spéciaux.");
                    return; // Arrêter l'exécution de la méthode si le nom n'est pas valide
                }

                // Vérifier si la description a au moins 20 caractères
                if (descriptionProduit.length() < 20) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur de saisie");
                    alert.setContentText("La description doit contenir au moins 20 caractères.");
                    alert.show();
                    return; // Arrêter l'exécution de la méthode si les champs sont vides
                }

                // Convertir le fichier en un objet Blob
                Connection connection = null;
                try {
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
                    CategorieService cs = new CategorieService();
                    Produit nouveauProduit = new Produit(nomProduit, prix, imageBlob, descriptionProduit, cs.getCategorieByNom(nomCategorie), quantite);
                    ps.create(nouveauProduit);

                    // Ajouter le nouveau produit à la liste existante
                    Produit_tableview.getItems().add(nouveauProduit);

                    // Rafraîchir la TableView
                    Produit_tableview.refresh();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Produit ajouté");
                    alert.setContentText("Produit ajouté !");
                    alert.show();
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

            } catch (NumberFormatException e) {
                showAlert("Veuillez entrer des valeurs numériques valides pour le prix et la quantité.");
            }
        } else {
            showAlert("Veuillez sélectionner une image d'abord !");
        }
    }




    @FXML
    void modifier_produit(Produit produit) {

        // Récupérez les valeurs modifiées depuis la ligne
        String nouvelleCategorie = produit.getNom_categorie();
        String nouveauNom = produit.getNom();
        int nouveauPrix = produit.getPrix();
        String nouvelleDescription = produit.getDescription();
        Blob img = produit.getImage();
        int nouvelleQuantite = produit.getQuantiteP();
        int id = produit.getId_produit();

        // Enregistrez les modifications dans la base de données en utilisant un service approprié
        ProduitService ps = new ProduitService();
        ps.update(produit);
    }



    void afficher_produit(){

        // Créer un nouveau ComboBox
        ImageView imageView = new ImageView();

        nomCP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("nom_categorie"));
        nomCP_tableC.setCellFactory(column -> new TableCell<Produit, String>() {
            @Override
           /* protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    CategorieService cs = new CategorieService();
                    ComboBox<String> newComboBox = new ComboBox<>();

                    // Obtenez la liste des noms de catégories
                    List<String> categorieNames = cs.getAllCategoriesNames();

                    // Ajoutez les noms de catégories au ComboBox
                    newComboBox.getItems().addAll(categorieNames);

                    // Sélectionnez le nom de la catégorie associée au produit
                    newComboBox.setValue(item);

                    // Afficher le ComboBox nouvellement créé dans la cellule
                    setGraphic(newComboBox);
                    newComboBox.setOnAction(event -> {
                        Produit produit = getTableView().getItems().get(getIndex());
                        // Mise à jour de la catégorie associée au produit
                        Categorie selectedCategorie = cs.getCategorieByNom(newComboBox.getValue());
                        produit.setCategorie(selectedCategorie);
                        modifier_produit(produit);
                        newComboBox.getStyleClass().add("combo-box-red");

                    });
                }
            }
        });*/

            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }

                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        CategorieService cs = new CategorieService();

                        // Créer un ComboBox contenant les noms des catégories
                        ComboBox<String> produitComboBox = new ComboBox<>();

                        // Obtenez la liste des noms de catégories
                        List<String> categorieNames = cs.getAllCategoriesNames();

                        // Ajoutez les noms de catégories au ComboBox
                        produitComboBox.getItems().addAll(categorieNames);

                        // Sélectionnez le nom de la catégorie associée au produit
                        produitComboBox.setValue(getItem());

                        // Définir un EventHandler pour le changement de sélection dans le ComboBox
                        produitComboBox.setOnAction(e -> {
                            Produit produit = getTableView().getItems().get(getIndex());
                            // Mise à jour de la catégorie associée au produit
                            Categorie_Produit selectedCategorieProduit = cs.getCategorieByNom(produitComboBox.getValue());
                            produit.setCategorie(selectedCategorieProduit);
                            produitComboBox.getStyleClass().add("combo-box-red");

                            // Mise à jour de la cellule à partir du ComboBox
                            commitEdit(produitComboBox.getValue());

                            // Rétablir la classe CSS pour afficher le texte
                            getStyleClass().remove("cell-hide-text");
                        });

                        // Appliquer la classe CSS pour masquer le texte
                        getStyleClass().add("cell-hide-text");

                        // Afficher le ComboBox dans la cellule
                        setGraphic(produitComboBox);
                    }
                });




            }
        });




        nomP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("nom"));
        nomP_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        nomP_tableC.setOnEditCommit(event -> {
            Produit produit = event.getRowValue();
            produit.setNom(event.getNewValue());
            modifier_produit(produit);
        });



        PrixP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("prix"));
        PrixP_tableC.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
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
                        afficher_produit();
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

        // Utiliser une ObservableList pour stocker les éléments
        ObservableList<Produit> list = FXCollections.observableArrayList();
        ProduitService ps = new ProduitService();
        list.addAll(ps.read());
        Produit_tableview.setItems(list);

        // Activer la sélection de cellules
        Produit_tableview.getSelectionModel().setCellSelectionEnabled(true);

    }




    private void initDeleteColumn() {
        Callback<TableColumn<Produit, Void>, TableCell<Produit, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Produit, Void> call(final TableColumn<Produit, Void> param) {
                final TableCell<Produit, Void> cell = new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        btnDelete.getStyleClass().add("delete-button");
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Produit produit = getTableView().getItems().get(getIndex());
                            ProduitService ps = new ProduitService();
                            ps.delete(produit);

                            // Mise à jour de la TableView après la suppression de la base de données
                            Produit_tableview.getItems().remove(produit);
                            Produit_tableview.refresh();
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

        deleteColumn.setCellFactory(cellFactory);
        //Produit_tableview.getColumns().add(deleteColumn);
    }



    // Méthode pour changer l'image
    private void changerImage(Produit produit) {
         FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        selectedFile = fileChooser.showOpenDialog(null);

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


            // Charger la nouvelle interface ListproduitAdmin.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignCategorieAdmin.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Gestion des categories");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        }




    @FXML
    private void search(String keyword) {
        ProduitService produitservice = new ProduitService();
        ObservableList<Produit> filteredList = FXCollections.observableArrayList();
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredList.addAll(produitservice.read());
        } else {
            for (Produit produit : produitservice.read()) {
                if (produit.getNom().toLowerCase().contains(keyword.toLowerCase()) ||
                        produit.getDescription().toLowerCase().contains(keyword.toLowerCase())||
                        produit.getNom_categorie().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(produit);
                }
            }
        }
        Produit_tableview.setItems(filteredList);
    }



    private void filterCategorieProduits(String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des cinémas pour ne garder que ceux dont le nom contient le texte saisi
            ObservableList<Produit> filteredList = FXCollections.observableArrayList();
            for (Produit produit : Produit_tableview.getItems()) {
                if (produit.getCategorie().getNom_categorie().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(produit);
                }
            }
            // Mettre à jour la TableView avec la liste filtrée
            Produit_tableview.setItems(filteredList);
        } else {
            // Si le champ de recherche est vide, afficher tous les cinémas
            afficher_produit();
        }
    }

    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> statusCheckBoxes = new ArrayList<>();



    private List<Produit> getAllCategories() {
        ProduitService categorieservice = new ProduitService();
        List<Produit> categorie = categorieservice.read();
        return categorie;
    }
    @FXML
    void filtrer(MouseEvent event) {

        Produit_tableview.setOpacity(0.5);

        FilterAnchor.setVisible(true);

        // Nettoyer les listes des cases à cocher
        addressCheckBoxes.clear();
        statusCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        List<String> categorie = getCategorie_Produit();


        // Créer des VBox pour les adresses
        VBox addressCheckBoxesVBox = new VBox(5);
        Label addressLabel = new Label("Category");
        addressLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        addressCheckBoxesVBox.getChildren().add(addressLabel);
        for (String address : categorie) {
            CheckBox checkBox = new CheckBox(address);
            addressCheckBoxesVBox.getChildren().add(checkBox);
            addressCheckBoxes.add(checkBox);
        }
        addressCheckBoxesVBox.setLayoutX(25);
        addressCheckBoxesVBox.setLayoutY(25);


        // Ajouter les VBox dans le FilterAnchor
        FilterAnchor.getChildren().addAll(addressCheckBoxesVBox);
        FilterAnchor.setVisible(true);
    }




    public List<String> getCategorie_Produit() {
        // Récupérer tous les cinémas depuis la base de données
        List<Produit> categories = getAllCategories();




        List<String> categorie = categories.stream()
                .map(c -> c.getCategorie().getNom_categorie())
                .distinct()
                .collect(Collectors.toList());

        return categorie;
    }

    @FXML
    public void filtercinema(ActionEvent event) {

        Produit_tableview.setOpacity(1);


        FilterAnchor.setVisible(false);

        Produit_tableview.setVisible(true);
        formulaire.setVisible(true);

        // Récupérer les adresses sélectionnées
        List<String> selectedCategories = getSelectedCategories();
        // Récupérer les statuts sélectionnés

        Produit categorieProduit = new Produit();
        // Filtrer les cinémas en fonction des adresses et/ou des statuts sélectionnés
        List<Produit> filteredCategories = getAllCategories().stream()
                .filter(c -> selectedCategories.contains(c.getCategorie().getNom_categorie()))
                .collect(Collectors.toList());

        // Mettre à jour le TableView avec les cinémas filtrés
        ObservableList<Produit> filteredList = FXCollections.observableArrayList(filteredCategories);
        Produit_tableview.setItems(filteredList);


    }

    private List<String> getSelectedCategories() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return addressCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }





    @FXML
    void cinemaclient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentaireProduit.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("cinema ");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }


    }

    @FXML
    void eventClient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DesignEvenementAdmin.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Event ");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }


    }

    @FXML
    void produitClient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignProduitAdmin.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("products ");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }


    }

    @FXML
    void profilclient(ActionEvent event) {



    }


    @FXML
    void MovieClient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/filmuser.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("movie ");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }
    }

    @FXML
    void SerieClient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Serie-view.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("series ");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }

    }







}
