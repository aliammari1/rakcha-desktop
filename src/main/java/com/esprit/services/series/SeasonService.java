package com.esprit.services.series;

import com.esprit.models.series.Episode;
import com.esprit.models.series.Season;
import com.esprit.utils.DataSource;
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

/**
 * Service class for managing seasons of series.
 * Handles CRUD operations for seasons and related queries.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SeasonService {

    private static final Logger LOGGER = Logger.getLogger(SeasonService.class.getName());
    private final Connection connection;

    /**
     * Constructs a new SeasonService instance.
     */
    public SeasonService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * Creates a new season.
     *
     * @param season the Season to create
     * @throws SQLException if a database error occurs
     */
    public void create(Season season) throws SQLException {
        String query = "INSERT INTO seasons (series_id, season_number, title) VALUES (?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setLong(1, season.getSeriesId());
            pst.setInt(2, season.getSeasonNumber());
            pst.setString(3, season.getTitle());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                season.setId(rs.getLong(1));
            }
            LOGGER.info("Season created successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating season", e);
            throw e;
        }
    }

    /**
     * Updates an existing season.
     *
     * @param season the Season to update
     * @throws SQLException if a database error occurs
     */
    public void update(Season season) throws SQLException {
        String query = "UPDATE seasons SET series_id = ?, season_number = ?, title = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, season.getSeriesId());
            pst.setInt(2, season.getSeasonNumber());
            pst.setString(3, season.getTitle());
            pst.setLong(4, season.getId());
            pst.executeUpdate();
            LOGGER.info("Season updated successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating season", e);
            throw e;
        }
    }

    /**
     * Deletes a season by ID.
     *
     * @param seasonId the ID of the season to delete
     * @throws SQLException if a database error occurs
     */
    public void delete(Long seasonId) throws SQLException {
        String query = "DELETE FROM seasons WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, seasonId);
            pst.executeUpdate();
            LOGGER.info("Season deleted successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting season", e);
            throw e;
        }
    }

    /**
     * Gets a season by ID.
     *
     * @param seasonId the ID of the season
     * @return the Season or null if not found
     */
    public Season getById(Long seasonId) {
        String query = "SELECT * FROM seasons WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, seasonId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToSeason(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting season by id", e);
        }
        return null;
    }

    /**
     * Gets all seasons for a series.
     *
     * @param seriesId the ID of the series
     * @return list of seasons
     */
    public List<Season> getSeasonsBySeriesId(Long seriesId) {
        List<Season> seasons = new ArrayList<>();
        String query = "SELECT * FROM seasons WHERE series_id = ? ORDER BY season_number ASC";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, seriesId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                seasons.add(mapResultSetToSeason(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting seasons by series id", e);
        }

        return seasons;
    }

    /**
     * Alias for getSeasonsBySeriesId.
     * @param seriesId the ID of the series
     * @return list of seasons
     */
    public List<Season> getSeasonsBySeries(Long seriesId) {
        return getSeasonsBySeriesId(seriesId);
    }

    /**
     * Gets all seasons.
     *
     * @return list of all seasons
     */
    public List<Season> getAll() {
        List<Season> seasons = new ArrayList<>();
        String query = "SELECT * FROM seasons ORDER BY series_id, season_number";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                seasons.add(mapResultSetToSeason(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all seasons", e);
        }

        return seasons;
    }

    /**
     * Gets the count of seasons for a series.
     *
     * @param seriesId the ID of the series
     * @return the number of seasons
     */
    public int getSeasonCount(Long seriesId) {
        String query = "SELECT COUNT(*) FROM seasons WHERE series_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, seriesId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting seasons", e);
        }
        return 0;
    }

    /**
     * Gets a season by series ID and season number.
     *
     * @param seriesId     the ID of the series
     * @param seasonNumber the season number
     * @return the Season or null if not found
     */
    public Season getSeasonByNumber(Long seriesId, int seasonNumber) {
        String query = "SELECT * FROM seasons WHERE series_id = ? AND season_number = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, seriesId);
            pst.setInt(2, seasonNumber);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToSeason(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting season by number", e);
        }
        return null;
    }

    /**
     * Gets all episodes for a season.
     *
     * @param seasonId the ID of the season
     * @return list of episodes
     */
    public List<Episode> getEpisodesBySeason(Long seasonId) {
        List<Episode> episodes = new ArrayList<>();
        String query = "SELECT * FROM episodes WHERE season_id = ? ORDER BY episode_number ASC";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, seasonId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                episodes.add(mapResultSetToEpisode(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting episodes by season", e);
        }

        return episodes;
    }

    /**
     * Gets the episode count for a season.
     *
     * @param seasonId the ID of the season
     * @return the number of episodes
     */
    public int getEpisodeCount(Long seasonId) {
        String query = "SELECT COUNT(*) FROM episodes WHERE season_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, seasonId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting episodes", e);
        }
        return 0;
    }

    /**
     * Gets the total duration of all episodes in a season.
     *
     * @param seasonId the ID of the season
     * @return total duration in minutes
     */
    public int getTotalDuration(Long seasonId) {
        String query = "SELECT COALESCE(SUM(duration), 0) FROM episodes WHERE season_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, seasonId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating total duration", e);
        }
        return 0;
    }

    /**
     * Checks if a season exists for a series.
     *
     * @param seriesId     the ID of the series
     * @param seasonNumber the season number
     * @return true if the season exists
     */
    public boolean seasonExists(Long seriesId, int seasonNumber) {
        String query = "SELECT id FROM seasons WHERE series_id = ? AND season_number = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, seriesId);
            pst.setInt(2, seasonNumber);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking season existence", e);
            return false;
        }
    }

    /**
     * Maps a ResultSet row to a Season object.
     *
     * @param rs the ResultSet to map
     * @return the Season object
     * @throws SQLException if a database error occurs
     */
    private Season mapResultSetToSeason(ResultSet rs) throws SQLException {
        return Season.builder()
            .id(rs.getLong("id"))
            .seriesId(rs.getLong("series_id"))
            .seasonNumber(rs.getInt("season_number"))
            .title(rs.getString("title"))
            .build();
    }

    /**
     * Maps a ResultSet row to an Episode object.
     *
     * @param rs the ResultSet to map
     * @return the Episode object
     * @throws SQLException if a database error occurs
     */
    private Episode mapResultSetToEpisode(ResultSet rs) throws SQLException {
        return Episode.builder()
            .id(rs.getLong("id"))
            .seasonId(rs.getLong("season_id"))
            .episodeNumber(rs.getInt("episode_number"))
            .title(rs.getString("title"))
            .imageUrl(rs.getString("image_url"))
            .videoUrl(rs.getString("video_url"))
            .durationMin(rs.getInt("duration_min"))
            .releaseDate(rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null)
            .build();
    }
}
