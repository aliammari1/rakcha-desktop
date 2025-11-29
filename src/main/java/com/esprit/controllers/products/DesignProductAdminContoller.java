package com.esprit.controllers.products;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esprit.models.products.Product;
import com.esprit.models.products.ProductCategory;
import com.esprit.services.products.CategoryService;
import com.esprit.services.products.ProductService;
import com.esprit.utils.CloudinaryStorage;
import com.esprit.utils.DataSource;
import com.esprit.utils.PageRequest;

import javafx.beans.property.SimpleObjectProperty;
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

/**
 * Is responsible for handling user interactions related to the "Products"
 * section of the Rakcha web application. It contains several methods that
 * handle different types of user actions, such as importing images, creating a
 * new product, and viewing a product details page. The controller also uses
 * FXML to load user interface elements from a file named
 * "/ui/produits/DesignProductAdmin.fxml".
 */
public class DesignProductAdminContoller {
    private static final Logger LOGGER = Logger.getLogger(DesignProductAdminContoller.class.getName());
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> statusCheckBoxes = new ArrayList<>();
    private final List<Product> l1 = new ArrayList<>();
    private File selectedFile;
    private String cloudinaryImageUrl;
    @FXML
    private TableColumn<Product, Integer> PrixP_tableC;
    @FXML
    private TableView<Product> Product_tableview;
    @FXML
    private TableColumn<Product, String> descriptionP_tableC;
    @FXML
    private TextArea descriptionP_textArea;
    @FXML
    private TableColumn<Product, ImageView> image_tableC;
    @FXML
    private ComboBox<String> nomC_comboBox;
    @FXML
    private TableColumn<Product, String> nomCP_tableC;
    @FXML
    private TableColumn<Product, String> nomP_tableC;
    @FXML
    private TextField nomP_textFiled;
    @FXML
    private TextField prix_textFiled;
    @FXML
    private TableColumn<Product, Integer> quantiteP_tableC;
    @FXML
    private TextField quantiteP_textFiled;
    @FXML
    private ImageView image;
    @FXML
    private TextField SearchBar;
    @FXML
    private AnchorPane categorieList;
    @FXML
    private AnchorPane filterAnchor;
    @FXML
    private AnchorPane formulaire;
    @FXML
    private TableColumn<Product, Void> deleteColumn;

