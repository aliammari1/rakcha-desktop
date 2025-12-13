package com.esprit.services.users;

import com.esprit.models.users.Message;
import com.esprit.models.users.User;
import com.esprit.utils.DataSource;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing direct messages between users.
 * Handles sending, receiving, and managing message conversations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Log4j2
public class MessageService {

    private static final Logger LOGGER = Logger.getLogger(MessageService.class.getName());
    private final Connection connection;
    private final UserService userService;

    /**
     * Constructs a new MessageService instance.
     */
    public MessageService() {
        this.connection = DataSource.getInstance().getConnection();
        this.userService = new UserService();
    }

    /**
     * Sends a message from one user to another.
     *
     * @param senderId    the ID of the sender
     * @param recipientId the ID of the recipient
     * @param content     the message content
     * @return the created Message
     * @throws SQLException if a database error occurs
     */
    public Message sendMessage(Long senderId, Long recipientId, String content) throws SQLException {
        String query = "INSERT INTO messages (sender_id, recipient_id, content, created_at, is_read) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(now);
            pst.setLong(1, senderId);
            pst.setLong(2, recipientId);
            pst.setString(3, content);
            pst.setTimestamp(4, timestamp);
            pst.setBoolean(5, false);
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                Message message = Message.builder()
                    .id(rs.getLong(1))
                    .senderId(senderId)
                    .recipientId(recipientId)
                    .content(content)
                    .createdAt(now)
                    .read(false)
                    .build();
                LOGGER.info("Message sent successfully");
                return message;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error sending message", e);
            throw e;
        }
        return null;
    }

    /**
     * Gets all messages between two users (a conversation).
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     * @return list of messages between the two users
     */
    public List<Message> getMessages(Long userId1, Long userId2) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE " +
            "(sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?) " +
            "ORDER BY created_at ASC";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId1);
            pst.setLong(2, userId2);
            pst.setLong(3, userId2);
            pst.setLong(4, userId1);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving messages", e);
        }

        return messages;
    }

    /**
     * Gets all conversations for a user.
     * Returns a list of users the current user has exchanged messages with.
     *
     * @param userId the ID of the user
     * @return list of users in conversations
     */
    public List<User> getConversations(Long userId) {
        List<User> conversations = new ArrayList<>();
        Map<Long, User> userMap = new HashMap<>();

        String query = "SELECT DISTINCT CASE WHEN sender_id = ? THEN recipient_id ELSE sender_id END as other_user_id " +
            "FROM messages WHERE sender_id = ? OR recipient_id = ? " +
            "ORDER BY (SELECT MAX(created_at) FROM messages m2 " +
            "WHERE (m2.sender_id = ? AND m2.recipient_id = other_user_id) " +
            "OR (m2.sender_id = other_user_id AND m2.recipient_id = ?)) DESC";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, userId);
            pst.setLong(3, userId);
            pst.setLong(4, userId);
            pst.setLong(5, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Long otherUserId = rs.getLong("other_user_id");
                if (!userMap.containsKey(otherUserId)) {
                    User user = userService.getUserById(otherUserId);
                    if (user != null) {
                        userMap.put(otherUserId, user);
                        conversations.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving conversations", e);
        }

        return conversations;
    }

    /**
     * Marks all messages from a sender to a recipient as read.
     *
     * @param senderId    the ID of the sender
     * @param recipientId the ID of the recipient (current user)
     * @throws SQLException if a database error occurs
     */
    public void markAsRead(Long senderId, Long recipientId) throws SQLException {
        String query = "UPDATE messages SET is_read = true WHERE sender_id = ? AND recipient_id = ? AND is_read = false";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, senderId);
            pst.setLong(2, recipientId);
            pst.executeUpdate();
            LOGGER.info("Messages marked as read");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking messages as read", e);
            throw e;
        }
    }

    /**
     * Marks a specific message as read.
     *
     * @param messageId the ID of the message
     * @throws SQLException if a database error occurs
     */
    public void markMessageAsRead(Long messageId) throws SQLException {
        String query = "UPDATE messages SET is_read = true WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, messageId);
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking message as read", e);
            throw e;
        }
    }

    /**
     * Deletes a message.
     *
     * @param messageId the ID of the message to delete
     * @throws SQLException if a database error occurs
     */
    public void deleteMessage(Long messageId) throws SQLException {
        String query = "DELETE FROM messages WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, messageId);
            pst.executeUpdate();
            LOGGER.info("Message deleted successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting message", e);
            throw e;
        }
    }

    /**
     * Deletes an entire conversation between two users.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     * @throws SQLException if a database error occurs
     */
    public void deleteConversation(Long userId1, Long userId2) throws SQLException {
        String query = "DELETE FROM messages WHERE " +
            "(sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId1);
            pst.setLong(2, userId2);
            pst.setLong(3, userId2);
            pst.setLong(4, userId1);
            pst.executeUpdate();
            LOGGER.info("Conversation deleted successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting conversation", e);
            throw e;
        }
    }

    /**
     * Gets the count of unread messages for a user.
     *
     * @param userId the ID of the user
     * @return the number of unread messages
     */
    public int getUnreadCount(Long userId) {
        String query = "SELECT COUNT(*) FROM messages WHERE recipient_id = ? AND is_read = false";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting unread messages", e);
        }
        return 0;
    }

    /**
     * Gets the count of unread messages from a specific sender.
     *
     * @param senderId    the ID of the sender
     * @param recipientId the ID of the recipient
     * @return the number of unread messages
     */
    public int getUnreadCountFrom(Long senderId, Long recipientId) {
        String query = "SELECT COUNT(*) FROM messages WHERE sender_id = ? AND recipient_id = ? AND is_read = false";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, senderId);
            pst.setLong(2, recipientId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting unread messages from sender", e);
        }
        return 0;
    }

    /**
     * Gets the last message in a conversation.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     * @return the last message or null
     */
    public Message getLastMessage(Long userId1, Long userId2) {
        String query = "SELECT * FROM messages WHERE " +
            "(sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?) " +
            "ORDER BY created_at DESC LIMIT 1";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId1);
            pst.setLong(2, userId2);
            pst.setLong(3, userId2);
            pst.setLong(4, userId1);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return mapResultSetToMessage(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving last message", e);
        }

        return null;
    }

    /**
     * Maps a ResultSet row to a Message object.
     *
     * @param rs the ResultSet to map
     * @return the Message object
     * @throws SQLException if a database error occurs
     */
    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        java.sql.Timestamp timestamp = rs.getTimestamp("created_at");
        return Message.builder()
            .id(rs.getLong("id"))
            .senderId(rs.getLong("sender_id"))
            .recipientId(rs.getLong("recipient_id"))
            .content(rs.getString("content"))
            .createdAt(timestamp != null ? timestamp.toLocalDateTime() : java.time.LocalDateTime.now())
            .read(rs.getBoolean("is_read"))
            .build();
    }
}
