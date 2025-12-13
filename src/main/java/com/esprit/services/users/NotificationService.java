package com.esprit.services.users;

import com.esprit.models.users.Notification;
import com.esprit.utils.DataSource;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing user notifications.
 * Handles creating, reading, updating, and deleting notifications.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Log4j2
public class NotificationService {

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    private final Connection connection;

    /**
     * Constructs a new NotificationService instance.
     */
    public NotificationService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * Creates a new notification.
     *
     * @param notification the notification to create
     * @throws SQLException if a database error occurs
     */
    public void createNotification(Notification notification) throws SQLException {
        String query = "INSERT INTO notifications (user_id, title, message, is_read, type, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setLong(1, notification.getUserId());
            pst.setString(2, notification.getTitle());
            pst.setString(3, notification.getMessage());
            pst.setBoolean(4, notification.isRead());
            pst.setString(5, notification.getType());
            pst.setTimestamp(6, notification.getCreatedAt() != null ? notification.getCreatedAt() : new Timestamp(System.currentTimeMillis()));
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                notification.setId(rs.getLong(1));
            }
            LOGGER.info("Notification created successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating notification", e);
            throw e;
        }
    }

    /**
     * Retrieves all notifications for a user.
     *
     * @param userId the ID of the user
     * @return list of notifications for the user
     */
    public List<Notification> getNotificationsByUserId(Long userId) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving notifications", e);
        }

        return notifications;
    }

    /**
     * Retrieves unread notifications for a user.
     *
     * @param userId the ID of the user
     * @return list of unread notifications
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notifications WHERE user_id = ? AND is_read = false ORDER BY created_at DESC";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving unread notifications", e);
        }

        return notifications;
    }

    /**
     * Marks a notification as read.
     *
     * @param notificationId the ID of the notification
     * @throws SQLException if a database error occurs
     */
    public void markAsRead(Long notificationId) throws SQLException {
        String query = "UPDATE notifications SET is_read = true WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, notificationId);
            pst.executeUpdate();
            LOGGER.info("Notification marked as read");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking notification as read", e);
            throw e;
        }
    }

    /**
     * Marks all notifications as read for a user.
     *
     * @param userId the ID of the user
     * @throws SQLException if a database error occurs
     */
    public void markAllAsRead(Long userId) throws SQLException {
        String query = "UPDATE notifications SET is_read = true WHERE user_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.executeUpdate();
            LOGGER.info("All notifications marked as read");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking all notifications as read", e);
            throw e;
        }
    }

    /**
     * Deletes a notification.
     *
     * @param notificationId the ID of the notification to delete
     * @throws SQLException if a database error occurs
     */
    public void deleteNotification(Long notificationId) throws SQLException {
        String query = "DELETE FROM notifications WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, notificationId);
            pst.executeUpdate();
            LOGGER.info("Notification deleted successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting notification", e);
            throw e;
        }
    }

    /**
     * Clears all notifications for a user.
     *
     * @param userId the ID of the user
     * @throws SQLException if a database error occurs
     */
    public void clearAllNotifications(Long userId) throws SQLException {
        String query = "DELETE FROM notifications WHERE user_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.executeUpdate();
            LOGGER.info("All notifications cleared");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error clearing notifications", e);
            throw e;
        }
    }

    /**
     * Gets the count of unread notifications for a user.
     *
     * @param userId the ID of the user
     * @return the number of unread notifications
     */
    public int getUnreadCount(Long userId) {
        String query = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = false";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting unread notifications", e);
        }
        return 0;
    }

    /**
     * Sends a notification to a user.
     *
     * @param userId  the ID of the recipient user
     * @param title   the notification title
     * @param message the notification message
     * @param type    the notification type
     */
    public void sendNotification(Long userId, String title, String message, String type) {
        Notification notification = Notification.builder()
            .userId(userId)
            .title(title)
            .message(message)
            .type(type)
            .isRead(false)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .build();
        try {
            createNotification(notification);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error sending notification", e);
        }
    }

    /**
     * Maps a ResultSet row to a Notification object.
     *
     * @param rs the ResultSet to map
     * @return the Notification object
     * @throws SQLException if a database error occurs
     */
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        return Notification.builder()
            .id(rs.getLong("id"))
            .userId(rs.getLong("user_id"))
            .title(rs.getString("title"))
            .message(rs.getString("message"))
            .isRead(rs.getBoolean("is_read"))
            .type(rs.getString("type"))
            .createdAt(rs.getTimestamp("created_at"))
            .build();
    }
}
