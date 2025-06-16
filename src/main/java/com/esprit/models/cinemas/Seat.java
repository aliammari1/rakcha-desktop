package com.esprit.models.cinemas;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.esprit.models.films.Ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats")
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "row_number", nullable = false)
    private Integer rowNumber;

    @Column(name = "is_occupied", nullable = false)
    @Builder.Default
    private Boolean isOccupied = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_hall_id", nullable = false)
    private CinemaHall cinemaHall;

    @ManyToMany(mappedBy = "reservedSeats", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * Constructor without id for creating new seat instances.
     */
    public Seat(Integer seatNumber, Integer rowNumber, Boolean isOccupied, CinemaHall cinemaHall) {
        this.seatNumber = seatNumber;
        this.rowNumber = rowNumber;
        this.isOccupied = isOccupied;
        this.cinemaHall = cinemaHall;
        this.tickets = new ArrayList<>();
    }
}
