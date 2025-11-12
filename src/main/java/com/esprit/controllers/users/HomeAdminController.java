package com.esprit.controllers.users;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.films.Film;
import com.esprit.models.products.Product;
import com.esprit.models.users.User;
import com.esprit.models.series.Series;
import com.esprit.models.cinemas.Cinema;
import com.esprit.services.films.FilmService;
import com.esprit.services.products.ProductService;
import com.esprit.services.users.UserService;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.services.series.IServiceSeriesImpl;
import com.esprit.services.cinemas.CinemaService;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
 * Controller for the Admin Home Dashboard interface with advanced animations
 * and data visualization.
 * Provides an overview of system statistics, real-time monitoring, and quick
 * access to admin functions.
 * Features similar complexity to HomeClient with enhanced admin-specific
 * functionality.
 * 
 * @author RAKCHA Team
 * @version 2.0.0
 * @since 1.0.0
 */
public class HomeAdminController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(HomeAdminController.class.getName());

    // Services
    private final UserService userService = new UserService();
    private final FilmService filmService = new FilmService();
    private final ProductService productService = new ProductService();
    private final IServiceSeriesImpl seriesService = new IServiceSeriesImpl();
    private final CinemaService cinemaService = new CinemaService();

    // Main FXML Elements
    @FXML
    private StackPane rootContainer;
    @FXML
    private ScrollPane mainScrollPane;

    // Header Elements
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label systemStatusLabel;
    @FXML
    private TextField quickSearchField;

    // Statistics Cards
    @FXML
    private GridPane statisticsGrid;
    @FXML
    private VBox userStatsCard;
    @FXML
    private VBox movieStatsCard;
    @FXML
    private VBox productStatsCard;
    @FXML
    private VBox orderStatsCard;
    @FXML
    private VBox seriesStatsCard;
    @FXML
    private VBox cinemaStatsCard;

    // Statistics Labels
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label totalMoviesLabel;
    @FXML
    private Label totalProductsLabel;
    @FXML
    private Label totalOrdersLabel;
    @FXML
    private Label totalSeriesLabel;
    @FXML
    private Label totalCinemasLabel;

    // Growth indicators
    @FXML
    private Label userGrowthLabel;
    @FXML
    private Label movieGrowthLabel;
    @FXML
    private Label productGrowthLabel;
    @FXML
    private Label orderGrowthLabel;
    @FXML
    private Label seriesGrowthLabel;
    @FXML
    private Label cinemaGrowthLabel;

    // Activity and Recent Data
    @FXML
    private VBox activityContainer;
    @FXML
    private VBox recentUsersContainer;
    @FXML
    private ScrollPane recentUsersScrollPane;
    @FXML
    private VBox recentContentContainer;
    @FXML
    private ScrollPane recentContentScrollPane;
    @FXML
    private VBox systemLogsContainer;
    @FXML
    private ScrollPane systemLogsScrollPane;

    // Quick Actions - Buttons
    @FXML
    private Button manageUsersBtn;
    @FXML
    private Button manageMoviesBtn;
    @FXML
    private Button viewReportsBtn;
    @FXML
    private Button manageOrdersBtn;
    @FXML
    private Button manageProductsBtn;
    @FXML
    private Button settingsBtn;

    // Chart and Analytics Containers
    @FXML
    private StackPane analyticsChartContainer;
    @FXML
    private VBox performanceMetricsContainer;
    @FXML
    private VBox contentContainer; // General content container
    @FXML
    private VBox mainContainer; // Main content container
    @FXML
    private VBox quickActionsContainer; // Quick actions container

    // Animation Elements
    @FXML
    private AnchorPane particlesContainer;
    @FXML
    private Circle particle1, particle2, particle3, particle4, particle5, particle6;
    @FXML
    private Polygon shape1, shape2, shape5;
    @FXML
    private Rectangle shape3, shape6;
    @FXML
    private Circle shape4;

    // Animation and Data Management
    private Timeline clockUpdateTimeline;
    private Timeline statsUpdateTimeline;
    private Timeline activityUpdateTimeline;
    private Timeline particleAnimationTimeline;
    private List<Circle> dynamicParticles;
    private List<javafx.scene.Node> dynamicShapes;
    private Random random = new Random();

    // Data Lists
    private List<User> recentUsers;
    private List<Film> recentFilms;
    private List<Product> recentProducts;
    private List<Series> recentSeries;
    private List<Cinema> recentCinemas;

    // Analytics Data
    private long totalUsers = 0;
    private long totalMovies = 0;
    private long totalProducts = 0;
    private long totalSeries = 0;
    private long totalCinemas = 0;
    private long totalOrders = 0;

    /**
     * Initializes the controller with complex UI setup and advanced animations.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info("Initializing Advanced Admin Dashboard...");

        try {
            // Initialize data structures
            initializeDataStructures();

            // Configure scrollpanes for proper visibility
            configureScrollPanes();

            // Load and display dashboard statistics
            loadComprehensiveStatistics();

            // Setup welcome message and user data
            setupAdvancedWelcomeMessage();

            // Load recent activity and system data
            loadRecentSystemActivity();
            loadRecentUsers();
            loadRecentContent(); // Now available with proper FXML container
            loadSystemLogs(); // Now available with proper FXML container

            // Setup real-time updates
            setupRealTimeUpdates();

            // Setup advanced animations
            setupAdvancedAnimations();
            createDynamicParticles();
            createDynamicShapes();

            // Setup quick search functionality
            setupQuickSearchFunctionality();

            // Apply styling and effects
            applyAdvancedStyling();

            // Setup interactive elements
            setupInteractiveElements();

            LOGGER.info("Advanced Admin Dashboard initialized successfully");
        }
 catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing advanced admin dashboard: " + e.getMessage(), e);
        }

    }


    /**
     * Initialize data structures and collections
     */
    private void initializeDataStructures() {
        dynamicParticles = new ArrayList<>();
        dynamicShapes = new ArrayList<>();
        recentUsers = new ArrayList<>();
        recentFilms = new ArrayList<>();
        recentProducts = new ArrayList<>();
        recentSeries = new ArrayList<>();
        recentCinemas = new ArrayList<>();
    }


    /**
     * Configure ScrollPanes for proper scrollbar visibility and behavior
     */
    private void configureScrollPanes() {
        // Configure main scroll pane
        if (mainScrollPane != null) {
            mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            mainScrollPane.setFitToWidth(true);
            mainScrollPane.setFitToHeight(false);
            mainScrollPane.setPannable(true);
            mainScrollPane.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-background: transparent;" +
                            "-fx-focus-color: transparent;" +
                            "-fx-faint-focus-color: transparent;" +
                            "");
        }


        // Configure recent users scroll pane
        if (recentUsersScrollPane != null) {
            recentUsersScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            recentUsersScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            recentUsersScrollPane.setFitToWidth(true);
            recentUsersScrollPane.setFitToHeight(false);
            recentUsersScrollPane.setPannable(true);
            recentUsersScrollPane.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-background: transparent;" +
                            "-fx-focus-color: transparent;" +
                            "-fx-faint-focus-color: transparent;");
        }


        // Configure recent content scroll pane
        if (recentContentScrollPane != null) {
            recentContentScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            recentContentScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            recentContentScrollPane.setFitToWidth(true);
            recentContentScrollPane.setFitToHeight(false);
            recentContentScrollPane.setPannable(true);
            recentContentScrollPane.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-background: transparent;" +
                            "-fx-focus-color: transparent;" +
                            "-fx-faint-focus-color: transparent;");
        }


        // Configure system logs scroll pane
        if (systemLogsScrollPane != null) {
            systemLogsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            systemLogsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            systemLogsScrollPane.setFitToWidth(true);
            systemLogsScrollPane.setFitToHeight(false);
            systemLogsScrollPane.setPannable(true);
            systemLogsScrollPane.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-background: transparent;" +
                            "-fx-focus-color: transparent;" +
                            "-fx-faint-focus-color: transparent;");
        }

    }


    /**
     * Setup advanced welcome message with user context and system status
     */
    private void setupAdvancedWelcomeMessage() {
        Timeline delayedInit = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            try {
                if (rootContainer != null && rootContainer.getScene() != null) {
                    Stage stage = (Stage) rootContainer.getScene().getWindow();
                    if (stage != null && stage.getUserData() instanceof User) {
                        User currentUser = (User) stage.getUserData();
                        welcomeLabel.setText("Welcome back, Admin " + currentUser.getFirstName() + "!");
                    }
 else {
                        welcomeLabel.setText("Welcome back, System Administrator!");
                    }

                }
 else {
                    welcomeLabel.setText("Welcome to RAKCHA Admin Dashboard!");
                }


                // Update system status
                if (systemStatusLabel != null) {
                    systemStatusLabel.setText("SYSTEM OPERATIONAL");
                    systemStatusLabel.getStyleClass().add("pulsing-indicator");
                }

            }
 catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Error setting welcome message: " + ex.getMessage(), ex);
                welcomeLabel.setText("Welcome to RAKCHA Admin Dashboard!");
            }

        }
));
        delayedInit.play();
    }


    /**
     * Load comprehensive system statistics with enhanced data visualization
     */
    private void loadComprehensiveStatistics() {
        try {
            PageRequest pageRequest = new PageRequest(0, 100);
            // Load user statistics
            Page<User> users = userService.read(pageRequest);
            totalUsers = users.getTotalElements();
            updateStatCard(totalUsersLabel, totalUsers, userGrowthLabel, calculateGrowthPercentage("users"));

            // Load movie statistics
            Page<Film> films = filmService.read(pageRequest);
            totalMovies = films.getTotalElements();
            updateStatCard(totalMoviesLabel, totalMovies, movieGrowthLabel, calculateGrowthPercentage("movies"));

            // Load product statistics
            Page<Product> products = productService.read(pageRequest);
            totalProducts = products.getTotalElements();
            updateStatCard(totalProductsLabel, totalProducts, productGrowthLabel,
                    calculateGrowthPercentage("products"));

            // Load series statistics
            try {
                Page<Series> series = seriesService.read(pageRequest);
                totalSeries = series.getTotalElements();
                updateStatCard(totalSeriesLabel, totalSeries, seriesGrowthLabel, 0);
            }
 catch (Exception e) {
                totalSeries = 0;
                if (totalSeriesLabel != null)
                    totalSeriesLabel.setText("0");
            }


            // Load cinema statistics
            try {
                Page<Cinema> cinemas = cinemaService.read(pageRequest);
                totalCinemas = cinemas.getTotalElements();
                updateStatCard(totalCinemasLabel, totalCinemas, cinemaGrowthLabel, 0);
            }
 catch (Exception e) {
                totalCinemas = 0;
                if (totalCinemasLabel != null)
                    totalCinemasLabel.setText("0");
            }


            // Calculate orders (simulated for now)
            totalOrders = calculateTotalOrders();
            updateStatCard(totalOrdersLabel, totalOrders, orderGrowthLabel, calculateGrowthPercentage("orders"));

        }
 catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading comprehensive statistics: " + e.getMessage(), e);
            setDefaultStatistics();
        }

    }


    /**
     * Update stat card with animated number counting and growth indicators
     */
    private void updateStatCard(Label countLabel, long value, Label growthLabel, double growthPercentage) {
        if (countLabel != null) {
            // Animate number counting
            animateNumberCount(countLabel, 0, value, Duration.seconds(2));

            // Update growth indicator
            if (growthLabel != null) {
                String growthText = String.format("%+.1f%%", growthPercentage);
                growthLabel.setText(growthText);

                if (growthPercentage > 0) {
                    growthLabel.setStyle("-fx-text-fill: #00ff7f; -fx-font-weight: bold;");
                }
 else if (growthPercentage < 0) {
                    growthLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-weight: bold;");
                }
 else {
                    growthLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-weight: bold;");
                }

            }

        }

    }


    /**
     * Animate number counting effect for statistics
     */
    private void animateNumberCount(Label label, long startValue, long endValue, Duration duration) {
        if (label == null)
            return;

        Timeline timeline = new Timeline();
        long difference = endValue - startValue;

        for (int i = 0; i <= 20; i++) {
            long currentValue = startValue + (difference * i / 20);
            KeyFrame keyFrame = new KeyFrame(
                    duration.multiply((double) i / 20),
                    e -> {
                        label.setText(String.valueOf(currentValue));
                        if (currentValue == endValue) {
                            label.getStyleClass().add("counter-animation");
                        }

                    }
);
            timeline.getKeyFrames().add(keyFrame);
        }


        timeline.play();
    }


    /**
     * Calculate growth percentage (simulated with random data for demo)
     */
    private double calculateGrowthPercentage(String metric) {
        // In a real implementation, this would compare with previous period data
        return (random.nextDouble() - 0.5) * 20; // Random growth between -10% and +10%
    }


    /**
     * Calculate total orders (simulated)
     */
    private long calculateTotalOrders() {
        // In real implementation, this would query order service
        return totalProducts * 3 + random.nextLong(50);
    }


    /**
     * Set default statistics in case of error
     */
    private void setDefaultStatistics() {
        if (totalUsersLabel != null)
            totalUsersLabel.setText("0");
        if (totalMoviesLabel != null)
            totalMoviesLabel.setText("0");
        if (totalProductsLabel != null)
            totalProductsLabel.setText("0");
        if (totalOrdersLabel != null)
            totalOrdersLabel.setText("0");
        if (totalSeriesLabel != null)
            totalSeriesLabel.setText("0");
        if (totalCinemasLabel != null)
            totalCinemasLabel.setText("0");
    }


    /**
     * Load recent system activity with enhanced details
     */
    private void loadRecentSystemActivity() {
        try {
            if (activityContainer != null) {
                activityContainer.getChildren().clear();

                // Add real-time activity items with timestamps
                String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

                addEnhancedActivityItem("🔐 Admin dashboard accessed", currentTime, "success");
                addEnhancedActivityItem("📊 Statistics refreshed", getTimeAgo(2), "info");
                addEnhancedActivityItem("👤 New user registration: " + generateRandomUserName(), getTimeAgo(5),
                        "success");
                addEnhancedActivityItem("🎬 Movie catalog updated", getTimeAgo(15), "info");
                addEnhancedActivityItem("🛒 Order #" + (1000 + random.nextInt(9000)) + " processed", getTimeAgo(30),
                        "success");
                addEnhancedActivityItem("⚠️ System backup completed", getTimeAgo(60), "warning");
                addEnhancedActivityItem("🔧 Database optimization finished", getTimeAgo(120), "info");

                // Apply animation to activity container
                if (activityContainer.getStyleClass() != null) {
                    activityContainer.getStyleClass().add("activity-feed");
                }

            }

        }
 catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading recent system activity: " + e.getMessage(), e);
        }

    }


    /**
     * Load recent users with enhanced display
     */
    private void loadRecentUsers() {
        try {
            if (recentUsersContainer != null) {
                recentUsersContainer.getChildren().clear();

                // Load and store recent users
                recentUsers.clear();
                PageRequest pageRequest = new PageRequest(0, 10);
                Page<User> users = userService.read(pageRequest);
                int count = Math.min(5, users.getContent().size());

                for (int i = 0; i < count; i++) {
                    User user = users.getContent().get(i);
                    recentUsers.add(user); // Store the user
                    addRecentUserCard(user);
                }


                // Add placeholder users if not enough real users
                if (count < 5) {
                    for (int i = count; i < 5; i++) {
                        User placeholderUser = createPlaceholderUser();
                        recentUsers.add(placeholderUser); // Store placeholder user
                        addRecentUserCard(placeholderUser);
                    }

                }

            }

        }
 catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading recent users: " + e.getMessage(), e);
        }

    }


    /**
     * Load recent content (movies, series, products)
     */
    private void loadRecentContent() {
        try {
            if (recentContentContainer != null) {
                recentContentContainer.getChildren().clear();

                // Clear and reload data lists
                recentFilms.clear();
                recentSeries.clear();
                recentProducts.clear();
                recentCinemas.clear();

                // Load recent movies
                try {
                    PageRequest pageRequest = new PageRequest(0, 10);
                    Page<Film> films = filmService.read(pageRequest);
                    for (int i = 0; i < Math.min(3, films.getTotalElements()); i++) {
                        Film film = films.getContent().get(i);
                        recentFilms.add(film); // Store the film
                        addRecentContentCard(film.getName(), "Movie", "🎬");
                    }

                }
 catch (Exception e) {
                    // Add placeholder films
                    Film placeholder1 = new Film();
                    placeholder1.setName("The Matrix");
                    recentFilms.add(placeholder1);
                    addRecentContentCard("The Matrix", "Movie", "🎬");

                    Film placeholder2 = new Film();
                    placeholder2.setName("Inception");
                    recentFilms.add(placeholder2);
                    addRecentContentCard("Inception", "Movie", "🎬");
                }


                // Load recent series
                try {
                    PageRequest pageRequest = new PageRequest(0, 10);
                    Page<Series> series = seriesService.read(pageRequest);
                    for (int i = 0; i < Math.min(2, series.getTotalElements()); i++) {
                        Series seriesItem = series.getContent().get(i);
                        recentSeries.add(seriesItem); // Store the series
                        addRecentContentCard(seriesItem.getName(), "Series", "📺");
                    }

                }
 catch (Exception e) {
                    // Add placeholder series
                    Series placeholder1 = new Series();
                    placeholder1.setName("Breaking Bad");
                    recentSeries.add(placeholder1);
                    addRecentContentCard("Breaking Bad", "Series", "📺");

                    Series placeholder2 = new Series();
                    placeholder2.setName("Game of Thrones");
                    recentSeries.add(placeholder2);
                    addRecentContentCard("Game of Thrones", "Series", "📺");
                }


                // Load recent products
                try {
                    PageRequest pageRequest = new PageRequest(0, 10);
                    Page<Product> products = productService.read(pageRequest);
                    for (int i = 0; i < Math.min(2, products.getTotalElements()); i++) {
                        Product product = products.getContent().get(i);
                        recentProducts.add(product); // Store the product
                        addRecentContentCard(product.getName(), "Product", "🛍️");
                    }

                }
 catch (Exception e) {
                    // Add placeholder products
                    Product placeholder1 = new Product();
                    placeholder1.setName("Premium Popcorn");
                    recentProducts.add(placeholder1);
                    addRecentContentCard("Premium Popcorn", "Product", "🛍️");

                    Product placeholder2 = new Product();
                    placeholder2.setName("Movie Tickets");
                    recentProducts.add(placeholder2);
                    addRecentContentCard("Movie Tickets", "Product", "🛍️");
                }


                // Load recent cinemas
                try {
                    PageRequest pageRequest = new PageRequest(0, 10);
                    Page<Cinema> cinemas = cinemaService.read(pageRequest);
                    for (int i = 0; i < Math.min(2, cinemas.getTotalElements()); i++) {
                        Cinema cinema = cinemas.getContent().get(i);
                        recentCinemas.add(cinema); // Store the cinema
                        addRecentContentCard(cinema.getName(), "Cinema", "🏢");
                    }

                }
 catch (Exception e) {
                    // Add placeholder cinemas
                    Cinema placeholder1 = new Cinema();
                    placeholder1.setName("Grand Cinema");
                    recentCinemas.add(placeholder1);
                    addRecentContentCard("Grand Cinema", "Cinema", "🏢");

                    Cinema placeholder2 = new Cinema();
                    placeholder2.setName("Elite Theater");
                    recentCinemas.add(placeholder2);
                    addRecentContentCard("Elite Theater", "Cinema", "🏢");
                }

            }

        }
 catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading recent content: " + e.getMessage(), e);
        }

    }


    /**
     * Load system logs with enhanced formatting
     */
    private void loadSystemLogs() {
        try {
            if (systemLogsContainer != null) {
                systemLogsContainer.getChildren().clear();

                addSystemLogEntry("[INFO] Application started successfully", getTimeAgo(180), "info");
                addSystemLogEntry("[DEBUG] Database connection established", getTimeAgo(175), "debug");
                addSystemLogEntry("[WARN] High memory usage detected", getTimeAgo(120), "warning");
                addSystemLogEntry("[INFO] Backup process initiated", getTimeAgo(90), "info");
                addSystemLogEntry("[SUCCESS] All services operational", getTimeAgo(30), "success");
            }

        }
 catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading system logs: " + e.getMessage(), e);
        }

    }


    /**
     * Setup real-time updates for dynamic data
     */
    private void setupRealTimeUpdates() {
        // Update current time every second
        clockUpdateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateCurrentTime()));
        clockUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        clockUpdateTimeline.play();

        // Update statistics every 30 seconds
        statsUpdateTimeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> loadComprehensiveStatistics()));
        statsUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        statsUpdateTimeline.play();

        // Update activity feed every 10 seconds
        activityUpdateTimeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
            if (random.nextDouble() < 0.3) { // 30% chance to add new activity
                addRandomActivity();
            }

        }
));
        activityUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        activityUpdateTimeline.play();
    }


    /**
     * Update current time display
     */
    private void updateCurrentTime() {
        if (currentTimeLabel != null) {
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            currentTimeLabel.setText(currentTime);
        }

    }


    /**
     * Add random activity to demonstrate real-time updates
     */
    private void addRandomActivity() {
        if (activityContainer != null && activityContainer.getChildren().size() > 7) {
            // Remove oldest activity
            activityContainer.getChildren().remove(activityContainer.getChildren().size() - 1);
        }


        String[] activities = {
                "👤 User login detected",
                "🎬 New movie added to catalog",
                "🛒 Product purchase completed",
                "📱 Mobile app access",
                "🔧 System maintenance check",
                "📊 Analytics report generated"
        }
;

        String activity = activities[random.nextInt(activities.length)];
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        addEnhancedActivityItem(activity, currentTime, "info");
    }


    /**
     * Add enhanced activity item with styling and animations
     */
    private void addEnhancedActivityItem(String activity, String timestamp, String type) {
        if (activityContainer == null)
            return;

        HBox activityItem = new HBox(15);
        activityItem.setAlignment(Pos.CENTER_LEFT);

        // Set background based on type
        String backgroundColor = switch (type) {
            case "success" -> "rgba(0, 255, 127, 0.1)";
            case "warning" -> "rgba(255, 170, 0, 0.1)";
            case "error" -> "rgba(255, 68, 68, 0.1)";
            default -> "rgba(102, 204, 255, 0.1)";
        }
;

        activityItem.setStyle(String.format(
                "-fx-padding: 12 15 12 15; " +
                        "-fx-background-color: %s; " +
                        "-fx-background-radius: 12; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 8, 0, 0, 2);",
                backgroundColor));

        // Status indicator circle
        Circle indicator = new Circle(4);
        String indicatorColor = switch (type) {
            case "success" -> "#00ff7f";
            case "warning" -> "#ffaa00";
            case "error" -> "#ff4444";
            default -> "#66ccff";
        }
;
        indicator.setStyle("-fx-fill: " + indicatorColor + "; -fx-effect: dropshadow(gaussian, " + indicatorColor
                + ", 8, 0, 0, 0);");
        indicator.getStyleClass().add("pulsing-indicator");

        // Activity text
        Label activityLabel = new Label(activity);
        activityLabel.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-weight: normal;");

        // Timestamp
        Label timestampLabel = new Label(timestamp);
        timestampLabel.setStyle(
                "-fx-text-fill: #aaaaaa; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-font-size: 11px; " +
                        "-fx-font-style: italic;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        activityItem.getChildren().addAll(indicator, activityLabel, spacer, timestampLabel);

        // Add fade-in animation
        activityItem.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), activityItem);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Add slide-in animation
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), activityItem);
        slideIn.setFromX(50);
        slideIn.setToX(0);

        ParallelTransition animation = new ParallelTransition(fadeIn, slideIn);

        activityContainer.getChildren().add(0, activityItem); // Add to top
        animation.play();
    }


    /**
     * Add recent user card with enhanced design
     */
    private void addRecentUserCard(User user) {
        if (recentUsersContainer == null)
            return;

        HBox userCard = new HBox(12);
        userCard.setAlignment(Pos.CENTER_LEFT);
        userCard.setStyle(
                "-fx-padding: 10 15 10 15; " +
                        "-fx-background-color: linear-gradient(to right, rgba(139, 0, 0, 0.15), rgba(50, 0, 0, 0.1)); "
                        +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: rgba(139, 0, 0, 0.3); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 6, 0, 0, 2);");

        // User avatar (placeholder)
        Circle avatar = new Circle(18);
        avatar.setStyle(
                "-fx-fill: linear-gradient(to bottom right, #ff6666, #cc3333); " +
                        "-fx-stroke: white; " +
                        "-fx-stroke-width: 2; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 6, 0, 0, 1);");

        // User info
        VBox userInfo = new VBox(2);
        Label nameLabel = new Label(user.getFirstName() + " " + user.getLastName());
        nameLabel.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-weight: bold;");

        Label emailLabel = new Label(user.getEmail());
        emailLabel.setStyle(
                "-fx-text-fill: #cccccc; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-font-size: 11px;");

        userInfo.getChildren().addAll(nameLabel, emailLabel);

        // Status indicator
        Label statusLabel = new Label("ACTIVE");
        statusLabel.setStyle(
                "-fx-text-fill: #00ff7f; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-font-size: 10px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: rgba(0, 255, 127, 0.2); " +
                        "-fx-background-radius: 8; " +
                        "-fx-padding: 2 6 2 6;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        userCard.getChildren().addAll(avatar, userInfo, spacer, statusLabel);

        // Add hover effect
        userCard.setOnMouseEntered(e -> {
            userCard.setStyle(userCard.getStyle()
                    + "-fx-background-color: linear-gradient(to right, rgba(139, 0, 0, 0.25), rgba(50, 0, 0, 0.15));");
        }
);

        userCard.setOnMouseExited(e -> {
            userCard.setStyle(
                    "-fx-padding: 10 15 10 15; " +
                            "-fx-background-color: linear-gradient(to right, rgba(139, 0, 0, 0.15), rgba(50, 0, 0, 0.1)); "
                            +
                            "-fx-background-radius: 10; " +
                            "-fx-border-color: rgba(139, 0, 0, 0.3); " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 10; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 6, 0, 0, 2);");
        }
);

        recentUsersContainer.getChildren().add(userCard);
    }


    /**
     * Add recent content card
     */
    private void addRecentContentCard(String name, String type, String icon) {
        if (recentContentContainer == null)
            return;

        HBox contentCard = new HBox(10);
        contentCard.setAlignment(Pos.CENTER_LEFT);
        contentCard.setStyle(
                "-fx-padding: 8 12 8 12; " +
                        "-fx-background-color: rgba(40, 40, 40, 0.7); " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;");

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px;");

        VBox contentInfo = new VBox(1);
        Label nameLabel = new Label(name);
        nameLabel.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold;");

        Label typeLabel = new Label(type);
        typeLabel.setStyle(
                "-fx-text-fill: #888888; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-font-size: 10px;");

        contentInfo.getChildren().addAll(nameLabel, typeLabel);
        contentCard.getChildren().addAll(iconLabel, contentInfo);

        recentContentContainer.getChildren().add(contentCard);
    }


    /**
     * Add system log entry
     */
    private void addSystemLogEntry(String message, String timestamp, String level) {
        if (systemLogsContainer == null)
            return;

        HBox logEntry = new HBox(10);
        logEntry.setAlignment(Pos.CENTER_LEFT);
        logEntry.setStyle("-fx-padding: 6 10 6 10;");

        // Level indicator
        Label levelLabel = new Label(level.toUpperCase());
        String levelColor = switch (level) {
            case "error" -> "#ff4444";
            case "warning" -> "#ffaa00";
            case "success" -> "#00ff7f";
            case "debug" -> "#66ccff";
            default -> "#cccccc";
        }
;

        levelLabel.setStyle(
                "-fx-text-fill: " + levelColor + "; " +
                        "-fx-font-family: 'Courier New'; " +
                        "-fx-font-size: 9px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-min-width: 60px;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle(
                "-fx-text-fill: #dddddd; " +
                        "-fx-font-family: 'Courier New'; " +
                        "-fx-font-size: 10px;");

        Label timeLabel = new Label(timestamp);
        timeLabel.setStyle(
                "-fx-text-fill: #888888; " +
                        "-fx-font-family: 'Courier New'; " +
                        "-fx-font-size: 9px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        logEntry.getChildren().addAll(levelLabel, messageLabel, spacer, timeLabel);
        systemLogsContainer.getChildren().add(logEntry);
    }


    /**
     * Setup advanced animations similar to HomeClient
     */
    private void setupAdvancedAnimations() {
        // Setup background fade-in
        if (rootContainer != null) {
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), rootContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }


        // Setup particle animations
        setupParticleAnimations();

        // Setup shape animations
        setupShapeAnimations();

        // Setup content animations
        setupContentAnimations();

        // Setup stat card animations
        setupStatCardAnimations();
    }


    /**
     * Create dynamic particles for enhanced visual effects
     */
    private void createDynamicParticles() {
        if (particlesContainer == null)
            return;

        // Create additional floating particles
        for (int i = 0; i < 8; i++) {
            Circle particle = new Circle();
            particle.setRadius(2 + random.nextDouble() * 4);
            particle.setLayoutX(random.nextDouble() * 1100);
            particle.setLayoutY(random.nextDouble() * 650);

            // Random red-themed colors
            String[] colors = {
                    "#ff4444", "#ff6666", "#ff3333", "#ff5555",
                    "#cc3333", "#aa2222", "#ff7777", "#bb0000"
            }
;
            String color = colors[random.nextInt(colors.length)];

            particle.setStyle(String.format(
                    "-fx-fill: radial-gradient(center 50%% 50%%, radius 50%%, %s, %s66); " +
                            "-fx-effect: dropshadow(gaussian, %s, 12, 0, 0, 0);",
                    color, color, color));

            particle.setOpacity(0.4 + random.nextDouble() * 0.4);
            particle.getStyleClass().addAll("floating-particle", "dynamic-particle-" + (i % 3 + 1));

            dynamicParticles.add(particle);
            particlesContainer.getChildren().add(particle);

            // Setup individual particle animation
            setupAdvancedParticleAnimation(particle, i * 0.5);
        }

    }


    /**
     * Create dynamic shapes for background animation
     */
    private void createDynamicShapes() {
        if (particlesContainer == null)
            return;

        // Create additional geometric shapes
        for (int i = 0; i < 6; i++) {
            javafx.scene.Node shape;

            if (i % 3 == 0) {
                // Create polygon
                Polygon polygon = new Polygon();
                polygon.getPoints().addAll(new Double[] {
                        0.0, 20.0, 10.0, 0.0, 20.0, 20.0, 10.0, 30.0
                }
);
                shape = polygon;
            }
 else if (i % 3 == 1) {
                // Create rectangle
                Rectangle rectangle = new Rectangle(15 + random.nextInt(20), 8 + random.nextInt(15));
                rectangle.setRotate(random.nextDouble() * 45);
                shape = rectangle;
            }
 else {
                // Create circle
                Circle circle = new Circle(8 + random.nextDouble() * 12);
                shape = circle;
            }


            shape.setLayoutX(random.nextDouble() * 1100);
            shape.setLayoutY(random.nextDouble() * 650);

            // Apply styling
            String[] colors = { "#8b0000", "#a00000", "#660000", "#cc0000" }
;
            String color = colors[random.nextInt(colors.length)];

            shape.setStyle(String.format(
                    "-fx-fill: linear-gradient(to bottom right, %s80, %s40); " +
                            "-fx-stroke: %s; " +
                            "-fx-stroke-width: 1; " +
                            "-fx-effect: dropshadow(gaussian, %s66, 8, 0, 0, 0);",
                    color, color, color, color));

            shape.setOpacity(0.3 + random.nextDouble() * 0.3);
            shape.getStyleClass().addAll("rotating-shape", "dynamic-shape-" + (i % 2 + 1));

            dynamicShapes.add(shape);
            particlesContainer.getChildren().add(shape);

            // Setup shape animation
            setupAdvancedShapeAnimation(shape, i * 0.3);
        }

    }


    /**
     * Setup advanced particle animation
     */
    private void setupAdvancedParticleAnimation(Circle particle, double delay) {
        // Floating animation
        TranslateTransition floatY = new TranslateTransition(Duration.seconds(4 + random.nextDouble() * 2), particle);
        floatY.setFromY(0);
        floatY.setToY(-20 - random.nextDouble() * 15);
        floatY.setAutoReverse(true);
        floatY.setCycleCount(Timeline.INDEFINITE);
        floatY.setDelay(Duration.seconds(delay));

        // Horizontal drift
        TranslateTransition floatX = new TranslateTransition(Duration.seconds(6 + random.nextDouble() * 4), particle);
        floatX.setFromX(0);
        floatX.setToX(-10 + random.nextDouble() * 20);
        floatX.setAutoReverse(true);
        floatX.setCycleCount(Timeline.INDEFINITE);
        floatX.setDelay(Duration.seconds(delay + 1));

        // Opacity pulsing
        FadeTransition fade = new FadeTransition(Duration.seconds(3 + random.nextDouble() * 2), particle);
        fade.setFromValue(0.3);
        fade.setToValue(0.8);
        fade.setAutoReverse(true);
        fade.setCycleCount(Timeline.INDEFINITE);
        fade.setDelay(Duration.seconds(delay + 0.5));

        ParallelTransition animation = new ParallelTransition(floatY, floatX, fade);
        animation.play();
    }


    /**
     * Setup advanced shape animation
     */
    private void setupAdvancedShapeAnimation(javafx.scene.Node shape, double delay) {
        // Rotation
        RotateTransition rotate = new RotateTransition(Duration.seconds(8 + random.nextDouble() * 4), shape);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setCycleCount(Timeline.INDEFINITE);
        rotate.setDelay(Duration.seconds(delay));

        // Scale pulsing
        ScaleTransition scale = new ScaleTransition(Duration.seconds(3 + random.nextDouble() * 2), shape);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.2);
        scale.setToY(1.2);
        scale.setAutoReverse(true);
        scale.setCycleCount(Timeline.INDEFINITE);
        scale.setDelay(Duration.seconds(delay + 1));

        ParallelTransition animation = new ParallelTransition(rotate, scale);
        animation.play();
    }


    /**
     * Setup particle animations for static particles
     */
    private void setupParticleAnimations() {
        Circle[] particles = { particle1, particle2, particle3, particle4, particle5, particle6 }
;

        for (int i = 0; i < particles.length; i++) {
            if (particles[i] != null) {
                setupParticleAnimation(particles[i], i * 0.5);
            }

        }

    }


    /**
     * Setup individual particle animation
     */
    private void setupParticleAnimation(Circle particle, double delay) {
        // Floating animation
        TranslateTransition floatTransition = new TranslateTransition(Duration.seconds(4), particle);
        floatTransition.setFromY(0);
        floatTransition.setToY(-25);
        floatTransition.setAutoReverse(true);
        floatTransition.setCycleCount(Timeline.INDEFINITE);
        floatTransition.setDelay(Duration.seconds(delay));

        // Glow animation
        FadeTransition glowTransition = new FadeTransition(Duration.seconds(2), particle);
        glowTransition.setFromValue(0.4);
        glowTransition.setToValue(0.9);
        glowTransition.setAutoReverse(true);
        glowTransition.setCycleCount(Timeline.INDEFINITE);
        glowTransition.setDelay(Duration.seconds(delay + 1));

        ParallelTransition animation = new ParallelTransition(floatTransition, glowTransition);
        animation.play();
    }


    /**
     * Setup shape animations
     */
    private void setupShapeAnimations() {
        javafx.scene.Node[] shapes = { shape1, shape2, shape3, shape4, shape5, shape6 }
;

        for (int i = 0; i < shapes.length; i++) {
            if (shapes[i] != null) {
                setupRotationAnimation(shapes[i], 8 + i * 2);
                setupPulsingAnimation(shapes[i]);
            }

        }

    }


    /**
     * Setup rotation animation for shapes
     */
    private void setupRotationAnimation(javafx.scene.Node shape, double duration) {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(duration), shape);
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(360);
        rotateTransition.setCycleCount(Timeline.INDEFINITE);
        rotateTransition.play();
    }


    /**
     * Setup pulsing animation for shapes
     */
    private void setupPulsingAnimation(javafx.scene.Node shape) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(3), shape);
        scaleTransition.setFromX(0.8);
        scaleTransition.setFromY(0.8);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(Timeline.INDEFINITE);
        scaleTransition.play();
    }


    /**
     * Setup content animations
     */
    private void setupContentAnimations() {
        if (contentContainer != null) {
            // Slide in from bottom
            TranslateTransition slideIn = new TranslateTransition(Duration.seconds(1), contentContainer);
            slideIn.setFromY(50);
            slideIn.setToY(0);
            slideIn.play();
        }

    }


    /**
     * Setup stat card animations
     */
    private void setupStatCardAnimations() {
        VBox[] statCards = { userStatsCard, movieStatsCard, productStatsCard, orderStatsCard, seriesStatsCard,
                cinemaStatsCard }
;

        for (int i = 0; i < statCards.length; i++) {
            if (statCards[i] != null) {
                setupStatCardAnimation(statCards[i], i * 0.2);
            }

        }

    }


    /**
     * Setup individual stat card animation
     */
    private void setupStatCardAnimation(VBox card, double delay) {
        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), card);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setDelay(Duration.seconds(delay));

        // Scale in
        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.8), card);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        scaleIn.setDelay(Duration.seconds(delay));

        // Add hover effect
        card.setOnMouseEntered(e -> {
            ScaleTransition hoverScale = new ScaleTransition(Duration.seconds(0.2), card);
            hoverScale.setToX(1.05);
            hoverScale.setToY(1.05);
            hoverScale.play();
        }
);

        card.setOnMouseExited(e -> {
            ScaleTransition hoverScale = new ScaleTransition(Duration.seconds(0.2), card);
            hoverScale.setToX(1.0);
            hoverScale.setToY(1.0);
            hoverScale.play();
        }
);

        ParallelTransition animation = new ParallelTransition(fadeIn, scaleIn);
        animation.play();
    }


    /**
     * Setup quick search functionality
     */
    private void setupQuickSearchFunctionality() {
        if (quickSearchField != null) {
            quickSearchField.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("ENTER")) {
                    performQuickSearch(quickSearchField.getText());
                }

            }
);

            // Add search suggestion as you type
            quickSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() > 2) {
                    // In a real implementation, this would show search suggestions
                    LOGGER.info("Searching for: " + newValue);
                }

            }
);
        }

    }


    /**
     * Perform quick search across admin data
     */
    private void performQuickSearch(String query) {
        if (query == null || query.trim().isEmpty())
            return;

        LOGGER.info("Admin searching for: " + query);
        // In a real implementation, this would search users, content, etc.

        // Show search results in a popup or navigate to search results page
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Search Results");
        alert.setHeaderText("Quick Search: " + query);
        alert.setContentText("Search functionality will display results for: " + query);
        alert.showAndWait();
    }


    /**
     * Apply advanced styling to UI elements
     */
    private void applyAdvancedStyling() {
        // Apply CSS classes for animations
        if (rootContainer != null) {
            rootContainer.getStyleClass().add("fade-in");
        }


        if (mainContainer != null) {
            mainContainer.getStyleClass().add("glow-container");
        }


        if (welcomeLabel != null) {
            welcomeLabel.getStyleClass().add("animated-text");
        }

    }


    /**
     * Setup interactive elements with enhanced functionality
     */
    private void setupInteractiveElements() {
        // Setup stat cards as clickable elements
        setupStatCardInteractivity();

        // Setup quick action buttons
        setupQuickActionButtons();
    }


    /**
     * Setup stat card interactivity
     */
    private void setupStatCardInteractivity() {
        if (userStatsCard != null) {
            userStatsCard.setOnMouseClicked(e -> manageUsers(null));
            userStatsCard.getStyleClass().add("hover-scale");
        }


        if (movieStatsCard != null) {
            movieStatsCard.setOnMouseClicked(e -> manageMovies(null));
            movieStatsCard.getStyleClass().add("hover-scale");
        }


        if (productStatsCard != null) {
            productStatsCard.setOnMouseClicked(e -> manageProducts(null));
            productStatsCard.getStyleClass().add("hover-scale");
        }


        if (orderStatsCard != null) {
            orderStatsCard.setOnMouseClicked(e -> manageOrders(null));
            orderStatsCard.getStyleClass().add("hover-scale");
        }

    }


    /**
     * Setup quick action buttons with enhanced styling
     */
    private void setupQuickActionButtons() {
        if (quickActionsContainer != null) {
            quickActionsContainer.getChildren().forEach(node -> {
                if (node instanceof Button button) {
                    button.getStyleClass().addAll("glow-button", "action-button");
                }

            }
);
        }

    }


    // Utility Methods

    /**
     * Generate time ago string
     */
    private String getTimeAgo(int minutesAgo) {
        if (minutesAgo < 60) {
            return minutesAgo + " min ago";
        }
 else if (minutesAgo < 1440) {
            return (minutesAgo / 60) + " hr ago";
        }
 else {
            return (minutesAgo / 1440) + " day ago";
        }

    }


    /**
     * Generate random user name for demo purposes
     */
    private String generateRandomUserName() {
        String[] firstNames = { "John", "Jane", "Alex", "Sarah", "Mike", "Lisa", "David", "Emma" }
;
        String[] lastNames = { "Smith", "Johnson", "Brown", "Davis", "Wilson", "Miller", "Taylor", "Anderson" }
;

        return firstNames[random.nextInt(firstNames.length)] + " " +
                lastNames[random.nextInt(lastNames.length)];
    }


    /**
     * Create placeholder user for demo
     */
    private User createPlaceholderUser() {
        User user = new User() {
        }
;
        String name = generateRandomUserName();
        String[] parts = name.split(" ");
        user.setFirstName(parts[0]);
        user.setLastName(parts[1]);
        user.setEmail(parts[0].toLowerCase() + "." + parts[1].toLowerCase() + "@example.com");
        return user;
    }


    /**
     * Cleanup method called when controller is destroyed
     */
    public void cleanup() {
        if (clockUpdateTimeline != null) {
            clockUpdateTimeline.stop();
        }

        if (statsUpdateTimeline != null) {
            statsUpdateTimeline.stop();
        }

        if (activityUpdateTimeline != null) {
            activityUpdateTimeline.stop();
        }

        if (particleAnimationTimeline != null) {
            particleAnimationTimeline.stop();
        }

    }


    /**
     * Get recent users data for external access
     */
    public List<User> getRecentUsers() {
        return new ArrayList<>(recentUsers);
    }


    /**
     * Get recent films data for external access
     */
    public List<Film> getRecentFilms() {
        return new ArrayList<>(recentFilms);
    }


    /**
     * Get recent products data for external access
     */
    public List<Product> getRecentProducts() {
        return new ArrayList<>(recentProducts);
    }


    /**
     * Get recent series data for external access
     */
    public List<Series> getRecentSeries() {
        return new ArrayList<>(recentSeries);
    }


    /**
     * Get recent cinemas data for external access
     */
    public List<Cinema> getRecentCinemas() {
        return new ArrayList<>(recentCinemas);
    }


    /**
     * Refresh recent data
     */
    public void refreshRecentData() {
        loadRecentUsers();
        loadRecentContent();
        loadRecentSystemActivity();
    }


    // Navigation Action Handlers

    @FXML
    void manageUsers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/AdminDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
 catch (IOException e) {
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
        }
 catch (IOException e) {
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
        }
 catch (IOException e) {
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
        }
 catch (IOException e) {
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
        }
 catch (IOException e) {
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
        }
 catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error opening system settings: " + e.getMessage(), e);
        }

    }


    @FXML
    public void viewReports(ActionEvent actionEvent) {
        try {
            // Create a reports dialog or navigate to reports page
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Analytics & Reports");
            alert.setHeaderText("System Analytics & Reports");
            alert.setContentText("Detailed analytics and reporting functionality will be implemented here.\n\n" +
                    "Available Reports:\n" +
                    "• User Activity Reports\n" +
                    "• Content Performance Analytics\n" +
                    "• Revenue & Sales Reports\n" +
                    "• System Performance Metrics");
            alert.showAndWait();
        }
 catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error opening reports: " + e.getMessage(), e);
        }

    }

}

