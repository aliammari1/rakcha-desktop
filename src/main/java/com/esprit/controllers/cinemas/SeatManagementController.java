package com.esprit.controllers.cinemas;

import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.cinemas.Seat;
import com.esprit.services.cinemas.CinemaHallService;
import com.esprit.services.cinemas.SeatService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing seats in a cinema hall (admin functionality).
 */
public class SeatManagementController {

    private static final Logger LOGGER = Logger.getLogger(SeatManagementController.class.getName());
    private final SeatService seatService;
    private final CinemaHallService hallService;
    @FXML
    private VBox managementContainer;
    @FXML
    private Label hallNameLabel;
    @FXML
    private Label cinemaNameLabel;
    @FXML
    private Label capacityLabel;
    @FXML
    private Label totalSeatsLabel;
    @FXML
    private Label standardSeatsLabel;
    @FXML
    private Label premiumSeatsLabel;
    @FXML
    private Label vipSeatsLabel;
    @FXML
    private Label disabledSeatsLabel;
    @FXML
    private GridPane seatGrid;
    @FXML
    private VBox seatDetailPanel;
    @FXML
    private Label selectedSeatLabel;
    @FXML
    private ComboBox<String> seatTypeCombo;
    @FXML
    private Spinner<Double> seatPriceMultiplierSpinner;
    @FXML
    private CheckBox isActiveCheck;
    @FXML
    private CheckBox isAccessibleCheck;
    @FXML
    private Spinner<Integer> rowsSpinner;
    @FXML
    private Spinner<Integer> seatsPerRowSpinner;
    @FXML
    private ProgressIndicator loadingIndicator;
    private CinemaHall currentHall;
    private ObservableList<Seat> seats;
    private Seat selectedSeat;
    private List<Button> selectedSeatButtons = new ArrayList<>();
    private boolean isMultiSelectMode = false;

    public SeatManagementController() {
        this.seatService = new SeatService();
        this.hallService = new CinemaHallService();
        this.seats = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing SeatManagementController");

        setupForm();
    }

    /**
     * Sets the hall to manage seats for.
     */
    public void setHall(CinemaHall hall) {
        this.currentHall = hall;
        updateHallInfo();
        loadSeats();
    }

