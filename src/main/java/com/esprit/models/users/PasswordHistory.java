package com.esprit.models.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Password History model for tracking user password changes.
 * Prevents password reuse by storing hashed passwords.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordHistory {

    /**
     * Unique identifier for password history entry
     */
    private Long id;

    /**
     * User ID this password belongs to
     */
    private Long userId;

    /**
     * Hashed password (BCrypt or similar)
     */
    private String passwordHash;

    /**
     * When this password was set
     */
    @Builder.Default
    private java.sql.Timestamp createdAt = new java.sql.Timestamp(System.currentTimeMillis());
}

