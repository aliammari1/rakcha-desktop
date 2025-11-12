package com.esprit.services.series;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.series.Feedback;
import com.esprit.services.IService;
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
public class IServiceFeedbackImpl implements IService<Feedback> {
    private static final Logger LOGGER = Logger.getLogger(IServiceFeedbackImpl.class.getName());
    public Connection connection;
    public Statement statement;

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = { "id", "id_user", "description", "date", "id_episode" }
;

    /**
     * Constructs a new IServiceFeedbackImpl instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public IServiceFeedbackImpl() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create feedback table
            String createFeedbackTable = """
                    CREATE TABLE feedback (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        id_user BIGINT NOT NULL,
                        description TEXT,
                        date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        id_episode BIGINT NOT NULL
                    )
                    """;
            tableCreator.createTableIfNotExists("feedback", createFeedbackTable);

        }
 catch (Exception e) {
            log.error("Error creating tables for IServiceFeedbackImpl", e);
        }

    }


    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final Feedback feedback) {
        final String req = """
                INSERT INTO feedback
                (id_user, description, date, id_episode)
                VALUES(?, ?, ?, ?)
                """;
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setLong(1, feedback.getUserId());
            ps.setString(2, feedback.getDescription());
            ps.setTimestamp(3, new Timestamp(feedback.getDate().getTime()));
            ps.setLong(4, feedback.getEpisodeId());
            ps.executeUpdate();
            LOGGER.info("Feedback added successfully!");
        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final Feedback feedback) {
        final String req = "UPDATE feedback SET id_user=?, description=?, date=?, id_episode=? WHERE id=?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setLong(1, feedback.getUserId());
            pst.setString(2, feedback.getDescription());
            pst.setTimestamp(3, new Timestamp(feedback.getDate().getTime()));
            pst.setLong(4, feedback.getEpisodeId());
            pst.setLong(5, feedback.getId());
            pst.executeUpdate();
            LOGGER.info("Feedback updated successfully!");
        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final Feedback feedback) {
        final String req = "DELETE FROM feedback WHERE id=?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setLong(1, feedback.getId());
            pst.executeUpdate();
            LOGGER.info("Feedback deleted successfully!");
        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    @Override
    /**
     * Retrieves feedback with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of feedback
     */
    public Page<Feedback> read(PageRequest pageRequest) {
        final List<Feedback> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM feedback";

        // Validate sort column to prevent SQL injection
        if (pageRequest.hasSorting() &&
                !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            LOGGER.warning("Invalid sort column: " + pageRequest.getSortBy() + ". Using default sorting.");
            pageRequest = PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), "date", "DESC");
        }


        try {
            // Get total count
            final String countQuery = PaginationQueryBuilder.buildCountQuery(baseQuery);
            final long totalElements = PaginationQueryBuilder.executeCountQuery(connection, countQuery);

            // Get paginated results
            final String paginatedQuery = PaginationQueryBuilder.buildPaginatedQuery(baseQuery, pageRequest);

            try (final Statement st = this.connection.createStatement();
                    final ResultSet rs = st.executeQuery(paginatedQuery)) {
                while (rs.next()) {
                    final Feedback feedback = buildFeedbackFromResultSet(rs);
                    content.add(feedback);
                }

            }


            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving paginated feedback: " + e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }

    }


    /**
     * Helper method to build Feedback object from ResultSet.
     *
     * @param rs the ResultSet containing feedback data
     * @return the Feedback object
     * @throws SQLException if there's an error reading from ResultSet
     */
    private Feedback buildFeedbackFromResultSet(ResultSet rs) throws SQLException {
        return Feedback.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("id_user"))
                .description(rs.getString("description"))
                .date(rs.getTimestamp("date"))
                .episodeId(rs.getLong("id_episode"))
                .build();
    }

}

