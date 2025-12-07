package com.esprit.models.cinemas;

import com.esprit.models.films.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    private String rowLabel;

    private String seatNumber;

    @Builder.Default
    private String type = "STANDARD"; // STANDARD, VIP, WHEELCHAIR

    @Builder.Default
    private Boolean isOccupied = false;

    private CinemaHall cinemaHall;

    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    @Builder.Default
    private Double priceMultiplier = 1.0;

    @Builder.Default
    private Boolean isAccessible = false;

    /**
     * Create a Seat instance without an id for use when creating new seats.
     *
     * @param rowLabel   the row label for the seat (e.g., "A", "B")
     * @param seatNumber the seat's number within its row
     * @param type       the type of the seat (STANDARD, VIP, WHEELCHAIR)
     * @param cinemaHall the cinema hall that contains this seat
     */
    public Seat(String rowLabel, String seatNumber, String type, CinemaHall cinemaHall) {
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.type = type != null ? type : "STANDARD";
        this.cinemaHall = cinemaHall;
        this.tickets = new ArrayList<>();
        this.priceMultiplier = 1.0;
        this.isAccessible = false;
    }

    /**
     * Create a Seat instance without an id for use when creating new seats (legacy constructor).
     *
     * @param seatNumber the seat's number within its row
     * @param rowNumber  the row index for the seat
     * @param type       the type of the seat (STANDARD, VIP, WHEELCHAIR)
     * @param isOccupied whether the seat is currently occupied
     * @param cinemaHall the cinema hall that contains this seat
     */
    @Deprecated(forRemoval = true)
    public Seat(Integer seatNumber, Integer rowNumber, String type, Boolean isOccupied, CinemaHall cinemaHall) {
        this.seatNumber = seatNumber != null ? seatNumber.toString() : null;
        this.rowLabel = rowNumber != null ? String.valueOf((char) (64 + rowNumber)) : null;
        this.type = type != null ? type : "STANDARD";
        this.cinemaHall = cinemaHall;
        this.tickets = new ArrayList<>();
        this.isOccupied = isOccupied != null ? isOccupied : false;
        this.priceMultiplier = 1.0;
        this.isAccessible = false;
    }

    /**
     * Get the seat type for display and styling.
     *
     * @return the seat type
     */
    public String getSeatType() {
        return this.type;
    }

    /**
     * Set the seat type.
     *
     * @param seatType the seat type to set
     */
    public void setSeatType(String seatType) {
        this.type = seatType;
    }

    /**
     * Check if the seat is active (available for booking).
     *
     * @return true if seat is active, false otherwise
     */
    public Boolean isActive() {
        return !isOccupied;
    }

    /**
     * Set the active status of the seat.
     *
     * @param active true to activate, false to deactivate
     */
    public void setActive(Boolean active) {
        this.isOccupied = !active;
    }

    /**
     * Get the price multiplier for this seat.
     *
     * @return the price multiplier
     */
    public Double getPriceMultiplier() {
        return this.priceMultiplier != null ? this.priceMultiplier : 1.0;
    }

    /**
     * Set the price multiplier for this seat.
     *
     * @param priceMultiplier the multiplier to set
     */
    public void setPriceMultiplier(Double priceMultiplier) {
        this.priceMultiplier = priceMultiplier != null ? priceMultiplier : 1.0;
    }

    /**
     * Check if the seat is accessible (wheelchair accessible).
     *
     * @return true if accessible, false otherwise
     */
    public Boolean isAccessible() {
        return this.isAccessible != null && this.isAccessible;
    }

    /**
     * Set the accessibility of the seat.
     *
     * @param accessible true if accessible, false otherwise
     */
    public void setAccessible(Boolean accessible) {
        this.isAccessible = accessible != null ? accessible : false;
    }

}

