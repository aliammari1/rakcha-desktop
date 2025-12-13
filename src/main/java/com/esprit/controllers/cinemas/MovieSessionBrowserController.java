package com.esprit.controllers.cinemas;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.films.Film;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.services.films.FilmService;
import com.esprit.utils.SlideOverNavigator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller for browsing movie sessions - allows users to find and filter
 * available movie sessions across cinemas.
 */
public class MovieSessionBrowserController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(MovieSessionBrowserController.class.getName());
    private final MovieSessionService sessionService = new MovieSessionService();
    private final FilmService filmService = new FilmService();
    private final CinemaService cinemaService = new CinemaService();
    private final ObservableList<MovieSession> allSessions = FXCollections.observableArrayList();
    private final ObservableList<MovieSession> filteredSessions = FXCollections.observableArrayList();
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> cinemaFilter;
    @FXML
    private ComboBox<String> genreFilter;
    @FXML
    private DatePicker dateFilter;
    @FXML
    private ComboBox<String> formatFilter;
    @FXML
    private FlowPane sessionsGrid;
    @FXML
    private ScrollPane sessionsScroll;
    @FXML
    private FlowPane quickFiltersPane;
    @FXML
    private Label resultsCountLabel;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private VBox emptyStatePane;
    private String selectedQuickFilter = "all";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupFilters();
        setupQuickFilters();
        setupSearch();
        loadSessions();
    }

    private void setupFilters() {
        // Cinema filter
        List<Cinema> cinemas = cinemaService.getAll();
        List<String> cinemaNames = cinemas.stream()
                .map(Cinema::getName)
                .collect(Collectors.toList());
        cinemaNames.add(0, "All Cinemas");
        cinemaFilter.setItems(FXCollections.observableArrayList(cinemaNames));
        cinemaFilter.getSelectionModel().selectFirst();
        cinemaFilter.setOnAction(e -> applyFilters());

        // Genre filter
        List<String> genres = Arrays.asList("All Genres", "Action", "Comedy", "Drama", "Horror", "Romance", "Sci-Fi",
                "Thriller");
        genreFilter.setItems(FXCollections.observableArrayList(genres));
        genreFilter.getSelectionModel().selectFirst();
        genreFilter.setOnAction(e -> applyFilters());

        // Date filter
        dateFilter.setValue(LocalDate.now());
        dateFilter.setOnAction(e -> applyFilters());

        // Format filter
        List<String> formats = Arrays.asList("All Formats", "2D", "3D", "IMAX", "Dolby Atmos", "4DX");
        formatFilter.setItems(FXCollections.observableArrayList(formats));
        formatFilter.getSelectionModel().selectFirst();
        formatFilter.setOnAction(e -> applyFilters());
    }

    private void setupQuickFilters() {
        String[] filters = { "All", "Now Showing", "Coming Soon", "Today", "This Week", "IMAX", "Premieres" };

        for (String filter : filters) {
            Button chip = createFilterChip(filter);
            if (quickFiltersPane != null) {
                quickFiltersPane.getChildren().add(chip);
            }
        }
    }

    private Button createFilterChip(String label) {
        Button chip = new Button(label);
        chip.getStyleClass().add("filter-chip");

        if (label.equalsIgnoreCase("All")) {
            chip.getStyleClass().add("active");
        }

        chip.setOnAction(e -> {
            // Remove active from all chips
            quickFiltersPane.getChildren().forEach(node -> {
                if (node instanceof Button) {
                    node.getStyleClass().remove("active");
                }
            });
            chip.getStyleClass().add("active");
            selectedQuickFilter = label.toLowerCase().replace(" ", "-");
            applyFilters();
        });

        return chip;
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
    }

    private void loadSessions() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<MovieSession> sessions = sessionService.getAll();

                Platform.runLater(() -> {
                    allSessions.setAll(sessions);
                    applyFilters();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to load sessions", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load sessions");
                });
            }
        }).start();
    }

    private void applyFilters() {
        List<MovieSession> filtered = new ArrayList<>(allSessions);

        // Apply search filter
        String searchText = searchField.getText().toLowerCase().trim();
        if (!searchText.isEmpty()) {
            filtered = filtered.stream()
                    .filter(s -> s.getFilm().getTitle().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
        }

        // Apply cinema filter
        String selectedCinema = cinemaFilter.getValue();
        if (selectedCinema != null && !selectedCinema.equals("All Cinemas")) {
            filtered = filtered.stream()
                    .filter(s -> s.getCinemaHall().getCinema().getName().equals(selectedCinema))
                    .collect(Collectors.toList());
        }

        // Apply date filter
        LocalDate selectedDate = dateFilter.getValue();
        if (selectedDate != null) {
            filtered = filtered.stream()
                    .filter(s -> s.getStartTime().toLocalDate().equals(selectedDate))
                    .collect(Collectors.toList());
        }

        // Apply quick filter
        filtered = applyQuickFilter(filtered);

        filteredSessions.setAll(filtered);
        displaySessions(filteredSessions);
        updateResultsCount(filteredSessions.size());
    }

    private List<MovieSession> applyQuickFilter(List<MovieSession> sessions) {
        LocalDate today = LocalDate.now();

        return switch (selectedQuickFilter) {
            case "today" -> sessions.stream()
                    .filter(s -> s.getStartTime().toLocalDate().equals(today))
                    .collect(Collectors.toList());
            case "this-week" -> sessions.stream()
                    .filter(s -> {
                        LocalDate sessionDate = s.getStartTime().toLocalDate();
                        return !sessionDate.isBefore(today) && !sessionDate.isAfter(today.plusDays(7));
                    })
                    .collect(Collectors.toList());
            case "imax" -> sessions.stream()
                    .filter(s -> s.getCinemaHall().getName().toLowerCase().contains("imax"))
                    .collect(Collectors.toList());
            default -> sessions;
        };
    }

    private void displaySessions(List<MovieSession> sessions) {
        sessionsGrid.getChildren().clear();

        if (sessions.isEmpty()) {
            showEmptyState(true);
            return;
        }

        showEmptyState(false);

        // Group sessions by film
        Map<Film, List<MovieSession>> sessionsByFilm = sessions.stream()
                .collect(Collectors.groupingBy(MovieSession::getFilm));

        for (Map.Entry<Film, List<MovieSession>> entry : sessionsByFilm.entrySet()) {
            VBox card = createSessionCard(entry.getKey(), entry.getValue());
            sessionsGrid.getChildren().add(card);
        }
    }

    private VBox createSessionCard(Film film, List<MovieSession> sessions) {
        VBox card = new VBox();
        card.getStyleClass().add("session-card");
        card.setSpacing(0);

        // Poster section with gradient
        StackPane posterSection = new StackPane();
        posterSection.getStyleClass().add("session-poster");
        posterSection.setMinHeight(180);

        // Background image
        ImageView poster = new ImageView();
        poster.setFitWidth(360);
        poster.setFitHeight(180);
        poster.setPreserveRatio(false);

        try {
            if (film.getImage() != null && !film.getImage().isEmpty()) {
                poster.setImage(new Image(film.getImage(), true));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load poster", e);
        }

        // Gradient overlay
        VBox gradient = new VBox();
        gradient.getStyleClass().add("session-gradient");
        gradient.setMaxHeight(Double.MAX_VALUE);
        gradient.setMaxWidth(Double.MAX_VALUE);

        // Format badges
        HBox formatBadges = new HBox(6);
        formatBadges.getStyleClass().add("format-badges");
        formatBadges.setAlignment(Pos.TOP_LEFT);
        formatBadges.setPadding(new Insets(12));

        if (sessions.stream().anyMatch(s -> s.getCinemaHall().getName().toLowerCase().contains("imax"))) {
            Label imax = new Label("IMAX");
            imax.getStyleClass().addAll("format-badge", "imax");
            formatBadges.getChildren().add(imax);
        }

        posterSection.getChildren().addAll(poster, gradient, formatBadges);

        // Content section
        VBox content = new VBox(12);
        content.getStyleClass().add("session-content");
        content.setPadding(new Insets(20));

        // Title and metadata
        Label title = new Label(film.getTitle());
        title.getStyleClass().add("movie-title-card");

        HBox meta = new HBox(10);
        Label genre = new Label(film.getGenre());
        genre.getStyleClass().add("movie-genre");

        Label duration = new Label(film.getDuration() + " min");
        duration.getStyleClass().add("movie-duration");

        meta.getChildren().addAll(genre, new Label("•"), duration);

        // Session times by cinema
        VBox sessionTimes = new VBox(10);
        sessionTimes.getStyleClass().add("session-times");

        Map<Cinema, List<MovieSession>> byCinema = sessions.stream()
                .collect(Collectors.groupingBy(s -> s.getCinemaHall().getCinema()));

        for (Map.Entry<Cinema, List<MovieSession>> cinemaEntry : byCinema.entrySet()) {
            VBox cinemaBox = new VBox(8);

            HBox cinemaNameBox = new HBox(6);
            cinemaNameBox.setAlignment(Pos.CENTER_LEFT);
            FontIcon locationIcon = new FontIcon("mdi2m-map-marker");
            locationIcon.setIconSize(14);
            Label cinemaName = new Label(cinemaEntry.getKey().getName());
            cinemaName.getStyleClass().add("cinema-name");
            cinemaNameBox.getChildren().addAll(locationIcon, cinemaName);

            FlowPane timeSlots = new FlowPane();
            timeSlots.getStyleClass().add("time-slots");
            timeSlots.setHgap(8);
            timeSlots.setVgap(8);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            for (MovieSession session : cinemaEntry.getValue()) {
                VBox slot = createTimeSlot(session, timeFormatter);
                timeSlots.getChildren().add(slot);
            }

            cinemaBox.getChildren().addAll(cinemaNameBox, timeSlots);
            sessionTimes.getChildren().add(cinemaBox);
        }

        // View Details button
        Button viewBtn = new Button("View Details");
        viewBtn.getStyleClass().add("view-details-btn");
        viewBtn.setOnAction(e -> openSessionDetails(film, sessions));

        content.getChildren().addAll(title, meta, sessionTimes, viewBtn);
        card.getChildren().addAll(posterSection, content);

        return card;
    }

    private VBox createTimeSlot(MovieSession session, DateTimeFormatter formatter) {
        VBox slot = new VBox(2);
        slot.getStyleClass().add("time-slot");
        slot.setAlignment(Pos.CENTER);
        slot.setOnMouseClicked(e -> navigateToSeatSelection(session));

        Label time = new Label(session.getStartTime().format(formatter));
        time.getStyleClass().add("time");

        int availableSeats = calculateAvailableSeats(session);
        Label seats = new Label(availableSeats + " seats");
        seats.getStyleClass().add("seats");

        if (availableSeats <= 10) {
            slot.getStyleClass().add("low-availability");
        }
        if (availableSeats == 0) {
            slot.getStyleClass().add("sold-out");
            seats.setText("Sold out");
        }

        slot.getChildren().addAll(time, seats);
        return slot;
    }

    private int calculateAvailableSeats(MovieSession session) {
        // TODO: Implement actual available seats calculation
        return (int) (Math.random() * 100);
    }

    private void openSessionDetails(Film film, List<MovieSession> sessions) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/booking/MovieSessionDetails.fxml"));
            Node detailsView = loader.load();

            MovieSessionDetailsController controller = loader.getController();
            controller.initData(film, sessions);

            // Use slide-over navigation instead of full page navigation
            StackPane rootPane = (StackPane) sessionsGrid.getScene().getRoot();
            SlideOverNavigator.slideIn(detailsView, rootPane, () -> {
                LOGGER.info("Session details panel closed");
            });

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to open session details", e);
            showError("Could not open session details");
        }
    }

    private void navigateToSeatSelection(MovieSession session) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/films/SeatSelection.fxml"));
            Node seatView = loader.load();

            // Pass session data to seat selection controller
            // SeatSelectionController controller = loader.getController();
            // controller.setSession(session);

            Stage stage = (Stage) sessionsGrid.getScene().getWindow();
            stage.getScene().setRoot((javafx.scene.Parent) seatView);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to open seat selection", e);
            showError("Could not open seat selection");
        }
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(show);
        }
        if (sessionsScroll != null) {
            sessionsScroll.setVisible(!show);
        }
    }

    private void showEmptyState(boolean show) {
        if (emptyStatePane != null) {
            emptyStatePane.setVisible(show);
        }
    }

    private void updateResultsCount(int count) {
        if (resultsCountLabel != null) {
            resultsCountLabel.setText(count + " sessions found");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleRefresh() {
        loadSessions();
    }

    @FXML
    private void handleClearFilters() {
        searchField.clear();
        cinemaFilter.getSelectionModel().selectFirst();
        genreFilter.getSelectionModel().selectFirst();
        dateFilter.setValue(LocalDate.now());
        formatFilter.getSelectionModel().selectFirst();

        quickFiltersPane.getChildren().forEach(node -> {
            if (node instanceof Button) {
                node.getStyleClass().remove("active");
                if (((Button) node).getText().equalsIgnoreCase("All")) {
                    node.getStyleClass().add("active");
                }
            }
        });
        selectedQuickFilter = "all";

        applyFilters();
    }
}
