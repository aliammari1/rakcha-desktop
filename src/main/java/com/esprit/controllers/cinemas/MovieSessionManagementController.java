package com.esprit.controllers.cinemas;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.films.Film;
import com.esprit.services.cinemas.CinemaHallService;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.services.films.FilmService;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing movie sessions (admin functionality).
 */
public class MovieSessionManagementController {

    private static final Logger LOGGER = Logger.getLogger(MovieSessionManagementController.class.getName());
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final MovieSessionService sessionService;
    private final CinemaService cinemaService;
    private final CinemaHallService hallService;
    private final FilmService filmService;
    @FXML
    private VBox managementContainer;
    @FXML
    private ComboBox<Cinema> cinemaFilter;
    @FXML
    private ComboBox<CinemaHall> hallFilter;
    @FXML
    private ComboBox<Film> filmFilter;
    @FXML
    private DatePicker dateFilter;
    @FXML
    private TableView<MovieSession> sessionsTable;
    @FXML
    private TableColumn<MovieSession, Integer> idColumn;
    @FXML
    private TableColumn<MovieSession, String> filmColumn;
    @FXML
    private TableColumn<MovieSession, String> hallColumn;
    @FXML
    private TableColumn<MovieSession, String> startTimeColumn;
    @FXML
    private TableColumn<MovieSession, String> endTimeColumn;
    @FXML
    private TableColumn<MovieSession, Double> priceColumn;
    @FXML
    private TableColumn<MovieSession, String> statusColumn;
    @FXML
    private Label totalSessionsLabel;
    @FXML
    private Label todaySessionsLabel;
    @FXML
    private Label upcomingSessionsLabel;
    @FXML
    private ProgressIndicator loadingIndicator;
    // Form fields
    @FXML
    private ComboBox<Film> sessionFilmCombo;
    @FXML
    private ComboBox<CinemaHall> sessionHallCombo;
    @FXML
    private DatePicker sessionDatePicker;
    @FXML
    private ComboBox<String> sessionTimeCombo;
    @FXML
    private Spinner<Double> basePriceSpinner;
    @FXML
    private CheckBox is3DCheck;
    @FXML
    private CheckBox isIMAXCheck;
    @FXML
    private TextArea sessionNotesArea;
    private ObservableList<MovieSession> sessions;
    private ObservableList<Cinema> cinemas;
    private ObservableList<CinemaHall> halls;
    private ObservableList<Film> films;
    private MovieSession selectedSession;
    private CinemaHall preFilterHall;

    public MovieSessionManagementController() {
        this.sessionService = new MovieSessionService();
        this.cinemaService = new CinemaService();
        this.hallService = new CinemaHallService();
        this.filmService = new FilmService();
        this.sessions = FXCollections.observableArrayList();
        this.cinemas = FXCollections.observableArrayList();
        this.halls = FXCollections.observableArrayList();
        this.films = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing MovieSessionManagementController");

        setupTable();
        setupFilters();
        setupForm();
        loadData();
    }

