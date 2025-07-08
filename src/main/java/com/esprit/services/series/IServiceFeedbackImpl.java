package com.esprit.services.series;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.series.Feedback;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;

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

    /**
     * Constructs a new IServiceFeedbackImpl instance.
     * Initializes database connection.
     */
    public IServiceFeedbackImpl() {
        this.connection = DataSource.getInstance().getConnection();
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
        } catch (final SQLException e) {
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
        } catch (final SQLException e) {
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
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
    public List<Feedback> read() {
        final List<Feedback> list = new ArrayList<>();
        final String req = "SELECT * FROM feedback";
        try (final Statement st = this.connection.createStatement(); final ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                list.add(Feedback.builder().id(rs.getLong("id")).userId(rs.getLong("id_user"))
                        .description(rs.getString("description")).date(rs.getTimestamp("date"))
                        .episodeId(rs.getLong("id_episode")).build());
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }
}
