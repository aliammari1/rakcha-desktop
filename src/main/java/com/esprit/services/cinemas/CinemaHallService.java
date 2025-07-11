package com.esprit.services.cinemas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.TableCreator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * Service class providing business logic for cinema halls in the RAKCHA
 * application.
 * Implements CRUD operations and business rules for cinema hall data
 * management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class CinemaHallService implements IService<CinemaHall> {
    private final Connection connection;
    private final CinemaService cinemaService;

    /**
     * Constructs a new CinemaHallService with database connection and required
     * services. Creates tables if they don't exist.
     */
    public CinemaHallService() {
        this.connection = DataSource.getInstance().getConnection();
        this.cinemaService = new CinemaService();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create cinema_hall table
            String createCinemaHallTable = """
                    CREATE TABLE cinema_hall (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        cinema_id BIGINT NOT NULL,
                        seat_capacity INT NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        screen_type VARCHAR(100),
                        is_available BOOLEAN DEFAULT TRUE
                    )
                    """;
            tableCreator.createTableIfNotExists("cinema_hall", createCinemaHallTable);

        } catch (Exception e) {
            log.error("Error creating tables for CinemaHallService", e);
        }
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(CinemaHall cinemaHall) {
        String query = "INSERT INTO cinema_hall (cinema_id, seat_capacity, name) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaHall.getCinema().getId());
            stmt.setInt(2, cinemaHall.getSeatCapacity());
            stmt.setString(3, cinemaHall.getName());
            stmt.executeUpdate();
            log.info("Cinema hall created successfully");
        } catch (SQLException e) {
            log.error("Error creating cinema hall", e);
        }
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(CinemaHall cinemaHall) {
        String query = "UPDATE cinema_hall SET cinema_id = ?, seat_capacity = ?, name = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaHall.getCinema().getId());
            stmt.setInt(2, cinemaHall.getSeatCapacity());
            stmt.setString(3, cinemaHall.getName());
            stmt.setLong(4, cinemaHall.getId());
            stmt.executeUpdate();
            log.info("Cinema hall updated successfully");
        } catch (SQLException e) {
            log.error("Error updating cinema hall", e);
        }
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(CinemaHall cinemaHall) {
        String query = "DELETE FROM cinema_hall WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaHall.getId());
            stmt.executeUpdate();
            log.info("Cinema hall deleted successfully");
        } catch (SQLException e) {
            log.error("Error deleting cinema hall", e);
        }
    }

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
    public List<CinemaHall> read() {
        List<CinemaHall> cinemaHalls = new ArrayList<>();
        String query = "SELECT * FROM cinema_hall";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CinemaHall hall = buildCinemaHall(rs);
                if (hall != null) {
                    cinemaHalls.add(hall);
                }
            }
        } catch (SQLException e) {
            log.error("Error reading cinema halls", e);
        }
        return cinemaHalls;
    }

    /**
     * Retrieves a cinema hall by its ID.
     *
     * @param id the ID of the cinema hall to retrieve
     * @return the cinema hall with the specified ID, or null if not found
     */
    public CinemaHall getCinemaHallById(Long id) {
        String query = "SELECT * FROM cinema_hall WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildCinemaHall(rs);
            }
        } catch (SQLException e) {
            log.error("Error getting cinema hall by id: " + id, e);
        }
        return null;
    }

    /**
     * Retrieves a cinema hall by its name.
     *
     * @param name the name of the cinema hall to retrieve
     * @return the cinema hall with the specified name, or null if not found
     */
    public CinemaHall getCinemaHallByName(String name) {
        String query = "SELECT * FROM cinema_hall WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildCinemaHall(rs);
            }
        } catch (SQLException e) {
            log.error("Error getting cinema hall by name: " + name, e);
        }
        return null;
    }

    /**
     * Retrieves cinema halls by cinema ID.
     *
     * @param cinemaId the ID of the cinema to get halls for
     * @return list of cinema halls for the specified cinema
     */
    public List<CinemaHall> getCinemaHallsByCinemaId(Long cinemaId) {
        List<CinemaHall> cinemaHalls = new ArrayList<>();
        String query = "SELECT * FROM cinema_hall WHERE cinema_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CinemaHall hall = buildCinemaHall(rs);
                if (hall != null) {
                    cinemaHalls.add(hall);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting cinema halls by cinema id: " + cinemaId, e);
        }
        return cinemaHalls;
    }

    /**
     * @param rs
     * @return CinemaHall
     */
    private CinemaHall buildCinemaHall(ResultSet rs) {
        try {
            Cinema cinema = cinemaService.getCinemaById(rs.getLong("cinema_id"));
            if (cinema == null) {
                log.warn("Cinema not found for cinema hall id: " + rs.getLong("id"));
                return null;
            }

            return CinemaHall.builder().id(rs.getLong("id")).cinema(cinema).seatCapacity(rs.getInt("seat_capacity"))
                    .name(rs.getString("name")).build();
        } catch (SQLException e) {
            log.error("Error building cinema hall from ResultSet", e);
            return null;
        }
    }
}
