package com.esprit.models.products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Model for payment records.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    private int paymentId;
    private int orderId;
    private double amount;
    private String paymentMethod; // CREDIT_CARD, PAYPAL, CASH, etc.
    private String status; // PENDING, COMPLETED, FAILED, REFUNDED
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String refundReason;
    
    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", createdAt=" + createdAt +
                ", processedAt=" + processedAt +
                ", refundReason='" + refundReason + '\'' +
                '}';
    }
}
