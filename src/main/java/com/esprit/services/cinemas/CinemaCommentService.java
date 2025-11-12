package com.esprit.services.cinemas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaComment;
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
 * Service class providing business logic for cinema comments in the RAKCHA
 * application.
 * Implements CRUD operations and business rules for cinema comment data
 * management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class CinemaCommentService implements IService<CinemaComment> {
    private final Connection connection;
    private final CinemaService cinemaService;
    private final UserService userService;

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
            "id", "cinema_id", "client_id", "comment_text", "sentiment", "created_at"
    }
;

    /**
     * Constructs a new CinemaCommentService with database connection and required
     * services. Creates tables if they don't exist.
     */
    public CinemaCommentService() {
        this.connection = DataSource.getInstance().getConnection();
        this.cinemaService = new CinemaService();
        this.userService = new UserService();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create cinema_comment table
            String createCinemaCommentTable = """
                    CREATE TABLE cinema_comment (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        cinema_id BIGINT NOT NULL,
                        client_id BIGINT NOT NULL,
                        comment_text TEXT NOT NULL,
                        sentiment VARCHAR(50),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """;
            tableCreator.createTableIfNotExists("cinema_comment", createCinemaCommentTable);

        }
 catch (Exception e) {
            log.error("Error creating tables for CinemaCommentService", e);
        }

    }


    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(CinemaComment cinemaComment) {
        String query = "INSERT INTO cinema_comment (cinema_id, client_id, comment_text, sentiment) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaComment.getCinema().getId());
            stmt.setLong(2, cinemaComment.getClient().getId());
            stmt.setString(3, cinemaComment.getCommentText());
            stmt.setString(4, cinemaComment.getSentiment());
            stmt.executeUpdate();
            log.info("Cinema comment created successfully");
        }
 catch (SQLException e) {
            log.error("Error creating cinema comment", e);
        }

    }


    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(CinemaComment cinemaComment) {
        String query = "UPDATE cinema_comment SET comment_text = ?, sentiment = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cinemaComment.getCommentText());
            stmt.setString(2, cinemaComment.getSentiment());
            stmt.setLong(3, cinemaComment.getId());
            stmt.executeUpdate();
            log.info("Cinema comment updated successfully");
        }
 catch (SQLException e) {
            log.error("Error updating cinema comment", e);
        }

    }


    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(CinemaComment cinemaComment) {
        String query = "DELETE FROM cinema_comment WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaComment.getId());
            stmt.executeUpdate();
            log.info("Cinema comment deleted successfully");
        }
 catch (SQLException e) {
            log.error("Error deleting cinema comment", e);
        }

    }


    @Override
    /**
     * Retrieves cinema comments with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of cinema comments
     */
    public Page<CinemaComment> read(PageRequest pageRequest) {
        final List<CinemaComment> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM cinema_comment";

        // Validate sort column to prevent SQL injection
        if (pageRequest.hasSorting() &&
                !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            log.warn("Invalid sort column: {}
. Using default sorting.", pageRequest.getSortBy());
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
                    CinemaComment comment = buildCinemaComment(rs);
                    if (comment != null) {
                        content.add(comment);
                    }

                }

            }


            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        }
 catch (final SQLException e) {
            log.error("Error retrieving paginated cinema comments: {}
", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
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
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CinemaComment comment = buildCinemaComment(rs);
                if (comment != null) {
                    comments.add(comment);
                }

            }

        }
 catch (SQLException e) {
            log.error("Error getting comments by cinema id: " + cinemaId, e);
        }

        return comments;
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
        }
 catch (SQLException e) {
            log.error("Error building cinema comment from ResultSet", e);
            return null;
        }

    }

}

