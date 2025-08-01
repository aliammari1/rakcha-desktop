package com.esprit.services.cinemas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaComment;
import com.esprit.models.cinemas.CinemaRating;
import com.esprit.models.users.Client;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
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
public class CinemaRatingService implements IService<CinemaRating> {
    private final Connection connection;
    private final CinemaService cinemaService;
    private final UserService userService;

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
            "id", "client_id", "cinema_id", "rating", "created_at"
    };

    /**
     * Constructs a new CinemaRatingService instance.
     * Initializes database connection, related services, and creates tables if they
     * don't exist.
     */
    public CinemaRatingService() {
        this.connection = DataSource.getInstance().getConnection();
        this.cinemaService = new CinemaService();
        this.userService = new UserService();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create cinema_rating table
            String createCinemaRatingTable = """
                    CREATE TABLE cinema_rating (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        client_id BIGINT NOT NULL,
                        cinema_id BIGINT NOT NULL,
                        rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        UNIQUE(client_id, cinema_id)
                    )
                    """;
            tableCreator.createTableIfNotExists("cinema_rating", createCinemaRatingTable);

        } catch (Exception e) {
            log.error("Error creating tables for CinemaRatingService", e);
        }
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(CinemaRating cinemaRating) {
        // Delete existing rating from same user for same cinema
        deleteByClientAndCinema(cinemaRating.getClient().getId(), cinemaRating.getCinema().getId());

        String query = "INSERT INTO cinema_rating (cinema_id, client_id, rating) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaRating.getCinema().getId());
            stmt.setLong(2, cinemaRating.getClient().getId());
            stmt.setInt(3, cinemaRating.getRating());
            stmt.executeUpdate();
            log.info("Cinema rating created successfully");
        } catch (SQLException e) {
            log.error("Error creating cinema rating", e);
        }
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(CinemaRating cinemaRating) {
        String query = "UPDATE cinema_rating SET rating = ? WHERE cinema_id = ? AND client_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cinemaRating.getRating());
            stmt.setLong(2, cinemaRating.getCinema().getId());
            stmt.setLong(3, cinemaRating.getClient().getId());
            stmt.executeUpdate();
            log.info("Cinema rating updated successfully");
        } catch (SQLException e) {
            log.error("Error updating cinema rating", e);
        }
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(CinemaRating cinemaRating) {
        String query = "DELETE FROM cinema_rating WHERE cinema_id = ? AND client_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaRating.getCinema().getId());
            stmt.setLong(2, cinemaRating.getClient().getId());
            stmt.executeUpdate();
            log.info("Cinema rating deleted successfully");
        } catch (SQLException e) {
            log.error("Error deleting cinema rating", e);
        }
    }

    
    @Override
    /**
     * Retrieves cinema ratings with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of cinema ratings
     */
    public Page<CinemaRating> read(PageRequest pageRequest) {
        final List<CinemaRating> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM cinema_rating";

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
                    CinemaRating rating = buildCinemaRating(rs);
                    if (rating != null) {
                        content.add(rating);
                    }
                }
            }

            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated cinema ratings: {}", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }
    }

    /**
     * Retrieves the rating for a specific client and cinema.
     *
     * @param clientId the ID of the client
     * @param cinemaId the ID of the cinema
     * @return the rating value, or null if not found
     */
    public Integer getRatingForClientAndCinema(Long clientId, Long cinemaId) {
        String query = "SELECT rating FROM cinema_rating WHERE cinema_id = ? AND client_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaId);
            stmt.setLong(2, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rating");
                }
            }
        } catch (SQLException e) {
            log.error("Error getting rating for client and cinema", e);
        }
        return null;
    }

    /**
     * Retrieves the average rating for a cinema.
     *
     * @param cinemaId the ID of the cinema
     * @return the average rating for the cinema
     */
    public Double getAverageRating(Long cinemaId) {
        String query = "SELECT AVG(rating) as average FROM cinema_rating WHERE cinema_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("average");
                }
            }
        } catch (SQLException e) {
            log.error("Error getting average rating for cinema: " + cinemaId, e);
        }
        return 0.0;
    }

    /**
     * Retrieves the TopRatedCinemas value.
     *
     * @return the TopRatedCinemas value
     */
    public List<Cinema> getTopRatedCinemas() {
        List<Cinema> topRatedCinemas = new ArrayList<>();
        String query = "SELECT cinema_id, AVG(rating) as average_rating FROM cinema_rating GROUP BY cinema_id ORDER BY average_rating DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Long cinemaId = rs.getLong("cinema_id");
                Cinema cinema = cinemaService.getCinemaById(cinemaId);
                if (cinema != null) {
                    topRatedCinemas.add(cinema);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting top rated cinemas", e);
        }
        return topRatedCinemas;
    }

    /**
     * @param clientId
     * @param cinemaId
     */
    private void deleteByClientAndCinema(Long clientId, Long cinemaId) {
        String query = "DELETE FROM cinema_rating WHERE cinema_id = ? AND client_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaId);
            stmt.setLong(2, clientId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting existing rating", e);
        }
    }

    /**
     * Retrieves comments by cinema ID.
     *
     * @param cinemaId the ID of the cinema to get comments for
     * @return list of comments for the specified cinema
     */
    public List<CinemaComment> getCommentsByCinemaId(Long cinemaId) {
        List<CinemaComment> comments = new ArrayList<>();
        String query = "SELECT * FROM cinema_comment WHERE cinema_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CinemaComment comment = buildCinemaComment(rs);
                    if (comment != null) {
                        comments.add(comment);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error getting comments by cinema id: " + cinemaId, e);
        }
        return comments;
    }

    /**
     * @param rs
     * @return CinemaRating
     */
    private CinemaRating buildCinemaRating(ResultSet rs) {
        try {
            Cinema cinema = cinemaService.getCinemaById(rs.getLong("cinema_id"));
            Client client = (Client) userService.getUserById(rs.getLong("client_id"));

            if (cinema == null || client == null) {
                log.warn("Missing required entities for cinema rating id: " + rs.getLong("id"));
                return null;
            }

            return CinemaRating.builder().id(rs.getLong("id")).cinema(cinema).client(client).rating(rs.getInt("rating"))
                    .build();
        } catch (SQLException e) {
            log.error("Error building cinema rating from ResultSet", e);
            return null;
        }
    }

    /**
     * @param rs
     * @return CinemaComment
     */
    private CinemaComment buildCinemaComment(ResultSet rs) {
        try {
            Cinema cinema = cinemaService.getCinemaById(rs.getLong("cinema_id"));
            Client client = (Client) userService.getUserById(rs.getLong("client_id"));

            if (cinema == null || client == null) {
                log.warn("Missing required entities for cinema comment id: " + rs.getLong("id"));
                return null;
            }

            return CinemaComment.builder().id(rs.getLong("id")).cinema(cinema).client(client)
                    .commentText(rs.getString("comment_text")).sentiment(rs.getString("sentiment")).build();
        } catch (SQLException e) {
            log.error("Error building cinema comment from ResultSet", e);
            return null;
        }
    }
}
