package com.esprit.controllers.films;

import com.esprit.models.films.Ticket;
import com.esprit.models.users.User;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.films.FilmService;
import com.esprit.services.films.TicketService;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for viewing user's ticket history with filtering capabilities.
 * Displays past and upcoming tickets with status tracking.
 */
public class TicketHistoryController {

    private static final Logger LOGGER = Logger.getLogger(TicketHistoryController.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final int TICKETS_PER_PAGE = 10;
    private final TicketService ticketService;
    private final FilmService filmService;
    private final CinemaService cinemaService;
    @FXML
    private VBox ticketsContainer;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private ComboBox<String> sortCombo;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private TextField searchField;
    @FXML
    private Label totalTicketsLabel;
    @FXML
    private Label upcomingTicketsLabel;
    @FXML
    private Label totalSpentLabel;
    @FXML
    private Label loyaltyPointsLabel;
    @FXML
    private ToggleButton upcomingToggle;
    @FXML
    private ToggleButton pastToggle;
    @FXML
    private ToggleButton allToggle;
    @FXML
    private ToggleButton upcomingTab;
    @FXML
    private ToggleButton pastTab;
    @FXML
    private ToggleButton cancelledTab;
    @FXML
    private ToggleGroup viewToggleGroup;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private Label emptyStateLabel;
    @FXML
    private Pagination ticketPagination;
    @FXML
    private ComboBox<String> dateRangeFilter;
    @FXML
    private VBox ticketDetailOverlay;
    @FXML
    private ImageView detailPoster;
    @FXML
    private Label detailFilmTitle;
    @FXML
    private Label detailCinema;
    @FXML
    private Label detailDate;
    @FXML
    private Label detailTime;
    @FXML
    private Label detailHall;
    @FXML
    private Label detailSeat;
    @FXML
    private Label detailType;
    private ObservableList<Ticket> allTickets;
    private FilteredList<Ticket> filteredTickets;
    private User currentUser;

    public TicketHistoryController() {
        this.ticketService = new TicketService();
        this.filmService = new FilmService();
        this.cinemaService = new CinemaService();
        this.allTickets = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing TicketHistoryController");

        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            LOGGER.warning("No user logged in");
            return;
        }

        setupFilters();
        setupToggleGroup();
        setupSearch();
        loadTicketHistory();
    }

    private void setupFilters() {
        // Status filter
        statusFilter.setItems(FXCollections.observableArrayList(
            "All Statuses", "Confirmed", "Pending", "Used", "Cancelled", "Expired"
        ));
        statusFilter.setValue("All Statuses");
        statusFilter.setOnAction(e -> applyFilters());

        // Sort options
        sortCombo.setItems(FXCollections.observableArrayList(
            "Date (Newest)", "Date (Oldest)", "Movie Name", "Cinema", "Price (High-Low)", "Price (Low-High)"
        ));
        sortCombo.setValue("Date (Newest)");
        sortCombo.setOnAction(e -> applyFilters());

        // Date pickers
        if (fromDatePicker != null) fromDatePicker.setOnAction(e -> applyFilters());
        if (toDatePicker != null) toDatePicker.setOnAction(e -> applyFilters());
    }

