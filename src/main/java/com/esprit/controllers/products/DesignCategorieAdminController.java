package com.esprit.controllers.products;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;

import com.esprit.models.products.ProductCategory;
import com.esprit.services.products.CategoryService;

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

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class DesignCategorieAdminController {
    private static final Logger LOGGER = Logger.getLogger(DesignCategorieAdminController.class.getName());
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> statusCheckBoxes = new ArrayList<>();
    @FXML
    private TableView<ProductCategory> categorie_tableview;
    @FXML
    private TextField SearchBar;
    @FXML
    private TableColumn<ProductCategory, Void> deleteColumn;
    @FXML
    private TextArea descriptionC_textArea;
    @FXML
    private TextField nomC_textFile;
    @FXML
    private FontIcon idfilter;
    @FXML
    private AnchorPane categorieList;
    @FXML
    private AnchorPane filterAnchor;
    @FXML
    private AnchorPane formulaire;
    @FXML
    private TableColumn<ProductCategory, String> nomC_tableC;
    @FXML
    private TableColumn<ProductCategory, String> description_tableC;

    /**
     * Opens the DesignProductAdmin view in a new window and closes the current window.
     *
     * @param event the ActionEvent that triggered the navigation
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML
    void GestionProduct(final ActionEvent event) throws IOException {
        // Charger la nouvelle interface ListproduitAdmin.fxml
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/produits/DesignProductAdmin.fxml"));
        final Parent root = loader.load();
        // Créer une nouvelle scène avec la nouvelle interface
        final Scene scene = new Scene(root);
        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Gestion des categories");
        stage.show();
        // Fermer la fenêtre actuelle
        currentStage.close();
    }


    /**
     * Initialize the controller by setting up the search bar listener, populating the category table, and configuring the delete column.
     */
    @FXML
    void initialize() {
        this.SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            this.search(newValue);
            this.filterCategorieProducts(newValue.trim());
        }
);
        this.afficher_categorie();
        this.initDeleteColumn();
    }


    /**
     * Create and persist a product category from the current form inputs and add it to the table view.
     *
     * Validates that name and description are not empty and that description has at least 20 characters;
     * shows an error Alert on validation failure. On success, persists the new ProductCategory via
     * CategoryService, adds it to the table view, refreshes the view, and shows a confirmation Alert.
     *
     * @param event the action event that triggered the handler
     */
    @FXML
    void ajouter_categorie(final ActionEvent event) {
        // Récupérer les valeurs des champs de saisie
        final String nomCategorie = this.nomC_textFile.getText().trim();
        final String descriptionCategorie = this.descriptionC_textArea.getText().trim();
        // Vérifier si les champs sont vides
        if (nomCategorie.isEmpty() || descriptionCategorie.isEmpty()) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.show();
            return; // Arrêter l'exécution de la méthode si les champs sont vides
        }

        // Vérifier si la description a au moins 20 caractères
        if (20 > descriptionCategorie.length()) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("La description doit contenir au moins 20 caractères.");
            alert.show();
            return; // Arrêter l'exécution de la méthode si les champs sont vides
        }

        // Créer l'objet Categorie
        final CategoryService cs = new CategoryService();
        final ProductCategory nouvelleCategorieProduct = new ProductCategory(nomCategorie, descriptionCategorie);
        // Ajouter le nouveau categorie à la liste existante
        cs.create(nouvelleCategorieProduct);
        this.categorie_tableview.getItems().add(nouvelleCategorieProduct);
        // Rafraîchir la TableView
        this.categorie_tableview.refresh();
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Categorie ajouté");
        alert.setContentText("Categorie ajouté !");
        alert.show();
    }


    /**
     * Configure the delete table column to render a per-row "Delete" button.
     *
     * Clicking the button deletes the corresponding ProductCategory via CategoryService
     * and removes it from the table view.
     */
    private void initDeleteColumn() {
        final Callback<TableColumn<ProductCategory, Void>, TableCell<ProductCategory, Void>> cellFactory = new Callback<>() {
            /**
             * Create a TableCell containing a "Delete" button that removes the row's ProductCategory.
             *
             * The button deletes the associated ProductCategory via CategoryService, removes the item
             * from the table's items, and refreshes the table view.
             *
             * @param param the TableColumn for which the cell is being created
             * @return a TableCell whose graphic is a "Delete" button that deletes the row's ProductCategory
             */
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<ProductCategory, Void> call(TableColumn<ProductCategory, Void> param) {
                return new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        this.btnDelete.getStyleClass().add("delete-button");
                        this.btnDelete.setOnAction((final ActionEvent event) -> {
                            final ProductCategory categorieProduct = this.getTableView().getItems()
                                    .get(this.getIndex());
                            final CategoryService cs = new CategoryService();
                            cs.delete(categorieProduct);
                            categorie_tableview.getItems().remove(categorieProduct);
                            categorie_tableview.refresh();
                        }
);
                    }


                    /**
                     * Set the cell's graphic to null when the cell is empty, otherwise set it to the delete button.
                     *
                     * @param item  ignored Void placeholder required by the cell API
                     * @param empty true if the cell is empty, false otherwise
                     */
                    @Override
                    protected void updateItem(final Void item, final boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setGraphic(null);
                        }
 else {
                            this.setGraphic(this.btnDelete);
                        }

                    }

                }
