package com.esprit.controllers.users;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.users.User;
import com.esprit.services.users.UserService;

import animatefx.animation.*;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ProfileController {
    private static final Logger LOGGER = Logger.getLogger(ProfileController.class.getName());
    // Using a more reliable GIF URL
    private static final String DEFAULT_PROFILE_GIF = "https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExcDJ1dGw4M2g5NXlyYmgweHhkeG95a2d4bTdqbXR0NWpwbGJqNjN6eCZlcD12MV9pbnRlcm5hbF9naWZzX2dpZklkJmN0PWc/jUwpNzg9IcyrK/giphy.gif";
    private static final String PROFILE_IMAGES_DIR = "profile-images";
    private Timeline pulseAnimation;

    @FXML
    public AnchorPane leftPane;
    User user;
    @FXML
    private TextField adresseTextField;
    @FXML
    private DatePicker dateDeNaissanceDatePicker;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField phoneNumberTextField;
    @FXML
    private ImageView photoDeProfilImageView;
    @FXML
    private Circle imageCircle;
    @FXML
    private Button importPhotoButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button signOutButton;

    @FXML
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize() {
        configureImageCircle();
        ensureProfileImageDirectoryExists();
    }

    private void configureImageCircle() {
        loadAndSetImage(DEFAULT_PROFILE_GIF);
    }

    private void loadAndSetImage(String imageUrl) {
        try {
            // Create image with explicit size and background loading disabled
            Image image = new Image(imageUrl, 140, // width
                    140, // height
                    true, // preserve ratio
                    true, // smooth
                    false); // no background loading for immediate error detection

            if (!image.isError()) {
                // Stop any existing animations
                if (pulseAnimation != null) {
                    pulseAnimation.stop();
                }

                // Clear existing effects
                imageCircle.setEffect(null);

                // Set the new image
                imageCircle.setFill(new ImagePattern(image));

                // Add subtle glow effect
                DropShadow glow = new DropShadow();
                glow.setColor(Color.valueOf("#e74141"));
                glow.setRadius(10);
                glow.setSpread(0.4);
                imageCircle.setEffect(glow);
            } else {
                throw new IllegalArgumentException("Image loading failed");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load image: " + imageUrl, e);
            useGradientFallback();
        }
    }

    private void useGradientFallback() {
        imageCircle.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.valueOf("#e74141")), new Stop(1, Color.valueOf("#333333"))));

        // Add glow effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.valueOf("#e74141"));
        glow.setRadius(20);
        imageCircle.setEffect(glow);

        // Add pulse animation as fallback
        new Pulse(imageCircle).setCycleCount(-1).play();
    }

    private void ensureProfileImageDirectoryExists() {
        try {
            Path profileImagesPath = Paths.get(PROFILE_IMAGES_DIR);
            if (!Files.exists(profileImagesPath)) {
                Files.createDirectories(profileImagesPath);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not create profile images directory", e);
        }
    }

    @FXML
    /**
     * Sets the Data value.
     *
     * @param setData
     *            the value to set
     */
    public void setData(final User user) {
        this.user = user;
        if (null != user.getFirstName()) {
            this.firstNameTextField.setText(user.getFirstName());
        }
        if (null != user.getLastName()) {
            this.lastNameTextField.setText(user.getLastName());
        }
        if (null != user.getAddress()) {
            this.adresseTextField.setText(user.getAddress());
        }
        if (null != user.getEmail()) {
            this.emailTextField.setText(user.getEmail());
        }
        if (!String.valueOf(0).equals(user.getPhoneNumber())) {
            this.phoneNumberTextField.setText(String.valueOf(user.getPhoneNumber()));
        }
        if (null != user.getBirthDate()) {
            this.dateDeNaissanceDatePicker.setValue(user.getBirthDate().toLocalDate());
        }
        if (user.getPhotoDeProfil() != null && !user.getPhotoDeProfil().isEmpty()) {
            try {
                if (user.getPhotoDeProfil().startsWith("file:")) {
                    // Handle local files
                    Path imagePath = Paths.get(URI.create(user.getPhotoDeProfil()));
                    if (Files.exists(imagePath)) {
                        loadAndSetImage(imagePath.toUri().toString());
                    } else {
                        loadAndSetImage(DEFAULT_PROFILE_GIF);
                    }
                } else {
                    // Handle URLs directly
                    loadAndSetImage(user.getPhotoDeProfil());
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error loading profile image, using default", e);
                loadAndSetImage(DEFAULT_PROFILE_GIF);
            }
        } else {
            loadAndSetImage(DEFAULT_PROFILE_GIF);
        }
    }

    @FXML
    /**
     * Performs deleteAccount operation.
     *
     * @return the result of the operation
     */
    public void deleteAccount(final ActionEvent event) throws IOException {
        final Stage stage = (Stage) this.firstNameTextField.getScene().getWindow();
        final User user = (User) stage.getUserData();
        final UserService userService = new UserService();
        userService.delete(user);
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/SignUp.fxml"));
        final Parent root = loader.load();
        stage.setScene(new Scene(root));
    }

    @FXML
    /**
     * Performs modifyAccount operation.
     *
     * @return the result of the operation
     */
    public void modifyAccount(final ActionEvent event) {
        try {
            if (validateInputs()) {
                updateUserFromFields();
                UserService userService = new UserService();
                userService.update(user);
                showAlert("Success", "Profile updated successfully", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating profile", e);
            showAlert("Error", "Could not update profile", Alert.AlertType.ERROR);
        }
    }

    private boolean validateInputs() {
        if (emailTextField.getText().isEmpty() || !emailTextField.getText().contains("@")) {
            showAlert("Validation Error", "Please enter a valid email", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void updateUserFromFields() {
        user.setFirstName(firstNameTextField.getText());
        user.setLastName(lastNameTextField.getText());
        user.setAddress(adresseTextField.getText());
        user.setEmail(emailTextField.getText());
        user.setPassword(passwordTextField.getText());
        if (dateDeNaissanceDatePicker.getValue() != null) {
            user.setBirthDate(Date.valueOf(dateDeNaissanceDatePicker.getValue()));
        }
        try {
            user.setPhoneNumber(phoneNumberTextField.getText());
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid phone number format");
        }
    }

    @FXML
    /**
     * Performs signOut operation.
     *
     * @return the result of the operation
     */
    public void signOut(final ActionEvent event) throws IOException {
        final Stage stage = (Stage) this.emailTextField.getScene().getWindow();
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/SignUp.fxml"));
        final Parent root = loader.load();
        stage.setScene(new Scene(root));
    }

    /**
     * Sets the LeftPane value.
     *
     * @param setLeftPane
     *            the value to set
     */
    public void setLeftPane(final AnchorPane leftPane) {
        this.leftPane.getChildren().clear();
        this.leftPane.getChildren().add(leftPane);
    }

    @FXML
    /**
     * Performs importPhoto operation.
     *
     * @return the result of the operation
     */
    public void importPhoto() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Profile Picture");
            fileChooser.getExtensionFilters()
                    .addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

            File selectedFile = fileChooser.showOpenDialog(imageCircle.getScene().getWindow());
            if (selectedFile != null) {
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destinationPath = Paths.get(PROFILE_IMAGES_DIR, fileName);
                Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                String imageUri = destinationPath.toUri().toString();
                loadAndSetImage(imageUri);

                if (user != null) {
                    user.setPhotoDeProfil(imageUri);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error importing photo", e);
            showAlert("Error", "Could not import photo", Alert.AlertType.ERROR);
            loadAndSetImage(DEFAULT_PROFILE_GIF);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
