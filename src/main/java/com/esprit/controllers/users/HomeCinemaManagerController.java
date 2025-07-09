package com.esprit.controllers.users;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.films.Actor;
import com.esprit.models.films.Category;
import com.esprit.models.films.Film;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.users.User;
import com.esprit.services.films.ActorService;
import com.esprit.services.films.CategoryService;
import com.esprit.services.films.FilmService;
import com.esprit.services.cinemas.MovieSessionService;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for the Cinema Manager Home Dashboard interface.
 * Provides an overview of cinema operations and quick access to manager
 * functions.
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class HomeCinemaManagerController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(HomeCinemaManagerController.class.getName());

    // Services
    private final FilmService filmService = new FilmService();
    private final ActorService actorService = new ActorService();
    private final CategoryService categoryService = new CategoryService();
    private final MovieSessionService sessionService = new MovieSessionService();

    // FXML Elements
    @FXML
    private StackPane rootContainer;
    @FXML
    private TextField searchField;
    @FXML
    private ScrollPane mainScrollPane;
    @FXML
    private VBox todaySessionsContainer;
    @FXML
    private VBox activityContainer;

    // Statistics Labels
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label movieCountLabel;
    @FXML
    private Label sessionCountLabel;
    @FXML
    private Label actorCountLabel;
    @FXML
    private Label categoryCountLabel;

    // Animation elements
    @FXML
    private Circle particle1, particle2;
    @FXML
    private Polygon shape1, shape2;
    @FXML
    private Rectangle shape3;
    @FXML
    private Circle shape4;
    @FXML
    private AnchorPane particlesContainer;

    /**
     * Initializes the controller and loads dashboard data.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadDashboardStatistics();
            loadTodaySessions();
            loadRecentActivity();
            startBackgroundAnimations();
            
            // Set welcome message using user data from stage
            Timeline delayedInit = new Timeline(new KeyFrame(Duration.millis(100), e -> {
                try {
                    if (rootContainer != null && rootContainer.getScene() != null) {
                        Stage stage = (Stage) rootContainer.getScene().getWindow();
                        if (stage != null && stage.getUserData() instanceof User) {
                            User currentUser = (User) stage.getUserData();
                            welcomeLabel.setText("Welcome, " + currentUser.getFirstName() + " "
                                    + currentUser.getLastName() + "!");
                        } else {
                            welcomeLabel.setText("Welcome, Cinema Manager!");
                        }
                    } else {
                        welcomeLabel.setText("Welcome, Cinema Manager!");
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Error setting welcome message: " + ex.getMessage(), ex);
                    welcomeLabel.setText("Welcome, Cinema Manager!");
                }
            }));
            delayedInit.play();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing cinema manager dashboard: " + e.getMessage(), e);
        }
    }

    /**
     * Loads and displays dashboard statistics.
     */
    private void loadDashboardStatistics() {
        try {
            // Load movie count
            List<Film> films = filmService.read();
            movieCountLabel.setText(String.valueOf(films.size()));

            // Load actor count
            List<Actor> actors = actorService.read();
            actorCountLabel.setText(String.valueOf(actors.size()));

            // Load category count
            List<Category> categories = categoryService.read();
            categoryCountLabel.setText(String.valueOf(categories.size()));

            // Load session count
            List<MovieSession> sessions = sessionService.read();
            sessionCountLabel.setText(String.valueOf(sessions.size()));

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading dashboard statistics: " + e.getMessage(), e);
            // Set default values in case of error
            movieCountLabel.setText("0");
            actorCountLabel.setText("0");
            categoryCountLabel.setText("0");
            sessionCountLabel.setText("0");
        }
    }

    /**
     * Loads today's movie sessions.
     */
    private void loadTodaySessions() {
        try {
            todaySessionsContainer.getChildren().clear();

            // Add sample session items for today
            addSessionItem("The Matrix", "10:00 AM", "Hall 1");
            addSessionItem("Inception", "2:00 PM", "Hall 2");
            addSessionItem("The Dark Knight", "6:00 PM", "Hall 1");
            addSessionItem("Interstellar", "9:00 PM", "Hall 3");

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading today's sessions: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a session item to the sessions container.
     */
    private void addSessionItem(String movieTitle, String time, String hall) {
        HBox sessionItem = new HBox(10);
        sessionItem
                .setStyle("-fx-padding: 10; -fx-background-color: rgba(60, 60, 60, 0.5); -fx-background-radius: 10;");

        Label movieLabel = new Label(movieTitle);
        movieLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-text-fill: #ff6666; -fx-font-size: 12px;");

        Label hallLabel = new Label(hall);
        hallLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        sessionItem.getChildren().addAll(movieLabel, spacer, timeLabel, hallLabel);
        todaySessionsContainer.getChildren().add(sessionItem);
    }

    /**
     * Loads recent cinema activity.
     */
    private void loadRecentActivity() {
        try {
            activityContainer.getChildren().clear();

            // Add sample activity items
            addActivityItem("New movie session added: Avatar", "10 minutes ago");
            addActivityItem("Actor profile updated: Tom Hanks", "30 minutes ago");
            addActivityItem("Category created: Sci-Fi", "1 hour ago");
            addActivityItem("Movie statistics updated", "2 hours ago");

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading recent activity: " + e.getMessage(), e);
        }
    }

    /**
     * Adds an activity item to the activity container.
     */
    private void addActivityItem(String activity, String timestamp) {
        HBox activityItem = new HBox(10);
        activityItem
                .setStyle("-fx-padding: 10; -fx-background-color: rgba(60, 60, 60, 0.5); -fx-background-radius: 10;");

        Label activityLabel = new Label(activity);
        activityLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label timestampLabel = new Label(timestamp);
        timestampLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        activityItem.getChildren().addAll(activityLabel, spacer, timestampLabel);
        activityContainer.getChildren().add(activityItem);
    }

    /**
     * Starts background particle animations.
     */
    private void startBackgroundAnimations() {
        if (particle1 != null && particle2 != null) {
            // Floating animation for particles
            Timeline particleAnimation = new Timeline(
                    new KeyFrame(Duration.seconds(0),
                            new KeyValue(particle1.layoutXProperty(), particle1.getLayoutX())),
                    new KeyFrame(Duration.seconds(3),
                            new KeyValue(particle1.layoutXProperty(), particle1.getLayoutX() + 50)),
                    new KeyFrame(Duration.seconds(6),
                            new KeyValue(particle1.layoutXProperty(), particle1.getLayoutX())));
            particleAnimation.setCycleCount(Timeline.INDEFINITE);
            particleAnimation.play();

            Timeline particle2Animation = new Timeline(
                    new KeyFrame(Duration.seconds(0),
                            new KeyValue(particle2.layoutYProperty(), particle2.getLayoutY())),
                    new KeyFrame(Duration.seconds(4),
                            new KeyValue(particle2.layoutYProperty(), particle2.getLayoutY() - 30)),
                    new KeyFrame(Duration.seconds(8),
                            new KeyValue(particle2.layoutYProperty(), particle2.getLayoutY())));
            particle2Animation.setCycleCount(Timeline.INDEFINITE);
            particle2Animation.play();
        }

        // Add fade-in animation to root container
        if (rootContainer != null) {
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), rootContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }

    // Navigation Action Handlers

    @FXML
    void manageMovies(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/films/Film.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading movie management interface: " + e.getMessage(), e);
        }
    }

    @FXML
    void manageSessions(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/cinemas/DashboardResponsable.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading session management interface: " + e.getMessage(), e);
        }
    }

    @FXML
    void manageActors(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/films/Actor.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading actor management interface: " + e.getMessage(), e);
        }
    }

    @FXML
    void viewStatistics(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/Statistique.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading statistics interface: " + e.getMessage(), e);
        }
    }

    // Additional methods referenced in FXML
    @FXML
    void manageCinemas(ActionEvent event) {
        manageSessions(event); // Redirect to session management which includes cinema management
    }

    @FXML
    void viewAnalytics(ActionEvent event) {
        viewStatistics(event); // Redirect to statistics view
    }

    @FXML
    void generateReports(ActionEvent event) {
        viewStatistics(event); // Redirect to statistics view for now
    }
}