    private void setupToggleGroup() {
        viewToggleGroup = new ToggleGroup();
        if (upcomingToggle != null) upcomingToggle.setToggleGroup(viewToggleGroup);
        if (pastToggle != null) pastToggle.setToggleGroup(viewToggleGroup);
        if (allToggle != null) {
            allToggle.setToggleGroup(viewToggleGroup);
            allToggle.setSelected(true);
        }

        viewToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                applyFilters();
            }
        });
    }

    private void setupSearch() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }
    }

    private void loadTicketHistory() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<Ticket> tickets = ticketService.getTicketsByUser(currentUser.getId());

                Platform.runLater(() -> {
                    allTickets.setAll(tickets);
                    filteredTickets = new FilteredList<>(allTickets, p -> true);

                    updateStatistics();
                    applyFilters();
                    showLoading(false);

                    LOGGER.info("Loaded " + tickets.size() + " tickets for user");
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading ticket history", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load ticket history. Please try again.");
                });
            }
        }).start();
    }

    private void applyFilters() {
        if (filteredTickets == null) return;

        String status = statusFilter.getValue();
        String searchText = searchField != null ? searchField.getText().toLowerCase() : "";
        LocalDate fromDate = fromDatePicker != null ? fromDatePicker.getValue() : null;
        LocalDate toDate = toDatePicker != null ? toDatePicker.getValue() : null;
        Toggle selectedToggle = viewToggleGroup.getSelectedToggle();

        filteredTickets.setPredicate(ticket -> {
            // Status filter
            if (!"All Statuses".equals(status)) {
                if (!ticket.getStatus().toString().equalsIgnoreCase(status)) {
                    return false;
                }
            }

            // Time-based filter (upcoming/past/all)
            LocalDateTime sessionTime = ticket.getMovieSession() != null ?
                ticket.getMovieSession().getStartTime() : null;

            if (selectedToggle == upcomingToggle && sessionTime != null) {
                if (sessionTime.isBefore(LocalDateTime.now())) {
                    return false;
                }
            } else if (selectedToggle == pastToggle && sessionTime != null) {
                if (sessionTime.isAfter(LocalDateTime.now())) {
                    return false;
                }
            }

            // Date range filter
            if (fromDate != null && sessionTime != null) {
                if (sessionTime.toLocalDate().isBefore(fromDate)) {
                    return false;
                }
            }
            if (toDate != null && sessionTime != null) {
                if (sessionTime.toLocalDate().isAfter(toDate)) {
                    return false;
                }
            }

            // Search filter
            if (!searchText.isEmpty()) {
                String filmName = ticket.getMovieSession() != null &&
                    ticket.getMovieSession().getFilm() != null ?
                    ticket.getMovieSession().getFilm().getNom().toLowerCase() : "";
                String cinemaName = ticket.getMovieSession() != null &&
                    ticket.getMovieSession().getCinemaHall() != null &&
                    ticket.getMovieSession().getCinemaHall().getCinema() != null ?
                    ticket.getMovieSession().getCinemaHall().getCinema().getNom().toLowerCase() : "";

                if (!filmName.contains(searchText) && !cinemaName.contains(searchText)) {
                    return false;
                }
            }

            return true;
        });

        sortTickets();
        updateDisplay();
    }

    private void sortTickets() {
        String sortBy = sortCombo.getValue();

        FXCollections.sort(filteredTickets, (t1, t2) -> {
            switch (sortBy) {
                case "Date (Newest)":
                    return compareSessionDates(t2, t1);
                case "Date (Oldest)":
                    return compareSessionDates(t1, t2);
                case "Movie Name":
                    return getFilmName(t1).compareToIgnoreCase(getFilmName(t2));
                case "Cinema":
                    return getCinemaName(t1).compareToIgnoreCase(getCinemaName(t2));
                case "Price (High-Low)":
                    return Double.compare(t2.getPricePaid(), t1.getPricePaid());
                case "Price (Low-High)":
                    return Double.compare(t1.getPricePaid(), t2.getPricePaid());
                default:
                    return 0;
            }
        });
    }

    private int compareSessionDates(Ticket t1, Ticket t2) {
        LocalDateTime d1 = t1.getMovieSession() != null ? t1.getMovieSession().getStartTime() : null;
        LocalDateTime d2 = t2.getMovieSession() != null ? t2.getMovieSession().getStartTime() : null;

        if (d1 == null && d2 == null) return 0;
        if (d1 == null) return 1;
        if (d2 == null) return -1;
        return d1.compareTo(d2);
    }

    private String getFilmName(Ticket ticket) {
        if (ticket.getMovieSession() != null && ticket.getMovieSession().getFilm() != null) {
            return ticket.getMovieSession().getFilm().getNom();
        }
        return "";
    }

    private String getCinemaName(Ticket ticket) {
        if (ticket.getMovieSession() != null &&
            ticket.getMovieSession().getCinemaHall() != null &&
            ticket.getMovieSession().getCinemaHall().getCinema() != null) {
            return ticket.getMovieSession().getCinemaHall().getCinema().getNom();
        }
        return "";
    }

    private void updateDisplay() {
        ticketsContainer.getChildren().clear();

        if (filteredTickets.isEmpty()) {
            emptyStateLabel.setVisible(true);
            emptyStateLabel.setText("No tickets found matching your criteria");
            ticketPagination.setVisible(false);
        } else {
            emptyStateLabel.setVisible(false);
            ticketPagination.setVisible(true);

            int pageCount = (int) Math.ceil((double) filteredTickets.size() / TICKETS_PER_PAGE);
            ticketPagination.setPageCount(Math.max(pageCount, 1));
            ticketPagination.setPageFactory(this::createPage);
        }
    }

    private VBox createPage(int pageIndex) {
        VBox page = new VBox(12);
        page.getStyleClass().add("ticket-list-page");

        int start = pageIndex * TICKETS_PER_PAGE;
        int end = Math.min(start + TICKETS_PER_PAGE, filteredTickets.size());

        for (int i = start; i < end; i++) {
            page.getChildren().add(createTicketCard(filteredTickets.get(i)));
        }

        return page;
    }

    private HBox createTicketCard(Ticket ticket) {
        HBox card = new HBox(16);
        card.getStyleClass().add("ticket-history-card");

        // Movie poster placeholder
        VBox posterBox = new VBox();
        posterBox.getStyleClass().add("ticket-poster");
        posterBox.setPrefSize(80, 120);

        // Ticket info
        VBox infoBox = new VBox(6);
        infoBox.getStyleClass().add("ticket-info");
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        String filmName = getFilmName(ticket);
        String cinemaName = getCinemaName(ticket);
        LocalDateTime sessionTime = ticket.getMovieSession() != null ?
            ticket.getMovieSession().getStartTime() : null;

        Label titleLabel = new Label(filmName.isEmpty() ? "Unknown Movie" : filmName);
        titleLabel.getStyleClass().add("ticket-movie-title");

        Label cinemaLabel = new Label(cinemaName.isEmpty() ? "Unknown Cinema" : cinemaName);
        cinemaLabel.getStyleClass().add("ticket-cinema");

        Label dateLabel = new Label(sessionTime != null ?
            sessionTime.format(DATE_FORMAT) + " at " + sessionTime.format(TIME_FORMAT) : "N/A");
        dateLabel.getStyleClass().add("ticket-date");

        Label seatLabel = new Label("Seat: " + (ticket.getSeat() != null ?
            ticket.getSeat().getSeatNumber() : "N/A"));
        seatLabel.getStyleClass().add("ticket-seat");

        infoBox.getChildren().addAll(titleLabel, cinemaLabel, dateLabel, seatLabel);

        // Status and price
        VBox statusBox = new VBox(8);
        statusBox.getStyleClass().add("ticket-status-box");

        Label statusLabel = new Label(ticket.getStatus().toString());
        statusLabel.getStyleClass().addAll("ticket-status", "status-" + ticket.getStatus().toString().toLowerCase());

        Label priceLabel = new Label(String.format("$%.2f", ticket.getPricePaid()));
        priceLabel.getStyleClass().add("ticket-price");

        Button viewButton = new Button("View Details");
        viewButton.getStyleClass().add("view-ticket-btn");
        viewButton.setOnAction(e -> viewTicketDetails(ticket));

        statusBox.getChildren().addAll(statusLabel, priceLabel, viewButton);

        card.getChildren().addAll(posterBox, infoBox, statusBox);

        return card;
    }

    private void updateStatistics() {
        if (allTickets == null) return;

        int total = allTickets.size();
        long upcoming = allTickets.stream()
            .filter(t -> t.getMovieSession() != null &&
                t.getMovieSession().getStartTime() != null &&
                t.getMovieSession().getStartTime().isAfter(LocalDateTime.now()))
            .count();
        double totalSpent = allTickets.stream()
            .filter(t -> !"Cancelled".equalsIgnoreCase(t.getStatus().toString()))
            .mapToDouble(Ticket::getPricePaid)
            .sum();

        if (totalTicketsLabel != null) {
            totalTicketsLabel.setText(String.valueOf(total));
        }
        if (upcomingTicketsLabel != null) {
            upcomingTicketsLabel.setText(String.valueOf(upcoming));
        }
        if (totalSpentLabel != null) {
            totalSpentLabel.setText(String.format("$%.2f", totalSpent));
        }
    }

    @FXML
    private void handleRefresh() {
        loadTicketHistory();
    }

    @FXML
    private void handleClearFilters() {
        statusFilter.setValue("All Statuses");
        sortCombo.setValue("Date (Newest)");
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        if (searchField != null) searchField.clear();
        if (allToggle != null) allToggle.setSelected(true);

        applyFilters();
    }

    @FXML
    private void handleExportHistory() {
        LOGGER.info("Export ticket history requested");
        // TODO: Implement PDF/CSV export functionality
        showInfo("Export feature coming soon!");
    }

    @FXML
    private void exportTickets() {
        handleExportHistory();
    }

    @FXML
    private void showUpcoming() {
        if (upcomingToggle != null) upcomingToggle.setSelected(true);
        applyFilters();
    }

    @FXML
    private void showPast() {
        if (pastToggle != null) pastToggle.setSelected(true);
        applyFilters();
    }

    @FXML
    private void showCancelled() {
        applyFilters();
    }

    private void viewTicketDetails(Ticket ticket) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/tickets/TicketDetails.fxml"));
            Parent root = loader.load();

            TicketDetailsController controller = loader.getController();
            controller.setTicket(ticket);

            Stage stage = (Stage) ticketsContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ui/styles/tickets.css").toExternalForm());
            stage.setScene(scene);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating to ticket details", e);
            showError("Could not load ticket details.");
        }
    }

    @FXML
    private void downloadTicket() {
        LOGGER.info("Download ticket PDF");
    }

    @FXML
    private void shareTicket() {
        LOGGER.info("Share ticket");
    }

    @FXML
    private void cancelTicket() {
        LOGGER.info("Cancel ticket");
    }

    @FXML
    private void closeTicketDetail() {
        if (ticketDetailOverlay != null) {
            ticketDetailOverlay.setVisible(false);
            ticketDetailOverlay.setManaged(false);
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/ClientDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ticketsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating back", e);
        }
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(show);
        }
        if (ticketsContainer != null) {
            ticketsContainer.setDisable(show);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
