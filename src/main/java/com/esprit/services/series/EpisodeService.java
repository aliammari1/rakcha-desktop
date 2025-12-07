package com.esprit.services.series;

import com.esprit.models.series.Episode;
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
public class EpisodeService implements IService<Episode> {

    private static final Logger LOGGER = Logger.getLogger(EpisodeService.class.getName());
    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
        "id", "title", "episode_number", "season_id", "image_url", "video_url", "duration_min"
    };
    private final Connection connection;

    /**
     * Constructs a new IServiceEpisodeImpl instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public EpisodeService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final Episode episode) {
        final String req = "INSERT INTO episodes (season_id, episode_number, title, image_url, video_url, duration_min) VALUES (?, ?, ?, ?, ?, ?)";
        try (final PreparedStatement st = this.connection.prepareStatement(req)) {
            st.setLong(1, episode.getSeasonId());
            st.setInt(2, episode.getEpisodeNumber());
            st.setString(3, episode.getTitle());
            st.setString(4, episode.getImageUrl());
            st.setString(5, episode.getVideoUrl());
            st.setInt(6, episode.getDurationMin());
            st.executeUpdate();
            LOGGER.info("Episode created successfully");
        } catch (SQLException e) {
            log.error("Error creating episode: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create episode", e);
        }

    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final Episode episode) {
        final String req = "UPDATE episodes SET season_id = ?, episode_number = ?, title = ?, image_url = ?, video_url = ?, duration_min = ? WHERE id = ?";
        try (final PreparedStatement st = this.connection.prepareStatement(req)) {
            st.setLong(1, episode.getSeasonId());
            st.setInt(2, episode.getEpisodeNumber());
            st.setString(3, episode.getTitle());
            st.setString(4, episode.getImageUrl());
            st.setString(5, episode.getVideoUrl());
            st.setInt(6, episode.getDurationMin());
            st.setLong(7, episode.getId());
            st.executeUpdate();
            LOGGER.info("Episode updated successfully");
        } catch (SQLException e) {
            log.error("Error updating episode: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update episode", e);
        }

    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final Episode episode) {
        if (episode == null) {
            log.error("Cannot delete null episode");
            throw new IllegalArgumentException("Episode cannot be null");
        }

        if (episode.getId() == null) {
            log.error("Cannot delete episode with null ID");
            throw new IllegalArgumentException("Episode ID cannot be null");
        }

        deleteById(episode.getId());
    }

    /**
     * Deletes an episode by its ID.
     *
     * @param id the ID of the episode to delete
     */
    public void deleteById(final Long id) {
        final String req = "DELETE FROM episodes WHERE id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setLong(1, id);
            ps.executeUpdate();
            LOGGER.info("Episode deleted successfully");
        } catch (SQLException e) {
            log.error("Error deleting episode: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete episode", e);
        }

    }

    @Override
    /**
     * Counts the total number of episodes in the database.
     *
     * @return the total count of episodes
     */
    public int count() {
        String query = "SELECT COUNT(*) as count FROM episodes";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            log.error("Error counting episodes: {}", e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean exists(Long id) {
        final String query = "SELECT COUNT(*) FROM episodes WHERE id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(query)) {
            ps.setLong(1, id);
            try (final var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            log.error("Error checking episode existence: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    /**
     * Retrieves episodes with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of episodes
     */
    public Page<Episode> read(PageRequest pageRequest) {
        final List<Episode> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM episodes";

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

            try (final Statement st = this.connection.createStatement();
                 final ResultSet rs = st.executeQuery(paginatedQuery)) {
                while (rs.next()) {
                    final Episode episode = buildEpisodeFromResultSet(rs);
                    content.add(episode);
                }

            }

            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated episodes: {}", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }

    }

    /**
     * Helper method to build Episode object from ResultSet.
     *
     * @param rs the ResultSet containing episode data
     * @return the Episode object
     * @throws SQLException if there's an error reading from ResultSet
     */
    private Episode buildEpisodeFromResultSet(ResultSet rs) throws SQLException {
        return Episode.builder()
            .id(rs.getLong("id"))
            .seasonId(rs.getLong("season_id"))
            .episodeNumber(rs.getInt("episode_number"))
            .title(rs.getString("title"))
            .imageUrl(rs.getString("image_url"))
            .videoUrl(rs.getString("video_url"))
            .durationMin(rs.getInt("duration_min"))
            .build();
    }

    /**
     * Retrieves episodes for a specific series.
     *
     * @param seriesId the ID of the series to retrieve episodes for
     * @return list of episodes for the specified series
     */
    public List<Episode> retrieveBySeries(final Long seriesId) {
        final List<Episode> episodes = new ArrayList<>();
        final String req = "SELECT e.* FROM episodes e JOIN seasons s ON e.season_id = s.id WHERE s.series_id = ? ORDER BY s.season_number, e.episode_number";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setLong(1, seriesId);
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Episode episode = buildEpisodeFromResultSet(rs);
                    episodes.add(episode);
                }

            }

        } catch (SQLException e) {
            log.error("Error retrieving episodes by series {}: {}", seriesId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve episodes by series", e);
        }

        return episodes;
    }

    /**
     * Get episodes for a specific season.
     *
     * @param seasonId the season ID
     * @return list of episodes for the season
     */
    public List<Episode> getEpisodesBySeason(Long seasonId) {
        final List<Episode> episodes = new ArrayList<>();
        final String query = "SELECT * FROM episodes WHERE season_id = ? ORDER BY episode_number";

        try (final PreparedStatement ps = this.connection.prepareStatement(query)) {
            ps.setLong(1, seasonId);
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Episode episode = buildEpisodeFromResultSet(rs);
                    if (episode != null) {
                        episodes.add(episode);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving episodes for season {}: {}", seasonId, e.getMessage(), e);
        }

        return episodes;
    }

    @Override
    /**
     * Retrieves all episodes from the database.
     *
     * @return a list of all episodes
     */
    public List<Episode> getAll() {
        final List<Episode> episodes = new ArrayList<>();
        final String query = "SELECT * FROM episodes ORDER BY id";
        try (final PreparedStatement ps = this.connection.prepareStatement(query)) {
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Episode episode = buildEpisodeFromResultSet(rs);
                    if (episode != null) {
                        episodes.add(episode);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving all episodes", e);
        }
        return episodes;
    }

    @Override
    /**
     * Retrieves an episode by its ID.
     *
     * @param id the ID of the episode
     * @return the episode if found, null otherwise
     */
    public Episode getById(Long id) {
        final String query = "SELECT * FROM episodes WHERE id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(query)) {
            ps.setLong(1, id);
            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildEpisodeFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving episode by id: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Episode> search(String query) {
        List<Episode> episodes = new ArrayList<>();
        String sql = "SELECT * FROM episodes WHERE title LIKE ? OR description LIKE ? ORDER BY episode_number";
        try (final PreparedStatement ps = this.connection.prepareStatement(sql)) {
            String pattern = "%" + query + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Episode episode = buildEpisodeFromResultSet(rs);
                    if (episode != null) {
                        episodes.add(episode);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error searching episodes", e);
        }
        return episodes;
    }

}

