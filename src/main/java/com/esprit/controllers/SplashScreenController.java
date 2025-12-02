package com.esprit.controllers;

import com.esprit.utils.DataSource;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for the cinematic splash screen.
 * Creates an immersive movie-theater experience while loading the application.
 */
public class SplashScreenController {

    // Loading messages for cinematic effect
    private static final String[] LOADING_MESSAGES = {
        "Preparing your cinematic experience...",
        "Loading movie database...",
        "Initializing premium features...",
        "Setting up 4K streaming...",
        "Configuring Dolby Atmos...",
        "Preparing VIP lounge...",
        "Loading exclusive content...",
        "Almost ready for showtime...",
        "Welcome to RAKCHA..."
    };
    private final Random random = new Random();
    private final List<Timeline> activeAnimations = new ArrayList<>();
    @FXML
    private StackPane rootPane;
    @FXML
    private Pane backgroundPane;
    @FXML
    private Rectangle filmGrain;
    @FXML
    private StackPane cinematicCanvas;
    @FXML
    private StackPane mediaContainer;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private Rectangle vignetteOverlay;
    @FXML
    private Pane lightRaysPane;
    @FXML
    private Rectangle lightRay1;
    @FXML
    private Rectangle lightRay2;
    @FXML
    private Rectangle lightRay3;
    @FXML
    private StackPane logoContainer;
    @FXML
    private Circle logoGlow;
    @FXML
    private Circle logoRing;
    @FXML
    private ImageView logoImage;
    @FXML
    private Pane particlePane;
    @FXML
    private Label titleLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private Rectangle loadingBarBackground;
    @FXML
    private Rectangle loadingBar;
    @FXML
    private Label loadingText;
    @FXML
    private Label loadingPercentage;
    @FXML
    private Pane floatingElementsPane;
    @FXML
    private FontIcon filmReel1;
    @FXML
    private FontIcon popcorn1;
    @FXML
    private FontIcon camera1;
    @FXML
    private FontIcon star1;
    @FXML
    private FontIcon ticket1;
    @FXML
    private FontIcon clapper1;
    @FXML
    private Rectangle fadeOverlay;
    private Stage splashStage;
    private Stage mainStage;

    /**
     * Initialize the splash screen with all cinematic animations.
     */
    @FXML
    public void initialize() {
        // Set initial states
        setupInitialStates();

        // Load the logo
        loadLogo();

        // Load cinematic background
        loadCinematicBackground();

        // Start entrance animation sequence
        Platform.runLater(this::startEntranceAnimation);
    }

    /**
     * Set initial states for all elements.
     */
    private void setupInitialStates() {
        // Hide elements initially for entrance animation
        logoContainer.setOpacity(0);
        logoContainer.setScaleX(0.5);
        logoContainer.setScaleY(0.5);

        titleLabel.setOpacity(0);
        titleLabel.setTranslateY(50);

        subtitleLabel.setOpacity(0);

        loadingBar.setWidth(0);
        loadingText.setOpacity(0);
        loadingPercentage.setOpacity(0);

        fadeOverlay.setOpacity(1);

        // Set floating elements to be invisible initially
        if (floatingElementsPane != null) {
            floatingElementsPane.setOpacity(0);
        }
    }

