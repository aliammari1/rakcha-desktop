package com.esprit.utils;

/**
 * Security configuration constants for user account management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class SecurityConfig {

    /**
     * Maximum number of failed login attempts before account lockout
     */
    public static final int MAX_FAILED_ATTEMPTS = 5;

    /**
     * Account lockout duration in minutes (-1 for permanent lockout requiring admin
     * unlock)
     */
    public static final int LOCKOUT_DURATION_MINUTES = 30;

    /**
     * Number of previous passwords to track and prevent reuse
     */
    public static final int PASSWORD_HISTORY_LIMIT = 5;

    /**
     * Private constructor to prevent instantiation
     */
    private SecurityConfig() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
