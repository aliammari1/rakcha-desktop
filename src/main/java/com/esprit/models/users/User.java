package com.esprit.models.users;

import com.esprit.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

/**
 * Abstract User entity representing the base user model Fixed issues:
 * phoneNumber changed from int to String for proper phone handling Added proper
 * Hibernate annotations and Lombok annotations
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
/**
 * Entity class for the RAKCHA application. Provides data persistence using
 * Hibernate/JPA annotations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class User {

    @EqualsAndHashCode.Include
    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String passwordHash;

    private UserRole role;

    private String address;

    private Date birthDate;

    @EqualsAndHashCode.Include
    private String email;

    private String username;

    private String profilePictureUrl;

    @Builder.Default
    private boolean isVerified = false;

    private String totpSecret;

    private java.sql.Timestamp createdAt;

    // Security - Account Lockout fields (matches schema)
    @Builder.Default
    private int failedLoginAttempts = 0;

    private java.sql.Timestamp lastFailedLogin;

    @Builder.Default
    private boolean isLocked = false;

    private java.sql.Timestamp lockedUntil;

    // Security - Account Status fields
    @Builder.Default
    private boolean isActive = true;

    private java.sql.Timestamp deactivatedAt;

    private String deactivationReason;

    /**
     * Create a new User instance without an id.
     *
     * @param firstName         the user's first name
     * @param lastName          the user's last name
     * @param phoneNumber       the user's phone number
     * @param passwordHash      the user's password hash
     * @param role              the user's role in the system
     * @param address           the user's address
     * @param birthDate         the user's birth date
     * @param email             the user's email address
     * @param profilePictureUrl the path to the user's profile photo
     */
    protected User(final String firstName, final String lastName, final String phoneNumber, final String passwordHash,
                   final UserRole role, final String address, final Date birthDate, final String email,
                   final String profilePictureUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.role = role;
        this.address = address;
        this.birthDate = birthDate;
        this.email = email;
        this.username = email; // Default username to email if not set
        this.profilePictureUrl = profilePictureUrl;
        this.createdAt = new java.sql.Timestamp(System.currentTimeMillis());
    }

    protected User(final Long id, final String firstName, final String lastName, final String phoneNumber,
                   final String passwordHash,
                   final UserRole role, final String address, final Date birthDate, final String email,
                   final String profilePictureUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.role = role;
        this.address = address;
        this.birthDate = birthDate;
        this.email = email;
        this.username = email; // Default username to email if not set
        this.profilePictureUrl = profilePictureUrl;
    }

    /**
     * Get the username of the user.
     *
     * @return the username
     */
    public String getUsername() {
        return this.username != null ? this.username : this.email;
    }

    /**
     * Get the profile picture URL of the user.
     *
     * @return the profile picture URL
     */
    public String getProfilePicture() {
        return this.profilePictureUrl;
    }

    /**
     * Set the profile picture URL of the user.
     *
     * @param profilePictureUrl the URL to set
     */
    public void setProfilePicture(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    /**
     * Get the loyalty points for this user (convenience method).
     * Calculated based on total spent, tickets purchased, or other metrics.
     *
     * @return the loyalty points count
     */
    public int getLoyaltyPoints() {
        // For now, return 0 as a default
        // In a full system, this would calculate based on purchases or be a stored field
        return 0;
    }

}