;
            }

        }
;
        this.deleteColumn.setCellFactory(cellFactory);
        // categorie_tableview.getColumns().add(deleteColumn);
    }


    /**
     * Persists updates made to a ProductCategory to the database.
     *
     * @param categorieProduct the ProductCategory whose updated fields should be saved
     */
    @FXML
    void modifier_categorie(final ProductCategory categorieProduct) {
        final String nouveauNom = categorieProduct.getCategoryName();
        final String nouvelleDescription = categorieProduct.getDescription();
        // Enregistrez les modifications dans la base de données en utilisant un service
        // approprié
        final CategoryService ps = new CategoryService();
        ps.update(categorieProduct);
    }


    /**
     * Initialize and populate the category table view for inline editing and selection.
     *
     * Configures the name and description columns as editable text cells, commits edits
     * to the underlying category objects, loads all categories into the table, and
     * enables cell selection. Pressing Enter while a row is selected persists that row's changes.
     */
    @FXML
    void afficher_categorie() {
        this.nomC_tableC.setCellValueFactory(new PropertyValueFactory<ProductCategory, String>("nom_categorie"));
        this.nomC_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        this.nomC_tableC.setOnEditCommit(event -> {
            final ProductCategory categorieProduct = event.getRowValue();
            categorieProduct.setCategoryName(event.getNewValue());
            this.modifier_categorie(categorieProduct);
        }
);
        this.description_tableC.setCellValueFactory(new PropertyValueFactory<ProductCategory, String>("description"));
        this.description_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        this.description_tableC.setOnEditCommit(event -> {
            final ProductCategory categorieProduct = event.getRowValue();
            categorieProduct.setDescription(event.getNewValue());
            this.modifier_categorie(categorieProduct);
        }
);
        // Activer l'édition en cliquant sur une ligne
        this.categorie_tableview.setEditable(true);
        // Gérer la modification du texte dans une cellule et le valider en appuyant sur
        // Enter
        this.categorie_tableview.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                final ProductCategory selectedCategorieProduct = this.categorie_tableview.getSelectionModel()
                        .getSelectedItem();
                if (null != selectedCategorieProduct) {
                    this.modifier_categorie(selectedCategorieProduct);
                }

            }

        }
);
        // Utiliser une ObservableList pour stocker les éléments
        final ObservableList<ProductCategory> list = FXCollections.observableArrayList();
        final CategoryService cs = new CategoryService();
        list.addAll(cs.read());
        this.categorie_tableview.setItems(list);
        // Activer la sélection de cellules
        this.categorie_tableview.getSelectionModel().setCellSelectionEnabled(true);
    }


    /**
     * Filters product categories by a search keyword and updates the table view with the matches.
     *
     * @param keyword text to match against category name or description; if null or empty, all categories are shown
     */
    @FXML
    private void search(final String keyword) {
        final CategoryService categoryService = new CategoryService();
        final ObservableList<ProductCategory> filteredList = FXCollections.observableArrayList();
        if (null == keyword || keyword.trim().isEmpty()) {
            filteredList.addAll(categoryService.read());
        }
 else {
            for (final ProductCategory category : categoryService.read()) {
                if (category.getCategoryName().toLowerCase().contains(keyword.toLowerCase())
                        || category.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(category);
                }

            }

        }

        this.categorie_tableview.setItems(filteredList);
    }


    /**
     * Filters the table's product categories to those whose name contains the given text.
     *
     * Performs a case-insensitive match against each category's name and replaces the
     * TableView items with the matching subset; if the search text is empty, restores
     * the full category list by calling afficher_categorie().
     *
     * @param searchText text to match against category names (case-insensitive)
     */
    private void filterCategorieProducts(final String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des cinémas pour ne garder que ceux dont le nom contient le
            // texte saisi
            final ObservableList<ProductCategory> filteredList = FXCollections.observableArrayList();
            for (final ProductCategory categorie : this.categorie_tableview.getItems()) {
                if (categorie.getCategoryName().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(categorie);
                }

            }

            // Mettre à jour la TableView avec la liste filtrée
            this.categorie_tableview.setItems(filteredList);
        }
 else {
            // Si le champ de recherche est vide, afficher tous les cinémas
            this.afficher_categorie();
        }

    }


    /**
     * Retrieves all product categories from persistent storage.
     *
     * @return a List of ProductCategory objects containing every stored category
     */
    private List<ProductCategory> getAllCategories() {
        final CategoryService categoryservice = new CategoryService();
        return categoryservice.read();
    }


    /**
     * Reveals the category filter panel and populates it with a CheckBox for each available category.
     *
     * The category table is dimmed (opacity set to 0.5), any previously stored checkboxes are cleared, and a VBox containing a "Category" label and one CheckBox per category is added to the filter anchor.
     */
    @FXML
    void filtrer(final MouseEvent event) {
        this.categorie_tableview.setOpacity(0.5);
        this.filterAnchor.setVisible(true);
        // Nettoyer les listes des cases à cocher
        this.addressCheckBoxes.clear();
        this.statusCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        final List<String> categorie = this.getProductCategory();
        // Créer des VBox pour les adresses
        final VBox addressCheckBoxesVBox = new VBox();
        final Label addressLabel = new Label("Category");
        addressLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        addressCheckBoxesVBox.getChildren().add(addressLabel);
        for (final String address : categorie) {
            final CheckBox checkBox = new CheckBox(address);
            addressCheckBoxesVBox.getChildren().add(checkBox);
            this.addressCheckBoxes.add(checkBox);
        }

        addressCheckBoxesVBox.setLayoutX(25);
        addressCheckBoxesVBox.setLayoutY(60);
        // Ajouter les VBox dans le filterAnchor
        this.filterAnchor.getChildren().addAll(addressCheckBoxesVBox);
        this.filterAnchor.setVisible(true);
    }


    /**
     * Retrieve a list of unique product category names.
     *
     * @return a list containing distinct category names from the data source
     */
    public List<String> getProductCategory() {
        // Récupérer tous les cinémas depuis la base de données
        final List<ProductCategory> categories = this.getAllCategories();
        // Extraire les adresses uniques des cinémas
        return categories.stream().map(ProductCategory::getCategoryName).distinct().collect(Collectors.toList());
    }


    /**
     * Apply category checkbox filters to the table and restore the main view layout.
     *
     * Reads selected category names from the UI checkboxes, filters all available
     * ProductCategory items to those whose name is selected, updates the table's
     * items with the filtered list, and restores visibility and opacity of related panes.
     *
     * @param event the ActionEvent that triggered the filter action
     */
    @FXML
    /**
     * Performs filtercinema operation.
     *
     * @return the result of the operation
     */
    public void filtercinema(final ActionEvent event) {
        this.categorieList.setOpacity(1);
        this.formulaire.setOpacity(1);
        this.categorieList.setVisible(true);
        this.categorie_tableview.setOpacity(1);
        this.filterAnchor.setVisible(false);
        this.categorie_tableview.setVisible(true);
        this.formulaire.setVisible(true);
        // Récupérer les adresses sélectionnées
        final List<String> selectedCategories = this.getSelectedCategories();
        // Récupérer les statuts sélectionnés
        final ProductCategory categorieProduct = new ProductCategory();
        // Filtrer les cinémas en fonction des adresses et/ou des statuts sélectionnés
        final List<ProductCategory> filteredCategories = this.getAllCategories().stream()
                .filter(c -> selectedCategories.contains(c.getCategoryName())).collect(Collectors.toList());
        // Mettre à jour le TableView avec les cinémas filtrés
        final ObservableList<ProductCategory> filteredList = FXCollections.observableArrayList(filteredCategories);
        this.categorie_tableview.setItems(filteredList);
    }


    /**
     * Collects the texts of all selected category checkboxes.
     *
     * @return a List<String> containing the text of each selected checkbox
     */
    private List<String> getSelectedCategories() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return this.addressCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                .collect(Collectors.toList());
    }


    /**
     * Opens the product comment view ("/ui/produits/CommentaireProduct.fxml") in a new stage and closes the current stage.
     *
     * @param event the ActionEvent that triggered the navigation; used to locate the current window
     */
    @FXML
    void cinemaclient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(
                    this.getClass().getResource("/ui/produits/CommentaireProduct.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("cinema ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (final IOException e) {
            DesignCategorieAdminController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * Open the Event UI in a new window and close the window that triggered the action.
     *
     * @param event the ActionEvent whose source window will be closed after the new window opens
     */
    @FXML
    void eventClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui//ui/.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Event ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (final IOException e) {
            DesignCategorieAdminController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * Open the product administration view in a new window and close the current window.
     *
     * Loads "/ui/produits/DesignProductAdmin.fxml", displays it in a new Stage titled "products",
     * and closes the stage that originated the action. Any IOExceptions during loading are caught
     * and logged.
     */
    @FXML
    void produitClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(
                    this.getClass().getResource("/ui/produits/DesignProductAdmin.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("products ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (final IOException e) {
            DesignCategorieAdminController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * Placeholder event handler invoked when the user triggers the profile action.
     *
     * @param event the ActionEvent that triggered this handler
     */
    @FXML
    void profilclient(final ActionEvent event) {
    }


    /**
     * Open the film user view in a new window and close the current window.
     *
     * @param event the triggering ActionEvent whose source Node is used to obtain and close the current Stage
     */
    @FXML
    void MovieClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/films/filmuser.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("movie ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (final IOException e) {
            DesignCategorieAdminController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }


    /**
     * Opens the Series view in a new window and closes the current window.
     *
     * <p>Loads the "/ui/series/Serie-view.fxml" interface, shows it in a new Stage titled "series", and closes the originating Stage.</p>
     */
    @FXML
    void SerieClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/series/Serie-view.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("series ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (final IOException e) {
            DesignCategorieAdminController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                        // d'entrée/sortie
        }

    }

}