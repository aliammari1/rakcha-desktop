package com.esprit.services.users;

import com.esprit.models.users.Achievement;
import com.esprit.models.users.Activity;
import com.esprit.models.users.WatchProgress;
import com.esprit.utils.DataSource;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing user watch progress and statistics.
 * Handles tracking watch time, progress, achievements, and activity.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Log4j2
public class WatchProgressService {

    private static final Logger LOGGER = Logger.getLogger(WatchProgressService.class.getName());
    private final Connection connection;

    /**
     * Constructs a new WatchProgressService instance.
     */
    public WatchProgressService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    // ==================== Watch Progress CRUD ====================

    /**
     * Saves or updates watch progress for a user.
     *
     * @param progress the WatchProgress to save
     * @throws SQLException if a database error occurs
     */
    public void saveProgress(WatchProgress progress) throws SQLException {
        String checkQuery = "SELECT id FROM watch_progress WHERE user_id = ? AND " +
            "(series_id = ? OR film_id = ?)";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setLong(1, progress.getUserId());
            checkStmt.setObject(2, progress.getSeriesId());
            checkStmt.setObject(3, progress.getFilmId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Update existing
                updateProgress(progress);
            } else {
                // Insert new
                insertProgress(progress);
            }
        }
    }

    private void insertProgress(WatchProgress progress) throws SQLException {
        String query = "INSERT INTO watch_progress (user_id, series_id, film_id, progress, watched_episodes, " +
            "total_episodes, last_watched_episode, last_watched_at, user_rating, watched_minutes) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setLong(1, progress.getUserId());
            pst.setObject(2, progress.getSeriesId());
            pst.setObject(3, progress.getFilmId());
            pst.setDouble(4, progress.getProgress());
            pst.setInt(5, progress.getWatchedEpisodes());
            pst.setInt(6, progress.getTotalEpisodes());
            pst.setInt(7, progress.getLastWatchedEpisodeNum());
            pst.setTimestamp(8, progress.getLastWatchedAt() != null ?
                Timestamp.valueOf(progress.getLastWatchedAt()) : Timestamp.valueOf(LocalDateTime.now()));
            pst.setObject(9, progress.getUserRating());
            pst.setInt(10, progress.getWatchedMinutes());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                progress.setId(rs.getLong(1));
            }
        }
    }

    private void updateProgress(WatchProgress progress) throws SQLException {
        String query = "UPDATE watch_progress SET progress = ?, watched_episodes = ?, " +
            "last_watched_episode = ?, last_watched_at = ?, user_rating = ?, watched_minutes = ? " +
            "WHERE user_id = ? AND (series_id = ? OR film_id = ?)";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setDouble(1, progress.getProgress());
            pst.setInt(2, progress.getWatchedEpisodes());
            pst.setInt(3, progress.getLastWatchedEpisodeNum());
            pst.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pst.setObject(5, progress.getUserRating());
            pst.setInt(6, progress.getWatchedMinutes());
            pst.setLong(7, progress.getUserId());
            pst.setObject(8, progress.getSeriesId());
            pst.setObject(9, progress.getFilmId());
            pst.executeUpdate();
        }
    }

    /**
     * Gets watch progress for a user and series.
     *
     * @param userId   the ID of the user
     * @param seriesId the ID of the series
     * @return the WatchProgress or null
     */
    public WatchProgress getUserSeriesProgress(Long userId, Long seriesId) {
        String query = "SELECT * FROM watch_progress WHERE user_id = ? AND series_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, seriesId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToWatchProgress(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user series progress", e);
        }
        return null;
    }

    /**
     * Gets all watch progress entries for a user.
     *
     * @param userId the ID of the user
     * @return list of WatchProgress entries
     */
    public List<WatchProgress> getAllUserProgress(Long userId) {
        List<WatchProgress> progressList = new ArrayList<>();
        String query = "SELECT * FROM watch_progress WHERE user_id = ? ORDER BY last_watched_at DESC";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                progressList.add(mapResultSetToWatchProgress(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all user progress", e);
        }
        return progressList;
    }

    // ==================== Statistics Methods ====================

    /**
     * Gets the total watch time for a user in minutes.
     *
     * @param userId the ID of the user
     * @return total watch time in minutes
     */
    public int getTotalWatchTime(Long userId) {
        String query = "SELECT COALESCE(SUM(watched_minutes), 0) as total FROM watch_progress WHERE user_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total watch time", e);
        }
        return 0;
    }

    /**
     * Gets the count of films watched by a user.
     *
     * @param userId the ID of the user
     * @return count of films watched
     */
    public int getFilmsWatchedCount(Long userId) {
        String query = "SELECT COUNT(*) FROM watch_progress WHERE user_id = ? AND film_id IS NOT NULL AND progress >= 0.9";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting films watched count", e);
        }
        return 0;
    }

    /**
     * Gets the count of series watched by a user.
     *
     * @param userId the ID of the user
     * @return count of series watched
     */
    public int getSeriesWatchedCount(Long userId) {
        String query = "SELECT COUNT(*) FROM watch_progress WHERE user_id = ? AND series_id IS NOT NULL AND progress >= 0.9";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting series watched count", e);
        }
        return 0;
    }

    /**
     * Gets the current watch streak for a user (consecutive days watched).
     *
     * @param userId the ID of the user
     * @return the streak count in days
     */
    public int getCurrentStreak(Long userId) {
        String query = "SELECT DISTINCT DATE(last_watched_at) as watch_date FROM watch_progress " +
            "WHERE user_id = ? ORDER BY watch_date DESC";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();

            int streak = 0;
            LocalDate previousDate = LocalDate.now().plusDays(1); // Start from tomorrow to include today

            while (rs.next()) {
                LocalDate watchDate = rs.getDate("watch_date").toLocalDate();
                if (previousDate.minusDays(1).equals(watchDate) || previousDate.equals(watchDate.plusDays(1))) {
                    streak++;
                    previousDate = watchDate;
                } else if (streak == 0 && (watchDate.equals(LocalDate.now()) || watchDate.equals(LocalDate.now().minusDays(1)))) {
                    streak = 1;
                    previousDate = watchDate;
                } else {
                    break;
                }
            }
            return streak;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating streak", e);
        }
        return 0;
    }

    /**
     * Gets the genre distribution for a user's watched content.
     *
     * @param userId the ID of the user
     * @return map of genre names to watch counts
     */
    public Map<String, Integer> getGenreDistribution(Long userId) {
        Map<String, Integer> distribution = new HashMap<>();

        // Get from films
        String filmQuery = "SELECT g.name, COUNT(*) as count FROM watch_progress wp " +
            "JOIN films f ON wp.film_id = f.id " +
            "JOIN film_genres fg ON f.id = fg.film_id " +
            "JOIN genres g ON fg.genre_id = g.id " +
            "WHERE wp.user_id = ? AND wp.film_id IS NOT NULL " +
            "GROUP BY g.name";

        try (PreparedStatement pst = connection.prepareStatement(filmQuery)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String genre = rs.getString("name");
                int count = rs.getInt("count");
                distribution.merge(genre, count, Integer::sum);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting genre distribution", e);
        }

        // Get from series
        String seriesQuery = "SELECT g.name, COUNT(*) as count FROM watch_progress wp " +
            "JOIN series s ON wp.series_id = s.id " +
            "JOIN series_genres sg ON s.id = sg.series_id " +
            "JOIN genres g ON sg.genre_id = g.id " +
            "WHERE wp.user_id = ? AND wp.series_id IS NOT NULL " +
            "GROUP BY g.name";

        try (PreparedStatement pst = connection.prepareStatement(seriesQuery)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String genre = rs.getString("name");
                int count = rs.getInt("count");
                distribution.merge(genre, count, Integer::sum);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting series genre distribution", e);
        }

        return distribution;
    }

    /**
     * Gets the monthly watch time for a user.
     *
     * @param userId the ID of the user
     * @return map of month strings to watch minutes
     */
    public Map<String, Integer> getMonthlyWatchTime(Long userId) {
        Map<String, Integer> monthlyTime = new HashMap<>();
        String query = "SELECT DATE_FORMAT(last_watched_at, '%Y-%m') as month, SUM(watched_minutes) as total " +
            "FROM watch_progress WHERE user_id = ? " +
            "GROUP BY DATE_FORMAT(last_watched_at, '%Y-%m') " +
            "ORDER BY month DESC LIMIT 12";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                monthlyTime.put(rs.getString("month"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting monthly watch time", e);
        }

        return monthlyTime;
    }

    /**
     * Gets the daily activity for a user (last 30 days).
     *
     * @param userId the ID of the user
     * @return map of date strings to watch counts
     */
    public Map<String, Integer> getDailyActivity(Long userId) {
        Map<String, Integer> dailyActivity = new HashMap<>();
        String query = "SELECT DATE(last_watched_at) as day, COUNT(*) as count " +
            "FROM watch_progress WHERE user_id = ? AND last_watched_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
            "GROUP BY DATE(last_watched_at) ORDER BY day";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                dailyActivity.put(rs.getDate("day").toString(), rs.getInt("count"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting daily activity", e);
        }

        return dailyActivity;
    }

    // ==================== Activity Methods ====================

    /**
     * Gets recent activity for a user.
     *
     * @param userId the ID of the user
     * @param limit  maximum number of activities to return
     * @return list of recent activities
     */
    public List<Activity> getRecentActivity(Long userId, int limit) {
        List<Activity> activities = new ArrayList<>();
        String query = "SELECT * FROM user_activities WHERE user_id = ? ORDER BY timestamp DESC LIMIT ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setInt(2, limit);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting recent activity", e);
        }

        return activities;
    }

    /**
     * Logs a user activity.
     *
     * @param activity the Activity to log
     * @throws SQLException if a database error occurs
     */
    public void logActivity(Activity activity) throws SQLException {
        String query = "INSERT INTO user_activities (user_id, type, description, timestamp, related_entity_id, related_entity_type) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setLong(1, activity.getUserId());
            pst.setString(2, activity.getType());
            pst.setString(3, activity.getDescription());
            pst.setTimestamp(4, activity.getTimestamp() != null ?
                Timestamp.valueOf(activity.getTimestamp()) : Timestamp.valueOf(LocalDateTime.now()));
            pst.setObject(5, activity.getRelatedEntityId());
            pst.setString(6, activity.getRelatedEntityType());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                activity.setId(rs.getLong(1));
            }
        }
    }

    // ==================== Achievements Methods ====================

    /**
     * Gets all achievements for a user.
     *
     * @param userId the ID of the user
     * @return list of achievements
     */
    public List<Achievement> getUserAchievements(Long userId) {
        List<Achievement> achievements = new ArrayList<>();
        String query = "SELECT a.*, ua.progress, ua.is_unlocked, ua.unlocked_at " +
            "FROM achievements a " +
            "LEFT JOIN user_achievements ua ON a.id = ua.achievement_id AND ua.user_id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                achievements.add(mapResultSetToAchievement(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user achievements", e);
        }

        return achievements;
    }

    /**
     * Updates achievement progress for a user.
     *
     * @param userId        the ID of the user
     * @param achievementId the ID of the achievement
     * @param progress      the new progress value (0.0 to 1.0)
     * @throws SQLException if a database error occurs
     */
    public void updateAchievementProgress(Long userId, Long achievementId, double progress) throws SQLException {
        boolean isUnlocked = progress >= 1.0;

        String query = "INSERT INTO user_achievements (user_id, achievement_id, progress, is_unlocked, unlocked_at) " +
            "VALUES (?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE progress = ?, is_unlocked = ?, unlocked_at = CASE WHEN ? AND unlocked_at IS NULL THEN NOW() ELSE unlocked_at END";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, achievementId);
            pst.setDouble(3, progress);
            pst.setBoolean(4, isUnlocked);
            pst.setTimestamp(5, isUnlocked ? Timestamp.valueOf(LocalDateTime.now()) : null);
            pst.setDouble(6, progress);
            pst.setBoolean(7, isUnlocked);
            pst.setBoolean(8, isUnlocked);
            pst.executeUpdate();
        }
    }

    // ==================== Series Specific Methods ====================

    /**
     * Marks a series as fully watched.
     *
     * @param userId   the ID of the user
     * @param seriesId the ID of the series
     * @throws SQLException if a database error occurs
     */
    public void markSeriesAsWatched(Long userId, Long seriesId) throws SQLException {
        String query = "UPDATE watch_progress SET progress = 1.0, watched_episodes = total_episodes, " +
            "last_watched_at = ? WHERE user_id = ? AND series_id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pst.setLong(2, userId);
            pst.setLong(3, seriesId);
            int rows = pst.executeUpdate();

            if (rows == 0) {
                // Create new progress entry
                WatchProgress progress = WatchProgress.builder()
                    .userId(userId)
                    .seriesId(seriesId)
                    .progress(1.0)
                    .lastWatchedAt(LocalDateTime.now())
                    .build();
                insertProgress(progress);
            }
        }
    }

    /**
     * Resets watch progress for a series.
     *
     * @param userId   the ID of the user
     * @param seriesId the ID of the series
     * @throws SQLException if a database error occurs
     */
    public void resetSeriesProgress(Long userId, Long seriesId) throws SQLException {
        String query = "UPDATE watch_progress SET progress = 0, watched_episodes = 0, " +
            "last_watched_episode = 0, user_rating = NULL WHERE user_id = ? AND series_id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, seriesId);
            pst.executeUpdate();
        }
    }

    /**
     * Removes a series from the user's list.
     *
     * @param userId   the ID of the user
     * @param seriesId the ID of the series
     * @throws SQLException if a database error occurs
     */
    public void removeFromList(Long userId, Long seriesId) throws SQLException {
        String query = "DELETE FROM watch_progress WHERE user_id = ? AND series_id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, seriesId);
            pst.executeUpdate();
        }
    }

    /**
     * Rates a series.
     *
     * @param userId   the ID of the user
     * @param seriesId the ID of the series
     * @param rating   the rating (1-10)
     * @throws SQLException if a database error occurs
     */
    public void rateSeries(Long userId, Long seriesId, Integer rating) throws SQLException {
        String query = "UPDATE watch_progress SET user_rating = ? WHERE user_id = ? AND series_id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, rating);
            pst.setLong(2, userId);
            pst.setLong(3, seriesId);
            int rows = pst.executeUpdate();

            if (rows == 0) {
                // Create new progress entry with rating
                WatchProgress progress = WatchProgress.builder()
                    .userId(userId)
                    .seriesId(seriesId)
                    .userRating(rating)
                    .lastWatchedAt(LocalDateTime.now())
                    .build();
                insertProgress(progress);
            }
        }
    }

    // ==================== Mapping Methods ====================

    private WatchProgress mapResultSetToWatchProgress(ResultSet rs) throws SQLException {
        return WatchProgress.builder()
            .id(rs.getLong("id"))
            .userId(rs.getLong("user_id"))
            .seriesId(rs.getObject("series_id") != null ? rs.getLong("series_id") : null)
            .filmId(rs.getObject("film_id") != null ? rs.getLong("film_id") : null)
            .progress(rs.getDouble("progress"))
            .watchedEpisodes(rs.getInt("watched_episodes"))
            .totalEpisodes(rs.getInt("total_episodes"))
            .lastWatchedEpisodeNum(rs.getInt("last_watched_episode"))
            .lastWatchedAt(rs.getTimestamp("last_watched_at") != null ? rs.getTimestamp("last_watched_at").toLocalDateTime() : null)
            .userRating(rs.getObject("user_rating") != null ? rs.getInt("user_rating") : null)
            .watchedMinutes(rs.getInt("watched_minutes"))
            .build();
    }

    private Activity mapResultSetToActivity(ResultSet rs) throws SQLException {
        return Activity.builder()
            .id(rs.getLong("id"))
            .userId(rs.getLong("user_id"))
            .type(rs.getString("type"))
            .description(rs.getString("description"))
            .timestamp(rs.getTimestamp("timestamp") != null ? rs.getTimestamp("timestamp").toLocalDateTime() : null)
            .referenceId(rs.getObject("related_entity_id") != null ? rs.getLong("related_entity_id") : null)
            .referenceType(rs.getString("related_entity_type"))
            .build();
    }

    private Achievement mapResultSetToAchievement(ResultSet rs) throws SQLException {
        return Achievement.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .icon(rs.getString("icon"))
            .progress(rs.getDouble("progress"))
            .unlocked(rs.getBoolean("is_unlocked"))
            .unlockedAt(rs.getTimestamp("unlocked_at") != null ? rs.getTimestamp("unlocked_at").toLocalDateTime() : null)
            .build();
    }

    /**
     * Get the watch progress for a specific user and content.
     *
     * @param userId      the user ID
     * @param contentType the content type (e.g., "film", "series")
     * @param contentId   the content ID
     * @return the progress percentage (0-100)
     */
    public int getProgress(Long userId, String contentType, Long contentId) {
        String query = "SELECT progress FROM watch_progress WHERE user_id = ? AND content_type = ? AND content_id = ? LIMIT 1";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setString(2, contentType);
            pst.setLong(3, contentId);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("progress");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting progress for content", e);
        }

        return 0;
    }

    /**
     * Get watch progress for a user (returns all their progress).
     *
     * @param userId the user ID
     * @return list of watch progress
     */
    public List<WatchProgress> getUserSeriesProgress(Long userId) {
        List<WatchProgress> progressList = new ArrayList<>();
        String query = "SELECT * FROM watch_progress WHERE user_id = ? ORDER BY last_watched DESC";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                // Build WatchProgress objects
                // This is simplified - would need full mapping
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user progress", e);
        }
        return progressList;
    }

    /**
     * Add a series/film to user's watchlist.
     *
     * @param userId      the user ID
     * @param contentType the type of content (film/series)
     * @param contentId   the ID of the content
     */
    public void addToWatchlist(Long userId, String contentType, Long contentId) {
        String query = "INSERT INTO watch_progress (user_id, film_id, series_id, content_type, progress, last_watched) " +
            "VALUES (?, ?, ?, ?, 0, NOW()) ON DUPLICATE KEY UPDATE last_watched = NOW()";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            if ("film".equals(contentType)) {
                pst.setLong(2, contentId);
                pst.setNull(3, java.sql.Types.BIGINT);
            } else {
                pst.setNull(2, java.sql.Types.BIGINT);
                pst.setLong(3, contentId);
            }
            pst.setString(4, contentType);
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding to watchlist", e);
        }
    }
}

