package com.esprit.services.cinemas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
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

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
            "id", "cinema_id", "seat_capacity", "name", "screen_type", "is_available"
    }
;

    /**
     * Initialize a CinemaHallService and ensure the required database table exists.
     *
     * Initializes the JDBC connection and the dependent CinemaService, and creates
     * the `cinema_hall` table if it does not already exist.
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


    /**
     * Insert a CinemaHall record into the database.
     *
     * Persists the cinema hall's associated cinema id, seat capacity, and name as a new row
     * in the cinema_hall table.
     *
     * @param cinemaHall the CinemaHall to persist; its associated Cinema must have a valid `id`
     */
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


    /**
     * Update the database record for the given CinemaHall.
     *
     * Updates the cinema_id, seat_capacity, and name columns for the row matching the CinemaHall's id.
     *
     * @param cinemaHall the CinemaHall containing the new values; its id identifies which row to update
     */
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


    /**
     * Deletes the specified cinema hall from the database.
     *
     * @param cinemaHall the cinema hall to delete; its `id` identifies the row to remove
     */
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
     * Retrieves cinema halls with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of cinema halls
     */
    public Page<CinemaHall> read(PageRequest pageRequest) {
        final List<CinemaHall> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM cinema_hall";

        // Validate sort column to prevent SQL injection
        if (pageRequest.hasSorting() &&
                !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            log.warn("Invalid sort column: {}. Using default sorting.", pageRequest.getSortBy());
            pageRequest = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        }


        try {
            // Get total count
            final String countQuery = PaginationQueryBuilder.buildCountQuery(baseQuery);
            final long totalElements = PaginationQueryBuilder.executeCountQuery(connection, countQuery);

            // Get paginated results
            final String paginatedQuery = PaginationQueryBuilder.buildPaginatedQuery(baseQuery, pageRequest);

            try (PreparedStatement stmt = connection.prepareStatement(paginatedQuery);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CinemaHall hall = buildCinemaHall(rs);
                    if (hall != null) {
                        content.add(hall);
                    }

                }

            }


            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated cinema halls: {}", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }

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
     * Finds a cinema hall by its exact name.
     *
     * @param name the exact name of the cinema hall to search for
     * @return the matching CinemaHall, or null if none is found
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
     * Retrieve cinema halls for a given cinema.
     *
     * @param cinemaId the ID of the cinema
     * @return a list of CinemaHall objects belonging to the specified cinema; an empty list if none are found or if an error occurs
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
     * Builds a CinemaHall from the current row of the given ResultSet.
     *
     * @param rs the ResultSet positioned at a row containing cinema_hall columns
     * @return the constructed CinemaHall, or `null` if the referenced Cinema is not found or a database error occurs
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
