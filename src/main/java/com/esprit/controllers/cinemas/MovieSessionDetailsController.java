package com.esprit.controllers.cinemas;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.films.Film;
import com.esprit.services.films.TicketService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller for viewing detailed movie session information and selecting
 * a specific showtime for booking.
 */
public class MovieSessionDetailsController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(MovieSessionDetailsController.class.getName());
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, MMM d");

    @FXML
    private ImageView backdropImage;
    @FXML
    private ImageView posterImage;
    @FXML
    private Label titleLabel;
    @FXML
    private Label metaLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private HBox dateSelector;
    @FXML
    private VBox cinemasList;
    @FXML
    private FlowPane showtimeGrid;
    @FXML
    private VBox bookingSummary;
    @FXML
    private Label selectedDateLabel;
    @FXML
    private Label selectedTimeLabel;
    @FXML
    private Label selectedCinemaLabel;
    @FXML
    private Label selectedHallLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Button bookButton;
    @FXML
    private Button addToWatchlistBtn;
    @FXML
    private Button shareBtn;
    @FXML
    private HBox ratingStars;

    private Film currentFilm;
    private List<MovieSession> allSessions;
    private LocalDate selectedDate;
    private MovieSession selectedSession;
    private Cinema selectedCinema;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize with default date
        selectedDate = LocalDate.now();

        // Setup book button
        if (bookButton != null) {
            bookButton.setDisable(true);
            bookButton.setOnAction(e -> handleBooking());
        }
    }

    /**
     * Initialize the controller with film and session data
     */
    public void initData(Film film, List<MovieSession> sessions) {
        this.currentFilm = film;
        this.allSessions = sessions;

        Platform.runLater(() -> {
            populateFilmDetails();
            setupDateSelector();
            displaySessionsForDate(selectedDate);
        });
    }

    private void populateFilmDetails() {
        if (currentFilm == null)
            return;

        // Title
        if (titleLabel != null) {
            titleLabel.setText(currentFilm.getTitle());
        }

        // Metadata
        if (metaLabel != null) {
            String meta = String.format("%s • %d min • %s",
                    currentFilm.getGenre(),
                    currentFilm.getDuration(),
                    currentFilm.getCountry());
            metaLabel.setText(meta);
        }

        // Rating
        if (ratingLabel != null) {
            ratingLabel.setText(String.format("%.1f", currentFilm.getAverageRating()));
        }

        // Rating stars
        if (ratingStars != null) {
            ratingStars.getChildren().clear();
            int fullStars = (int) currentFilm.getAverageRating();
            for (int i = 0; i < 5; i++) {
                FontIcon star = new FontIcon(i < fullStars ? "mdi2s-star" : "mdi2s-star-outline");
                star.setIconSize(16);
                star.getStyleClass().add(i < fullStars ? "star-filled" : "star-empty");
                ratingStars.getChildren().add(star);
            }
        }

        // Description
        if (descriptionLabel != null) {
            descriptionLabel.setText(currentFilm.getDescription());
        }

        // Images
        loadImages();
    }

    private void loadImages() {
        String imageUrl = currentFilm.getImage();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image image = new Image(imageUrl, true);

                if (backdropImage != null) {
                    backdropImage.setImage(image);
                }
                if (posterImage != null) {
                    posterImage.setImage(image);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load film images", e);
            }
        }
    }

    private void setupDateSelector() {
        if (dateSelector == null)
            return;

        dateSelector.getChildren().clear();

        // Get unique dates from sessions
        Set<LocalDate> availableDates = allSessions.stream()
                .map(s -> s.getStartTime().toLocalDate())
                .collect(Collectors.toSet());

        // Add next 7 days
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            VBox dateCard = createDateCard(date, availableDates.contains(date));
            dateSelector.getChildren().add(dateCard);
        }
    }

    private VBox createDateCard(LocalDate date, boolean hasShowings) {
        VBox card = new VBox(4);
        card.getStyleClass().add("date-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16, 20, 16, 20));

        if (date.equals(selectedDate)) {
            card.getStyleClass().add("selected");
        }

        if (!hasShowings) {
            card.getStyleClass().add("disabled");
        }

        Label dayLabel = new Label(date.getDayOfWeek().toString().substring(0, 3));
        dayLabel.getStyleClass().add("date-day");

        Label numberLabel = new Label(String.valueOf(date.getDayOfMonth()));
        numberLabel.getStyleClass().add("date-number");

        Label monthLabel = new Label(date.getMonth().toString().substring(0, 3));
        monthLabel.getStyleClass().add("date-month");

        card.getChildren().addAll(dayLabel, numberLabel, monthLabel);

        if (hasShowings) {
            card.setOnMouseClicked(e -> selectDate(date));
        }

        return card;
    }

    private void selectDate(LocalDate date) {
        selectedDate = date;

        // Update UI
        dateSelector.getChildren().forEach(node -> {
            node.getStyleClass().remove("selected");
        });

        // Find and select the date card
        int dayIndex = (int) (date.toEpochDay() - LocalDate.now().toEpochDay());
        if (dayIndex >= 0 && dayIndex < dateSelector.getChildren().size()) {
            dateSelector.getChildren().get(dayIndex).getStyleClass().add("selected");
        }

        displaySessionsForDate(date);
    }

    private void displaySessionsForDate(LocalDate date) {
        if (cinemasList == null)
            return;

        cinemasList.getChildren().clear();

        // Filter sessions for the selected date
        List<MovieSession> daySessions = allSessions.stream()
                .filter(s -> s.getStartTime().toLocalDate().equals(date))
                .collect(Collectors.toList());

        // Group by cinema
        Map<Cinema, List<MovieSession>> byCinema = daySessions.stream()
                .collect(Collectors.groupingBy(s -> s.getCinemaHall().getCinema()));

        for (Map.Entry<Cinema, List<MovieSession>> entry : byCinema.entrySet()) {
            VBox cinemaSection = createCinemaSection(entry.getKey(), entry.getValue());
            cinemasList.getChildren().add(cinemaSection);
        }

        if (byCinema.isEmpty()) {
            Label noShowings = new Label("No showings available for this date");
            noShowings.getStyleClass().add("no-showings-label");
            cinemasList.getChildren().add(noShowings);
        }
    }

    private VBox createCinemaSection(Cinema cinema, List<MovieSession> sessions) {
        VBox section = new VBox(16);
        section.getStyleClass().add("cinema-selector");
        section.setPadding(new Insets(20));

        // Cinema header
        VBox header = new VBox(4);
        header.getStyleClass().add("cinema-header");

        Label cinemaName = new Label(cinema.getName());
        cinemaName.getStyleClass().add("cinema-name-large");

        Label address = new Label(cinema.getAddress());
        address.getStyleClass().add("cinema-address");

        header.getChildren().addAll(cinemaName, address);

        // Group sessions by hall
        Map<CinemaHall, List<MovieSession>> byHall = sessions.stream()
                .collect(Collectors.groupingBy(MovieSession::getCinemaHall));

        // Hall tabs
        HBox hallTabs = new HBox(8);
        hallTabs.getStyleClass().add("hall-tabs");

        for (CinemaHall hall : byHall.keySet()) {
            Button hallTab = new Button(hall.getName());
            hallTab.getStyleClass().add("hall-tab");
            hallTab.setOnAction(e -> {
                // Switch hall selection
                hallTabs.getChildren().forEach(n -> n.getStyleClass().remove("selected"));
                hallTab.getStyleClass().add("selected");
            });
            hallTabs.getChildren().add(hallTab);
        }

        // Select first hall by default
        if (!hallTabs.getChildren().isEmpty()) {
            hallTabs.getChildren().get(0).getStyleClass().add("selected");
        }

        // Showtime buttons
        FlowPane showtimes = new FlowPane();
        showtimes.getStyleClass().add("showtime-grid");
        showtimes.setHgap(12);
        showtimes.setVgap(12);

        for (MovieSession session : sessions) {
            VBox showtimeBtn = createShowtimeButton(session);
            showtimes.getChildren().add(showtimeBtn);
        }

        section.getChildren().addAll(header, hallTabs, showtimes);
        return section;
    }

    private VBox createShowtimeButton(MovieSession session) {
        VBox btn = new VBox(4);
        btn.getStyleClass().add("showtime-btn");
        btn.setAlignment(Pos.CENTER);
        btn.setPadding(new Insets(14, 20, 14, 20));

        if (session.equals(selectedSession)) {
            btn.getStyleClass().add("selected");
        }

        Label time = new Label(session.getStartTime().format(TIME_FORMATTER));
        time.getStyleClass().add("showtime-time");

        Label format = new Label(session.getCinemaHall().getName());
        format.getStyleClass().add("showtime-format");

        Label price = new Label(String.format("$%.2f", session.getPrice()));
        price.getStyleClass().add("showtime-price");

        // Availability indicator
        HBox availabilityBox = new HBox(6);
        availabilityBox.setAlignment(Pos.CENTER);

        int available = calculateAvailableSeats(session);
        javafx.scene.shape.Circle indicator = new javafx.scene.shape.Circle(4);
        indicator.getStyleClass().add("availability-indicator");

        if (available > 50) {
            indicator.getStyleClass().add("high");
        } else if (available > 20) {
            indicator.getStyleClass().add("medium");
        } else {
            indicator.getStyleClass().add("low");
        }

        availabilityBox.getChildren().add(indicator);

        btn.getChildren().addAll(time, format, price, availabilityBox);

        btn.setOnMouseClicked(e -> selectSession(session));

        return btn;
    }

    private int calculateAvailableSeats(MovieSession session) {
        if (session == null || session.getCinemaHall() == null) {
            return 0;
        }
        int totalSeats = session.getCinemaHall().getSeatCapacity();
        TicketService ticketService = new TicketService();
        int bookedSeats = ticketService.countBookedSeatsForSession(session.getId());
        return Math.max(0, totalSeats - bookedSeats);
    }

    private void selectSession(MovieSession session) {
        selectedSession = session;
        selectedCinema = session.getCinemaHall().getCinema();

        // Update UI selection
        updateBookingSummary();

        if (bookButton != null) {
            bookButton.setDisable(false);
        }
    }

    private void updateBookingSummary() {
        if (selectedSession == null)
            return;

        if (selectedDateLabel != null) {
            selectedDateLabel.setText(selectedDate.format(DATE_FORMATTER));
        }
        if (selectedTimeLabel != null) {
            selectedTimeLabel.setText(selectedSession.getStartTime().format(TIME_FORMATTER));
        }
        if (selectedCinemaLabel != null) {
            selectedCinemaLabel.setText(selectedCinema.getName());
        }
        if (selectedHallLabel != null) {
            selectedHallLabel.setText(selectedSession.getCinemaHall().getName());
        }
        if (priceLabel != null) {
            priceLabel.setText(String.format("From $%.2f", selectedSession.getPrice()));
        }
    }

    @FXML
    private void handleBooking() {
        if (selectedSession == null) {
            showAlert("Please select a showtime first");
            return;
        }

        try {
            // Navigate to seat selection
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/films/SeatSelection.fxml"));
            Node seatView = loader.load();

            // Pass session to seat selection controller
            // SeatSelectionController controller = loader.getController();
            // controller.setSession(selectedSession);

            Stage stage = (Stage) bookButton.getScene().getWindow();
            stage.getScene().setRoot((javafx.scene.Parent) seatView);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to navigate to seat selection", e);
            showAlert("Could not proceed to seat selection");
        }
    }

    @FXML
    private void handleAddToWatchlist() {
        // TODO: Implement watchlist functionality
        showAlert("Added to your watchlist!");
    }

    @FXML
    private void handleShare() {
        // TODO: Implement share functionality
        String shareText = String.format("Check out %s at the cinema!", currentFilm.getTitle());

        // Copy to clipboard
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(shareText);
        clipboard.setContent(content);

        showAlert("Link copied to clipboard!");
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/booking/MovieSessionBrowser.fxml"));
            Node browserView = loader.load();

            Stage stage = (Stage) titleLabel.getScene().getWindow();
            stage.getScene().setRoot((javafx.scene.Parent) browserView);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to navigate back", e);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
