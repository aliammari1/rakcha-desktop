package com.esprit.controllers.base;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import net.synedra.validatorfx.Validator;

/**
 * Generic base controller providing common functionality for CRUD operations
 * and UI management.
 * 
 * <p>
 * This abstract class reduces boilerplate code across controllers by providing
 * common functionality such as table setup, search functionality, validation,
 * and standard UI operations. It follows the Template Method pattern where
 * concrete implementations provide specific behavior while common operations
 * are handled by this base class.
 * </p>
 *
 * @param <T> the entity type this controller manages
 * @author Esprit Team
 * @version 1.0
 * @since 1.0
 */
public abstract class BaseController<T> {

    protected static final Logger logger = Logger.getLogger(BaseController.class.getName());

    protected Validator validator;
    protected FilteredList<T> filteredItems;
    protected SortedList<T> sortedItems;
    protected ObservableList<T> items = FXCollections.observableArrayList();

    @FXML
    protected TableView<T> tableView;

    @FXML
    protected TextField searchTextField;

    @FXML
    protected ImageView imageView;

    /**
     * Initialize the controller with common setup.
     * 
     * <p>
     * This method is called automatically when the FXML file is loaded.
     * It sets up validation, table view, search functionality, and loads initial
     * data.
     * </p>
     */
    @FXML
    protected void initialize() {
        validator = new Validator();
        setupTableView();
        setupSearch();
        setupValidation();
        loadData();
    }

