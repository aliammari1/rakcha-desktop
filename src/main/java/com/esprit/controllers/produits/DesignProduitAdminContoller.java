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
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
/**
 * Is responsible for handling user interactions related to the "Produits" section
 * of the Rakcha web application. It contains several methods that handle different
 * types of user actions, such as importing images, creating a new product, and viewing
 * a product details page. The controller also uses FXML to load user interface
 * elements from a file named "DesignProduitAdmin.fxml".
 */
public class DesignProduitAdminContoller {
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> statusCheckBoxes = new ArrayList<>();
    private final List<Produit> l1 = new ArrayList<>();
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
    /**
     * Sets up event listeners for the `SearchBar` text property and triggers actions
     * when the text changes. It also reads category names from a service and adds them
     * to a combobox, and displays the first product in the list.
     */
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
    /**
     * Creates an `Alert` object and displays it with a title, header text, and content
     * text provided as arguments.
     * 
     * @param message text to be displayed as an information alert when the `showAlert()`
     * method is called.
     */
    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    /**
     * Allows the user to select an image file using a FileChooser dialog. If an image
     * is selected, it sets the `image` field to the selected image.
     * 
     * @param event MouseEvent object that triggered the function, providing information
     * about the mouse event that initiated the image selection process.
     * 
     * Event: MouseEvent
     * Type: Input event related to mouse actions (clicks, moves, releases, etc.)
     * Parameters: none
     */
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
    /**
     * Allows users to add a new product to a list. It validates user input, converts
     * prices and quantities to integers, checks for invalid input, and adds the product
     * to the existing list.
     * 
     * @param actionEvent event that triggered the execution of the `add_produit()` method,
     * which in this case is a button click.
     * 
     * 	- `actionEvent` represents an action event triggered by the user, such as clicking
     * on a button or selecting an image.
     * 	- The event may contain additional information, such as the source of the action
     * (e.g., a button or a link) and the ID of the element that was activated.
     */
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
            if (nomProduit.isEmpty() || prixText.isEmpty() || descriptionProduit.isEmpty() || nomCategorie == null
                    || quantiteText.isEmpty()) {
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
                    alert.setContentText(
                            "Veuillez entrer des valeurs valides pour le prix et la quantité (supérieures à zéro)");
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
                try {
                    URI uri = null;
                    String fullPath = image.getImage().getUrl();
                    System.out.println(fullPath);
                    String requiredPath = fullPath.substring(fullPath.indexOf("/img/produit/"));
                    uri = new URI(requiredPath);
                    ProduitService ps = new ProduitService();
                    CategorieService cs = new CategorieService();
                    Produit nouveauProduit = new Produit(nomProduit, prix, uri.getPath(), descriptionProduit,
                            cs.getCategorieByNom(nomCategorie), quantite);
                    System.out.println(nomProduit + " " + prix + " " + uri.getPath() + " " + descriptionProduit + " " +
                            cs.getCategorieByNom(nomCategorie) + " " + quantite);
                    System.out.println("nouveauProduit: --------------- " + nouveauProduit);
                    ps.create(nouveauProduit);
                    // Ajouter le nouveau produit à la liste existante
                    Produit_tableview.getItems().add(nouveauProduit);
                    // Rafraîchir la TableView
                    Produit_tableview.refresh();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Produit ajouté");
                    alert.setContentText("Produit ajouté !");
                    alert.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Erreur lors de l'ajout du produit : " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                showAlert("Veuillez entrer des valeurs numériques valides pour le prix et la quantité.");
            }
        } else {
            showAlert("Veuillez sélectionner une image d'abord !");
        }
    }
    /**
     * Modifies the values of a `Produit` object, such as its category, name, price,
     * description, image, quantity, and ID, before saving the modified data to the
     * database using a `ProduitService`.
     * 
     * @param produit Produit object to be modified, containing the product's ID, category
     * name, nom, prix, description, image, and quantity.
     * 
     * 	- `nouvelleCategorie`: The new category name for the product.
     * 	- `nouveauNom`: The new product name.
     * 	- `nouveauPrix`: The new price of the product.
     * 	- `nouvelleDescription`: The new product description.
     * 	- `img`: The new image associated with the product.
     * 	- `nouvelleQuantite`: The new quantity of the product in stock.
     * 	- `id`: The unique identifier for the product in the database.
     */
    @FXML
    void modifier_produit(Produit produit) {
        // Récupérez les valeurs modifiées depuis la ligne
        String nouvelleCategorie = produit.getNom_categorie();
        String nouveauNom = produit.getNom();
        int nouveauPrix = produit.getPrix();
        String nouvelleDescription = produit.getDescription();
        String img = produit.getImage();
        int nouvelleQuantite = produit.getQuantiteP();
        int id = produit.getId_produit();
        // Enregistrez les modifications dans la base de données en utilisant un service
        // approprié
        ProduitService ps = new ProduitService();
        ps.update(produit);
    }
    /**
     * 1) sets the style class for the cell containing the Produit object, 2) updates the
     * graphic and text of the cell based on the Produit object, and 3) makes the cell editable.
     */
    void afficher_produit() {
        // Créer un nouveau ComboBox
        ImageView imageView = new ImageView();
        nomCP_tableC.setCellValueFactory(new PropertyValueFactory<Produit, String>("nom_categorie"));
        nomCP_tableC.setCellFactory(column -> new TableCell<Produit, String>() {
            /**
             * Updates an item's graphic and adds an EventHandler to a ComboBox within the cell,
             * which triggers when the ComboBox selection changes. It sets the selected category
             * name as the item's category and updates the item's category in the produits table.
             * 
             * @param item current selected item or value in the `updateItem` function, which is
             * being updated and processed accordingly.
             * 
             * @param empty empty state of the item being updated, which determines whether to
             * show or hide the combo box and its content.
             */
            @Override
            /*
             * protected void updateItem(String item, boolean empty) {
             * super.updateItem(item, empty);
             *
             * if (item == null || empty) {
             * setGraphic(null);
             * } else {
             * CategorieService cs = new CategorieService();
             * ComboBox<String> newComboBox = new ComboBox<>();
             *
             * // Obtenez la liste des noms de catégories
             * List<String> categorieNames = cs.getAllCategoriesNames();
             *
             * // Ajoutez les noms de catégories au ComboBox
             * newComboBox.getItems().addAll(categorieNames);
             *
             * // Sélectionnez le nom de la catégorie associée au produit
             * newComboBox.setValue(item);
             *
             * // Afficher le ComboBox nouvellement créé dans la cellule
             * setGraphic(newComboBox);
             * newComboBox.setOnAction(event -> {
             * Produit produit = getTableView().getItems().get(getIndex());
             * // Mise à jour de la catégorie associée au produit
             * Categorie selectedCategorie = cs.getCategorieByNom(newComboBox.getValue());
             * produit.setCategorie(selectedCategorie);
             * modifier_produit(produit);
             * newComboBox.getStyleClass().add("combo-box-red");
             *
             * });
             * }
             * }
             * });
             */
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
                            Categorie_Produit selectedCategorieProduit = cs
                                    .getCategorieByNom(produitComboBox.getValue());
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
                String pImage = p.getImage();
                if (!pImage.isEmpty()) {
                    Image image = new Image(pImage);
                    imageView.setImage(image);
                } else {
                    // Afficher une image par défaut si le logo est null
                    Image defaultImage = new Image(
                            Objects.requireNonNull(getClass().getResourceAsStream("default_image.png")));
                    imageView.setImage(defaultImage);
                }
            } catch (Exception e) {
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
            /**
             * Updates an item's graphic and text based on its empty or non-empty status, and
             * sets the corresponding properties of the function's image view.
             * 
             * @param item ImageView object that is being updated, and its `Image` property is
             * set to the `Image` object of the corresponding table row item.
             * 
             * 	- `empty`: A boolean indicating whether `item` is empty or not.
             * 	- `item`: The ImageView object to be updated, which contains an image and other
             * display properties.
             * 	- `produit`: The product associated with the item, which is obtained from the
             * parent TableRow object.
             * 
             * @param empty whether the imageView is empty or not and affects whether the graphic
             * and text are set to null or not.
             */
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
        // Gérer la modification du texte dans une cellule et le valider en appuyant sur
        // Enter
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
    /**
     * Sets up a delete button for each item in a table, with the button's on-action
     * triggering the deletion of the corresponding item from the data source and updating
     * the table view accordingly.
     */
    private void initDeleteColumn() {
        Callback<TableColumn<Produit, Void>, TableCell<Produit, Void>> cellFactory = new Callback<>() {
            /**
             * Generates a TableCell that displays a delete button for each item in a TableView.
             * When the button is pressed, the corresponding item is deleted from the TableView
             * and the underlying data source.
             * 
             * @param param TableColumn object from which the method is being called, and is used
             * to supply the necessary context for the method to work properly.
             * 
             * 	- `param`: an instance of `TableColumn<Produit, Void>` representing the table
             * column that triggered the cell's creation.
             * 	- `getIndex()`: returns the index of the row where the cell is located in the
             * `produit` list.
             * 	- `getItems()`: returns a list of `Produit` objects representing the data displayed
             * in the column.
             * 
             * @returns a `TableCell` object that displays a delete button for each item in the
             * table.
             * 
             * 	- The output is a `TableCell` object with a button graphic that displays "Delete".
             * 	- The button has a style class of "delete-button".
             * 	- When the button is clicked, the `onAction` method is triggered, which calls the
             * `delete` method of the `ProduitService` class to delete the corresponding item
             * from the `Produit_tableview`.
             * 	- After deletion, the table view is refreshed to update the display.
             */
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
                    /**
                     * Updates an item's graphic based on whether it is empty or not. When the item is
                     * empty, the function sets the graphic to null; otherwise, it sets the graphic to a
                     * button with the text "Delete".
                     * 
                     * @param item Void object being updated, which is passed to the superclass's
                     * `updateItem()` method and then processed further in the current method based on
                     * the value of the `empty` parameter.
                     * 
                     * 	- `item`: The object being updated, which may be `null`.
                     * 	- `empty`: A boolean indicating whether the item is empty or not.
                     * 
                     * @param empty whether the item is empty or not and affects the graphic displayed
                     * in the function, with `true` indicating no item and `false` indicating an item present.
                     */
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
        // Produit_tableview.getColumns().add(deleteColumn);
    }
    // Méthode pour changer l'image
    /**
     * Allows the user to select an image, then sets the selected image as the product's
     * image using a database connection.
     * 
     * @param produit object being updated with the selected image.
     * 
     * 	- `produit`: A `Produit` object representing the product whose image is being changed.
     * 	- `image`: A string containing the URL of the existing image associated with the
     * product.
     */
    private void changerImage(Produit produit) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) { // Vérifier si une image a été sélectionnée
            Connection connection = null;
            try {
                connection = DataSource.getInstance().getConnection();
                produit.setImage(selectedFile.toURI().toURL().toString());
                modifier_produit(produit);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur lors du chargement de la nouvelle image : " + e.getMessage());
            }
        }
    }
    /**
     * Loads a new user interface (`DesignCategorieAdmin.fxml`) into a scene, creates a
     * new stage with the new interface, and replaces the current stage with it, while
     * closing the original stage.
     * 
     * @param event event that triggered the function, specifically the button click event
     * that initiated the category management process.
     * 
     * 	- `event` is an `ActionEvent` object representing the triggering event for the function.
     */
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
    /**
     * Searches for products based on a given keyword, filtering an observable list of
     * products from the `ProduitService`. It adds the filtered products to the `ProduitTableView`.
     * 
     * @param keyword search query provided by the user and is used to filter the list
     * of products in the `produitservice.read()` method.
     */
    @FXML
    private void search(String keyword) {
        ProduitService produitservice = new ProduitService();
        ObservableList<Produit> filteredList = FXCollections.observableArrayList();
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredList.addAll(produitservice.read());
        } else {
            for (Produit produit : produitservice.read()) {
                if (produit.getNom().toLowerCase().contains(keyword.toLowerCase()) ||
                        produit.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                        produit.getNom_categorie().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(produit);
                }
            }
        }
        Produit_tableview.setItems(filteredList);
    }
    /**
     * Filters a list of products based on a search query, by adding only the products
     * that have the searched category name in their name.
     * 
     * @param searchText search query entered by the user, which is used to filter the
     * list of products in the `Produit_tableview`.
     */
    private void filterCategorieProduits(String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des cinémas pour ne garder que ceux dont le nom contient le
            // texte saisi
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
    /**
     * Retrieves a list of product categories from the service layer using the `ProduitService`.
     * The returned list contains all product categories.
     * 
     * @returns a list of `Produit` objects retrieved from the database through the `ProduitService`.
     */
    private List<Produit> getAllCategories() {
        ProduitService categorieservice = new ProduitService();
        List<Produit> categorie = categorieservice.read();
        return categorie;
    }
    /**
     * Updates the opacity and visibility of an ancestor element, sets a `CheckBox` list
     * as visible, clears existing check boxes, retrieves unique addresses from a database,
     * creates a `VBox` for each address, adds the `VBox` to a parent element, and makes
     * the parent element visible.
     * 
     * @param event MouseEvent that triggered the function execution and provides information
     * about the event, such as the button that was clicked or the location of the click
     * within the screen.
     * 
     * 	- `event` is an instance of `MouseEvent`, which represents a mouse event such as
     * a click or a drag.
     * 	- The event may have various properties such as the `button` (which button was
     * clicked), `clickCount` (the number of times the button was clicked), and `location`
     * (the coordinates of the event).
     */
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
    /**
     * Retrieves a list of category names from a database and returns it as a list of strings.
     * 
     * @returns a list of distinct category names retrieved from the database.
     * 
     * 	- The output is a list of strings, representing the categories of products obtained
     * from the database.
     * 	- The list contains distinct category names as extracted from the `Categorie`
     * objects in the `categories` list.
     * 	- The categories are determined by the `nom_categorie` field of each `Produit` object.
     */
    public List<String> getCategorie_Produit() {
        // Récupérer tous les cinémas depuis la base de données
        List<Produit> categories = getAllCategories();
        List<String> categorie = categories.stream()
                .map(c -> c.getCategorie().getNom_categorie())
                .distinct()
                .collect(Collectors.toList());
        return categorie;
    }
    /**
     * Filters a list of cinemas based on selected categories and statuses, updates the
     * TableView with the filtered results.
     * 
     * @param event action event that triggers the execution of the `filtercinema()` method.
     * 
     * 	- Type: ActionEvent, indicating that the event was triggered by an action (e.g.,
     * button click)
     * 	- Source: the object that generated the event (e.g., a button)
     * 
     * In summary, `event` is an instance of the ActionEvent class, providing information
     * about the source and type of the event.
     */
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
    /**
     * Streams, filters, and collects the selected addresses from the `addressCheckBoxes`
     * array, returning a list of strings representing the selected categories.
     * 
     * @returns a list of selected addresses.
     * 
     * 	- The list contains only selected addresses from the `addressCheckBoxes` stream.
     * 	- Each element in the list is a string representing the text of the corresponding
     * selected address.
     */
    private List<String> getSelectedCategories() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return addressCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }
    /**
     * Charges a new FXML file (`CommentaireProduit.fxml`), creates a new scene with it,
     * and attaches the scene to a new stage. It then closes the current stage and shows
     * the new one.
     * 
     * @param event ActionEvent object that triggered the function execution, providing
     * information about the source of the event and allowing the function to handle the
     * appropriate action.
     * 
     * 	- `event`: an ActionEvent object representing a user action that triggered the function.
     */
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
    /**
     * Loads a new FXML file "DesignEvenementAdmin.fxml" and creates a new scene with it,
     * replacing the current stage with the new scene.
     * 
     * @param event ActionEvent object that triggered the event handling, providing the
     * source of the event and allowing the code to access the relevant information.
     * 
     * 	- `event` is an `ActionEvent` representing a user action that triggered the
     * function execution.
     * 	- The source of the event is the element in the UI that was interacted with by
     * the user, which is passed as the parameter to the function.
     */
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
    /**
     * Loads a new FXML interface, creates a new scene, and attaches it to a new stage,
     * replacing the current stage.
     * 
     * @param event ActionEvent object triggered by the user's action, which initiates
     * the code execution and loads the new interface in the scene.
     * 
     * 	- It is an instance of `ActionEvent`, which represents an event triggered by a
     * user action on a JavaFX component.
     * 	- The source of the event is typically a button or other control that initiated
     * the action.
     */
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
    /**
     * Appears to be a handler for an action event, likely related to the execution of
     * client-side code. It does not contain any explicit logic or functionality beyond
     * calling the default behavior of the `void` return type.
     * 
     * @param event occurrence of an action event that triggered the execution of the
     * `profilclient` method.
     */
    @FXML
    void profilclient(ActionEvent event) {
    }
    /**
     * Loads a new user interface, creates a new stage with it, and replaces the current
     * stage with the new one.
     * 
     * @param event ActionEvent object that triggered the `MovieClient()` method, providing
     * information about the action that was performed, such as the source of the event
     * and the type of event.
     * 
     * 	- Event type: The event type is `ActionEvent`, indicating that the event was
     * triggered by an action (such as clicking a button or pressing a key).
     */
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
    /**
     * Loads a new FXML view, creates a new scene and stage, and replaces the current
     * stage with the new one.
     * 
     * @param event ActionEvent that triggered the function, providing information about
     * the source of the event and any relevant data.
     * 
     * 	- Event source: The object that generated the event.
     * 	- Type of event: The type of event (e.g., button click, window closing).
     */
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
    /**
     * Allows the user to select an image file from a list of accepted formats, saves it
     * to a specified directory, and sets the selected image as the `image` field's new
     * image.
     * 
     * @param event MouseEvent object that triggered the function's execution and provides
     * information about the user's action, such as the button that was clicked or the
     * position of the mouse pointer, which is not used in this particular function.
     * 
     * 	- `MouseEvent event`: represents an event generated by a mouse button press or
     * release, or a mouse move.
     */
    @FXML
    void importImage(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );
        fileChooser.setTitle("Sélectionner une image");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destinationDirectory1 = "./src/main/resources/img/produit/";
                String destinationDirectory2 = "C:\\xampp\\htdocs\\Rakcha\\rakcha-web\\public\\img\\produit\\";
                Path destinationPath1 = Paths.get(destinationDirectory1);
                Path destinationPath2 = Paths.get(destinationDirectory2);
                String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destinationFilePath1 = destinationPath1.resolve(uniqueFileName);
                Path destinationFilePath2 = destinationPath2.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationFilePath1);
                Files.copy(selectedFile.toPath(), destinationFilePath2);
                Image selectedImage = new Image(destinationFilePath1.toUri().toString());
                image.setImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
