package com.esprit.services.users;

import com.esprit.models.films.Film;
import com.esprit.services.films.FilmService;
import com.esprit.utils.DataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing user watchlists.
 * Handles adding, removing, and retrieving films from a user's watchlist.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class WatchlistService {

    private static final Logger LOGGER = Logger.getLogger(WatchlistService.class.getName());
    private final Connection connection;
    private final FilmService filmService;

    /**
     * Constructs a new WatchlistService instance.
     */
    public WatchlistService() {
        this.connection = DataSource.getInstance().getConnection();
        this.filmService = new FilmService();
    }

    /**
     * Adds a film to the user's watchlist.
     *
     * @param userId the ID of the user
     * @param filmId the ID of the film to add
     * @throws SQLException if a database error occurs
     */
    public void addToWatchlist(Long userId, Long filmId) throws SQLException {
        String checkQuery = "SELECT id FROM user_watchlist WHERE user_id = ? AND film_id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setLong(1, userId);
            checkStmt.setLong(2, filmId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                LOGGER.info("Film already in watchlist");
                return;
            }
        }

        String insertQuery = "INSERT INTO user_watchlist (user_id, film_id, added_at, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(insertQuery)) {
            pst.setLong(1, userId);
            pst.setLong(2, filmId);
            pst.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pst.setString(4, "NOT_STARTED");
            pst.executeUpdate();
            LOGGER.info("Film added to watchlist successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding film to watchlist", e);
            throw e;
        }
    }

    /**
     * Removes a film from the user's watchlist.
     *
     * @param userId the ID of the user
     * @param filmId the ID of the film to remove
     * @throws SQLException if a database error occurs
     */
    public void removeFromWatchlist(Long userId, Long filmId) throws SQLException {
        String query = "DELETE FROM user_watchlist WHERE user_id = ? AND film_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, filmId);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Film removed from watchlist successfully");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing film from watchlist", e);
            throw e;
        }
    }

    /**
     * Retrieves all films in a user's watchlist.
     *
     * @param userId the ID of the user
     * @return list of films in the user's watchlist
     */
    public List<Film> getWatchlistByUserId(Long userId) {
        List<Film> watchlist = new ArrayList<>();
        String query = "SELECT film_id FROM user_watchlist WHERE user_id = ? ORDER BY added_at DESC";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Film film = filmService.getFilm(filmId);
                if (film != null) {
                    watchlist.add(film);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving watchlist", e);
        }

        return watchlist;
    }

    /**
     * Checks if a film is in the user's watchlist.
     *
     * @param userId the ID of the user
     * @param filmId the ID of the film
     * @return true if the film is in the watchlist
     */
    public boolean isInWatchlist(Long userId, Long filmId) {
        String query = "SELECT id FROM user_watchlist WHERE user_id = ? AND film_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, filmId);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking watchlist", e);
            return false;
        }
    }

    /**
     * Clears all films from a user's watchlist.
     *
     * @param userId the ID of the user
     * @throws SQLException if a database error occurs
     */
    public void clearWatchlist(Long userId) throws SQLException {
        String query = "DELETE FROM user_watchlist WHERE user_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.executeUpdate();
            LOGGER.info("Watchlist cleared successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error clearing watchlist", e);
            throw e;
        }
    }

    /**
     * Gets the count of films in the user's watchlist.
     *
     * @param userId the ID of the user
     * @return the number of films in the watchlist
     */
    public int getWatchlistCount(Long userId) {
        String query = "SELECT COUNT(*) FROM user_watchlist WHERE user_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting watchlist", e);
        }
        return 0;
    }

    /**
     * Updates the watch status of a film in the watchlist.
     *
     * @param userId the ID of the user
     * @param filmId the ID of the film
     * @param status the new status (NOT_STARTED, IN_PROGRESS, COMPLETED)
     */
    public void updateWatchStatus(Long userId, Long filmId, String status) {
        String query = "UPDATE user_watchlist SET status = ? WHERE user_id = ? AND film_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, status);
            pst.setLong(2, userId);
            pst.setLong(3, filmId);
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating watch status", e);
        }
    }
}
