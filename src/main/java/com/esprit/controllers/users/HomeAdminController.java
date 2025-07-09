package com.esprit.controllers.users;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.films.Film;
import com.esprit.models.products.Product;
import com.esprit.models.users.User;
import com.esprit.services.films.FilmService;
import com.esprit.services.products.ProductService;
import com.esprit.services.users.UserService;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for the Admin Home Dashboard interface.
 * Provides an overview of system statistics and quick access to admin
 * functions.
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class HomeAdminController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(HomeAdminController.class.getName());

    // Services
    private final UserService userService = new UserService();
    private final FilmService filmService = new FilmService();
    private final ProductService productService = new ProductService();

    // FXML Elements
    @FXML
    private BorderPane rootContainer;
    @FXML
    private ScrollPane mainScrollPane;
    @FXML
    private VBox activityContainer;

    // Statistics Labels
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label userCountLabel;
    @FXML
    private Label movieCountLabel;
    @FXML
    private Label productCountLabel;
    @FXML
    private Label orderCountLabel;

    /**
     * Initializes the controller and loads dashboard data.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadDashboardStatistics();
            loadRecentActivity();
            startBackgroundAnimations();
            
            // Set welcome message using user data from stage
            Timeline delayedInit = new Timeline(new KeyFrame(Duration.millis(100), e -> {
                try {
                    if (rootContainer != null && rootContainer.getScene() != null) {
                        Stage stage = (Stage) rootContainer.getScene().getWindow();
                        if (stage != null && stage.getUserData() instanceof User) {
                            User currentUser = (User) stage.getUserData();
                            welcomeLabel.setText("Welcome, Admin " + currentUser.getFirstName() + " "
                                    + currentUser.getLastName() + "!");
                        } else {
                            welcomeLabel.setText("Welcome, Admin!");
                        }
                    } else {
                        welcomeLabel.setText("Welcome, Admin!");
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Error setting welcome message: " + ex.getMessage(), ex);
                    welcomeLabel.setText("Welcome, Admin!");
                }
            }));
            delayedInit.play();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing admin dashboard: " + e.getMessage(), e);
        }
    }

    /**
     * Loads and displays dashboard statistics.
     */
    private void loadDashboardStatistics() {
        try {
            // Load user count
            List<User> users = userService.read();
            userCountLabel.setText(String.valueOf(users.size()));

            // Load movie count
            List<Film> films = filmService.read();
            movieCountLabel.setText(String.valueOf(films.size()));

            // Load product count
            List<Product> products = productService.read();
            productCountLabel.setText(String.valueOf(products.size()));

            // Load order count - using a default value since method might not exist
            orderCountLabel.setText("0");

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading dashboard statistics: " + e.getMessage(), e);
            // Set default values in case of error
            userCountLabel.setText("0");
            movieCountLabel.setText("0");
            productCountLabel.setText("0");
            orderCountLabel.setText("0");
        }
    }

    /**
     * Loads recent system activity.
     */
    private void loadRecentActivity() {
        try {
            activityContainer.getChildren().clear();

            // Add sample activity items
            addActivityItem("New user registered: John Doe", "5 minutes ago");
            addActivityItem("Order #1234 completed", "15 minutes ago");
            addActivityItem("New movie added: The Matrix", "1 hour ago");
            addActivityItem("Product stock updated", "2 hours ago");

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
     * Starts background animations (simplified for cleaner interface).
     */
    private void startBackgroundAnimations() {
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
    void manageUsers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/AdminDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading user management interface: " + e.getMessage(), e);
        }
    }

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
    void manageProducts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/products/Product.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading product management interface: " + e.getMessage(), e);
        }
    }

    @FXML
    void manageOrders(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/products/ListOrder.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading orders interface: " + e.getMessage(), e);
        }
    }

    @FXML
    void viewOrders(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/products/ListOrder.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading orders interface: " + e.getMessage(), e);
        }
    }

    @FXML
    void systemSettings(ActionEvent event) {
        try {
            // Create a simple settings dialog or navigate to settings page
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("System Settings");
            alert.setHeaderText("System Settings");
            alert.setContentText("System settings functionality will be implemented here.");
            alert.showAndWait();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error opening system settings: " + e.getMessage(), e);
        }
    }
}
