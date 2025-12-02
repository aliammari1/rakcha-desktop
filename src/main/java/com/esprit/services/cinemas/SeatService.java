package com.esprit.services.cinemas;

import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.cinemas.Seat;
import com.esprit.utils.DataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
/**
 * Service class providing business logic for the RAKCHA application. Implements
 * CRUD operations and business rules for data management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class SeatService {

    private final CinemaHallService cinemaHallService;
    private Connection connection;

    /**
     * Constructs a new SeatService instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public SeatService() {
        this.connection = DataSource.getInstance().getConnection();
        this.cinemaHallService = new CinemaHallService();
    }


    /**
     * Retrieves all seats that belong to the specified cinema hall.
     *
     * @param cinemaHallId the ID of the cinema hall whose seats should be returned
     * @return a list of Seat objects associated with the specified cinema hall; empty if none are found
     */
    public List<Seat> getSeatsByCinemaHallId(Long cinemaHallId) {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM seats WHERE hall_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaHallId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Seat seat = buildSeat(rs);
                    if (seat != null) {
                        seats.add(seat);
                    }

                }

            }

        } catch (SQLException e) {
            log.error("Error retrieving seats for cinema hall: " + cinemaHallId, e);
        }

        return seats;
    }


    /**
     * Inserts the given Seat into the database seats table.
     *
     * @param seat the Seat to insert; its associated CinemaHall must have a valid id
     * @throws RuntimeException if a database error prevents the seat from being created
     */
    public void create(Seat seat) {
        String query = "INSERT INTO seats (hall_id, row_label, seat_number, type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, seat.getCinemaHall().getId());
            stmt.setString(2, seat.getRowLabel());
            stmt.setString(3, seat.getSeatNumber());
            stmt.setString(4, seat.getType());
            stmt.executeUpdate();
            log.info("Seat created successfully");
        } catch (SQLException e) {
            log.error("Error creating seat", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates an existing seat in the database.
     *
     * @param seat the Seat with updated values; must have a valid id
     * @throws RuntimeException if a database error prevents the update
     */
    public void update(Seat seat) {
        String query = "UPDATE seats SET hall_id = ?, row_label = ?, seat_number = ?, type = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, seat.getCinemaHall().getId());
            stmt.setString(2, seat.getRowLabel());
            stmt.setString(3, seat.getSeatNumber());
            stmt.setString(4, seat.getType());
            stmt.setLong(5, seat.getId());
            stmt.executeUpdate();
            log.info("Seat updated successfully: " + seat.getId());
        } catch (SQLException e) {
            log.error("Error updating seat: " + seat.getId(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a seat from the database by its ID.
     *
     * @param seatId the ID of the seat to delete
     * @throws RuntimeException if a database error prevents the deletion
     */
    public void deleteSeat(Long seatId) {
        if (seatId == null || seatId <= 0) {
            log.warn("Invalid seat ID for deletion");
            return;
        }
        
        String query = "DELETE FROM seats WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, seatId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Seat deleted successfully: " + seatId);
            }
        } catch (SQLException e) {
            log.error("Error deleting seat: " + seatId, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves all seats for a cinema hall by hall ID.
     * Alias for getSeatsByCinemaHallId for controller convenience.
     *
     * @param hallId the ID of the cinema hall
     * @return a list of Seat objects for the hall
     */
    public List<Seat> getSeatsByHall(Long hallId) {
        return getSeatsByCinemaHallId(hallId);
    }


    /**
     * Constructs a Seat object from the current row of the provided ResultSet.
     *
     * @param rs the ResultSet positioned at a valid seat row
     * @return the Seat built from the current row, or `null` if the referenced CinemaHall cannot be found or a SQL error occurs
     */
    private Seat buildSeat(ResultSet rs) {
        try {
            CinemaHall cinemaHall = cinemaHallService.getCinemaHallById(rs.getLong("hall_id"));
            if (cinemaHall == null) {
                log.warn("Cinema hall not found for seat id: " + rs.getLong("id"));
                return null;
            }


            return Seat.builder().id(rs.getLong("id")).seatNumber(rs.getString("seat_number"))
                .rowLabel(rs.getString("row_label")).type(rs.getString("type")).cinemaHall(cinemaHall)
                .build();
        } catch (SQLException e) {
            log.error("Error building seat from ResultSet", e);
            return null;
        }

    }

    /**
     * Create a new seat (convenience alias for create method).
     * @param seat the seat to create
     */
    public void createSeat(Seat seat) {
        this.create(seat);
    }

    /**
     * Update an existing seat (convenience alias for update method).
     * @param seat the seat to update
     */
    public void updateSeat(Seat seat) {
        this.update(seat);
    }

}
