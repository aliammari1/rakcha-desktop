package com.esprit.models.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {

    private Long id;
    private Long requesterId;
    private Long addresseeId;
    private String status; // PENDING, ACCEPTED, REJECTED, BLOCKED
    @Builder.Default
    private java.sql.Timestamp createdAt = new java.sql.Timestamp(System.currentTimeMillis());
}

