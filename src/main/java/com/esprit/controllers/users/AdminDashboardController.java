package com.esprit.controllers.users;
import com.esprit.models.users.Admin;
import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
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
import javafx.scene.control.TableColumn.CellEditEvent;
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
import javafx.util.converter.IntegerStringConverter;
import net.synedra.validatorfx.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
public class AdminDashboardController {
    TableColumn<User, String> roleTableColumn;
    TableColumn<User, HBox> photoDeProfilTableColumn;
    TableColumn<User, String> lastNameTableColumn;
    TableColumn<User, String> passwordTableColumn;
    TableColumn<User, Integer> numTelTableColumn;
    TableColumn<User, String> firstNameTableColumn;
    TableColumn<User, DatePicker> dateDeNaissanceTableColumn;
    TableColumn<User, String> adresseTableColumn;
    TableColumn<User, String> emailTableColumn;
    TableColumn<User, Button> deleteTableColumn;
    Validator formValidator;
    Validator tableValidator;
    Tooltip formValidatorTooltip;
    Tooltip tableValidatorTooltip;
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
     * Sets up user interface components for a table displaying information about users,
     * including text fields, combo boxes, and validation listeners to ensure data validity.
     */
    @FXML
    void initialize() {
        try {
            roleTableColumn = new TableColumn<>("role");
            photoDeProfilTableColumn = new TableColumn<>("photo de profil");
            lastNameTableColumn = new TableColumn<>("lastName");
            passwordTableColumn = new TableColumn<>("password");
            numTelTableColumn = new TableColumn<>("numero de telephone");
            firstNameTableColumn = new TableColumn<>("firstName");
            dateDeNaissanceTableColumn = new TableColumn<>("date de naissance");
            adresseTableColumn = new TableColumn<>("adresse");
            emailTableColumn = new TableColumn<>("email");
            deleteTableColumn = new TableColumn<>("delete");
            tableValidator = new Validator();
            userTableView.setEditable(true);
            List<TableColumn<User, ?>> columns = Arrays.asList(firstNameTableColumn, lastNameTableColumn,
                    emailTableColumn,
                    passwordTableColumn, numTelTableColumn, adresseTableColumn, dateDeNaissanceTableColumn,
                    roleTableColumn, photoDeProfilTableColumn, deleteTableColumn);
            userTableView.getColumns().addAll(columns);
            setupCellValueFactories();
            setupCellFactories();
            setupCellOnEditCommit();
            Tooltip tooltip = new Tooltip();
            addValidationListener(firstNameTextField, newValue -> newValue.toLowerCase().equals(newValue),
                    "Please use only lowercase letters.");
            addValidationListener(lastNameTextField, newValue -> newValue.toLowerCase().equals(newValue),
                    "Please use only lowercase letters.");
            addValidationListener(adresseTextField, newValue -> newValue.toLowerCase().equals(newValue),
                    "Please use only lowercase letters.");
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            addValidationListener(emailTextField, newValue -> newValue.matches(emailRegex), "Invalid email format.");
            addValidationListener(passwordTextField, newValue -> newValue.toLowerCase().equals(newValue),
                    "Please use only lowercase letters.");
            String numberRegex = "\\d*";
            addValidationListener(phoneNumberTextField, newValue -> newValue.matches(numberRegex),
                    "Please use only numbers.");
            List<String> roleList = List.of("admin");
            for (String role : roleList) {
                roleComboBox.getItems().add(role);
            }
            readUserTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** 
    /**
     * Adds a listener to a `TextField` that validates the inputted string using a provided
     * `Predicate<String>`. If the string is invalid, a tooltip with an error message is
     * displayed near the text field.
     * 
     * @param textField TextField component whose text value will be validated and whose
     * tooltip will be updated accordingly.
     * 
     * @param validationPredicate function that determines whether or not a given string
     * is valid, and it is used to determine whether an error message should be displayed
     * when the user types something into the text field.
     * 
     * @param errorMessage message to be displayed as a tooltip when the user enters an
     * invalid value in the text field.
     */
    private void addValidationListener(TextField textField, Predicate<String> validationPredicate,
                                       String errorMessage) {
        Tooltip tooltip = new Tooltip();
        textField.textProperty().addListener(new ChangeListener<String>() {
            /**
             * Detects changes to the `textField`'s value and displays an error message in a
             * tooltip if the new value does not meet a validation predicate or is empty.
             * 
             * @param observable ObservableValue object that emits changes to its value, allowing
             * the function to detect and respond to those changes.
             * 
             * @param oldValue previous value of the observable variable before the change occurred,
             * which is used to validate the new value and determine if an error message should
             * be displayed.
             * 
             * @param newValue updated value of the `TextField`, which is used to validate and
             * display an error message if necessary.
             */
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String error = null;
                if (newValue != null) {
                    if (!validationPredicate.test(newValue)) {
                        error = errorMessage;
                    } else if (newValue.isEmpty()) {
                        error = "The string is empty.";
                    }
                }
                Window window = textField.getScene().getWindow();
                Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                if (error != null) {
                    tooltip.setText(error);
                    tooltip.setStyle("-fx-background-color: #f00;");
                    textField.setTooltip(tooltip);
                    textField.getTooltip().show(window, bounds.getMinX() - 10, bounds.getMinY() + 30);
                } else {
                    if (textField.getTooltip() != null) {
                        textField.getTooltip().hide();
                    }
                }
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && textField.getTooltip() != null) {
                textField.getTooltip().hide();
            }
        });
    }
    /**
     * Reads data from a User Service and populates the user table view with the retrieved
     * data.
     */
    @FXML
    void readUserTable() {
        try {
            UserService userService = new UserService();
            List<User> userList = userService.read();
            userTableView.setItems(FXCollections.observableArrayList(userList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** 
    /**
     * Allows users to create a new admin account by providing their first name, last
     * name, phone number, password, email, and role. It then validates the input and
     * creates a new admin user using the provided information.
     * 
     * @param event `addAdmin` action, triggering the execution of the code within the function.
     */
    @FXML
    void addAdmin(ActionEvent event) {
        try {
            String role = roleComboBox.getValue();
            User user = null;
            URI uri = null;
            if (role.equals("admin")) {
                String fullPath = photoDeProfilImageView.getImage().getUrl();
                String requiredPath = fullPath.substring(fullPath.indexOf("/img/users/"));
                uri = new URI(requiredPath);
                String firstName = firstNameTextField.getText();
                String lastName = lastNameTextField.getText();
                String phoneNumber = phoneNumberTextField.getText();
                String password = passwordTextField.getText();
                String email = emailTextField.getText();
                LocalDate dateDeNaissance = dateDeNaissanceDatePicker.getValue();
                // Perform input validation
                if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() ||
                        role.isEmpty() || email.isEmpty() || dateDeNaissance == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all the required fields", ButtonType.CLOSE);
                    alert.show();
                    return;
                }
                // Validate name format
                if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid name format", ButtonType.CLOSE);
                    alert.show();
                    return;
                }
                // Validate password length
                if (password.length() < 8) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Password must be at least 8 characters long", ButtonType.CLOSE);
                    alert.show();
                    return;
                }
                // Validate phone number format
                if (!phoneNumber.matches("\\d{10}")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid phone number format", ButtonType.CLOSE);
                    alert.show();
                    return;
                }
                // Validate email format
                if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid email format", ButtonType.CLOSE);
                    alert.show();
                    return;
                }
                user = new Admin(firstName, lastName, Integer.parseInt(phoneNumber), password, role, email,
                        Date.valueOf(dateDeNaissance), email, uri.getPath());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "the given role is not available", ButtonType.CLOSE);
                alert.show();
                return;
            }
            UserService userService = new UserService();
            userService.create(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Sets up cell value factories for each column of a table displaying information
     * about users. These factories provide the content for each cell, such as a user's
     * first or last name, phone number, password, role, address, date of birth, email,
     * and photo profile picture.
     */
    private void setupCellValueFactories() {
        firstNameTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        numTelTableColumn.setCellValueFactory(new PropertyValueFactory<User, Integer>("phoneNumber"));
        passwordTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        roleTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("role"));
        adresseTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("address"));
        dateDeNaissanceTableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<User, DatePicker>, ObservableValue<DatePicker>>() {
                    /**
                     * Creates a new `DatePicker` object and sets its value to the local date of the
                     * user's birth date if it is not null.
                     * 
                     * @param param value of a table cell, which contains the birth date of a user.
                     * 
                     * @returns a `SimpleObjectProperty` of a `DatePicker` object initialized with the
                     * birth date value from the input `User` object, if available.
                     */
                    @Override
                    public ObservableValue<DatePicker> call(TableColumn.CellDataFeatures<User, DatePicker> param) {
                        DatePicker datePicker = new DatePicker();
                        if (param.getValue().getBirthDate() != null) {
                            datePicker.setValue(param.getValue().getBirthDate().toLocalDate());
                        }
                        return new SimpleObjectProperty<DatePicker>(datePicker);
                    }
                });
        emailTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        photoDeProfilTableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<User, HBox>, ObservableValue<HBox>>() {
                    /**
                     * Takes a `TableColumn.CellDataFeatures` parameter and generates an `HBox` widget
                     * with an image view containing the user's profile picture. The image view is
                     * customized to fit a specified size, and an event handler is added to handle mouse
                     * clicks on the image view, opening a file chooser to allow the user to select a new
                     * profile picture.
                     * 
                     * @param param value of the `User` object being processed, which provides the
                     * `photo_de_profil` property that is used to display the photo of the profile in the
                     * `ImageView`.
                     * 
                     * @returns an `ObservableValue` of type `HBox`, which contains a single `ImageView`
                     * component that displays the user's profile picture.
                     */
                    @Override
                    public ObservableValue<HBox> call(TableColumn.CellDataFeatures<User, HBox> param) {
                        HBox hBox = new HBox();
                        try {
                            ImageView imageView = new ImageView(new Image(param.getValue().getPhoto_de_profil()));
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            hBox.getChildren().add(imageView);
                            hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                                /**
                                 * Displays a file open dialog box, selects an image file using FileChooser, sets the
                                 * selected image as the viewable image in the stage, and clears any previous images
                                 * in the container.
                                 * 
                                 * @param event mouse event that triggered the function's execution, providing no
                                 * further context beyond that.
                                 */
                                @Override
                                public void handle(MouseEvent event) {
                                    try {
                                        FileChooser fileChooser = new FileChooser();
                                        fileChooser.getExtensionFilters().addAll(
                                                new FileChooser.ExtensionFilter("PNG", "*.png"),
                                                new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                                        File file = fileChooser.showOpenDialog(new Stage());
                                        if (file != null) {
                                            Image image = new Image(file.toURI().toURL().toString());
                                            imageView.setImage(image);
                                            hBox.getChildren().clear();
                                            hBox.getChildren().add(imageView);
                                            photoDeProfilImageView.setImage(image);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return new SimpleObjectProperty<HBox>(hBox);
                    }
                });
        deleteTableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<User, Button>, ObservableValue<Button>>() {
                    /**
                     * Creates a new `Button` element with the text "delete". The button's `onAction`
                     * event handler is set to delete the corresponding user's ID when clicked, and then
                     * reads the entire user table.
                     * 
                     * @param param `CellDataFeatures` of a table column, providing the current cell value
                     * and related data.
                     * 
                     * @returns a `SimpleObjectProperty` of a `Button` object with an action to delete
                     * the corresponding user ID.
                     */
                    @Override
                    public ObservableValue<Button> call(TableColumn.CellDataFeatures<User, Button> param) {
                        Button button = new Button("delete");
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            /**
                             * Deletes a record with the specified ID from a data storage and subsequently reloads
                             * the user table.
                             * 
                             * @param event deleting event triggered by the user's action, and it is passed to
                             * the `handle()` method as an argument to enable the appropriate actions to be taken.
                             */
                            @Override
                            public void handle(ActionEvent event) {
                                delete(param.getValue().getId());
                                readUserTable();
                            }
                        });
                        return new SimpleObjectProperty<Button>(button);
                    }
                });
    }
    /**
     * Sets up cell factories for the `adresse` and `email` columns of the user table,
     * which will display a text field for each column. The `cellFactory` method is used
     * to create TableCells that can display text data in a formatted way.
     */
    private void setupCellFactories() {
        firstNameTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            public TableCell<User, String> call(TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;
                    @Override
                    public void startEdit() {
                        super.startEdit();
                        TextField textField = (TextField) getGraphic();
                        if (textField != null && validator == null) {
                            validator = new Validator();
                            validator.createCheck()
                                    .dependsOn("firstName", textField.textProperty())
                                    .withMethod(c -> {
                                        String userName = c.get("firstName");
                                        if (userName != null && !userName.toLowerCase().equals(userName)) {
                                            c.error("Please use only lowercase letters.");
                                        }
                                    })
                                    .decorates(textField)
                                    .immediate();
                            Window window = this.getScene().getWindow();
                            Tooltip tooltip = new Tooltip();
                            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                    String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX() - 10,
                                                bounds.getMinY() + 30);
                                    } else {
                                        if (textField.getTooltip() != null) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
                            textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                                @Override
                                public void handle(KeyEvent event) {
                                    if (event.getCode().equals(KeyCode.ENTER)) {
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
        lastNameTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            public TableCell<User, String> call(TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;
                    @Override
                    public void startEdit() {
                        super.startEdit();
                        TextField textField = (TextField) getGraphic();
                        if (textField != null && validator == null) {
                            validator = new Validator();
                            validator.createCheck()
                                    .dependsOn("lastName", textField.textProperty())
                                    .withMethod(c -> {
                                        String userName = c.get("lastName");
                                        if (userName != null && !userName.toLowerCase().equals(userName)) {
                                            c.error("Please use only lowercase letters.");
                                        }
                                    })
                                    .decorates(textField)
                                    .immediate();
                            Window window = this.getScene().getWindow();
                            Tooltip tooltip = new Tooltip();
                            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                    String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX() - 10,
                                                bounds.getMinY() + 30);
                                    } else {
                                        if (textField.getTooltip() != null) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
                            textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                                @Override
                                public void handle(KeyEvent event) {
                                    if (event.getCode().equals(KeyCode.ENTER)) {
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
        numTelTableColumn.setCellFactory(new Callback<TableColumn<User, Integer>, TableCell<User, Integer>>() {
            @Override
            public TableCell<User, Integer> call(TableColumn<User, Integer> param) {
                return new TextFieldTableCell<User, Integer>(new IntegerStringConverter()) {
                    private Validator validator;
                    @Override
                    public void startEdit() {
                        super.startEdit();
                        TextField textField = (TextField) getGraphic();
                        if (textField != null && validator == null) {
                            validator = new Validator();
                            validator.createCheck()
                                    .dependsOn("phoneNumber", textField.textProperty())
                                    .withMethod(c -> {
                                        String userName = c.get("phoneNumber");
                                        if (userName != null && !userName.toLowerCase().equals(userName)) {
                                            c.error("Please use only lowercase letters.");
                                        }
                                    })
                                    .decorates(textField)
                                    .immediate();
                            Window window = this.getScene().getWindow();
                            Tooltip tooltip = new Tooltip();
                            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                    String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX() - 10,
                                                bounds.getMinY() + 30);
                                    } else {
                                        if (textField.getTooltip() != null) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
                            textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                                @Override
                                public void handle(KeyEvent event) {
                                    if (event.getCode().equals(KeyCode.ENTER)) {
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
        passwordTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            public TableCell<User, String> call(TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;
                    @Override
                    public void startEdit() {
                        super.startEdit();
                        TextField textField = (TextField) getGraphic();
                        if (textField != null && validator == null) {
                            validator = new Validator();
                            validator.createCheck()
                                    .dependsOn("password", textField.textProperty())
                                    .withMethod(c -> {
                                        String userName = c.get("password");
                                        if (userName != null && !userName.toLowerCase().equals(userName)) {
                                            c.error("Please use only lowercase letters.");
                                        }
                                    })
                                    .decorates(textField)
                                    .immediate();
                            Window window = this.getScene().getWindow();
                            Tooltip tooltip = new Tooltip();
                            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                    String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX() - 10,
                                                bounds.getMinY() + 30);
                                    } else {
                                        if (textField.getTooltip() != null) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
                            textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                                @Override
                                public void handle(KeyEvent event) {
                                    if (event.getCode().equals(KeyCode.ENTER)) {
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
        roleTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), "admin", "client",
                "responsable de cinema"));
        adresseTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            public TableCell<User, String> call(TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;
                    @Override
                    public void startEdit() {
                        super.startEdit();
                        TextField textField = (TextField) getGraphic();
                        if (textField != null && validator == null) {
                            validator = new Validator();
                            validator.createCheck()
                                    .dependsOn("adresse", textField.textProperty())
                                    .withMethod(c -> {
                                        String userName = c.get("adresse");
                                        if (userName != null && !userName.toLowerCase().equals(userName)) {
                                            c.error("Please use only lowercase letters.");
                                        }
                                    })
                                    .decorates(textField)
                                    .immediate();
                            Window window = this.getScene().getWindow();
                            Tooltip tooltip = new Tooltip();
                            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                    String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX() - 10,
                                                bounds.getMinY() + 30);
                                    } else {
                                        if (textField.getTooltip() != null) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
                            textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                                @Override
                                public void handle(KeyEvent event) {
                                    if (event.getCode().equals(KeyCode.ENTER)) {
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
        emailTableColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            public TableCell<User, String> call(TableColumn<User, String> param) {
                return new TextFieldTableCell<User, String>(new DefaultStringConverter()) {
                    private Validator validator;
                    @Override
                    public void startEdit() {
                        super.startEdit();
                        TextField textField = (TextField) getGraphic();
                        if (textField != null && validator == null) {
                            validator = new Validator();
                            validator.createCheck()
                                    .dependsOn("email", textField.textProperty())
                                    .withMethod(c -> {
                                        String userName = c.get("email");
                                        if (userName != null && !userName.toLowerCase().equals(userName)) {
                                            c.error("Please use only lowercase letters.");
                                        }
                                    })
                                    .decorates(textField)
                                    .immediate();
                            Window window = this.getScene().getWindow();
                            Tooltip tooltip = new Tooltip();
                            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                    String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX() - 10,
                                                bounds.getMinY() + 30);
                                    } else {
                                        if (textField.getTooltip() != null) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
                            textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                                @Override
                                public void handle(KeyEvent event) {
                                    if (event.getCode().equals(KeyCode.ENTER)) {
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
        firstNameTableColumn.setOnEditCommit(new EventHandler<CellEditEvent<User, String>>() {
            @Override
            public void handle(CellEditEvent<User, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setFirstName(event.getNewValue());
                    update(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        lastNameTableColumn.setOnEditCommit(new EventHandler<CellEditEvent<User, String>>() {
            @Override
            public void handle(CellEditEvent<User, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setLastName(event.getNewValue());
                    update(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        numTelTableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, Integer>>() {
            @Override
            public void handle(CellEditEvent<User, Integer> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setPhoneNumber(event.getNewValue());
                    update(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        passwordTableColumn.setOnEditStart(new EventHandler<CellEditEvent<User, String>>() {
            @Override
            public void handle(CellEditEvent<User, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setPassword(event.getNewValue());
                    update(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        roleTableColumn.setOnEditStart(new EventHandler<CellEditEvent<User, String>>() {
            @Override
            public void handle(CellEditEvent<User, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setRole(event.getNewValue());
                    update(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        adresseTableColumn.setOnEditStart(new EventHandler<CellEditEvent<User, String>>() {
            @Override
            public void handle(CellEditEvent<User, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setAddress(event.getNewValue());
                    update(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dateDeNaissanceTableColumn.setOnEditStart(new EventHandler<CellEditEvent<User, DatePicker>>() {
            @Override
            public void handle(CellEditEvent<User, DatePicker> event) {
                event.getTableView().getItems().get(
                                event.getTablePosition().getRow())
                        .setBirthDate(Date.valueOf(event.getNewValue().getValue()));
                update(event.getTableView().getItems().get(
                        event.getTablePosition().getRow()));
            }
        });
        emailTableColumn.setOnEditStart(new EventHandler<CellEditEvent<User, String>>() {
            @Override
            public void handle(CellEditEvent<User, String> event) {
                event.getTableView().getItems().get(
                        event.getTablePosition().getRow()).setEmail(event.getNewValue());
                update(event.getTableView().getItems().get(
                        event.getTablePosition().getRow()));
            }
        });
    }
    @FXML
    void clearTextFields(ActionEvent event) {
        idTextField.setText("");
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        phoneNumberTextField.setText("");
        passwordTextField.setText("");
        roleComboBox.setValue("");
        adresseTextField.setText("");
        dateDeNaissanceDatePicker.setValue(new Date(0, 0, 0).toLocalDate());
        emailTextField.setText("");
        photoDeProfilImageView.setImage(null);
    }
    void delete(int id) {
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
        // user = new Responsable_de_cinema(Integer.parseInt(idTextField.getText()),
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
        UserService userService = new UserService();
        userService.delete(new User(id, "", "", 0, "", "", "", new Date(0, 0, 0), "", null) {
        });
    }
    @FXML
    void importImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );
        fileChooser.setTitle("Sélectionner une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destinationDirectory1 = "./src/main/resources/img/users/";
                String destinationDirectory2 = "C:\\xampp\\htdocs\\Rakcha\\rakcha-web\\public\\img\\users\\";
                Path destinationPath1 = Paths.get(destinationDirectory1);
                Path destinationPath2 = Paths.get(destinationDirectory2);
                String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destinationFilePath1 = destinationPath1.resolve(uniqueFileName);
                Path destinationFilePath2 = destinationPath2.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationFilePath1);
                Files.copy(selectedFile.toPath(), destinationFilePath2);
                Image selectedImage = new Image(destinationFilePath1.toUri().toString());
                photoDeProfilImageView.setImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void update(User user) {
        try {
            UserService userService = new UserService();
            userService.update(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void generatePDF() {
        UserService userService = new UserService();
        userService.generateUserPDF();
    }
    @FXML
    public void signOut(ActionEvent event) throws IOException {
        Stage stage = (Stage) emailTextField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUp.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
    }
}
