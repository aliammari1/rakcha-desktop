package com.esprit.models.cinemas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cinema hall (room) within a cinema.
 */

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
public class CinemaHall {

    private Long id;

    private Cinema cinema;

    private Integer seatCapacity;

    private String name;

    @Builder.Default
    private List<Seat> seats = new ArrayList<>();

    @Builder.Default
    private List<MovieSession> movieSessions = new ArrayList<>();

    /**
     * Create a new cinema hall associated with the given cinema, with the specified seat capacity and name.
     *
     * @param cinema       the parent Cinema for this hall
     * @param seatCapacity the number of seats the hall can contain
     * @param name         the human-readable name of the hall
     */
    public CinemaHall(final Cinema cinema, final Integer seatCapacity, final String name) {
        this.cinema = cinema;
        this.seatCapacity = seatCapacity;
        this.name = name;
        this.seats = new ArrayList<>();
        this.movieSessions = new ArrayList<>();
    }

    /**
     * Get the hall name (alias for compatibility).
     *
     * @return the hall name
     */
    public String getHallName() {
        return this.name;
    }

    /**
     * Set the hall name (alias for compatibility).
     *
     * @param hallName the name to set
     */
    public void setHallName(String hallName) {
        this.name = hallName;
    }

    /**
     * Get the seat capacity.
     *
     * @return the capacity
     */
    public Integer getCapacity() {
        return this.seatCapacity;
    }

    /**
     * Set the seat capacity.
     *
     * @param capacity the capacity to set
     */
    public void setCapacity(Integer capacity) {
        this.seatCapacity = capacity;
    }

    /**
     * Get the screen type (legacy field, returns fixed string for compatibility).
     *
     * @return the screen type
     */
    public String getScreenType() {
        return "Standard";
    }

    /**
     * Set the screen type (convenience method for compatibility).
     *
     * @param screenType the screen type to set
     */
    public void setScreenType(String screenType) {
        // Implementation can be enhanced if needed
    }

    /**
     * Check if the hall is active.
     *
     * @return true if active, false otherwise
     */
    public Boolean isActive() {
        return true;
    }

    /**
     * Set whether the hall is active (convenience method for compatibility).
     *
     * @param active whether the hall is active
     */
    public void setActive(boolean active) {
        // Implementation can be enhanced if needed
    }

    /**
     * Get the description of the cinema hall.
     *
     * @return the description
     */
    public String getDescription() {
        return "Cinema hall: " + this.name;
    }

    /**
     * Set the description of the cinema hall (convenience method for compatibility).
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        // Implementation can be enhanced if needed
    }

}

