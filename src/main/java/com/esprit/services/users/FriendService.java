package com.esprit.services.users;

import com.esprit.models.users.Friendship;
import com.esprit.models.users.User;
import com.esprit.utils.DataSource;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing user friendships.
 * Handles friend requests, friend lists, blocking, and suggestions.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Log4j2
public class FriendService {

    // Friendship status constants
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_BLOCKED = "BLOCKED";
    private static final Logger LOGGER = Logger.getLogger(FriendService.class.getName());
    private final Connection connection;
    private final UserService userService;

    /**
     * Constructs a new FriendService instance.
     */
    public FriendService() {
        this.connection = DataSource.getInstance().getConnection();
        this.userService = new UserService();
    }

    /**
     * Sends a friend request from one user to another.
     *
     * @param requesterId the ID of the user sending the request
     * @param addresseeId the ID of the user receiving the request
     * @throws SQLException if a database error occurs
     */
    public void sendFriendRequest(Long requesterId, Long addresseeId) throws SQLException {
        // Check if a friendship already exists
        if (friendshipExists(requesterId, addresseeId)) {
            LOGGER.info("Friendship already exists between these users");
            return;
        }

        String query = "INSERT INTO friendships (requester_id, addressee_id, status, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, requesterId);
            pst.setLong(2, addresseeId);
            pst.setString(3, STATUS_PENDING);
            pst.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pst.executeUpdate();
            LOGGER.info("Friend request sent successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error sending friend request", e);
            throw e;
        }
    }

    /**
     * Accepts a pending friend request.
     *
     * @param friendshipId the ID of the friendship to accept
     * @throws SQLException if a database error occurs
     */
    public void acceptFriendRequest(Long friendshipId) throws SQLException {
        updateFriendshipStatus(friendshipId, STATUS_ACCEPTED);
        LOGGER.info("Friend request accepted");
    }

    /**
     * Declines a pending friend request.
     *
     * @param friendshipId the ID of the friendship to decline
     * @throws SQLException if a database error occurs
     */
    public void declineFriendRequest(Long friendshipId) throws SQLException {
        updateFriendshipStatus(friendshipId, STATUS_REJECTED);
        LOGGER.info("Friend request declined");
    }

    /**
     * Accept a friend request (overloaded version with userId and friendId).
     *
     * @param userId   the user ID (receiver of the request)
     * @param friendId the friend ID (sender of the request)
     * @throws SQLException if a database error occurs
     */
    public void acceptFriendRequest(Long userId, Long friendId) throws SQLException {
        Long friendshipId = getFriendshipId(friendId, userId);
        if (friendshipId != null) {
            acceptFriendRequest(friendshipId);
        }
    }

    /**
     * Decline a friend request (overloaded version with userId and friendId).
     *
     * @param userId   the user ID (receiver of the request)
     * @param friendId the friend ID (sender of the request)
     * @throws SQLException if a database error occurs
     */
    public void declineFriendRequest(Long userId, Long friendId) throws SQLException {
        Long friendshipId = getFriendshipId(friendId, userId);
        if (friendshipId != null) {
            declineFriendRequest(friendshipId);
        }
    }

    /**
     * Blocks a user.
     *
     * @param userId        the ID of the user doing the blocking
     * @param blockedUserId the ID of the user being blocked
     * @throws SQLException if a database error occurs
     */
    public void blockUser(Long userId, Long blockedUserId) throws SQLException {
        // Check if friendship exists
        Long friendshipId = getFriendshipId(userId, blockedUserId);
        if (friendshipId != null) {
            updateFriendshipStatus(friendshipId, STATUS_BLOCKED);
        } else {
            // Create a new blocked relationship
            String query = "INSERT INTO friendships (requester_id, addressee_id, status, created_at) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setLong(1, userId);
                pst.setLong(2, blockedUserId);
                pst.setString(3, STATUS_BLOCKED);
                pst.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                pst.executeUpdate();
            }
        }
        LOGGER.info("User blocked successfully");
    }

    /**
     * Unfriends a user (removes the friendship).
     *
     * @param userId   the ID of the current user
     * @param friendId the ID of the friend to remove
     * @throws SQLException if a database error occurs
     */
    public void unfriend(Long userId, Long friendId) throws SQLException {
        String query = "DELETE FROM friendships WHERE " +
            "((requester_id = ? AND addressee_id = ?) OR (requester_id = ? AND addressee_id = ?)) " +
            "AND status = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, friendId);
            pst.setLong(3, friendId);
            pst.setLong(4, userId);
            pst.setString(5, STATUS_ACCEPTED);
            pst.executeUpdate();
            LOGGER.info("Friend removed successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing friend", e);
            throw e;
        }
    }

    /**
     * Gets all friends of a user.
     *
     * @param userId the ID of the user
     * @return list of friends
     */
    public List<User> getFriends(Long userId) {
        List<User> friends = new ArrayList<>();
        String query = "SELECT CASE WHEN requester_id = ? THEN addressee_id ELSE requester_id END as friend_id " +
            "FROM friendships WHERE (requester_id = ? OR addressee_id = ?) AND status = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, userId);
            pst.setLong(3, userId);
            pst.setString(4, STATUS_ACCEPTED);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Long friendId = rs.getLong("friend_id");
                User friend = userService.getUserById(friendId);
                if (friend != null) {
                    friends.add(friend);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving friends", e);
        }

        return friends;
    }

    /**
     * Gets pending friend requests for a user.
     *
     * @param userId the ID of the user
     * @return list of users who sent pending requests
     */
    public List<User> getPendingRequests(Long userId) {
        List<User> pendingRequests = new ArrayList<>();
        String query = "SELECT requester_id FROM friendships WHERE addressee_id = ? AND status = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setString(2, STATUS_PENDING);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Long requesterId = rs.getLong("requester_id");
                User requester = userService.getUserById(requesterId);
                if (requester != null) {
                    pendingRequests.add(requester);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving pending requests", e);
        }

        return pendingRequests;
    }

    /**
     * Gets sent friend requests that are still pending.
     *
     * @param userId the ID of the user
     * @return list of users to whom requests were sent
     */
    public List<User> getSentRequests(Long userId) {
        List<User> sentRequests = new ArrayList<>();
        String query = "SELECT addressee_id FROM friendships WHERE requester_id = ? AND status = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setString(2, STATUS_PENDING);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Long addresseeId = rs.getLong("addressee_id");
                User addressee = userService.getUserById(addresseeId);
                if (addressee != null) {
                    sentRequests.add(addressee);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving sent requests", e);
        }

        return sentRequests;
    }

    /**
     * Gets suggested friends for a user based on mutual friends and activity.
     *
     * @param userId the ID of the user
     * @return list of suggested users to befriend
     */
    public List<User> getSuggestedFriends(Long userId) {
        List<User> suggestions = new ArrayList<>();
        // Get friends of friends who are not already friends
        String query = "SELECT DISTINCT CASE WHEN f2.requester_id = f1_friend THEN f2.addressee_id ELSE f2.requester_id END as suggested_id " +
            "FROM (SELECT CASE WHEN requester_id = ? THEN addressee_id ELSE requester_id END as f1_friend " +
            "FROM friendships WHERE (requester_id = ? OR addressee_id = ?) AND status = ?) as friends_subq " +
            "JOIN friendships f2 ON (f2.requester_id = f1_friend OR f2.addressee_id = f1_friend) AND f2.status = ? " +
            "WHERE CASE WHEN f2.requester_id = f1_friend THEN f2.addressee_id ELSE f2.requester_id END != ? " +
            "AND CASE WHEN f2.requester_id = f1_friend THEN f2.addressee_id ELSE f2.requester_id END NOT IN " +
            "(SELECT CASE WHEN requester_id = ? THEN addressee_id ELSE requester_id END FROM friendships " +
            "WHERE (requester_id = ? OR addressee_id = ?)) " +
            "LIMIT 10";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, userId);
            pst.setLong(3, userId);
            pst.setString(4, STATUS_ACCEPTED);
            pst.setString(5, STATUS_ACCEPTED);
            pst.setLong(6, userId);
            pst.setLong(7, userId);
            pst.setLong(8, userId);
            pst.setLong(9, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Long suggestedId = rs.getLong("suggested_id");
                User suggested = userService.getUserById(suggestedId);
                if (suggested != null) {
                    suggestions.add(suggested);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving suggested friends", e);
        }

        return suggestions;
    }

    /**
     * Gets the count of friends for a user.
     *
     * @param userId the ID of the user
     * @return the number of friends
     */
    public int getFriendsCount(Long userId) {
        String query = "SELECT COUNT(*) FROM friendships WHERE (requester_id = ? OR addressee_id = ?) AND status = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, userId);
            pst.setString(3, STATUS_ACCEPTED);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting friends", e);
        }
        return 0;
    }

    /**
     * Checks if two users are friends.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     * @return true if they are friends
     */
    public boolean areFriends(Long userId1, Long userId2) {
        String query = "SELECT id FROM friendships WHERE " +
            "((requester_id = ? AND addressee_id = ?) OR (requester_id = ? AND addressee_id = ?)) " +
            "AND status = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId1);
            pst.setLong(2, userId2);
            pst.setLong(3, userId2);
            pst.setLong(4, userId1);
            pst.setString(5, STATUS_ACCEPTED);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking friendship", e);
            return false;
        }
    }

    /**
     * Gets the friendship between two users if it exists.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     * @return the Friendship object or null if not found
     */
    public Friendship getFriendship(Long userId1, Long userId2) {
        String query = "SELECT * FROM friendships WHERE " +
            "(requester_id = ? AND addressee_id = ?) OR (requester_id = ? AND addressee_id = ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId1);
            pst.setLong(2, userId2);
            pst.setLong(3, userId2);
            pst.setLong(4, userId1);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToFriendship(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting friendship", e);
        }
        return null;
    }

    // Private helper methods

    private boolean friendshipExists(Long userId1, Long userId2) {
        String query = "SELECT id FROM friendships WHERE " +
            "(requester_id = ? AND addressee_id = ?) OR (requester_id = ? AND addressee_id = ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId1);
            pst.setLong(2, userId2);
            pst.setLong(3, userId2);
            pst.setLong(4, userId1);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking friendship existence", e);
            return false;
        }
    }

    private Long getFriendshipId(Long userId1, Long userId2) {
        String query = "SELECT id FROM friendships WHERE " +
            "(requester_id = ? AND addressee_id = ?) OR (requester_id = ? AND addressee_id = ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId1);
            pst.setLong(2, userId2);
            pst.setLong(3, userId2);
            pst.setLong(4, userId1);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting friendship id", e);
        }
        return null;
    }

    private void updateFriendshipStatus(Long friendshipId, String status) throws SQLException {
        String query = "UPDATE friendships SET status = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, status);
            pst.setLong(2, friendshipId);
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating friendship status", e);
            throw e;
        }
    }

    private Friendship mapResultSetToFriendship(ResultSet rs) throws SQLException {
        return Friendship.builder()
            .id(rs.getLong("id"))
            .requesterId(rs.getLong("requester_id"))
            .addresseeId(rs.getLong("addressee_id"))
            .status(rs.getString("status"))
            .createdAt(rs.getTimestamp("created_at"))
            .build();
    }
}
