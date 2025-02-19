package com.esprit.controllers.films;

import com.esprit.models.cinemas.Seat;
import com.esprit.models.cinemas.Seance;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.SeatService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class SeatSelectionController {
    @FXML
    private GridPane seatGrid;
    @FXML
    private Button confirmButton;

    private Seance seance;
    private Client client;
    private List<Seat> selectedSeats = new ArrayList<>();

    public void initialize(Seance seance, Client client) {
        this.seance = seance;
        this.client = client;
        loadSeats();
    }

    private void loadSeats() {
        SeatService seatService = new SeatService();
        List<Seat> seats = seatService.getSeatsBySalleId(seance.getId_salle().getId());

        int maxRow = 0;
        int maxCol = 0;
        for (Seat seat : seats) {
            maxRow = Math.max(maxRow, seat.getRowNumber());
            maxCol = Math.max(maxCol, seat.getSeatNumber());
        }

        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                Button seatButton = new Button();
                seatButton.setPrefSize(40, 40);

                final Seat currentSeat = findSeat(seats, row + 1, col + 1);
                if (currentSeat != null) {
                    seatButton.setDisable(currentSeat.isOccupied());
                    seatButton.setStyle(
                            currentSeat.isOccupied() ? "-fx-background-color: red;" : "-fx-background-color: green;");

                    seatButton.setOnAction(e -> handleSeatSelection(seatButton, currentSeat));
                }

                seatGrid.add(seatButton, col, row);
            }
        }
    }

    private Seat findSeat(List<Seat> seats, int row, int col) {
        return seats.stream()
                .filter(s -> s.getRowNumber() == row && s.getSeatNumber() == col)
                .findFirst()
                .orElse(null);
    }

    private void handleSeatSelection(Button seatButton, Seat seat) {
        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
            seatButton.setStyle("-fx-background-color: green;");
        } else {
            selectedSeats.add(seat);
            seatButton.setStyle("-fx-background-color: yellow;");
        }
    }

    @FXML
    private void confirmSelection(ActionEvent event) throws IOException {
        if (selectedSeats.isEmpty()) {
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/films/Paymentuser.fxml"));
        AnchorPane root = loader.load();

        PaymentuserController controller = loader.getController();
        controller.initWithSeats(seance, client, selectedSeats);

        Stage stage = (Stage) confirmButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }
}
