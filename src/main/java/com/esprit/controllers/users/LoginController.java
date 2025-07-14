package com.esprit.controllers.users;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class LoginController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
    private static final Random RANDOM = new Random();

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label emailErrorLabel;
    @FXML
    private TextField emailTextField;
    @FXML
    private Hyperlink forgetPasswordEmailHyperlink;
    @FXML
    private Hyperlink forgetPasswordHyperlink;
    @FXML
    private Label passwordErrorLabel;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Button signInButton;
    @FXML
    private Button signUpButton;
    @FXML
    private Button googleSIgnInButton;
    @FXML
    private Button microsoftSignInButton;
    @FXML
    private AnchorPane loginAnchorPane;

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

    // Featured movie elements
    @FXML
    private ImageView featuredMovieImage;
    @FXML
    private Label featuredMovieTitle;

    // Updated movie poster URLs with new TMDB images
    private final String[] moviePosters = {
            "https://image.tmdb.org/t/p/w500/jWZLvnBAjZvpbIJGV1GKK2JqfK3.jpg", // Dune
            "https://image.tmdb.org/t/p/w500/kAVRgw7GgK1CfYEJq8ME6EvRIgU.jpg", // Joker
            "https://image.tmdb.org/t/p/w500/6KErczPBROQty7QoIsaa6wJYXZi.jpg", // Oppenheimer
            "https://image.tmdb.org/t/p/w500/u7SeO6Y42P7VCTWLhpnL96cyOqd.jpg", // Blade Runner 2049
            "https://image.tmdb.org/t/p/w500/vqzNJRH4YyquRiWxCCOH0aXggHI.jpg", // Shutter Island
            "https://image.tmdb.org/t/p/w500/8Vt6mWEReuy4Of61Lnj5Xj704m8.jpg", // Spider-Man: Across the Spider-Verse
            "https://image.tmdb.org/t/p/w500/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg", // The Batman
            "https://image.tmdb.org/t/p/w500/uJYYizSuA9Y3DCs1fFuCc1yGdZP.jpg", // The Departed
            "https://image.tmdb.org/t/p/w500/m2FNRngyJMyxLatBMJR8pbeG2v.jpg", // Arrival
            "https://image.tmdb.org/t/p/w500/q6WREviZ5IYA4mwWKXpvZMvVBGN.jpg", // John Wick
            "https://image.tmdb.org/t/p/w500/br4MHru7Z0x1Uz6CGu8ph6AAlmy.jpg", // Top Gun: Maverick
            "https://image.tmdb.org/t/p/w500/7Bl4CF3EHz9xZYuVB55hf6SD8hP.jpg" // Everything Everywhere All at Once
    };

    // Movie titles corresponding to the posters
    private final String[] movieTitles = {
            "Dune",
            "Joker",
            "Oppenheimer",
            "Blade Runner 2049",
            "Shutter Island",
            "Spider-Man: Across the Spider-Verse",
            "The Batman",
            "The Departed",
            "Arrival",
            "John Wick",
            "Top Gun: Maverick",
            "Everything Everywhere All at Once"
    };

    private int currentImageIndex = 0;
    private Timeline featuredMovieSwitchTimeline;
    private Timeline particleCreationTimeline;

    // Arrays to store dynamic particles and shapes
    private Circle[] dynamicParticles;
    private Polygon[] dynamicShapes;
    private Rectangle[] dynamicRectangles;
    private int maxDynamicElements = 15; // Number of dynamic elements to create

    /**
     * @param event
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @FXML
    void signInWithGoogle(final ActionEvent event) throws IOException, ExecutionException, InterruptedException {
        try {
            stopAllAnimations();
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/VerifyWithGoogle.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.googleSIgnInButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            LoginController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @param event
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @FXML
    void signInWithMicrosoft(final ActionEvent event) throws IOException, ExecutionException, InterruptedException {
        try {
            stopAllAnimations();
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/VerifyWithMicrosoft.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.microsoftSignInButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            LoginController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @param event
     * @throws IOException
     */
    @FXML
    void login(final ActionEvent event) throws IOException {
        final UserService userService = new UserService();
        final User user = userService.login(this.emailTextField.getText(), this.passwordTextField.getText());
        if (null != user) {
            try {
                final TrayNotification trayNotification = new TrayNotification("users", "login successful",
                        Notifications.SUCCESS);
                trayNotification.showAndDismiss(new Duration(3000));

                final Stage stage = (Stage) this.signInButton.getScene().getWindow();

                // Redirect to role-specific home screen
                String fxmlPath = "";
                String windowTitle = "RAKCHA";

                switch (user.getRole().toLowerCase()) {
                    case "admin":
                        fxmlPath = "/ui/users/HomeAdmin.fxml";
                        windowTitle = "RAKCHA - Admin Dashboard";
                        break;
                    case "client":
                        fxmlPath = "/ui/users/HomeClient.fxml";
                        windowTitle = "RAKCHA - Client Home";
                        break;
                    case "responsable de cinema":
                        fxmlPath = "/ui/users/HomeCinemaManager.fxml";
                        windowTitle = "RAKCHA - Cinema Manager";
                        break;
                    default:
                        // Fallback to profile page for unknown roles
                        fxmlPath = "/ui/users/Profile.fxml";
                        windowTitle = "RAKCHA - Profile";
                        LOGGER.log(Level.WARNING,
                                "Unknown user role: " + user.getRole() + ". Redirecting to profile page.");
                        break;
                }

                final FXMLLoader loader = new FXMLLoader(this.getClass().getResource(fxmlPath));
                final Parent root = loader.load();

                // Create new scene and set it to the stage
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle(windowTitle);

                // Set user data on the stage so controllers can access it via getUserData()
                stage.setUserData(user);

                // Stop animations when navigating away
                stopAllAnimations();

                LOGGER.log(Level.INFO,
                        "User " + user.getEmail() + " with role " + user.getRole() + " logged in successfully");

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error during login process", e);
                throw new IOException("Failed to complete login process", e);
            }
        } else {
            final Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid email or password", ButtonType.CLOSE);
            alert.show();
        }
    }

    /**
     * @param event
     * @throws IOException
     */
    @FXML
    void switchToSignUp(ActionEvent event) throws IOException {
        try {
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/SignUp.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));

            // Stop animations when navigating away
            stopAllAnimations();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error switching to signup view", e);
            throw e;
        }
    }

    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL location, final ResourceBundle resources) {
        this.forgetPasswordHyperlink.setOnAction(new EventHandler<>() {
            @Override
            public void handle(final ActionEvent event) {
                try {
                    final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/smsadmin.fxml"));
                    final Parent root = loader.load();
                    final Stage stage = (Stage) forgetPasswordHyperlink.getScene().getWindow();
                    stage.setScene(new Scene(root));
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.forgetPasswordEmailHyperlink.setOnAction(new EventHandler<>() {
            @Override
            public void handle(final ActionEvent event) {
                try {
                    final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/maillogin.fxml"));
                    final Parent root = loader.load();
                    final Stage stage = (Stage) forgetPasswordEmailHyperlink.getScene()
                            .getWindow();
                    stage.setScene(new Scene(root));
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Ensure signUpButton has its event handler
        signUpButton.setOnAction(event -> {
            try {
                switchToSignUp(event);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error switching to signup view", e);
            }
        });

        // Initialize arrays for dynamic elements
        dynamicParticles = new Circle[maxDynamicElements];
        dynamicShapes = new Polygon[maxDynamicElements / 3];
        dynamicRectangles = new Rectangle[maxDynamicElements / 3];

        // Initialize animations with a slight delay to ensure FXML is fully loaded
        Timeline delayedInit = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            LOGGER.info("Starting animation initialization...");
            initializeFeaturedMovie();
            initializeParticleAnimation();
            initializeShapeAnimation();
            createDynamicAnimations();
            LOGGER.info("Animation initialization completed.");
        }));
        delayedInit.play();
    }

    /**
     * Creates dynamic animated elements on the screen
     */
    private void createDynamicAnimations() {
        // Get a reference to the foreground AnchorPane (the last AnchorPane in the
        // StackPane)
        AnchorPane foregroundPane = (AnchorPane) ((StackPane) anchorPane.getScene().getRoot()).getChildren().get(2);

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
     * Creates animations for a particle
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
        });
        delay.play();
    }

    /**
     * Creates animations for a shape
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
        });
        delay.play();
    }

    /**
     * Creates animations for a rectangle
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
        });
        delay.play();
    }

    /**
     * Initializes the featured movie display with image switching
     */
    private void initializeFeaturedMovie() {
        if (featuredMovieImage != null && featuredMovieTitle != null) {
            // Start with The Batman (index 6 in our new array)
            int featuredIndex = 6; // The Batman
            try {
                Image featuredImage = new Image(moviePosters[featuredIndex], true);
                featuredMovieImage.setImage(featuredImage);
                featuredMovieTitle.setText(movieTitles[featuredIndex]);

                // Add some subtle animation to the featured poster
                ScaleTransition posterScale = new ScaleTransition(Duration.seconds(5), featuredMovieImage);
                posterScale.setFromX(1.0);
                posterScale.setFromY(1.0);
                posterScale.setToX(1.03);
                posterScale.setToY(1.03);
                posterScale.setCycleCount(Animation.INDEFINITE);
                posterScale.setAutoReverse(true);
                posterScale.setInterpolator(Interpolator.EASE_BOTH);
                posterScale.play();

                // Create image switching timeline - switches featured movie every 8 seconds
                featuredMovieSwitchTimeline = new Timeline();
                featuredMovieSwitchTimeline.getKeyFrames()
                        .add(new KeyFrame(Duration.seconds(8), e -> switchFeaturedMovie()));
                featuredMovieSwitchTimeline.setCycleCount(Animation.INDEFINITE);
                featuredMovieSwitchTimeline.play();

                LOGGER.info("Initialized featured movie: " + movieTitles[featuredIndex]);
            } catch (Exception e) {
                LOGGER.warning("Failed to load initial featured movie: " + e.getMessage());
            }
        } else {
            LOGGER.warning("Featured movie elements are null");
        }
    }

    /**
     * Switches the featured movie display with fade transitions
     */
    private void switchFeaturedMovie() {
        // Increment the current image index
        currentImageIndex = (currentImageIndex + 1) % moviePosters.length;

        // Update the featured movie display
        if (featuredMovieImage != null && featuredMovieTitle != null) {
            // Use a featured index that cycles through our movies
            int featuredIndex = (currentImageIndex + 3) % moviePosters.length;
            String featuredPosterUrl = moviePosters[featuredIndex];
            String featuredTitle = movieTitles[featuredIndex];

            // Create fade transition for smooth change
            FadeTransition posterFade = new FadeTransition(Duration.millis(400), featuredMovieImage);
            posterFade.setFromValue(1.0);
            posterFade.setToValue(0.3);

            FadeTransition titleFade = new FadeTransition(Duration.millis(400), featuredMovieTitle);
            titleFade.setFromValue(1.0);
            titleFade.setToValue(0.3);

            ParallelTransition fadeOut = new ParallelTransition(posterFade, titleFade);

            fadeOut.setOnFinished(e -> {
                try {
                    // Update image and title
                    featuredMovieImage.setImage(new Image(featuredPosterUrl, true));
                    featuredMovieTitle.setText(featuredTitle);

                    // Fade back in
                    FadeTransition posterFadeIn = new FadeTransition(Duration.millis(400), featuredMovieImage);
                    posterFadeIn.setFromValue(0.3);
                    posterFadeIn.setToValue(1.0);

                    FadeTransition titleFadeIn = new FadeTransition(Duration.millis(400), featuredMovieTitle);
                    titleFadeIn.setFromValue(0.3);
                    titleFadeIn.setToValue(1.0);

                    ParallelTransition fadeIn = new ParallelTransition(posterFadeIn, titleFadeIn);
                    fadeIn.play();
                } catch (Exception ex) {
                    LOGGER.warning("Error updating featured movie: " + ex.getMessage());
                }
            });

            fadeOut.play();

            LOGGER.info("Updated featured movie to: " + featuredTitle);
        }
    }

    /**
     * Initializes the particle animation for floating red particles
     */
    private void initializeParticleAnimation() {
        Circle[] particles = { particle1, particle2, particle3, particle4, particle5, particle6,
                particle7, particle8, particle9, particle10, particle11, particle12 };

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
            } else {
                LOGGER.warning("particle" + (i + 1) + " is null");
            }
        }
    }

    /**
     * Initializes the geometric shape animations
     */
    private void initializeShapeAnimation() {
        // Array of all existing shapes
        Object[] shapes = { shape1, shape2, shape3, shape4, shape5, shape6 };

        for (int i = 0; i < shapes.length; i++) {
            if (shapes[i] != null) {
                if (shapes[i] instanceof Polygon) {
                    animatePolygon((Polygon) shapes[i], i);
                } else if (shapes[i] instanceof Rectangle) {
                    animateRectangleShape((Rectangle) shapes[i], i);
                } else if (shapes[i] instanceof Circle) {
                    animateCircleShape((Circle) shapes[i], i);
                }
            }
        }
    }

    /**
     * Animates a polygon shape
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
     * Animates a rectangle shape
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
     * Animates a circle shape
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
     * Stops all animations when leaving the login screen
     */
    private void stopAllAnimations() {
        if (featuredMovieSwitchTimeline != null) {
            featuredMovieSwitchTimeline.stop();
        }

        if (particleCreationTimeline != null) {
            particleCreationTimeline.stop();
        }

        // Clean up dynamic elements by removing them from the scene
        if (dynamicParticles != null) {
            AnchorPane foregroundPane = null;
            try {
                foregroundPane = (AnchorPane) ((StackPane) anchorPane.getScene().getRoot()).getChildren().get(2);
            } catch (Exception e) {
                LOGGER.warning("Could not access foreground pane for cleanup: " + e.getMessage());
            }

            if (foregroundPane != null) {
                for (Circle particle : dynamicParticles) {
                    if (particle != null) {
                        try {
                            foregroundPane.getChildren().remove(particle);
                        } catch (Exception e) {
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
                foregroundPane = (AnchorPane) ((StackPane) anchorPane.getScene().getRoot()).getChildren().get(2);
            } catch (Exception e) {
                LOGGER.warning("Could not access foreground pane for cleanup: " + e.getMessage());
            }

            if (foregroundPane != null) {
                for (Polygon shape : dynamicShapes) {
                    if (shape != null) {
                        try {
                            foregroundPane.getChildren().remove(shape);
                        } catch (Exception e) {
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
                foregroundPane = (AnchorPane) ((StackPane) anchorPane.getScene().getRoot()).getChildren().get(2);
            } catch (Exception e) {
                LOGGER.warning("Could not access foreground pane for cleanup: " + e.getMessage());
            }

            if (foregroundPane != null) {
                for (Rectangle rect : dynamicRectangles) {
                    if (rect != null) {
                        try {
                            foregroundPane.getChildren().remove(rect);
                        } catch (Exception e) {
                            LOGGER.warning("Could not remove rectangle: " + e.getMessage());
                        }
                    }
                }
            }
        }

        LOGGER.info("All animations stopped and dynamic elements cleaned up");
    }
}
