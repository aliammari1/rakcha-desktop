package com.esprit.services.users;

import com.esprit.exceptions.PasswordReusedException;
import com.esprit.models.users.PasswordHistory;
import com.esprit.utils.DataSource;
import com.esprit.utils.SecurityConfig;
import org.mindrot.jbcrypt.BCrypt;

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
 * Service class for user security operations including account lockout,
 * password history tracking, and account deactivation.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserSecurityService {

    private static final Logger LOGGER = Logger.getLogger(UserSecurityService.class.getName());
    private final Connection connection;

    /**
     * Constructs a new UserSecurityService and ensures required tables exist.
     */
    public UserSecurityService() {
        this.connection = DataSource.getInstance().getConnection();
    }


    // ======================== ACCOUNT LOCKOUT METHODS ========================

    /**
     * Records a failed login attempt for a user.
     * Locks account if max attempts exceeded.
     *
     * @param email the user's email
     */
    public void recordFailedLogin(String email) {
        try {
            String sql = "UPDATE users SET failed_login_attempts = failed_login_attempts + 1, " +
                "last_failed_login = ? WHERE email = ?";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                pst.setString(2, email);
                pst.executeUpdate();
            }

            // Check if we need to lock the account
            int attempts = getFailedLoginAttempts(email);
            if (attempts >= SecurityConfig.MAX_FAILED_ATTEMPTS) {
                lockAccount(email);
                LOGGER.warning(String.format("Account locked for %s after %d failed attempts",
                    email, attempts));
            } else {
                LOGGER.info(String.format("Failed login attempt %d/%d for %s",
                    attempts, SecurityConfig.MAX_FAILED_ATTEMPTS, email));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error recording failed login", e);
        }
    }

    /**
     * Records a successful login and resets failed attempt counter.
     *
     * @param email the user's email
     */
    public void recordSuccessfulLogin(String email) {
        try {
            String sql = "UPDATE users SET failed_login_attempts = 0, " +
                "last_failed_login = NULL WHERE email = ?";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setString(1, email);
                pst.executeUpdate();
            }

            LOGGER.info("Successful login for " + email + ", reset failed attempts");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error recording successful login", e);
        }
    }

    /**
     * Locks a user account.
     *
     * @param email the user's email
     */
    private void lockAccount(String email) {
        try {
            LocalDateTime lockUntil = SecurityConfig.LOCKOUT_DURATION_MINUTES > 0
                ? LocalDateTime.now().plusMinutes(SecurityConfig.LOCKOUT_DURATION_MINUTES)
                : null; // Permanent lock

            String sql = "UPDATE users SET is_locked = TRUE, locked_until = ? WHERE email = ?";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                if (lockUntil != null) {
                    pst.setTimestamp(1, Timestamp.valueOf(lockUntil));
                } else {
                    pst.setNull(1, java.sql.Types.TIMESTAMP);
                }
                pst.setString(2, email);
                pst.executeUpdate();
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error locking account", e);
        }
    }

    /**
     * Unlocks a user account (admin action).
     *
     * @param userId the user ID
     */
    public void unlockAccount(Long userId) {
        try {
            String sql = "UPDATE users SET is_locked = FALSE, locked_until = NULL, " +
                "failed_login_attempts = 0 WHERE id = ?";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setLong(1, userId);
                pst.executeUpdate();
            }

            LOGGER.info("Account unlocked for user ID: " + userId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error unlocking account", e);
        }
    }

    /**
     * Checks if an account is currently locked.
     *
     * @param email the user's email
     * @return true if account is locked
     */
    public boolean isAccountLocked(String email) {
        try {
            String sql = "SELECT is_locked, locked_until FROM users WHERE email = ?";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setString(1, email);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    boolean isLocked = rs.getBoolean("is_locked");
                    Timestamp lockedUntil = rs.getTimestamp("locked_until");

                    // Check if temporary lock has expired
                    if (isLocked && lockedUntil != null) {
                        if (LocalDateTime.now().isAfter(lockedUntil.toLocalDateTime())) {
                            // Auto-unlock
                            unlockAccountByEmail(email);
                            return false;
                        }
                    }

                    return isLocked;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking account lock status", e);
        }

        return false;
    }

    /**
     * Gets the number of failed login attempts.
     *
     * @param email the user's email
     * @return number of failed attempts
     */
    private int getFailedLoginAttempts(String email) {
        try {
            String sql = "SELECT failed_login_attempts FROM users WHERE email = ?";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setString(1, email);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    return rs.getInt("failed_login_attempts");
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting failed login attempts", e);
        }

        return 0;
    }

    /**
     * Unlocks account by email.
     */
    private void unlockAccountByEmail(String email) {
        try {
            String sql = "UPDATE users SET is_locked = FALSE, locked_until = NULL, " +
                "failed_login_attempts = 0 WHERE email = ?";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setString(1, email);
                pst.executeUpdate();
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error unlocking account by email", e);
        }
    }

    // ======================== PASSWORD HISTORY METHODS ========================

    /**
     * Checks if a password has been used recently.
     *
     * @param userId      the user ID
     * @param newPassword the plaintext password to check
     * @return true if password was recently used
     */
    public boolean isPasswordReused(Long userId, String newPassword) {
        List<PasswordHistory> history = getPasswordHistory(userId, SecurityConfig.PASSWORD_HISTORY_LIMIT);

        for (PasswordHistory ph : history) {
            if (BCrypt.checkpw(newPassword, ph.getPasswordHash())) {
                LOGGER.warning(String.format("Password reuse attempt detected for user ID %d", userId));
                return true;
            }
        }

        return false;
    }

    /**
     * Saves a password to history.
     *
     * @param userId       the user ID
     * @param passwordHash the BCrypt hashed password
     */
    public void savePasswordToHistory(Long userId, String passwordHash) {
        try {
            String sql = "INSERT INTO password_history (user_id, password_hash, created_at) VALUES (?, ?, ?)";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setLong(1, userId);
                pst.setString(2, passwordHash);
                pst.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                pst.executeUpdate();
            }

            // Clean up old history entries (keep only last N)
            cleanupPasswordHistory(userId);

            LOGGER.info("Password saved to history for user ID: " + userId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving password to history", e);
        }
    }

    /**
     * Gets password history for a user.
     *
     * @param userId the user ID
     * @param limit  maximum number of entries to retrieve
     * @return list of password history entries
     */
    public List<PasswordHistory> getPasswordHistory(Long userId, int limit) {
        List<PasswordHistory> history = new ArrayList<>();

        try {
            String sql = "SELECT * FROM password_history WHERE user_id = ? " +
                "ORDER BY created_at DESC LIMIT ?";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setLong(1, userId);
                pst.setInt(2, limit);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    PasswordHistory ph = PasswordHistory.builder()
                        .id(rs.getLong("id"))
                        .userId(rs.getLong("user_id"))
                        .passwordHash(rs.getString("password_hash"))
                        .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at") : null)
                        .build();
                    history.add(ph);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving password history", e);
        }

        return history;
    }

    /**
     * Cleans up old password history entries, keeping only the most recent N.
     *
     * @param userId the user ID
     */
    private void cleanupPasswordHistory(Long userId) {
        try {
            String sql = "DELETE FROM password_history WHERE user_id = ? AND id NOT IN " +
                "(SELECT id FROM (SELECT id FROM password_history WHERE user_id = ? " +
                "ORDER BY created_at DESC LIMIT ?) AS recent)";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setLong(1, userId);
                pst.setLong(2, userId);
                pst.setInt(3, SecurityConfig.PASSWORD_HISTORY_LIMIT);
                pst.executeUpdate();
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error cleaning up password history", e);
        }
    }

    /**
     * Updates a user's password with history tracking.
     *
     * @param userId      the user ID
     * @param newPassword the new plaintext password
     * @throws PasswordReusedException if password was recently used
     */
    public void updatePasswordWithHistory(Long userId, String newPassword) throws PasswordReusedException {
        // Check if password is reused
        if (isPasswordReused(userId, newPassword)) {
            throw new PasswordReusedException(SecurityConfig.PASSWORD_HISTORY_LIMIT);
        }

        // Hash the new password
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());


        // Save to history
        savePasswordToHistory(userId, hashedPassword);

        LOGGER.info("Password updated with history tracking for user ID: " + userId);
    }

    // ======================== ACCOUNT DEACTIVATION METHODS
    // ========================

    /**
     * Deactivates a user account.
     *
     * @param userId the user ID
     * @param reason the reason for deactivation
     */
    public void deactivateAccount(Long userId, String reason) {
        try {
            String sql = "UPDATE users SET is_active = FALSE, deactivated_at = ?, " +
                "deactivation_reason = ? WHERE id = ?";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                pst.setString(2, reason);
                pst.setLong(3, userId);
                pst.executeUpdate();
            }

            LOGGER.info(String.format("Account deactivated for user ID %d: %s", userId, reason));

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deactivating account", e);
        }
    }

    /**
     * Reactivates a deactivated user account.
     *
     * @param userId the user ID
     */
    public void reactivateAccount(Long userId) {
        try {
            String sql = "UPDATE users SET is_active = TRUE, deactivated_at = NULL, " +
                "deactivation_reason = NULL WHERE id = ?";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setLong(1, userId);
                pst.executeUpdate();
            }

            LOGGER.info("Account reactivated for user ID: " + userId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error reactivating account", e);
        }
    }

    /**
     * Checks if a user account is active.
     *
     * @param email the user's email
     * @return true if account is active
     */
    public boolean isAccountActive(String email) {
        try {
            String sql = "SELECT is_active FROM users WHERE email = ?";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setString(1, email);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    return rs.getBoolean("is_active");
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking account active status", e);
        }

        return true; // Default to active if error
    }

    /**
     * Gets all deactivated user accounts.
     *
     * @return list of deactivated user IDs
     */
    public List<Long> getDeactivatedAccounts() {
        List<Long> deactivatedIds = new ArrayList<>();

        try {
            String sql = "SELECT id FROM users WHERE is_active = FALSE";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    deactivatedIds.add(rs.getLong("id"));
                }
            }

            LOGGER.info(String.format("Found %d deactivated accounts", deactivatedIds.size()));

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting deactivated accounts", e);
        }

        return deactivatedIds;
    }
}
