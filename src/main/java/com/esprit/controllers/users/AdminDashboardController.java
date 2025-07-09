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
     * /** Adds a listener to a `TextField` that validates the inputted string using
     * a provided `Predicate<String>`. If the string is invalid, a tooltip with an
     * error message is displayed near the text field.
     *
     * @param textField
     *                            TextField component whose text value will be
     *                            validated and whose
     *                            tooltip will be updated accordingly.
     * @param validationPredicate
     *                            function that determines whether or not a given
     *                            string is valid,
     *                            and it is used to determine whether an error
     *                            message should be
     *                            displayed when the user types something into the
     *                            text field.
     * @param errorMessage
     *                            message to be displayed as a tooltip when the user
     *                            enters an
     *                            invalid value in the text field.
     */
    private void addValidationListener(final TextField textField, final Predicate<String> validationPredicate,
            final String errorMessage) {
        final Tooltip tooltip = new Tooltip();
        textField.textProperty().addListener(new ChangeListener<String>() {
            /**
             * Detects changes to the `textField`'s value and displays an error message in a
             * tooltip if the new value does not meet a validation predicate or is empty.
             *
             * @param observable
             *                   ObservableValue object that emits changes to its value,
             *                   allowing
             *                   the function to detect and respond to those changes.
             *
             * @param oldValue
             *                   previous value of the observable variable before the change
             *                   occurred, which is used to validate the new value and
             *                   determine if
             *                   an error message should be displayed.
             *
             * @param newValue
             *                   updated value of the `TextField`, which is used to validate
             *                   and
             *                   display an error message if necessary.
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
                    } else if (newValue.isEmpty()) {
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
                } else {
                    if (null != textField.getTooltip()) {
                        textField.getTooltip().hide();
                    }
                }
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && null != textField.getTooltip()) {
                textField.getTooltip().hide();
            }
        });
    }

    /**
     * Reads data from a User Service and populates the user table view with the
     * retrieved data.
     */
    @FXML
    void readUserTable() {
        try {
            final UserService userService = new UserService();
            final List<User> userList = userService.read();
            this.userTableView.setItems(FXCollections.observableArrayList(userList));
        } catch (final Exception e) {
            AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * /** Allows users to create a new admin account by providing their first name,
     * last name, phone number, password, email, and role. It then validates the
     * input and creates a new admin user using the provided information.
     *
     * @param event
     *              `addAdmin` action, triggering the execution of the code within
     *              the
     *              function.
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
            } else {
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
     * Sets up cell value factories for each column of a table displaying
     * information about users. These factories provide the content for each cell,
     * such as a user's first or last name, phone number, password, role, address,
     * date of birth, email, and photo profile picture.
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
                     * Creates a new `DatePicker` object and sets its value to the local date of the
                     * user's birth date if it is not null.
                     *
                     * @param param
                     *              value of a table cell, which contains the birth date of a user.
                     *
                     * @returns a `SimpleObjectProperty` of a `DatePicker` object initialized with
                     *          the birth date value from the input `User` object, if available.
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
                });
        this.emailTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        this.photoDeProfilTableColumn
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, HBox>, ObservableValue<HBox>>() {
                    /**
                     * Takes a `TableColumn.CellDataFeatures` parameter and generates an `HBox`
                     * widget with an image view containing the user's profile picture. The image
                     * view is customized to fit a specified size, and an event handler is added to
                     * handle mouse clicks on the image view, opening a file chooser to allow the
                     * user to select a new profile picture.
                     *
                     * @param param
                     *              value of the `User` object being processed, which provides the
                     *              `photo_de_profil` property that is used to display the photo of
                     *              the profile in the `ImageView`.
                     *
                     * @returns an `ObservableValue` of type `HBox`, which contains a single
                     *          `ImageView` component that displays the user's profile picture.
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
                                 * Displays a file open dialog box, selects an image file using FileChooser,
                                 * sets the selected image as the viewable image in the stage, and clears any
                                 * previous images in the container.
                                 *
                                 * @param event
                                 *              mouse event that triggered the function's execution, providing
                                 *              no
                                 *              further context beyond that.
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
                            });
                        } catch (final Exception e) {
                            AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }
                        return new SimpleObjectProperty<HBox>(hBox);
                    }
                });
        this.deleteTableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<User, Button>, ObservableValue<Button>>() {
                    /**
                     * Creates a new `Button` element with the text "delete". The button's
                     * `onAction` event handler is set to delete the corresponding user's ID when
                     * clicked, and then reads the entire user table.
                     *
                     * @param param
                     *              `CellDataFeatures` of a table column, providing the current cell
                     *              value and related data.
                     *
                     * @returns a `SimpleObjectProperty` of a `Button` object with an action to
                     *          delete the corresponding user ID.
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
                             * Deletes a record with the specified ID from a data storage and subsequently
                             * reloads the user table.
                             *
                             * @param event
                             *              deleting event triggered by the user's action, and it is passed
                             *              to
                             *              the `handle()` method as an argument to enable the appropriate
                             *              actions to be taken.
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
                        });
                        return new SimpleObjectProperty<Button>(button);
                    }
                });
    }

    /**
     * Sets up cell factories for the `adresse` and `email` columns of the user
     * table, which will display a text field for each column. The `cellFactory`
     * method is used to create TableCells that can display text data in a formatted
     * way.
     */
    private void setupCellFactories() {
        this.firstNameTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<User, String> call(final TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;

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
                                    }).decorates(textField).immediate();
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
                                    } else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
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
                            });
                        }
                    }
                };
            }
        });
        this.lastNameTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<User, String> call(final TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;

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
                                    }).decorates(textField).immediate();
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
                                    } else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
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
                            });
                        }
                    }
                };
            }
        });
        this.numTelTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<User, String> call(final TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>() {
                    private Validator validator;

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
                                    }).decorates(textField).immediate();
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
                                    } else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
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
                            });
                        }
                    }
                };
            }
        });
        this.passwordTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<User, String> call(final TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;

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
                                    }).decorates(textField).immediate();
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
                                    } else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
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
                            });
                        }
                    }
                };
            }
        });
        this.roleTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), "admin",
                "client", "responsable de cinema"));
        this.adresseTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<User, String> call(final TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;

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
                                    }).decorates(textField).immediate();
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
                                    } else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
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
                            });
                        }
                    }
                };
            }
        });
        this.emailTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<User, String> call(final TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;

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
                            }).decorates(textField).immediate();
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
                                    } else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
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
                            });
                        }
                    }
                };
            }
        });
    }

    private void setupCellOnEditCommit() {
        this.firstNameTableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
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
        });
        this.lastNameTableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
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
        });
        this.numTelTableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
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
        });
        this.passwordTableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
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
        });
        this.roleTableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
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
        });
        this.adresseTableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
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
        });
        this.dateDeNaissanceTableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<User, DatePicker>>() {
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
        });
        this.emailTableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
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
        });
    }

    /**
     * @param event
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
     * @param id
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
        });
    }

    /**
     * @param event
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
     * @param user
     */
    void update(final User user) {
        try {
            final UserService userService = new UserService();
            userService.update(user);
        } catch (final Exception e) {
            AdminDashboardController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    void generatePDF() {
        final UserService userService = new UserService();
        userService.generateUserPDF();
    }

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
