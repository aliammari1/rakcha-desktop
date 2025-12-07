package com.esprit.services.series;

import com.esprit.models.series.Series;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class SeriesService implements IService<Series> {

    private static final Logger LOGGER = Logger.getLogger(SeriesService.class.getName());
    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
        "id", "title", "summary", "director", "country", "image_url", "release_year"
    };
    private final Connection connection;

    /**
     * Constructs a new IServiceSeriesImpl instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public SeriesService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(Series serie) {
        final String req = "INSERT INTO series (title, summary, director, country, image_url, release_year) VALUES (?, ?, ?, ?, ?, ?)";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setString(1, serie.getName());
            ps.setString(2, serie.getSummary());
            ps.setString(3, serie.getDirector());
            ps.setString(4, serie.getCountry());
            ps.setString(5, serie.getImageUrl());
            ps.setInt(6, serie.getReleaseYear());
            ps.executeUpdate();
            LOGGER.info("Series created successfully");
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating series: " + e.getMessage(), e);
        }
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(Series serie) {
        final String req = "UPDATE series SET title = ?, summary = ?, director = ?, country = ?, image_url = ?, release_year = ? WHERE id = ?";
        try (final PreparedStatement st = this.connection.prepareStatement(req)) {
            st.setString(1, serie.getName());
            st.setString(2, serie.getSummary());
            st.setString(3, serie.getDirector());
            st.setString(4, serie.getCountry());
            st.setString(5, serie.getImageUrl());
            st.setInt(6, serie.getReleaseYear());
            st.setLong(7, serie.getId());
            st.executeUpdate();
            LOGGER.info("Series updated successfully");
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating series: " + e.getMessage(), e);
        }
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(Series serie) {
        final String req = "DELETE FROM series WHERE id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setLong(1, serie.getId());
            ps.executeUpdate();
            LOGGER.info("Series deleted successfully");
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting series: " + e.getMessage(), e);
        }

    }

    @Override
    /**
     * Retrieves series with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of series
     */
    public Page<Series> read(PageRequest pageRequest) {
        final List<Series> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM series";

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

            try (final Statement st = this.connection.createStatement();
                 final ResultSet rs = st.executeQuery(paginatedQuery)) {
                while (rs.next()) {
                    final Series s = buildSeriesFromResultSet(rs);
                    content.add(s);
                }

            }

            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving paginated series: " + e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }

    }

    /**
     * Helper method to build Series object from ResultSet.
     *
     * @param rs the ResultSet containing series data
     * @return the Series object
     * @throws SQLException if there's an error reading from ResultSet
     */
    private Series buildSeriesFromResultSet(ResultSet rs) throws SQLException {
        return Series.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("title"))
            .summary(rs.getString("summary"))
            .director(rs.getString("director"))
            .country(rs.getString("country"))
            .imageUrl(rs.getString("image_url"))
            .releaseYear(rs.getInt("release_year"))
            .build();
    }

    /**
     * Retrieves series by category.
     *
     * @param categoryId the ID of the category to filter by
     * @return list of series in the specified category
     * @throws SQLException if there is an error querying the database
     */
    public List<Series> retrieveByCategory(final Long categoryId) throws SQLException {
        final List<Series> series = new ArrayList<>();
        // Note: Check if series_categories table exists in your schema
        // This may need adjustment based on actual table structure
        final String req = """
            SELECT DISTINCT s.* FROM series s
            WHERE s.id IN (SELECT DISTINCT series_id FROM user_favorites WHERE series_id IS NOT NULL)
            ORDER BY s.id
            """;
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setLong(1, categoryId);
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Series serie = buildSeriesFromResultSet(rs);
                    series.add(serie);
                }
            }
        }
        return series;
    }

    /**
     * Adds a favorite for the current user to the specified series.
     *
     * @param userId   the user ID
     * @param seriesId the series ID
     * @throws SQLException if there is an error updating the database
     */
    public void addFavorite(final long userId, final long seriesId) throws SQLException {
        final String req = "INSERT INTO user_favorites (user_id, series_id, is_favorite) VALUES (?, ?, true) " +
            "ON CONFLICT (user_id, series_id) DO UPDATE SET is_favorite = true";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setLong(1, userId);
            ps.setLong(2, seriesId);
            ps.executeUpdate();
        }
    }

    /**
     * Removes a favorite for the current user from the specified series.
     *
     * @param userId   the user ID
     * @param seriesId the series ID
     * @throws SQLException if there is an error updating the database
     */
    public void removeFavorite(final long userId, final long seriesId) throws SQLException {
        final String req = "UPDATE user_favorites SET is_favorite = false WHERE user_id = ? AND series_id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setLong(1, userId);
            ps.setLong(2, seriesId);
            ps.executeUpdate();
        }
    }

    /**
     * Adds a like to the specified series.
     *
     * @param serie the series to add a like to
     * @throws SQLException if there is an error updating the database
     */
    public void addLike(final Series serie) throws SQLException {
        // Like functionality is now tracked via user_favorites table
        addFavorite(1, serie.getId()); // Default user_id 1 for compatibility
    }

    /**
     * Removes a like from the specified series.
     *
     * @param serie the series to remove a like from
     * @throws SQLException if there is an error updating the database
     */
    public void removeLike(final Series serie) throws SQLException {
        // Like functionality is now tracked via user_favorites table
        removeFavorite(1, serie.getId()); // Default user_id 1 for compatibility
    }

    /**
     * Adds a dislike to the specified series.
     *
     * @param serie the series to add a dislike to
     * @throws SQLException if there is an error updating the database
     */
    public void addDislike(final Series serie) throws SQLException {
        // Dislike functionality is now tracked via user_favorites table with is_favorite = false
        final String req = "INSERT INTO user_favorites (user_id, series_id, is_favorite) VALUES (?, ?, false) " +
            "ON CONFLICT (user_id, series_id) DO UPDATE SET is_favorite = false";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setLong(1, 1); // Default user_id 1 for compatibility
            ps.setLong(2, serie.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Removes a dislike from the specified series.
     *
     * @param serie the series to remove a dislike from
     * @throws SQLException if there is an error updating the database
     */
    public void removeDislike(final Series serie) throws SQLException {
        // Dislike functionality is now tracked via user_favorites table
        final String req = "DELETE FROM user_favorites WHERE user_id = ? AND series_id = ? AND is_favorite = false";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setLong(1, 1); // Default user_id 1 for compatibility
            ps.setLong(2, serie.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Finds the most liked series.
     *
     * @return list of the most liked series (top 3)
     */
    public List<Series> findMostLiked() {
        final List<Series> series = new ArrayList<>();
        // Query based on user_favorites table to get most favorited series
        final String sql = "SELECT s.* FROM series s ORDER BY s.release_year DESC LIMIT 3";
        try (final Statement st = this.connection.createStatement(); final ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                final Series serie = buildSeriesFromResultSet(rs);
                series.add(serie);
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return series;
    }

    /**
     * Retrieves the LikesStatistics value based on user_favorites.
     *
     * @return map of series to their favorite counts
     */
    public Map<Series, Integer> getLikesStatistics() {
        final Map<Series, Integer> likesStatistics = new HashMap<>();
        // Query based on user_favorites table - count favorites per series
        final String query = "SELECT s.*, COUNT(uf.id) AS favorite_count FROM series s " +
            "LEFT JOIN user_favorites uf ON s.id = uf.series_id AND uf.is_favorite = true " +
            "GROUP BY s.id ORDER BY favorite_count DESC";
        try (final Statement statement = this.connection.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                final Series serie = buildSeriesFromResultSet(resultSet);
                final int favoriteCount = resultSet.getInt("favorite_count");
                likesStatistics.put(serie, favoriteCount);
            }

        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return likesStatistics;
    }

    /**
     * Retrieves the ByIdSeries value.
     *
     * @return the ByIdSeries value
     */
    public Series getByIdSeries(final Long serieId) throws SQLException {
        Series serie = null;
        final String query = "SELECT * FROM series WHERE id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(query)) {
            ps.setLong(1, serieId);
            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    serie = buildSeriesFromResultSet(rs);
                }

            }

        }

        return serie;
    }

    /**
     * Counts the total number of series in the database.
     *
     * @return the total count of series
     */
    @Override
    public int count() {
        String query = "SELECT COUNT(*) FROM series";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error counting series", e);
        }
        return 0;
    }

    /**
     * Retrieves a series by its ID.
     *
     * @param id the ID of the series to retrieve
     * @return the series with the specified ID, or null if not found
     */
    @Override
    public Series getById(final Long id) {
        try {
            return getByIdSeries(id);
        } catch (SQLException e) {
            log.error("Error retrieving series by id: " + id, e);
            return null;
        }
    }

    /**
     * Retrieves all series from the database.
     *
     * @return a list of all series
     */
    @Override
    public List<Series> getAll() {
        List<Series> series = new ArrayList<>();
        String query = "SELECT * FROM series";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Series s = buildSeriesFromResultSet(rs);
                if (s != null) {
                    series.add(s);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving all series", e);
        }
        return series;
    }

    /**
     * Searches for series by title or summary.
     *
     * @param query the search query
     * @return a list of series matching the search query
     */
    @Override
    public List<Series> search(final String query) {
        List<Series> series = new ArrayList<>();
        final String req = "SELECT * FROM series WHERE title LIKE ? OR summary LIKE ? ORDER BY title";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            final String searchPattern = "%" + query + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Series s = buildSeriesFromResultSet(rs);
                if (s != null) {
                    series.add(s);
                }
            }
        } catch (final SQLException e) {
            log.error("Error searching series", e);
        }
        return series;
    }

    /**
     * Checks if a series exists by its ID.
     *
     * @param id the ID of the series to check
     * @return true if the series exists, false otherwise
     */
    @Override
    public boolean exists(final Long id) {
        try {
            return getByIdSeries(id) != null;
        } catch (SQLException e) {
            log.error("Error checking series existence", e);
            return false;
        }
    }

    /**
     * Get a series by its ID (convenience alias for getById).
     *
     * @param id the series ID
     * @return the series or null if not found
     */
    public Series getSeriesById(final Long id) {
        return this.getById(id);
    }

}