    private void setupForm() {
        seatTypeCombo.getItems().addAll("Standard", "Premium", "VIP", "Wheelchair Accessible", "Companion");
        seatTypeCombo.setValue("Standard");

        seatPriceMultiplierSpinner.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 3.0, 1.0, 0.1));

        rowsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 10));
        seatsPerRowSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 15));

        if (isActiveCheck != null) isActiveCheck.setSelected(true);
    }

    private void updateHallInfo() {
        if (currentHall == null) return;

        if (hallNameLabel != null) hallNameLabel.setText(currentHall.getHallName());
        if (cinemaNameLabel != null && currentHall.getCinema() != null) {
            cinemaNameLabel.setText(currentHall.getCinema().getNom());
        }
        if (capacityLabel != null) capacityLabel.setText(String.valueOf(currentHall.getCapacity()));
    }

    private void loadSeats() {
        if (currentHall == null) return;

        showLoading(true);

        new Thread(() -> {
            try {
                List<Seat> seatList = seatService.getSeatsByHall(currentHall.getId());

                Platform.runLater(() -> {
                    seats.setAll(seatList);
                    displaySeatMap();
                    updateStatistics();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading seats", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load seats.");
                });
            }
        }).start();
    }

    private void displaySeatMap() {
        seatGrid.getChildren().clear();
        seatGrid.getColumnConstraints().clear();
        seatGrid.getRowConstraints().clear();

        if (seats.isEmpty()) {
            Label emptyLabel = new Label("No seats configured. Generate seat layout below.");
            emptyLabel.getStyleClass().add("empty-state-label");
            seatGrid.add(emptyLabel, 0, 0);
            return;
        }

        // Find max row and column
        int maxRow = seats.stream().mapToInt(s -> getRowNumber(s.getRowLabel())).max().orElse(0);
        int maxCol = seats.stream().mapToInt(s -> {
            try {
                return Integer.parseInt(s.getSeatNumber());
            } catch (NumberFormatException e) {
                return 0;
            }
        }).max().orElse(0);

        // Add column headers
        for (int col = 1; col <= maxCol; col++) {
            Label colLabel = new Label(String.valueOf(col));
            colLabel.getStyleClass().add("seat-header");
            colLabel.setMinWidth(35);
            colLabel.setAlignment(Pos.CENTER);
            seatGrid.add(colLabel, col, 0);
        }

        // Add row headers and seats
        for (Seat seat : seats) {
            int row = getRowNumber(seat.getRowLabel());
            int col;
            try {
                col = Integer.parseInt(seat.getSeatNumber());
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid seat number format: " + seat.getSeatNumber());
                col = 0;
            }

            // Row header (only once per row)
            if (seatGrid.getChildren().stream().noneMatch(node ->
                GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == 0)) {
                Label rowLabel = new Label(seat.getRowLabel());
                rowLabel.getStyleClass().add("seat-header");
                rowLabel.setMinWidth(35);
                rowLabel.setAlignment(Pos.CENTER);
                seatGrid.add(rowLabel, 0, row);
            }

            // Seat button
            Button seatBtn = createSeatButton(seat);
            seatGrid.add(seatBtn, col, row);
        }

        // Add screen indicator at top
        Label screenLabel = new Label("SCREEN");
        screenLabel.getStyleClass().add("screen-indicator");
        screenLabel.setMinWidth(maxCol * 35 + 50);
        screenLabel.setAlignment(Pos.CENTER);
        HBox screenBox = new HBox(screenLabel);
        screenBox.setAlignment(Pos.CENTER);
        seatGrid.add(screenBox, 0, maxRow + 2, maxCol + 1, 1);
    }

    private Button createSeatButton(Seat seat) {
        Button btn = new Button(seat.getRowLabel() + seat.getSeatNumber());
        btn.setMinSize(35, 35);
        btn.setMaxSize(35, 35);
        btn.getStyleClass().add("seat-button");

        // Style based on seat type
        String seatType = seat.getSeatType() != null ? seat.getSeatType().toLowerCase() : "standard";
        btn.getStyleClass().add("seat-" + seatType.replace(" ", "-"));

        if (!seat.isActive()) {
            btn.getStyleClass().add("seat-disabled");
        }

        if (seat.isAccessible()) {
            btn.getStyleClass().add("seat-accessible");
        }

        btn.setOnAction(e -> {
            if (isMultiSelectMode) {
                toggleSeatSelection(btn, seat);
            } else {
                selectSeat(seat);
            }
        });

        // Tooltip
        Tooltip tooltip = new Tooltip(String.format(
            "Seat %s%s\nType: %s\nPrice Multiplier: %.1fx\nStatus: %s",
            seat.getRowLabel(), seat.getSeatNumber(),
            seat.getSeatType(),
            seat.getPriceMultiplier(),
            seat.isActive() ? "Active" : "Disabled"
        ));
        Tooltip.install(btn, tooltip);

        return btn;
    }

    private int getRowNumber(String rowLabel) {
        if (rowLabel == null || rowLabel.isEmpty()) return 1;
        return rowLabel.charAt(0) - 'A' + 1;
    }

    private void selectSeat(Seat seat) {
        selectedSeat = seat;

        if (selectedSeatLabel != null) {
            selectedSeatLabel.setText("Seat " + seat.getRowLabel() + seat.getSeatNumber());
        }
        if (seatTypeCombo != null) {
            seatTypeCombo.setValue(seat.getSeatType());
        }
        if (seatPriceMultiplierSpinner != null) {
            seatPriceMultiplierSpinner.getValueFactory().setValue(seat.getPriceMultiplier());
        }
        if (isActiveCheck != null) {
            isActiveCheck.setSelected(seat.isActive());
        }
        if (isAccessibleCheck != null) {
            isAccessibleCheck.setSelected(seat.isAccessible());
        }

        if (seatDetailPanel != null) {
            seatDetailPanel.setVisible(true);
        }
    }

    private void toggleSeatSelection(Button btn, Seat seat) {
        if (selectedSeatButtons.contains(btn)) {
            selectedSeatButtons.remove(btn);
            btn.getStyleClass().remove("seat-selected");
        } else {
            selectedSeatButtons.add(btn);
            btn.getStyleClass().add("seat-selected");
        }
    }

    private void updateStatistics() {
        int total = seats.size();
        long standard = seats.stream().filter(s -> "Standard".equalsIgnoreCase(s.getSeatType())).count();
        long premium = seats.stream().filter(s -> "Premium".equalsIgnoreCase(s.getSeatType())).count();
        long vip = seats.stream().filter(s -> "VIP".equalsIgnoreCase(s.getSeatType())).count();
        long disabled = seats.stream().filter(s -> !s.isActive()).count();

        if (totalSeatsLabel != null) totalSeatsLabel.setText(String.valueOf(total));
        if (standardSeatsLabel != null) standardSeatsLabel.setText(String.valueOf(standard));
        if (premiumSeatsLabel != null) premiumSeatsLabel.setText(String.valueOf(premium));
        if (vipSeatsLabel != null) vipSeatsLabel.setText(String.valueOf(vip));
        if (disabledSeatsLabel != null) disabledSeatsLabel.setText(String.valueOf(disabled));
    }

    @FXML
    private void handleSaveSeat() {
        if (selectedSeat == null) {
            showError("Please select a seat first.");
            return;
        }

        selectedSeat.setSeatType(seatTypeCombo.getValue());
        selectedSeat.setPriceMultiplier(seatPriceMultiplierSpinner.getValue());
        selectedSeat.setActive(isActiveCheck.isSelected());
        selectedSeat.setAccessible(isAccessibleCheck.isSelected());

        showLoading(true);

        new Thread(() -> {
            try {
                seatService.updateSeat(selectedSeat);

                Platform.runLater(() -> {
                    showLoading(false);
                    showSuccess("Seat updated!");
                    displaySeatMap();
                    updateStatistics();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error updating seat", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to update seat.");
                });
            }
        }).start();
    }

    @FXML
    private void handleBulkUpdate() {
        if (selectedSeatButtons.isEmpty()) {
            showError("No seats selected. Enable multi-select mode and select seats.");
            return;
        }

        String seatType = seatTypeCombo.getValue();
        double priceMultiplier = seatPriceMultiplierSpinner.getValue();
        boolean active = isActiveCheck.isSelected();

        showLoading(true);

        new Thread(() -> {
            try {
                // Find seats that match selected buttons
                List<Seat> seatsToUpdate = seats.stream()
                    .filter(s -> selectedSeatButtons.stream()
                        .anyMatch(btn -> btn.getText().equals(s.getRowLabel() + s.getSeatNumber())))
                    .toList();

                for (Seat seat : seatsToUpdate) {
                    seat.setSeatType(seatType);
                    seat.setPriceMultiplier(priceMultiplier);
                    seat.setActive(active);
                    seatService.updateSeat(seat);
                }

                Platform.runLater(() -> {
                    showLoading(false);
                    showSuccess("Updated " + seatsToUpdate.size() + " seats!");
                    selectedSeatButtons.clear();
                    isMultiSelectMode = false;
                    loadSeats();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error bulk updating seats", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to update seats.");
                });
            }
        }).start();
    }

    @FXML
    private void handleToggleMultiSelect() {
        isMultiSelectMode = !isMultiSelectMode;

        if (!isMultiSelectMode) {
            // Clear selections
            selectedSeatButtons.forEach(btn -> btn.getStyleClass().remove("seat-selected"));
            selectedSeatButtons.clear();
        }

        showInfo(isMultiSelectMode ?
            "Multi-select mode enabled. Click seats to select." :
            "Multi-select mode disabled.");
    }

    @FXML
    private void handleGenerateSeats() {
        int rows = rowsSpinner.getValue();
        int seatsPerRow = seatsPerRowSpinner.getValue();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Generate Seats");
        confirm.setHeaderText("Generate " + (rows * seatsPerRow) + " seats?");
        confirm.setContentText("This will replace all existing seats in this hall.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                generateSeats(rows, seatsPerRow);
            }
        });
    }

    private void generateSeats(int rows, int seatsPerRow) {
        showLoading(true);

        new Thread(() -> {
            try {
                // Delete existing seats
                for (Seat seat : seats) {
                    seatService.deleteSeat(seat.getId());
                }

                // Generate new seats
                List<Seat> newSeats = new ArrayList<>();
                for (int row = 0; row < rows; row++) {
                    String rowLabel = String.valueOf((char) ('A' + row));

                    for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                        Seat seat = new Seat();
                        seat.setCinemaHall(currentHall);
                        seat.setRowLabel(rowLabel);
                        seat.setSeatNumber(String.valueOf(seatNum));

                        // Determine seat type based on position
                        if (row >= rows - 2) {
                            seat.setSeatType("Premium");
                            seat.setPriceMultiplier(1.5);
                        } else if (seatNum >= seatsPerRow - 1 || seatNum <= 2) {
                            // Edge seats
                            seat.setSeatType("Standard");
                            seat.setPriceMultiplier(0.9);
                        } else {
                            seat.setSeatType("Standard");
                            seat.setPriceMultiplier(1.0);
                        }

                        seat.setActive(true);
                        seat.setAccessible(row == 0 && (seatNum == 1 || seatNum == seatsPerRow));

                        seatService.createSeat(seat);
                        newSeats.add(seat);
                    }
                }

                // Update hall capacity
                currentHall.setCapacity(rows * seatsPerRow);
                hallService.updateHall(currentHall);

                Platform.runLater(() -> {
                    showLoading(false);
                    showSuccess("Generated " + newSeats.size() + " seats!");
                    loadSeats();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error generating seats", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to generate seats.");
                });
            }
        }).start();
    }

    @FXML
    private void handleSelectRow() {
        if (selectedSeat == null) {
            showError("Please select a seat first to identify the row.");
            return;
        }

        isMultiSelectMode = true;
        String rowLabel = selectedSeat.getRowLabel();

        seats.stream()
            .filter(s -> s.getRowLabel().equals(rowLabel))
            .forEach(s -> {
                seatGrid.getChildren().stream()
                    .filter(node -> node instanceof Button)
                    .map(node -> (Button) node)
                    .filter(btn -> btn.getText().startsWith(rowLabel))
                    .forEach(btn -> {
                        if (!selectedSeatButtons.contains(btn)) {
                            selectedSeatButtons.add(btn);
                            btn.getStyleClass().add("seat-selected");
                        }
                    });
            });

        showInfo("Selected row " + rowLabel);
    }

    @FXML
    private void handleClearSelection() {
        selectedSeatButtons.forEach(btn -> btn.getStyleClass().remove("seat-selected"));
        selectedSeatButtons.clear();
        selectedSeat = null;
        if (seatDetailPanel != null) seatDetailPanel.setVisible(false);
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/admin/CinemaHallManagement.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) managementContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ui/styles/admin.css").toExternalForm());
            stage.setScene(scene);
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
