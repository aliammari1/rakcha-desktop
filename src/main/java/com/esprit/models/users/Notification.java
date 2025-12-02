package com.esprit.models.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    private Long id;
    private Long userId;
    private String title;
    private String message;
    @Builder.Default
    private boolean isRead = false;
    private String type; // e.g., 'ORDER_UPDATE', 'FRIEND_REQ'
    @Builder.Default
    private java.sql.Timestamp createdAt = new java.sql.Timestamp(System.currentTimeMillis());
}

