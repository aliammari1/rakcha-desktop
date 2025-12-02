package com.esprit.controllers.cinemas;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.services.cinemas.CinemaHallService;
import com.esprit.services.cinemas.CinemaService;
import javafx.application.Platform;
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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing cinema halls (admin functionality).
 */
public class CinemaHallManagementController {

    private static final Logger LOGGER = Logger.getLogger(CinemaHallManagementController.class.getName());
    private final CinemaService cinemaService;
    private final CinemaHallService cinemaHallService;
    @FXML
    private VBox managementContainer;
    @FXML
    private ComboBox<Cinema> cinemaFilter;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<CinemaHall> hallsTable;
    @FXML
    private TableColumn<CinemaHall, Integer> idColumn;
    @FXML
    private TableColumn<CinemaHall, String> nameColumn;
    @FXML
    private TableColumn<CinemaHall, String> cinemaColumn;
    @FXML
    private TableColumn<CinemaHall, Integer> capacityColumn;
    @FXML
    private TableColumn<CinemaHall, String> screenTypeColumn;
    @FXML
    private TableColumn<CinemaHall, String> statusColumn;
    @FXML
    private Label totalHallsLabel;
    @FXML
    private Label activeHallsLabel;
    @FXML
    private Label totalSeatsLabel;
    @FXML
    private ProgressIndicator loadingIndicator;
    // Form fields for add/edit
    @FXML
    private TextField hallNameField;
    @FXML
    private ComboBox<Cinema> hallCinemaCombo;
    @FXML
    private Spinner<Integer> rowsSpinner;
    @FXML
    private Spinner<Integer> seatsPerRowSpinner;
    @FXML
    private ComboBox<String> screenTypeCombo;
    @FXML
    private CheckBox isActiveCheck;
    @FXML
    private TextArea hallDescriptionArea;
    private ObservableList<CinemaHall> halls;
    private ObservableList<Cinema> cinemas;
    private CinemaHall selectedHall;

