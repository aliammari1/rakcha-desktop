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
     * Sets up event listeners for the `SearchBar` text property and triggers
     * actions when the text changes. It also reads category names from a service
     * and adds them to a combobox, and displays the first product in the list.
     */
    @FXML
    void initialize() {
        this.SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            this.search(newValue);
            this.filterCategorieProducts(newValue.trim());
        });
        final CategoryService cs = new CategoryService();
        for (final ProductCategory c : cs.read()) {
            this.nomC_comboBox.getItems().add(c.getCategoryName());
        }
        this.afficher_produit();
        this.initDeleteColumn();
    }

    /**
     * Creates an `Alert` object and displays it with a title, header text, and
     * content text provided as arguments.
     *
     * @param message
     *                text to be displayed as an information alert when the
     *                `showAlert()` method is called.
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
     * Allows the user to select an image file using a FileChooser dialog. If an
     * image is selected, it sets the `image` field to the selected image.
     *
     * @param event
     *              MouseEvent object that triggered the function, providing
     *              information about the mouse event that initiated the image
     *              selection process.
     *              <p>
     *              Event: MouseEvent Type: Input event related to mouse actions
     *              (clicks, moves, releases, etc.) Parameters: none
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
     * Allows users to add a new product to a list. It validates user input,
     * converts prices and quantities to integers, checks for invalid input, and
     * adds the product to the existing list.
     *
     * @param actionEvent
     *                    event that triggered the execution of the `add_produit()`
     *                    method,
     *                    which in this case is a button click.
     *                    <p>
     *                    - `actionEvent` represents an action event triggered by
     *                    the user,
     *                    such as clicking on a button or selecting an image. - The
     *                    event
     *                    may contain additional information, such as the source of
     *                    the
     *                    action (e.g., a button or a link) and the ID of the
     *                    element that
     *                    was activated.
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
        } else {
            this.showAlert("Veuillez sélectionner une image d'abord !");
        }
    }

    /**
     * Modifies the values of a `Product` object, such as its category, name, price,
     * description, image, quantity, and ID, before saving the modified data to the
     * database using a `ProductService`.
     *
     * @param produit
     *                Product object to be modified, containing the product's ID,
     *                category name, nom, prix, description, image, and quantity.
     *                <p>
     *                - `nouvelleCategorie`: The new category name for the product.
     *                -
     *                `nouveauNom`: The new product name. - `nouveauPrix`: The new
     *                price
     *                of the product. - `nouvelleDescription`: The new product
     *                description. - `img`: The new image associated with the
     *                product. -
     *                `nouvelleQuantite`: The new quantity of the product in stock.
     *                -
     *                `id`: The unique identifier for the product in the database.
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
     * 1) sets the style class for the cell containing the Product object, 2)
     * updates the graphic and text of the cell based on the Product object, and 3)
     * makes the cell editable.
     */
    void afficher_produit() {
        // Créer un nouveau ComboBox
        final ImageView imageView = new ImageView();
        this.nomCP_tableC.setCellValueFactory(new PropertyValueFactory<Product, String>("nom_categorie"));
        this.nomCP_tableC.setCellFactory(column -> new TableCell<Product, String>() {
            /**
             * Updates an item's graphic and adds an EventHandler to a ComboBox within the
             * cell, which triggers when the ComboBox selection changes. It sets the
             * selected category name as the item's category and updates the item's category
             * in the produits table.
             *
             * @param item
             *              current selected item or value in the `updateItem` function,
             *              which
             *              is being updated and processed accordingly.
             *
             * @param empty
             *              empty state of the item being updated, which determines whether
             *              to
             *              show or hide the combo box and its content.
             */
            @Override
            /*
             * protected void updateItem(String item, boolean empty) {
             * super.updateItem(item, empty);
             *
             * if (item == null || empty) { setGraphic(null); } else { CategoryService cs =
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
             * }); } } });
             */
            protected void updateItem(final String item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    this.setText(null);
                } else {
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
                        });
                        // Appliquer la classe CSS pour masquer le texte
                        this.getStyleClass().add("cell-hide-text");
                        // Afficher le ComboBox dans la cellule
                        this.setGraphic(produitComboBox);
                    }
                });
            }
        });
        this.nomP_tableC.setCellValueFactory(new PropertyValueFactory<Product, String>("nom"));
        this.nomP_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        this.nomP_tableC.setOnEditCommit(event -> {
            final Product produit = event.getRowValue();
            produit.setName(event.getNewValue());
            this.modifier_produit(produit);
        });
        this.PrixP_tableC.setCellValueFactory(new PropertyValueFactory<Product, Integer>("prix"));
        this.PrixP_tableC.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        this.PrixP_tableC.setOnEditCommit(event -> {
            final Product produit = event.getRowValue();
            produit.setPrice(event.getNewValue());
            this.modifier_produit(produit);
        });
        this.descriptionP_tableC.setCellValueFactory(new PropertyValueFactory<Product, String>("description"));
        this.descriptionP_tableC.setCellFactory(TextFieldTableCell.forTableColumn());
        this.descriptionP_tableC.setOnEditCommit(event -> {
            final Product produit = event.getRowValue();
            produit.setDescription(event.getNewValue());
            this.modifier_produit(produit);
        });
        this.quantiteP_tableC.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantiteP"));
        this.quantiteP_tableC.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        this.quantiteP_tableC.setOnEditCommit(event -> {
            final Product produit = event.getRowValue();
            produit.setQuantity(event.getNewValue());
            this.modifier_produit(produit);
        });
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
                } else {
                    // Afficher une image par défaut si le logo est null
                    final Image defaultImage = new Image(
                            Objects.requireNonNull(this.getClass().getResourceAsStream("default_image.png")));
                    imageView.setImage(defaultImage);
                }
            } catch (final Exception e) {
                DesignProductAdminContoller.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            return new SimpleObjectProperty<>(imageView);
        });
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
                });
            }

            /**
             * Updates an item's graphic and text based on its empty or non-empty status,
             * and sets the corresponding properties of the function's image view.
             *
             * @param item
             *              ImageView object that is being updated, and its `Image` property
             *              is set to the `Image` object of the corresponding table row
             *              item.
             *
             *              - `empty`: A boolean indicating whether `item` is empty or not.
             *              -
             *              `item`: The ImageView object to be updated, which contains an
             *              image and other display properties. - `produit`: The product
             *              associated with the item, which is obtained from the parent
             *              TableRow object.
             *
             * @param empty
             *              whether the imageView is empty or not and affects whether the
             *              graphic and text are set to null or not.
             */
            @Override
            protected void updateItem(final ImageView item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    this.setGraphic(null);
                    this.setText(null);
                } else {
                    this.imageView.setImage(item.getImage());
                    this.imageView.setFitWidth(100);
                    this.imageView.setFitHeight(50);
                    this.setGraphic(this.imageView);
                    this.setText(null);
                }
                this.produit = this.getTableRow().getItem();
            }
        });
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
        });
        // Utiliser une ObservableList pour stocker les éléments
        final ObservableList<Product> list = FXCollections.observableArrayList();
        final ProductService ps = new ProductService();
        list.addAll(ps.read());
        this.Product_tableview.setItems(list);
        // Activer la sélection de cellules
        this.Product_tableview.getSelectionModel().setCellSelectionEnabled(true);
    }

    /**
     * Sets up a delete button for each item in a table, with the button's on-action
     * triggering the deletion of the corresponding item from the data source and
     * updating the table view accordingly.
     */
    private void initDeleteColumn() {
        final Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<>() {
            /**
             * Generates a TableCell that displays a delete button for each item in a
             * TableView. When the button is pressed, the corresponding item is deleted from
             * the TableView and the underlying data source.
             *
             * @param param
             *              TableColumn object from which the method is being called, and is
             *              used to supply the necessary context for the method to work
             *              properly.
             *
             *              - `param`: an instance of `TableColumn<Product, Void>`
             *              representing the table column that triggered the cell's
             *              creation.
             *              - `getIndex()`: returns the index of the row where the cell is
             *              located in the `produit` list. - `getItems()`: returns a list of
             *              `Product` objects representing the data displayed in the column.
             *
             * @returns a `TableCell` object that displays a delete button for each item in
             *          the table.
             *
             *          - The output is a `TableCell` object with a button graphic that
             *          displays "Delete". - The button has a style class of
             *          "delete-button". - When the button is clicked, the `onAction` method
             *          is triggered, which calls the `delete` method of the
             *          `ProductService` class to delete the corresponding item from the
             *          `Product_tableview`. - After deletion, the table view is refreshed
             *          to update the display.
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
                        });
                    }

                    /**
                     * Updates an item's graphic based on whether it is empty or not. When the item
                     * is empty, the function sets the graphic to null; otherwise, it sets the
                     * graphic to a button with the text "Delete".
                     *
                     * @param item
                     *              Void object being updated, which is passed to the superclass's
                     *              `updateItem()` method and then processed further in the current
                     *              method based on the value of the `empty` parameter.
                     *
                     *              - `item`: The object being updated, which may be `null`. -
                     *              `empty`: A boolean indicating whether the item is empty or not.
                     *
                     * @param empty
                     *              whether the item is empty or not and affects the graphic
                     *              displayed
                     *              in the function, with `true` indicating no item and `false`
                     *              indicating an item present.
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
        // Product_tableview.getColumns().add(deleteColumn);
    }

    // Méthode pour changer l'image

    /**
     * Allows the user to select an image, then sets the selected image as the
     * product's image using a database connection.
     *
     * @param produit
     *                object being updated with the selected image.
     *                <p>
     *                - `produit`: A `Product` object representing the product whose
     *                image is being changed. - `image`: A string containing the URL
     *                of
     *                the existing image associated with the product.
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
     * Loads a new user interface (`DesignCategorieAdmin.fxml`) into a scene,
     * creates a new stage with the new interface, and replaces the current stage
     * with it, while closing the original stage.
     *
     * @param event
     *              event that triggered the function, specifically the button click
     *              event that initiated the category management process.
     *              <p>
     *              - `event` is an `ActionEvent` object representing the triggering
     *              event for the function.
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
     * Searches for products based on a given keyword, filtering an observable list
     * of products from the `ProductService`. It adds the filtered products to the
     * `ProductTableView`.
     *
     * @param keyword
     *                search query provided by the user and is used to filter the
     *                list
     *                of products in the `produitservice.read()` method.
     */
    @FXML
    private void search(final String keyword) {
        final ProductService produitservice = new ProductService();
        final ObservableList<Product> filteredList = FXCollections.observableArrayList();
        if (null == keyword || keyword.trim().isEmpty()) {
            filteredList.addAll(produitservice.read());
        } else {
            for (final Product produit : produitservice.read()) {
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
     * Filters a list of products based on a search query, by adding only the
     * products that have the searched category name in their name.
     *
     * @param searchText
     *                   search query entered by the user, which is used to filter
     *                   the list
     *                   of products in the `Product_tableview`.
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
        } else {
            // Si le champ de recherche est vide, afficher tous les cinémas
            this.afficher_produit();
        }
    }

    /**
     * Retrieves a list of product categories from the service layer using the
     * `ProductService`. The returned list contains all product categories.
     *
     * @returns a list of `Product` objects retrieved from the database through the
     *          `ProductService`.
     */
    private List<Product> getAllCategories() {
        final ProductService categoryservice = new ProductService();
        return categoryservice.read();
    }

    /**
     * Updates the opacity and visibility of an ancestor element, sets a `CheckBox`
     * list as visible, clears existing check boxes, retrieves unique addresses from
     * a database, creates a `VBox` for each address, adds the `VBox` to a parent
     * element, and makes the parent element visible.
     *
     * @param event
     *              MouseEvent that triggered the function execution and provides
     *              information about the event, such as the button that was clicked
     *              or the location of the click within the screen.
     *              <p>
     *              - `event` is an instance of `MouseEvent`, which represents a
     *              mouse
     *              event such as a click or a drag. - The event may have various
     *              properties such as the `button` (which button was clicked),
     *              `clickCount` (the number of times the button was clicked), and
     *              `location` (the coordinates of the event).
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
     * Retrieves a list of category names from a database and returns it as a list
     * of strings.
     *
     * @returns a list of distinct category names retrieved from the database.
     *          <p>
     *          - The output is a list of strings, representing the categories of
     *          products obtained from the database. - The list contains distinct
     *          category names as extracted from the `Categorie` objects in the
     *          `categories` list. - The categories are determined by the
     *          `nom_categorie` field of each `Product` object.
     */
    public List<String> getProductCategory() {
        // Récupérer tous les cinémas depuis la base de données
        final List<Product> categories = this.getAllCategories();
        return categories.stream().flatMap(c -> c.getCategories().stream()).map(c -> c.getCategoryName()).distinct()
                .collect(Collectors.toList());
    }

    /**
     * Filters a list of cinemas based on selected categories and statuses, updates
     * the TableView with the filtered results.
     *
     * @param event
     *              action event that triggers the execution of the `filtercinema()`
     *              method.
     *              <p>
     *              - Type: ActionEvent, indicating that the event was triggered by
     *              an
     *              action (e.g., button click) - Source: the object that generated
     *              the event (e.g., a button)
     *              <p>
     *              In summary, `event` is an instance of the ActionEvent class,
     *              providing information about the source and type of the event.
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
     * Streams, filters, and collects the selected addresses from the
     * `addressCheckBoxes` array, returning a list of strings representing the
     * selected categories.
     *
     * @returns a list of selected addresses.
     *          <p>
     *          - The list contains only selected addresses from the
     *          `addressCheckBoxes` stream. - Each element in the list is a string
     *          representing the text of the corresponding selected address.
     */
    private List<String> getSelectedCategories() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return this.addressCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    /**
     * Charges a new FXML file (`CommentaireProduct.fxml`), creates a new scene with
     * it, and attaches the scene to a new stage. It then closes the current stage
     * and shows the new one.
     *
     * @param event
     *              ActionEvent object that triggered the function execution,
     *              providing information about the source of the event and allowing
     *              the function to handle the appropriate action.
     *              <p>
     *              - `event`: an ActionEvent object representing a user action that
     *              triggered the function.
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
     * Loads a new FXML file "/ui//ui/DesignEvenementAdmin.fxml" and creates a new
     * scene with it, replacing the current stage with the new scene.
     *
     * @param event
     *              ActionEvent object that triggered the event handling, providing
     *              the source of the event and allowing the code to access the
     *              relevant information.
     *              <p>
     *              - `event` is an `ActionEvent` representing a user action that
     *              triggered the function execution. - The source of the event is
     *              the
     *              element in the UI that was interacted with by the user, which is
     *              passed as the parameter to the function.
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
     * Loads a new FXML interface, creates a new scene, and attaches it to a new
     * stage, replacing the current stage.
     *
     * @param event
     *              ActionEvent object triggered by the user's action, which
     *              initiates
     *              the code execution and loads the new interface in the scene.
     *              <p>
     *              - It is an instance of `ActionEvent`, which represents an event
     *              triggered by a user action on a JavaFX component. - The source
     *              of
     *              the event is typically a button or other control that initiated
     *              the action.
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
     * Appears to be a handler for an action event, likely related to the execution
     * of client-side code. It does not contain any explicit logic or functionality
     * beyond calling the default behavior of the `void` return type.
     *
     * @param event
     *              occurrence of an action event that triggered the execution of
     *              the
     *              `profilclient` method.
     */
    @FXML
    void profilclient(final ActionEvent event) {
    }

    /**
     * Loads a new user interface, creates a new stage with it, and replaces the
     * current stage with the new one.
     *
     * @param event
     *              ActionEvent object that triggered the `MovieClient()` method,
     *              providing information about the action that was performed, such
     *              as
     *              the source of the event and the type of event.
     *              <p>
     *              - Event type: The event type is `ActionEvent`, indicating that
     *              the
     *              event was triggered by an action (such as clicking a button or
     *              pressing a key).
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
     * Loads a new FXML view, creates a new scene and stage, and replaces the
     * current stage with the new one.
     *
     * @param event
     *              ActionEvent that triggered the function, providing information
     *              about the source of the event and any relevant data.
     *              <p>
     *              - Event source: The object that generated the event. - Type of
     *              event: The type of event (e.g., button click, window closing).
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
     * Allows the user to select an image file from a list of accepted formats,
     * saves it to a specified directory, and sets the selected image as the `image`
     * field's new image.
     *
     * @param event
     *              MouseEvent object that triggered the function's execution and
     *              provides information about the user's action, such as the button
     *              that was clicked or the position of the mouse pointer, which is
     *              not used in this particular function.
     *              <p>
     *              - `MouseEvent event`: represents an event generated by a mouse
     *              button press or release, or a mouse move.
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
