package com.esprit.controllers.users;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.films.Film;
import com.esprit.models.products.Product;
import com.esprit.models.series.Series;
import com.esprit.models.users.User;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.films.FilmService;
import com.esprit.services.products.ProductService;
import com.esprit.services.series.SeriesService;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the HomeClient interface that displays movies, series, and
 * products
 * with animations and modern UI design similar to login/signup interfaces.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class HomeClientController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(HomeClientController.class.getName());
    // Featured movie data
    private final String[] featuredMovies = {
        "The Batman|https://image.tmdb.org/t/p/w500/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg|When a sadistic serial killer begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.",
        "Dune|https://image.tmdb.org/t/p/w500/jWZLvnBAjZvpbIJGV1GKK2JqfK3.jpg|Paul Atreides, a brilliant and gifted young man born into a great destiny beyond his understanding, must travel to the most dangerous planet in the universe to ensure the future of his family and his people.",
        "Joker|https://image.tmdb.org/t/p/w500/kAVRgw7GgK1CfYEJq8ME6EvRIgU.jpg|During the 1980s, a failed stand-up comedian is driven insane and turns to a life of crime and chaos in Gotham City while becoming an infamous psychopathic crime figure.",
        "Oppenheimer|https://image.tmdb.org/t/p/w500/6KErczPBROQty7QoIsaa6wJYXZi.jpg|The story of J. Robert Oppenheimer's role in the development of the atomic bomb during World War II.",
        "Spider-Man: Across the Spider-Verse|https://image.tmdb.org/t/p/w500/8Vt6mWEReuy4Of61Lnj5Xj704m8.jpg|After reuniting with Gwen Stacy, Brooklyn's full-time, friendly neighborhood Spider-Man is catapulted across the Multiverse."
    };
    // FXML injected controls
    @FXML
    private StackPane rootContainer;
    @FXML
    private TextField searchField;
    @FXML
    private Label welcomeLabel;
    @FXML
    private ScrollPane mainScrollPane;
    // Featured section
    @FXML
    private StackPane featuredBanner;
    @FXML
    private ImageView featuredImage;
    @FXML
    private Label featuredTitle;
    @FXML
    private Label featuredDescription;
    // Content containers
    @FXML
    private HBox moviesContainer;
    @FXML
    private HBox seriesContainer;
    @FXML
    private HBox productsContainer;
    @FXML
    private HBox cinemasContainer;
    @FXML
    private ScrollPane moviesScrollPane;
    @FXML
    private ScrollPane seriesScrollPane;
    @FXML
    private ScrollPane productsScrollPane;
    @FXML
    private ScrollPane cinemasScrollPane;
    // Animation elements
    @FXML
    private Circle particle1, particle2, particle3, particle4, particle5, particle6;
    @FXML
    private Polygon shape1, shape2, shape5;
    @FXML
    private Rectangle shape3, shape6;
    @FXML
    private Circle shape4;
    @FXML
    private AnchorPane particlesContainer;
    // Dynamic particles and shapes arrays
    private List<Circle> dynamicParticles;
    private List<javafx.scene.Node> dynamicShapes;
    private java.util.Random random;
    // Services
    private FilmService filmService;
    private ProductService productService;
    private SeriesService seriesService;
    private CinemaService cinemaService;
    // Data lists
    private List<Film> recentFilms;
    private List<Series> recentSeries;
    private List<Product> recentProducts;
    private List<Cinema> recentCinemas;
    private int currentFeaturedIndex = 0;
    private Timeline featuredRotationTimeline;
    private Timeline particleAnimationTimeline;

    /**
     * Initialize the controller and prepare the HomeClient user interface.
     *
     * <p>
     * Performs startup tasks required for the view: initializes backend services,
     * configures the
     * welcome message, loads featured content and the
     * movies/series/products/cinemas sections,
     * sets up animations and search behavior, and applies initial styling.
     * </p>
     *
     * @param location  the location used to resolve relative paths for the root
     *                  object, may be null
     * @param resources the resources used to localize the root object, may be null
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info("Initializing HomeClient interface...");

        // Initialize services
        initializeServices();

        // Setup user welcome message
        setupWelcomeMessage();

        // Load content
        loadFeaturedContent();
        loadMoviesContent();
        loadSeriesContent();
        loadProductsContent();
        loadCinemasContent();

        // Setup animations
        setupAnimations();

        // Setup search functionality
        setupSearchFunctionality();

        // Apply initial styling and effects
        applyInitialStyling();

        LOGGER.info("HomeClient interface initialized successfully");
    }

    /**
     * Initialize backend services and the controller's in-memory collections used
     * for content and animations.
     * <p>
     * Instantiates service clients (filmService, productService, seriesService,
     * cinemaService),
     * creates lists for recent items (recentFilms, recentSeries, recentProducts,
     * recentCinemas),
     * initializes dynamic animation collections (dynamicParticles, dynamicShapes)
     * and the Random instance.
     * Logs successful initialization and records a warning if an initialization
     * error occurs.
     */
    private void initializeServices() {
        try {
            filmService = new FilmService();
            productService = new ProductService();
            seriesService = new SeriesService();
            cinemaService = new CinemaService();

            recentFilms = new ArrayList<>();
            recentSeries = new ArrayList<>();
            recentProducts = new ArrayList<>();
            recentCinemas = new ArrayList<>();

            // Initialize dynamic animation arrays
            dynamicParticles = new ArrayList<>();
            dynamicShapes = new ArrayList<>();
            random = new java.util.Random();

            LOGGER.info("Services initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error initializing services: " + e.getMessage(), e);
        }

    }

    /**
     * Sets the welcome label text to the logged-in user's first and last name when
     * available; otherwise sets a default welcome.
     * <p>
     * Attempts to read a User object from the window's userData and updates the
     * welcomeLabel. If no user is present or an error occurs, the label is set to
     * "Welcome to RAKCHA!".
     */
    private void setupWelcomeMessage() {
        try {
            // Use Platform.runLater to ensure the scene is fully initialized
            javafx.application.Platform.runLater(() -> {
                try {
                    if (rootContainer != null && rootContainer.getScene() != null) {
                        Stage stage = (Stage) rootContainer.getScene().getWindow();
                        if (stage != null && stage.getUserData() instanceof User) {
                            User currentUser = (User) stage.getUserData();
                            welcomeLabel.setText("Welcome back, " + currentUser.getFirstName() + " "
                                + currentUser.getLastName() + "!");
                        } else {
                            welcomeLabel.setText("Welcome to RAKCHA!");
                        }

                    } else {
                        welcomeLabel.setText("Welcome to RAKCHA!");
                    }

                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Error in delayed welcome message setup: " + ex.getMessage(), ex);
                    welcomeLabel.setText("Welcome to RAKCHA!");
                }

            });
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error setting welcome message: " + e.getMessage(), e);
            welcomeLabel.setText("Welcome to RAKCHA!");
        }

    }

    /**
     * Initialize the featured content display and start its automatic rotation.
     * <p>
     * Calls updateFeaturedMovie to set the initial featured entry, then starts a
     * Timeline
     * that advances the featured item every 8 seconds and stores it in
     * featuredRotationTimeline.
     */
    private void loadFeaturedContent() {
        // Start with first featured movie
        updateFeaturedMovie();

        // Setup automatic rotation
        featuredRotationTimeline = new Timeline(
            new KeyFrame(Duration.seconds(8), e -> rotateFeaturedMovie()));
        featuredRotationTimeline.setCycleCount(Timeline.INDEFINITE);
        featuredRotationTimeline.play();
    }

    /**
     * Update the featured banner to display the movie at the current featured
     * index.
     * <p>
     * Sets the featured title and description from the selected entry and, if the
     * image control is available,
     * replaces the banner image with a cross-fade transition. If there are no
     * featured entries or the
     * selected entry is malformed, the method returns without changing the UI.
     * Image loading errors are
     * logged but do not throw.
     */
    private void updateFeaturedMovie() {
        if (featuredMovies.length == 0)
            return;

        String[] movieData = featuredMovies[currentFeaturedIndex].split("\\|");
        if (movieData.length >= 3) {
            featuredTitle.setText(movieData[0]);
            featuredDescription.setText(movieData[2]);

            // Load image with fade transition
            if (featuredImage != null) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), featuredImage);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> {
                    try {
                        featuredImage.setImage(new Image(movieData[1]));
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), featuredImage);
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();
                    } catch (Exception ex) {
                        LOGGER.log(Level.WARNING, "Error loading featured image: " + ex.getMessage(), ex);
                    }

                });
                fadeOut.play();
            }

        }

    }

    /**
     * Advance the featured index to the next entry and refresh the featured
     * display.
     */
    private void rotateFeaturedMovie() {
        currentFeaturedIndex = (currentFeaturedIndex + 1) % featuredMovies.length;
        updateFeaturedMovie();
    }

    /**
     * Populates the movies horizontal container with movie cards and initializes
     * its carousel animation.
     * <p>
     * Attempts to load recent films from the film service and create a card for
     * each; if no films are available or loading fails, populates the container
     * with placeholder movie cards. Always sets up the carousel animation for the
     * movies container and logs errors or a missing container.
     */
    private void loadMoviesContent() {
        try {
            LOGGER.info("Loading movies content...");

            // Always create some content even if service fails
            if (moviesContainer != null) {
                moviesContainer.getChildren().clear();

                // Try to get from service first
                try {
                    PageRequest pageRequest = new PageRequest(0, 10);
                    Page<Film> films = filmService.read(pageRequest);
                    if (films != null && !films.getContent().isEmpty()) {
                        recentFilms = films.getContent();
                        LOGGER.info("Loaded " + recentFilms.size() + " films from service");

                        for (Film film : recentFilms) {
                            VBox movieCard = createMovieCard(film);
                            moviesContainer.getChildren().add(movieCard);
                        }

                    } else {
                        LOGGER.info("No films from service, creating placeholders");
                        createPlaceholderMovies();
                    }

                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Service failed, creating placeholders: " + e.getMessage());
                    createPlaceholderMovies();
                }

                // Setup carousel animation for movies
                setupCarouselAnimation(moviesContainer, moviesScrollPane);
            } else {
                LOGGER.warning("Movies container is null!");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in loadMoviesContent: " + e.getMessage(), e);
            if (moviesContainer != null) {
                createPlaceholderMovies();
                setupCarouselAnimation(moviesContainer, moviesScrollPane);
            }

        }

    }

    /**
     * Populate the series horizontal container with series cards and configure its
     * carousel.
     * <p>
     * Attempts to load up to 10 series from the configured series service and
     * replaces the
     * contents of {@code seriesContainer} with a card for each returned series. If
     * the service
     * returns no data or an error occurs, placeholder series cards are created
     * instead. After
     * populating the container the method initializes the carousel animation for
     * the container
     * and associated scroll pane.
     * <p>
     * Side effects:
     * - Updates the controller field {@code recentSeries} when real data is loaded.
     * - Calls {@code createPlaceholderSeries()} when no data is available or on
     * service failure.
     * - Calls {@code setupCarouselAnimation(Node, ScrollPane)} to start the
     * horizontal carousel.
     * - Logs progress, warnings, and errors; handles exceptions internally and does
     * not propagate them.
     */
    private void loadSeriesContent() {
        try {
            LOGGER.info("Loading series content...");

            if (seriesContainer != null) {
                seriesContainer.getChildren().clear();

                try {
                    PageRequest pageRequest = new PageRequest(0, 10);
                    Page<Series> series = seriesService.read(pageRequest);
                    if (series != null && !series.getContent().isEmpty()) {
                        recentSeries = series.getContent();
                        LOGGER.info("Loaded " + recentSeries.size() + " series from service");

                        for (Series serie : recentSeries) {
                            VBox seriesCard = createSeriesCard(serie);
                            seriesContainer.getChildren().add(seriesCard);
                        }

                    } else {
                        LOGGER.info("No series from service, creating placeholders");
                        createPlaceholderSeries();
                    }

                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Service failed, creating placeholders: " + e.getMessage());
                    createPlaceholderSeries();
                }

                // Setup carousel animation for series
                setupCarouselAnimation(seriesContainer, seriesScrollPane);
            } else {
                LOGGER.warning("Series container is null!");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in loadSeriesContent: " + e.getMessage(), e);
            if (seriesContainer != null) {
                createPlaceholderSeries();
                setupCarouselAnimation(seriesContainer, seriesScrollPane);
            }

        }

    }

    /**
     * Populate the products container with product cards and initialize its
     * horizontal carousel.
     * <p>
     * Attempts to load up to 10 products from the product service and, if products
     * are available,
     * creates and adds a card for each product to the products container. If the
     * service returns no
     * products or an error occurs, placeholder product cards are created instead.
     * After populating
     * the container, the method configures the carousel animation using the
     * products scroll pane.
     * <p>
     * If the products container is null, the method logs a warning and performs no
     * UI updates.
     */
    private void loadProductsContent() {
        try {
            LOGGER.info("Loading products content...");

            if (productsContainer != null) {
                productsContainer.getChildren().clear();

                try {
                    PageRequest pageRequest = new PageRequest(0, 10);
                    Page<Product> products = productService.read(pageRequest);
                    if (products != null && !products.getContent().isEmpty()) {
                        recentProducts = products.getContent();
                        LOGGER.info("Loaded " + recentProducts.size() + " products from service");

                        for (Product product : recentProducts) {
                            VBox productCard = createProductCard(product);
                            productsContainer.getChildren().add(productCard);
                        }

                    } else {
                        LOGGER.info("No products from service, creating placeholders");
                        createPlaceholderProducts();
                    }

                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Service failed, creating placeholders: " + e.getMessage());
                    createPlaceholderProducts();
                }

                // Setup carousel animation for products
                setupCarouselAnimation(productsContainer, productsScrollPane);
            } else {
                LOGGER.warning("Products container is null!");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in loadProductsContent: " + e.getMessage(), e);
            if (productsContainer != null) {
                createPlaceholderProducts();
                setupCarouselAnimation(productsContainer, productsScrollPane);
            }

        }

    }

    /**
     * Populates the horizontal cinemas container with cinema cards retrieved from
     * the cinema service.
     * <p>
     * Attempts to read a page of cinemas from the configured service and add a card
     * for each result to the cinemas container.
     * If the service call fails or returns no content, the method populates the
     * container with placeholder cinema cards.
     * After filling the container it initializes the container's carousel scrolling
     * animation.
     */
    private void loadCinemasContent() {
        try {
            LOGGER.info("Loading cinemas content...");

            if (cinemasContainer != null) {
                cinemasContainer.getChildren().clear();

                try {
                    PageRequest pageRequest = new PageRequest(0, 10);
                    Page<Cinema> cinemas = cinemaService.read(pageRequest);
                    if (cinemas != null && !cinemas.getContent().isEmpty()) {
                        recentCinemas = cinemas.getContent();
                        LOGGER.info("Loaded " + recentCinemas.size() + " cinemas from service");

                        for (Cinema cinema : recentCinemas) {
                            VBox cinemaCard = createCinemaCard(cinema);
                            cinemasContainer.getChildren().add(cinemaCard);
                        }

                    } else {
                        LOGGER.info("No cinemas from service, creating placeholders");
                        createPlaceholderCinemas();
                    }

                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Service failed, creating placeholders: " + e.getMessage());
                    createPlaceholderCinemas();
                }

                // Setup carousel animation for cinemas
                setupCarouselAnimation(cinemasContainer, cinemasScrollPane);
            } else {
                LOGGER.warning("Cinemas container is null!");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in loadCinemasContent: " + e.getMessage(), e);
            if (cinemasContainer != null) {
                createPlaceholderCinemas();
                setupCarouselAnimation(cinemasContainer, cinemasScrollPane);
            }

        }

    }

    /**
     * Create a UI card representing a Film with its poster, title, release year,
     * and a generated rating.
     *
     * @param film the Film to represent in the card (used for image, title, year,
     *             and rating)
     * @return a VBox containing the styled movie card node ready to be added to the
     * scene graph
     */
    private VBox createMovieCard(Film film) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(160);
        card.setPrefHeight(280);
        card.getStyleClass().add("content-card");
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgba(30, 30, 30, 0.9), rgba(20, 20, 20, 0.95));" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(139, 0, 0, 0.3);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0, 0, 3);" +
                "-fx-padding: 10;" +
                "-fx-cursor: hand;");

        // Movie poster
        ImageView poster = new ImageView();
        poster.setFitWidth(140);
        poster.setFitHeight(200);
        poster.setPreserveRatio(true);
        poster.setStyle("-fx-background-radius: 10;");

        try {
            String imageUrl = null;
            if (film.getImageUrl() != null && !film.getImageUrl().isEmpty()) {
                imageUrl = film.getImageUrl();
            } else {
                // Use movie placeholder images from TMDB or other sources
                String[] movieImages = {
                    "https://image.tmdb.org/t/p/w500/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg", // The Batman
                    "https://image.tmdb.org/t/p/w500/pFlaoHTZeyNkG83vxsAJiGzfSsa.jpg", // Interstellar
                    "https://image.tmdb.org/t/p/w500/rr7E0NoGKxvbkb89eR1GwfoYjpA.jpg", // Inception
                    "https://image.tmdb.org/t/p/w500/aosm8NMQ3UyoBVpSxyimorCQykC.jpg", // The Dark Knight
                    "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg", // The Matrix
                    "https://image.tmdb.org/t/p/w500/cvsXj3I9Q2iyyIo95AecSd1tad7.jpg", // Avengers Endgame
                    "https://image.tmdb.org/t/p/w500/7WsyChQLEftFiDOVTGkv3hFpyyt.jpg", // Avengers
                    "https://image.tmdb.org/t/p/w500/xLPffWMhMj1l50ND3KchMjYoKmE.jpg", // Iron Man
                    "https://image.tmdb.org/t/p/w500/kqjL17yufvn9OVLyXYpvtyrFfak.jpg", // Thor
                    "https://image.tmdb.org/t/p/w500/A6B6uONhxzYV52M8VaivRrxqBjl.jpg" // Captain America
                };
                imageUrl = movieImages[Math.abs(film.getTitle().hashCode()) % movieImages.length];
            }

            poster.setImage(new Image(imageUrl));
        } catch (Exception e) {
            try {
                poster.setImage(new Image("https://via.placeholder.com/140x200/333333/ffffff?text=" +
                    java.net.URLEncoder.encode(film.getTitle(), "UTF-8")));
            } catch (Exception ex) {
                poster.setImage(new Image("https://via.placeholder.com/140x200/333333/ffffff?text=No+Image"));
            }

        }

        // Movie title
        Label title = new Label(film.getTitle());
        title.setStyle(
            "-fx-text-fill: white;" +
                "-fx-font-family: 'Arial';" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-wrap-text: true;");
        title.setMaxWidth(140);
        title.setAlignment(Pos.CENTER);

        // Movie year
        Label year = new Label(String.valueOf(film.getReleaseYear()));
        year.setStyle(
            "-fx-text-fill: #cccccc;" +
                "-fx-font-family: 'Arial';" +
                "-fx-font-size: 10px;");
        year.setAlignment(Pos.CENTER);

        // Rating stars (generate rating based on film name for consistency)
        HBox ratingBox = new HBox(2);
        ratingBox.setAlignment(Pos.CENTER);
        int rating = 3 + (Math.abs(film.getTitle().hashCode()) % 3); // Generate 3-5 star rating
        for (int i = 0; i < 5; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-text-fill: " + (i < rating ? "#ffd700" : "#666666") + "; -fx-font-size: 10px;");
            ratingBox.getChildren().add(star);
        }

        card.getChildren().addAll(poster, title, year, ratingBox);

        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                card.getStyle() +
                    "-fx-border-color: rgba(139, 0, 0, 0.6);" +
                    "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.4), 15, 0, 0, 5);");
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(30, 30, 30, 0.9), rgba(20, 20, 20, 0.95));" +
                    "-fx-background-radius: 15;" +
                    "-fx-border-color: rgba(139, 0, 0, 0.3);" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 15;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0, 0, 3);" +
                    "-fx-padding: 10;" +
                    "-fx-cursor: hand;");
        });

        // Add click handler
        card.setOnMouseClicked(e -> openMovieDetails(film));

        return card;
    }

    /**
     * Builds a UI card for a TV series containing a poster image, title, and
     * director label, and wires hover and click interactions.
     *
     * @param series the Series whose data populates the card (name, director,
     *               image)
     * @return a configured VBox containing the series poster, title, and director
     * label
     */
    private VBox createSeriesCard(Series series) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(160);
        card.setPrefHeight(280);
        card.getStyleClass().add("content-card");
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgba(30, 30, 30, 0.9), rgba(20, 20, 20, 0.95));" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(139, 0, 0, 0.3);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0, 0, 3);" +
                "-fx-padding: 10;" +
                "-fx-cursor: hand;");

        // Series poster
        ImageView poster = new ImageView();
        poster.setFitWidth(140);
        poster.setFitHeight(200);
        poster.setPreserveRatio(true);
        poster.setStyle("-fx-background-radius: 10;");

        try {
            String imageUrl = null;
            if (series.getImageUrl() != null && !series.getImageUrl().isEmpty()) {
                imageUrl = series.getImageUrl();
            } else {
                // Use TV series placeholder images
                String[] seriesImages = {
                    "https://image.tmdb.org/t/p/w500/1XS1oqL89opfnbLl8WnZY1O1uJx.jpg", // Game of Thrones
                    "https://image.tmdb.org/t/p/w500/4UjiPdFKJGJYdxwRs2Rzg7EmWqr.jpg", // Stranger Things
                    "https://image.tmdb.org/t/p/w500/suopoADq0k8YZr4dQXcU6pToj6s.jpg", // Breaking Bad
                    "https://image.tmdb.org/t/p/w500/9yBVqNruk6Ykrwc32qrK2TIE5xw.jpg", // The Mandalorian
                    "https://image.tmdb.org/t/p/w500/7WUHnWGx5OO145IRxPDUkQSh4C7.jpg", // The Witcher
                    "https://image.tmdb.org/t/p/w500/xUtOM1QO4r8w8yeE00QvBdq58N5.jpg", // House of the Dragon
                    "https://image.tmdb.org/t/p/w500/mY7SeH4HFFxW1hiI6cWuwCRKptN.jpg", // Peaky Blinders
                    "https://image.tmdb.org/t/p/w500/6LelQ6GzOoSKAj0AQPHRDdPB3Zg.jpg", // Sherlock
                    "https://image.tmdb.org/t/p/w500/sWgBv7LV2PRoQgkxwlibdGXQ6he.jpg", // Squid Game
                    "https://image.tmdb.org/t/p/w500/1BIoJGKbXvdLDVv01hJR4VQ6vTX.jpg" // Wednesday
                };
                imageUrl = seriesImages[Math.abs(series.getName().hashCode()) % seriesImages.length];
            }

            poster.setImage(new Image(imageUrl));
        } catch (Exception e) {
            try {
                poster.setImage(new Image("https://via.placeholder.com/140x200/444444/ffffff?text=" +
                    java.net.URLEncoder.encode(series.getName(), "UTF-8")));
            } catch (Exception ex) {
                poster.setImage(new Image("https://via.placeholder.com/140x200/444444/ffffff?text=Series"));
            }

        }

        // Series title
        Label title = new Label(series.getName());
        title.setStyle(
            "-fx-text-fill: white;" +
                "-fx-font-family: 'Arial';" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-wrap-text: true;");
        title.setMaxWidth(140);
        title.setAlignment(Pos.CENTER);

        // Series director
        Label year = new Label(series.getDirector() != null ? series.getDirector() : "Unknown Director");
        year.setStyle(
            "-fx-text-fill: #cccccc;" +
                "-fx-font-family: 'Arial';" +
                "-fx-font-size: 10px;");

        card.getChildren().addAll(poster, title, year);

        // Add hover effect and click handler
        setupCardInteractions(card, () -> openSeriesDetails(series));

        return card;
    }

    /**
     * Create a visual card representing a product for display in the UI.
     * <p>
     * The card shows the product image (falls back to a selection of themed images
     * or a text placeholder if missing),
     * the product name, and the formatted price, and it attaches hover styling and
     * a click handler that opens product details.
     *
     * @param product the product to render in the card
     * @return a VBox node containing the product image, name, price, and
     * interaction handlers
     */
    private VBox createProductCard(Product product) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(160);
        card.setPrefHeight(280);
        card.getStyleClass().add("content-card");
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgba(30, 30, 30, 0.9), rgba(20, 20, 20, 0.95));" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(139, 0, 0, 0.3);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0, 0, 3);" +
                "-fx-padding: 10;" +
                "-fx-cursor: hand;");

        // Product image
        ImageView image = new ImageView();
        image.setFitWidth(140);
        image.setFitHeight(180);
        image.setPreserveRatio(true);
        image.setStyle("-fx-background-radius: 10;");

        try {
            String imageUrl = null;
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                imageUrl = product.getImageUrl();
            } else {
                // Use cinema-related product images
                String[] productImages = {
                    "https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?w=300&h=400&fit=crop", // Popcorn
                    "https://images.unsplash.com/photo-1627662235719-d9e9f31c4f73?w=300&h=400&fit=crop", // Cinema
                    // tickets
                    "https://images.unsplash.com/photo-1489599446190-c9ad1d88c3dc?w=300&h=400&fit=crop", // Movie
                    // poster
                    "https://images.unsplash.com/photo-1574375927938-d5a98e8ffe85?w=300&h=400&fit=crop", // Cinema
                    // glasses
                    "https://images.unsplash.com/photo-1509924603848-78edc5642dea?w=300&h=400&fit=crop", // Cinema
                    // drinks
                    "https://images.unsplash.com/photo-1514306191717-452ec28c7814?w=300&h=400&fit=crop", // Movie
                    // reel
                    "https://images.unsplash.com/photo-1528827871709-dbe8fe4e57f8?w=300&h=400&fit=crop", // Cinema
                    // candy
                    "https://images.unsplash.com/photo-1616530940355-351fabd9524b?w=300&h=400&fit=crop", // Movie
                    // merchandise
                    "https://images.unsplash.com/photo-1596473499708-4ff13b15f4cc?w=300&h=400&fit=crop", // Cinema
                    // snacks
                    "https://images.unsplash.com/photo-1580745313093-4e79b5b3e6c0?w=300&h=400&fit=crop" // Movie
                    // memorabilia
                };
                imageUrl = productImages[Math.abs(product.getName().hashCode()) % productImages.length];
            }

            image.setImage(new Image(imageUrl));
        } catch (Exception e) {
            try {
                image.setImage(new Image("https://via.placeholder.com/140x180/555555/ffffff?text=" +
                    java.net.URLEncoder.encode(product.getName(), "UTF-8")));
            } catch (Exception ex) {
                image.setImage(new Image("https://via.placeholder.com/140x180/555555/ffffff?text=Product"));
            }

        }

        // Product name
        Label name = new Label(product.getName());
        name.setStyle(
            "-fx-text-fill: white;" +
                "-fx-font-family: 'Arial';" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-wrap-text: true;");
        name.setMaxWidth(140);
        name.setAlignment(Pos.CENTER);

        // Product price
        Label price = new Label("$" + String.format("%.2f", product.getPrice()));
        price.setStyle(
            "-fx-text-fill: #ff6666;" +
                "-fx-font-family: 'Arial';" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;");

        card.getChildren().addAll(image, name, price);

        // Add hover effect and click handler
        setupCardInteractions(card, () -> openProductDetails(product));

        return card;
    }

    /**
     * Attach hover visual effects and a click handler to a card VBox.
     * <p>
     * Applies styling changes for mouse enter/exit to indicate hover state and
     * executes
     * the provided action when the card is clicked.
     *
     * @param card          the VBox that represents the interactive card
     * @param onClickAction the runnable to execute when the card is clicked
     */
    private void setupCardInteractions(VBox card, Runnable onClickAction) {
        card.setOnMouseEntered(e -> {
            card.setStyle(
                card.getStyle() +
                    "-fx-border-color: rgba(139, 0, 0, 0.6);" +
                    "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.4), 15, 0, 0, 5);");
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(30, 30, 30, 0.9), rgba(20, 20, 20, 0.95));" +
                    "-fx-background-radius: 15;" +
                    "-fx-border-color: rgba(139, 0, 0, 0.3);" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 15;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0, 0, 3);" +
                    "-fx-padding: 10;" +
                    "-fx-cursor: hand;");
        });

        card.setOnMouseClicked(e -> onClickAction.run());
    }

    /**
     * Initialize and start all visual animations and dynamic decorative elements
     * used by the home interface.
     * <p>
     * Configures predefined particle and shape animations, applies content
     * animations (such as fade-ins and rotations),
     * and generates additional randomized dynamic particles and shapes added to the
     * scene.
     */
    private void setupAnimations() {
        setupParticleAnimations();
        setupShapeAnimations();
        setupContentAnimations();
        createDynamicParticles();
        createDynamicShapes();
    }

    /**
     * Generates and adds a set of randomized decorative particle nodes to the
     * particles container.
     * <p>
     * Creates multiple Circle particles with varied radius, position, opacity,
     * color/gradient fills,
     * drop shadows, and style classes, adds each to the controller's
     * dynamicParticles list and the
     * particlesContainer node, and starts per-particle advanced animations. The
     * method logs the
     * number of particles created and will log a warning if particle creation fails
     * or the container
     * is unavailable.
     */
    private void createDynamicParticles() {
        if (particlesContainer == null) {
            LOGGER.warning("Particles container is null, cannot create dynamic particles");
            return;
        }

        try {
            // Create 15 additional random particles with more variety
            for (int i = 0; i < 15; i++) {
                Circle particle = new Circle();

                // Random properties with better distribution
                double radius = 1.0 + random.nextGaussian() * 1.5 + 2.5; // Gaussian distribution around 2.5
                radius = Math.max(0.5, Math.min(5.0, radius)); // Clamp between 0.5 and 5.0

                double x = 50 + random.nextDouble() * 1350; // Better spread across screen
                double y = 50 + random.nextDouble() * 750; // Better spread across screen
                double opacity = 0.2 + random.nextDouble() * 0.6; // 0.2 to 0.8

                particle.setRadius(radius);
                particle.setLayoutX(x);
                particle.setLayoutY(y);
                particle.setOpacity(opacity);
                particle.setMouseTransparent(true);

                // More varied color palette with red theme
                String[] colorPalette = {
                    "#ff1111", "#ff2222", "#ff3333", "#ff4444", "#ff5555", "#ff6666", "#ff7777",
                    "#ee1111", "#ee2222", "#ee3333", "#dd1111", "#dd2222", "#cc1111", "#cc2222",
                    "#bb1111", "#aa1111", "#990000", "#880000", "#770000", "#660000"
                };
                String color = colorPalette[random.nextInt(colorPalette.length)];

                // Randomize gradient direction
                String[] gradientTypes = {
                    "radial-gradient(center 50% 50%, radius 50%, " + color + "aa, #660000aa)",
                    "radial-gradient(center 30% 30%, radius 70%, " + color + "cc, #440000cc)",
                    "radial-gradient(center 70% 70%, radius 60%, " + color + "88, #880000aa)",
                    "linear-gradient(45deg, " + color + "aa, #660000aa)",
                    "linear-gradient(135deg, " + color + "88, #440000cc)"
                };
                String gradient = gradientTypes[random.nextInt(gradientTypes.length)];

                double shadowRadius = 6 + random.nextDouble() * 12; // 6-18px shadow

                particle.setStyle(
                    "-fx-fill: " + gradient + ";" +
                        "-fx-effect: dropshadow(gaussian, " + color + "99, " + shadowRadius + ", 0, 0, 0);");

                // Assign random animation class combinations
                String[] animationClasses = {"floating-particle", "glow-red", "pulsing-shape"};
                StringBuilder classBuilder = new StringBuilder();
                for (String animClass : animationClasses) {
                    if (random.nextBoolean()) { // 50% chance for each class
                        classBuilder.append(animClass).append(" ");
                    }

                }

                if (classBuilder.length() > 0) {
                    particle.getStyleClass().addAll(classBuilder.toString().trim().split(" "));
                }

                // Add to containers
                dynamicParticles.add(particle);
                particlesContainer.getChildren().add(particle);

                // Setup individual animation with more variation
                setupAdvancedParticleAnimation(particle, random.nextDouble() * 5);
            }

            LOGGER.info("Created " + dynamicParticles.size() + " dynamic particles with advanced randomness");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error creating dynamic particles: " + e.getMessage(), e);
        }

    }

    /**
     * Generates and adds randomized decorative shapes to the particlesContainer and
     * starts their animations.
     * <p>
     * Creates rectangles, circles, triangles, and diamonds with randomized
     * position, size, rotation,
     * opacity, gradient fill, stroke, and drop shadow; assigns optional CSS
     * animation classes,
     * stores each shape in the dynamicShapes list, appends it to the
     * particlesContainer, and
     * invokes the advanced shape animation setup for each created shape.
     */
    private void createDynamicShapes() {
        if (particlesContainer == null)
            return;

        try {
            // Create 10 additional random shapes with more variety
            for (int i = 0; i < 10; i++) {
                javafx.scene.Node shape = null;
                double x = 30 + random.nextDouble() * 1400;
                double y = 30 + random.nextDouble() * 780;
                double opacity = 0.15 + random.nextDouble() * 0.5; // 0.15 to 0.65

                // Random shape selection with more variety
                int shapeType = random.nextInt(4);

                switch (shapeType) {
                    case 0: // Rectangle
                        Rectangle rect = new Rectangle();
                        rect.setWidth(8 + random.nextDouble() * 35);
                        rect.setHeight(6 + random.nextDouble() * 30);
                        rect.setLayoutX(x);
                        rect.setLayoutY(y);
                        rect.setOpacity(opacity);
                        rect.setRotate(random.nextDouble() * 360);
                        shape = rect;
                        break;

                    case 1: // Circle
                        Circle circle = new Circle();
                        circle.setRadius(4 + random.nextDouble() * 18);
                        circle.setLayoutX(x);
                        circle.setLayoutY(y);
                        circle.setOpacity(opacity);
                        shape = circle;
                        break;

                    case 2: // Triangle (Polygon)
                        Polygon triangle = new Polygon();
                        double size = 8 + random.nextDouble() * 20;
                        triangle.getPoints().addAll(new Double[]{
                            0.0, -size,
                            -size * 0.866, size * 0.5,
                            size * 0.866, size * 0.5
                        });
                        triangle.setLayoutX(x);
                        triangle.setLayoutY(y);
                        triangle.setOpacity(opacity);
                        triangle.setRotate(random.nextDouble() * 360);
                        shape = triangle;
                        break;

                    case 3: // Diamond (Polygon)
                        Polygon diamond = new Polygon();
                        double diamondSize = 6 + random.nextDouble() * 16;
                        diamond.getPoints().addAll(new Double[]{
                            0.0, -diamondSize,
                            diamondSize, 0.0,
                            0.0, diamondSize,
                            -diamondSize, 0.0
                        });
                        diamond.setLayoutX(x);
                        diamond.setLayoutY(y);
                        diamond.setOpacity(opacity);
                        diamond.setRotate(random.nextDouble() * 360);
                        shape = diamond;
                        break;
                }

                if (shape != null) {
                    // Enhanced color variety
                    String[] colorPalette = {
                        "#c80000", "#b40000", "#dc3232", "#a00000", "#e04444", "#cc1111",
                        "#d02020", "#b81818", "#f03030", "#c41515", "#e82828", "#bc0c0c"
                    };
                    String primaryColor = colorPalette[random.nextInt(colorPalette.length)];
                    String secondaryColor = colorPalette[random.nextInt(colorPalette.length)];

                    // Random gradient patterns
                    String[] gradientPatterns = {
                        "linear-gradient(to bottom right, " + primaryColor + "66, " + secondaryColor + "aa)",
                        "linear-gradient(45deg, " + primaryColor + "80, " + secondaryColor + "60)",
                        "linear-gradient(135deg, " + primaryColor + "70, " + secondaryColor + "90)",
                        "radial-gradient(center 50% 50%, radius 60%, " + primaryColor + "88, " + secondaryColor
                            + "66)",
                        "radial-gradient(center 30% 70%, radius 80%, " + primaryColor + "aa, " + secondaryColor
                            + "44)"
                    };
                    String gradient = gradientPatterns[random.nextInt(gradientPatterns.length)];

                    String strokeColor = colorPalette[random.nextInt(colorPalette.length)];
                    double strokeWidth = 0.5 + random.nextDouble() * 2.5; // 0.5-3.0px
                    double shadowRadius = 5 + random.nextDouble() * 15; // 5-20px

                    shape.setStyle(
                        "-fx-fill: " + gradient + ";" +
                            "-fx-stroke: " + strokeColor + ";" +
                            "-fx-stroke-width: " + strokeWidth + ";" +
                            "-fx-effect: dropshadow(gaussian, " + primaryColor + "80, " + shadowRadius
                            + ", 0, 0, 0);");

                    shape.setMouseTransparent(true);

                    // Random animation class assignments
                    String[] animationClasses = {"rotating-shape", "pulsing-shape", "glow-red"};
                    java.util.List<String> assignedClasses = new ArrayList<>();
                    for (String animClass : animationClasses) {
                        if (random.nextDouble() < 0.7) { // 70% chance for each class
                            assignedClasses.add(animClass);
                        }

                    }

                    if (!assignedClasses.isEmpty()) {
                        shape.getStyleClass().addAll(assignedClasses);
                    }

                    dynamicShapes.add(shape);
                    particlesContainer.getChildren().add(shape);

                    // Setup complex animation
                    setupAdvancedShapeAnimation(shape, random.nextDouble() * 3);
                }

            }

            LOGGER.info("Created " + dynamicShapes.size() + " dynamic shapes with enhanced randomness");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error creating dynamic shapes: " + e.getMessage(), e);
        }

    }

    /**
     * Applies a composite floating animation to a particle, combining vertical and
     * horizontal drift,
     * pulsing scale, and fading effects that run indefinitely.
     *
     * @param particle the Circle node to animate
     * @param delay    initial delay in seconds before the animations begin
     */
    private void setupAdvancedParticleAnimation(Circle particle, double delay) {
        // Complex floating animation with multiple axes
        TranslateTransition moveY = new TranslateTransition(
            Duration.seconds(2 + random.nextDouble() * 6), particle);
        moveY.setFromY(0);
        moveY.setToY(-10 - random.nextDouble() * 25);
        moveY.setCycleCount(Timeline.INDEFINITE);
        moveY.setAutoReverse(true);
        moveY.setDelay(Duration.seconds(delay));

        TranslateTransition moveX = new TranslateTransition(
            Duration.seconds(3 + random.nextDouble() * 8), particle);
        moveX.setFromX(0);
        moveX.setToX(-5 + random.nextDouble() * 10); // Random horizontal drift
        moveX.setCycleCount(Timeline.INDEFINITE);
        moveX.setAutoReverse(true);
        moveX.setDelay(Duration.seconds(delay + random.nextDouble()));

        // Advanced fade animation
        FadeTransition fade = new FadeTransition(
            Duration.seconds(1.5 + random.nextDouble() * 3), particle);
        fade.setFromValue(particle.getOpacity());
        fade.setToValue(Math.min(1.0, particle.getOpacity() + 0.2 + random.nextDouble() * 0.3));
        fade.setCycleCount(Timeline.INDEFINITE);
        fade.setAutoReverse(true);
        fade.setDelay(Duration.seconds(delay + random.nextDouble() * 2));

        // Scale pulsing
        ScaleTransition scale = new ScaleTransition(
            Duration.seconds(2 + random.nextDouble() * 4), particle);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.8 + random.nextDouble() * 0.6); // 0.8-1.4 scale
        scale.setToY(0.8 + random.nextDouble() * 0.6);
        scale.setCycleCount(Timeline.INDEFINITE);
        scale.setAutoReverse(true);
        scale.setDelay(Duration.seconds(delay + random.nextDouble() * 1.5));

        // Start all animations
        moveY.play();
        moveX.play();
        fade.play();
        scale.play();
    }

    /**
     * Attach and start a set of continuous visual animations on the given node.
     * <p>
     * Applies independent rotation, horizontal and vertical scaling, and opacity
     * oscillation to the provided node, and optionally a gentle translational
     * drift. Each animation uses randomized durations, targets, and direction and
     * runs indefinitely (with auto-reverse where applicable). Animations begin
     * after the specified delay.
     *
     * @param shape the node to animate (e.g., a Shape, Group, or Region)
     * @param delay initial delay in seconds before animations start
     */
    private void setupAdvancedShapeAnimation(javafx.scene.Node shape, double delay) {
        // Complex rotation animation
        RotateTransition rotation = new RotateTransition(
            Duration.seconds(4 + random.nextDouble() * 16), shape);
        rotation.setFromAngle(0);
        rotation.setToAngle(random.nextBoolean() ? 360 : -360); // Random direction
        rotation.setCycleCount(Timeline.INDEFINITE);
        rotation.setDelay(Duration.seconds(delay));

        // Multi-axis scale animation
        ScaleTransition scaleX = new ScaleTransition(
            Duration.seconds(3 + random.nextDouble() * 6), shape);
        scaleX.setFromX(1.0);
        scaleX.setToX(0.7 + random.nextDouble() * 0.8); // 0.7-1.5 scale
        scaleX.setCycleCount(Timeline.INDEFINITE);
        scaleX.setAutoReverse(true);
        scaleX.setDelay(Duration.seconds(delay + random.nextDouble()));

        ScaleTransition scaleY = new ScaleTransition(
            Duration.seconds(2.5 + random.nextDouble() * 5), shape);
        scaleY.setFromY(1.0);
        scaleY.setToY(0.8 + random.nextDouble() * 0.6); // 0.8-1.4 scale
        scaleY.setCycleCount(Timeline.INDEFINITE);
        scaleY.setAutoReverse(true);
        scaleY.setDelay(Duration.seconds(delay + random.nextDouble() * 2));

        // Opacity oscillation
        FadeTransition fade = new FadeTransition(
            Duration.seconds(2 + random.nextDouble() * 5), shape);
        fade.setFromValue(shape.getOpacity());
        fade.setToValue(Math.min(0.9, shape.getOpacity() + 0.1 + random.nextDouble() * 0.4));
        fade.setCycleCount(Timeline.INDEFINITE);
        fade.setAutoReverse(true);
        fade.setDelay(Duration.seconds(delay + random.nextDouble() * 3));

        // Optional drift animation for extra movement
        if (random.nextBoolean()) {
            TranslateTransition drift = new TranslateTransition(
                Duration.seconds(8 + random.nextDouble() * 12), shape);
            drift.setFromX(0);
            drift.setFromY(0);
            drift.setToX(-10 + random.nextDouble() * 20);
            drift.setToY(-8 + random.nextDouble() * 16);
            drift.setCycleCount(Timeline.INDEFINITE);
            drift.setAutoReverse(true);
            drift.setDelay(Duration.seconds(delay + random.nextDouble() * 4));
            drift.play();
        }

        // Start all animations
        rotation.play();
        scaleX.play();
        scaleY.play();
        fade.play();
    }

    /**
     * Initializes and starts floating animations for the particle nodes.
     * <p>
     * Attaches a looping floating animation to each of the six particle Circle
     * fields (if present)
     * and staggers their start times by 0.5 seconds per particle index to create a
     * natural, offset motion.
     */
    private void setupParticleAnimations() {
        Circle[] particles = {particle1, particle2, particle3, particle4, particle5, particle6};

        for (int i = 0; i < particles.length; i++) {
            if (particles[i] != null) {
                setupParticleAnimation(particles[i], i * 0.5);
            }

        }

    }

    /**
     * Applies a vertical floating translation and a pulsing fade animation to the
     * given particle.
     *
     * @param particle the Circle node to animate
     * @param delay    initial delay in seconds before the animation starts
     */
    private void setupParticleAnimation(Circle particle, double delay) {
        TranslateTransition moveTransition = new TranslateTransition(Duration.seconds(4), particle);
        moveTransition.setFromY(0);
        moveTransition.setToY(-20);
        moveTransition.setCycleCount(Timeline.INDEFINITE);
        moveTransition.setAutoReverse(true);
        moveTransition.setDelay(Duration.seconds(delay));
        moveTransition.play();

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), particle);
        fadeTransition.setFromValue(0.6);
        fadeTransition.setToValue(1.0);
        fadeTransition.setCycleCount(Timeline.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setDelay(Duration.seconds(delay));
        fadeTransition.play();
    }

    /**
     * Apply animated effects to decorative shape nodes in the UI.
     *
     * <p>
     * Starts continuous rotation on the available shape nodes used for background
     * decoration and
     * starts a pulsing scale/fade animation for the designated pulsing shape.
     * </p>
     *
     * <p>
     * Specifically:
     * - shape1: continuous rotation with a 10-second cycle
     * - shape2: continuous rotation with a 15-second cycle
     * - shape3: continuous rotation with an 8-second cycle
     * - shape5: continuous rotation with a 12-second cycle
     * - shape6: continuous rotation with a 6-second cycle
     * - shape4: pulsing (scale and fade) animation
     * </p>
     *
     * <p>
     * The method is null-safe and will skip any shape nodes that are not present.
     * </p>
     */
    private void setupShapeAnimations() {
        // Rotation animations
        if (shape1 != null)
            setupRotationAnimation(shape1, 10);
        if (shape2 != null)
            setupRotationAnimation(shape2, 15);
        if (shape3 != null)
            setupRotationAnimation(shape3, 8);
        if (shape5 != null)
            setupRotationAnimation(shape5, 12);
        if (shape6 != null)
            setupRotationAnimation(shape6, 6);

        // Pulsing animations
        if (shape4 != null)
            setupPulsingAnimation(shape4);
    }

    /**
     * Applies a continuous 360-degree rotation to the specified node.
     *
     * @param shape    the node to animate
     * @param duration the rotation period in seconds for one full 360° cycle
     */
    private void setupRotationAnimation(javafx.scene.Node shape, double duration) {
        RotateTransition rotation = new RotateTransition(Duration.seconds(duration), shape);
        rotation.setFromAngle(0);
        rotation.setToAngle(360);
        rotation.setCycleCount(Timeline.INDEFINITE);
        rotation.play();
    }

    /**
     * Applies a continuous pulsing scale and fade animation to the given node.
     * <p>
     * The animation scales the node up and down and adjusts its opacity in a
     * repeating cycle (~3 seconds per cycle).
     *
     * @param shape the JavaFX Node to animate with a pulsing effect
     */
    private void setupPulsingAnimation(javafx.scene.Node shape) {
        ScaleTransition scale = new ScaleTransition(Duration.seconds(3), shape);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.2);
        scale.setToY(1.2);
        scale.setCycleCount(Timeline.INDEFINITE);
        scale.setAutoReverse(true);
        scale.play();

        FadeTransition fade = new FadeTransition(Duration.seconds(3), shape);
        fade.setFromValue(0.6);
        fade.setToValue(0.9);
        fade.setCycleCount(Timeline.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();
    }

    /**
     * Applies a fade-in animation to the main scrollable content area when
     * available.
     *
     * <p>
     * If {@code mainScrollPane} is non-null, its opacity is animated from 0.0 to
     * 1.0 over 800 milliseconds.
     * </p>
     */
    private void setupContentAnimations() {
        // Fade in the main content
        if (mainScrollPane != null) {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainScrollPane);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }

    }

    /**
     * Attach a listener to the search field that triggers searches for queries
     * longer than two characters.
     *
     * <p>
     * If the injected `searchField` is non-null, a listener is added to its text
     * property which calls
     * {@code performSearch(String)} whenever the new text length exceeds 2
     * characters.
     * </p>
     */
    private void setupSearchFunctionality() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && newValue.length() > 2) {
                    performSearch(newValue);
                }

            });
        }

    }

    /**
     * Filter the currently displayed content using the provided search text.
     *
     * @param query the text used to filter visible movies, series, products, and
     *              cinemas
     */
    private void performSearch(String query) {
        // This would filter the displayed content based on the search query
        // Implementation would depend on your specific search requirements
        LOGGER.info("Searching for: " + query);
    }

    /**
     * Applies initial UI styling by adding the "fade-in" CSS class to the root
     * container.
     * <p>
     * This triggers the initial load animation when the root container is
     * available.
     */
    private void applyInitialStyling() {
        // Add CSS classes for animations
        if (rootContainer != null) {
            rootContainer.getStyleClass().add("fade-in");
        }

    }

    /**
     * Loads the movies view (filmuser.fxml) and replaces the current window's scene
     * with it.
     *
     * @param event the ActionEvent that triggered this navigation
     */

    @FXML
    void showMovies(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/films/filmuser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading movies interface: " + e.getMessage(), e);
        }

    }

    /**
     * Navigates the application to the Series client view by loading and setting
     * "SeriesClient.fxml"
     * as the current scene on the existing stage.
     *
     * @param event the action event that triggered the navigation
     */
    @FXML
    void showSeries(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/SeriesClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading series interface: " + e.getMessage(), e);
        }

    }

    /**
     * Loads the product listing UI (AfficherProduitClient.fxml) into the current
     * window.
     *
     * @param event the ActionEvent that triggered the navigation
     */
    @FXML
    void showProducts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/products/AfficherProduitClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading products interface: " + e.getMessage(), e);
        }

    }

    /**
     * Navigates the application to the cinemas dashboard view by loading and
     * setting DashboardClientCinema.fxml as the current scene.
     * <p>
     * If loading the FXML fails, the error is logged and the current scene is left
     * unchanged.
     */
    @FXML
    void showCinemas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/cinemas/DashboardClientCinema.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading cinemas interface: " + e.getMessage(), e);
        }

    }

    /**
     * Navigates the application to the user's profile view by loading the
     * Profile.fxml
     * and replacing the current scene with the loaded content.
     *
     * @param event the ActionEvent that triggered the navigation
     */
    @FXML
    void showProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/Profile.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading profile interface: " + e.getMessage(), e);
        }

    }

    /**
     * Clears the current user's session data and navigates the application back to
     * the login screen.
     * <p>
     * Loads the login FXML, clears the stage's user data, and replaces the scene
     * with the login scene.
     *
     * @param event the ActionEvent that triggered the logout
     */
    @FXML
    void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setUserData(null); // Clear user data
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error during logout: " + e.getMessage(), e);
        }

    }

    /**
     * Open the movie details view for the given film and navigate to the movies
     * screen.
     *
     * @param film the Film whose details should be displayed
     */

    private void openMovieDetails(Film film) {
        LOGGER.info("Opening movie details for: " + film.getTitle());
        // Navigate to movie details or show movie player
        try {
            showMovies(null);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error opening movie details: " + e.getMessage(), e);
        }

    }

    /**
     * Open the detail view for the given series and navigate to the series screen.
     *
     * @param series the series whose details should be displayed
     */
    private void openSeriesDetails(Series series) {
        LOGGER.info("Opening series details for: " + series.getName());
        // Navigate to series details or episode list
        try {
            showSeries(null);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error opening series details: " + e.getMessage(), e);
        }

    }

    /**
     * Initiates navigation to the products view for the given product (for viewing
     * details or adding to cart).
     *
     * @param product the product to present in the products view
     */
    private void openProductDetails(Product product) {
        LOGGER.info("Opening product details for: " + product.getName());
        // Navigate to product details or add to cart
        try {
            showProducts(null);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error opening product details: " + e.getMessage(), e);
        }

    }

    /**
     * Populate the movies container with a predefined set of placeholder movie
     * cards.
     * <p>
     * This method clears the current movies container and fills it with a fixed
     * list
     * of popular movie titles and short descriptors to display when the film
     * service
     * fails or returns no data.
     */

    private void createPlaceholderMovies() {
        String[] placeholderMovies = {
            "The Batman", "Dune", "Joker", "Oppenheimer", "Spider-Man: Across the Spider-Verse",
            "Avengers: Endgame", "The Dark Knight", "Inception", "Interstellar", "The Matrix"
        };

        String[] movieDescriptions = {
            "2022 • Action", "2021 • Sci-Fi", "2019 • Drama", "2023 • Biography", "2023 • Animation",
            "2019 • Action", "2008 • Action", "2010 • Sci-Fi", "2014 • Sci-Fi", "1999 • Sci-Fi"
        };

        moviesContainer.getChildren().clear();
        for (int i = 0; i < placeholderMovies.length; i++) {
            VBox card = createRichPlaceholderCard(placeholderMovies[i], movieDescriptions[i], "Movie");
            moviesContainer.getChildren().add(card);
        }

    }

    /**
     * Populates the series container with a fixed set of rich placeholder series
     * cards.
     * <p>
     * Creates a predefined list of series titles and short descriptions, builds a
     * rich placeholder
     * card for each via createRichPlaceholderCard, and adds them to the
     * seriesContainer.
     */
    private void createPlaceholderSeries() {
        String[] placeholderSeries = {
            "Breaking Bad", "Game of Thrones", "The Office", "Stranger Things", "The Crown",
            "The Mandalorian", "House of the Dragon", "Wednesday", "The Witcher", "Peaky Blinders"
        };

        String[] seriesDescriptions = {
            "5 Seasons • Drama", "8 Seasons • Fantasy", "9 Seasons • Comedy", "4 Seasons • Sci-Fi",
            "6 Seasons • Drama",
            "3 Seasons • Sci-Fi", "1 Season • Fantasy", "1 Season • Comedy", "3 Seasons • Fantasy",
            "6 Seasons • Crime"
        };

        seriesContainer.getChildren().clear();
        for (int i = 0; i < placeholderSeries.length; i++) {
            VBox card = createRichPlaceholderCard(placeholderSeries[i], seriesDescriptions[i], "Series");
            seriesContainer.getChildren().add(card);
        }

    }

    /**
     * Populate the products container with a predefined set of placeholder product
     * cards.
     * <p>
     * Clears the container and adds rich placeholder cards for a fixed list of
     * product names
     * paired with example prices to be shown when real product data is unavailable.
     */
    private void createPlaceholderProducts() {
        String[] placeholderProducts = {
            "Premium Popcorn", "VIP Movie Tickets", "Gourmet Candy Mix", "Specialty Drinks", "Movie Merchandise",
            "Collector's Edition", "Director's Cut", "Limited Edition", "Exclusive Bundle", "Gift Cards"
        };

        String[] productPrices = {
            "$8.99", "$15.99", "$6.49", "$4.99", "$12.99",
            "$24.99", "$19.99", "$29.99", "$39.99", "$25.00"
        };

        productsContainer.getChildren().clear();
        for (int i = 0; i < placeholderProducts.length; i++) {
            VBox card = createRichPlaceholderCard(placeholderProducts[i], productPrices[i], "Product");
            productsContainer.getChildren().add(card);
        }

    }

    /**
     * Builds a styled placeholder card to represent content when real data is
     * unavailable.
     * <p>
     * The card includes an image (selected by title and type with a network
     * fallback), a title,
     * a short description, a type label, and wired hover/click interactions for
     * user feedback.
     *
     * @param title       the display title for the placeholder card
     * @param description a short descriptive text to show under the title
     * @param type        the content category (for example "Movie", "Series", or
     *                    "Product") used to choose an appropriate image and label
     * @return a configured VBox representing the rich placeholder card
     */
    private VBox createRichPlaceholderCard(String title, String description, String type) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(160);
        card.setPrefHeight(280);
        card.getStyleClass().add("content-card");
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgba(35, 35, 35, 0.9), rgba(25, 25, 25, 0.95));" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(139, 0, 0, 0.4);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0, 0, 3);" +
                "-fx-padding: 10;" +
                "-fx-cursor: hand;");

        // Enhanced image with proper sources
        ImageView imageView = new ImageView();
        imageView.setFitWidth(140);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-background-radius: 10;");

        String imageUrl = getImageUrlForType(title, type);

        try {
            imageView.setImage(new Image(imageUrl));
        } catch (Exception e) {
            // Fallback to a more reliable placeholder
            try {
                String fallbackUrl = "https://via.placeholder.com/140x200/666666/ffffff?text=" +
                    java.net.URLEncoder.encode(type, "UTF-8");
                imageView.setImage(new Image(fallbackUrl));
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Error loading placeholder image: " + ex.getMessage());
            }

        }

        // Enhanced title
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-text-fill: #ffffff;" +
                "-fx-font-family: 'Arial';" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-wrap-text: true;" +
                "-fx-text-alignment: center;");
        titleLabel.setMaxWidth(140);
        titleLabel.setAlignment(Pos.CENTER);

        // Enhanced description/info
        Label descLabel = new Label(description);
        descLabel.setStyle(
            "-fx-text-fill: #cccccc;" +
                "-fx-font-family: 'Arial';" +
                "-fx-font-size: 10px;" +
                "-fx-text-alignment: center;");
        descLabel.setMaxWidth(140);
        descLabel.setAlignment(Pos.CENTER);

        // Type indicator
        Label typeLabel = new Label(type.toUpperCase());
        typeLabel.setStyle(
            "-fx-text-fill: #ff6666;" +
                "-fx-font-family: 'Arial';" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;");
        typeLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imageView, titleLabel, descLabel, typeLabel);

        // Enhanced hover effect
        setupCardInteractions(card, () -> LOGGER.info("Clicked rich placeholder: " + title + " (" + type + ")"));

        return card;
    }

    /**
     * Selects a stable placeholder image URL for a given content item.
     *
     * @param title the content title used to deterministically choose an image
     * @param type  the content category such as "Movie", "Series", "Product", or
     *              "Cinema"
     * @return a URL string pointing to an image appropriate for the given type (or
     * a generic placeholder URL if the type is unrecognized)
     */
    private String getImageUrlForType(String title, String type) {
        switch (type) {
            case "Movie":
                String[] movieImages = {
                    "https://image.tmdb.org/t/p/w500/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg", // The Batman
                    "https://image.tmdb.org/t/p/w500/d5NXSklXo0qyIYkgV94XAgMIckC.jpg", // Dune
                    "https://image.tmdb.org/t/p/w500/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg", // Joker
                    "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg", // Oppenheimer
                    "https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg", // Spider-Man
                    "https://image.tmdb.org/t/p/w500/cvsXj3I9Q2iyyIo95AecSd1tad7.jpg", // Avengers Endgame
                    "https://image.tmdb.org/t/p/w500/aosm8NMQ3UyoBVpSxyimorCQykC.jpg", // The Dark Knight
                    "https://image.tmdb.org/t/p/w500/rr7E0NoGKxvbkb89eR1GwfoYjpA.jpg", // Inception
                    "https://image.tmdb.org/t/p/w500/pFlaoHTZeyNkG83vxsAJiGzfSsa.jpg", // Interstellar
                    "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg" // The Matrix
                };
                return movieImages[Math.abs(title.hashCode()) % movieImages.length];

            case "Series":
                String[] seriesImages = {
                    "https://image.tmdb.org/t/p/w500/suopoADq0k8YZr4dQXcU6pToj6s.jpg", // Breaking Bad
                    "https://image.tmdb.org/t/p/w500/1XS1oqL89opfnbLl8WnZY1O1uJx.jpg", // Game of Thrones
                    "https://image.tmdb.org/t/p/w500/qWnJzyZhyy74gjpSjIXWmuk0ifX.jpg", // The Office
                    "https://image.tmdb.org/t/p/w500/4UjiPdFKJGJYdxwRs2Rzg7EmWqr.jpg", // Stranger Things
                    "https://image.tmdb.org/t/p/w500/1M876KPjulVwppEpldhdc8V4o68.jpg", // The Crown
                    "https://image.tmdb.org/t/p/w500/9yBVqNruk6Ykrwc32qrK2TIE5xw.jpg", // The Mandalorian
                    "https://image.tmdb.org/t/p/w500/xUtOM1QO4r8w8yeE00QvBdq58N5.jpg", // House of the Dragon
                    "https://image.tmdb.org/t/p/w500/1BIoJGKbXvdLDVv01hJR4VQ6vTX.jpg", // Wednesday
                    "https://image.tmdb.org/t/p/w500/7WUHnWGx5OO145IRxPDUkQSh4C7.jpg", // The Witcher
                    "https://image.tmdb.org/t/p/w500/mY7SeH4HFFxW1hiI6cWuwCRKptN.jpg" // Peaky Blinders
                };
                return seriesImages[Math.abs(title.hashCode()) % seriesImages.length];

            case "Product":
                String[] productImages = {
                    "https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?w=300&h=400&fit=crop", // Popcorn
                    "https://images.unsplash.com/photo-1627662235719-d9e9f31c4f73?w=300&h=400&fit=crop", // Movie
                    // tickets
                    "https://images.unsplash.com/photo-1528827871709-dbe8fe4e57f8?w=300&h=400&fit=crop", // Cinema
                    // candy
                    "https://images.unsplash.com/photo-1509924603848-78edc5642dea?w=300&h=400&fit=crop", // Cinema
                    // drinks
                    "https://images.unsplash.com/photo-1616530940355-351fabd9524b?w=300&h=400&fit=crop", // Movie
                    // merchandise
                    "https://images.unsplash.com/photo-1489599446190-c9ad1d88c3dc?w=300&h=400&fit=crop", // Cinema
                    // items
                    "https://images.unsplash.com/photo-1514306191717-452ec28c7814?w=300&h=400&fit=crop", // Movie
                    // reel
                    "https://images.unsplash.com/photo-1596473499708-4ff13b15f4cc?w=300&h=400&fit=crop", // Cinema
                    // memorabilia
                    "https://images.unsplash.com/photo-1580745313093-4e79b5b3e6c0?w=300&h=400&fit=crop", // Movie
                    // collectibles
                    "https://images.unsplash.com/photo-1574375927938-d5a98e8ffe85?w=300&h=400&fit=crop" // Cinema
                    // accessories
                };
                return productImages[Math.abs(title.hashCode()) % productImages.length];

            case "Cinema":
                String[] cinemaImages = {
                    "https://images.unsplash.com/photo-1489599446190-c9ad1d88c3dc?w=300&h=400&fit=crop", // Cinema
                    // theater
                    "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=300&h=400&fit=crop", // Movie
                    // theater
                    "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=300&h=400&fit=crop", // Cinema
                    // seats
                    "https://images.unsplash.com/photo-1542204165-65bf26472b9b?w=300&h=400&fit=crop", // Cinema
                    // interior
                    "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=300&h=400&fit=crop", // Movie
                    // screen
                    "https://images.unsplash.com/photo-1556909459-f3a22c77cd8d?w=300&h=400&fit=crop", // Cinema
                    // lobby
                    "https://images.unsplash.com/photo-1574267432553-4b4628081c31?w=300&h=400&fit=crop", // Cinema
                    // hall
                    "https://images.unsplash.com/photo-1518676590629-3dcbd9c5a5c9?w=300&h=400&fit=crop", // Theater
                    // lights
                    "https://images.unsplash.com/photo-1524712245354-2c4e5e7121c0?w=300&h=400&fit=crop", // Movie
                    // posters
                    "https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=300&h=400&fit=crop" // Cinema
                    // exterior
                };
                return cinemaImages[Math.abs(title.hashCode()) % cinemaImages.length];

            default:
                return "https://via.placeholder.com/140x200/555555/ffffff?text=" + type;
        }

    }

    /**
     * Builds a styled VBox representing a cinema card with image, name, and
     * address,
     * and attaches hover and click interactions that open the cinema details view.
     *
     * @param cinema the Cinema whose data (name, address, logo path) populates the
     *               card
     * @return a configured VBox node that visually represents the cinema and
     * handles user interaction
     */
    private VBox createCinemaCard(Cinema cinema) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(160);
        card.setPrefHeight(280);
        card.getStyleClass().add("content-card");
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgba(30, 30, 30, 0.9), rgba(20, 20, 20, 0.95));" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(139, 0, 0, 0.3);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0, 0, 3);" +
                "-fx-padding: 10;" +
                "-fx-cursor: hand;");

        // Cinema image
        ImageView image = new ImageView();
        image.setFitWidth(140);
        image.setFitHeight(180);
        image.setPreserveRatio(true);
        image.setStyle("-fx-background-radius: 10;");

        try {
            String imageUrl = null;
            if (cinema.getLogoUrl() != null && !cinema.getLogoUrl().isEmpty()) {
                imageUrl = cinema.getLogoUrl();
            } else {
                // Use cinema-related images
                String[] cinemaImages = {
                    "https://images.unsplash.com/photo-1489599446190-c9ad1d88c3dc?w=300&h=400&fit=crop", // Cinema
                    // theater
                    "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=300&h=400&fit=crop", // Movie
                    // theater
                    "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=300&h=400&fit=crop", // Cinema
                    // seats
                    "https://images.unsplash.com/photo-1542204165-65bf26472b9b?w=300&h=400&fit=crop", // Cinema
                    // interior
                    "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=300&h=400&fit=crop", // Movie
                    // screen
                    "https://images.unsplash.com/photo-1556909459-f3a22c77cd8d?w=300&h=400&fit=crop", // Cinema
                    // lobby
                    "https://images.unsplash.com/photo-1574267432553-4b4628081c31?w=300&h=400&fit=crop", // Cinema
                    // hall
                    "https://images.unsplash.com/photo-1518676590629-3dcbd9c5a5c9?w=300&h=400&fit=crop", // Theater
                    // lights
                    "https://images.unsplash.com/photo-1524712245354-2c4e5e7121c0?w=300&h=400&fit=crop", // Movie
                    // posters
                    "https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=300&h=400&fit=crop" // Cinema
                    // exterior
                };
                imageUrl = cinemaImages[Math.abs(cinema.getName().hashCode()) % cinemaImages.length];
            }

            image.setImage(new Image(imageUrl));
        } catch (Exception e) {
            try {
                image.setImage(new Image("https://via.placeholder.com/140x180/555555/ffffff?text=" +
                    java.net.URLEncoder.encode(cinema.getName(), "UTF-8")));
            } catch (Exception ex) {
                image.setImage(new Image("https://via.placeholder.com/140x180/555555/ffffff?text=Cinema"));
            }

        }

        // Cinema name
        Label name = new Label(cinema.getName());
        name.setStyle(
            "-fx-text-fill: white;" +
                "-fx-font-family: 'Arial';" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-wrap-text: true;");
        name.setMaxWidth(140);
        name.setAlignment(Pos.CENTER);

        // Cinema address
        Label address = new Label(cinema.getAddress() != null ? cinema.getAddress() : "Location");
        address.setStyle(
            "-fx-text-fill: #cccccc;" +
                "-fx-font-family: 'Arial';" +
                "-fx-font-size: 10px;");
        address.setMaxWidth(140);
        address.setAlignment(Pos.CENTER);

        card.getChildren().addAll(image, name, address);

        // Add hover effect and click handler
        setupCardInteractions(card, () -> openCinemaDetails(cinema));

        return card;
    }

    /**
     * Configures and starts an enhanced horizontal carousel auto-scroll for a
     * content HBox and its ScrollPane.
     * <p>
     * This method ensures the container has enough items (adds placeholders when
     * needed), adjusts scroll pane
     * policies for smooth auto-scrolling, creates a continuous Timeline to advance
     * the horizontal scroll value
     * with a graceful reset near the end, and installs hover interactions that
     * pause the auto-scroll, enable
     * manual panning, and provide brief scale-based visual feedback. It also adds a
     * subtle scroll indicator
     * animation and schedules the animation to start after layout completion.
     * Errors are caught and logged.
     *
     * @param container  the HBox containing the horizontal content items to be
     *                   scrolled
     * @param scrollPane the ScrollPane that displays the container and will be
     *                   driven by the carousel animation
     */
    private void setupCarouselAnimation(HBox container, ScrollPane scrollPane) {
        if (container == null || scrollPane == null)
            return;

        try {
            // Ensure we have enough content to scroll
            if (container.getChildren().size() < 8) {
                // Add more placeholder content to make scrolling visible
                createAdditionalPlaceholderContent(container);
                LOGGER.info("Added additional content. Container now has " + container.getChildren().size() + " items");
            }

            // Set up the scroll pane properties for smooth scrolling
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setFitToHeight(true);
            scrollPane.setPannable(false); // Disable manual panning during auto-scroll

            // Create more visible auto-scroll animation
            Timeline autoScroll = new Timeline(
                new KeyFrame(Duration.millis(30), e -> { // Faster updates for smoother animation
                    double currentHValue = scrollPane.getHvalue();
                    double increment = 0.005; // Visible but smooth increment

                    if (currentHValue >= 0.95) { // Reset before the end for smoother transition
                        // Smooth reset to beginning with fade effect
                        Timeline resetAnimation = new Timeline(
                            new KeyFrame(Duration.millis(800),
                                new KeyValue(scrollPane.hvalueProperty(), 0.0, Interpolator.EASE_BOTH)));
                        resetAnimation.play();
                        LOGGER.info("Carousel reset to beginning for container");
                    } else {
                        scrollPane.setHvalue(currentHValue + increment);

                        // Log progress every 20% for debugging
                        if ((int) (currentHValue * 100) % 20 == 0) {
                            LOGGER.info("Carousel progress: " + String.format("%.1f%%", currentHValue * 100));
                        }

                    }

                }));
            autoScroll.setCycleCount(Timeline.INDEFINITE);

            // Enhanced hover interactions with visual feedback
            EventHandler<MouseEvent> pauseHandler = e -> {
                autoScroll.pause();
                scrollPane.setPannable(true); // Allow manual scrolling when paused

                // Add visual feedback for pause
                ScaleTransition pauseFeedback = new ScaleTransition(Duration.millis(150), container);
                pauseFeedback.setToX(1.02);
                pauseFeedback.setToY(1.02);
                pauseFeedback.play();

                LOGGER.info("Carousel paused on hover");
            };

            EventHandler<MouseEvent> resumeHandler = e -> {
                autoScroll.play();
                scrollPane.setPannable(false); // Disable manual scrolling during auto-scroll

                // Return to normal scale
                ScaleTransition resumeFeedback = new ScaleTransition(Duration.millis(150), container);
                resumeFeedback.setToX(1.0);
                resumeFeedback.setToY(1.0);
                resumeFeedback.play();

                LOGGER.info("Carousel resumed");
            };

            container.setOnMouseEntered(pauseHandler);
            scrollPane.setOnMouseEntered(pauseHandler);
            container.setOnMouseExited(resumeHandler);
            scrollPane.setOnMouseExited(resumeHandler);

            // Add visual scroll indicator (subtle animation on container)
            setupScrollIndicator(container);

            // Start the animation with a small delay to ensure layout is complete
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(500); // Give more time for layout
                    autoScroll.play();
                    LOGGER.info("Carousel animation started for " + container.getChildren().size() + " items");
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

            });

            LOGGER.info("Enhanced carousel animation setup for container with " + container.getChildren().size()
                + " items");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error setting up carousel animation: " + e.getMessage(), e);
        }

    }

    /**
     * Populate the given horizontal container with rich placeholder cards until it
     * contains at least twelve items.
     *
     * <p>
     * The method inspects the provided container to infer a content type (Movie,
     * Series, Product, or Cinema)
     * and appends rich placeholder cards created via
     * {@code createRichPlaceholderCard} until the container's
     * child count reaches a minimum of 12. Placeholder titles and descriptions are
     * chosen cyclically from
     * predefined arrays and numbered to produce distinct entries.
     * </p>
     *
     * @param container the HBox to populate (expected to be one of moviesContainer,
     *                  seriesContainer,
     *                  productsContainer, or cinemasContainer); additional children
     *                  will be appended to
     *                  enable visible horizontal scrolling
     */
    private void createAdditionalPlaceholderContent(HBox container) {
        int currentSize = container.getChildren().size();
        int targetSize = Math.max(12, currentSize); // Ensure at least 12 items for very visible scrolling

        // Determine content type based on container context
        String contentType = "Content";
        if (container == moviesContainer) {
            contentType = "Movie";
        } else if (container == seriesContainer) {
            contentType = "Series";
        } else if (container == productsContainer) {
            contentType = "Product";
        } else if (container == cinemasContainer) {
            contentType = "Cinema";
        }

        String[] contentTitles = {
            "Featured " + contentType, "Popular " + contentType, "Trending " + contentType,
            "New Release", "Top Rated", "Editor's Choice", "Premium Selection",
            "Exclusive Content", "Staff Pick", "Must Watch", "Bestseller", "Award Winner"
        };

        String[] contentDescriptions = {
            "Premium Content", "Highly Rated", "Most Watched", "Latest Addition",
            "Five Stars", "Staff Pick", "VIP Selection", "Exclusive Access",
            "Curated Choice", "Fan Favorite", "Top Seller", "Critics' Choice"
        };

        for (int i = currentSize; i < targetSize; i++) {
            String title = contentTitles[i % contentTitles.length] + " " + (i + 1);
            String description = contentDescriptions[i % contentDescriptions.length];
            VBox placeholderCard = createRichPlaceholderCard(title, description, contentType);
            container.getChildren().add(placeholderCard);
        }

    }

    /**
     * Apply a subtle pulsing glow to indicate that the horizontal container is
     * scrollable.
     * <p>
     * Adds a repeating Timeline that animates the container's opacity to create a
     * gentle visual hint
     * that the HBox can be scrolled horizontally.
     *
     * @param container the HBox to receive the pulsing glow indicator
     */
    private void setupScrollIndicator(HBox container) {
        // Add subtle glow effect that pulses to indicate scrolling
        Timeline glowAnimation = new Timeline(
            new KeyFrame(Duration.seconds(0), new KeyValue(container.opacityProperty(), 0.95)),
            new KeyFrame(Duration.seconds(2), new KeyValue(container.opacityProperty(), 1.0)),
            new KeyFrame(Duration.seconds(4), new KeyValue(container.opacityProperty(), 0.95)));
        glowAnimation.setCycleCount(Timeline.INDEFINITE);
        glowAnimation.play();
    }

    /**
     * Populates the cinemas container with a fixed set of placeholder cinema cards.
     * <p>
     * Clears any existing children from the cinemas container and adds ten
     * placeholder
     * cinema cards using predefined names and locations for display when real data
     * is unavailable.
     */
    private void createPlaceholderCinemas() {
        String[] placeholderCinemas = {
            "CineMax Plaza", "MovieWorld IMAX", "Star Cinema Complex", "Grand Theater", "Royal Movies",
            "Premiere Cinemas", "Silver Screen", "Diamond Theater", "Elite Cinemas", "Luxury Movies"
        };

        String[] cinemaLocations = {
            "Downtown", "Mall District", "City Center", "Uptown", "Suburbs",
            "Shopping Plaza", "Entertainment Zone", "Metro Center", "Business District", "West Side"
        };

        cinemasContainer.getChildren().clear();
        for (int i = 0; i < placeholderCinemas.length; i++) {
            VBox card = createRichPlaceholderCard(placeholderCinemas[i], cinemaLocations[i], "Cinema");
            cinemasContainer.getChildren().add(card);
        }

    }

    /**
     * Navigates to the cinemas view and attempts to display details for the
     * specified cinema.
     *
     * @param cinema the cinema whose details should be presented
     */
    private void openCinemaDetails(Cinema cinema) {
        LOGGER.info("Opening cinema details for: " + cinema.getName());
        // Navigate to cinema details or show cinema information
        try {
            showCinemas(null);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error opening cinema details: " + e.getMessage(), e);
        }

    }

    /**
     * Navigate to the Movies view.
     *
     * @param event the action event that triggered this handler
     */
    @FXML
    void browseMovies(ActionEvent event) {
        showMovies(event);
    }

    /**
     * Navigate to the cinemas view.
     *
     * @param event the action event that triggered the navigation
     */
    @FXML
    void findCinemas(ActionEvent event) {
        showCinemas(event);
    }

    /**
     * Navigates the application to the series listing view.
     *
     * @param event the action event that triggered the navigation
     */
    @FXML
    void browseSeries(ActionEvent event) {
        showSeries(event);
    }

    /**
     * Navigate to the user's profile view.
     *
     * @param event the action event that triggered the navigation
     */
    @FXML
    void viewProfile(ActionEvent event) {
        showProfile(event);
    }

    /**
     * Stop animation timelines and clear dynamic particle and shape collections
     * used by the controller.
     * <p>
     * Performs a best-effort cleanup of resources created by this controller and
     * logs outcome; exceptions are caught and logged.
     */
    public void cleanup() {
        try {
            if (featuredRotationTimeline != null) {
                featuredRotationTimeline.stop();
            }

            if (particleAnimationTimeline != null) {
                particleAnimationTimeline.stop();
            }

            // Clear dynamic particles and shapes
            if (dynamicParticles != null) {
                dynamicParticles.clear();
            }

            if (dynamicShapes != null) {
                dynamicShapes.clear();
            }

            LOGGER.info("HomeClient controller cleanup completed");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error during cleanup: " + e.getMessage(), e);
        }

    }

}
