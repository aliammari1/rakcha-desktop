package com.esprit.services.series;

import com.esprit.models.series.Favorite;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
/**
 * Service class providing business logic for the RAKCHA application. Implements
 * CRUD operations and business rules for data management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class FavoriteService implements IService<Favorite> {

    private static final Logger LOGGER = Logger.getLogger(FavoriteService.class.getName());
    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {"id", "user_id", "series_id"};
    public Connection connection;
    public Statement statement;

    /**
     * Constructs a new IServiceFavoriteImpl instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public FavoriteService() {
        this.connection = DataSource.getInstance().getConnection();
    }


    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final Favorite favorite) {
        final String req = """
            INSERT INTO user_favorites
            (user_id, movie_id, series_id, created_at)
            VALUES(?, ?, ?, CURRENT_TIMESTAMP)
            """;
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setLong(1, favorite.getUserId());

            // Set movie_id or series_id based on what's available
            if (favorite.getMovieId() != null) {
                ps.setLong(2, favorite.getMovieId());
                ps.setNull(3, java.sql.Types.INTEGER);
            } else if (favorite.getSeriesId() != null) {
                ps.setNull(2, java.sql.Types.INTEGER);
                ps.setLong(3, favorite.getSeriesId());
            } else {
                throw new IllegalArgumentException("Either movieId or seriesId must be set");
            }

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
     *               the entity to update
     */
    public void update(final Favorite favorite) {
        final String req = "UPDATE user_favorites SET user_id=?, movie_id=?, series_id=? WHERE id=?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setLong(1, favorite.getUserId());

            if (favorite.getMovieId() != null) {
                pst.setLong(2, favorite.getMovieId());
                pst.setNull(3, java.sql.Types.INTEGER);
            } else if (favorite.getSeriesId() != null) {
                pst.setNull(2, java.sql.Types.INTEGER);
                pst.setLong(3, favorite.getSeriesId());
            } else {
                throw new IllegalArgumentException("Either movieId or seriesId must be set");
            }

            pst.setLong(4, favorite.getId());
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
     *           the ID of the entity to delete
     */
    public void delete(final Favorite favorite) {
        final String req = "DELETE FROM user_favorites WHERE id=?";
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
     * Counts the total number of favorites in the database.
     *
     * @return the total count of favorites
     */
    public int count() {
        String query = "SELECT COUNT(*) as count FROM user_favorites";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting favorites: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean exists(Long id) {
        final String query = "SELECT COUNT(*) FROM user_favorites WHERE id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(query)) {
            ps.setLong(1, id);
            try (final var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking favorite existence: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    /**
     * Retrieves favorites with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of favorites
     */
    public Page<Favorite> read(PageRequest pageRequest) {
        final List<Favorite> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM user_favorites";

        // Validate sort column to prevent SQL injection
        if (pageRequest.hasSorting() &&
            !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            LOGGER.warning("Invalid sort column: " + pageRequest.getSortBy() + ". Using default sorting.");
            pageRequest = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        }


        try {
            // Get total count
            final String countQuery = PaginationQueryBuilder.buildCountQuery(baseQuery);
            final long totalElements = PaginationQueryBuilder.executeCountQuery(connection, countQuery);

            // Get paginated results
            final String paginatedQuery = PaginationQueryBuilder.buildPaginatedQuery(baseQuery, pageRequest);

            try (final PreparedStatement stmt = connection.prepareStatement(paginatedQuery);
                 final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final Favorite favorite = buildFavoriteFromResultSet(rs);
                    content.add(favorite);
                }

            }


            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving paginated favorites: " + e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }

    }


    /**
     * Helper method to build Favorite object from ResultSet.
     *
     * @param rs the ResultSet containing favorite data
     * @return the Favorite object
     * @throws SQLException if there's an error reading from ResultSet
     */
    private Favorite buildFavoriteFromResultSet(ResultSet rs) throws SQLException {
        return Favorite.builder()
            .id(rs.getLong("id"))
            .userId(rs.getLong("user_id"))
            .movieId(rs.getObject("movie_id") != null ? rs.getLong("movie_id") : null)
            .seriesId(rs.getObject("series_id") != null ? rs.getLong("series_id") : null)
            .build();
    }


    /**
     * Retrieves the ByIdUserAndIdSerie value.
     *
     * @return the ByIdUserAndIdSerie value
     */
    public Favorite getByIdUserAndIdSerie(final long userId, final Long serieId) throws SQLException {
        Favorite favorite = null;
        final String query = "SELECT * FROM user_favorites WHERE user_id = ? AND series_id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(query)) {
            ps.setLong(1, userId);
            ps.setLong(2, serieId);
            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    favorite = buildFavoriteFromResultSet(rs);
                }

            }

        }

        return favorite;
    }

    /**
     * Retrieves a favorite by user ID and movie ID.
     *
     * @return the favorite if found, null otherwise
     */
    public Favorite getByIdUserAndIdMovie(final long userId, final Long movieId) throws SQLException {
        Favorite favorite = null;
        final String query = "SELECT * FROM user_favorites WHERE user_id = ? AND movie_id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(query)) {
            ps.setLong(1, userId);
            ps.setLong(2, movieId);
            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    favorite = buildFavoriteFromResultSet(rs);
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
        final String req = "SELECT * FROM user_favorites WHERE user_id = ? ORDER BY id";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setInt(1, userId);
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildFavoriteFromResultSet(rs));
                }

            }

        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return list;
    }

    @Override
    /**
     * Retrieves all favorites from the database.
     *
     * @return a list of all favorites
     */
    public List<Favorite> getAll() {
        final List<Favorite> list = new ArrayList<>();
        final String req = "SELECT * FROM user_favorites ORDER BY id";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildFavoriteFromResultSet(rs));
                }
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }

    @Override
    /**
     * Retrieves a favorite by its ID.
     *
     * @param id the ID of the favorite
     * @return the favorite if found, null otherwise
     */
    public Favorite getById(Long id) {
        final String query = "SELECT * FROM user_favorites WHERE id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(query)) {
            ps.setLong(1, id);
            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildFavoriteFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving favorite by id", e);
        }
        return null;
    }

    @Override
    public List<Favorite> search(String query) {
        final List<Favorite> list = new ArrayList<>();
        final String req = "SELECT * FROM user_favorites WHERE name LIKE ? OR content_title LIKE ? ORDER BY id";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            String pattern = "%" + query + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildFavoriteFromResultSet(rs));
                }
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }

}

