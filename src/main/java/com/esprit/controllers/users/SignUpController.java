package com.esprit.controllers.users;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import com.esprit.utils.CloudinaryStorage;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.synedra.validatorfx.Validator;

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class SignUpController {
    private static final Logger LOGGER = Logger.getLogger(SignUpController.class.getName());
    @FXML
    AnchorPane anchorPane;
    @FXML
    private TextField adresseTextField;
    @FXML
    private DatePicker dateDeNaissanceDatePicker;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField nomTextField;
    @FXML
    private TextField num_telephoneTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ImageView photoDeProfilImageView;
    @FXML
    private TextField prenomTextField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Button loginButton; // Add this field

    /** 
     * @param event
     * @throws IOException
     */
    @FXML
    void switchToLogin(ActionEvent event) throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/Login.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
    }

    @FXML
    void initialize() {
        final Tooltip tooltip = new Tooltip();
        final UserService userService = new UserService();
        this.emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            final String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!this.emailTextField.getText().matches(emailRegex)) {
                this.emailTextField.getStyleClass().removeAll("checked");
                // emailErrorLabel.setText("this mail format is wrong");
                this.emailTextField.getStyleClass().add("notChecked");
            } else if (userService.checkEmailFound(newValue)) {
                this.emailTextField.getStyleClass().removeAll("checked");
                // emailErrorLabel.setText("this mail address is used");
                this.emailTextField.getStyleClass().add("notChecked");
            } else {
                // emailErrorLabel.setText("");
                this.emailTextField.getStyleClass().removeAll("notChecked");
                this.emailTextField.getStyleClass().add("checked");
            }
        });
        this.passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (8 > passwordTextField.getLength()) {
                this.passwordTextField.getStyleClass().removeAll("checked");
                // passwordErrorLabel.setText("the password must contain at least 8
                // characters");
                this.emailTextField.getStyleClass().add("notChecked");
            } else {
                // passwordErrorLabel.setText("");
                this.passwordTextField.getStyleClass().removeAll("notChecked");
                this.passwordTextField.getStyleClass().add("checked");
            }
        });
        this.nomTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            /**
             * Performs changed operation.
             *
             * @return the result of the operation
             */
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                final Validator validator = new Validator();
                validator.createCheck().dependsOn("firstName", nomTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        }).decorates(nomTextField).immediate();
                final Window window = nomTextField.getScene().getWindow();
                final Bounds bounds = nomTextField
                        .localToScreen(nomTextField.getBoundsInLocal());
                nomTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    /**
                     * Performs changed operation.
                     *
                     * @return the result of the operation
                     */
                    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                            final String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            nomTextField.setTooltip(tooltip);
                            nomTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (null != nomTextField.getTooltip()) {
                                nomTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                nomTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
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
        });
        this.prenomTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            /**
             * Performs changed operation.
             *
             * @return the result of the operation
             */
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                final Validator validator = new Validator();
                validator.createCheck().dependsOn("firstName", prenomTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        }).decorates(prenomTextField).immediate();
                final Window window = prenomTextField.getScene().getWindow();
                final Bounds bounds = prenomTextField
                        .localToScreen(prenomTextField.getBoundsInLocal());
                prenomTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    /**
                     * Performs changed operation.
                     *
                     * @return the result of the operation
                     */
                    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                            final String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            prenomTextField.setTooltip(tooltip);
                            prenomTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (null != prenomTextField.getTooltip()) {
                                prenomTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                prenomTextField.addEventFilter(KeyEvent.KEY_PRESSED,
                        new EventHandler<KeyEvent>() {
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
        });
        this.adresseTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            /**
             * Performs changed operation.
             *
             * @return the result of the operation
             */
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                final Validator validator = new Validator();
                validator.createCheck().dependsOn("firstName", adresseTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        }).decorates(adresseTextField).immediate();
                final Window window = adresseTextField.getScene().getWindow();
                final Bounds bounds = adresseTextField
                        .localToScreen(adresseTextField.getBoundsInLocal());
                adresseTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    /**
                     * Performs changed operation.
                     *
                     * @return the result of the operation
                     */
                    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                            final String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            adresseTextField.setTooltip(tooltip);
                            adresseTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (null != adresseTextField.getTooltip()) {
                                adresseTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                adresseTextField.addEventFilter(KeyEvent.KEY_PRESSED,
                        new EventHandler<KeyEvent>() {
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
        });
        this.emailTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            /**
             * Performs changed operation.
             *
             * @return the result of the operation
             */
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                final Validator validator = new Validator();
                validator.createCheck().dependsOn("firstName", emailTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName) {
                                final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                                if (!userName.matches(emailRegex)) {
                                    c.error("Invalid email format.");
                                }
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        }).decorates(emailTextField).immediate();
                final Window window = emailTextField.getScene().getWindow();
                final Bounds bounds = emailTextField
                        .localToScreen(emailTextField.getBoundsInLocal());
                emailTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    /**
                     * Performs changed operation.
                     *
                     * @return the result of the operation
                     */
                    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                            final String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            emailTextField.setTooltip(tooltip);
                            emailTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (null != emailTextField.getTooltip()) {
                                emailTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                emailTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
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
        });
        this.passwordTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            /**
             * Performs changed operation.
             *
             * @return the result of the operation
             */
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                final Validator validator = new Validator();
                validator.createCheck().dependsOn("firstName", passwordTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        }).decorates(passwordTextField).immediate();
                final Window window = passwordTextField.getScene().getWindow();
                final Bounds bounds = passwordTextField
                        .localToScreen(passwordTextField.getBoundsInLocal());
                passwordTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    /**
                     * Performs changed operation.
                     *
                     * @return the result of the operation
                     */
                    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                            final String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            passwordTextField.setTooltip(tooltip);
                            passwordTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (null != passwordTextField.getTooltip()) {
                                passwordTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                passwordTextField.addEventFilter(KeyEvent.KEY_PRESSED,
                        new EventHandler<KeyEvent>() {
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
        });
        this.num_telephoneTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            /**
             * Performs changed operation.
             *
             * @return the result of the operation
             */
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                final Validator validator = new Validator();
                validator.createCheck()
                        .dependsOn("firstName", num_telephoneTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName) {
                                final String numberRegex = "\\d*";
                                if (!userName.matches(numberRegex)) {
                                    c.error("Please use only numbers.");
                                }
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        }).decorates(num_telephoneTextField).immediate();
                final Window window = num_telephoneTextField.getScene().getWindow();
                final Bounds bounds = num_telephoneTextField
                        .localToScreen(num_telephoneTextField.getBoundsInLocal());
                num_telephoneTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    /**
                     * Performs changed operation.
                     *
                     * @return the result of the operation
                     */
                    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                            final String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            num_telephoneTextField.setTooltip(tooltip);
                            num_telephoneTextField.getTooltip().show(window,
                                    bounds.getMinX() - 10, bounds.getMinY() + 30);
                        } else {
                            if (null != num_telephoneTextField.getTooltip()) {
                                num_telephoneTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                num_telephoneTextField.addEventFilter(KeyEvent.KEY_PRESSED,
                        new EventHandler<KeyEvent>() {
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
        });
        final List<String> roleList = Arrays.asList("client", "responsable de cinema");
        for (final String role : roleList) {
            this.roleComboBox.getItems().add(role);
        }

        // Add event handler for login button
        loginButton.setOnAction(event -> {
            try {
                switchToLogin(event);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error switching to login view", e);
            }
        });
    }

    private String cloudinaryImageUrl;

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
                SignUpController.LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
            }
        }
    }

    /**
     * @param event
     * @throws IOException
     */
    @FXML
    void signup(final ActionEvent event) throws IOException {
        final String role = this.roleComboBox.getValue();
        User user = null;
        final String nom = this.nomTextField.getText();
        final String prenom = this.prenomTextField.getText();
        final String num_telephone = this.num_telephoneTextField.getText();
        final String password = this.passwordTextField.getText();
        final String email = this.emailTextField.getText();
        final LocalDate dateDeNaissance = this.dateDeNaissanceDatePicker.getValue();
        if (nom.isEmpty() || prenom.isEmpty() || num_telephone.isEmpty() || password.isEmpty() || role.isEmpty()
                || email.isEmpty() || null == dateDeNaissance) {
            final Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all the required fields",
                    ButtonType.CLOSE);
            alert.show();
            return;
        }
        if (!num_telephone.matches("\\d+")) {
            final Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid phone number format", ButtonType.CLOSE);
            alert.show();
            return;
        }
        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            final Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid email format", ButtonType.CLOSE);
            alert.show();
            return;
        }
        switch (role) {
            case "responsable de cinema":
                user = new CinemaManager(this.nomTextField.getText(), this.prenomTextField.getText(),
                        this.num_telephoneTextField.getText(), this.passwordTextField.getText(),
                        this.roleComboBox.getValue(), this.emailTextField.getText(),
                        Date.valueOf(this.dateDeNaissanceDatePicker.getValue()), this.emailTextField.getText(),
                        cloudinaryImageUrl != null ? cloudinaryImageUrl : "");
                break;
            case "client":
                user = new Client(this.nomTextField.getText(), this.prenomTextField.getText(),
                        this.num_telephoneTextField.getText(), this.passwordTextField.getText(),
                        this.roleComboBox.getValue(), this.emailTextField.getText(),
                        Date.valueOf(this.dateDeNaissanceDatePicker.getValue()), this.emailTextField.getText(),
                        cloudinaryImageUrl != null ? cloudinaryImageUrl : "");
                break;
            default:
                final Alert alert = new Alert(Alert.AlertType.ERROR, "the given role is not available",
                        ButtonType.CLOSE);
                alert.show();
                return;
        }
        final UserService userService = new UserService();
        userService.create(user);
        final Stage stage = (Stage) this.nomTextField.getScene().getWindow();
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Profile.fxml"));
        final Parent root = loader.load();
        final ProfileController profileController = loader.getController();
        profileController.setData(user);
        stage.setScene(new Scene(root));
    }
}
