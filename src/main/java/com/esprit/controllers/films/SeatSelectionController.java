package com.esprit.controllers.films;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.cinemas.Seat;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.SeatService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Controller class responsible for seat selection in the cinema booking
 * process.
 * 
 * <p>
 * This controller manages the seat grid interface where users can select their
 * preferred seats for a movie session. It visualizes the seat layout with color
 * coding to indicate available, occupied, and selected seats.
 * </p>
 * 
 * <p>
 * Key features:
 * </p>
 * <ul>
 * <li>Interactive seat grid with visual status indicators</li>
 * <li>Multi-seat selection capability</li>
 * <li>Integration with payment processing</li>
 * </ul>
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class SeatSelectionController {
    @FXML
    private GridPane seatGrid;
    @FXML
    private Button confirmButton;

    private MovieSession moviesession;
    private Client client;
    private List<Seat> selectedSeats = new ArrayList<>();

    /**
     * Initialize the controller with the specified movie session and client and load the seat layout.
     *
     * @param moviesession the MovieSession whose cinema hall seats should be displayed
     * @param client       the Client performing the selection
     * @throws IllegalArgumentException if {@code moviesession} or {@code client} is {@code null}
     */
    public void initialize(MovieSession moviesession, Client client) {
        if (moviesession == null || client == null) {
            throw new IllegalArgumentException("MovieSession and Client cannot be null");
        }

        this.moviesession = moviesession;
        this.client = client;
        loadSeats();
    }


    /**
     * Populate the seatGrid with buttons representing seats for the current movie session's cinema hall.
     *
     * Occupied seats are disabled and styled red; available seats are styled green. Clicking a seat
     * toggles its selection state and updates the controller's selectedSeats list.
     */
    private void loadSeats() {
        SeatService seatService = new SeatService();
        List<Seat> seats = seatService.getSeatsByCinemaHallId(moviesession.getCinemaHall().getId());

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
                    seatButton.setDisable(currentSeat.getIsOccupied());
                    seatButton.setStyle(currentSeat.getIsOccupied()
                            ? "-fx-background-color: red;"
                            : "-fx-background-color: green;");

                    seatButton.setOnAction(e -> handleSeatSelection(seatButton, currentSeat));
                }


                seatGrid.add(seatButton, col, row);
            }

        }

    }


    /**
     * Finds a specific seat in the list based on row and column coordinates.
     *
     * @param seats the list of seats to search through
     * @param row   the row number to find
     * @param col   the column number to find
     * @return the matching Seat object, or null if no match is found
     */
    private Seat findSeat(List<Seat> seats, int row, int col) {
        return seats.stream().filter(s -> s.getRowNumber() == row && s.getSeatNumber() == col).findFirst().orElse(null);
    }


    /**
     * Handles the selection and deselection of seats.
     * 
     * <p>
     * Toggles the selection state of a seat when clicked and updates the visual
     * appearance accordingly. Selected seats are highlighted in yellow, while
     * available
     * seats are shown in green.
     * </p>
     *
     * @param seatButton the button representing the seat in the UI
     * @param seat       the Seat model object associated with the button
     */
    private void handleSeatSelection(Button seatButton, Seat seat) {
        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
            seatButton.setStyle("-fx-background-color: green;");
        }
 else {
            selectedSeats.add(seat);
            seatButton.setStyle("-fx-background-color: yellow;");
        }

    }


    /**
     * Navigate to the payment screen for the currently selected seats.
     *
     * <p>If no seats are selected this method returns without changing the UI.
     * Otherwise it loads the payment view, initializes its controller with the current
     * movie session, client, and selected seats, and replaces the current scene with the payment scene.</p>
     *
     * @throws IOException if the payment view cannot be loaded
     */
    @FXML
    private void confirmSelection(ActionEvent event) throws IOException {
        if (selectedSeats.isEmpty()) {
            return;
        }


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/films/Paymentuser.fxml"));
        AnchorPane root = loader.load();

        PaymentUserController controller = loader.getController();
        controller.initWithSeats(moviesession, client, selectedSeats);

        Stage stage = (Stage) confirmButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

}