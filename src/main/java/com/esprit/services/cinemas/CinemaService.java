package com.esprit.services.cinemas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.users.CinemaManager;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
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
public class CinemaService implements IService<Cinema> {
    private final Connection connection;
    private final UserService userService;

    /**
     * Constructs a new CinemaService instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public CinemaService() {
        this.connection = DataSource.getInstance().getConnection();
        this.userService = new UserService();

        // Create tables if they don't exist
        TableCreator tableCreator = new TableCreator(connection);
        tableCreator.createTableIfNotExists("cinema", """
                    CREATE TABLE cinema (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(50) NOT NULL,
                        address VARCHAR(100) NOT NULL,
                        manager_id BIGINT NOT NULL,
                        logo_path VARCHAR(1000) NOT NULL,
                        status VARCHAR(50) NOT NULL
                    )
                """);
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(Cinema cinema) {
        String query = "INSERT INTO cinema (name, address, manager_id, logo_path, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cinema.getName());
            stmt.setString(2, cinema.getAddress());

            // Handle null manager case
            if (cinema.getManager() != null && cinema.getManager().getId() != null) {
                stmt.setLong(3, cinema.getManager().getId());
            } else {
                log.warn("Cinema manager is null for cinema: " + cinema.getName());
                throw new IllegalArgumentException("Cinema must have a valid manager with an ID");
            }

            stmt.setString(4, cinema.getLogoPath());
            stmt.setString(5, cinema.getStatus() != null ? cinema.getStatus() : "Pending");
            stmt.executeUpdate();
            log.info("Cinema created successfully");
        } catch (SQLException e) {
            log.error("Error creating cinema", e);
            throw new RuntimeException("Failed to create cinema", e);
        }
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(Cinema cinema) {
        String query = "UPDATE cinema SET name = ?, address = ?, logo_path = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cinema.getName());
            stmt.setString(2, cinema.getAddress());
            stmt.setString(3, cinema.getLogoPath());
            stmt.setString(4, cinema.getStatus());
            stmt.setLong(5, cinema.getId());
            stmt.executeUpdate();
            log.info("Cinema updated successfully");
        } catch (SQLException e) {
            log.error("Error updating cinema", e);
        }
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(Cinema cinema) {
        String query = "DELETE FROM cinema WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinema.getId());
            stmt.executeUpdate();
            log.info("Cinema deleted successfully");
        } catch (SQLException e) {
            log.error("Error deleting cinema", e);
        }
    }

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
    public List<Cinema> read() {
        List<Cinema> cinemas = new ArrayList<>();
        String query = "SELECT * FROM cinema";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cinema cinema = buildCinema(rs);
                if (cinema != null) {
                    cinemas.add(cinema);
                }
            }
        } catch (SQLException e) {
            log.error("Error reading cinemas", e);
        }
        return cinemas;
    }

    /**
     * Sorts cinemas by the specified field.
     *
     * @param orderBy the field to sort by
     * @return sorted list of cinemas
     */
    public List<Cinema> sort(String orderBy) {
        List<Cinema> cinemas = new ArrayList<>();
        String query = "SELECT * FROM cinema ORDER BY " + orderBy;
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cinema cinema = buildCinema(rs);
                if (cinema != null) {
                    cinemas.add(cinema);
                }
            }
        } catch (SQLException e) {
            log.error("Error sorting cinemas", e);
        }
        return cinemas;
    }

    /**
     * Retrieves a cinema by its ID.
     *
     * @param cinemaId the ID of the cinema to retrieve
     * @return the cinema with the specified ID, or null if not found
     */
    public Cinema getCinemaById(Long cinemaId) {
        String query = "SELECT * FROM cinema WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildCinema(rs);
            }
        } catch (SQLException e) {
            log.error("Error getting cinema by id: " + cinemaId, e);
        }
        return null;
    }

    /**
     * Retrieves a cinema by its name.
     *
     * @param name the name of the cinema to retrieve
     * @return the cinema with the specified name, or null if not found
     */
    public Cinema getCinemaByName(String name) {
        String query = "SELECT * FROM cinema WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildCinema(rs);
            }
        } catch (SQLException e) {
            log.error("Error getting cinema by name: " + name, e);
        }
        return null;
    }

    /**
     * @param rs
     * @return Cinema
     */
    private Cinema buildCinema(ResultSet rs) {
        try {
            CinemaManager manager = (CinemaManager) userService.getUserById(rs.getLong("manager_id"));
            if (manager == null) {
                log.warn("Cinema manager not found for cinema id: " + rs.getLong("id"));
                return null;
            }

            return Cinema.builder().id(rs.getLong("id")).name(rs.getString("name")).address(rs.getString("address"))
                    .manager(manager).logoPath(rs.getString("logo_path")).status(rs.getString("status")).build();
        } catch (SQLException e) {
            log.error("Error building cinema from ResultSet", e);
            return null;
        }
    }
}
