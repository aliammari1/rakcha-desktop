package com.esprit.controllers.common;

import com.esprit.enums.CategoryType;
import com.esprit.models.common.Category;
import com.esprit.models.users.Admin;
import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.User;
import com.esprit.services.common.CategoryService;
import com.esprit.utils.PageRequest;
import com.esprit.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Unified controller for managing all category types (MOVIE, SERIE, PRODUCT).
 * Provides role-based filtering so admins see all categories while cinema managers
 * only see movie and series categories.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 */
@Slf4j
public class CategoryManagementController implements Initializable {

    private final CategoryService categoryService = new CategoryService();
    private User currentUser;
    private CategoryType selectedType = null; // null means all types

    // FXML Components
    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private TableColumn<Category, Long> idColumn;
    @FXML
    private TableColumn<Category, String> nameColumn;
    @FXML
    private TableColumn<Category, String> descriptionColumn;
    @FXML
    private TableColumn<Category, String> typeColumn;
    @FXML
    private TableColumn<Category, Void> actionsColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<CategoryType> typeComboBox;
    @FXML
    private ComboBox<CategoryType> filterTypeComboBox;

    @FXML
    private TextField searchField;
    @FXML
    private Button addButton;
    @FXML
    private Button clearButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Label statusLabel;

    private Category selectedCategory = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = SessionManager.getCurrentUser();
        setupTable();
        setupTypeComboBoxes();
        setupSearch();
        loadCategories();
        log.info("CategoryManagementController initialized for user: {}",
            currentUser != null ? currentUser.getEmail() : "unknown");
    }

    /**
     * Configure table columns and cell factories.
     */
    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getType().toString()));

        // Setup actions column with Edit and Delete buttons
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(5, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("edit-btn");
                deleteBtn.getStyleClass().add("delete-btn");

                editBtn.setOnAction(event -> {
                    Category category = getTableView().getItems().get(getIndex());
                    editCategory(category);
                });

                deleteBtn.setOnAction(event -> {
                    Category category = getTableView().getItems().get(getIndex());
                    deleteCategory(category);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonBox);
            }
        });

        // Enable row selection to populate form
        categoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });

        categoryTable.setEditable(false);
    }

    /**
     * Setup type combo boxes based on user role.
     */
    private void setupTypeComboBoxes() {
        ObservableList<CategoryType> availableTypes = FXCollections.observableArrayList();

        if (currentUser instanceof Admin) {
            // Admin can manage all category types
            availableTypes.addAll(CategoryType.MOVIE, CategoryType.SERIE, CategoryType.PRODUCT);
        } else if (currentUser instanceof CinemaManager) {
            // Cinema manager can only manage movie and series categories
            availableTypes.addAll(CategoryType.MOVIE, CategoryType.SERIE);
        } else {
            // Default: show all (read-only for clients, but they shouldn't access this screen)
            availableTypes.addAll(CategoryType.values());
        }

        typeComboBox.setItems(availableTypes);
        typeComboBox.getSelectionModel().selectFirst();

        // Filter combo box includes "All" option
        ObservableList<CategoryType> filterTypes = FXCollections.observableArrayList();
        filterTypes.add(null); // null represents "All"
        filterTypes.addAll(availableTypes);
        filterTypeComboBox.setItems(filterTypes);
        filterTypeComboBox.getSelectionModel().selectFirst();

        // Custom cell factory to display "All" for null
        filterTypeComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CategoryType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item == null ? "All Categories" : item.toString()));
            }
        });
        filterTypeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CategoryType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item == null ? "All Categories" : item.toString()));
            }
        });

        // Filter change listener
        filterTypeComboBox.setOnAction(e -> {
            selectedType = filterTypeComboBox.getValue();
            loadCategories();
        });
    }

    /**
     * Setup search functionality.
     */
    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterCategories(newVal));
    }

    /**
     * Load categories from database based on current filter.
     */
    private void loadCategories() {
        ObservableList<Category> categories = FXCollections.observableArrayList();
        PageRequest pageRequest = PageRequest.defaultPage(); // Load up to 100 categories

        if (selectedType != null) {
            categories.addAll(categoryService.readByType(selectedType, pageRequest).getContent());
        } else {
            // Filter based on user role when showing all
            if (currentUser instanceof Admin) {
                categories.addAll(categoryService.read(pageRequest).getContent());
            } else if (currentUser instanceof CinemaManager) {
                // Only show MOVIE and SERIE for cinema managers
                categories.addAll(categoryService.getAllByType(CategoryType.MOVIE));
                categories.addAll(categoryService.getAllByType(CategoryType.SERIE));
            }
        }

        categoryTable.setItems(categories);
        updateStatus("Loaded " + categories.size() + " categories");
    }

    /**
     * Filter displayed categories by search text.
     */
    private void filterCategories(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadCategories();
            return;
        }

        String lowerSearch = searchText.toLowerCase().trim();
        ObservableList<Category> filtered = categoryTable.getItems().filtered(cat ->
            cat.getName().toLowerCase().contains(lowerSearch) ||
                cat.getDescription().toLowerCase().contains(lowerSearch) ||
                cat.getType().toString().toLowerCase().contains(lowerSearch)
        );
        categoryTable.setItems(filtered);
    }

    /**
     * Populate the form with category data for editing.
     */
    private void populateForm(Category category) {
        selectedCategory = category;
        nameField.setText(category.getName());
        descriptionField.setText(category.getDescription());
        typeComboBox.setValue(category.getType());
        addButton.setText("Update");
    }

    /**
     * Clear the form fields.
     */
    @FXML
    private void clearForm() {
        selectedCategory = null;
        nameField.clear();
        descriptionField.clear();
        typeComboBox.getSelectionModel().selectFirst();
        addButton.setText("Add");
        categoryTable.getSelectionModel().clearSelection();
    }

    /**
     * Add or update a category.
     */
    @FXML
    private void saveCategory(ActionEvent event) {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        CategoryType type = typeComboBox.getValue();

        // Validation
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Category name is required.");
            return;
        }
        if (name.length() < 2 || name.length() > 50) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Category name must be between 2 and 50 characters.");
            return;
        }
        if (description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Description is required.");
            return;
        }
        if (type == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a category type.");
            return;
        }

        try {
            if (selectedCategory != null) {
                // Update existing category
                selectedCategory.setName(name);
                selectedCategory.setDescription(description);
                selectedCategory.setType(type);
                categoryService.update(selectedCategory);
                updateStatus("Category updated successfully");
                log.info("Updated category: {}", selectedCategory.getId());
            } else {
                // Create new category
                Category newCategory = new Category(name, description, type);
                categoryService.create(newCategory);
                updateStatus("Category created successfully");
                log.info("Created new category: {}", name);
            }

            clearForm();
            loadCategories();
        } catch (Exception e) {
            log.error("Error saving category: {}", e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save category: " + e.getMessage());
        }
    }

    /**
     * Edit a category by populating the form.
     */
    private void editCategory(Category category) {
        populateForm(category);
    }

    /**
     * Delete a category with confirmation.
     */
    private void deleteCategory(Category category) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Category");
        confirmDialog.setContentText("Are you sure you want to delete the category '" + category.getName() + "'?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                categoryService.delete(category);
                loadCategories();
                clearForm();
                updateStatus("Category deleted successfully");
                log.info("Deleted category: {}", category.getId());
            } catch (Exception e) {
                log.error("Error deleting category: {}", e.getMessage(), e);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete category: " + e.getMessage());
            }
        }
    }

    /**
     * Show an alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Update the status label.
     */
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    /**
     * Navigate back to the appropriate dashboard.
     */
    @FXML
    private void goBack(ActionEvent event) {
        try {
            String fxmlPath;
            if (currentUser instanceof Admin) {
                fxmlPath = "/ui/users/HomeAdmin.fxml";
            } else if (currentUser instanceof CinemaManager) {
                fxmlPath = "/ui/users/HomeCinemaManager.fxml";
            } else {
                fxmlPath = "/ui/users/HomeClient.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) categoryTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            log.error("Error navigating back: {}", e.getMessage(), e);
        }
    }

    /**
     * Set the initial category type filter (used when navigating from specific context).
     */
    public void setInitialType(CategoryType type) {
        this.selectedType = type;
        if (filterTypeComboBox != null) {
            filterTypeComboBox.setValue(type);
        }
        loadCategories();
    }

    /**
     * Set the current user for role-based access control.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        setupTypeComboBoxes();
        loadCategories();
    }
}