    /**
         * Initialize the controller: wire search and category-filter listeners, populate the
         * category combo box, and configure the product table and delete column.
         *
         * <p>Wires the SearchBar text changes to update displayed products via
         * search(...) and filterCategorieProducts(...), loads category names from
         * CategoryService into {@code nomC_comboBox}, and calls {@code afficher_produit()}
         * and {@code initDeleteColumn()} to set up the table view.
         */
    @FXML
    void initialize() {
        this.SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            this.search(newValue);
            this.filterCategorieProducts(newValue.trim());
        }
);
        final CategoryService cs = new CategoryService();
        for (final ProductCategory c : cs.read()) {
            this.nomC_comboBox.getItems().add(c.getCategoryName());
        }

        this.afficher_produit();
        this.initDeleteColumn();
    }


    /**
     * Display an information alert dialog showing the given message.
     *
     * The alert uses the title "Information" and no header text.
     *
     * @param message the text to display as the alert's content
     */
    @FXML
    private void showAlert(final String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }


    /**
     * Prompt the user to choose an image file and, if one is selected, store it and display it in the controller's ImageView.
     *
     * @param event the MouseEvent that triggered the file chooser
     */
    @FXML
    void selectImage(final MouseEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        this.selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            final Image selectedImage = new Image(this.selectedFile.toURI().toString());
            this.image.setImage(selectedImage);
        }

    }


    /**
     * Create a new Product from the form, validate inputs, persist it, and append it to the products table.
     *
     * Validations: requires a selected image; all form fields present; price and quantity greater than zero;
     * name contains only letters and digits; description at least 20 characters. On success the product is persisted
     * and added to the Product_tableview; on failure an alert is shown describing the problem.
     */
    @FXML
    /**
     * Performs add_produit operation.
     *
     * @return the result of the operation
     */
    public void add_produit(final ActionEvent actionEvent) {
        // Vérifier si une image a été sélectionnée
        if (null != selectedFile) {
            // Récupérer les valeurs des champs de saisie
            final String nomProduct = this.nomP_textFiled.getText().trim();
            final String prixText = this.prix_textFiled.getText().trim();
            final String descriptionProduct = this.descriptionP_textArea.getText().trim();
            final String nomCategorie = this.nomC_comboBox.getValue();
            final String quantiteText = this.quantiteP_textFiled.getText().trim();
            // Vérifier si les champs sont vides
            if (nomProduct.isEmpty() || prixText.isEmpty() || descriptionProduct.isEmpty() || null == nomCategorie
                    || quantiteText.isEmpty()) {
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setContentText("Veuillez remplir tous les champs.");
                alert.show();
            }

            try {
                // Convertir le prix et la quantité en entiers
                final int prix = Integer.parseInt(prixText);
                final int quantite = Integer.parseInt(quantiteText);
                // Vérifier si le prix et la quantité sont des valeurs valides
                if (0 >= prix || 0 >= quantite) {
                    final Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur de saisie");
                    alert.setContentText(
                            "Veuillez entrer des valeurs valides pour le prix et la quantité (supérieures à zéro)");
                    alert.show(); // Arrêter l'exécution de la méthode si les valeurs ne sont pas valides
                }

                // Vérifier si le nom ne contient que des alphabets et des chiffres
                if (!nomProduct.matches("[a-zA-Z0-9]*")) {
                    this.showAlert("Veuillez entrer un nom valide sans caractères spéciaux.");
                    return; // Arrêter l'exécution de la méthode si le nom n'est pas valide
                }

                // Vérifier si la description a au moins 20 caractères
                if (20 > descriptionProduct.length()) {
                    final Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur de saisie");
                    alert.setContentText("La description doit contenir au moins 20 caractères.");
                    alert.show();
                    return; // Arrêter l'exécution de la méthode si les champs sont vides
                }

                try {
                    URI uri = null;
                    final String fullPath = this.image.getImage().getUrl();
                    DesignProductAdminContoller.LOGGER.info(fullPath);
                    final String requiredPath = fullPath.substring(fullPath.indexOf("/img/produit/"));
                    uri = new URI(requiredPath);
                    final ProductService ps = new ProductService();
                    final CategoryService cs = new CategoryService();
                    final Product nouveauProduct = new Product(nomProduct, prix, uri.getPath(), descriptionProduct,
                            List.of(cs.getCategoryByName(nomCategorie)), quantite);
                    DesignProductAdminContoller.LOGGER.info(nomProduct + " " + prix + " " + uri.getPath() + " "
                            + descriptionProduct + " " + cs.getCategoryByName(nomCategorie) + " " + quantite);
                    DesignProductAdminContoller.LOGGER.info("nouveauProduct: --------------- " + nouveauProduct);
                    ps.create(nouveauProduct);
                    // Ajouter le nouveau produit à la liste existante
                    this.Product_tableview.getItems().add(nouveauProduct);
                    // Rafraîchir la TableView
                    this.Product_tableview.refresh();
                    final Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Product ajouté");
                    alert.setContentText("Product ajouté !");
                    alert.show();
                } catch (final Exception e) {
                    DesignProductAdminContoller.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    this.showAlert("Erreur lors de l'ajout du produit : " + e.getMessage());
                }

            } catch (final NumberFormatException e) {
                this.showAlert("Veuillez entrer des valeurs numériques valides pour le prix et la quantité.");
            }

        }
 else {
            this.showAlert("Veuillez sélectionner une image d'abord !");
        }

    }


    /**
     * Persist changes of the given Product to the application's data store.
     *
     * Updates the stored product record so its fields (category, name, price,
     * description, image, quantity, etc.) match the provided Product instance.
     *
     * @param produit the Product instance whose current field values will be persisted; must have a valid identifier
     */
    @FXML
    void modifier_produit(final Product produit) {
        // Récupérez les valeurs modifiées depuis la ligne
        final String nouvelleCategorie = produit.getCategoryNames();
        final String nouveauNom = produit.getName();
        final int nouveauPrix = produit.getPrice();
        final String nouvelleDescription = produit.getDescription();
        final String img = produit.getImage();
        final int nouvelleQuantite = produit.getQuantity();
        final Long id = produit.getId();
        // Enregistrez les modifications dans la base de données en utilisant un service
        // approprié
        final ProductService ps = new ProductService();
        ps.update(produit);
    }


    /**
     * Configure the Product_tableview: set up cell value factories, custom cell factories,
     * and commit handlers for editing product fields; enable per-cell editing and image
     * interactions; and load the initial page of products into the table.
     *
     * <p>Specifically, this method:
     * - Binds columns to Product properties (category, name, price, description, quantity, image).
     * - Provides an inline ComboBox edit for a product's category (activated by double-click)
     *   and commits category changes to persistence.
     * - Enables in-place editing for name, price, description and quantity and persists commits.
     * - Renders product images in the image column, shows a placeholder when missing, and
     *   triggers an image-change flow when an image cell is clicked.
     * - Commits pending edits when Enter is pressed and populates the table from ProductService.
     */
    void afficher_produit() {
        // Créer un nouveau ComboBox
        final ImageView imageView = new ImageView();
        this.nomCP_tableC.setCellValueFactory(new PropertyValueFactory<Product, String>("nom_categorie"));
        this.nomCP_tableC.setCellFactory(column -> new TableCell<Product, String>() {
            /**
             * Displays a category ComboBox on double-click and updates the product's category when selection changes.
             *
             * When the cell is double-clicked, the cell graphic is replaced with a ComboBox populated with category names;
             * selecting an entry sets the Product's categories to the chosen category, commits the cell edit with the selected
             * name, and restores the cell display.
             *
             * @param item the current cell value (category name) being rendered
             * @param empty true if the cell is empty and should display no content
             */
            @Override
            /*
             * protected void updateItem(String item, boolean empty) {
             * super.updateItem(item, empty);
             *
             * if (item == null || empty) { setGraphic(null); }
 else { CategoryService cs =
             * new CategoryService(); ComboBox<String> newComboBox = new ComboBox<>();
             *
             * // Obtenez la liste des noms de catégories List<String> categorieNames =
             * cs.getAllCategoriesNames();
             *
             * // Ajoutez les noms de catégories au ComboBox
             * newComboBox.getItems().addAll(categorieNames);
             *
             * // Sélectionnez le nom de la catégorie associée au produit
             * newComboBox.setValue(item);
             *
             * // Afficher le ComboBox nouvellement créé dans la cellule
             * setGraphic(newComboBox); newComboBox.setOnAction(event -> { Product produit =
             * getTableView().getItems().get(getIndex()); // Mise à jour de la catégorie
             * associée au produit Categorie selectedCategorie =
             * cs.getCategorieByNom(newComboBox.getValue());
             * produit.setCategorie(selectedCategorie); modifier_produit(produit);
             * newComboBox.getStyleClass().add("combo-box-red");
             *
             * }
); }
 }
 }
);
             */
            protected void updateItem(final String item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    this.setText(null);
                }
 else {
                    this.setText(item);
                }

                this.setOnMouseClicked(event -> {
                    if (2 == event.getClickCount()) {
                        final CategoryService cs = new CategoryService();
                        // Créer un ComboBox contenant les noms des catégories
                        final ComboBox<String> produitComboBox = new ComboBox<>();
                        // Obtenez la liste des noms de catégories
                        final List<String> categorieNames = cs.getAllCategoriesNames();
                        // Ajoutez les noms de catégories au ComboBox
                        produitComboBox.getItems().addAll(categorieNames);
                        // Sélectionnez le nom de la catégorie associée au produit
                        produitComboBox.setValue(this.getItem());
                        // Définir un EventHandler pour le changement de sélection dans le ComboBox
                        produitComboBox.setOnAction(e -> {
                            final Product produit = this.getTableView().getItems().get(this.getIndex());
                            // Mise à jour de la catégorie associée au produit
                            final ProductCategory selectedCategorieProduct = cs
                                    .getCategoryByName(produitComboBox.getValue());
                            produit.setCategories(List.of(selectedCategorieProduct));
                            produitComboBox.getStyleClass().add("combo-box-red");
                            // Mise à jour de la cellule à partir du ComboBox
                            this.commitEdit(produitComboBox.getValue());
                            // Rétablir la classe CSS pour afficher le texte
                            this.getStyleClass().remove("cell-hide-text");
                        }
);
                        // Appliquer la classe CSS pour masquer le texte
                        this.getStyleClass().add("cell-hide-text");
                        // Afficher le ComboBox dans la cellule
                        this.setGraphic(produitComboBox);
                    }

                }
);
            }

        }
);
        this.nomP_tableC.setCellValueFactory(new PropertyValueFactory<Product, String>("nom"));
        this.nomP_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        this.nomP_tableC.setOnEditCommit(event -> {
            final Product produit = event.getRowValue();
            produit.setName(event.getNewValue());
            this.modifier_produit(produit);
        }
);
        this.PrixP_tableC.setCellValueFactory(new PropertyValueFactory<Product, Integer>("prix"));
        this.PrixP_tableC.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        this.PrixP_tableC.setOnEditCommit(event -> {
            final Product produit = event.getRowValue();
            produit.setPrice(event.getNewValue());
            this.modifier_produit(produit);
        }
);
        this.descriptionP_tableC.setCellValueFactory(new PropertyValueFactory<Product, String>("description"));
        this.descriptionP_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        this.descriptionP_tableC.setOnEditCommit(event -> {
            final Product produit = event.getRowValue();
            produit.setDescription(event.getNewValue());
            this.modifier_produit(produit);
        }
);
        this.quantiteP_tableC.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantiteP"));
        this.quantiteP_tableC.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        this.quantiteP_tableC.setOnEditCommit(event -> {
            final Product produit = event.getRowValue();
            produit.setQuantity(event.getNewValue());
            this.modifier_produit(produit);
        }
);
        // Configurer la colonne Logo pour afficher et changer l'image
        this.image_tableC.setCellValueFactory(cellData -> {
            final Product p = cellData.getValue();
            imageView.setFitWidth(100); // Réglez la largeur de l'image selon vos préférences
            imageView.setFitHeight(50); // Réglez la hauteur de l'image selon vos préférences
            try {
                final String pImage = p.getImage();
                if (!pImage.isEmpty()) {
                    final Image image = new Image(pImage);
                    imageView.setImage(image);
                }
 else {
                    // Afficher une image par défaut si le logo est null
                    final Image defaultImage = new Image(
                            Objects.requireNonNull(this.getClass().getResourceAsStream("default_image.png")));
                    imageView.setImage(defaultImage);
                }

            } catch (final Exception e) {
                DesignProductAdminContoller.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

            return new SimpleObjectProperty<>(imageView);
        }
);
        // Configurer l'événement de clic pour changer l'image
        this.image_tableC.setCellFactory(col -> new TableCell<Product, ImageView>() {
            private final ImageView imageView = new ImageView();
            private Product produit;

            {
                this.setOnMouseClicked(event -> {
                    if (!this.isEmpty()) {
                        changerImage(this.produit);
                        afficher_produit();
                    }

                }
);
            }


            /**
             * Update the cell's graphic to show the provided ImageView and record the cell's product.
             *
             * When `item` is non-null and `empty` is false, the cell's internal ImageView is set to
             * the provided image and resized to 100x50; the cell's graphic is set to that ImageView
             * and text is cleared. When `empty` is true or `item` is null, the cell's graphic and text
             * are cleared.
             *
             * @param item  the ImageView whose Image will be displayed in the cell when present
             * @param empty true if the cell should be cleared (no graphic or text), false otherwise
             */
            @Override
            protected void updateItem(final ImageView item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    this.setGraphic(null);
                    this.setText(null);
                }
 else {
                    this.imageView.setImage(item.getImage());
                    this.imageView.setFitWidth(100);
                    this.imageView.setFitHeight(50);
                    this.setGraphic(this.imageView);
                    this.setText(null);
                }

                this.produit = this.getTableRow().getItem();
            }

        }
);
        // Activer l'édition en cliquant sur une ligne
        this.Product_tableview.setEditable(true);
        // Gérer la modification du texte dans une cellule et le valider en appuyant sur
        // Enter
        this.Product_tableview.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                final Product selectedProduct = this.Product_tableview.getSelectionModel().getSelectedItem();
                if (null != selectedProduct) {
                    this.modifier_produit(selectedProduct);
                }

            }

        }
);
        // Utiliser une ObservableList pour stocker les éléments
        final ObservableList<Product> list = FXCollections.observableArrayList();
        final ProductService ps = new ProductService();
        PageRequest pageRequest = new PageRequest(0, 10);
        list.addAll(ps.read(pageRequest).getContent());
        this.Product_tableview.setItems(list);
        // Activer la sélection de cellules
        this.Product_tableview.getSelectionModel().setCellSelectionEnabled(true);
    }


    /**
     * Configure the delete column so each table row shows a "Delete" button that removes that product and refreshes the table.
     *
     * Creates and sets a cell factory that renders a per-row "Delete" button which, when activated, deletes the row's Product via ProductService, removes it from Product_tableview items, and refreshes the TableView.
     */
    private void initDeleteColumn() {
        final Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<>() {
            /**
             * Create a TableCell that displays a "Delete" button which removes the cell's Product from persistence and from the TableView.
             *
             * @param param the TableColumn for which this cell factory is created; used to locate the row's Product when the button is pressed
             * @return a TableCell whose graphic is a "Delete" button that deletes the row's Product from the data store and removes it from the TableView
             */
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<Product, Void> call(TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        this.btnDelete.getStyleClass().add("delete-button");
                        this.btnDelete.setOnAction((final ActionEvent event) -> {
                            final Product produit = this.getTableView().getItems().get(this.getIndex());
                            final ProductService ps = new ProductService();
                            ps.delete(produit);
                            // Mise à jour de la TableView après la suppression de la base de données
                            Product_tableview.getItems().remove(produit);
                            Product_tableview.refresh();
                        }
);
                    }


                    /**
                     * Update the cell graphic: clears it when the cell is empty, otherwise shows the delete button.
                     *
                     * @param item the cell value (unused)
                     * @param empty true if the cell has no content and should not show a graphic
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
        // Product_tableview.getColumns().add(deleteColumn);
    }


    // Méthode pour changer l'image

    /**
         * Replace the given product's image by prompting the user to select a file and persisting the change.
         *
         * If the user cancels selection, the product is not modified. On failure the method displays an error alert.
         *
         * @param produit the Product to update; its image field will be set to the selected file's URL and the change persisted
         */
    private void changerImage(final Product produit) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        this.selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) { // Vérifier si une image a été sélectionnée
            Connection connection = null;
            try {
                connection = DataSource.getInstance().getConnection();
                produit.setImage(this.selectedFile.toURI().toURL().toString());
                this.modifier_produit(produit);
            } catch (final Exception e) {
                DesignProductAdminContoller.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                this.showAlert("Erreur lors du chargement de la nouvelle image : " + e.getMessage());
            }

        }

    }


    /**
     * Open the category management UI (DesignCategorieAdmin.fxml) in a new window and close the current window.
     *
     * @param event the ActionEvent that triggered the navigation
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML
    void GestionCategorie(final ActionEvent event) throws IOException {
        // Charger la nouvelle interface ListproduitAdmin.fxml
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/produits/DesignCategorieAdmin.fxml"));
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
     * Filter and display products whose name, description, or category names contain the given keyword.
     *
     * If `keyword` is null or empty, the first page of products is displayed.
     *
     * @param keyword search query; matched case-insensitively against product name, description, and category names
     */
    @FXML
    private void search(final String keyword) {
        final ProductService produitservice = new ProductService();
        final ObservableList<Product> filteredList = FXCollections.observableArrayList();
        PageRequest pageRequest = new PageRequest(0, 10);
        if (null == keyword || keyword.trim().isEmpty()) {
            filteredList.addAll(produitservice.read(pageRequest).getContent());
        }
 else {
            for (final Product produit : produitservice.read(pageRequest).getContent()) {
                if (produit.getName().toLowerCase().contains(keyword.toLowerCase())
                        || produit.getDescription().toLowerCase().contains(keyword.toLowerCase())
                        || produit.getCategoryNames().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(produit);
                }

            }

        }

        this.Product_tableview.setItems(filteredList);
    }


    /**
     * Filters the Product_tableview to products that have at least one category whose name contains the given query (case-insensitive).
     *
     * If the query is empty, restores the table to show the unfiltered product list.
     *
     * @param searchText text to match against product category names (case-insensitive)
     */
    private void filterCategorieProducts(final String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des cinémas pour ne garder que ceux dont le nom contient le
            // texte saisi
            final ObservableList<Product> filteredList = FXCollections.observableArrayList();
            for (final Product produit : this.Product_tableview.getItems()) {
                for (final ProductCategory productCategory : produit.getCategories())
                    if (productCategory.getCategoryName().toLowerCase().contains(searchText.toLowerCase())) {
                        filteredList.add(produit);
                    }

            }

            // Mettre à jour la TableView avec la liste filtrée
            this.Product_tableview.setItems(filteredList);
        }
 else {
            // Si le champ de recherche est vide, afficher tous les cinémas
            this.afficher_produit();
        }

    }


    /**
     * Fetches the first page of products (up to 10 items) from ProductService.
     *
     * @return the list of products from page 0, containing at most 10 items
     */
    private List<Product> getAllCategories() {
        final ProductService categoryservice = new ProductService();
        PageRequest pageRequest = new PageRequest(0, 10);
        return categoryservice.read(pageRequest).getContent();
    }


    /**
         * Show the category filter panel and populate it with a checkbox for each distinct product category.
         *
         * Dims the product table, clears and fills the controller's checkbox lists with created CheckBox nodes,
         * adds them into the filterAnchor container, and makes the filter UI visible.
         */
    @FXML
    void filtrer(final MouseEvent event) {
        this.Product_tableview.setOpacity(0.5);
        this.filterAnchor.setVisible(true);
        // Nettoyer les listes des cases à cocher
        this.addressCheckBoxes.clear();
        this.statusCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        final List<String> categorie = this.getProductCategory();
        // Créer des VBox pour les adresses
        final VBox addressCheckBoxesVBox = new VBox(5);
        final Label addressLabel = new Label("Category");
        addressLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        addressCheckBoxesVBox.getChildren().add(addressLabel);
        for (final String address : categorie) {
            final CheckBox checkBox = new CheckBox(address);
            addressCheckBoxesVBox.getChildren().add(checkBox);
            this.addressCheckBoxes.add(checkBox);
        }

        addressCheckBoxesVBox.setLayoutX(25);
        addressCheckBoxesVBox.setLayoutY(25);
        // Ajouter les VBox dans le filterAnchor
        this.filterAnchor.getChildren().addAll(addressCheckBoxesVBox);
        this.filterAnchor.setVisible(true);
    }


    /**
     * Collects and returns distinct product category names present in the database.
     *
     * @return a list of distinct category names found across all products
     */
    public List<String> getProductCategory() {
        // Récupérer tous les cinémas depuis la base de données
        final List<Product> categories = this.getAllCategories();
        return categories.stream().flatMap(c -> c.getCategories().stream()).map(c -> c.getCategoryName()).distinct()
                .collect(Collectors.toList());
    }


    /**
     * Applies the currently selected category checkboxes as filters and restores filter/form visibility.
     *
     * Filters the product list so it contains only products whose category names include all selected
     * category names, and updates the products table with the filtered results.
     */
    @FXML
    /**
     * Performs filtercinema operation.
     *
     * @return the result of the operation
     */
    public void filtercinema(final ActionEvent event) {
        this.Product_tableview.setOpacity(1);
        this.filterAnchor.setVisible(false);
        this.Product_tableview.setVisible(true);
        this.formulaire.setVisible(true);
        // Récupérer les adresses sélectionnées
        final List<String> selectedCategories = this.getSelectedCategories();
        // Récupérer les statuts sélectionnés
        final Product categorieProduct = new Product();
        // Filtrer les cinémas en fonction des adresses et/ou des statuts sélectionnés
        final List<Product> filteredCategories = this.getAllCategories().stream()
                .filter(c -> selectedCategories.containsAll(
                        c.getCategories().stream().map(ProductCategory::getCategoryName).collect(Collectors.toList())))
                .collect(Collectors.toList());
        // Mettre à jour le TableView avec les cinémas filtrés
        final ObservableList<Product> filteredList = FXCollections.observableArrayList(filteredCategories);
        this.Product_tableview.setItems(filteredList);
    }


    /**
     * Collects the text values of all selected address checkboxes.
     *
     * @return a list containing the text of each selected checkbox from `addressCheckBoxes`.
     */
    private List<String> getSelectedCategories() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return this.addressCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                .collect(Collectors.toList());
    }


    /**
         * Opens the CommentaireProduct UI in a new window and closes the current window.
         *
         * @param event the ActionEvent that triggered the navigation to the CommentaireProduct view
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
            DesignProductAdminContoller.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                     // d'entrée/sortie
        }

    }


    /**
     * Open the Event administration UI in a new window and close the current window.
     *
     * Loads the DesignEvenementAdmin.fxml UI, displays it in a new Stage, and closes the Stage
     * that originated the triggering ActionEvent.
     *
     * @param event the ActionEvent that triggered navigation; used to locate and close the originating window
     */
    @FXML
    void eventClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui//ui/DesignEvenementAdmin.fxml"));
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
            DesignProductAdminContoller.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                     // d'entrée/sortie
        }

    }


    /**
     * Opens the product administration UI in a new window and closes the current window.
     *
     * Loads "/ui/produits/DesignProductAdmin.fxml", attaches it to a new Stage titled "products", shows that Stage, and closes the originating Stage.
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
            DesignProductAdminContoller.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                     // d'entrée/sortie
        }

    }


    /**
     * Handle client-profile actions; currently a no-op placeholder for opening or managing the client profile view.
     *
     * @param event the ActionEvent that triggered this handler
     */
    @FXML
    void profilclient(final ActionEvent event) {
    }


    /**
         * Open the film user interface in a new window and close the current window.
         *
         * Loads "/ui/films/filmuser.fxml", displays it in a new Stage, and closes the originating Stage.
         *
         * @param event the ActionEvent that triggered navigation
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
            DesignProductAdminContoller.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                     // d'entrée/sortie
        }

    }


    /**
     * Opens the Series administration view in a new window and closes the current window.
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
            DesignProductAdminContoller.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
                                                                                     // d'entrée/sortie
        }

    }


    /**
     * Opens a file chooser to upload a selected PNG or JPG to Cloudinary and display it in the controller's ImageView.
     *
     * <p>If the user cancels the chooser, no state is changed. On successful upload the resulting URL is stored
     * in {@code cloudinaryImageUrl} and the controller's {@code image} ImageView is updated. IOExceptions raised
     * during upload are logged and handled internally.
     *
     * @param event the MouseEvent that triggered the action
     */
    @FXML
    void importImage(final MouseEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        fileChooser.setTitle("Sélectionner une image");
        this.selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            try {
                // Use the CloudinaryStorage service to upload the image
                CloudinaryStorage cloudinaryStorage = CloudinaryStorage.getInstance();
                cloudinaryImageUrl = cloudinaryStorage.uploadImage(selectedFile);

                // Display the image in the ImageView
                final Image selectedImage = new Image(cloudinaryImageUrl);
                this.image.setImage(selectedImage);

                LOGGER.info("Image uploaded to Cloudinary: " + cloudinaryImageUrl);
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
                DesignProductAdminContoller.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

        }

    }

}