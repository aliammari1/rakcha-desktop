package com.esprit.controllers.produits;


import com.esprit.controllers.ClientSideBarController;
import com.esprit.models.produits.Categorie_Produit;
import com.esprit.models.users.Client;
import com.esprit.services.produits.CategorieService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DesignCategorieAdminController {
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> statusCheckBoxes = new ArrayList<>();

    @FXML
    private TableView<Categorie_Produit> categorie_tableview;
    @FXML
    private TextField SearchBar;
    @FXML
    private TableColumn<Categorie_Produit, Void> deleteColumn;
    @FXML
    private TextArea descriptionC_textArea;
    @FXML
    private TextField nomC_textFile;
    @FXML
    private FontAwesomeIconView idfilter;
    @FXML
    private AnchorPane categorieList;
    @FXML
    private AnchorPane FilterAnchor;
    @FXML
    private AnchorPane formulaire;
    @FXML
    private TableColumn<Categorie_Produit, String> nomC_tableC;
    @FXML
    private TableColumn<Categorie_Produit, String> description_tableC;


    @FXML
    void GestionProduit(ActionEvent event) throws IOException {

        // Charger la nouvelle interface ListproduitAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignProduitAdmin.fxml"));
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
    void initialize() {


        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            search(newValue);
            filterCategorieProduits(newValue.trim());
        });


        afficher_categorie();
        initDeleteColumn();


    }

    @FXML
    void ajouter_categorie(ActionEvent event) {
        // Récupérer les valeurs des champs de saisie
        String nomCategorie = nomC_textFile.getText().trim();
        String descriptionCategorie = descriptionC_textArea.getText().trim();

        // Vérifier si les champs sont vides
        if (nomCategorie.isEmpty() || descriptionCategorie.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.show();
            return; // Arrêter l'exécution de la méthode si les champs sont vides
        }

        // Vérifier si la description a au moins 20 caractères
        if (descriptionCategorie.length() < 20) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("La description doit contenir au moins 20 caractères.");
            alert.show();
            return; // Arrêter l'exécution de la méthode si les champs sont vides
        }

        // Créer l'objet Categorie
        CategorieService cs = new CategorieService();
        Categorie_Produit nouvelleCategorieProduit = new Categorie_Produit(nomCategorie, descriptionCategorie);

        // Ajouter le nouveau categorie à la liste existante
        cs.create(nouvelleCategorieProduit);
        categorie_tableview.getItems().add(nouvelleCategorieProduit);

        // Rafraîchir la TableView
        categorie_tableview.refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Categorie ajouté");
        alert.setContentText("Categorie ajouté !");
        alert.show();
    }

    private void initDeleteColumn() {

        Callback<TableColumn<Categorie_Produit, Void>, TableCell<Categorie_Produit, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Categorie_Produit, Void> call(final TableColumn<Categorie_Produit, Void> param) {
                final TableCell<Categorie_Produit, Void> cell = new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        btnDelete.getStyleClass().add("delete-button");
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Categorie_Produit categorieProduit = getTableView().getItems().get(getIndex());
                            CategorieService cs = new CategorieService();
                            cs.delete(categorieProduit);

                            categorie_tableview.getItems().remove(categorieProduit);
                            categorie_tableview.refresh();

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
        //categorie_tableview.getColumns().add(deleteColumn);
    }

    @FXML
    void modifier_categorie(Categorie_Produit categorieProduit) {

        String nouveauNom = categorieProduit.getNom_categorie();
        String nouvelleDescription = categorieProduit.getDescription();

        // Enregistrez les modifications dans la base de données en utilisant un service approprié
        CategorieService ps = new CategorieService();
        ps.update(categorieProduit);


    }

    @FXML
    void afficher_categorie() {


        nomC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie_Produit, String>("nom_categorie"));
        nomC_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        nomC_tableC.setOnEditCommit(event -> {
            Categorie_Produit categorieProduit = event.getRowValue();
            categorieProduit.setNom_categorie(event.getNewValue());
            modifier_categorie(categorieProduit);
        });

        description_tableC.setCellValueFactory(new PropertyValueFactory<Categorie_Produit, String>("description"));
        description_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        description_tableC.setOnEditCommit(event -> {
            Categorie_Produit categorieProduit = event.getRowValue();
            categorieProduit.setDescription(event.getNewValue());
            modifier_categorie(categorieProduit);
        });


        // Activer l'édition en cliquant sur une ligne
        categorie_tableview.setEditable(true);

        // Gérer la modification du texte dans une cellule et le valider en appuyant sur Enter
        categorie_tableview.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Categorie_Produit selectedCategorieProduit = categorie_tableview.getSelectionModel().getSelectedItem();
                if (selectedCategorieProduit != null) {
                    modifier_categorie(selectedCategorieProduit);
                }
            }
        });
        // Utiliser une ObservableList pour stocker les éléments
        ObservableList<Categorie_Produit> list = FXCollections.observableArrayList();
        CategorieService cs = new CategorieService();
        list.addAll(cs.read());
        categorie_tableview.setItems(list);

        // Activer la sélection de cellules
        categorie_tableview.getSelectionModel().setCellSelectionEnabled(true);

    }

    @FXML
    private void search(String keyword) {
        CategorieService categoryService = new CategorieService();
        ObservableList<Categorie_Produit> filteredList = FXCollections.observableArrayList();
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredList.addAll(categoryService.read());
        } else {
            for (Categorie_Produit category : categoryService.read()) {
                if (category.getNom_categorie().toLowerCase().contains(keyword.toLowerCase()) ||
                        category.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(category);
                }
            }
        }
        categorie_tableview.setItems(filteredList);
    }

    private void filterCategorieProduits(String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des cinémas pour ne garder que ceux dont le nom contient le texte saisi
            ObservableList<Categorie_Produit> filteredList = FXCollections.observableArrayList();
            for (Categorie_Produit categorie : categorie_tableview.getItems()) {
                if (categorie.getNom_categorie().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(categorie);
                }
            }
            // Mettre à jour la TableView avec la liste filtrée
            categorie_tableview.setItems(filteredList);
        } else {
            // Si le champ de recherche est vide, afficher tous les cinémas
            afficher_categorie();
        }
    }

    private List<Categorie_Produit> getAllCategories() {
        CategorieService categorieservice = new CategorieService();
        List<Categorie_Produit> categorie = categorieservice.read();
        return categorie;
    }

    @FXML
    void filtrer(MouseEvent event) {

        categorie_tableview.setOpacity(0.5);
        FilterAnchor.setVisible(true);

        // Nettoyer les listes des cases à cocher
        addressCheckBoxes.clear();
        statusCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        List<String> categorie = getCategorie_Produit();


        // Créer des VBox pour les adresses
        VBox addressCheckBoxesVBox = new VBox();
        Label addressLabel = new Label("Category");
        addressLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        addressCheckBoxesVBox.getChildren().add(addressLabel);
        for (String address : categorie) {
            CheckBox checkBox = new CheckBox(address);
            addressCheckBoxesVBox.getChildren().add(checkBox);
            addressCheckBoxes.add(checkBox);
        }
        addressCheckBoxesVBox.setLayoutX(25);
        addressCheckBoxesVBox.setLayoutY(60);


        // Ajouter les VBox dans le FilterAnchor
        FilterAnchor.getChildren().addAll(addressCheckBoxesVBox);
        FilterAnchor.setVisible(true);
    }


    public List<String> getCategorie_Produit() {
        // Récupérer tous les cinémas depuis la base de données
        List<Categorie_Produit> categories = getAllCategories();

        // Extraire les adresses uniques des cinémas
        List<String> categorie = categories.stream()
                .map(Categorie_Produit::getNom_categorie)
                .distinct()
                .collect(Collectors.toList());

        return categorie;
    }

    @FXML
    public void filtercinema(ActionEvent event) {

        categorieList.setOpacity(1);
        formulaire.setOpacity(1);
        categorieList.setVisible(true);
        categorie_tableview.setOpacity(1);


        FilterAnchor.setVisible(false);

        categorie_tableview.setVisible(true);
        formulaire.setVisible(true);

        // Récupérer les adresses sélectionnées
        List<String> selectedCategories = getSelectedCategories();
        // Récupérer les statuts sélectionnés

        Categorie_Produit categorieProduit = new Categorie_Produit();
        // Filtrer les cinémas en fonction des adresses et/ou des statuts sélectionnés
        List<Categorie_Produit> filteredCategories = getAllCategories().stream()
                .filter(c -> selectedCategories.contains(c.getNom_categorie()))
                .collect(Collectors.toList());

        // Mettre à jour le TableView avec les cinémas filtrés
        ObservableList<Categorie_Produit> filteredList = FXCollections.observableArrayList(filteredCategories);
        categorie_tableview.setItems(filteredList);


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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(".fxml"));
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
