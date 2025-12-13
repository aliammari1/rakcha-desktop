package com.esprit.controllers.users;

import animatefx.animation.FadeIn;
import animatefx.animation.Pulse;
import animatefx.animation.ZoomIn;
import com.esprit.controllers.SidebarController;
import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import com.esprit.utils.CloudinaryStorage;
import com.esprit.utils.SessionManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final Random random = new Random();
    @FXML
    public AnchorPane leftPane;
    User user;
    private Timeline pulseAnimation;
    // Root container
    @FXML
    private StackPane rootContainer;
    // Sidebar pane from fx:include
    @FXML
    private VBox sidebarPane;
    // Particles for animation
    @FXML
    private AnchorPane particlePane;
    @FXML
    private Circle particle1;
    @FXML
    private Circle particle2;
    @FXML
    private Circle particle3;
    @FXML
    private Circle particle4;
    @FXML
    private Circle particle5;
    // Background shapes
    @FXML
    private Polygon shape1;
    @FXML
    private Circle bgShape2;
    @FXML
    private Rectangle bgShape3;
    @FXML
    private Polygon bgShape4;
    // User info labels
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userEmailLabel;
    @FXML
    private Label memberSinceLabel;
    @FXML
    private Label aiInsightLabel;
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
    private PasswordField passwordTextField;
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
        setData(SessionManager.getCurrentUser());
        LOGGER.info("Initializing ProfileController with cinematic styling");

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

        // Initialize cinematic animations
        initializeParticleAnimations();
        initializeBackgroundAnimations();

        // Set a default image
        loadAndSetImage(DEFAULT_PROFILE_GIF);

        // Play entrance animation for root container
        if (rootContainer != null) {
            new FadeIn(rootContainer).setSpeed(0.5).play();
        }
    }

    /**
     * Initializes floating particle animations for the cinematic effect.
     */
    private void initializeParticleAnimations() {
        Circle[] particles = { particle1, particle2, particle3, particle4, particle5 };

        for (Circle particle : particles) {
            if (particle != null) {
                // Create floating animation for each particle
                createFloatingAnimation(particle);
            }
        }
    }

    /**
     * Creates a smooth floating animation for a particle.
     */
    private void createFloatingAnimation(Circle particle) {
        // Random parameters for natural movement
        double durationSeconds = 4 + random.nextDouble() * 4; // 4-8 seconds
        double moveX = 20 + random.nextDouble() * 40; // 20-60 pixels
        double moveY = 15 + random.nextDouble() * 30; // 15-45 pixels

        TranslateTransition translate = new TranslateTransition(Duration.seconds(durationSeconds), particle);
        translate.setByX(random.nextBoolean() ? moveX : -moveX);
        translate.setByY(random.nextBoolean() ? moveY : -moveY);
        translate.setCycleCount(Animation.INDEFINITE);
        translate.setAutoReverse(true);
        translate.play();

        // Add pulsing opacity effect
        Timeline opacityTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(particle.opacityProperty(), 0.4)),
                new KeyFrame(Duration.seconds(durationSeconds / 2), new KeyValue(particle.opacityProperty(), 0.9)),
                new KeyFrame(Duration.seconds(durationSeconds), new KeyValue(particle.opacityProperty(), 0.4)));
        opacityTimeline.setCycleCount(Animation.INDEFINITE);
        opacityTimeline.play();
    }

    /**
     * Initializes background shape animations.
     */
    private void initializeBackgroundAnimations() {
        // Animate shape1 (diamond)
        if (shape1 != null) {
            TranslateTransition translate1 = new TranslateTransition(Duration.seconds(8), shape1);
            translate1.setByX(30);
            translate1.setByY(-20);
            translate1.setCycleCount(Animation.INDEFINITE);
            translate1.setAutoReverse(true);
            translate1.play();
        }

        // Animate bgShape2 (circle)
        if (bgShape2 != null) {
            TranslateTransition translate2 = new TranslateTransition(Duration.seconds(6), bgShape2);
            translate2.setByX(-25);
            translate2.setByY(35);
            translate2.setCycleCount(Animation.INDEFINITE);
            translate2.setAutoReverse(true);
            translate2.play();
        }

        // Animate bgShape3 (rectangle)
        if (bgShape3 != null) {
            TranslateTransition translate3 = new TranslateTransition(Duration.seconds(7), bgShape3);
            translate3.setByX(-30);
            translate3.setByY(-25);
            translate3.setCycleCount(Animation.INDEFINITE);
            translate3.setAutoReverse(true);
            translate3.play();
        }

        // Animate bgShape4 (polygon)
        if (bgShape4 != null) {
            TranslateTransition translate4 = new TranslateTransition(Duration.seconds(9), bgShape4);
            translate4.setByX(35);
            translate4.setByY(15);
            translate4.setCycleCount(Animation.INDEFINITE);
            translate4.setAutoReverse(true);
            translate4.play();
        }
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

        if (!user.getPhoneNumber().isEmpty()) {
            this.phoneNumberTextField.setText(user.getPhoneNumber());
        }

        if (user.getBirthDate() != null) {
            this.dateDeNaissanceDatePicker.setValue(user.getBirthDate().toLocalDate());
        }

        // Update header labels with user info
        updateUserInfoLabels(user);

        // Generate AI insight based on user
        updateAIInsight(user);

        // Configure sidebar with current user
        configureSidebar(user);

        // Handle profile image loading
        handleProfileImage(user);

        // Play entrance animation for content
        playContentEntranceAnimations();
    }

    /**
     * Updates the user info labels in the header.
     */
    private void updateUserInfoLabels(User user) {
        if (userNameLabel != null) {
            String displayName = "Welcome";
            if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                displayName = "Welcome, " + user.getFirstName();
            } else if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                displayName = "Welcome, " + user.getLastName();
            }
            userNameLabel.setText(displayName);
        }

        if (userEmailLabel != null && user.getEmail() != null) {
            userEmailLabel.setText(user.getEmail());
        }

        if (memberSinceLabel != null) {
            // Format member since date (using current date as placeholder if no creation
            // date)
            java.time.LocalDate memberDate = java.time.LocalDate.now();
            memberSinceLabel.setText("Member since " + memberDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        }
    }

    /**
     * Updates the AI insight label with personalized content.
     */
    private void updateAIInsight(User user) {
        if (aiInsightLabel == null)
            return;

        // AI-powered insights based on user profile
        String[] insights = {
                "Based on your profile, you might enjoy exploring new cinema experiences! Try our VIP seating for your next movie.",
                "Your viewing preferences suggest you love action and sci-fi. Check out our upcoming blockbuster releases!",
                "We noticed you haven't updated your preferences in a while. Complete your profile for better recommendations!",
                "You're a valued member! Unlock exclusive discounts by keeping your profile up to date.",
                "Based on trending movies in your area, we think you'd love the new thriller releases this month!"
        };

        // Select a random insight for now (in real app, this would use actual user
        // data)
        aiInsightLabel.setText(insights[random.nextInt(insights.length)]);
    }

    /**
     * Configures the sidebar with the current user information.
     * Note: The sidebar is already instantiated by fx:include in the FXML.
     * We just need to get its controller and set the user.
     */
    private void configureSidebar(User user) {
        if (user == null) {
            LOGGER.warning("User is null, cannot configure sidebar");
            return;
        }

        // Schedule on JavaFX application thread to ensure scene is fully initialized
        javafx.application.Platform.runLater(() -> {
            try {
                if (sidebarPane != null) {
                    // The fx:include with fx:id="sidebarPane" should have a controller
                    // Get it from the scene's root node properties if available
                    Object controller = sidebarPane.getProperties().get("fx:controller");

                    if (controller instanceof SidebarController) {
                        SidebarController sidebarController = (SidebarController) controller;
                        sidebarController.setCurrentUser(user);
                        LOGGER.info("Sidebar configured successfully with user role: " + user.getRole());
                    } else {
                        LOGGER.info("Sidebar will configure itself on initialization");
                    }
                } else {
                    LOGGER.warning("sidebarPane is null");
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error configuring sidebar with user", e);
            }
        });
    }

    /**
     * Plays entrance animations for content sections.
     */
    private void playContentEntranceAnimations() {
        // Add slight delay between animations for staggered effect
        javafx.application.Platform.runLater(() -> {
            if (imageCircle != null) {
                new ZoomIn(imageCircle).setSpeed(0.8).play();
            }
        });
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
        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
            LOGGER.info("Loading profile image from: " + user.getProfilePictureUrl());
            try {
                String imageUrl = user.getProfilePictureUrl();

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
        final User user = SessionManager.getCurrentUser();
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
        user.setPasswordHash(passwordTextField.getText());
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
                        user.setProfilePictureUrl(imageUrl);

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

    // ============= QUICK ACTION NAVIGATION METHODS =============

    /**
     * Navigate to Ticket History screen.
     */
    @FXML
    void goToTicketHistory(javafx.scene.input.MouseEvent event) {
        navigateToScreen("/ui/tickets/TicketHistory.fxml", "Ticket History");
    }

    /**
     * Navigate to Watch History / Series Watch Progress screen.
     */
    @FXML
    void goToWatchHistory(javafx.scene.input.MouseEvent event) {
        navigateToScreen("/ui/series/SeriesWatchProgress.fxml", "Watch History");
    }

    /**
     * Navigate to Favorites screen.
     */
    @FXML
    void goToFavorites(javafx.scene.input.MouseEvent event) {
        navigateToScreen("/ui/series/ListFavoris.fxml", "Favorites");
    }

    /**
     * Navigate to Account Settings screen.
     */
    @FXML
    void goToAccountSettings(javafx.scene.input.MouseEvent event) {
        navigateToScreen("/ui/users/AccountSettings.fxml", "Account Settings");
    }

    /**
     * Navigate to Notifications screen.
     */
    @FXML
    void goToNotifications(javafx.scene.input.MouseEvent event) {
        navigateToScreen("/ui/users/UserNotifications.fxml", "Notifications");
    }

    /**
     * Navigate to Wishlist screen.
     */
    @FXML
    void goToWishlist(javafx.scene.input.MouseEvent event) {
        navigateToScreen("/ui/shop/ProductWishlist.fxml", "Wishlist");
    }

    /**
     * Navigate to User Review History screen.
     */
    @FXML
    void goToReviewHistory(javafx.scene.input.MouseEvent event) {
        navigateToScreen("/ui/reviews/UserReviewHistory.fxml", "Review History");
    }

    /**
     * Helper method to navigate to a screen.
     */
    private void navigateToScreen(String fxmlPath, String screenName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            LOGGER.info("Navigated to " + screenName);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating to " + screenName, e);
            showAlert("Navigation Error", "Could not open " + screenName + ": " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }
}
