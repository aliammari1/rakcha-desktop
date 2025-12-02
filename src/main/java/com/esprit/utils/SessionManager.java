package com.esprit.utils;

import com.esprit.models.users.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Session manager for storing temporary authentication data during the reset
 * password flow and managing the current logged-in user session.
 * Enhanced with automatic session cleanup and thread-safe storage.
 *
 * <p>This class implements the Singleton pattern to ensure only one instance
 * exists throughout the application lifecycle.</p>
 *
 * @author RAKCHA Team
 * @version 3.0.0
 * @since 1.0.0
 */
@Slf4j
public class SessionManager {

    // Session timeout in minutes
    private static final long SESSION_TIMEOUT_MINUTES = 10;
    // Cleanup interval in minutes
    private static final long CLEANUP_INTERVAL_MINUTES = 5;
    // Singleton instance with double-checked locking
    private static volatile SessionManager instance;
    // Thread-safe storage for multiple sessions
    private final Map<String, SessionData> sessions = new ConcurrentHashMap<>();

    // Scheduled executor for automatic cleanup
    private final ScheduledExecutorService cleanupScheduler = Executors.newScheduledThreadPool(1);

    // ============= USER SESSION MANAGEMENT =============

    /**
     * The currently logged-in user. Thread-safe singleton storage.
     */
    private volatile User currentUser = null;

