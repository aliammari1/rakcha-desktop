package com.esprit.services.products;

import com.esprit.models.products.Payment;
import com.esprit.models.products.Order;
import com.esprit.utils.DataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing payments and payment processing.
 */
@Slf4j
public class PaymentService {

    private static final String TABLE_NAME = "payments";

    /**
     * Process a payment for an order.
     */
    public Payment processPayment(Order order, String paymentMethod, String cardToken) {
        if (order == null || paymentMethod == null || paymentMethod.isEmpty()) {
            log.warn("Invalid payment parameters");
            return null;
        }

        String sql = "INSERT INTO " + TABLE_NAME + " (order_id, amount, payment_method, status, transaction_id, created_at, processed_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, order.getId());
            pstmt.setDouble(2, order.getTotalAmount());
            pstmt.setString(3, paymentMethod);
            pstmt.setString(4, "PENDING");
            pstmt.setString(5, cardToken != null ? cardToken : "");
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(7, null);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return getPaymentById(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error processing payment for order {}: {}", order.getId(), e.getMessage(), e);
        }

        return null;
    }

    /**
     * Confirm a payment as completed.
     */
    public boolean confirmPayment(Payment payment) {
        if (payment == null || payment.getPaymentId() <= 0) {
            log.warn("Invalid payment");
            return false;
        }

        String sql = "UPDATE " + TABLE_NAME + " SET status = ?, processed_at = ? WHERE payment_id = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "COMPLETED");
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(3, payment.getPaymentId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            log.error("Error confirming payment {}: {}", payment.getPaymentId(), e.getMessage(), e);
        }

        return false;
    }

    /**
     * Refund a payment.
     */
    public boolean refundPayment(Payment payment, String reason) {
        if (payment == null || payment.getPaymentId() <= 0) {
            log.warn("Invalid payment");
            return false;
        }

        String sql = "UPDATE " + TABLE_NAME + " SET status = ?, processed_at = ?, refund_reason = ? WHERE payment_id = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "REFUNDED");
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(3, reason != null ? reason : "");
            pstmt.setInt(4, payment.getPaymentId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            log.error("Error refunding payment {}: {}", payment.getPaymentId(), e.getMessage(), e);
        }

        return false;
    }

    /**
     * Get payment by ID.
     */
    public Payment getPaymentById(int paymentId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE payment_id = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, paymentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving payment {}: {}", paymentId, e.getMessage(), e);
        }

        return null;
    }

    /**
     * Get all payments for an order.
     */
    public List<Payment> getPaymentsByOrderId(int orderId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE order_id = ? ORDER BY created_at DESC";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving payments for order {}: {}", orderId, e.getMessage(), e);
        }

        return payments;
    }

    /**
     * Get all payments for a user.
     */
    public List<Payment> getPaymentsByUserId(int userId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.* FROM " + TABLE_NAME + " p " +
            "JOIN orders o ON p.order_id = o.order_id " +
            "WHERE o.user_id = ? ORDER BY p.created_at DESC";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving payments for user {}: {}", userId, e.getMessage(), e);
        }

        return payments;
    }

    /**
     * Get all completed payments.
     */
    public List<Payment> getCompletedPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE status = 'COMPLETED' ORDER BY processed_at DESC";

        try (Connection conn = DataSource.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            log.error("Error retrieving completed payments: {}", e.getMessage(), e);
        }

        return payments;
    }

    /**
     * Get total payment amount for a period.
     */
    public double getTotalPaymentAmount(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM " + TABLE_NAME +
            " WHERE status = 'COMPLETED' AND processed_at >= ? AND processed_at <= ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            log.error("Error calculating total payment amount: {}", e.getMessage(), e);
        }

        return 0.0;
    }

    /**
     * Check if payment exists.
     */
    public boolean paymentExists(int paymentId) {
        String sql = "SELECT 1 FROM " + TABLE_NAME + " WHERE payment_id = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, paymentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.error("Error checking if payment exists: {}", e.getMessage(), e);
        }

        return false;
    }

    /**
     * Map ResultSet to Payment object.
     */
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setOrderId(rs.getInt("order_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setStatus(rs.getString("status"));
        payment.setTransactionId(rs.getString("transaction_id"));

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            payment.setCreatedAt(createdTs.toLocalDateTime());
        }

        Timestamp processedTs = rs.getTimestamp("processed_at");
        if (processedTs != null) {
            payment.setProcessedAt(processedTs.toLocalDateTime());
        }

        payment.setRefundReason(rs.getString("refund_reason"));

        return payment;
    }

    /**
     * Process a card payment.
     *
     * @param cardNumber the credit card number
     * @param cardHolder the cardholder name
     * @param expiryDate the card expiry date
     * @param cvv        the CVV code
     * @param amount     the payment amount
     * @return true if payment was successful
     */
    public boolean processCardPayment(String cardNumber, String cardHolder, String expiryDate, String cvv, double amount) {
        // Validate card details
        if (cardNumber == null || cardNumber.isEmpty() || amount <= 0) {
            return false;
        }

        // Simulate card processing (in real system, would call payment gateway)
        // For now, return true for valid-looking card numbers
        return cardNumber.length() >= 13 && cardNumber.length() <= 19;
    }

    /**
     * Process a PayPal payment.
     *
     * @param paypalEmail the PayPal email
     * @param amount      the payment amount
     * @return true if payment was successful
     */
    public boolean processPayPalPayment(String paypalEmail, double amount) {
        // Validate PayPal details
        if (paypalEmail == null || paypalEmail.isEmpty() || amount <= 0) {
            return false;
        }

        // Simulate PayPal processing (in real system, would call PayPal API)
        return paypalEmail.contains("@");
    }
}