    /**
     * Setup the TableView with common configurations.
     * 
     * <p>
     * Configures table view properties, sets up columns, cell factories,
     * and initializes filtered and sorted lists for data binding.
     * </p>
     */
    protected void setupTableView() {
        if (tableView != null) {
            tableView.setEditable(true);
            setupTableColumns();
            setupCellFactories();
            setupCellValueFactories();
            setupCellEditCommit();

            // Setup filtered and sorted lists
            filteredItems = new FilteredList<>(items, p -> true);
            sortedItems = new SortedList<>(filteredItems);
            sortedItems.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedItems);
        }
    }

    /**
     * Setup search functionality for the table view.
     */
    protected void setupSearch() {
        if (searchTextField != null && filteredItems != null) {
            searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredItems.setPredicate(createSearchPredicate(newValue));
            });
        }
    }

    /**
     * Setup validation for form fields.
     * 
     * <p>
     * This method must be implemented by subclasses to define specific
     * validation rules for their form fields.
     * </p>
     */
    protected abstract void setupValidation();

    /**
     * Setup table columns - to be implemented by subclasses.
     * 
     * <p>
     * Subclasses should implement this method to define their specific
     * table column structure and properties.
     * </p>
     */
    protected abstract void setupTableColumns();

    /**
     * Setup cell factories for table columns.
     * 
     * <p>
     * Subclasses should implement this method to define custom cell
     * factories for their table columns.
     * </p>
     */
    protected abstract void setupCellFactories();

    /**
     * Setup cell value factories for table columns.
     * 
     * <p>
     * Subclasses should implement this method to bind table columns
     * to entity properties.
     * </p>
     */
    protected abstract void setupCellValueFactories();

    /**
     * Setup cell edit commit handlers for editable columns.
     * 
     * <p>
     * Subclasses should implement this method to handle cell edit
     * commit events for editable table columns.
     * </p>
     */
    protected abstract void setupCellEditCommit();

    /**
     * Create search predicate for filtering table data.
     * 
     * @param searchText the text to search for
     * @return a predicate that filters entities based on the search text
     */
    protected abstract Predicate<T> createSearchPredicate(String searchText);

    /**
     * Load data into the table.
     * 
     * <p>
     * Subclasses should implement this method to load their specific
     * entity data from the appropriate service layer.
     * </p>
     */
    protected abstract void loadData();

    /**
     * Add validation listener to a text field.
     * 
     * @param textField           the text field to add validation to
     * @param validationPredicate the predicate to validate the text
     * @param errorMessage        the error message to display if validation fails
     */
    protected void addValidationListener(TextField textField, Predicate<String> validationPredicate,
            String errorMessage) {
        if (textField == null)
            return;

        Tooltip tooltip = new Tooltip();
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !validationPredicate.test(newValue)) {
                tooltip.setText(errorMessage);
                textField.setTooltip(tooltip);
                textField.getStyleClass().removeAll("valid-field");
                textField.getStyleClass().add("invalid-field");
            } else {
                textField.setTooltip(null);
                textField.getStyleClass().removeAll("invalid-field");
                textField.getStyleClass().add("valid-field");
            }
        });
    }

    /**
     * Generic method to create text field table cells with validation.
     * 
     * @param <S>          the type of the cell value
     * @param converter    the string converter for the cell value
     * @param validator    the predicate to validate the cell value
     * @param errorMessage the error message to display if validation fails
     * @return a callback that creates validated text field table cells
     */
    protected <S> Callback<TableColumn<T, S>, TableCell<T, S>> createTextFieldCellFactory(StringConverter<S> converter,
            Predicate<S> validator, String errorMessage) {
        return column -> {
            TextFieldTableCell<T, S> cell = new TextFieldTableCell<T, S>(converter) {
                @Override
                /**
                 * Updates the item display in the table cell.
                 *
                 * @param item  the item to display
                 * @param empty whether the cell is empty
                 */
                public void updateItem(S item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty && item != null && !validator.test(item)) {
                        setTooltip(new Tooltip(errorMessage));
                        getStyleClass().add("invalid-cell");
                    } else {
                        setTooltip(null);
                        getStyleClass().removeAll("invalid-cell");
                    }
                }
            };
            return cell;
        };
    }

    /**
     * Generic method to create delete button column.
     * 
     * @return a table column with delete buttons for each row
     */
    protected TableColumn<T, Button> createDeleteColumn() {
        TableColumn<T, Button> deleteColumn = new TableColumn<>("Actions");
        deleteColumn.setCellValueFactory(param -> {
            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().addAll("btn", "btn-danger");
            deleteButton.setOnAction(event -> {
                T item = param.getValue();
                if (showConfirmationDialog("Delete Item", "Are you sure you want to delete this item?")) {
                    deleteItem(item);
                }
            });
            return new javafx.beans.property.SimpleObjectProperty<>(deleteButton);
        });
        return deleteColumn;
    }

    /**
     * Import image functionality
     */
    @FXML
    protected void importImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Copy file to resources
                Path targetPath = Paths.get("src/main/resources/images", selectedFile.getName());
                Files.createDirectories(targetPath.getParent());
                Files.copy(selectedFile.toPath(), targetPath);

                // Set image in ImageView
                if (imageView != null) {
                    Image image = new Image(targetPath.toUri().toString());
                    imageView.setImage(image);
                }

                logger.info("Image imported successfully: " + targetPath);
            } catch (Exception e) {
                logger.severe("Error importing image: " + e.getMessage());
                showErrorAlert("Error", "Failed to import image: " + e.getMessage());
            }
        }
    }

    /**
     * Clear all form fields.
     */
    @FXML
    protected abstract void clearFields();

    /**
     * Save/Create new item.
     */
    @FXML
    protected abstract void saveItem();

    /**
     * Update existing item.
     * 
     * @param item the item to update
     */
    protected abstract void updateItem(T item);

    /**
     * Delete item.
     * 
     * @param item the item to delete
     */
    protected abstract void deleteItem(T item);

    /**
     * Refresh the table data.
     */
    @FXML
    protected void refreshTable() {
        loadData();
    }

    /**
     * Show error alert dialog.
     * 
     * @param title   the alert title
     * @param message the alert message
     */
    protected void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show information alert dialog.
     * 
     * @param title   the alert title
     * @param message the alert message
     */
    protected void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show confirmation dialog.
     * 
     * @param title   the alert title
     * @param message the alert message
     * @return true if user confirms, false otherwise
     */
    protected boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Validate form fields before save/update.
     * 
     * @return true if form is valid, false otherwise
     */
    protected abstract boolean validateForm();

    /**
     * Get selected item from table.
     * 
     * @return the currently selected item, or null if none selected
     */
    protected T getSelectedItem() {
        return tableView != null ? tableView.getSelectionModel().getSelectedItem() : null;
    }

    /**
     * Select item in table.
     * 
     * @param item the item to select
     */
    protected void selectItem(T item) {
        if (tableView != null && item != null) {
            tableView.getSelectionModel().select(item);
        }
    }
}
