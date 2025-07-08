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
import com.esprit.utils.CloudinaryStorage;

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
    // Using a more expressive avatar placeholder GIF
    private static final String DEFAULT_PROFILE_GIF = "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExeGJhOTk0dWkzdnVtbGoxcmNnMG5ndXVsd3BpcWIwcGV6ZDV3MDBsYiZlcD12MV9pbnRlcm5hbF9naWZzX2dpZklkJmN0PWc/q217GUnfKAmJlFcjBX/giphy.gif";
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
        LOGGER.info("Initializing ProfileController");

        // Ensure the UI components exist
        if (imageCircle == null) {
            LOGGER.warning("Image circle is null in initialize()");
        } else {
            LOGGER.info("Image circle is available, configuring...");
        }

        if (photoDeProfilImageView == null) {
            LOGGER.warning("Profile image view is null in initialize()");
        } else {
            LOGGER.info("Profile image view is available, configuring...");
        }

        configureImageCircle();
        configureImageView();

        // Set a default image
        loadAndSetImage(DEFAULT_PROFILE_GIF);
    }

    private void configureImageCircle() {
        if (imageCircle != null) {
            LOGGER.info("Configuring image circle...");
            // Add any specific circle configuration here
            imageCircle.setSmooth(true);
        }
    }

    private void configureImageView() {
        if (photoDeProfilImageView != null) {
            LOGGER.info("Configuring image view...");
            // Set initial circular clip for the ImageView
            photoDeProfilImageView.setPreserveRatio(true);

            // This will remain hidden but needs to be properly configured
            photoDeProfilImageView.setOpacity(0.0);
            photoDeProfilImageView.setManaged(true);

            double width = photoDeProfilImageView.getFitWidth();
            double height = photoDeProfilImageView.getFitHeight();
            double radius = Math.min(width, height) / 2;

            Circle clip = new Circle(width / 2, height / 2, radius);
            photoDeProfilImageView.setClip(clip);
        }
    }

    /**
     * @param imageUrl
     */
    private void loadAndSetImage(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                LOGGER.warning("Empty or null image URL provided, using default");
                useGradientFallback();
                return;
            }

            LOGGER.info("Loading image from URL: " + imageUrl);

            // Normalize the URL if needed (handle special cases)
            if (!imageUrl.startsWith("http") && !imageUrl.startsWith("file:")) {
                LOGGER.info("URL doesn't have a protocol, assuming file: " + imageUrl);
                imageUrl = "file:" + imageUrl;
            }

            // Create image with explicit size and background loading disabled
            LOGGER.info("Creating image object for URL: " + imageUrl);
            Image image = new Image(imageUrl,
                    140, // width
                    140, // height
                    true, // preserve ratio
                    true, // smooth
                    false); // no background loading for immediate error detection

            // Wait for the image to load and check for errors
            image.progressProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() == 1.0) {
                    LOGGER.info("Image loaded: " + !image.isError() + ", Error: " + image.getException());
                    if (image.isError() && image.getException() != null) {
                        LOGGER.warning("Image error details: " + image.getException().getMessage());
                    }
                }
            });

            if (!image.isError()) {
                // Stop any existing animations
                if (pulseAnimation != null) {
                    pulseAnimation.stop();
                }

                // Clear existing effects
                imageCircle.setEffect(null);

                // Set the new image to the Circle using ImagePattern
                LOGGER.info("Creating image pattern for circle");
                ImagePattern pattern = new ImagePattern(image);
                imageCircle.setFill(pattern);
                LOGGER.info("Image pattern set to circle successfully");

                // Also set the image to the ImageView if available
                if (photoDeProfilImageView != null) {
                    LOGGER.info("Setting image to ImageView");
                    photoDeProfilImageView.setImage(image);
                    // Make the ImageView circular by setting a clip
                    Circle clip = new Circle(photoDeProfilImageView.getFitWidth() / 2,
                            photoDeProfilImageView.getFitHeight() / 2,
                            Math.min(photoDeProfilImageView.getFitWidth(),
                                    photoDeProfilImageView.getFitHeight()) / 2);
                    photoDeProfilImageView.setClip(clip);
                    photoDeProfilImageView.setPreserveRatio(true);
                    LOGGER.info("Image set to ImageView successfully");
                } else {
                    LOGGER.warning("photoDeProfilImageView is null");
                }

                // Add subtle glow effect
                DropShadow glow = new DropShadow();
                glow.setColor(Color.valueOf("#e74141"));
                glow.setRadius(10);
                glow.setSpread(0.4);
                imageCircle.setEffect(glow);
            } else {
                LOGGER.warning("Image loading failed for URL: " + imageUrl);
                if (image.getException() != null) {
                    LOGGER.warning("Error details: " + image.getException().getMessage());
                }
                throw new IllegalArgumentException("Image loading failed");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load image: " + imageUrl, e);
            useGradientFallback();
        }
    }

    private void useGradientFallback() {
        // Create a more visually appealing gradient fallback
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.valueOf("#e74141")),
                new Stop(0.5, Color.valueOf("#F1644B")),
                new Stop(1, Color.valueOf("#2D3A66")));

        imageCircle.setFill(gradient);

        // Add a more prominent glow effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.valueOf("#e74141"));
        glow.setRadius(15);
        glow.setSpread(0.5);
        imageCircle.setEffect(glow);

        // Add pulse animation as fallback to draw attention
        new Pulse(imageCircle).setCycleCount(-1).play();

        LOGGER.info("Applied gradient fallback for profile image");
    }

    @FXML
    /**
     * Sets the user data in the form fields and loads the profile image.
     *
     * @param user the user object containing the data to display
     */
    public void setData(final User user) {
        if (user == null) {
            LOGGER.warning("Null user provided to setData");
            loadAndSetImage(DEFAULT_PROFILE_GIF);
            return;
        }

        this.user = user;
        LOGGER.info("Setting user data for: " + user.getEmail());

        // Set text fields
        setFieldIfNotNull(firstNameTextField, user.getFirstName());
        setFieldIfNotNull(lastNameTextField, user.getLastName());
        setFieldIfNotNull(adresseTextField, user.getAddress());
        setFieldIfNotNull(emailTextField, user.getEmail());

        if (user.getPhoneNumber() != null && !user.getPhoneNumber().equals("0")) {
            this.phoneNumberTextField.setText(user.getPhoneNumber());
        }

        if (user.getBirthDate() != null) {
            this.dateDeNaissanceDatePicker.setValue(user.getBirthDate().toLocalDate());
        }

        // Handle profile image loading
        handleProfileImage(user);
    }

    /**
     * Sets a text field value if the provided value is not null.
     * 
     * @param field The TextField to set
     * @param value The value to set
     */
    private void setFieldIfNotNull(TextField field, String value) {
        if (field != null && value != null) {
            field.setText(value);
        }
    }

    /**
     * Handles loading the profile image from the user object.
     * 
     * @param user The user containing the profile image URL
     */
    private void handleProfileImage(User user) {
        if (user.getPhotoDeProfil() != null && !user.getPhotoDeProfil().isEmpty()) {
            LOGGER.info("Loading profile image from: " + user.getPhotoDeProfil());
            try {
                String imageUrl = user.getPhotoDeProfil();

                // Verify if it's a valid URL or path
                if (imageUrl.startsWith("file:")) {
                    // Handle local files
                    Path imagePath = Paths.get(URI.create(imageUrl));
                    if (Files.exists(imagePath)) {
                        loadAndSetImage(imageUrl);
                    } else {
                        LOGGER.warning("Local file doesn't exist: " + imagePath);
                        loadAndSetImage(DEFAULT_PROFILE_GIF);
                    }
                } else if (imageUrl.startsWith("http")) {
                    // Handle URLs directly (like Cloudinary)
                    loadAndSetImage(imageUrl);
                } else {
                    // Try to interpret as a local path
                    File file = new File(imageUrl);
                    if (file.exists()) {
                        loadAndSetImage(file.toURI().toString());
                    } else {
                        // Just try with the raw string
                        loadAndSetImage(imageUrl);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error loading profile image, using default", e);
                loadAndSetImage(DEFAULT_PROFILE_GIF);
            }
        } else {
            LOGGER.info("No profile image set, using default");
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

    /**
     * @return boolean
     */
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
     * @param leftPane the value to set
     */
    public void setLeftPane(final AnchorPane leftPane) {
        this.leftPane.getChildren().clear();
        this.leftPane.getChildren().add(leftPane);
    }

    @FXML
    /**
     * Allows the user to import a profile photo from their file system.
     * The image will be uploaded to Cloudinary and set as the user's profile
     * picture.
     */
    public void importPhoto() {
        try {
            LOGGER.info("Starting profile photo import process");

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Profile Picture");
            fileChooser.getExtensionFilters()
                    .addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

            File selectedFile = fileChooser.showOpenDialog(imageCircle.getScene().getWindow());
            if (selectedFile != null) {
                LOGGER.info("User selected file: " + selectedFile.getAbsolutePath());

                try {
                    // Use CloudinaryStorage to upload the image
                    LOGGER.info("Uploading image to Cloudinary");
                    CloudinaryStorage cloudinaryStorage = CloudinaryStorage.getInstance();
                    String imageUrl = cloudinaryStorage.uploadImage(selectedFile);

                    LOGGER.info("Image successfully uploaded to Cloudinary: " + imageUrl);

                    // Load and display the image from Cloudinary URL
                    loadAndSetImage(imageUrl);

                    // Update user profile image URL
                    if (user != null) {
                        user.setPhotoDeProfil(imageUrl);

                        // Save the updated user to the database
                        try {
                            UserService userService = new UserService();
                            userService.update(user);
                            LOGGER.info("User profile updated with new image URL");
                            showAlert("Success", "Profile picture updated successfully", Alert.AlertType.INFORMATION);
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Failed to update user profile in database", e);
                            showAlert("Warning", "Image uploaded but profile not saved. Please save your profile.",
                                    Alert.AlertType.WARNING);
                        }
                    } else {
                        LOGGER.warning("User object is null, cannot update profile image URL");
                        showAlert("Warning", "Image uploaded but couldn't update your profile. Try again later.",
                                Alert.AlertType.WARNING);
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
                    showAlert("Upload Error", "Could not upload image to cloud storage: " + e.getMessage(),
                            Alert.AlertType.ERROR);
                    loadAndSetImage(DEFAULT_PROFILE_GIF);
                }
            } else {
                LOGGER.info("User cancelled file selection");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error importing photo", e);
            showAlert("Error", "Could not import photo: " + e.getMessage(), Alert.AlertType.ERROR);
            loadAndSetImage(DEFAULT_PROFILE_GIF);
        }
    }

    /**
     * @param title
     * @param content
     * @param type
     */
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