    /**
     * Pre-filter by hall (from CinemaHallManagement).
     */
    public void setFilterHall(CinemaHall hall) {
        this.preFilterHall = hall;
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getId().intValue()).asObject());
        filmColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getFilm() != null ?
                cellData.getValue().getFilm().getNom() : "N/A"));
        hallColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCinemaHall() != null ?
                cellData.getValue().getCinemaHall().getHallName() : "N/A"));
        startTimeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getStartTime() != null ?
                cellData.getValue().getStartTime().format(DATE_TIME_FORMAT) : "N/A"));
        endTimeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getEndTime() != null ?
                cellData.getValue().getEndTime().format(DATE_TIME_FORMAT) : "N/A"));
        priceColumn.setCellValueFactory(cellData ->
            new SimpleDoubleProperty(cellData.getValue().getBasePrice()).asObject());
        statusColumn.setCellValueFactory(cellData -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = cellData.getValue().getStartTime();
            LocalDateTime end = cellData.getValue().getEndTime();

            String status;
            if (end != null && end.isBefore(now)) {
                status = "Completed";
            } else if (start != null && start.isBefore(now)) {
                status = "In Progress";
            } else {
                status = "Scheduled";
            }
            return new SimpleStringProperty(status);
        });

        sessionsTable.setItems(sessions);

        sessionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedSession = newSel;
                populateForm(newSel);
            }
        });
    }

    private void setupFilters() {
        cinemaFilter.setOnAction(e -> {
            loadHallsForCinema(cinemaFilter.getValue());
            filterSessions();
        });
        hallFilter.setOnAction(e -> filterSessions());
        filmFilter.setOnAction(e -> filterSessions());
        dateFilter.setOnAction(e -> filterSessions());
    }

    private void setupForm() {
        // Time slots
        ObservableList<String> timeSlots = FXCollections.observableArrayList();
        for (int hour = 9; hour <= 23; hour++) {
            timeSlots.add(String.format("%02d:00", hour));
            timeSlots.add(String.format("%02d:30", hour));
        }
        sessionTimeCombo.setItems(timeSlots);
        sessionTimeCombo.setValue("14:00");

        // Price spinner
        basePriceSpinner.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(5.0, 50.0, 12.0, 0.5));

        // Default date
        sessionDatePicker.setValue(LocalDate.now().plusDays(1));
    }

    private void loadData() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<Cinema> cinemaList = cinemaService.getAllCinemas();
                List<Film> filmList = filmService.getAllFilms();
                List<MovieSession> sessionList = sessionService.getAllSessions();

                Platform.runLater(() -> {
                    cinemas.setAll(cinemaList);
                    films.setAll(filmList);
                    sessions.setAll(sessionList);

                    // Setup filter combos
                    Cinema allCinemas = new Cinema();
                    allCinemas.setId(-1L);
                    allCinemas.setNom("All Cinemas");

                    cinemaFilter.getItems().clear();
                    cinemaFilter.getItems().add(allCinemas);
                    cinemaFilter.getItems().addAll(cinemas);
                    cinemaFilter.setValue(allCinemas);

                    Film allFilms = new Film();
                    allFilms.setId(-1L);
                    allFilms.setNom("All Films");

                    filmFilter.getItems().clear();
                    filmFilter.getItems().add(allFilms);
                    filmFilter.getItems().addAll(films);
                    filmFilter.setValue(allFilms);

                    // Form combos
                    sessionFilmCombo.setItems(films);

                    // Apply pre-filter if set
                    if (preFilterHall != null) {
                        hallFilter.setValue(preFilterHall);
                        filterSessions();
                    }

                    updateStatistics();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading data", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load data.");
                });
            }
        }).start();
    }

    private void loadHallsForCinema(Cinema cinema) {
        if (cinema == null || cinema.getId() == -1) {
            hallFilter.getItems().clear();
            sessionHallCombo.getItems().clear();
            return;
        }

        new Thread(() -> {
            try {
                List<CinemaHall> hallList = hallService.getHallsByCinema(cinema.getId());

                Platform.runLater(() -> {
                    halls.setAll(hallList);

                    CinemaHall allHalls = new CinemaHall();
                    allHalls.setId(-1L);
                    allHalls.setHallName("All Halls");

                    hallFilter.getItems().clear();
                    hallFilter.getItems().add(allHalls);
                    hallFilter.getItems().addAll(halls);
                    hallFilter.setValue(allHalls);

                    sessionHallCombo.setItems(halls);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading halls", e);
            }
        }).start();
    }

    private void filterSessions() {
        Cinema selectedCinema = cinemaFilter.getValue();
        CinemaHall selectedHall = hallFilter.getValue();
        Film selectedFilm = filmFilter.getValue();
        LocalDate selectedDate = dateFilter.getValue();

        new Thread(() -> {
            try {
                List<MovieSession> allSessions = sessionService.getAllSessions();

                List<MovieSession> filtered = allSessions.stream()
                    .filter(session -> {
                        // Cinema filter
                        if (selectedCinema != null && selectedCinema.getId() != -1) {
                            if (session.getCinemaHall() == null ||
                                session.getCinemaHall().getCinema() == null ||
                                session.getCinemaHall().getCinema().getId() != selectedCinema.getId()) {
                                return false;
                            }
                        }

                        // Hall filter
                        if (selectedHall != null && selectedHall.getId() != -1) {
                            if (session.getCinemaHall() == null ||
                                session.getCinemaHall().getId() != selectedHall.getId()) {
                                return false;
                            }
                        }

                        // Film filter
                        if (selectedFilm != null && selectedFilm.getId() != -1) {
                            if (session.getFilm() == null ||
                                session.getFilm().getId() != selectedFilm.getId()) {
                                return false;
                            }
                        }

                        // Date filter
                        if (selectedDate != null && session.getStartTime() != null) {
                            if (!session.getStartTime().toLocalDate().equals(selectedDate)) {
                                return false;
                            }
                        }

                        return true;
                    })
                    .toList();

                Platform.runLater(() -> {
                    sessions.setAll(filtered);
                    updateStatistics();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error filtering sessions", e);
            }
        }).start();
    }

    private void populateForm(MovieSession session) {
        sessionFilmCombo.setValue(session.getFilm());
        sessionHallCombo.setValue(session.getCinemaHall());

        if (session.getStartTime() != null) {
            sessionDatePicker.setValue(session.getStartTime().toLocalDate());
            sessionTimeCombo.setValue(session.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        basePriceSpinner.getValueFactory().setValue(session.getBasePrice());
    }

    private void clearForm() {
        selectedSession = null;
        sessionFilmCombo.setValue(null);
        sessionHallCombo.setValue(null);
        sessionDatePicker.setValue(LocalDate.now().plusDays(1));
        sessionTimeCombo.setValue("14:00");
        basePriceSpinner.getValueFactory().setValue(12.0);
        if (is3DCheck != null) is3DCheck.setSelected(false);
        if (isIMAXCheck != null) isIMAXCheck.setSelected(false);
        if (sessionNotesArea != null) sessionNotesArea.clear();

        sessionsTable.getSelectionModel().clearSelection();
    }

    private void updateStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        int total = sessions.size();
        long todayCount = sessions.stream()
            .filter(s -> s.getStartTime() != null &&
                s.getStartTime().toLocalDate().equals(today))
            .count();
        long upcoming = sessions.stream()
            .filter(s -> s.getStartTime() != null &&
                s.getStartTime().isAfter(now))
            .count();

        if (totalSessionsLabel != null) totalSessionsLabel.setText(String.valueOf(total));
        if (todaySessionsLabel != null) todaySessionsLabel.setText(String.valueOf(todayCount));
        if (upcomingSessionsLabel != null) upcomingSessionsLabel.setText(String.valueOf(upcoming));
    }

    @FXML
    private void handleAddSession() {
        clearForm();
    }

    @FXML
    private void handleSaveSession() {
        if (!validateForm()) return;

        showLoading(true);

        MovieSession session = selectedSession != null ? selectedSession : new MovieSession();
        session.setFilm(sessionFilmCombo.getValue());
        session.setCinemaHall(sessionHallCombo.getValue());

        LocalDate date = sessionDatePicker.getValue();
        LocalTime time = LocalTime.parse(sessionTimeCombo.getValue());
        session.setStartTime(LocalDateTime.of(date, time));

        // Calculate end time based on film duration
        int duration = session.getFilm() != null ? session.getFilm().getDuree() : 120;
        session.setEndTime(session.getStartTime().plusMinutes(duration + 15)); // Add 15 min buffer

        session.setBasePrice(basePriceSpinner.getValue());

        new Thread(() -> {
            try {
                if (selectedSession != null) {
                    sessionService.updateSession(session);
                } else {
                    sessionService.createSession(session);
                }

                Platform.runLater(() -> {
                    showLoading(false);
                    showSuccess(selectedSession != null ? "Session updated!" : "Session created!");
                    clearForm();
                    filterSessions();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error saving session", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to save session.");
                });
            }
        }).start();
    }

    private boolean validateForm() {
        if (sessionFilmCombo.getValue() == null) {
            showError("Please select a film.");
            return false;
        }
        if (sessionHallCombo.getValue() == null) {
            showError("Please select a hall.");
            return false;
        }
        if (sessionDatePicker.getValue() == null) {
            showError("Please select a date.");
            return false;
        }
        if (sessionDatePicker.getValue().isBefore(LocalDate.now())) {
            showError("Cannot schedule sessions in the past.");
            return false;
        }
        return true;
    }

    @FXML
    private void handleDeleteSession() {
        if (selectedSession == null) {
            showError("Please select a session to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Session");
        confirm.setHeaderText("Delete this session?");
        confirm.setContentText("This will cancel all associated bookings.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            showLoading(true);

            new Thread(() -> {
                try {
                    sessionService.deleteSession(selectedSession.getId());

                    Platform.runLater(() -> {
                        showLoading(false);
                        showSuccess("Session deleted!");
                        clearForm();
                        filterSessions();
                    });
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error deleting session", e);
                    Platform.runLater(() -> {
                        showLoading(false);
                        showError("Failed to delete session.");
                    });
                }
            }).start();
        }
    }

    @FXML
    private void handleBulkCreate() {
        showInfo("Bulk session creation coming soon!");
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleClearFilters() {
        cinemaFilter.setValue(cinemaFilter.getItems().get(0));
        hallFilter.getItems().clear();
        filmFilter.setValue(filmFilter.getItems().get(0));
        dateFilter.setValue(null);
        filterSessions();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/AdminDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) managementContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating back", e);
        }
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null) loadingIndicator.setVisible(show);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
