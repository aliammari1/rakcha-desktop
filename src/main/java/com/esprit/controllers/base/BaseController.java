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
 * and UI management. Reduces boilerplate code across controllers.
 *
 * @param <T>
 *            The entity type this controller manages
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
     * Initialize the controller with common setup
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
     * Setup the TableView with common configurations
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
     * Setup search functionality
     */
    protected void setupSearch() {
        if (searchTextField != null && filteredItems != null) {
            searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredItems.setPredicate(createSearchPredicate(newValue));
            });
        }
    }

    /**
     * Setup validation for form fields
     */
    protected abstract void setupValidation();

    /**
     * Setup table columns - to be implemented by subclasses
     */
    protected abstract void setupTableColumns();

    /**
     * Setup cell factories for table columns
     */
    protected abstract void setupCellFactories();

    /**
     * Setup cell value factories for table columns
     */
    protected abstract void setupCellValueFactories();

    /**
     * Setup cell edit commit handlers
     */
    protected abstract void setupCellEditCommit();

    /**
     * Create search predicate for filtering
     */
    protected abstract Predicate<T> createSearchPredicate(String searchText);

    /**
     * Load data into the table
     */
    protected abstract void loadData();

    /**
     * Add validation listener to a text field
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
     * Generic method to create text field table cells with validation
     */
    protected <S> Callback<TableColumn<T, S>, TableCell<T, S>> createTextFieldCellFactory(StringConverter<S> converter,
            Predicate<S> validator, String errorMessage) {
        return column -> {
            TextFieldTableCell<T, S> cell = new TextFieldTableCell<T, S>(converter) {
                @Override
                /**
                 * Performs updateItem operation.
                 *
                 * @return the result of the operation
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
     * Generic method to create delete button column
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
     * Clear all form fields
     */
    @FXML
    protected abstract void clearFields();

    /**
     * Save/Create new item
     */
    @FXML
    protected abstract void saveItem();

    /**
     * Update existing item
     */
    protected abstract void updateItem(T item);

    /**
     * Delete item
     */
    protected abstract void deleteItem(T item);

    /**
     * Refresh the table data
     */
    @FXML
    protected void refreshTable() {
        loadData();
    }

    /**
     * Show error alert dialog
     */
    protected void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show information alert dialog
     */
    protected void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show confirmation dialog
     */
    protected boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Validate form fields before save/update
     */
    protected abstract boolean validateForm();

    /**
     * Get selected item from table
     */
    protected T getSelectedItem() {
        return tableView != null ? tableView.getSelectionModel().getSelectedItem() : null;
    }

    /**
     * Select item in table
     */
    protected void selectItem(T item) {
        if (tableView != null && item != null) {
            tableView.getSelectionModel().select(item);
        }
    }
}
