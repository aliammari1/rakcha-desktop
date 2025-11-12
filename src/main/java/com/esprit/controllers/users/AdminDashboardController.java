package com.esprit.controllers.users;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.users.Admin;
import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import com.esprit.utils.CloudinaryStorage;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import net.synedra.validatorfx.Validator;

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class AdminDashboardController {
    private static final Logger LOGGER = Logger.getLogger(AdminDashboardController.class.getName());
    TableColumn<User, String> roleTableColumn;
    TableColumn<User, HBox> photoDeProfilTableColumn;
    TableColumn<User, String> lastNameTableColumn;
    TableColumn<User, String> passwordTableColumn;
    TableColumn<User, String> numTelTableColumn;
    TableColumn<User, String> firstNameTableColumn;
    TableColumn<User, DatePicker> dateDeNaissanceTableColumn;
    TableColumn<User, String> adresseTableColumn;
    TableColumn<User, String> emailTableColumn;
    TableColumn<User, Button> deleteTableColumn;
    Validator formValidator;
    Validator tableValidator;
    Tooltip formValidatorTooltip;
    Tooltip tableValidatorTooltip;
    private String cloudinaryImageUrl;
    @FXML
    private TextField adresseTextField;
    @FXML
    private DatePicker dateDeNaissanceDatePicker;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField phoneNumberTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ImageView photoDeProfilImageView;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private TableView<User> userTableView;

    /**
     * Sets up user interface components for a table displaying information about
     * users, including text fields, combo boxes, and validation listeners to ensure
     * data validity.
     */
    @FXML
    void initialize() {
        try {
            this.roleTableColumn = new TableColumn<>("role");
            this.photoDeProfilTableColumn = new TableColumn<>("photo de profil");
            this.lastNameTableColumn = new TableColumn<>("lastName");
            this.passwordTableColumn = new TableColumn<>("password");
            this.numTelTableColumn = new TableColumn<>("numero de telephone");
            this.firstNameTableColumn = new TableColumn<>("firstName");
            this.dateDeNaissanceTableColumn = new TableColumn<>("date de naissance");
            this.adresseTableColumn = new TableColumn<>("adresse");
            this.emailTableColumn = new TableColumn<>("email");
            this.deleteTableColumn = new TableColumn<>("delete");
            this.tableValidator = new Validator();
            this.userTableView.setEditable(true);
            final List<TableColumn<User, ?>> columns = Arrays.asList(this.firstNameTableColumn,
                    this.lastNameTableColumn, this.emailTableColumn, this.passwordTableColumn, this.numTelTableColumn,
                    this.adresseTableColumn, this.dateDeNaissanceTableColumn, this.roleTableColumn,
                    this.photoDeProfilTableColumn, this.deleteTableColumn);
            this.userTableView.getColumns().addAll(columns);
            this.setupCellValueFactories();
            this.setupCellFactories();
            this.setupCellOnEditCommit();
            final Tooltip tooltip = new Tooltip();
            this.addValidationListener(this.firstNameTextField, newValue -> newValue.toLowerCase().equals(newValue),
                    "Please use only lowercase letters.");
            this.addValidationListener(this.lastNameTextField, newValue -> newValue.toLowerCase().equals(newValue),
                    "Please use only lowercase letters.");
            this.addValidationListener(this.adresseTextField, newValue -> newValue.toLowerCase().equals(newValue),
                    "Please use only lowercase letters.");
            final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            this.addValidationListener(this.emailTextField, newValue -> newValue.matches(emailRegex),
                    "Invalid email format.");
            this.addValidationListener(this.passwordTextField, newValue -> newValue.toLowerCase().equals(newValue),
                    "Please use only lowercase letters.");
            final String numberRegex = "\\d*";
            this.addValidationListener(this.phoneNumberTextField, newValue -> newValue.matches(numberRegex),
                    "Please use only numbers.");
            final List<String> roleList = List.of("admin");
            for (final String role : roleList) {
                this.roleComboBox.getItems().add(role);
            }

            this.readUserTable();
        } catch (final Exception e) {
            AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Attaches a listener to a TextField that validates user input and shows a tooltip with an error message when the value is invalid.
     *
     * The tooltip is displayed near the TextField while the value fails the predicate and is hidden when the field loses focus or the value becomes valid.
     *
     * @param textField           the TextField to validate and to which the tooltip will be attached
     * @param validationPredicate a predicate that returns `true` for valid input and `false` for invalid input
     * @param errorMessage        the message shown in the tooltip when the input does not satisfy the predicate
     */
    private void addValidationListener(final TextField textField, final Predicate<String> validationPredicate,
            final String errorMessage) {
        final Tooltip tooltip = new Tooltip();
        textField.textProperty().addListener(new ChangeListener<String>() {
            /**
             * Validates a text change for the associated TextField and shows or hides an inline tooltip.
             *
             * When the new value is empty or fails the configured predicate, a red tooltip with the
             * error message is shown near the TextField; when valid, any shown tooltip is hidden.
             *
             * @param observable the observable emitting text changes
             * @param oldValue the previous text value
             * @param newValue the new text value to validate
             */
            @Override
            /**
             * Performs changed operation.
             *
             * @return the result of the operation
             */
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                String error = null;
                if (null != newValue) {
                    if (!validationPredicate.test(newValue)) {
                        error = errorMessage;
                    }
 else if (newValue.isEmpty()) {
                        error = "The string is empty.";
                    }

                }

                final Window window = textField.getScene().getWindow();
                final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                if (null != error) {
                    tooltip.setText(error);
                    tooltip.setStyle("-fx-background-color: #f00;");
                    textField.setTooltip(tooltip);
                    textField.getTooltip().show(window, bounds.getMinX() - 10, bounds.getMinY() + 30);
                }
 else {
                    if (null != textField.getTooltip()) {
                        textField.getTooltip().hide();
                    }

                }

            }

        }
);
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && null != textField.getTooltip()) {
                textField.getTooltip().hide();
            }

        }
);
    }


    /**
     * Reads data from a User Service and populates the user table view with the
     * retrieved data.
     */
    @FXML
    void readUserTable() {
        try {
            final UserService userService = new UserService();
            PageRequest pageRequest = new PageRequest(0, 10);
            final Page<User> userList = userService.read(pageRequest);
            this.userTableView.setItems(FXCollections.observableArrayList(userList.getContent()));
        } catch (final Exception e) {
            AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Create a new admin user from the current form inputs and persist it after validation.
     *
     * <p>Validates required fields (first name, last name, phone number, password, role, email,
     * and birth date). If validation fails, displays an error Alert with a descriptive message.
     * On success, constructs an Admin using the provided values (including the selected profile
     * image path) and delegates persistence to UserService.create.</p>
     *
     * @param event the ActionEvent that triggered the add action
     */
    @FXML
    void addAdmin(final ActionEvent event) {
        try {
            final String role = this.roleComboBox.getValue();
            User user = null;
            URI uri = null;
            if ("admin".equals(role)) {
                final String fullPath = this.photoDeProfilImageView.getImage().getUrl();
                final String requiredPath = fullPath.substring(fullPath.indexOf("/img/users/"));
                uri = new URI(requiredPath);
                final String firstName = this.firstNameTextField.getText();
                final String lastName = this.lastNameTextField.getText();
                final String phoneNumber = this.phoneNumberTextField.getText();
                final String password = this.passwordTextField.getText();
                final String email = this.emailTextField.getText();
                final LocalDate dateDeNaissance = this.dateDeNaissanceDatePicker.getValue();
                // Perform input validation
                if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()
                        || role.isEmpty() || email.isEmpty() || null == dateDeNaissance) {
                    final Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all the required fields",
                            ButtonType.CLOSE);
                    alert.show();
                    return;
                }

                // Validate name format
                if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
                    final Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid name format", ButtonType.CLOSE);
                    alert.show();
                    return;
                }

                // Validate password length
                if (8 > password.length()) {
                    final Alert alert = new Alert(Alert.AlertType.ERROR, "Password must be at least 8 characters long",
                            ButtonType.CLOSE);
                    alert.show();
                    return;
                }

                // Validate phone number format
                if (!phoneNumber.matches("\\d{10}")) {
                    final Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid phone number format",
                            ButtonType.CLOSE);
                    alert.show();
                    return;
                }

                // Validate email format
                if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                    final Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid email format", ButtonType.CLOSE);
                    alert.show();
                    return;
                }

                user = new Admin(firstName, lastName, phoneNumber, password, role, email, Date.valueOf(dateDeNaissance),
                        email, uri.getPath());
            }
 else {
                final Alert alert = new Alert(Alert.AlertType.ERROR, "the given role is not available",
                        ButtonType.CLOSE);
                alert.show();
                return;
            }

            final UserService userService = new UserService();
            userService.create(user);
        } catch (final Exception e) {
            AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Configure how each table column obtains the value displayed for a User row.
     *
     * <p>Sets PropertyValueFactory mappings for simple string properties (first name, last name,
     * phone, password, role, address, email). For the date-of-birth column it provides a
     * DatePicker initialized from the user's birth date. For the profile-photo column it supplies
     * an HBox containing an ImageView sourced from the user's photo URL and attaches a click
     * handler that opens a FileChooser to replace the image and update the controller's
     * photoDeProfilImageView. For the delete column it provides a Button that deletes the row's
     * user and refreshes the table.</p>
     */
    private void setupCellValueFactories() {
        this.firstNameTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        this.lastNameTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        this.numTelTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("phoneNumber"));
        this.passwordTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        this.roleTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("role"));
        this.adresseTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("address"));
        this.dateDeNaissanceTableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<User, DatePicker>, ObservableValue<DatePicker>>() {
                    /**
                     * Create a DatePicker initialized from the row user's birth date for use as the table cell value.
                     *
                     * @param param cell data features containing the User for the current row
                     * @return an ObservableValue containing a DatePicker set to the user's birth date if present, otherwise a DatePicker with no value
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<DatePicker> call(
                            final TableColumn.CellDataFeatures<User, DatePicker> param) {
                        final DatePicker datePicker = new DatePicker();
                        if (null != param.getValue().getBirthDate()) {
                            datePicker.setValue(param.getValue().getBirthDate().toLocalDate());
                        }

                        return new SimpleObjectProperty<DatePicker>(datePicker);
                    }

                }
);
        this.emailTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        this.photoDeProfilTableColumn
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, HBox>, ObservableValue<HBox>>() {
                    /**
                     * Creates an HBox containing the user's profile ImageView and attaches a click handler that opens a file chooser to replace the image and update the controller's profile image view.
                     *
                     * @param param provides the TableColumn.CellDataFeatures for the row; its User's `photoDeProfil` URL is used to populate the ImageView and the selected replacement image (if any) is applied to the controller's `photoDeProfilImageView`
                     * @return an ObservableValue that wraps an HBox containing the user's profile ImageView
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<HBox> call(final TableColumn.CellDataFeatures<User, HBox> param) {
                        final HBox hBox = new HBox();
                        try {
                            final ImageView imageView = new ImageView(new Image(param.getValue().getPhotoDeProfil()));
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            hBox.getChildren().add(imageView);
                            hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                                /**
                                 * Opens a file chooser for PNG/JPG images and, if a file is selected, displays it in the controller.
                                 *
                                 * If the user selects a file the image is loaded and set on `imageView` and `photoDeProfilImageView`, and
                                 * the `hBox` children are replaced with the `imageView`.
                                 *
                                 * @param event the MouseEvent that triggered the file chooser
                                 */
                                @Override
                                /**
                                 * Performs handle operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void handle(final MouseEvent event) {
                                    try {
                                        final FileChooser fileChooser = new FileChooser();
                                        fileChooser.getExtensionFilters().addAll(
                                                new FileChooser.ExtensionFilter("PNG", "*.png"),
                                                new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                                        final File file = fileChooser.showOpenDialog(new Stage());
                                        if (null != file) {
                                            final Image image = new Image(file.toURI().toURL().toString());
                                            imageView.setImage(image);
                                            hBox.getChildren().clear();
                                            hBox.getChildren().add(imageView);
                                            photoDeProfilImageView.setImage(image);
                                        }

                                    } catch (final Exception e) {
                                        AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                                    }

                                }

                            }
);
                        } catch (final Exception e) {
                            AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }

                        return new SimpleObjectProperty<HBox>(hBox);
                    }

                }
);
        this.deleteTableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<User, Button>, ObservableValue<Button>>() {
                    /**
                     * Creates a table-cell value containing a "delete" button for the row's user.
                     *
                     * @param param table cell context used to obtain the row's User and its id
                     * @return an ObservableValue containing a Button that, when clicked, deletes the row's user and reloads the user table
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<Button> call(final TableColumn.CellDataFeatures<User, Button> param) {
                        final Button button = new Button("delete");
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            /**
                             * Delete the user associated with the triggering action and refresh the user table.
                             *
                             * @param event the ActionEvent that triggered the delete
                             */
                            @Override
                            /**
                             * Performs handle operation.
                             *
                             * @return the result of the operation
                             */
                            public void handle(final ActionEvent event) {
                                delete(param.getValue().getId());
                                readUserTable();
                            }

                        }
);
                        return new SimpleObjectProperty<Button>(button);
                    }

                }
);
    }


    /**
     * Configures table cell factories to enable in-place editing and validation for user fields.
     *
     * <p>Sets up editable text-cell behavior for first name, last name, phone number, password,
     * address, and email columns (each showing a TextField during edit) and a ComboBox cell for role.
     * Each editable text cell performs lowercase validation, displays an inline tooltip with validation
     * errors positioned near the editor, and prevents committing edits with the Enter key while errors
     * are present.</p>
     */
    private void setupCellFactories() {
        this.firstNameTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            /**
             * Creates and returns a editable TableCell for the given column that enforces a lowercase
             * validation rule and shows a tooltip with validation messages during in-place editing.
             *
             * @param param the table column for which the cell is being created
             * @return a configured TableCell instance for editing `User` string properties
             */
            /**
             * Begins editing the cell and attaches a lowercase validator and tooltip-driven feedback
             * to the cell's text field if not already attached.
             */
            /**
             * Reacts to changes in the cell's text field and shows or hides the validation tooltip.
             *
             * @param observable the observable text property being observed
             * @param oldValue   the previous text value
             * @param newValue   the current text value
             */
            /**
             * Create a TableCell for editing User string properties that enforces lowercase input
             * and prevents committing edits while validation errors are present.
             *
             * @param param the table column for which the cell factory is created
             * @return a TableCell configured with an inline text editor that validates input is lowercase and blocks commit (for example via Enter) when validation fails
             */
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<User, String> call(final TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;

                    /**
                     * Enters edit mode and attaches lowercase validation and inline feedback to the cell's text editor.
                     *
                     * <p>If the cell's graphic is a {@code TextField} and no validator is present, installs a validator
                     * that requires all-lowercase input, shows a red tooltip near the editor when validation fails,
                     * and prevents committing the edit with Enter while validation errors exist.</p>
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
                            this.validator.createCheck().dependsOn("firstName", textField.textProperty())
                                    .withMethod(c -> {
                                        final String userName = c.get("firstName");
                                        if (null != userName && !userName.toLowerCase().equals(userName)) {
                                            c.error("Please use only lowercase letters.");
                                        }

                                    }
).decorates(textField).immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                /**
                                 * Performs changed operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void changed(final ObservableValue<? extends String> observable,
                                        final String oldValue, final String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX() - 10,
                                                bounds.getMinY() + 30);
                                    }
 else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }

                                    }

                                }

                            }
);
                            textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                                @Override
                                /**
                                 * Performs handle operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void handle(final KeyEvent event) {
                                    if (KeyCode.ENTER == event.getCode()) {
                                        if (validator.containsErrors()) {
                                            event.consume();
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
);
        this.lastNameTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            /**
             * Creates a TableCell configured for inline editing of a user's last name with lowercase validation,
             * tooltip feedback, and Enter-key commit prevention while validation errors exist.
             *
             * @param param the table column for which the cell factory is being created
             * @return a configured TextFieldTableCell for editing the column's String values
             */
            /**
             * Begins in-place editing for the cell and attaches a lowercase validator, a tooltip for errors,
             * and event handlers that manage tooltip display and commit prevention.
             */
            /**
             * Invoked when the TextField's text changes; updates the tooltip text and visibility based on
             * the validator's current errors.
             *
             * @param observable the observed text property
             * @param oldValue the previous text value
             * @param newValue the new text value
             */
            /**
             * Creates a table cell that provides an inline TextField editor which enforces lowercase input
             * and prevents committing edits when validation errors are present.
             *
             * @param param the table column for which the cell is created
             * @return a configured TableCell that validates input (shows a red tooltip on error) and consumes Enter key presses while errors exist
             */
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<User, String> call(final TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;

                    /**
                     * Initializes inline editing for the cell: adds a lowercase-only validator to the cell's TextField, displays a red tooltip near the field when validation fails, and prevents committing the edit with Enter while validation errors exist.
                     *
                     * <p>The validator is created once per cell when editing starts and attaches listeners to update tooltip visibility and to consume Enter key events while the input is invalid.</p>
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
                            this.validator.createCheck().dependsOn("lastName", textField.textProperty())
                                    .withMethod(c -> {
                                        final String userName = c.get("lastName");
                                        if (null != userName && !userName.toLowerCase().equals(userName)) {
                                            c.error("Please use only lowercase letters.");
                                        }

                                    }
).decorates(textField).immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                /**
                                 * Performs changed operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void changed(final ObservableValue<? extends String> observable,
                                        final String oldValue, final String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX() - 10,
                                                bounds.getMinY() + 30);
                                    }
 else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }

                                    }

                                }

                            }
);
                            textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                                @Override
                                /**
                                 * Performs handle operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void handle(final KeyEvent event) {
                                    if (KeyCode.ENTER == event.getCode()) {
                                        if (validator.containsErrors()) {
                                            event.consume();
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
);
        this.numTelTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            /**
             * Creates and returns a table cell configured for inline text editing of a user's phone number.
             *
             * @param param the table column requesting the cell
             * @return a configured TextFieldTableCell for editing phone-number strings
             */
            /**
             * Begins in-place editing of the cell and attaches a validator and tooltip for the editor TextField.
             */
            /**
             * Validates editor text and shows or hides a tooltip describing validation errors when the text changes.
             *
             * @param observable the observable value being observed
             * @param oldValue   the previous text value
             * @param newValue   the new text value
             */
            /**
             * Creates an editable TableCell for the given column that enforces lowercase validation on its text editor,
             * shows a red tooltip for validation errors, and prevents committing edits when validation errors exist.
             *
             * @param param the TableColumn for which the cell is created
             * @return the configured TableCell instance
             */
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<User, String> call(final TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>() {
                    private Validator validator;

                    /**
                     * Prepare the table cell for inline editing by attaching a lowercase-only validator, displaying a red tooltip near the editor when validation fails, and preventing Enter commits while errors exist.
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
                            this.validator.createCheck().dependsOn("phoneNumber", textField.textProperty())
                                    .withMethod(c -> {
                                        final String userName = c.get("phoneNumber");
                                        if (null != userName && !userName.toLowerCase().equals(userName)) {
                                            c.error("Please use only lowercase letters.");
                                        }

                                    }
).decorates(textField).immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                /**
                                 * Performs changed operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void changed(final ObservableValue<? extends String> observable,
                                        final String oldValue, final String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX() - 10,
                                                bounds.getMinY() + 30);
                                    }
 else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }

                                    }

                                }

                            }
);
                            textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                                @Override
                                /**
                                 * Performs handle operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void handle(final KeyEvent event) {
                                    if (KeyCode.ENTER == event.getCode()) {
                                        if (validator.containsErrors()) {
                                            event.consume();
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
);
        this.passwordTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            /**
             * Creates a TableCell that provides an editable text field enforcing lowercase-only input with inline validation feedback.
             *
             * When the cell is edited, it displays a red tooltip with an error message if the input contains characters that are not lowercase
             * and prevents committing the edit via Enter while the validation error is present.
             *
             * @param param the TableColumn for which the cell is created
             * @return a configured TableCell<User, String> that validates edits to lowercase and shows inline error tooltip
             */
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<User, String> call(final TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;

                    /**
                     * Begins editing this table cell and attaches inline validation and UI feedback for the editor.
                     *
                     * If the cell's graphic is a TextField and no validator is present, a validator is added that requires
                     * the field's text to be all lowercase. When validation fails a red tooltip is shown near the editor,
                     * and pressing Enter to commit the edit is prevented while validation errors exist.
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
                            this.validator.createCheck().dependsOn("password", textField.textProperty())
                                    .withMethod(c -> {
                                        final String userName = c.get("password");
                                        if (null != userName && !userName.toLowerCase().equals(userName)) {
                                            c.error("Please use only lowercase letters.");
                                        }

                                    }
).decorates(textField).immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                /**
                                 * Performs changed operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void changed(final ObservableValue<? extends String> observable,
                                        final String oldValue, final String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX() - 10,
                                                bounds.getMinY() + 30);
                                    }
 else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }

                                    }

                                }

                            }
);
                            textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                                @Override
                                /**
                                 * Performs handle operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void handle(final KeyEvent event) {
                                    if (KeyCode.ENTER == event.getCode()) {
                                        if (validator.containsErrors()) {
                                            event.consume();
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
);
        this.roleTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), "admin",
                "client", "responsable de cinema"));
        this.adresseTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            /**
             * Creates a TableCell for editing a user's address that enforces lowercase input and shows a red tooltip when validation fails.
             *
             * @param param the table column for which the cell is created
             * @return a TableCell configured for editing address values; commits are prevented while validation errors exist
             */
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<User, String> call(final TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;

                    /**
                     * Prepares the cell's inline editor for editing an address and attaches lowercase validation.
                     *
                     * <p>When the cell's TextField editor is present, installs a Validator that requires the text
                     * to be all lowercase, displays a red tooltip with validation messages near the field when
                     * validation fails, and prevents committing the edit via Enter while validation errors exist.</p>
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
                            this.validator.createCheck().dependsOn("adresse", textField.textProperty())
                                    .withMethod(c -> {
                                        final String userName = c.get("adresse");
                                        if (null != userName && !userName.toLowerCase().equals(userName)) {
                                            c.error("Please use only lowercase letters.");
                                        }

                                    }
).decorates(textField).immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                /**
                                 * Performs changed operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void changed(final ObservableValue<? extends String> observable,
                                        final String oldValue, final String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX() - 10,
                                                bounds.getMinY() + 30);
                                    }
 else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }

                                    }

                                }

                            }
);
                            textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                                @Override
                                /**
                                 * Performs handle operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void handle(final KeyEvent event) {
                                    if (KeyCode.ENTER == event.getCode()) {
                                        if (validator.containsErrors()) {
                                            event.consume();
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
);
        this.emailTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            /**
             * Creates a TableCell that enables in-place string editing, enforces lowercase input, and shows an inline red tooltip for validation errors.
             *
             * @param param the TableColumn this cell factory is associated with
             * @return a TableCell configured for text editing with lowercase validation and inline error feedback
             */
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<User, String> call(final TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;

                    /**
                     * Prepares the cell for inline editing by initializing a lowercase-only validator for the
                     * email editor, showing a red tooltip near the editor when validation fails, and preventing
                     * commit via Enter while there are validation errors.
                     *
                     * <p>The validator is attached to the TextField used as the cell's graphic; it displays
                     * real-time validation feedback and consumes Enter key events if the current input is invalid.</p>
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
                            this.validator.createCheck().dependsOn("email", textField.textProperty()).withMethod(c -> {
                                final String userName = c.get("email");
                                if (null != userName && !userName.toLowerCase().equals(userName)) {
                                    c.error("Please use only lowercase letters.");
                                }

                            }
).decorates(textField).immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                /**
                                 * Performs changed operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void changed(final ObservableValue<? extends String> observable,
                                        final String oldValue, final String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX() - 10,
                                                bounds.getMinY() + 30);
                                    }
 else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }

                                    }

                                }

                            }
);
                            textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                                @Override
                                /**
                                 * Performs handle operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void handle(final KeyEvent event) {
                                    if (KeyCode.ENTER == event.getCode()) {
                                        if (validator.containsErrors()) {
                                            event.consume();
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
);
    }


    /**
     * Configure table column edit handlers to apply in-place edits to User objects and persist those changes.
     *
     * <p>Registers commit or start edit handlers for the table columns (first name, last name, phone number,
     * password, role, address, birth date, and email). Each handler updates the corresponding property on the
     * edited User instance and calls update(user) to persist the change. The birth date handler reads the
     * DatePicker value and converts it to an SQL Date before updating.</p>
     *
     * <p>Exceptions thrown during handler execution are caught and logged; they do not propagate from this method.</p>
     */
    private void setupCellOnEditCommit() {
        this.firstNameTableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            /**
             * Applies the edited first name from the table cell to the corresponding User and persists the change.
             *
             * @param event the cell edit event containing the affected row and the new first-name value
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<User, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow())
                            .setFirstName(event.getNewValue());
                    AdminDashboardController.this
                            .update(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }

            }

        }
);
        this.lastNameTableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            /**
             * Applies the edited last name from the given cell edit event to the corresponding User and persists the change.
             *
             * @param event the table cell edit event containing the new last name and the affected row
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<User, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow())
                            .setLastName(event.getNewValue());
                    AdminDashboardController.this
                            .update(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }

            }

        }
);
        this.numTelTableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            /**
             * Update the edited user's phone number and persist the change.
             *
             * @param event the cell edit event containing the new phone number and the target row
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<User, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow())
                            .setPhoneNumber(event.getNewValue());
                    AdminDashboardController.this
                            .update(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }

            }

        }
);
        this.passwordTableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            /**
             * Handles a password cell edit by applying the new value to the corresponding user and persisting the change.
             *
             * @param event the cell edit event containing the new password value and the table row context
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<User, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow())
                            .setPassword(event.getNewValue());
                    AdminDashboardController.this
                            .update(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }

            }

        }
);
        this.roleTableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            /**
             * Applies the edited role value to the user in the edited table row and persists the update.
             *
             * @param event the table cell edit event containing the new role value and the edited row information
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<User, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow()).setRole(event.getNewValue());
                    AdminDashboardController.this
                            .update(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }

            }

        }
);
        this.adresseTableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            /**
             * Commits an edited address cell to the corresponding User and persists the update.
             *
             * @param event the cell edit event carrying the new address value and target row
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<User, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow())
                            .setAddress(event.getNewValue());
                    AdminDashboardController.this
                            .update(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }

            }

        }
);
        this.dateDeNaissanceTableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<User, DatePicker>>() {
            /**
             * Handles a committed edit from a DatePicker cell by updating the corresponding user's birth date and persisting the change.
             *
             * @param event the cell edit event containing the new DatePicker value and the row for the edited user
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<User, DatePicker> event) {
                event.getTableView().getItems().get(event.getTablePosition().getRow())
                        .setBirthDate(Date.valueOf(event.getNewValue().getValue()));
                AdminDashboardController.this
                        .update(event.getTableView().getItems().get(event.getTablePosition().getRow()));
            }

        }
);
        this.emailTableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            /**
             * Updates the edited user's email from the table cell edit and persists the change.
             *
             * @param event the cell edit event containing the new email value and the affected row
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<User, String> event) {
                event.getTableView().getItems().get(event.getTablePosition().getRow()).setEmail(event.getNewValue());
                AdminDashboardController.this
                        .update(event.getTableView().getItems().get(event.getTablePosition().getRow()));
            }

        }
);
    }


    /**
     * Clears all form inputs: empties text fields, clears the role selection, sets the date picker to a minimal date, and removes the profile image.
     */
    @FXML
    void clearTextFields(final ActionEvent event) {
        this.idTextField.setText("");
        this.firstNameTextField.setText("");
        this.lastNameTextField.setText("");
        this.phoneNumberTextField.setText("");
        this.passwordTextField.setText("");
        this.roleComboBox.setValue("");
        this.adresseTextField.setText("");
        this.dateDeNaissanceDatePicker.setValue(new Date(0, 0, 0).toLocalDate());
        this.emailTextField.setText("");
        this.photoDeProfilImageView.setImage(null);
    }


    /**
     * Remove the user with the specified identifier from persistent storage.
     *
     * @param id the identifier of the user to remove
     */
    void delete(final Long id) {
        // String role = roleComboBox.getValue();
        // User user = null;
        // if (role.equals("admin")) {
        // user = new Admin(Integer.parseInt(idTextField.getText()),
        // firstNameTextField.getText(), lastNameTextField.getText(),
        // Integer.parseInt(phoneNumberTextField.getText()),
        // passwordTextField.getText(), roleComboBox.getValue(),
        // emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()),
        // emailTextField.getText());
        // } else if (role.equals("responsable de cinema")) {
        // user = new CinemaManager(Integer.parseInt(idTextField.getText()),
        // firstNameTextField.getText(), lastNameTextField.getText(),
        // Integer.parseInt(phoneNumberTextField.getText()),
        // passwordTextField.getText(), roleComboBox.getValue(),
        // emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()),
        // emailTextField.getText());
        // } else if (role.equals("client")) {
        // user = new Client(Integer.parseInt(idTextField.getText()),
        // firstNameTextField.getText(), lastNameTextField.getText(),
        // Integer.parseInt(phoneNumberTextField.getText()),
        // passwordTextField.getText(), roleComboBox.getValue(),
        // emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()),
        // emailTextField.getText());
        // } else {
        // Alert alert = new Alert(Alert.AlertType.ERROR, "the given role is not
        // available", ButtonType.CLOSE);
        // alert.show();
        // return;
        // }

        final UserService userService = new UserService();
        userService.delete(new User(id, "", "", "", "", "", "", new Date(0, 0, 0), "", null) {
        }
);
    }


    /**
     * Opens a file chooser to select a PNG or JPG image, uploads the selected file to Cloudinary,
     * sets the uploaded image into the profile ImageView, and stores the resulting URL.
     *
     * @param event the ActionEvent triggered by the import image control
     */
    @FXML
    void importImage(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        fileChooser.setTitle("Sélectionner une image");
        final File selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            try {
                // Use the CloudinaryStorage service to upload the image
                CloudinaryStorage cloudinaryStorage = CloudinaryStorage.getInstance();
                cloudinaryImageUrl = cloudinaryStorage.uploadImage(selectedFile);

                // Display the image in the ImageView
                final Image selectedImage = new Image(cloudinaryImageUrl);
                this.photoDeProfilImageView.setImage(selectedImage);

                LOGGER.info("Image uploaded to Cloudinary: " + cloudinaryImageUrl);
            } catch (final IOException e) {
                AdminDashboardController.LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
            }

        }

    }


    /**
     * Persist updates for the specified user to the data store.
     *
     * @param user the User whose updated state should be saved
     */
    void update(final User user) {
        try {
            final UserService userService = new UserService();
            userService.update(user);
        } catch (final Exception e) {
            AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Generate a PDF report containing the application's users.
     *
     * <p>Triggers creation of the user PDF report and persists or exports it according
     * to application configuration.</p>
     */
    @FXML
    void generatePDF() {
        final UserService userService = new UserService();
        userService.generateUserPDF();
    }


    /**
     * Sign out the current user and display the sign-up screen.
     *
     * @param event the triggering ActionEvent
     * @throws IOException if loading the sign-up FXML fails
     */
    @FXML
    /**
     * Performs signOut operation.
     * Signs out the current user and redirects to the sign-up page.
     *
     * @param event the action event triggered by the sign-out action
     * @throws IOException if there's an error loading the sign-up FXML file
     */
    public void signOut(final ActionEvent event) throws IOException {
        final Stage stage = (Stage) this.emailTextField.getScene().getWindow();
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/SignUp.fxml"));
        final Parent root = loader.load();
        stage.setScene(new Scene(root));
    }

}