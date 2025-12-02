package com.esprit.models.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Model class representing a direct message between users.
 * Used for the in-app messaging/chat system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    private Long id;

    // Sender and recipient
    private Long senderId;
    private Long recipientId;

    // Message content
    private String content;

    // Optional attachment (image, file URL)
    private String attachmentUrl;
    private String attachmentType; // "image", "file", "gif", etc.

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    // Status flags
    @Builder.Default
    private boolean read = false;

    @Builder.Default
    private boolean deleted = false;

    // For reply threading
    private Long replyToId;

    // Message type
    @Builder.Default
    private MessageType type = MessageType.TEXT;

    /**
     * Factory method to create a text message.
     */
    public static Message createTextMessage(Long senderId, Long recipientId, String content) {
        return Message.builder()
            .senderId(senderId)
            .recipientId(recipientId)
            .content(content)
            .type(MessageType.TEXT)
            .createdAt(LocalDateTime.now())
            .build();
    }

    /**
     * Factory method to create an image message.
     */
    public static Message createImageMessage(Long senderId, Long recipientId, String imageUrl) {
        return Message.builder()
            .senderId(senderId)
            .recipientId(recipientId)
            .content("📷 Image")
            .attachmentUrl(imageUrl)
            .attachmentType("image")
            .type(MessageType.IMAGE)
            .createdAt(LocalDateTime.now())
            .build();
    }

    /**
     * Factory method to create a system message.
     */
    public static Message createSystemMessage(Long recipientId, String content) {
        return Message.builder()
            .recipientId(recipientId)
            .content(content)
            .type(MessageType.SYSTEM)
            .createdAt(LocalDateTime.now())
            .build();
    }

    /**
     * Marks the message as read.
     */
    public void markAsRead() {
        if (!this.read) {
            this.read = true;
            this.readAt = LocalDateTime.now();
        }
    }

    /**
     * Checks if the message was sent by the given user.
     */
    public boolean isSentBy(Long userId) {
        return senderId != null && senderId.equals(userId);
    }

    /**
     * Checks if the message is a reply to another message.
     */
    public boolean isReply() {
        return replyToId != null;
    }

    /**
     * Enum representing different message types.
     */
    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        SYSTEM,
        EMOJI
    }
}
