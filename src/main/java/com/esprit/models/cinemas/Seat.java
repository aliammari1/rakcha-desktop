package com.esprit.models.cinemas;

import java.util.ArrayList;
import java.util.List;

import com.esprit.models.films.Ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Cinema management entity class for the RAKCHA application. Handles
 * cinema-related data with database persistence capabilities.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class Seat {

    private Long id;

    private Integer seatNumber;

    private Integer rowNumber;

    @Builder.Default
    private Boolean isOccupied = false;

    private CinemaHall cinemaHall;

    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * Create a Seat instance without an id for use when creating new seats.
     *
     * @param seatNumber the seat's number within its row
     * @param rowNumber the row index for the seat
     * @param isOccupied whether the seat is currently occupied
     * @param cinemaHall the cinema hall that contains this seat
     */
    public Seat(Integer seatNumber, Integer rowNumber, Boolean isOccupied, CinemaHall cinemaHall) {
        this.seatNumber = seatNumber;
        this.rowNumber = rowNumber;
        this.isOccupied = isOccupied;
        this.cinemaHall = cinemaHall;
        this.tickets = new ArrayList<>();
    }

}
