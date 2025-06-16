package com.esprit.controllers.users;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.logging.Level;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import com.esprit.controllers.base.BaseController;
import com.esprit.models.users.Admin;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import com.esprit.utils.ui.UIUtils;
import com.esprit.utils.validation.ValidationUtils;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Modern enhanced AdminDashboardController using generic base functionality
 * Significantly reduced boilerplate code while adding modern UI elements
 */
public class ModernAdminDashboardController extends BaseController<User> {

    // Table columns
    private TableColumn<User, String> roleTableColumn;
    private TableColumn<User, HBox> photoTableColumn;
    private TableColumn<User, String> lastNameTableColumn;
    private TableColumn<User, String> passwordTableColumn;
    private TableColumn<User, String> phoneTableColumn;
    private TableColumn<User, String> firstNameTableColumn;
    private TableColumn<User, DatePicker> birthDateTableColumn;
    private TableColumn<User, String> addressTableColumn;
    private TableColumn<User, String> emailTableColumn;
    private TableColumn<User, Button> deleteTableColumn;

    // Form controls
    @FXML
    private TextField addressTextField;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ImageView profileImageView;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private ComboBox<String> roleComboBox;

    // Service
    private final UserService userService = new UserService();

    @Override
    protected void initialize() {
        super.initialize();
        setupRoleComboBox();
        loadData();

        // Apply modern styling
        applyModernStyling();
    }

    /**
     * Apply modern UI styling and animations
     */
    private void applyModernStyling() {
        // Add modern button styles and animations
        if (tableView != null) {
            tableView.getStyleClass().add("modern-table");
            UIUtils.animateIn(tableView, UIUtils.AnimationType.FADE_IN);
        }

        // Style form controls
        if (firstNameTextField != null) {
            firstNameTextField.getStyleClass().add("modern-textfield");
            UIUtils.addHoverEffect(firstNameTextField);
        }

        if (lastNameTextField != null) {
            lastNameTextField.getStyleClass().add("modern-textfield");
            UIUtils.addHoverEffect(lastNameTextField);
        }

        if (emailTextField != null) {
            emailTextField.getStyleClass().add("modern-textfield");
            UIUtils.addHoverEffect(emailTextField);
        }
    }

    /**
     * Setup role ComboBox with predefined values
     */
    private void setupRoleComboBox() {
        if (roleComboBox != null) {
            roleComboBox.setItems(FXCollections.observableArrayList("admin", "client", "responsable de cinema"));
            roleComboBox.getStyleClass().add("modern-combo");
        }
    }

    @Override
    protected void setupValidation() {
        // Use the new ValidationUtils for cleaner validation setup
        ValidationUtils.forField(firstNameTextField).required().name()
                .message("First name is required and must contain only letters").apply();

        ValidationUtils.forField(lastNameTextField).required().name()
                .message("Last name is required and must contain only letters").apply();

        ValidationUtils.addEmailValidation(emailTextField);
        ValidationUtils.addPhoneValidation(phoneTextField);
        ValidationUtils.addPasswordValidation(passwordTextField);
        ValidationUtils.addRequiredValidation(addressTextField, "Address");
    }

    @Override
    protected void setupTableColumns() {
        // Initialize table columns with modern styling
        firstNameTableColumn = new TableColumn<>("First Name");
        lastNameTableColumn = new TableColumn<>("Last Name");
        emailTableColumn = new TableColumn<>("Email");
        passwordTableColumn = new TableColumn<>("Password");
        phoneTableColumn = new TableColumn<>("Phone");
        addressTableColumn = new TableColumn<>("Address");
        birthDateTableColumn = new TableColumn<>("Birth Date");
        roleTableColumn = new TableColumn<>("Role");
        photoTableColumn = new TableColumn<>("Photo");
        deleteTableColumn = createDeleteColumn();

        // Add columns to table
        tableView.getColumns().addAll(firstNameTableColumn, lastNameTableColumn, emailTableColumn, passwordTableColumn,
                phoneTableColumn, addressTableColumn, birthDateTableColumn, roleTableColumn, photoTableColumn,
                deleteTableColumn);
    }