    /**
     * Private constructor to prevent direct instantiation.
     * Initializes the cleanup scheduler and shutdown hook.
     */
    private SessionManager() {
        startCleanupScheduler();

        // Add shutdown hook to gracefully stop the scheduler
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down SessionManager cleanup scheduler");
            shutdown();
        }));

        log.info("SessionManager singleton instance created");
    }

    /**
     * Gets the singleton instance of SessionManager using double-checked locking.
     * This method is thread-safe and ensures only one instance is created.
     *
     * @return the singleton SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    // ============= USER SESSION METHODS =============

    /**
     * Gets the currently logged-in user.
     *
     * @return the current user, or null if no user is logged in
     */
    public static User getCurrentUser() {
        return getInstance().currentUser;
    }

    /**
     * Sets the currently logged-in user for the application session.
     *
     * @param user the user to set as currently logged in
     */
    public static void setCurrentUser(final User user) {
        getInstance().currentUser = user;
        if (user != null) {
            log.info("User session started for: {} ({})", user.getEmail(), user.getRole());
        } else {
            log.info("User session cleared");
        }
    }

    /**
     * Clears the current user session (for logout).
     */
    public static void clearCurrentUser() {
        SessionManager manager = getInstance();
        if (manager.currentUser != null) {
            log.info("Clearing user session for: {}", manager.currentUser.getEmail());
            manager.currentUser = null;
        }
    }

    /**
     * Logs out the current user and clears all session data.
     * This is a complete logout operation that clears user session and related data.
     */
    public static void logout() {
        SessionManager manager = getInstance();
        if (manager.currentUser != null) {
            log.info("User logout: {}", manager.currentUser.getEmail());
            // Clear all temporary sessions
            manager.sessions.clear();
        }
        // Clear current user
        manager.currentUser = null;
        log.info("Session fully cleared - user logged out");
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public static boolean isUserLoggedIn() {
        return getInstance().currentUser != null;
    }

    // ============= VERIFICATION CODE METHODS =============

    /**
     * Sets the verification code and email for the current session.
     *
     * @param code  the verification code
     * @param email the user's email address
     */
    public static void setVerificationData(final String code, final String email) {
        if (email == null || email.trim().isEmpty()) {
            log.warn("Attempted to set verification data with null or empty email");
            return;
        }

        getInstance().sessions.put(email.toLowerCase(), getInstance().new SessionData(code, email));
        log.debug("Verification data set for email: {}", email);
    }

    /**
     * Gets the stored verification code for an email.
     *
     * @param email the user's email address
     * @return the verification code or null if not set or expired
     */
    public static String getVerificationCode(final String email) {
        if (email == null) {
            return null;
        }

        SessionManager manager = getInstance();
        SessionData sessionData = manager.sessions.get(email.toLowerCase());
        if (sessionData == null || sessionData.isExpired()) {
            if (sessionData != null) {
                manager.sessions.remove(email.toLowerCase());
                log.debug("Expired session removed for email: {}", email);
            }
            return null;
        }

        return sessionData.getVerificationCode();
    }

    /**
     * Gets the stored verification code (legacy method for backward compatibility).
     *
     * @return the verification code or null if not set
     * @deprecated Use {@link #getVerificationCode(String)} instead
     */
    @Deprecated
    public static String getVerificationCode() {
        // Return null for safety - this method should not be used
        log.warn("Deprecated getVerificationCode() called without email parameter");
        return null;
    }

    /**
     * Gets the stored user email for a given email (identity function for
     * compatibility).
     *
     * @param email the user's email address
     * @return the email or null if session not found
     */
    public static String getUserEmail(final String email) {
        if (email == null) {
            return null;
        }

        SessionData sessionData = getInstance().sessions.get(email.toLowerCase());
        if (sessionData == null || sessionData.isExpired()) {
            return null;
        }

        return sessionData.getEmail();
    }

    /**
     * Gets the stored user email (legacy method).
     *
     * @return null
     * @deprecated Use {@link #getUserEmail(String)} instead
     */
    @Deprecated
    public static String getUserEmail() {
        log.warn("Deprecated getUserEmail() called without email parameter");
        return null;
    }

    /**
     * Checks if the provided code matches the stored code for an email.
     *
     * @param email the user's email address
     * @param code  the code to verify
     * @return true if code matches and is not expired, false otherwise
     */
    public static boolean verifyCode(final String email, final String code) {
        if (email == null || code == null) {
            return false;
        }

        SessionManager manager = getInstance();
        SessionData sessionData = manager.sessions.get(email.toLowerCase());
        if (sessionData == null || sessionData.isExpired()) {
            if (sessionData != null) {
                manager.sessions.remove(email.toLowerCase());
            }
            return false;
        }

        return sessionData.getVerificationCode().equals(code);
    }

    /**
     * Legacy verify code method for backward compatibility.
     *
     * @param code the code to verify
     * @return false
     * @deprecated Use {@link #verifyCode(String, String)} instead
     */
    @Deprecated
    public static boolean verifyCode(final String code) {
        log.warn("Deprecated verifyCode() called without email parameter");
        return false;
    }

    /**
     * Clears the session data after successful verification for a specific email.
     *
     * @param email the user's email address
     */
    public static void clearVerificationData(final String email) {
        if (email != null) {
            getInstance().sessions.remove(email.toLowerCase());
            log.debug("Verification data cleared for email: {}", email);
        }
    }

    /**
     * Clears all verification data (use with caution).
     */
    public static void clearVerificationData() {
        getInstance().sessions.clear();
        log.info("All verification data cleared");
    }

    /**
     * Checks if the code has expired for a specific email.
     *
     * @param email the user's email address
     * @return true if code is expired or not found, false otherwise
     */
    public static boolean isCodeExpired(final String email) {
        if (email == null) {
            return true;
        }

        SessionData sessionData = getInstance().sessions.get(email.toLowerCase());
        if (sessionData == null) {
            return true;
        }

        return sessionData.isExpired();
    }

    /**
     * Legacy isCodeExpired method.
     *
     * @return true
     * @deprecated Use {@link #isCodeExpired(String)} instead
     */
    @Deprecated
    public static boolean isCodeExpired() {
        log.warn("Deprecated isCodeExpired() called without email parameter");
        return true;
    }

    /**
     * Cleans up expired sessions (called automatically by scheduler).
     * Public static method for manual cleanup if needed.
     */
    public static void cleanupExpiredSessions() {
        getInstance().cleanupExpiredSessionsInternal();
    }

    /**
     * Gets the number of active sessions.
     *
     * @return number of active sessions
     */
    public static int getActiveSessionCount() {
        return getInstance().sessions.size();
    }

    /**
     * Shuts down the cleanup scheduler gracefully.
     */
    public static void shutdown() {
        SessionManager manager = getInstance();
        try {
            manager.cleanupScheduler.shutdown();
            if (!manager.cleanupScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                manager.cleanupScheduler.shutdownNow();
            }
            log.info("SessionManager cleanup scheduler shut down successfully");
        } catch (InterruptedException e) {
            manager.cleanupScheduler.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("SessionManager shutdown interrupted", e);
        }
    }

    /**
     * Starts the automatic cleanup scheduler.
     */
    private void startCleanupScheduler() {
        cleanupScheduler.scheduleAtFixedRate(
            this::cleanupExpiredSessionsInternal,
            CLEANUP_INTERVAL_MINUTES,
            CLEANUP_INTERVAL_MINUTES,
            TimeUnit.MINUTES);
        log.info("SessionManager cleanup scheduler started (runs every {} minutes)", CLEANUP_INTERVAL_MINUTES);
    }

    /**
     * Internal method to clean up expired sessions (called by scheduler).
     */
    private void cleanupExpiredSessionsInternal() {
        int removedCount = 0;
        for (Map.Entry<String, SessionData> entry : sessions.entrySet()) {
            if (entry.getValue().isExpired()) {
                sessions.remove(entry.getKey());
                removedCount++;
            }
        }

        if (removedCount > 0) {
            log.info("Cleaned up {} expired session(s)", removedCount);
        }
    }

    /**
     * Session data holder class.
     * Non-static inner class as part of the Singleton instance.
     */
    private class SessionData {

        private final String verificationCode;
        private final String email;
        private final long creationTime;
        private long lastAccessTime;

        public SessionData(String code, String email) {
            this.verificationCode = code;
            this.email = email;
            this.creationTime = System.currentTimeMillis();
            this.lastAccessTime = System.currentTimeMillis();
        }

        public String getVerificationCode() {
            updateLastAccess();
            return verificationCode;
        }

        public String getEmail() {
            updateLastAccess();
            return email;
        }

        public long getCreationTime() {
            return creationTime;
        }

        public long getLastAccessTime() {
            return lastAccessTime;
        }

        private void updateLastAccess() {
            this.lastAccessTime = System.currentTimeMillis();
        }

        public boolean isExpired() {
            long currentTime = System.currentTimeMillis();
            long elapsedMinutes = (currentTime - creationTime) / (1000 * 60);
            return elapsedMinutes > SESSION_TIMEOUT_MINUTES;
        }
    }
}