    /**
     * Load the application logo.
     */
    private void loadLogo() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/Logo.png"));
            if (logoImage != null && !logo.isError()) {
                logoImage.setImage(logo);
            }
        } catch (Exception e) {
            System.err.println("Could not load logo: " + e.getMessage());
        }
    }

    /**
     * Load the cinematic background image.
     */
    private void loadCinematicBackground() {
        try {
            // Try to load the cinematic background
            Image bg = new Image(getClass().getResourceAsStream("/images/splash/cinematic-bg.jpg"));
            if (backgroundImage != null && !bg.isError()) {
                backgroundImage.setImage(bg);
            }
        } catch (Exception e) {
            // Use a fallback gradient if image not found
            System.out.println("Using fallback background: " + e.getMessage());
        }
    }

    /**
     * Start the entrance animation sequence.
     */
    private void startEntranceAnimation() {
        // Create the main entrance timeline
        Timeline entranceTimeline = new Timeline();

        // Fade in from black
        KeyFrame fadeIn = new KeyFrame(Duration.millis(1000),
            new KeyValue(fadeOverlay.opacityProperty(), 0, Interpolator.EASE_OUT)
        );

        // Logo entrance with dramatic zoom
        KeyFrame logoEntrance = new KeyFrame(Duration.millis(1500),
            new KeyValue(logoContainer.opacityProperty(), 1, Interpolator.EASE_OUT),
            new KeyValue(logoContainer.scaleXProperty(), 1.1, Interpolator.EASE_OUT),
            new KeyValue(logoContainer.scaleYProperty(), 1.1, Interpolator.EASE_OUT)
        );

        KeyFrame logoSettle = new KeyFrame(Duration.millis(2000),
            new KeyValue(logoContainer.scaleXProperty(), 1, Interpolator.EASE_BOTH),
            new KeyValue(logoContainer.scaleYProperty(), 1, Interpolator.EASE_BOTH)
        );

        // Title entrance with slide up
        KeyFrame titleEntrance = new KeyFrame(Duration.millis(2200),
            new KeyValue(titleLabel.opacityProperty(), 1, Interpolator.EASE_OUT),
            new KeyValue(titleLabel.translateYProperty(), 0, Interpolator.EASE_OUT)
        );

        // Subtitle fade in
        KeyFrame subtitleEntrance = new KeyFrame(Duration.millis(2600),
            new KeyValue(subtitleLabel.opacityProperty(), 1, Interpolator.EASE_OUT)
        );

        // Loading section fade in
        KeyFrame loadingEntrance = new KeyFrame(Duration.millis(3000),
            new KeyValue(loadingText.opacityProperty(), 1, Interpolator.EASE_OUT),
            new KeyValue(loadingPercentage.opacityProperty(), 1, Interpolator.EASE_OUT)
        );

        // Floating elements fade in
        KeyFrame floatingEntrance = new KeyFrame(Duration.millis(3200),
            event -> {
                if (floatingElementsPane != null) {
                    FadeTransition fadeFloating = new FadeTransition(Duration.millis(800), floatingElementsPane);
                    fadeFloating.setToValue(0.6);
                    fadeFloating.play();
                }
            }
        );

        entranceTimeline.getKeyFrames().addAll(
            fadeIn, logoEntrance, logoSettle,
            titleEntrance, subtitleEntrance,
            loadingEntrance, floatingEntrance
        );

        entranceTimeline.setOnFinished(event -> {
            startBackgroundAnimations();
            startLoadingProcess();
        });

        activeAnimations.add(entranceTimeline);
        entranceTimeline.play();
    }

    /**
     * Start all background animations.
     */
    private void startBackgroundAnimations() {
        startLogoGlowAnimation();
        startLogoRingAnimation();
        startParticleAnimation();
        startLightRayAnimation();
        startFloatingIconsAnimation();
        startFilmGrainAnimation();
    }

    /**
     * Create pulsing glow effect on logo.
     */
    private void startLogoGlowAnimation() {
        if (logoGlow == null) return;

        Timeline glowTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(logoGlow.opacityProperty(), 0.3),
                new KeyValue(logoGlow.scaleXProperty(), 0.9),
                new KeyValue(logoGlow.scaleYProperty(), 0.9)
            ),
            new KeyFrame(Duration.millis(2000),
                new KeyValue(logoGlow.opacityProperty(), 0.6, Interpolator.EASE_BOTH),
                new KeyValue(logoGlow.scaleXProperty(), 1.1, Interpolator.EASE_BOTH),
                new KeyValue(logoGlow.scaleYProperty(), 1.1, Interpolator.EASE_BOTH)
            )
        );
        glowTimeline.setCycleCount(Animation.INDEFINITE);
        glowTimeline.setAutoReverse(true);
        activeAnimations.add(glowTimeline);
        glowTimeline.play();
    }

    /**
     * Create rotating ring animation around logo.
     */
    private void startLogoRingAnimation() {
        if (logoRing == null) return;

        RotateTransition rotate = new RotateTransition(Duration.seconds(10), logoRing);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();

        // Also pulse the ring
        Timeline ringPulse = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(logoRing.opacityProperty(), 0.3)
            ),
            new KeyFrame(Duration.millis(1500),
                new KeyValue(logoRing.opacityProperty(), 0.8, Interpolator.EASE_BOTH)
            )
        );
        ringPulse.setCycleCount(Animation.INDEFINITE);
        ringPulse.setAutoReverse(true);
        activeAnimations.add(ringPulse);
        ringPulse.play();
    }

    /**
     * Create floating particle effects around the logo.
     */
    private void startParticleAnimation() {
        if (particlePane == null) return;

        for (int i = 0; i < 20; i++) {
            createParticle();
        }
    }

    private void createParticle() {
        Circle particle = new Circle(random.nextDouble() * 3 + 1);
        particle.setFill(Color.web("#ff4444", 0.6 + random.nextDouble() * 0.4));

        // Add glow effect
        particle.setEffect(new Glow(0.8));

        // Random starting position around the logo
        double angle = random.nextDouble() * 360;
        double distance = 80 + random.nextDouble() * 100;
        double startX = 200 + Math.cos(Math.toRadians(angle)) * distance;
        double startY = 200 + Math.sin(Math.toRadians(angle)) * distance;

        particle.setLayoutX(startX);
        particle.setLayoutY(startY);

        particlePane.getChildren().add(particle);

        // Animate the particle
        animateParticle(particle);
    }

    private void animateParticle(Circle particle) {
        double duration = 3000 + random.nextDouble() * 4000;

        Timeline particleTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(particle.opacityProperty(), 0),
                new KeyValue(particle.translateYProperty(), 0)
            ),
            new KeyFrame(Duration.millis(duration * 0.2),
                new KeyValue(particle.opacityProperty(), 0.8, Interpolator.EASE_OUT)
            ),
            new KeyFrame(Duration.millis(duration * 0.8),
                new KeyValue(particle.opacityProperty(), 0.8)
            ),
            new KeyFrame(Duration.millis(duration),
                new KeyValue(particle.opacityProperty(), 0, Interpolator.EASE_IN),
                new KeyValue(particle.translateYProperty(), -50 - random.nextDouble() * 100)
            )
        );

        particleTimeline.setOnFinished(e -> {
            // Reset and restart
            particle.setTranslateY(0);
            double angle = random.nextDouble() * 360;
            double distance = 80 + random.nextDouble() * 100;
            particle.setLayoutX(200 + Math.cos(Math.toRadians(angle)) * distance);
            particle.setLayoutY(200 + Math.sin(Math.toRadians(angle)) * distance);
            animateParticle(particle);
        });

        particleTimeline.play();
    }

    /**
     * Animate the dramatic light rays.
     */
    private void startLightRayAnimation() {
        if (lightRay1 == null) return;

        animateLightRay(lightRay1, 3000, 0);
        animateLightRay(lightRay2, 4000, 500);
        animateLightRay(lightRay3, 3500, 1000);
    }

    private void animateLightRay(Rectangle ray, double duration, double delay) {
        Timeline rayTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(ray.opacityProperty(), 0)
            ),
            new KeyFrame(Duration.millis(duration * 0.3),
                new KeyValue(ray.opacityProperty(), 0.15, Interpolator.EASE_OUT)
            ),
            new KeyFrame(Duration.millis(duration * 0.7),
                new KeyValue(ray.opacityProperty(), 0.15)
            ),
            new KeyFrame(Duration.millis(duration),
                new KeyValue(ray.opacityProperty(), 0, Interpolator.EASE_IN)
            )
        );
        rayTimeline.setCycleCount(Animation.INDEFINITE);
        rayTimeline.setDelay(Duration.millis(delay));
        activeAnimations.add(rayTimeline);
        rayTimeline.play();
    }

    /**
     * Animate floating movie icons.
     */
    private void startFloatingIconsAnimation() {
        if (filmReel1 != null) animateFloatingIcon(filmReel1, 4000, 0);
        if (popcorn1 != null) animateFloatingIcon(popcorn1, 5000, 300);
        if (camera1 != null) animateFloatingIcon(camera1, 4500, 600);
        if (star1 != null) animateFloatingIcon(star1, 3800, 900);
        if (ticket1 != null) animateFloatingIcon(ticket1, 5500, 200);
        if (clapper1 != null) animateFloatingIcon(clapper1, 4200, 500);
    }

    private void animateFloatingIcon(FontIcon icon, double duration, double delay) {
        // Floating up and down animation
        Timeline floatTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(icon.translateYProperty(), 0)
            ),
            new KeyFrame(Duration.millis(duration),
                new KeyValue(icon.translateYProperty(), -20 - random.nextDouble() * 15, Interpolator.EASE_BOTH)
            )
        );
        floatTimeline.setCycleCount(Animation.INDEFINITE);
        floatTimeline.setAutoReverse(true);
        floatTimeline.setDelay(Duration.millis(delay));
        activeAnimations.add(floatTimeline);
        floatTimeline.play();

        // Subtle rotation
        RotateTransition rotate = new RotateTransition(Duration.millis(duration * 2), icon);
        rotate.setByAngle(10);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setAutoReverse(true);
        rotate.setDelay(Duration.millis(delay));
        rotate.play();
    }

    /**
     * Create film grain effect animation.
     */
    private void startFilmGrainAnimation() {
        if (filmGrain == null) return;

        Timeline grainTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(filmGrain.opacityProperty(), 0.03)
            ),
            new KeyFrame(Duration.millis(100),
                new KeyValue(filmGrain.opacityProperty(), 0.05)
            ),
            new KeyFrame(Duration.millis(200),
                new KeyValue(filmGrain.opacityProperty(), 0.02)
            ),
            new KeyFrame(Duration.millis(300),
                new KeyValue(filmGrain.opacityProperty(), 0.04)
            )
        );
        grainTimeline.setCycleCount(Animation.INDEFINITE);
        activeAnimations.add(grainTimeline);
        grainTimeline.play();
    }

    /**
     * Start the loading process with database initialization.
     */
    private void startLoadingProcess() {
        CompletableFuture.runAsync(() -> {
            try {
                // Simulate loading stages
                for (int i = 0; i <= 100; i++) {
                    final int progress = i;
                    final int messageIndex = Math.min(i / 12, LOADING_MESSAGES.length - 1);

                    Platform.runLater(() -> {
                        updateLoadingProgress(progress, LOADING_MESSAGES[messageIndex]);
                    });

                    // Initialize database at 30%
                    if (i == 30) {
                        DataSource.getInstance().createTablesIfNotExists();
                    }

                    // Variable speed loading for dramatic effect
                    int delay = i < 20 ? 30 : (i < 70 ? 40 : (i < 90 ? 50 : 80));
                    Thread.sleep(delay);
                }

                // Brief pause at 100% before transition
                Thread.sleep(500);

                Platform.runLater(this::transitionToMainApp);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Platform.runLater(this::transitionToMainApp);
            }
        });
    }

    /**
     * Update the loading progress bar and text.
     */
    private void updateLoadingProgress(int progress, String message) {
        double width = (progress / 100.0) * 400;

        Timeline progressTimeline = new Timeline(
            new KeyFrame(Duration.millis(50),
                new KeyValue(loadingBar.widthProperty(), width, Interpolator.EASE_OUT)
            )
        );
        progressTimeline.play();

        loadingPercentage.setText(progress + "%");
        loadingText.setText(message);
    }

    /**
     * Transition to the main application with cinematic fade.
     */
    private void transitionToMainApp() {
        // Stop all animations
        activeAnimations.forEach(Timeline::stop);

        // Dramatic fade out
        Timeline fadeOut = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(fadeOverlay.opacityProperty(), 0)
            ),
            new KeyFrame(Duration.millis(800),
                new KeyValue(rootPane.scaleXProperty(), 1.05, Interpolator.EASE_IN),
                new KeyValue(rootPane.scaleYProperty(), 1.05, Interpolator.EASE_IN),
                new KeyValue(fadeOverlay.opacityProperty(), 1, Interpolator.EASE_IN)
            )
        );

        fadeOut.setOnFinished(event -> loadMainApplication());
        fadeOut.play();
    }

    /**
     * Load and display the main application.
     */
    private void loadMainApplication() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            if (mainStage != null) {
                mainStage.setScene(scene);
                mainStage.setTitle("RAKCHA - Cinema Experience");
                mainStage.show();
            }

            if (splashStage != null) {
                splashStage.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load main application: " + e.getMessage());
        }
    }

    /**
     * Set the splash stage reference.
     */
    public void setSplashStage(Stage stage) {
        this.splashStage = stage;
    }

    /**
     * Set the main stage reference.
     */
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }
}
