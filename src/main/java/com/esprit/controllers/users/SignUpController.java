package com.esprit.controllers.users;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.users.UserService;

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
                validator.createCheck().dependsOn("firstName", SignUpController.this.nomTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        }).decorates(SignUpController.this.nomTextField).immediate();
                final Window window = SignUpController.this.nomTextField.getScene().getWindow();
                final Bounds bounds = SignUpController.this.nomTextField
                        .localToScreen(SignUpController.this.nomTextField.getBoundsInLocal());
                SignUpController.this.nomTextField.textProperty().addListener(new ChangeListener<String>() {
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
                            SignUpController.this.nomTextField.setTooltip(tooltip);
                            SignUpController.this.nomTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (null != nomTextField.getTooltip()) {
                                SignUpController.this.nomTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                SignUpController.this.nomTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
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
                validator.createCheck().dependsOn("firstName", SignUpController.this.prenomTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        }).decorates(SignUpController.this.prenomTextField).immediate();
                final Window window = SignUpController.this.prenomTextField.getScene().getWindow();
                final Bounds bounds = SignUpController.this.prenomTextField
                        .localToScreen(SignUpController.this.prenomTextField.getBoundsInLocal());
                SignUpController.this.prenomTextField.textProperty().addListener(new ChangeListener<String>() {
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
                            SignUpController.this.prenomTextField.setTooltip(tooltip);
                            SignUpController.this.prenomTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (null != prenomTextField.getTooltip()) {
                                SignUpController.this.prenomTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                SignUpController.this.prenomTextField.addEventFilter(KeyEvent.KEY_PRESSED,
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
                validator.createCheck().dependsOn("firstName", SignUpController.this.adresseTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        }).decorates(SignUpController.this.adresseTextField).immediate();
                final Window window = SignUpController.this.adresseTextField.getScene().getWindow();
                final Bounds bounds = SignUpController.this.adresseTextField
                        .localToScreen(SignUpController.this.adresseTextField.getBoundsInLocal());
                SignUpController.this.adresseTextField.textProperty().addListener(new ChangeListener<String>() {
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
                            SignUpController.this.adresseTextField.setTooltip(tooltip);
                            SignUpController.this.adresseTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (null != adresseTextField.getTooltip()) {
                                SignUpController.this.adresseTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                SignUpController.this.adresseTextField.addEventFilter(KeyEvent.KEY_PRESSED,
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
                validator.createCheck().dependsOn("firstName", SignUpController.this.emailTextField.textProperty())
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
                        }).decorates(SignUpController.this.emailTextField).immediate();
                final Window window = SignUpController.this.emailTextField.getScene().getWindow();
                final Bounds bounds = SignUpController.this.emailTextField
                        .localToScreen(SignUpController.this.emailTextField.getBoundsInLocal());
                SignUpController.this.emailTextField.textProperty().addListener(new ChangeListener<String>() {
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
                            SignUpController.this.emailTextField.setTooltip(tooltip);
                            SignUpController.this.emailTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (null != emailTextField.getTooltip()) {
                                SignUpController.this.emailTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                SignUpController.this.emailTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
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
                validator.createCheck().dependsOn("firstName", SignUpController.this.passwordTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        }).decorates(SignUpController.this.passwordTextField).immediate();
                final Window window = SignUpController.this.passwordTextField.getScene().getWindow();
                final Bounds bounds = SignUpController.this.passwordTextField
                        .localToScreen(SignUpController.this.passwordTextField.getBoundsInLocal());
                SignUpController.this.passwordTextField.textProperty().addListener(new ChangeListener<String>() {
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
                            SignUpController.this.passwordTextField.setTooltip(tooltip);
                            SignUpController.this.passwordTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (null != passwordTextField.getTooltip()) {
                                SignUpController.this.passwordTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                SignUpController.this.passwordTextField.addEventFilter(KeyEvent.KEY_PRESSED,
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
                        .dependsOn("firstName", SignUpController.this.num_telephoneTextField.textProperty())
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
                        }).decorates(SignUpController.this.num_telephoneTextField).immediate();
                final Window window = SignUpController.this.num_telephoneTextField.getScene().getWindow();
                final Bounds bounds = SignUpController.this.num_telephoneTextField
                        .localToScreen(SignUpController.this.num_telephoneTextField.getBoundsInLocal());
                SignUpController.this.num_telephoneTextField.textProperty().addListener(new ChangeListener<String>() {
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
                            SignUpController.this.num_telephoneTextField.setTooltip(tooltip);
                            SignUpController.this.num_telephoneTextField.getTooltip().show(window,
                                    bounds.getMinX() - 10, bounds.getMinY() + 30);
                        } else {
                            if (null != num_telephoneTextField.getTooltip()) {
                                SignUpController.this.num_telephoneTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                SignUpController.this.num_telephoneTextField.addEventFilter(KeyEvent.KEY_PRESSED,
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
                // Configure Cloudinary with your credentials
                Map<String, String> config = new HashMap<>();
                config.put("cloud_name", "dddqilcmw"); // Replace with your Cloudinary cloud name
                config.put("api_key", "786637482253218"); // Replace with your Cloudinary API key
                config.put("api_secret", "qaDHzzQB2K7DsSTlmHyqgrJjGXo"); // Replace with your Cloudinary API secret
                Cloudinary cloudinary = new Cloudinary(config);

                // Upload the file to Cloudinary
                Map<String, Object> uploadResult = cloudinary.uploader().upload(selectedFile, ObjectUtils.emptyMap());

                // Get the secure URL of the uploaded image
                cloudinaryImageUrl = (String) uploadResult.get("secure_url");

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