    @Override
    protected void setupCellFactories() {
        // Setup modern cell factories with validation
        firstNameTableColumn.setCellFactory(createTextFieldCellFactory(new StringConverter<String>() {
            @Override
            /**
             * Performs toString operation.
             *
             * @return the result of the operation
             */
            public String toString(String object) {
                return object != null ? object : "";
            }

            @Override
            /**
             * Performs fromString operation.
             *
             * @return the result of the operation
             */
            public String fromString(String string) {
                return string;
            }
        }, ValidationUtils.nameValidator(), "Invalid name format"));

        lastNameTableColumn.setCellFactory(createTextFieldCellFactory(new StringConverter<String>() {
            @Override
            /**
             * Performs toString operation.
             *
             * @return the result of the operation
             */
            public String toString(String object) {
                return object != null ? object : "";
            }

            @Override
            /**
             * Performs fromString operation.
             *
             * @return the result of the operation
             */
            public String fromString(String string) {
                return string;
            }
        }, ValidationUtils.nameValidator(), "Invalid name format"));

        emailTableColumn.setCellFactory(createTextFieldCellFactory(new StringConverter<String>() {
            @Override
            /**
             * Performs toString operation.
             *
             * @return the result of the operation
             */
            public String toString(String object) {
                return object != null ? object : "";
            }

            @Override
            /**
             * Performs fromString operation.
             *
             * @return the result of the operation
             */
            public String fromString(String string) {
                return string;
            }
        }, ValidationUtils.emailValidator(), "Invalid email format"));

        phoneTableColumn.setCellFactory(createTextFieldCellFactory(new StringConverter<String>() {
            @Override
            /**
             * Performs toString operation.
             *
             * @return the result of the operation
             */
            public String toString(String object) {
                return object != null ? object : "";
            }

            @Override
            /**
             * Performs fromString operation.
             *
             * @return the result of the operation
             */
            public String fromString(String string) {
                return string;
            }
        }, ValidationUtils.phoneValidator(), "Invalid phone format"));

        // Setup role column with ComboBox
        roleTableColumn.setCellFactory(column -> {
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.setItems(FXCollections.observableArrayList("admin", "client", "responsable de cinema"));
            comboBox.getStyleClass().add("modern-combo");

            TableCell<User, String> cell = new TableCell<User, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        comboBox.setValue(item);
                        setGraphic(comboBox);
                    }
                }
            };
            return cell;
        });

        // Setup photo column
        photoTableColumn.setCellFactory(column -> new TableCell<User, HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    User user = getTableRow().getItem();
                    ImageView imageView = UIUtils.createCircularImageView(user.getPhotoDeProfil(), 25);
                    HBox hbox = new HBox(imageView);
                    hbox.setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(hbox);
                }
            }
        });
    }

    @Override
    protected void setupCellValueFactories() {
        firstNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailTableColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        passwordTableColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        phoneTableColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressTableColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        roleTableColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        birthDateTableColumn.setCellValueFactory(param -> {
            DatePicker datePicker = new DatePicker();
            if (param.getValue().getBirthDate() != null) {
                datePicker.setValue(param.getValue().getBirthDate().toLocalDate());
            }
            return new SimpleObjectProperty<>(datePicker);
        });

        photoTableColumn.setCellValueFactory(param -> {
            ImageView imageView = UIUtils.createCircularImageView(param.getValue().getPhotoDeProfil(), 25);
            HBox hbox = new HBox(imageView);
            hbox.setAlignment(javafx.geometry.Pos.CENTER);
            return new SimpleObjectProperty<>(hbox);
        });
    }

    @Override
    protected void setupCellEditCommit() {
        firstNameTableColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setFirstName(event.getNewValue());
            updateItem(user);
        });

        lastNameTableColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setLastName(event.getNewValue());
            updateItem(user);
        });

        emailTableColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setEmail(event.getNewValue());
            updateItem(user);
        });

        phoneTableColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setPhoneNumber(event.getNewValue());
            updateItem(user);
        });
    }

    @Override
    protected Predicate<User> createSearchPredicate(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return user -> true;
        }

        String lowerCaseFilter = searchText.toLowerCase();
        return user -> {
            return (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(lowerCaseFilter))
                    || (user.getLastName() != null && user.getLastName().toLowerCase().contains(lowerCaseFilter))
                    || (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseFilter))
                    || (user.getRole() != null && user.getRole().toLowerCase().contains(lowerCaseFilter));
        };
    }

    @Override
    protected void loadData() {
        try {
            items.setAll(userService.read());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading users", e);
            UIUtils.showErrorNotification("Failed to load users: " + e.getMessage());
        }
    }

    @Override
    protected void clearFields() {
        if (idTextField != null)
            idTextField.setText("");
        if (firstNameTextField != null)
            firstNameTextField.setText("");
        if (lastNameTextField != null)
            lastNameTextField.setText("");
        if (phoneTextField != null)
            phoneTextField.setText("");
        if (passwordTextField != null)
            passwordTextField.setText("");
        if (roleComboBox != null)
            roleComboBox.setValue(null);
        if (addressTextField != null)
            addressTextField.setText("");
        if (birthDatePicker != null)
            birthDatePicker.setValue(null);
        if (emailTextField != null)
            emailTextField.setText("");
        if (profileImageView != null)
            profileImageView.setImage(null);
    }

    @Override
    protected void saveItem() {
        if (!validateForm()) {
            UIUtils.showErrorNotification("Please correct the form errors before saving.");
            return;
        }

        try {
            String role = roleComboBox.getValue();
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            String phoneNumber = phoneTextField.getText();
            String password = passwordTextField.getText();
            String email = emailTextField.getText();
            String address = addressTextField.getText();
            LocalDate birthDate = birthDatePicker.getValue();

            User user;
            if ("admin".equals(role)) {
                user = new Admin(firstName, lastName, phoneNumber, password, role, address, Date.valueOf(birthDate),
                        email, profileImageView.getImage() != null ? profileImageView.getImage().getUrl() : "");
            } else {
                user = new Client(firstName, lastName, phoneNumber, password, role, address, Date.valueOf(birthDate),
                        email, profileImageView.getImage() != null ? profileImageView.getImage().getUrl() : "");
            }

            userService.create(user);
            loadData();
            clearFields();

            UIUtils.showSuccessNotification("User created successfully!");
            UIUtils.animateIn(tableView, UIUtils.AnimationType.BOUNCE_IN);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating user", e);
            UIUtils.showErrorNotification("Failed to create user: " + e.getMessage());
        }
    }

    @Override
    protected void updateItem(User user) {
        try {
            userService.update(user);
            loadData();
            UIUtils.showSuccessNotification("User updated successfully!");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating user", e);
            UIUtils.showErrorNotification("Failed to update user: " + e.getMessage());
        }
    }

    @Override
    protected void deleteItem(User user) {
        try {
            userService.delete(user);
            loadData();
            UIUtils.showSuccessNotification("User deleted successfully!");
            UIUtils.animateIn(tableView, UIUtils.AnimationType.FADE_IN);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting user", e);
            UIUtils.showErrorNotification("Failed to delete user: " + e.getMessage());
        }
    }

    @Override
    protected boolean validateForm() {
        return ValidationUtils.validateFields(firstNameTextField, lastNameTextField, emailTextField, phoneTextField,
                passwordTextField, addressTextField) && roleComboBox.getValue() != null
                && birthDatePicker.getValue() != null;
    }

    /**
     * Generate PDF report with modern styling
     */
    @FXML
    /**
     * Performs generatePDF operation.
     *
     * @return the result of the operation
     */
    public void generatePDF() {
        try {
            userService.generateUserPDF();
            UIUtils.showSuccessNotification("PDF report generated successfully!");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generating PDF", e);
            UIUtils.showErrorNotification("Failed to generate PDF: " + e.getMessage());
        }
    }

    /**
     * Sign out with modern animation
     */
    @FXML
    /**
     * Performs signOut operation.
     *
     * @return the result of the operation
     */
    public void signOut(ActionEvent event) {
        try {
            UIUtils.animateOut(tableView, UIUtils.AnimationType.SLIDE_OUT_RIGHT, () -> {
                try {
                    Stage stage = (Stage) emailTextField.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/SignUp.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    UIUtils.applyLightTheme(scene);
                    stage.setScene(scene);
                    UIUtils.animateIn(root, UIUtils.AnimationType.FADE_IN);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error during sign out", e);
                }
            });
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during sign out", e);
            UIUtils.showErrorNotification("Error during sign out: " + e.getMessage());
        }
    }

    /**
     * Toggle between light and dark themes
     */
    @FXML
    /**
     * Performs toggleTheme operation.
     *
     * @return the result of the operation
     */
    public void toggleTheme() {
        Scene scene = tableView.getScene();
        if (scene.getStylesheets().contains("/styles/dark-theme.css")) {
            UIUtils.applyLightTheme(scene);
            UIUtils.showInfoNotification("Switched to light theme");
        } else {
            UIUtils.applyDarkTheme(scene);
            UIUtils.showInfoNotification("Switched to dark theme");
        }
    }

    /**
     * Create modern action buttons
     */
    @FXML
    private void createModernButtons() {
        // This would be called in FXML or during initialization
        Button addButton = UIUtils.createPrimaryButton("Add User", FontAwesomeSolid.PLUS);
        Button editButton = UIUtils.createSecondaryButton("Edit", FontAwesomeSolid.EDIT);
        Button deleteButton = UIUtils.createDangerButton("Delete", FontAwesomeSolid.TRASH);
        Button refreshButton = UIUtils.createSuccessButton("Refresh", FontAwesomeSolid.SYNC_ALT);

        addButton.setOnAction(e -> saveItem());
        editButton.setOnAction(e -> {
            User selected = getSelectedItem();
            if (selected != null) {
                // Populate form with selected user data
                populateForm(selected);
            } else {
                UIUtils.showWarningNotification("Please select a user to edit");
            }
        });
        deleteButton.setOnAction(e -> {
            User selected = getSelectedItem();
            if (selected != null) {
                deleteItem(selected);
            } else {
                UIUtils.showWarningNotification("Please select a user to delete");
            }
        });
        refreshButton.setOnAction(e -> refreshTable());

        // Add animations to buttons
        UIUtils.addHoverEffect(addButton);
        UIUtils.addHoverEffect(editButton);
        UIUtils.addHoverEffect(deleteButton);
        UIUtils.addHoverEffect(refreshButton);

        UIUtils.addClickAnimation(addButton);
        UIUtils.addClickAnimation(editButton);
        UIUtils.addClickAnimation(deleteButton);
        UIUtils.addClickAnimation(refreshButton);
    }

    /**
     * Populate form with user data for editing
     */
    private void populateForm(User user) {
        if (user != null) {
            firstNameTextField.setText(user.getFirstName());
            lastNameTextField.setText(user.getLastName());
            emailTextField.setText(user.getEmail());
            phoneTextField.setText(user.getPhoneNumber());
            passwordTextField.setText(user.getPassword());
            addressTextField.setText(user.getAddress());
            roleComboBox.setValue(user.getRole());

            if (user.getBirthDate() != null) {
                birthDatePicker.setValue(user.getBirthDate().toLocalDate());
            }

            // Load profile image if available
            if (user.getPhotoDeProfil() != null && !user.getPhotoDeProfil().isEmpty()) {
                try {
                    profileImageView.setImage(new javafx.scene.image.Image(user.getPhotoDeProfil()));
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Could not load profile image", e);
                }
            }
        }
    }
}
