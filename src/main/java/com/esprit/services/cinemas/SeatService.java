package com.esprit.services.cinemas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.cinemas.Seat;
import com.esprit.utils.DataSource;
import com.esprit.utils.TableCreator;

import lombok.extern.slf4j.Slf4j;

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
    private Connection connection;
    private final CinemaHallService cinemaHallService;

    /**
     * Constructs a new SeatService instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public SeatService() {
        this.connection = DataSource.getInstance().getConnection();
        this.cinemaHallService = new CinemaHallService();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create seats table
            String createSeatsTable = """
                    CREATE TABLE seats (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        seat_number INT NOT NULL,
                        row_number INT NOT NULL,
                        is_occupied BOOLEAN DEFAULT FALSE,
                        cinema_hall_id BIGINT NOT NULL
                    )
                    """;
            tableCreator.createTableIfNotExists("seats", createSeatsTable);

        }
 catch (Exception e) {
            log.error("Error creating tables for SeatService", e);
        }

    }


    /**
     * Retrieves all seats that belong to the specified cinema hall.
     *
     * @param cinemaHallId the ID of the cinema hall whose seats should be returned
     * @return a list of Seat objects associated with the specified cinema hall; empty if none are found
     */
    public List<Seat> getSeatsByCinemaHallId(Long cinemaHallId) {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM seats WHERE cinema_hall_id = ?";

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

        }
 catch (SQLException e) {
            log.error("Error retrieving seats for cinema hall: " + cinemaHallId, e);
        }

        return seats;
    }


    /**
     * Update the occupied status of a seat identified by its ID.
     *
     * @param seatId    the identifier of the seat to update
     * @param isOccupied the new occupied status for the seat
     * @return `true` if a row was updated (status changed), `false` otherwise
     */
    public boolean updateSeatStatus(Long seatId, Boolean isOccupied) {
        String query = "UPDATE seats SET is_occupied = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, isOccupied);
            stmt.setLong(2, seatId);
            return stmt.executeUpdate() > 0;
        }
 catch (SQLException e) {
            log.error("Error updating seat status for seat: " + seatId, e);
            return false;
        }

    }


    /**
     * Inserts the given Seat into the database seats table.
     *
     * @param seat the Seat to insert; its associated CinemaHall must have a valid id
     * @throws RuntimeException if a database error prevents the seat from being created
     */
    public void create(Seat seat) {
        String query = "INSERT INTO seats (seat_number, row_number, is_occupied, cinema_hall_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, seat.getSeatNumber());
            stmt.setInt(2, seat.getRowNumber());
            stmt.setBoolean(3, seat.getIsOccupied());
            stmt.setLong(4, seat.getCinemaHall().getId());
            stmt.executeUpdate();
            log.info("Seat created successfully");
        }
 catch (SQLException e) {
            log.error("Error creating seat", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * Constructs a Seat object from the current row of the provided ResultSet.
     *
     * @param rs the ResultSet positioned at a valid seat row
     * @return the Seat built from the current row, or `null` if the referenced CinemaHall cannot be found or a SQL error occurs
     */
    private Seat buildSeat(ResultSet rs) {
        try {
            CinemaHall cinemaHall = cinemaHallService.getCinemaHallById(rs.getLong("cinema_hall_id"));
            if (cinemaHall == null) {
                log.warn("Cinema hall not found for seat id: " + rs.getLong("id"));
                return null;
            }


            return Seat.builder().id(rs.getLong("id")).seatNumber(rs.getInt("seat_number"))
                    .rowNumber(rs.getInt("row_number")).isOccupied(rs.getBoolean("is_occupied")).cinemaHall(cinemaHall)
                    .build();
        }
 catch (SQLException e) {
            log.error("Error building seat from ResultSet", e);
            return null;
        }

    }

}
