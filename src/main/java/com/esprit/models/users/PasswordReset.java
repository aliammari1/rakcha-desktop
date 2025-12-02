package com.esprit.models.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordReset {

    private Long id;
    private Long userId;
    private String selector;
    private String hashedToken;
    @Builder.Default
    private java.sql.Timestamp requestedAt = new java.sql.Timestamp(System.currentTimeMillis());
    private java.sql.Timestamp expiresAt;
}

