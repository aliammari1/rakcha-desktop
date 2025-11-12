package com.esprit.services.series;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.series.Series;
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
public class IServiceSeriesImpl implements IService<Series> {
    private static final Logger LOGGER = Logger.getLogger(IServiceSeriesImpl.class.getName());
    private final Connection connection;

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
            "id", "name", "summary", "director", "country", "image",
            "liked", "number_of_likes", "disliked", "number_of_dislikes"
    }
;

    /**
     * Constructs a new IServiceSeriesImpl instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public IServiceSeriesImpl() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        TableCreator tableCreator = new TableCreator(connection);

        // Create series categories table
        tableCreator.createTableIfNotExists("series_categories", """
                    CREATE TABLE series_categories (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(50) NOT NULL,
                        description VARCHAR(50) NOT NULL
                    )
                """);

        // Create series table
        tableCreator.createTableIfNotExists("serie", """
                    CREATE TABLE serie (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(30) NOT NULL,
                        summary VARCHAR(50) NOT NULL,
                        director VARCHAR(50) NOT NULL,
                        country VARCHAR(50) NOT NULL,
                        image VARCHAR(255) NOT NULL,
                        liked INT DEFAULT NULL,
                        number_of_likes INT DEFAULT NULL,
                        disliked INT DEFAULT NULL,
                        number_of_dislikes INT DEFAULT NULL,
                        category_id BIGINT
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
    public void create(Series serie) {
        final String req = "INSERT INTO serie (name, summary, director, country, image, liked, number_of_likes, disliked, number_of_dislikes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setString(1, serie.getName());
            ps.setString(2, serie.getSummary());
            ps.setString(3, serie.getDirector());
            ps.setString(4, serie.getCountry());
            ps.setString(5, serie.getImage());
            ps.setInt(6, 0);
            ps.setInt(7, 0);
            ps.setInt(8, 0);
            ps.setInt(9, 0);
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
        final String req = "UPDATE serie SET name = ?, summary = ?, director = ?, country = ?, image = ?, liked = ?, number_of_likes = ?, disliked = ?, number_of_dislikes = ? WHERE id = ?";
        try (final PreparedStatement st = this.connection.prepareStatement(req)) {
            st.setString(1, serie.getName());
            st.setString(2, serie.getSummary());
            st.setString(3, serie.getDirector());
            st.setString(4, serie.getCountry());
            st.setString(5, serie.getImage());
            st.setInt(6, serie.getLiked());
            st.setInt(7, serie.getNumberOfLikes());
            st.setInt(8, serie.getDisliked());
            st.setInt(9, serie.getNumberOfDislikes());
            st.setLong(10, serie.getId());
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
        final String req = "DELETE FROM serie WHERE id = ?";
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
        final String baseQuery = "SELECT * FROM serie";

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
                .name(rs.getString("name"))
                .summary(rs.getString("summary"))
                .director(rs.getString("director"))
                .country(rs.getString("country"))
                .image(rs.getString("image"))
                .liked(rs.getInt("liked"))
                .numberOfLikes(rs.getInt("number_of_likes"))
                .disliked(rs.getInt("disliked"))
                .numberOfDislikes(rs.getInt("number_of_dislikes"))
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
        final String req = """
                SELECT s.* FROM serie s
                JOIN series_categories sc ON s.id = sc.series_id
                WHERE sc.category_id = ?
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
     * Adds a like to the specified series.
     * 
     * @param serie the series to add a like to
     * @throws SQLException if there is an error updating the database
     */
    public void addLike(final Series serie) throws SQLException {
        final String req = "UPDATE serie SET liked = ?, number_of_likes = ? WHERE id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setInt(1, 1);
            ps.setInt(2, serie.getNumberOfLikes() + 1);
            ps.setLong(3, serie.getId());
            ps.executeUpdate();
        }

    }


    /**
     * Removes a like from the specified series.
     * 
     * @param serie the series to remove a like from
     * @throws SQLException if there is an error updating the database
     */
    public void removeLike(final Series serie) throws SQLException {
        final String req = "UPDATE serie SET liked = ?, number_of_likes = ? WHERE id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setInt(1, 0);
            ps.setInt(2, Math.max(0, serie.getNumberOfLikes() - 1));
            ps.setLong(3, serie.getId());
            ps.executeUpdate();
        }

    }


    /**
     * Adds a dislike to the specified series.
     * 
     * @param serie the series to add a dislike to
     * @throws SQLException if there is an error updating the database
     */
    public void addDislike(final Series serie) throws SQLException {
        final String req = "UPDATE serie SET disliked = ?, number_of_dislikes = ? WHERE id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setInt(1, 1);
            ps.setInt(2, serie.getNumberOfDislikes() + 1);
            ps.setLong(3, serie.getId());
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
        final String req = "UPDATE serie SET disliked = ?, number_of_dislikes = ? WHERE id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setInt(1, 0);
            ps.setInt(2, Math.max(0, serie.getNumberOfDislikes() - 1));
            ps.setLong(3, serie.getId());
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
        final String sql = "SELECT * FROM serie ORDER BY number_of_likes DESC LIMIT 3";
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
     * Retrieves the LikesStatistics value.
     *
     * @return the LikesStatistics value
     */
    public Map<Series, Integer> getLikesStatistics() {
        final Map<Series, Integer> likesStatistics = new HashMap<>();
        try (final Statement statement = this.connection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT * FROM serie ORDER BY id")) {
            while (resultSet.next()) {
                final Series serie = Series.builder().id(resultSet.getLong("id")).name(resultSet.getString("name"))
                        .build();
                final int nbLikes = resultSet.getInt("number_of_likes");
                likesStatistics.put(serie, nbLikes);
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
        final String query = "SELECT * FROM serie WHERE id = ?";
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

}

