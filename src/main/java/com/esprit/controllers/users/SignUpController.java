package com.esprit.controllers.users;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import com.esprit.utils.CloudinaryStorage;

import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import net.synedra.validatorfx.Validator;

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class SignUpController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(SignUpController.class.getName());
    private static final Random RANDOM = new Random();

    @FXML
    private StackPane rootContainer;
    @FXML
    private AnchorPane anchorPane;
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
    private PasswordField passwordTextField;
    @FXML
    private ImageView photoDeProfilImageView;
    @FXML
    private TextField prenomTextField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Button loginButton;
    @FXML
    private Button signUpButton;

    // Animation elements
    @FXML
    private Circle particle1, particle2, particle3, particle4, particle5, particle6;
    @FXML
    private Circle particle7, particle8, particle9, particle10, particle11, particle12;
    @FXML
    private Polygon shape1, shape2, shape5;
    @FXML
    private Rectangle shape3, shape6;
    @FXML
    private Circle shape4;

    // Animation control
    private Timeline particleCreationTimeline;

    // Arrays to store dynamic particles and shapes
    private Circle[] dynamicParticles;
    private Polygon[] dynamicShapes;
    private Rectangle[] dynamicRectangles;
    private int maxDynamicElements = 15; // Number of dynamic elements to create

    // Cloudinary image URL
    private String cloudinaryImageUrl;

    /**
     * Switches the current window to the login scene and stops any running signup animations.
     *
     * @throws IOException if the Login.fxml resource cannot be loaded or initialized
     */
    @FXML
    void switchToLogin(ActionEvent event) throws IOException {
        try {
            stopAllAnimations();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/Login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        }
 catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error switching to login view", e);
            throw e;
        }

    }


    /**
     * Initialize controller state, start UI animations, and configure validation and navigation handlers.
     *
     * Performs initial setup of dynamic element storage, schedules a short delayed initialization of
     * particle/shape animations and their dynamic creations, configures form validation, and attaches
     * the login button handler to transition back to the login view.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize arrays for dynamic elements
        dynamicParticles = new Circle[maxDynamicElements];
        dynamicShapes = new Polygon[maxDynamicElements / 3];
        dynamicRectangles = new Rectangle[maxDynamicElements / 3];

        // Initialize animations with a slight delay to ensure FXML is fully loaded
        Timeline delayedInit = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            LOGGER.info("Starting animation initialization...");
            initializeParticleAnimation();
            initializeShapeAnimation();
            createDynamicAnimations();
            LOGGER.info("Animation initialization completed.");
        }
));
        delayedInit.play();

        // Setup validation and data binding
        setupValidation();

        // Add event handler for login button
        loginButton.setOnAction(event -> {
            try {
                switchToLogin(event);
            }
 catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error switching to login view", e);
            }

        }
);
    }


    /**
     * Configure validation and UI feedback for the signup form fields and populate the role selector.
     *
     * <p>Attaches listeners that validate email (including existence via UserService), password length,
     * name, given name, address, and phone number formats, and shows contextual error tooltips and
     * prevents form submission via Enter when validation errors are present.</p>
     */
    private void setupValidation() {
        final Tooltip tooltip = new Tooltip();
        final UserService userService = new UserService();

        // Email validation
        this.emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            final String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!this.emailTextField.getText().matches(emailRegex)) {
                this.emailTextField.getStyleClass().removeAll("checked");
                this.emailTextField.getStyleClass().add("notChecked");
            }
 else if (userService.checkEmailFound(newValue)) {
                this.emailTextField.getStyleClass().removeAll("checked");
                this.emailTextField.getStyleClass().add("notChecked");
            }
 else {
                this.emailTextField.getStyleClass().removeAll("notChecked");
                this.emailTextField.getStyleClass().add("checked");
            }

        }
);

        // Password validation
        this.passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (8 > passwordTextField.getLength()) {
                this.passwordTextField.getStyleClass().removeAll("checked");
                this.emailTextField.getStyleClass().add("notChecked");
            }
 else {
                this.passwordTextField.getStyleClass().removeAll("notChecked");
                this.passwordTextField.getStyleClass().add("checked");
            }

        }
);

        // Nom validation
        this.nomTextField.textProperty().addListener(new ChangeListener<String>() {
            /**
             * Configures validation for the nomTextField: enforces lowercase-only and non-empty rules, displays a red tooltip with validation messages anchored near the field when validation fails, and installs an Enter-key filter that consumes Enter keystrokes while validation errors exist.
             *
             * @param observable the observed text property
             * @param oldValue the previous text value
             * @param newValue the new text value
             */
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                final Validator validator = new Validator();
                validator.createCheck().dependsOn("firstName", nomTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            }
 else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }

                        }
).decorates(nomTextField).immediate();
                final Window window = nomTextField.getScene().getWindow();
                final Bounds bounds = nomTextField
                        .localToScreen(nomTextField.getBoundsInLocal());
                nomTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                            final String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            nomTextField.setTooltip(tooltip);
                            nomTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        }
 else {
                            if (null != nomTextField.getTooltip()) {
                                nomTextField.getTooltip().hide();
                            }

                        }

                    }

                }
);
                nomTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                    @Override
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
);

        // Prenom validation
        this.prenomTextField.textProperty().addListener(new ChangeListener<String>() {
            /**
             * Validate the prenomTextField content and show or hide a contextual tooltip with validation messages.
             *
             * When the field contains errors (not all lowercase or empty), a red-styled tooltip with the validation
             * message is displayed near the field and pressing Enter is prevented; when the field is valid the tooltip
             * is hidden and Enter is allowed.
             *
             * @param observable the observable value that changed
             * @param oldValue the previous text value
             * @param newValue the new text value
             */
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                final Validator validator = new Validator();
                validator.createCheck().dependsOn("firstName", prenomTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            }
 else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }

                        }
).decorates(prenomTextField).immediate();
                final Window window = prenomTextField.getScene().getWindow();
                final Bounds bounds = prenomTextField
                        .localToScreen(prenomTextField.getBoundsInLocal());
                prenomTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                            final String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            prenomTextField.setTooltip(tooltip);
                            prenomTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        }
 else {
                            if (null != prenomTextField.getTooltip()) {
                                prenomTextField.getTooltip().hide();
                            }

                        }

                    }

                }
);
                prenomTextField.addEventFilter(KeyEvent.KEY_PRESSED,
                        new EventHandler<KeyEvent>() {
                            @Override
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
);

        // Adresse validation
        this.adresseTextField.textProperty().addListener(new ChangeListener<String>() {
            /**
             * Validate the adresseTextField value, display validation feedback, and block Enter when invalid.
             *
             * <p>Checks that the field is non-empty and contains only lowercase letters; when validation fails,
             * a red tooltip with the error message is shown near the field and pressing Enter is consumed to
             * prevent form submission. When the field is valid the tooltip is hidden.</p>
             *
             * @param observable the observed text property
             * @param oldValue the previous text value
             * @param newValue the current text value
             */
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                final Validator validator = new Validator();
                validator.createCheck().dependsOn("firstName", adresseTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            }
 else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }

                        }
).decorates(adresseTextField).immediate();
                final Window window = adresseTextField.getScene().getWindow();
                final Bounds bounds = adresseTextField
                        .localToScreen(adresseTextField.getBoundsInLocal());
                adresseTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                            final String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            adresseTextField.setTooltip(tooltip);
                            adresseTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        }
 else {
                            if (null != adresseTextField.getTooltip()) {
                                adresseTextField.getTooltip().hide();
                            }

                        }

                    }

                }
);
                adresseTextField.addEventFilter(KeyEvent.KEY_PRESSED,
                        new EventHandler<KeyEvent>() {
                            @Override
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
);

        // Email validation
        this.emailTextField.textProperty().addListener(new ChangeListener<String>() {
            /**
             * Validates the emailTextField value, displays a tooltip with validation messages when invalid,
             * and prevents Enter key submission while validation errors exist.
             *
             * @param observable the observable value backing the text property
             * @param oldValue   the previous text value
             * @param newValue   the new text value
             */
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                final Validator validator = new Validator();
                validator.createCheck().dependsOn("firstName", emailTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName) {
                                final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}
$";
                                if (!userName.matches(emailRegex)) {
                                    c.error("Invalid email format.");
                                }

                            }
 else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }

                        }
).decorates(emailTextField).immediate();
                final Window window = emailTextField.getScene().getWindow();
                final Bounds bounds = emailTextField
                        .localToScreen(emailTextField.getBoundsInLocal());
                emailTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                            final String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            emailTextField.setTooltip(tooltip);
                            emailTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        }
 else {
                            if (null != emailTextField.getTooltip()) {
                                emailTextField.getTooltip().hide();
                            }

                        }

                    }

                }
);
                emailTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                    @Override
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
);

        // Password validation
        this.passwordTextField.textProperty().addListener(new ChangeListener<String>() {
            /**
             * Responds to changes on the observed text property by configuring validation for the password field and wiring UI feedback.
             *
             * Sets up a validator that requires the password field value to be non-empty and composed only of lowercase letters; shows a tooltip with validation messages positioned near the password field when validation fails; hides the tooltip when validation passes; and consumes Enter key events on the password field while validation errors remain.
             *
             * @param observable the observable value that changed
             * @param oldValue the previous text value
             * @param newValue the new text value
             */
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                final Validator validator = new Validator();
                validator.createCheck().dependsOn("firstName", passwordTextField.textProperty())
                        .withMethod(c -> {
                            final String userName = c.get("firstName");
                            if (null != userName && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            }
 else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }

                        }
).decorates(passwordTextField).immediate();
                final Window window = passwordTextField.getScene().getWindow();
                final Bounds bounds = passwordTextField
                        .localToScreen(passwordTextField.getBoundsInLocal());
                passwordTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                            final String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            passwordTextField.setTooltip(tooltip);
                            passwordTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        }
 else {
                            if (null != passwordTextField.getTooltip()) {
                                passwordTextField.getTooltip().hide();
                            }

                        }

                    }

                }
);
                passwordTextField.addEventFilter(KeyEvent.KEY_PRESSED,
                        new EventHandler<KeyEvent>() {
                            @Override
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
);

        // Phone number validation
        this.num_telephoneTextField.textProperty().addListener(new ChangeListener<String>() {
            /**
             * Validates the phone number field input, shows/hides an inline error tooltip, and prevents submission via Enter when invalid.
             *
             * Performs two checks on the field's text: it must contain only digits and must not be empty. When validation fails,
             * an error tooltip with a red background is shown near the text field containing the validation message; when validation
             * passes the tooltip is hidden. If the Enter key is pressed while validation errors exist, the key event is consumed.
             *
             * @param observable the observable value that changed
             * @param oldValue   the previous text value
             * @param newValue   the new text value
             */
            @Override
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

                            }
 else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }

                        }
).decorates(num_telephoneTextField).immediate();
                final Window window = num_telephoneTextField.getScene().getWindow();
                final Bounds bounds = num_telephoneTextField
                        .localToScreen(num_telephoneTextField.getBoundsInLocal());
                num_telephoneTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                            final String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            num_telephoneTextField.setTooltip(tooltip);
                            num_telephoneTextField.getTooltip().show(window,
                                    bounds.getMinX() - 10, bounds.getMinY() + 30);
                        }
 else {
                            if (null != num_telephoneTextField.getTooltip()) {
                                num_telephoneTextField.getTooltip().hide();
                            }

                        }

                    }

                }
);
                num_telephoneTextField.addEventFilter(KeyEvent.KEY_PRESSED,
                        new EventHandler<KeyEvent>() {
                            @Override
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
);

        // Initialize role selection combobox
        final List<String> roleList = Arrays.asList("client", "responsable de cinema");
        for (final String role : roleList) {
            this.roleComboBox.getItems().add(role);
        }

    }


    /**
     * Opens a file chooser to select a PNG or JPG image, uploads the selected file to Cloudinary,
     * stores the resulting URL in `cloudinaryImageUrl`, and sets the image into `photoDeProfilImageView`.
     *
     * @param event the UI action event that triggered the image import
     */
    @FXML
    void importImage(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
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

            }
 catch (final IOException e) {
                SignUpController.LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
            }

        }

    }


    /**
     * Validate the signup form, create and persist a User (Client or CinemaManager), stop animations, and navigate to the profile view.
     *
     * Performs field validation (required fields, numeric phone, email format) and shows an error Alert for validation failures.
     * On successful validation, constructs the appropriate User subclass based on the selected role, persists it via UserService,
     * stops active UI animations, and replaces the current scene with the user's profile.
     *
     * @param event the ActionEvent triggered by the signup control
     * @throws IOException if persisting the user or loading the profile view fails
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

        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}
")) {
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


        try {
            // Stop animations before navigating away
            stopAllAnimations();

            final UserService userService = new UserService();
            userService.create(user);
            final Stage stage = (Stage) this.nomTextField.getScene().getWindow();
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Profile.fxml"));
            final Parent root = loader.load();
            final ProfileController profileController = loader.getController();
            profileController.setData(user);
            stage.setScene(new Scene(root));
        }
 catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during signup process", e);
            throw new IOException("Failed to complete signup process", e);
        }

    }


    /**
     * Stop all running animations and remove runtime-created dynamic UI elements from the signup scene.
     *
     * <p>Stops the particle creation timeline (if active) and removes any dynamically created particles,
     * polygons, and rectangles from the foreground pane. The method tolerates missing elements and
     * failures during removal and logs warnings for cleanup errors.</p>
     */
    private void stopAllAnimations() {
        if (particleCreationTimeline != null) {
            particleCreationTimeline.stop();
        }


        // Clean up dynamic elements by removing them from the scene
        if (dynamicParticles != null) {
            AnchorPane foregroundPane = null;
            try {
                foregroundPane = (AnchorPane) rootContainer.getChildren().get(2);
            }
 catch (Exception e) {
                LOGGER.warning("Could not access foreground pane for cleanup: " + e.getMessage());
            }


            if (foregroundPane != null) {
                for (Circle particle : dynamicParticles) {
                    if (particle != null) {
                        try {
                            foregroundPane.getChildren().remove(particle);
                        }
 catch (Exception e) {
                            LOGGER.warning("Could not remove particle: " + e.getMessage());
                        }

                    }

                }

            }

        }


        // Clean up dynamic shapes
        if (dynamicShapes != null) {
            AnchorPane foregroundPane = null;
            try {
                foregroundPane = (AnchorPane) rootContainer.getChildren().get(2);
            }
 catch (Exception e) {
                LOGGER.warning("Could not access foreground pane for cleanup: " + e.getMessage());
            }


            if (foregroundPane != null) {
                for (Polygon shape : dynamicShapes) {
                    if (shape != null) {
                        try {
                            foregroundPane.getChildren().remove(shape);
                        }
 catch (Exception e) {
                            LOGGER.warning("Could not remove shape: " + e.getMessage());
                        }

                    }

                }

            }

        }


        // Clean up dynamic rectangles
        if (dynamicRectangles != null) {
            AnchorPane foregroundPane = null;
            try {
                foregroundPane = (AnchorPane) rootContainer.getChildren().get(2);
            }
 catch (Exception e) {
                LOGGER.warning("Could not access foreground pane for cleanup: " + e.getMessage());
            }


            if (foregroundPane != null) {
                for (Rectangle rect : dynamicRectangles) {
                    if (rect != null) {
                        try {
                            foregroundPane.getChildren().remove(rect);
                        }
 catch (Exception e) {
                            LOGGER.warning("Could not remove rectangle: " + e.getMessage());
                        }

                    }

                }

            }

        }


        LOGGER.info("All animations stopped and dynamic elements cleaned up");
    }


    /**
     * Initialize and start animated floating effects for the controller's predefined particle nodes.
     *
     * For each non-null particle this method attaches and starts translate (X and Y), scale, and fade
     * transitions to produce drifting, pulsing, and glow animations; logs a startup message for each
     * animated particle and a warning if a particle reference is null.
     */
    private void initializeParticleAnimation() {
        Circle[] particles = { particle1, particle2, particle3, particle4, particle5, particle6,
                particle7, particle8, particle9, particle10, particle11, particle12 }
;

        for (int i = 0; i < particles.length; i++) {
            if (particles[i] != null) {
                // Floating X movement
                TranslateTransition particleFloatX = new TranslateTransition(Duration.seconds(3 + i * 0.3),
                        particles[i]);
                particleFloatX.setFromX(0);
                particleFloatX.setToX(30 - i * 5);
                particleFloatX.setCycleCount(Animation.INDEFINITE);
                particleFloatX.setAutoReverse(true);
                particleFloatX.setInterpolator(Interpolator.EASE_BOTH);

                // Floating Y movement
                TranslateTransition particleFloatY = new TranslateTransition(Duration.seconds(4 + i * 0.2),
                        particles[i]);
                particleFloatY.setFromY(0);
                particleFloatY.setToY(25 - i * 3);
                particleFloatY.setCycleCount(Animation.INDEFINITE);
                particleFloatY.setAutoReverse(true);
                particleFloatY.setInterpolator(Interpolator.EASE_BOTH);

                // Pulsing scale effect
                ScaleTransition particleScale = new ScaleTransition(Duration.seconds(2.5 + i * 0.15), particles[i]);
                particleScale.setFromX(1.0);
                particleScale.setFromY(1.0);
                particleScale.setToX(1.4);
                particleScale.setToY(1.4);
                particleScale.setCycleCount(Animation.INDEFINITE);
                particleScale.setAutoReverse(true);
                particleScale.setInterpolator(Interpolator.EASE_BOTH);

                // Opacity animation for glow effect
                FadeTransition particleFade = new FadeTransition(Duration.seconds(3 + i * 0.1), particles[i]);
                particleFade.setFromValue(particles[i].getOpacity() * 0.5);
                particleFade.setToValue(particles[i].getOpacity() * 1.2);
                particleFade.setCycleCount(Animation.INDEFINITE);
                particleFade.setAutoReverse(true);
                particleFade.setInterpolator(Interpolator.EASE_BOTH);

                // Start all particle animations
                particleFloatX.play();
                particleFloatY.play();
                particleScale.play();
                particleFade.play();

                LOGGER.info("Started animations for particle" + (i + 1));
            }
 else {
                LOGGER.warning("particle" + (i + 1) + " is null");
            }

        }

    }


    /**
         * Sets up animations for the controller's predefined geometric shape nodes.
         *
         * For each non-null shape field (shape1..shape6) this method invokes the
         * appropriate animation helper: animatePolygon for Polygon, animateRectangleShape
         * for Rectangle, and animateCircleShape for Circle.
         */
    private void initializeShapeAnimation() {
        // Array of all existing shapes
        Object[] shapes = { shape1, shape2, shape3, shape4, shape5, shape6 }
;

        for (int i = 0; i < shapes.length; i++) {
            if (shapes[i] != null) {
                if (shapes[i] instanceof Polygon) {
                    animatePolygon((Polygon) shapes[i], i);
                }
 else if (shapes[i] instanceof Rectangle) {
                    animateRectangleShape((Rectangle) shapes[i], i);
                }
 else if (shapes[i] instanceof Circle) {
                    animateCircleShape((Circle) shapes[i], i);
                }

            }

        }

    }


    /**
     * Starts continuous rotation, scale, and translate animations on the provided polygon.
     *
     * @param shape the Polygon node to animate
     * @param index an index used to vary animation parameters (affects rotation direction and timing)
     */
    private void animatePolygon(Polygon shape, int index) {
        // Rotation animation
        RotateTransition rotate = new RotateTransition(Duration.seconds(12 - index), shape);
        rotate.setByAngle(index % 2 == 0 ? 360 : -360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();

        // Scale animation
        ScaleTransition scale = new ScaleTransition(Duration.seconds(6 + index * 0.5), shape);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.3);
        scale.setToY(1.3);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setAutoReverse(true);
        scale.setInterpolator(Interpolator.EASE_BOTH);
        scale.play();

        // Movement animation
        TranslateTransition move = new TranslateTransition(Duration.seconds(15 + index), shape);
        move.setFromX(-15);
        move.setFromY(-15);
        move.setToX(15);
        move.setToY(15);
        move.setCycleCount(Animation.INDEFINITE);
        move.setAutoReverse(true);
        move.setInterpolator(Interpolator.EASE_BOTH);
        move.play();

        LOGGER.info("Started animations for polygon shape" + (index + 1));
    }


    /**
     * Animate the given Rectangle with continuous rotation, opacity pulsing, and back-and-forth translation.
     *
     * The index parameter staggers and varies the animations: it adjusts durations for each transition and
     * alternates rotation direction (even indices rotate clockwise, odd indices counter-clockwise).
     *
     * @param shape the Rectangle node to animate
     * @param index zero-based index used to vary animation durations and rotation direction
     */
    private void animateRectangleShape(Rectangle shape, int index) {
        // Rotation animation
        RotateTransition rotate = new RotateTransition(Duration.seconds(10 - index * 0.5), shape);
        rotate.setByAngle(index % 2 == 0 ? 360 : -360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();

        // Fade animation
        FadeTransition fade = new FadeTransition(Duration.seconds(4 + index * 0.3), shape);
        fade.setFromValue(0.3);
        fade.setToValue(0.8);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setAutoReverse(true);
        fade.setInterpolator(Interpolator.EASE_BOTH);
        fade.play();

        // Movement animation
        TranslateTransition move = new TranslateTransition(Duration.seconds(12 + index), shape);
        move.setFromX(-10);
        move.setFromY(-10);
        move.setToX(10);
        move.setToY(10);
        move.setCycleCount(Animation.INDEFINITE);
        move.setAutoReverse(true);
        move.setInterpolator(Interpolator.EASE_BOTH);
        move.play();

        LOGGER.info("Started animations for rectangle shape" + (index + 1));
    }


    /**
     * Starts continuous scaling, fading, and translation animations on the given circle.
     *
     * The provided `index` influences animation durations and offsets so multiple shapes animate with staggered timings.
     *
     * @param shape the Circle node to animate
     * @param index an integer used to vary animation timings and offsets for this shape
     */
    private void animateCircleShape(Circle shape, int index) {
        // Scale animation
        ScaleTransition scale = new ScaleTransition(Duration.seconds(5 + index * 0.5), shape);
        scale.setFromX(0.7);
        scale.setFromY(0.7);
        scale.setToX(1.5);
        scale.setToY(1.5);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setAutoReverse(true);
        scale.setInterpolator(Interpolator.EASE_BOTH);
        scale.play();

        // Glow animation
        FadeTransition glow = new FadeTransition(Duration.seconds(3 + index * 0.2), shape);
        glow.setFromValue(0.4);
        glow.setToValue(0.9);
        glow.setCycleCount(Animation.INDEFINITE);
        glow.setAutoReverse(true);
        glow.setInterpolator(Interpolator.EASE_BOTH);
        glow.play();

        // Movement animation
        TranslateTransition move = new TranslateTransition(Duration.seconds(8 + index), shape);
        move.setFromX(-5);
        move.setFromY(-5);
        move.setToX(5);
        move.setToY(5);
        move.setCycleCount(Animation.INDEFINITE);
        move.setAutoReverse(true);
        move.setInterpolator(Interpolator.EASE_BOTH);
        move.play();

        LOGGER.info("Started animations for circle shape" + (index + 1));
    }


    /**
     * Creates and starts runtime-generated animated visual elements and adds them to the foreground pane.
     *
     * <p>This method generates three categories of dynamic nodes — small circular particles, polygonal shapes,
     * and rectangles — with randomized sizes, positions, styles, and opacities; adds each node to the UI
     * foreground AnchorPane; stores references in the controller's dynamicParticles, dynamicShapes, and
     * dynamicRectangles arrays; and invokes the corresponding animation starter methods for each element
     * (animateParticle, animateShape, animateRectangle).</p>
     */
    private void createDynamicAnimations() {
        // Get a reference to the foreground AnchorPane (the last AnchorPane in the
        // StackPane)
        AnchorPane foregroundPane = (AnchorPane) rootContainer.getChildren().get(2);

        // Create dynamic particles
        for (int i = 0; i < maxDynamicElements; i++) {
            Circle particle = new Circle();
            particle.setRadius(2 + RANDOM.nextDouble() * 5); // Random size between 2-7
            particle.setLayoutX(RANDOM.nextDouble() * 1150 + 25); // Random X position
            particle.setLayoutY(RANDOM.nextDouble() * 580 + 25); // Random Y position

            // Apply styling
            particle.getStyleClass().addAll("floating-particle", "glow-red");

            // Random red shade
            int red = 180 + RANDOM.nextInt(75);
            int darkRed = 80 + RANDOM.nextInt(100);
            particle.setStyle("-fx-fill: radial-gradient(center 50% 50%, radius 50%, #" +
                    String.format("%02X", red) + "2222, #" + String.format("%02X", darkRed) + "0000); " +
                    "-fx-effect: dropshadow(gaussian, #ff" + String.format("%02X", red) +
                    String.format("%02X", red) + ", " + (10 + RANDOM.nextInt(15)) + ", 0, 0, 0); " +
                    "-fx-opacity: " + (0.6 + RANDOM.nextDouble() * 0.4) + ";" +
                    "-fx-z-index: " + (250 + i) + ";");

            // Add to parent and store reference
            foregroundPane.getChildren().add(particle);
            dynamicParticles[i] = particle;

            // Create animation
            animateParticle(particle, i);
        }


        // Create polygons
        for (int i = 0; i < dynamicShapes.length; i++) {
            Polygon shape = new Polygon();

            // Create a random polygon with 3-6 points
            int sides = 3 + RANDOM.nextInt(4);
            Double[] points = new Double[sides * 2];
            double radius = 10 + RANDOM.nextDouble() * 20;
            double centerX = RANDOM.nextDouble() * 1120 + 40;
            double centerY = RANDOM.nextDouble() * 550 + 40;

            for (int j = 0; j < sides; j++) {
                double angle = 2 * Math.PI * j / sides;
                points[j * 2] = centerX + radius * Math.cos(angle);
                points[j * 2 + 1] = centerY + radius * Math.sin(angle);
            }


            shape.getPoints().addAll(points);

            // Apply styling
            shape.getStyleClass().addAll("rotating-shape", "pulsing-shape");
            int red = 100 + RANDOM.nextInt(100);
            int darkRed = 40 + RANDOM.nextInt(60);
            shape.setStyle("-fx-fill: linear-gradient(to bottom right, rgba(" + red + ", 0, 0, 0.5), rgba(" + darkRed
                    + ", 0, 0, 0.3)); " +
                    "-fx-stroke: #" + String.format("%02X", red) + "0000; " +
                    "-fx-stroke-width: " + (1 + RANDOM.nextInt(2)) + "; " +
                    "-fx-effect: dropshadow(gaussian, rgba(" + red + ", 0, 0, 0.7), " + (8 + RANDOM.nextInt(10))
                    + ", 0, 0, 0); " +
                    "-fx-z-index: " + (300 + i) + ";");
            shape.setOpacity(0.4 + RANDOM.nextDouble() * 0.4);

            // Add to parent and store reference
            foregroundPane.getChildren().add(shape);
            dynamicShapes[i] = shape;

            // Create animation
            animateShape(shape, i);
        }


        // Create rectangles
        for (int i = 0; i < dynamicRectangles.length; i++) {
            Rectangle rect = new Rectangle();
            rect.setWidth(10 + RANDOM.nextDouble() * 30);
            rect.setHeight(10 + RANDOM.nextDouble() * 30);
            rect.setLayoutX(RANDOM.nextDouble() * 1150 + 25);
            rect.setLayoutY(RANDOM.nextDouble() * 580 + 25);
            rect.setRotate(RANDOM.nextDouble() * 45);

            // Apply styling
            rect.getStyleClass().addAll("rotating-shape", "pulsing-shape");
            int red = 150 + RANDOM.nextInt(100);
            int darkRed = 60 + RANDOM.nextInt(80);
            rect.setStyle("-fx-fill: linear-gradient(to bottom right, rgba(" + red + ", " + (red / 4) + ", " + (red / 4)
                    + ", 0.6), rgba(" + darkRed + ", " + (darkRed / 6) + ", " + (darkRed / 6) + ", 0.4)); " +
                    "-fx-stroke: #" + String.format("%02X", red) + "2222; " +
                    "-fx-stroke-width: " + (RANDOM.nextDouble() * 2) + "; " +
                    "-fx-effect: dropshadow(gaussian, rgba(" + red + ", " + (red / 4) + ", " + (red / 4) + ", 0.6), "
                    + (8 + RANDOM.nextInt(8)) + ", 0, 0, 0); " +
                    "-fx-z-index: " + (330 + i) + ";");
            rect.setOpacity(0.3 + RANDOM.nextDouble() * 0.5);

            // Add to parent and store reference
            foregroundPane.getChildren().add(rect);
            dynamicRectangles[i] = rect;

            // Create animation
            animateRectangle(rect, i);
        }


        LOGGER.info("Created " + maxDynamicElements + " dynamic particles and shapes");
    }


    /**
     * Set up and start continuous translate, scale, and fade animations for a particle, staggering the start by index.
     *
     * @param particle the Circle node to animate
     * @param index    staging index used to delay the animation start; the start delay is index * 50 milliseconds
     */
    private void animateParticle(Circle particle, int index) {
        // Create X movement
        TranslateTransition moveX = new TranslateTransition(Duration.seconds(5 + RANDOM.nextDouble() * 15), particle);
        moveX.setFromX(-20 - RANDOM.nextDouble() * 50);
        moveX.setToX(20 + RANDOM.nextDouble() * 50);
        moveX.setCycleCount(Animation.INDEFINITE);
        moveX.setAutoReverse(true);
        moveX.setInterpolator(Interpolator.EASE_BOTH);

        // Create Y movement
        TranslateTransition moveY = new TranslateTransition(Duration.seconds(5 + RANDOM.nextDouble() * 10), particle);
        moveY.setFromY(-20 - RANDOM.nextDouble() * 40);
        moveY.setToY(20 + RANDOM.nextDouble() * 40);
        moveY.setCycleCount(Animation.INDEFINITE);
        moveY.setAutoReverse(true);
        moveY.setInterpolator(Interpolator.EASE_BOTH);

        // Create scale pulse
        ScaleTransition scale = new ScaleTransition(Duration.seconds(2 + RANDOM.nextDouble() * 6), particle);
        scale.setFromX(0.7);
        scale.setFromY(0.7);
        scale.setToX(1.5);
        scale.setToY(1.5);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setAutoReverse(true);
        scale.setInterpolator(Interpolator.EASE_BOTH);

        // Create fade pulse
        FadeTransition fade = new FadeTransition(Duration.seconds(3 + RANDOM.nextDouble() * 4), particle);
        double baseOpacity = particle.getOpacity();
        fade.setFromValue(baseOpacity * 0.5);
        fade.setToValue(baseOpacity);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setAutoReverse(true);
        fade.setInterpolator(Interpolator.EASE_BOTH);

        // Start all animations with small delay for each particle
        PauseTransition delay = new PauseTransition(Duration.millis(index * 50));
        delay.setOnFinished(e -> {
            moveX.play();
            moveY.play();
            scale.play();
            fade.play();
        }
);
        delay.play();
    }


    /**
     * Animates a Polygon with continuous rotation, pulsing scale, and smooth translation.
     *
     * The provided `index` determines a short start delay (index * 200ms) to stagger multiple shapes.
     *
     * @param shape the Polygon to animate
     * @param index zero-based position used to stagger the animation start time for this shape
     */
    private void animateShape(Polygon shape, int index) {
        // Create rotation
        RotateTransition rotate = new RotateTransition(Duration.seconds(10 + RANDOM.nextDouble() * 20), shape);
        rotate.setByAngle(RANDOM.nextBoolean() ? 360 : -360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);

        // Create scale pulse
        ScaleTransition scale = new ScaleTransition(Duration.seconds(4 + RANDOM.nextDouble() * 8), shape);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.5);
        scale.setToY(1.5);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setAutoReverse(true);
        scale.setInterpolator(Interpolator.EASE_BOTH);

        // Create movement
        TranslateTransition move = new TranslateTransition(Duration.seconds(15 + RANDOM.nextDouble() * 15), shape);
        move.setFromX(-30 - RANDOM.nextDouble() * 50);
        move.setFromY(-30 - RANDOM.nextDouble() * 50);
        move.setToX(30 + RANDOM.nextDouble() * 50);
        move.setToY(30 + RANDOM.nextDouble() * 50);
        move.setCycleCount(Animation.INDEFINITE);
        move.setAutoReverse(true);
        move.setInterpolator(Interpolator.EASE_BOTH);

        // Start all animations with small delay for each shape
        PauseTransition delay = new PauseTransition(Duration.millis(index * 200));
        delay.setOnFinished(e -> {
            rotate.play();
            scale.play();
            move.play();
        }
);
        delay.play();
    }


    /**
     * Animate the given rectangle with continuous rotation, scale pulsing, opacity fading, and drifting translation.
     *
     * The animations start after a staggered delay determined by the index parameter (index * 150 ms).
     *
     * @param rect  the Rectangle node to animate
     * @param index zero-based index used to stagger the animation start time for this rectangle
     */
    private void animateRectangle(Rectangle rect, int index) {
        // Create rotation
        RotateTransition rotate = new RotateTransition(Duration.seconds(8 + RANDOM.nextDouble() * 12), rect);
        rotate.setByAngle(RANDOM.nextBoolean() ? 360 : -360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);

        // Create scale pulse
        ScaleTransition scale = new ScaleTransition(Duration.seconds(5 + RANDOM.nextDouble() * 5), rect);
        scale.setFromX(0.7);
        scale.setFromY(0.7);
        scale.setToX(1.3);
        scale.setToY(1.3);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setAutoReverse(true);
        scale.setInterpolator(Interpolator.EASE_BOTH);

        // Create fade pulse
        FadeTransition fade = new FadeTransition(Duration.seconds(4 + RANDOM.nextDouble() * 6), rect);
        double baseOpacity = rect.getOpacity();
        fade.setFromValue(baseOpacity * 0.5);
        fade.setToValue(baseOpacity);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setAutoReverse(true);
        fade.setInterpolator(Interpolator.EASE_BOTH);

        // Create movement
        TranslateTransition move = new TranslateTransition(Duration.seconds(10 + RANDOM.nextDouble() * 20), rect);
        move.setFromX(-20 - RANDOM.nextDouble() * 40);
        move.setFromY(-20 - RANDOM.nextDouble() * 40);
        move.setToX(20 + RANDOM.nextDouble() * 40);
        move.setToY(20 + RANDOM.nextDouble() * 40);
        move.setCycleCount(Animation.INDEFINITE);
        move.setAutoReverse(true);
        move.setInterpolator(Interpolator.EASE_BOTH);

        // Start all animations with small delay for each rectangle
        PauseTransition delay = new PauseTransition(Duration.millis(index * 150));
        delay.setOnFinished(e -> {
            rotate.play();
            scale.play();
            fade.play();
            move.play();
        }
);
        delay.play();
    }

}