    public CinemaHallManagementController() {
        this.cinemaService = new CinemaService();
        this.cinemaHallService = new CinemaHallService();
        this.halls = FXCollections.observableArrayList();
        this.cinemas = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing CinemaHallManagementController");

        setupTable();
        setupFilters();
        setupForm();
        loadCinemas();
        loadHalls();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getId().intValue()).asObject());
        nameColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getHallName()));
        cinemaColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCinema() != null ?
                cellData.getValue().getCinema().getNom() : "N/A"));
        capacityColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());
        screenTypeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getScreenType()));
        statusColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().isActive() ? "Active" : "Inactive"));

        hallsTable.setItems(halls);

        hallsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedHall = newSel;
                populateForm(newSel);
            }
        });
    }

    private void setupFilters() {
        cinemaFilter.setOnAction(e -> filterHalls());

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterHalls());
        }
    }

    private void setupForm() {
        screenTypeCombo.getItems().addAll("Standard", "IMAX", "3D", "4DX", "Dolby Atmos", "VIP");
        screenTypeCombo.setValue("Standard");

        rowsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 10));
        seatsPerRowSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 15));
    }

    private void loadCinemas() {
        new Thread(() -> {
            try {
                List<Cinema> cinemaList = cinemaService.getAllCinemas();

                Platform.runLater(() -> {
                    cinemas.setAll(cinemaList);

                    Cinema allOption = new Cinema();
                    allOption.setNom("All Cinemas");
                    allOption.setId(-1L);

                    cinemaFilter.getItems().clear();
                    cinemaFilter.getItems().add(allOption);
                    cinemaFilter.getItems().addAll(cinemas);
                    cinemaFilter.setValue(allOption);

                    if (hallCinemaCombo != null) {
                        hallCinemaCombo.setItems(cinemas);
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading cinemas", e);
            }
        }).start();
    }

    private void loadHalls() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<CinemaHall> hallList = cinemaHallService.getAllHalls();

                Platform.runLater(() -> {
                    halls.setAll(hallList);
                    updateStatistics();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading halls", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load cinema halls.");
                });
            }
        }).start();
    }

    private void filterHalls() {
        Cinema selectedCinema = cinemaFilter.getValue();
        String searchText = searchField != null ? searchField.getText().toLowerCase() : "";

        new Thread(() -> {
            try {
                List<CinemaHall> allHalls = cinemaHallService.getAllHalls();

                List<CinemaHall> filtered = allHalls.stream()
                    .filter(hall -> {
                        // Cinema filter
                        if (selectedCinema != null && selectedCinema.getId() != -1) {
                            if (hall.getCinema() == null ||
                                hall.getCinema().getId() != selectedCinema.getId()) {
                                return false;
                            }
                        }

                        // Search filter
                        if (!searchText.isEmpty()) {
                            String hallName = hall.getHallName().toLowerCase();
                            String cinemaName = hall.getCinema() != null ?
                                hall.getCinema().getNom().toLowerCase() : "";

                            if (!hallName.contains(searchText) && !cinemaName.contains(searchText)) {
                                return false;
                            }
                        }

                        return true;
                    })
                    .toList();

                Platform.runLater(() -> {
                    halls.setAll(filtered);
                    updateStatistics();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error filtering halls", e);
            }
        }).start();
    }

    private void populateForm(CinemaHall hall) {
        if (hallNameField != null) hallNameField.setText(hall.getHallName());
        if (hallCinemaCombo != null) hallCinemaCombo.setValue(hall.getCinema());
        if (screenTypeCombo != null) screenTypeCombo.setValue(hall.getScreenType());
        if (isActiveCheck != null) isActiveCheck.setSelected(hall.isActive());
        if (hallDescriptionArea != null) hallDescriptionArea.setText(hall.getDescription());

        // Calculate rows and seats per row from capacity
        int capacity = hall.getCapacity();
        int rows = (int) Math.ceil(capacity / 15.0);
        int seatsPerRow = capacity / Math.max(rows, 1);

        if (rowsSpinner != null) rowsSpinner.getValueFactory().setValue(rows);
        if (seatsPerRowSpinner != null) seatsPerRowSpinner.getValueFactory().setValue(seatsPerRow);
    }

    private void clearForm() {
        selectedHall = null;
        if (hallNameField != null) hallNameField.clear();
        if (hallCinemaCombo != null) hallCinemaCombo.setValue(null);
        if (screenTypeCombo != null) screenTypeCombo.setValue("Standard");
        if (rowsSpinner != null) rowsSpinner.getValueFactory().setValue(10);
        if (seatsPerRowSpinner != null) seatsPerRowSpinner.getValueFactory().setValue(15);
        if (isActiveCheck != null) isActiveCheck.setSelected(true);
        if (hallDescriptionArea != null) hallDescriptionArea.clear();

        hallsTable.getSelectionModel().clearSelection();
    }

    private void updateStatistics() {
        int total = halls.size();
        long active = halls.stream().filter(CinemaHall::isActive).count();
        int totalSeats = halls.stream().mapToInt(CinemaHall::getCapacity).sum();

        if (totalHallsLabel != null) totalHallsLabel.setText(String.valueOf(total));
        if (activeHallsLabel != null) activeHallsLabel.setText(String.valueOf(active));
        if (totalSeatsLabel != null) totalSeatsLabel.setText(String.valueOf(totalSeats));
    }

    @FXML
    private void handleAddHall() {
        clearForm();
    }

    @FXML
    private void handleSaveHall() {
        if (!validateForm()) return;

        showLoading(true);

        CinemaHall hall = selectedHall != null ? selectedHall : new CinemaHall();
        hall.setHallName(hallNameField.getText().trim());
        hall.setCinema(hallCinemaCombo.getValue());
        hall.setScreenType(screenTypeCombo.getValue());
        hall.setCapacity(rowsSpinner.getValue() * seatsPerRowSpinner.getValue());
        hall.setActive(isActiveCheck.isSelected());
        if (hallDescriptionArea != null) {
            hall.setDescription(hallDescriptionArea.getText());
        }

        new Thread(() -> {
            try {
                if (selectedHall != null) {
                    cinemaHallService.updateHall(hall);
                } else {
                    cinemaHallService.createHall(hall);
                }

                Platform.runLater(() -> {
                    showLoading(false);
                    showSuccess(selectedHall != null ? "Hall updated successfully!" : "Hall created successfully!");
                    clearForm();
                    loadHalls();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error saving hall", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to save hall.");
                });
            }
        }).start();
    }

    private boolean validateForm() {
        if (hallNameField.getText().trim().isEmpty()) {
            showError("Hall name is required.");
            return false;
        }
        if (hallCinemaCombo.getValue() == null) {
            showError("Please select a cinema.");
            return false;
        }
        return true;
    }

    @FXML
    private void handleDeleteHall() {
        if (selectedHall == null) {
            showError("Please select a hall to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Hall");
        confirm.setHeaderText("Delete \"" + selectedHall.getHallName() + "\"?");
        confirm.setContentText("This will also delete all associated seats and sessions. This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            showLoading(true);

            new Thread(() -> {
                try {
                    cinemaHallService.deleteHall(selectedHall.getId());

                    Platform.runLater(() -> {
                        showLoading(false);
                        showSuccess("Hall deleted successfully!");
                        clearForm();
                        loadHalls();
                    });
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error deleting hall", e);
                    Platform.runLater(() -> {
                        showLoading(false);
                        showError("Failed to delete hall. It may have active sessions.");
                    });
                }
            }).start();
        }
    }

    @FXML
    private void handleManageSeats() {
        if (selectedHall == null) {
            showError("Please select a hall first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/admin/SeatManagement.fxml"));
            Parent root = loader.load();

            SeatManagementController controller = loader.getController();
            controller.setHall(selectedHall);

            Stage stage = (Stage) managementContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ui/styles/admin.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error opening seat management", e);
            showError("Could not open seat management.");
        }
    }

    @FXML
    private void handleViewSessions() {
        if (selectedHall == null) {
            showError("Please select a hall first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/admin/MovieSessionManagement.fxml"));
            Parent root = loader.load();

            MovieSessionManagementController controller = loader.getController();
            controller.setFilterHall(selectedHall);

            Stage stage = (Stage) managementContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ui/styles/admin.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error opening session management", e);
            showError("Could not open session management.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadHalls();
    }

    @FXML
    private void handleClearForm() {
        clearForm();
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
