package com.esprit.services.series;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.series.Favorite;
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
public class IServiceFavoriteImpl implements IService<Favorite> {
    private static final Logger LOGGER = Logger.getLogger(IServiceFavoriteImpl.class.getName());
    public Connection connection;
    public Statement statement;

    /**
     * Performs IServiceFavoriteImpl operation.
     *
     * @return the result of the operation
     */
    public IServiceFavoriteImpl() {
        this.connection = DataSource.getInstance().getConnection();
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *            the entity to create
     */
    public void create(final Favorite favorite) {
        final String req = """
                INSERT INTO favorites
                (user_id, series_id)
                VALUES(?, ?)
                """;
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setLong(1, favorite.getUserId());
            ps.setLong(2, favorite.getSeriesId());
            ps.executeUpdate();
            LOGGER.info("Favorite created successfully!");
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *            the entity to update
     */
    public void update(final Favorite favorite) {
        final String req = "UPDATE favorites SET user_id=?, series_id=? WHERE id=?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setLong(1, favorite.getUserId());
            pst.setLong(2, favorite.getSeriesId());
            pst.setLong(3, favorite.getId());
            pst.executeUpdate();
            LOGGER.info("Favorite updated successfully!");
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *            the ID of the entity to delete
     */
    public void delete(final Favorite favorite) {
        final String req = "DELETE FROM favorites WHERE id=?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setLong(1, favorite.getId());
            pst.executeUpdate();
            LOGGER.info("Favorite deleted successfully!");
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
    public List<Favorite> read() {
        final List<Favorite> list = new ArrayList<>();
        final String req = "SELECT * FROM favorites";
        try (final Statement st = this.connection.createStatement(); final ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                list.add(Favorite.builder().id(rs.getLong("id")).userId(rs.getLong("user_id"))
                        .seriesId(rs.getLong("series_id")).build());
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }

    /**
     * Retrieves the ByIdUserAndIdSerie value.
     *
     * @return the ByIdUserAndIdSerie value
     */
    public Favorite getByIdUserAndIdSerie(final long userId, final Long serieId) throws SQLException {
        Favorite favorite = null;
        final String query = "SELECT * FROM favorites WHERE user_id = ? AND series_id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(query)) {
            ps.setLong(1, userId);
            ps.setLong(2, serieId);
            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    favorite = Favorite.builder().id(rs.getLong("id")).userId(rs.getLong("user_id"))
                            .seriesId(rs.getLong("series_id")).build();
                }
            }
        }
        return favorite;
    }

    /**
     * Performs showFavoritesList operation.
     *
     * @return the result of the operation
     */
    public List<Favorite> showFavoritesList(final int userId) {
        final List<Favorite> list = new ArrayList<>();
        final String req = "SELECT * FROM favorites WHERE user_id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setInt(1, userId);
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(Favorite.builder().id(rs.getLong("id")).userId(rs.getLong("user_id"))
                            .seriesId(rs.getLong("series_id")).build());
                }
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }
}
