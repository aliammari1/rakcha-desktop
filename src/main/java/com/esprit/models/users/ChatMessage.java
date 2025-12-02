package com.esprit.models.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    @Builder.Default
    private boolean isRead = false;
    @Builder.Default
    private Timestamp sentAt = new Timestamp(System.currentTimeMillis());
}

