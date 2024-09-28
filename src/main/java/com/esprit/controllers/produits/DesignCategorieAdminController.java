package com.esprit.controllers.produits;

import com.esprit.models.produits.Categorie_Produit;
import com.esprit.services.produits.CategorieService;
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
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DesignCategorieAdminController {
    private static final Logger LOGGER = Logger.getLogger(DesignCategorieAdminController.class.getName());
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
    private FontIcon idfilter;
    @FXML
    private AnchorPane categorieList;
    @FXML
    private AnchorPane filterAnchor;
    @FXML
    private AnchorPane formulaire;
    @FXML
    private TableColumn<Categorie_Produit, String> nomC_tableC;
    @FXML
    private TableColumn<Categorie_Produit, String> description_tableC;

    /**
     * /**
     * Loads a new UI component called `DesignProduitAdmin.fxml`, creates a new
     * scene
     * with it, obtains the current stage from the event, and then opens a new stage
     * with
     * the new scene, closing the previous one.
     *
     * @param event ActionEvent object that triggered the method call, providing
     *              information
     *              about the action that was performed, such as the source of the
     *              event and any
     *              associated data.
     */
    @FXML
    void GestionProduit(final ActionEvent event) throws IOException {
        // Charger la nouvelle interface ListproduitAdmin.fxml
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/DesignProduitAdmin.fxml"));
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
     * Sets up listeners for changes to the `SearchBar` text property and triggers
     * actions
     * when the search term changes, including searching the products database and
     * filtering
     * product categories based on the new value.
     */
    @FXML
    void initialize() {
        this.SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            this.search(newValue);
            this.filterCategorieProduits(newValue.trim());
        });
        this.afficher_categorie();
        this.initDeleteColumn();
    }

    /**
     * /**
     * Enables user input to create a new category and add it to the existing list
     * in the
     * `Categorie_Produit` class.
     *
     * @param event user action that triggered the method, and it is used to
     *              determine
     *              the specific action taken by the user, such as clicking on the
     *              "Ajouter une
     *              catégorie" button.
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
        final CategorieService cs = new CategorieService();
        final Categorie_Produit nouvelleCategorieProduit = new Categorie_Produit(nomCategorie, descriptionCategorie);
        // Ajouter le nouveau categorie à la liste existante
        cs.create(nouvelleCategorieProduit);
        this.categorie_tableview.getItems().add(nouvelleCategorieProduit);
        // Rafraîchir la TableView
        this.categorie_tableview.refresh();
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Categorie ajouté");
        alert.setContentText("Categorie ajouté !");
        alert.show();
    }

    /**
     * Defines a callback to create a table cell that displays a delete button for
     * each
     * item in a table, and adds an on-click listener to remove the item from the
     * table
     * when clicked.
     */
    private void initDeleteColumn() {
        final Callback<TableColumn<Categorie_Produit, Void>, TableCell<Categorie_Produit, Void>> cellFactory = new Callback<>() {
            /**
             * Creates a new `TableCell` instance and sets its graphic to a `Button` element
             * with
             * a delete icon. When clicked, it triggers an event handler that calls the
             * `delete`
             * method on a `CategorieService` instance, deleting the corresponding
             * `Categorie_Produit`
             * item from the table view.
             *
             * @param param TableColumn object that is used to define the appearance and
             *              behavior
             *              of the table cells in the table view.
             *
             * @returns a `TableCell` object that contains a button with a delete action.
             */
            @Override
            public TableCell<Categorie_Produit, Void> call(TableColumn<Categorie_Produit, Void> param) {
                return new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        this.btnDelete.getStyleClass().add("delete-button");
                        this.btnDelete.setOnAction((final ActionEvent event) -> {
                            final Categorie_Produit categorieProduit = this.getTableView().getItems().get(this.getIndex());
                            final CategorieService cs = new CategorieService();
                            cs.delete(categorieProduit);
                            DesignCategorieAdminController.this.categorie_tableview.getItems().remove(categorieProduit);
                            DesignCategorieAdminController.this.categorie_tableview.refresh();
                        });
                    }

                    /**
                     * Updates the graphical representation of an item based on its emptiness
                     * status.
                     * When the item is empty, the graphic is set to null; otherwise, it is set to a
                     * button representing deletion.
                     *
                     * @param item  Void item that is being updated, and its value is passed to the
                     *              parent
                     *              class's `updateItem()` method for further processing.
                     *
                     * @param empty whether or not the `item` is empty, and its value is used to
                     *              control
                     *              the display of the graphic element `btnDelete`.
                     */
                    @Override
                    protected void updateItem(final Void item, final boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setGraphic(null);
                        } else {
                            this.setGraphic(this.btnDelete);
                        }
                    }
                };
            }
        };
        this.deleteColumn.setCellFactory(cellFactory);
        // categorie_tableview.getColumns().add(deleteColumn);
    }

    /**
     * Modifies the attributes of a `CategorieProduit` object and stores the changes
     * in
     * the database using the `CategorieService`.
     *
     * @param categorieProduit categorization information for a product, which is
     *                         used
     *                         to update the corresponding record in the database
     *                         using the `CategorieService`.
     */
    @FXML
    void modifier_categorie(final Categorie_Produit categorieProduit) {
        final String nouveauNom = categorieProduit.getNom_categorie();
        final String nouvelleDescription = categorieProduit.getDescription();
        // Enregistrez les modifications dans la base de données en utilisant un service
        // approprié
        final CategorieService ps = new CategorieService();
        ps.update(categorieProduit);
    }

    /**
     * Sets up a table view to display and edit category data from a service. It
     * allows
     * for line editing and validation on enter press, and enables cell selection.
     */
    @FXML
    void afficher_categorie() {
        this.nomC_tableC.setCellValueFactory(new PropertyValueFactory<Categorie_Produit, String>("nom_categorie"));
        this.nomC_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        this.nomC_tableC.setOnEditCommit(event -> {
            final Categorie_Produit categorieProduit = event.getRowValue();
            categorieProduit.setNom_categorie(event.getNewValue());
            this.modifier_categorie(categorieProduit);
        });
        this.description_tableC.setCellValueFactory(new PropertyValueFactory<Categorie_Produit, String>("description"));
        this.description_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        this.description_tableC.setOnEditCommit(event -> {
            final Categorie_Produit categorieProduit = event.getRowValue();
            categorieProduit.setDescription(event.getNewValue());
            this.modifier_categorie(categorieProduit);
        });
        // Activer l'édition en cliquant sur une ligne
        this.categorie_tableview.setEditable(true);
        // Gérer la modification du texte dans une cellule et le valider en appuyant sur
        // Enter
        this.categorie_tableview.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                final Categorie_Produit selectedCategorieProduit = this.categorie_tableview.getSelectionModel().getSelectedItem();
                if (null != selectedCategorieProduit) {
                    this.modifier_categorie(selectedCategorieProduit);
                }
            }
        });
        // Utiliser une ObservableList pour stocker les éléments
        final ObservableList<Categorie_Produit> list = FXCollections.observableArrayList();
        final CategorieService cs = new CategorieService();
        list.addAll(cs.read());
        this.categorie_tableview.setItems(list);
        // Activer la sélection de cellules
        this.categorie_tableview.getSelectionModel().setCellSelectionEnabled(true);
    }

    /**
     * Takes a search keyword and searches the `CategorieService` for matching
     * categories,
     * adding them to an observable list which is then set as the table view's
     * items.
     *
     * @param keyword search term that filters the data displayed in the
     *                `categorie_tableview`.
     */
    @FXML
    private void search(final String keyword) {
        final CategorieService categoryService = new CategorieService();
        final ObservableList<Categorie_Produit> filteredList = FXCollections.observableArrayList();
        if (null == keyword || keyword.trim().isEmpty()) {
            filteredList.addAll(categoryService.read());
        } else {
            for (final Categorie_Produit category : categoryService.read()) {
                if (category.getNom_categorie().toLowerCase().contains(keyword.toLowerCase())
                        || category.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(category);
                }
            }
        }
        this.categorie_tableview.setItems(filteredList);
    }

    /**
     * Takes a search text as input and filters the observable list of
     * `Categorie_Produit`
     * objects in the `categorie_tableview` based on the search text. It updates the
     * `categorie_tableview` with the filtered list.
     *
     * @param searchText search term used to filter the list of cinemas.
     */
    private void filterCategorieProduits(final String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des cinémas pour ne garder que ceux dont le nom contient le
            // texte saisi
            final ObservableList<Categorie_Produit> filteredList = FXCollections.observableArrayList();
            for (final Categorie_Produit categorie : this.categorie_tableview.getItems()) {
                if (categorie.getNom_categorie().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(categorie);
                }
            }
            // Mettre à jour la TableView avec la liste filtrée
            this.categorie_tableview.setItems(filteredList);
        } else {
            // Si le champ de recherche est vide, afficher tous les cinémas
            this.afficher_categorie();
        }
    }

    /**
     * Retrieves all categories from the database through the `CategorieService`. It
     * returns a list of `Categorie_Produit` objects representing the retrieved
     * categories.
     *
     * @returns a list of `Categorie_Produit` objects containing all categories for
     * which
     * category data is available.
     */
    private List<Categorie_Produit> getAllCategories() {
        final CategorieService categorieservice = new CategorieService();
        return categorieservice.read();
    }

    /**
     * 1) sets the opacity of a category tableview to 0.5, 2) sets a filter anchor's
     * visibility to true, and 3) clears any previously selected addresses from
     * checkboxes
     * before recurring addresses from a database and displaying them in a new VBox
     * added
     * to the filter anchor.
     *
     * @param event mouse event that triggered the function execution and is not
     *              used in
     *              this specific code snippet.
     */
    @FXML
    void filtrer(final MouseEvent event) {
        this.categorie_tableview.setOpacity(0.5);
        this.filterAnchor.setVisible(true);
        // Nettoyer les listes des cases à cocher
        this.addressCheckBoxes.clear();
        this.statusCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        final List<String> categorie = this.getCategorie_Produit();
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
     * Retrieves a list of unique movie theater addresses from the database based on
     * their
     * names.
     *
     * @returns a list of unique cinema addresses.
     */
    public List<String> getCategorie_Produit() {
        // Récupérer tous les cinémas depuis la base de données
        final List<Categorie_Produit> categories = this.getAllCategories();
        // Extraire les adresses uniques des cinémas
        return categories.stream()
                .map(Categorie_Produit::getNom_categorie)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Filters cinema list based on selected categories and statuses from the list
     * of all
     * cinemas. It retrieves the addresses and/or statuses of the selected
     * categories,
     * streams them into a list of all cinemas, filters the cinemas based on the
     * retrieved
     * information, and updates the tableview with the filtered list.
     *
     * @param event ActionEvent that triggered the function, and it is not used or
     *              referred
     *              to within the provided code snippet.
     */
    @FXML
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
        final Categorie_Produit categorieProduit = new Categorie_Produit();
        // Filtrer les cinémas en fonction des adresses et/ou des statuts sélectionnés
        final List<Categorie_Produit> filteredCategories = this.getAllCategories().stream()
                .filter(c -> selectedCategories.contains(c.getNom_categorie()))
                .collect(Collectors.toList());
        // Mettre à jour le TableView avec les cinémas filtrés
        final ObservableList<Categorie_Produit> filteredList = FXCollections.observableArrayList(filteredCategories);
        this.categorie_tableview.setItems(filteredList);
    }

    /**
     * Streamlines and filters the selected addresses within an AnchorPane
     * component,
     * returning a list of selected categories as strings.
     *
     * @returns a list of selected addresses from an AnchorPane of filtering
     * controls.
     */
    private List<String> getSelectedCategories() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return this.addressCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    /**
     * Loads a new user interface "CommentaireProduit.fxml" using the FXMLLoader,
     * creates
     * a new scene from it, and attaches it to a new stage. It then closes the
     * current
     * stage and shows the new one.
     *
     * @param event ActionEvent that triggered the function, providing information
     *              about
     *              the source of the event and the event itself.
     */
    @FXML
    void cinemaclient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/CommentaireProduit.fxml"));
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
            DesignCategorieAdminController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }

    /**
     * Loads a new FXML file, creates a new scene, and attaches it to a new stage
     * when
     * an event is triggered. It then closes the current stage.
     *
     * @param event ActionEvent that triggered the event handling method, providing
     *              access
     *              to information about the action that occurred and the underlying
     *              source node.
     */
    @FXML
    void eventClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource(".fxml"));
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
            DesignCategorieAdminController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }

    /**
     * Loads a new FXML file, creates a new scene and attaches it to a new stage,
     * replacing
     * the current stage.
     *
     * @param event ActionEvent object that triggers the fonction and provides
     *              access to
     *              information about the event, such as the source of the event and
     *              the event's type.
     */
    @FXML
    void produitClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/DesignProduitAdmin.fxml"));
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
            DesignCategorieAdminController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }

    /**
     * Likely performs some client-side profiling tasks, such as collecting and
     * analyzing
     * data on performance metrics for a given application or user session.
     *
     * @param event event that triggered the execution of the `profilclient` method.
     */
    @FXML
    void profilclient(final ActionEvent event) {
    }

    /**
     * Charges a new UI file, creates a new scene, and attaches it to a new stage.
     * It
     * then closes the current stage and displays the new one.
     *
     * @param event action event that triggers the function and provides access to
     *              the
     *              source node of the event, which is used to load the FXML file.
     */
    @FXML
    void MovieClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/filmuser.fxml"));
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
            DesignCategorieAdminController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }

    /**
     * Charges a new UI file "Series-view.fxml" into an existing scene, creates a
     * new
     * stage with the new interface and attaches it to the current stage, closing
     * the
     * original stage upon attachment.
     *
     * @param event ActionEvent object that triggers the method, providing access to
     *              information about the action that triggered the method call,
     *              such as the source
     *              of the action and any related data.
     */
    @FXML
    void SerieClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/Serie-view.fxml"));
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
            DesignCategorieAdminController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }
}
