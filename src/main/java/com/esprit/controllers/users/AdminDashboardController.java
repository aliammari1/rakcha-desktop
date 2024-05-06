package com.esprit.controllers.users;

import com.esprit.controllers.ClientSideBarController;
import com.esprit.models.users.Admin;
import com.esprit.models.users.Client;
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
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

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

            formValidator = new Validator();
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

            firstNameTextField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                    formValidator.createCheck()
                            .dependsOn("firstName", firstNameTextField.textProperty())
                            .withMethod(c -> {
                                String userName = c.get("firstName");
                                if (userName != null) {
                                    if (!userName.toLowerCase().equals(userName)) {
                                        c.error("Please use only lowercase letters.");
                                    } else if (userName.isEmpty())
                                        c.error("the string is empty");
                                }
                            })
                            .decorates(firstNameTextField)
                            .immediate();
                    Window window = firstNameTextField.getScene().getWindow();
                    Bounds bounds = firstNameTextField.localToScreen(firstNameTextField.getBoundsInLocal());
                    firstNameTextField.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                            String newValue) {
                            if (formValidator.containsErrors()) {
                                tooltip.setText(formValidator.createStringBinding().getValue());
                                tooltip.setStyle("-fx-background-color: #f00;");
                                firstNameTextField.setTooltip(tooltip);
                                firstNameTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                        bounds.getMinY() + 30);
                            } else {
                                if (firstNameTextField.getTooltip() != null) {

                                    firstNameTextField.getTooltip().hide();
                                }
                            }
                        }
                    });
                }
            });

            lastNameTextField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    formValidator.createCheck()
                            .dependsOn("firstName", lastNameTextField.textProperty())
                            .withMethod(c -> {
                                String userName = c.get("firstName");
                                if (userName != null) {
                                    if (!userName.toLowerCase().equals(userName)) {
                                        c.error("Please use only lowercase letters.");
                                    } else if (userName.isEmpty())
                                        c.error("the string is empty");
                                }
                            })
                            .decorates(lastNameTextField)
                            .immediate();
                    Window window = lastNameTextField.getScene().getWindow();
                    Bounds bounds = lastNameTextField.localToScreen(lastNameTextField.getBoundsInLocal());
                    lastNameTextField.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                            String newValue) {
                            if (formValidator.containsErrors()) {
                                tooltip.setText(formValidator.createStringBinding().getValue());
                                tooltip.setStyle("-fx-background-color: #f00;");
                                lastNameTextField.setTooltip(tooltip);
                                lastNameTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                        bounds.getMinY() + 30);
                            } else {
                                if (lastNameTextField.getTooltip() != null) {
                                    lastNameTextField.getTooltip().hide();
                                }
                            }
                        }
                    });
                }
            });

            adresseTextField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    formValidator.createCheck()
                            .dependsOn("firstName", adresseTextField.textProperty())
                            .withMethod(c -> {
                                String userName = c.get("firstName");
                                if (userName != null) {
                                    if (!userName.toLowerCase().equals(userName)) {
                                        c.error("Please use only lowercase letters.");
                                    } else if (userName.isEmpty())
                                        c.error("the string is empty");
                                }
                            })
                            .decorates(adresseTextField)
                            .immediate();
                    Window window = adresseTextField.getScene().getWindow();
                    Bounds bounds = adresseTextField.localToScreen(adresseTextField.getBoundsInLocal());
                    adresseTextField.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                            String newValue) {
                            if (formValidator.containsErrors()) {
                                tooltip.setText(formValidator.createStringBinding().getValue());
                                tooltip.setStyle("-fx-background-color: #f00;");
                                adresseTextField.setTooltip(tooltip);
                                adresseTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                        bounds.getMinY() + 30);
                            } else {
                                if (adresseTextField.getTooltip() != null) {

                                    adresseTextField.getTooltip().hide();
                                }
                            }
                        }
                    });
                }
            });

            emailTextField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    formValidator.createCheck()
                            .dependsOn("firstName", emailTextField.textProperty())
                            .withMethod(c -> {
                                String userName = c.get("firstName");
                                if (userName != null) {
                                    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                                    if (!userName.matches(emailRegex)) {
                                        c.error("Invalid email format.");
                                    } else if (userName.isEmpty()) {
                                        c.error("the string is empty");
                                    }
                                }
                            })
                            .decorates(emailTextField)
                            .immediate();
                    Window window = emailTextField.getScene().getWindow();
                    Bounds bounds = emailTextField.localToScreen(emailTextField.getBoundsInLocal());
                    emailTextField.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                            String newValue) {
                            if (formValidator.containsErrors()) {
                                tooltip.setText(formValidator.createStringBinding().getValue());
                                tooltip.setStyle("-fx-background-color: #f00;");
                                emailTextField.setTooltip(tooltip);
                                emailTextField.getTooltip().show(window, bounds.getMinX() - 10, bounds.getMinY() + 30);
                            } else {
                                if (emailTextField.getTooltip() != null) {
                                    emailTextField.getTooltip().hide();
                                }
                            }
                        }
                    });
                }
            });

            passwordTextField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                    formValidator.createCheck()
                            .dependsOn("firstName", passwordTextField.textProperty())
                            .withMethod(c -> {
                                String userName = c.get("firstName");
                                if (userName != null) {
                                    if (!userName.toLowerCase().equals(userName)) {
                                        c.error("Please use only lowercase letters.");
                                    } else if (userName.isEmpty())
                                        c.error("the string is empty");
                                }
                            })
                            .decorates(passwordTextField)
                            .immediate();
                    Window window = passwordTextField.getScene().getWindow();
                    Bounds bounds = passwordTextField.localToScreen(passwordTextField.getBoundsInLocal());
                    passwordTextField.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                            String newValue) {
                            if (formValidator.containsErrors()) {
                                tooltip.setText(formValidator.createStringBinding().getValue());
                                tooltip.setStyle("-fx-background-color: #f00;");
                                passwordTextField.setTooltip(tooltip);
                                passwordTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                        bounds.getMinY() + 30);
                            } else {
                                if (passwordTextField.getTooltip() != null) {

                                    passwordTextField.getTooltip().hide();
                                }
                            }
                        }
                    });
                }
            });

            phoneNumberTextField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    formValidator.createCheck()
                            .dependsOn("firstName", phoneNumberTextField.textProperty())
                            .withMethod(c -> {
                                String userName = c.get("firstName");
                                if (userName != null) {
                                    String numberRegex = "\\d*";
                                    if (!userName.matches(numberRegex)) {
                                        c.error("Please use only numbers.");
                                    } else if (userName.isEmpty()) {
                                        c.error("the string is empty");
                                    }
                                }
                            })
                            .decorates(phoneNumberTextField)
                            .immediate();
                    Window window = phoneNumberTextField.getScene().getWindow();
                    Bounds bounds = phoneNumberTextField.localToScreen(phoneNumberTextField.getBoundsInLocal());
                    phoneNumberTextField.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                            String newValue) {
                            if (formValidator.containsErrors()) {
                                tooltip.setText(formValidator.createStringBinding().getValue());
                                tooltip.setStyle("-fx-background-color: #f00;");
                                phoneNumberTextField.setTooltip(tooltip);
                                phoneNumberTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                        bounds.getMinY() + 30);
                            } else {
                                if (phoneNumberTextField.getTooltip() != null) {
                                    phoneNumberTextField.getTooltip().hide();
                                }
                            }
                        }
                    });
                }
            });

            List<String> roleList = List.of("admin");

            for (String role : roleList)
                roleComboBox.getItems().add(role);

            readUserTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void validatorCheckTextField(TextField textField, List<Boolean> condition, List<String> errorMessage) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                formValidator.createCheck()
                        .dependsOn("text", textField.textProperty())
                        .withMethod(c -> {
                            String validationText = c.get("text");
                            if (validationText != null) {
                                for (int i = 0; i < condition.size(); i++) {
                                    if (condition.get(i)) {
                                        c.error(errorMessage.get(i));
                                    }
                                }
                            }
                        })
                        .decorates(textField)
                        .immediate();
                Window window = textField.getScene().getWindow();
                Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                textField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue,
                                        String newValue) {
                        if (formValidator.containsErrors()) {
                            formValidatorTooltip.setText(formValidator.createStringBinding().getValue());
                            formValidatorTooltip.setStyle("-fx-background-color: #f00;");
                            textField.setTooltip(formValidatorTooltip);
                            textField.getTooltip().show(window, bounds.getMinX() - 10, bounds.getMinY() + 30);
                        } else {
                            if (textField.getTooltip() != null) {
                                textField.getTooltip().hide();
                            }
                        }
                    }
                });
            }
        });
    }

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

    @FXML
    void addAdmin(ActionEvent event) {
        try {
            String role = roleComboBox.getValue();
            User user = null;
            URI uri = null;
            if (role.equals("admin")) {
                uri = new URI(photoDeProfilImageView.getImage().getUrl());
                user = new Admin(firstNameTextField.getText(), lastNameTextField.getText(),
                        Integer.parseInt(phoneNumberTextField.getText()), passwordTextField.getText(),
                        roleComboBox.getValue(), emailTextField.getText(),
                        Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText(), uri.getPath());
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

    private void setupCellValueFactories() {
        firstNameTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        numTelTableColumn.setCellValueFactory(new PropertyValueFactory<User, Integer>("phoneNumber"));
        passwordTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        roleTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("role"));
        adresseTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("address"));
        dateDeNaissanceTableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<User, DatePicker>, ObservableValue<DatePicker>>() {
                    @Override
                    public ObservableValue<DatePicker> call(TableColumn.CellDataFeatures<User, DatePicker> param) {
                        DatePicker datePicker = new DatePicker();
                        datePicker.setValue(param.getValue().getBirthDate().toLocalDate());
                        return new SimpleObjectProperty<DatePicker>(datePicker);
                    }
                });
        emailTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));

        photoDeProfilTableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<User, HBox>, ObservableValue<HBox>>() {
                    @Override
                    public ObservableValue<HBox> call(TableColumn.CellDataFeatures<User, HBox> param) {
                        HBox hBox = new HBox();
                        try {
                            ImageView imageView = new ImageView(new Image(param.getValue().getPhoto_de_profil()));
                            hBox.getChildren().add(imageView);
                            hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
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
                    @Override
                    public ObservableValue<Button> call(TableColumn.CellDataFeatures<User, Button> param) {
                        Button button = new Button("delete");
                        button.setOnAction(new EventHandler<ActionEvent>() {
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
                                        if (validator.containsErrors())
                                            event.consume();
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
                                        if (validator.containsErrors())
                                            event.consume();
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
                                        if (validator.containsErrors())
                                            event.consume();
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
                                        if (validator.containsErrors())
                                            event.consume();
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
                                        if (validator.containsErrors())
                                            event.consume();
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
                                        if (validator.containsErrors())
                                            event.consume();
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
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"));
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                Image image = new Image(file.toURI().toURL().toString());
                photoDeProfilImageView.setImage(image);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
