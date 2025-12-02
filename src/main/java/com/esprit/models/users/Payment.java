package com.esprit.models.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private Long id;
    private Long userId;
    private Double amount;
    private String paymentMethod; // 'STRIPE', 'PAYPAL', 'CASH'
    private String status; // 'COMPLETED', 'PENDING', 'FAILED'
    private String transactionId;
    private Long orderId;
    private Long ticketId;
    @Builder.Default
    private java.sql.Timestamp createdAt = new java.sql.Timestamp(System.currentTimeMillis());
}

