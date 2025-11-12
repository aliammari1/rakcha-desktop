package com.esprit.controllers.films;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.films.Category;
import com.esprit.services.films.CategoryService;
import com.esprit.utils.PageRequest;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import net.synedra.validatorfx.Validator;

/**
 * Controller class for managing film categories in the RAKCHA application.
 * 
 * <p>
 * This controller handles user interactions with the category management
 * interface,
 * including listing, creating, updating, and deleting film categories. It
 * provides
 * a robust validation system for user input with real-time feedback through
 * tooltips.
 * </p>
 * 
 * <p>
 * Key features include:
 * </p>
 * <ul>
 * <li>Category CRUD operations (Create, Read, Update, Delete)</li>
 * <li>Real-time validation with immediate feedback</li>
 * <li>Inline table editing with validation</li>
 * <li>Category search and filtering</li>
 * </ul>
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class CategoryController {
    private static final Logger LOGGER = Logger.getLogger(CategoryController.class.getName());

    @FXML
    private TextArea descriptionCategory_textArea;
    @FXML
    private TextArea nomCategory_textArea;
    @FXML
    private TableView<Category> filmCategory_tableView;
    @FXML
    private TableColumn<Category, String> nomCategory_tableColumn;
    @FXML
    private TableColumn<Category, String> descrptionCategory_tableColumn;
    @FXML
    private TableColumn<Category, Integer> idCategory_tableColumn;
    @FXML
    private TableColumn<Category, Button> delete_tableColumn;
    @FXML
    private AnchorPane categoryCrudInterface;
    @FXML
    private ComboBox<String> filterCriteriaComboBox;
    @FXML
    private StackPane stackPane;
    @FXML
    private Button AjouterCategory_Button;
    @FXML
    private TextField recherche_textField;

    /**
     * Initializes the controller after FXML loading is complete.
     * 
     * <p>
     * This method sets up the TableView with necessary columns, configures
     * cell factories and value factories, and initializes the filtering criteria.
     * It is automatically called by JavaFX after the FXML file is loaded.
     * </p>
     */
    @FXML
    void initialize() {
        this.delete_tableColumn = new TableColumn<>("delete");
        this.filmCategory_tableView.getColumns().add(this.delete_tableColumn);
        this.filmCategory_tableView.setEditable(true);
        this.setupCellFactory();
        this.setupCellValueFactory();
        this.setupCellOnEditCommit();
        this.readCategoryTable();
        this.filterCriteriaComboBox.setItems(FXCollections.observableArrayList("Name", "Description"));
    }


    /**
         * Filter the table to show categories whose name or description contains the given keyword.
         *
         * @param keyword the search term used to filter categories by name or description; when null or empty, all categories are shown
         */
    private void search(final String keyword) {
        final CategoryService categoryService = new CategoryService();
        final ObservableList<Category> filteredList = FXCollections.observableArrayList();
        PageRequest pageRequest = new PageRequest(0, 10);
        if (null == keyword || keyword.trim().isEmpty()) {
            filteredList.addAll(categoryService.read(pageRequest).getContent());
        }
 else {
            for (final Category category : categoryService.read(pageRequest).getContent()) {
                if (category.getName().toLowerCase().contains(keyword.toLowerCase())
                        || category.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(category);
                }

            }

        }

        this.filmCategory_tableView.setItems(filteredList);
    }


    /**
     * Create a new category from the current name and description inputs, persist it, show a confirmation alert, and refresh the category table.
     *
     * @param event the ActionEvent triggered by the insert button
     */
    @FXML
    void insertCategory(final ActionEvent event) {
        final CategoryService categoryService = new CategoryService();
        final Category category = new Category(this.nomCategory_textArea.getText(),
                this.descriptionCategory_textArea.getText());
        categoryService.create(category);
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("l'insertion est terminer");
        alert.setHeaderText("categorie");
        alert.setHeaderText("categorie");
        alert.show();
        this.readCategoryTable();
    }


    /**
     * Retrieves categories from the database and populates the table view.
     * 
     * <p>
     * This method fetches all categories using the CategoryService,
     * converts them to an ObservableList, and sets this list as the
     * items source for the category table view. Any exceptions during
     * this process are logged.
     * </p>
     * 
     * @see CategoryService#read()
     * @see FXCollections#observableArrayList(java.util.Collection)
     */
    void readCategoryTable() {
        try {
            final CategoryService categoryService = new CategoryService();
            PageRequest pageRequest = new PageRequest(0, 10);
            final ObservableList<Category> obC = FXCollections
                    .observableArrayList(categoryService.read(pageRequest).getContent());
            this.filmCategory_tableView.setItems(obC);
        }
 catch (final Exception e) {
            CategoryController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Shows the category CRUD interface when the add button is the event source.
     *
     * @param event the ActionEvent that triggered this handler
     */
    public void switchForm(final ActionEvent event) {
        if (event.getSource() == this.AjouterCategory_Button) {
            this.categoryCrudInterface.setVisible(true);
        }

    }


    /**
     * Delete the category identified by the given ID, show a confirmation alert, and refresh the category table.
     *
     * Removes the category with the provided ID from persistent storage, displays a confirmation alert to the user,
     * and reloads the table view to reflect the deletion.
     *
     * @param id the unique identifier of the category to delete
     */
    void deleteCategory(final Long id) {
        try {
            final CategoryService categoryService = new CategoryService();
            categoryService.delete(categoryService.getCategory(id));
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("la suppression est terminer");
            alert.setHeaderText("categorie");
            alert.setHeaderText("categorie");
            alert.show();
            this.readCategoryTable();
        }
 catch (final Exception e) {
            CategoryController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Persists updates for the given category, shows a confirmation alert, and refreshes the category table view.
     *
     * @param category the Category instance containing updated values to persist
     * @see CategoryService#update(Category)
     */
    void updateCategory(final Category category) {
        final CategoryService categoryService = new CategoryService();
        categoryService.update(category);
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("le mis à jour est terminer");
        alert.setHeaderText("categorie");
        alert.setHeaderText("categorie");
        alert.show();
        this.readCategoryTable();
    }


    /**
     * Configure table columns to use editable text cells with inline validation and error tooltips.
     *
     * Sets the id column invisible and assigns a custom cell factory to the name and description
     * columns that provides an editable TextFieldTableCell which validates input (must not be empty
     * and must start with an uppercase letter) and displays validation messages as a tooltip anchored
     * to the editing field.
     */
    private void setupCellFactory() {
        this.idCategory_tableColumn.setVisible(false);
        final Callback<TableColumn<Category, String>, TableCell<Category, String>> stringCellFactory = new Callback<>() {
            /**
             * Create a table cell that edits Category string values with inline validation and an error tooltip.
             *
             * @param param the TableColumn for which the cell is created
             * @return a TableCell that provides an editable text field which enforces that input is not empty and that the first character is an uppercase letter; validation errors are presented to the user as a tooltip on the editing field
             */
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<Category, String> call(final TableColumn<Category, String> param) {
                return new TextFieldTableCell<>(new DefaultStringConverter()) {
                    private Validator validator;

                    /**
                     * Begins cell editing and, if not already present, installs a validator and error tooltip on
                     * the editing TextField.
                     *
                     * <p>The installed validator requires the text to be non-empty and to start with an uppercase
                     * letter. While editing, validation errors are shown in a red-styled tooltip positioned above
                     * the TextField; the tooltip is hidden when the input becomes valid.</p>
                     */
                    @Override
                    /**
                     * Performs startEdit operation.
                     *
                     * @return the result of the operation
                     */
                    public void startEdit() {
                        super.startEdit();
                        final TextField textField = (TextField) this.getGraphic();
                        if (null != textField && null == validator) {
                            this.validator = new Validator();
                            this.validator.createCheck().dependsOn("text", textField.textProperty()).withMethod(c -> {
                                final String input = c.get("text");
                                if (null == input || input.trim().isEmpty()) {
                                    c.error("Input cannot be empty.");
                                }
 else if (!Character.isUpperCase(input.charAt(0))) {
                                    c.error("Please start with an uppercase letter.");
                                }

                            }
).decorates(textField).immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                /**
                                 * Updates the editing TextField's validation tooltip when the observed text changes.
                                 *
                                 * If validation errors exist, sets the tooltip text and displays it above the field; otherwise hides any visible tooltip.
                                 *
                                 * @param observable the observed String value
                                 * @param oldValue the previous text value
                                 * @param newValue the new text value
                                 */
                                @Override
                                /**
                                 * Performs changed operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void changed(final ObservableValue<? extends String> observable,
                                        final String oldValue, final String newValue) {
                                    CategoryController.LOGGER.info(String.valueOf(validator.containsErrors()));
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX(), bounds.getMinY() - 30);
                                    }
 else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }

                                    }

                                }

                            }
);
                        }

                    }

                }
;
            }

        }
;
        this.nomCategory_tableColumn.setCellFactory(stringCellFactory);
        this.descrptionCategory_tableColumn.setCellFactory(stringCellFactory);
    }


    /**
     * Configure cell value factories for the category table's ID, description, name, and delete columns.
     *
     * The ID column is bound to the Category id property, the name and description columns expose
     * their values as observable string properties, and the delete column provides a per-row
     * Button that invokes deleteCategory for the row's category ID when clicked.
     */
    private void setupCellValueFactory() {
        this.idCategory_tableColumn.setCellValueFactory(new PropertyValueFactory<Category, Integer>("id"));
        this.descrptionCategory_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
                    /**
                     * Create an observable string property containing the category's description.
                     *
                     * @param param cell data features that provide the Category for the current table row
                     * @return a SimpleStringProperty containing the Category's description
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<String> call(final TableColumn.CellDataFeatures<Category, String> param) {
                        return new SimpleStringProperty(param.getValue().getDescription());
                    }

                }
);
        this.nomCategory_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
                    /**
                     * Create a property holding the category's name.
                     *
                     * @param filmcategoryStringCellDataFeatures cell data features whose value is the Category instance for the row
                     * @return a SimpleStringProperty containing the Category's name
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<String> call(
                            final TableColumn.CellDataFeatures<Category, String> filmcategoryStringCellDataFeatures) {
                        return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getName());
                    }

                }
);
        this.delete_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Category, Button>, ObservableValue<Button>>() {
                    /**
                     * Create a cell value that contains a "delete" button for the category in the current table row.
                     *
                     * @param param cell data features for the table cell; provides access to the Category instance for this row
                     * @return an ObservableValue wrapping a Button that, when clicked, deletes the category represented by the row
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<Button> call(final TableColumn.CellDataFeatures<Category, Button> param) {
                        final Button button = new Button("delete");
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            /**
                             * Deletes the category represented by the table row that triggered this action.
                             *
                             * @param event the ActionEvent that triggered the deletion
                             */
                            @Override
                            /**
                             * Performs handle operation.
                             *
                             * @return the result of the operation
                             */
                            public void handle(final ActionEvent event) {
                                deleteCategory(param.getValue().getId());
                            }

                        }
);
                        return new SimpleObjectProperty<Button>(button);
                    }

                }
);
    }


    /**
     * Installs edit-commit handlers on the name and description table columns to apply edits to the underlying Category and persist the change.
     *
     * When a cell edit is committed for either the name or description column, the corresponding Category instance in the table's items is updated with the new value and persisted via updateCategory. Any exceptions encountered during persistence are logged.
     */
    private void setupCellOnEditCommit() {
        this.nomCategory_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Category, String>>() {
            /**
             * Apply the edited cell string to the Category in the edited row and persist the change.
             *
             * @param event the CellEditEvent containing the edited value and table position; the event's new value
             *              will be set as the Category's name for the row identified by event.getTablePosition().getRow()
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<Category, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue());
                    CategoryController.this
                            .updateCategory(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                }
 catch (final Exception e) {
                    CategoryController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }

            }

        }
);
        this.descrptionCategory_tableColumn
                .setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Category, String>>() {
                    /**
                     * Handle a table-cell edit commit by updating the edited Category's description and persisting the change.
                     *
                     * @param event the cell edit event containing the new description value and the row position of the edited Category
                     */
                    @Override
                    /**
                     * Performs handle operation.
                     *
                     * @return the result of the operation
                     */
                    public void handle(final TableColumn.CellEditEvent<Category, String> event) {
                        try {
                            event.getTableView().getItems().get(event.getTablePosition().getRow())
                                    .setDescription(event.getNewValue());
                            updateCategory(
                                    event.getTableView().getItems().get(event.getTablePosition().getRow()));
                        }
 catch (final Exception e) {
                            CategoryController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }

                    }

                }
);
    }

}
